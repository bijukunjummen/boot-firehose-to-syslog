package io.pivotal.cf.nozzle.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.pivotal.cf.nozzle.doppler.FirehoseClient;
import io.pivotal.cf.nozzle.doppler.WrappedDopplerClient;
import io.pivotal.cf.nozzle.mapper.EnvelopeSerializationMapper;
import io.pivotal.cf.nozzle.mapper.JsonSerializer;
import io.pivotal.cf.nozzle.mapper.TextSerializationMapper;
import io.pivotal.cf.nozzle.props.CfProperties;
import io.pivotal.cf.nozzle.props.FirehoseProperties;
import io.pivotal.cf.nozzle.service.FirehoseObserver;
import org.cloudfoundry.doppler.DopplerClient;
import org.cloudfoundry.reactor.ConnectionContext;
import org.cloudfoundry.reactor.DefaultConnectionContext;
import org.cloudfoundry.reactor.TokenProvider;
import org.cloudfoundry.reactor.client.ReactorCloudFoundryClient;
import org.cloudfoundry.reactor.doppler.ReactorDopplerClient;
import org.cloudfoundry.reactor.tokenprovider.PasswordGrantTokenProvider;
import org.cloudfoundry.reactor.uaa.ReactorUaaClient;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Responsible for all bean definitions to set up a Firehose client
 */
@Configuration
@EnableConfigurationProperties({CfProperties.class, FirehoseProperties.class})
public class FirehoseClientConfiguration {

	@Bean
	public DefaultConnectionContext connectionContext(CfProperties cfProps) {
		return DefaultConnectionContext.builder()
				.apiHost(cfProps.getHost())
				.skipSslValidation(true)
				.build();
	}

	@Bean
	public PasswordGrantTokenProvider tokenProvider(CfProperties cfProps) {
		return PasswordGrantTokenProvider.builder()
				.username(cfProps.getUser())
				.password(cfProps.getPassword())
				.build();
	}

	@Bean
	public ReactorCloudFoundryClient cloudFoundryClient(ConnectionContext connectionContext, TokenProvider tokenProvider) {
		return ReactorCloudFoundryClient.builder()
				.connectionContext(connectionContext)
				.tokenProvider(tokenProvider)
				.build();
	}

	@Bean
	public ReactorDopplerClient dopplerClient(ConnectionContext connectionContext, TokenProvider tokenProvider) {
		return ReactorDopplerClient.builder()
				.connectionContext(connectionContext)
				.tokenProvider(tokenProvider)
				.build();
	}

	@Bean
	public ReactorUaaClient uaaClient(ConnectionContext connectionContext, TokenProvider tokenProvider) {
		return ReactorUaaClient.builder()
				.connectionContext(connectionContext)
				.tokenProvider(tokenProvider)
				.build();
	}

	@Bean
	public WrappedDopplerClient wrappedDopplerClient(DopplerClient dopplerClient, FirehoseProperties firehoseProperties) {
		return new WrappedDopplerClient(dopplerClient, firehoseProperties);
	}

	@Bean
	public FirehoseObserver firehoseObserver(FirehoseClient wrappedDopplerClient) {
		return new FirehoseObserver(wrappedDopplerClient);
	}


	@Bean
	public EnvelopeSerializationMapper serializationMapper(
			FirehoseProperties firehoseProperties, ObjectMapper objectMapper) {
		switch (firehoseProperties.getTextFormat()) {
			case JSON:
				return jsonSerializer(objectMapper);
			case TEXT:
				return textSerializationMapper();
		}

		return textSerializationMapper();
	}


	private TextSerializationMapper textSerializationMapper() {
		return new TextSerializationMapper();
	}

	private JsonSerializer jsonSerializer(ObjectMapper objectMapper) {
		return new JsonSerializer(objectMapper);
	}

}

