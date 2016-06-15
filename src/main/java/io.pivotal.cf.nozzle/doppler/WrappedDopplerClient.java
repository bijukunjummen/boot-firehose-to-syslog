package io.pivotal.cf.nozzle.doppler;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.cloudfoundry.doppler.Event;
import org.cloudfoundry.doppler.FirehoseRequest;
import org.cloudfoundry.reactor.doppler.ReactorDopplerClient;
import org.cloudfoundry.reactor.util.AbstractReactorOperations;
import org.cloudfoundry.reactor.util.AuthorizationProvider;
import org.cloudfoundry.reactor.util.ConnectionContextSupplier;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.util.Exceptions;
import reactor.io.netty.http.HttpClient;
import reactor.io.netty.http.HttpInbound;

import java.io.IOException;
import java.io.InputStream;

/**
 * Doppler Client that returns events with Envelope around it..
 */
public class WrappedDopplerClient extends AbstractReactorOperations {

	public WrappedDopplerClient(ConnectionContextSupplier cloudFoundryClient) {
		super(cloudFoundryClient.getConnectionContext().getAuthorizationProvider(), cloudFoundryClient.getConnectionContext().getHttpClient(),
				cloudFoundryClient.getConnectionContext().getObjectMapper(), cloudFoundryClient.getConnectionContext().getRoot("doppler_logging_endpoint"));
	}

	public Flux<Envelope<? extends Event>> firehose(FirehoseRequest request) {
		return doWs(builder -> builder.pathSegment("firehose", request.getSubscriptionId()), outbound -> outbound)
				.flatMap(HttpInbound::receiveInputStream)
				.map(WrappedDopplerClient::toEnvelope)
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
