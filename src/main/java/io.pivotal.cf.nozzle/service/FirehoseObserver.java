package io.pivotal.cf.nozzle.service;

import io.pivotal.cf.nozzle.doppler.Envelope;
import io.pivotal.cf.nozzle.doppler.WrappedDopplerClient;
import io.pivotal.cf.nozzle.props.FirehoseProperties;
import org.cloudfoundry.doppler.Event;
import org.cloudfoundry.doppler.FirehoseRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.UUID;

@Service
public class FirehoseObserver {

	private final WrappedDopplerClient dopplerClient;
	private final FirehoseProperties firehoseProperties;

	@Autowired
	public FirehoseObserver(WrappedDopplerClient wrappedDopplerClient, FirehoseProperties firehoseProperties) {
		this.dopplerClient = wrappedDopplerClient;
		this.firehoseProperties = firehoseProperties;
	}

	public Flux<Envelope<? extends Event>> observeFirehose(int retryCount) {

		if (retryCount >= 10) {
			throw new RuntimeException("Retry count exceeded, terminating!");
		}

		String subscriptionId = (firehoseProperties.getSubscriptionId() != null)
				? firehoseProperties.getSubscriptionId()
				: UUID.randomUUID().toString();

		Flux<Envelope<? extends Event>> cfEvents = this.dopplerClient.firehose(
				FirehoseRequest
						.builder()
						.subscriptionId(subscriptionId)
						.build()
		);

		return cfEvents.onErrorResumeWith(t -> this.observeFirehose(retryCount + 1));
	}
}
