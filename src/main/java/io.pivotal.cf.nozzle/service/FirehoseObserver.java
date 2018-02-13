package io.pivotal.cf.nozzle.service;

import io.pivotal.cf.nozzle.doppler.FirehoseClient;
import org.cloudfoundry.doppler.Envelope;
import reactor.core.publisher.Flux;

public class FirehoseObserver {

	private final FirehoseClient firehoseClient;

	public FirehoseObserver(FirehoseClient firehoseClient) {
		this.firehoseClient = firehoseClient;
	}

	public Flux<Envelope> observeFirehose(int retryCount) {

		//Allow 1000 retries..
		if (retryCount >= 1000) {
			throw new RuntimeException("Retry count exceeded, terminating!");
		}
		Flux<Envelope> cfEvents = this.firehoseClient.firehose();

		return cfEvents.onErrorResume(t -> this.observeFirehose(retryCount + 1));
	}
}
