package io.pivotal.cf.nozzle.service;

import io.pivotal.cf.nozzle.doppler.Envelope;
import io.pivotal.cf.nozzle.doppler.FirehoseClient;
import io.pivotal.cf.nozzle.props.FirehoseProperties;
import org.cloudfoundry.doppler.Event;
import reactor.core.publisher.Flux;

import java.util.UUID;

public class FirehoseObserver {

	private final FirehoseClient firehoseClient;
	private final FirehoseProperties firehoseProperties;

	public FirehoseObserver(FirehoseClient firehoseClient, FirehoseProperties firehoseProperties) {
		this.firehoseClient = firehoseClient;
		this.firehoseProperties = firehoseProperties;
	}

	public Flux<Envelope<? extends Event>> observeFirehose(int retryCount) {

		if (retryCount >= 10) {
			throw new RuntimeException("Retry count exceeded, terminating!");
		}

		String subscriptionId = (firehoseProperties.getSubscriptionId() != null)
				? firehoseProperties.getSubscriptionId()
				: UUID.randomUUID().toString();

		Flux<Envelope<? extends Event>> cfEvents = this.firehoseClient.firehose();

		return cfEvents.onErrorResumeWith(t -> this.observeFirehose(retryCount + 1));
	}
}
