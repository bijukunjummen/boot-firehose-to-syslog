package io.pivotal.cf.nozzle.service;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.concurrent.CountDownLatch;

import org.cloudfoundry.doppler.Envelope;
import org.junit.Test;

import io.pivotal.cf.nozzle.doppler.FirehoseClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

public class FirehoseObserverTest {

	@Test
	public void testFirehoseObserver() {
		FirehoseClient dopplerClient = mock(FirehoseClient.class);
		when(dopplerClient.firehose())
				.thenReturn(Flux.just(sampleEnvelope()));
		FirehoseObserver firehoseObserver = new FirehoseObserver(dopplerClient);
		Flux<Envelope> publisher = firehoseObserver.observeFirehose(0);


		StepVerifier.create(publisher).expectNext(sampleEnvelope()).expectComplete().verify();
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
