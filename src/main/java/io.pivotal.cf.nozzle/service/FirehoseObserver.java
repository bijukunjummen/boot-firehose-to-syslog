package io.pivotal.cf.nozzle.service;

import io.pivotal.cf.nozzle.doppler.Envelope;
import io.pivotal.cf.nozzle.doppler.FirehoseClient;
import reactor.core.publisher.Flux;

public class FirehoseObserver {

	private final FirehoseClient firehoseClient;

	public FirehoseObserver(FirehoseClient firehoseClient) {
		this.firehoseClient = firehoseClient;
	}

	public Flux<Envelope> observeFirehose(int retryCount) {

		if (retryCount >= 10) {
			throw new RuntimeException("Retry count exceeded, terminating!");
		}
		Flux<Envelope> cfEvents = this.firehoseClient.firehose();

		return cfEvents.onErrorResumeWith(t -> this.observeFirehose(retryCount + 1));
	}
}
