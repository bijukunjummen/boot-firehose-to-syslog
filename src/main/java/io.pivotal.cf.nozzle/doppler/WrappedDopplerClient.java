package io.pivotal.cf.nozzle.doppler;

import io.pivotal.cf.nozzle.props.FirehoseProperties;
import org.cloudfoundry.reactor.util.AbstractReactorOperations;
import org.cloudfoundry.reactor.util.ConnectionContextSupplier;
import reactor.core.publisher.Flux;
import reactor.core.util.Exceptions;
import reactor.io.netty.http.HttpInbound;

import java.io.IOException;
import java.io.InputStream;

public class WrappedDopplerClient extends AbstractReactorOperations implements FirehoseClient {

	private final FirehoseProperties firehoseProperties;

	public WrappedDopplerClient(ConnectionContextSupplier cloudFoundryClient, FirehoseProperties firehoseProperties) {
		super(cloudFoundryClient.getConnectionContext().getAuthorizationProvider(), cloudFoundryClient.getConnectionContext().getHttpClient(),
				cloudFoundryClient.getConnectionContext().getObjectMapper(), cloudFoundryClient.getConnectionContext().getRoot("doppler_logging_endpoint"));

		this.firehoseProperties = firehoseProperties;
	}

	public Flux<Envelope> firehose() {
		return doWs(builder -> builder.pathSegment("firehose", this.firehoseProperties.getSubscriptionId()), outbound -> outbound)
				.flatMap(HttpInbound::receiveInputStream)
				.map(WrappedDopplerClient::toEnvelope)
				.filter(envelope -> envelope!=null)
				.map(Envelope::from);
	}

	private static org.cloudfoundry.dropsonde.events.Envelope toEnvelope(InputStream inputStream) {
		try (InputStream in = inputStream) {
			return org.cloudfoundry.dropsonde.events.Envelope.ADAPTER.decode(in);
		} catch (IOException e) {
			throw Exceptions.propagate(e);
		}
	}
}