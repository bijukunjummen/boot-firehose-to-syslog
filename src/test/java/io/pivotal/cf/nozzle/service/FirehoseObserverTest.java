package io.pivotal.cf.nozzle.service;

import io.pivotal.cf.nozzle.doppler.FirehoseClient;
import org.cloudfoundry.doppler.Envelope;
import org.cloudfoundry.util.test.TestSubscriber;
import org.junit.Test;
import reactor.core.publisher.Flux;

import java.util.concurrent.CountDownLatch;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class FirehoseObserverTest {

	@Test
	public void testFirehoseObserver() {
		FirehoseClient dopplerClient = mock(FirehoseClient.class);
		when(dopplerClient.firehose())
				.thenReturn(Flux.just(sampleEnvelope()));
		FirehoseObserver firehoseObserver = new FirehoseObserver(dopplerClient);
		Flux<Envelope> publisher = firehoseObserver.observeFirehose(0);

		publisher.subscribe(new TestSubscriber<Envelope>().expectEquals(sampleEnvelope()));
	}

	@Test(expected = RuntimeException.class)
	//retry count should exceed the limits..
	public void testFirehoseObserverWithAllErrors() throws Exception {
		FirehoseClient dopplerClient = mock(FirehoseClient.class);
		when(dopplerClient.firehose())
				.thenReturn(Flux.error(new RuntimeException("test")));
		FirehoseObserver firehoseObserver = new FirehoseObserver(dopplerClient);
		Flux<Envelope> publisher = firehoseObserver.observeFirehose(0);
		CountDownLatch cl = new CountDownLatch(1);
		publisher.subscribe(e -> {}, t -> {throw new RuntimeException(t);}, () -> cl.countDown() );
		cl.await();
	}

	@Test
	public void testFirehoseObserverWithFewErrors() throws Exception {
		FirehoseClient dopplerClient = mock(FirehoseClient.class);
		when(dopplerClient.firehose())
				.thenReturn(Flux.error(new RuntimeException("test")))
				.thenReturn(Flux.just(sampleEnvelope()));
		FirehoseObserver firehoseObserver = new FirehoseObserver(dopplerClient);
		Flux<Envelope> publisher = firehoseObserver.observeFirehose(0);
		CountDownLatch cl = new CountDownLatch(1);
		publisher.subscribe(e -> {}, t -> {throw new RuntimeException(t);}, () -> cl.countDown() );
		cl.await();
	}

	private Envelope sampleEnvelope() {
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
