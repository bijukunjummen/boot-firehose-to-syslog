package io.pivotal.cf.nozzle.doppler;

import io.pivotal.cf.nozzle.props.FirehoseProperties;
import org.cloudfoundry.doppler.DopplerClient;
import org.cloudfoundry.doppler.Envelope;
import org.cloudfoundry.doppler.FirehoseRequest;
import reactor.core.publisher.Flux;

public class WrappedDopplerClient implements FirehoseClient {

	private final FirehoseProperties firehoseProperties;
	private final DopplerClient dopplerClient;

	public WrappedDopplerClient(DopplerClient dopplerClient, FirehoseProperties firehoseProperties) {
		this.firehoseProperties = firehoseProperties;
		this.dopplerClient = dopplerClient;
	}

	public Flux<Envelope> firehose() {
		return this.dopplerClient.firehose(FirehoseRequest.builder()
			.subscriptionId(this.firehoseProperties.getSubscriptionId()).build());
	}

}