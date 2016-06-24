package io.pivotal.cf.nozzle.service;

import io.pivotal.cf.nozzle.doppler.Envelope;
import io.pivotal.cf.nozzle.doppler.NettyDopplerClient;
import io.pivotal.cf.nozzle.props.FirehoseProperties;
import org.cloudfoundry.doppler.Event;
import org.cloudfoundry.doppler.FirehoseRequest;
import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.core.test.TestSubscriber;

import java.util.concurrent.CountDownLatch;

import static org.mockito.Mockito.*;

public class FirehoseObserverTest {

	@Test
	public void testFirehoseObserver() {
		NettyDopplerClient nettyDopplerClient = mock(NettyDopplerClient.class);
		when(nettyDopplerClient.firehose(any(FirehoseRequest.class)))
				.thenReturn(Flux.just(sampleEnvelope()));
		FirehoseObserver firehoseObserver = new FirehoseObserver(nettyDopplerClient, new FirehoseProperties());
		Flux<Envelope<? extends Event>> publisher = firehoseObserver.observeFirehose(0);
		TestSubscriber.subscribe(publisher).await().assertValues(sampleEnvelope());
	}

	@Test(expected = RuntimeException.class)
	//retry count should exceed the limits..
	public void testFirehoseObserverWithAllErrors() throws Exception {
		NettyDopplerClient nettyDopplerClient = mock(NettyDopplerClient.class);
		when(nettyDopplerClient.firehose(any(FirehoseRequest.class)))
				.thenReturn(Flux.error(new RuntimeException("test")));
		FirehoseObserver firehoseObserver = new FirehoseObserver(nettyDopplerClient, new FirehoseProperties());
		Flux<Envelope<? extends Event>> publisher = firehoseObserver.observeFirehose(0);
		CountDownLatch cl = new CountDownLatch(1);
		publisher.subscribe(e -> e.getEvent(), t -> {throw new RuntimeException(t);}, () -> cl.countDown() );
		cl.await();
	}

	@Test
	public void testFirehoseObserverWithFewErrors() throws Exception {
		NettyDopplerClient nettyDopplerClient = mock(NettyDopplerClient.class);
		when(nettyDopplerClient.firehose(any(FirehoseRequest.class)))
				.thenReturn(Flux.error(new RuntimeException("test")))
				.thenReturn(Flux.just(sampleEnvelope()));
		FirehoseObserver firehoseObserver = new FirehoseObserver(nettyDopplerClient, new FirehoseProperties());
		Flux<Envelope<? extends Event>> publisher = firehoseObserver.observeFirehose(0);
		CountDownLatch cl = new CountDownLatch(1);
		publisher.subscribe(e -> e.getEvent(), t -> {throw new RuntimeException(t);}, () -> cl.countDown() );
		cl.await();
	}

	private Envelope<? extends Event> sampleEnvelope() {
		org.cloudfoundry.dropsonde.events.CounterEvent cfCounterEvent =
				new org.cloudfoundry.dropsonde.events.CounterEvent.Builder()
						.name("counter")
						.delta(1L)
						.build();

		org.cloudfoundry.dropsonde.events.Envelope cfEnvelope = new org.cloudfoundry.dropsonde.events.Envelope.Builder()
				.index("index")
				.eventType(org.cloudfoundry.dropsonde.events.Envelope.EventType.CounterEvent)
				.deployment("deployment")
				.origin("origin")
				.job("job")
				.ip("ip")
				.timestamp(123L)
				.counterEvent(cfCounterEvent)
				.build();

		return Envelope.from(cfEnvelope);
	}
}
