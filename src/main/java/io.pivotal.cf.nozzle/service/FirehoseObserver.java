package io.pivotal.cf.nozzle.service;

import io.pivotal.cf.nozzle.doppler.Envelope;
import io.pivotal.cf.nozzle.doppler.WrappedDopplerClient;
import org.cloudfoundry.doppler.Event;
import org.cloudfoundry.doppler.FirehoseRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Flux;

import java.util.UUID;

public class FirehoseObserver {

	private final WrappedDopplerClient dopplerClient;

	@Autowired
	public FirehoseObserver(WrappedDopplerClient wrappedDopplerClient) {
		this.dopplerClient = wrappedDopplerClient;
	}

	public Flux<Envelope<? extends Event>> observeFirehose(int retryCount) {

		if (retryCount >= 10) {
			throw new RuntimeException("Retry count exceeded, terminating!");
		}
		Flux<Envelope<? extends Event>> cfEvents = this.dopplerClient.firehose(
				FirehoseRequest
						.builder()
						.subscriptionId(UUID.randomUUID().toString())
						.build()
		);
		//Not sure if recursively calling itself is a good idea..
		return cfEvents.onErrorResumeWith(t -> this.observeFirehose(retryCount + 1));
	}
}
