package io.pivotal.cf.nozzle.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.pivotal.cf.nozzle.doppler.FirehoseClient;
import io.pivotal.cf.nozzle.doppler.NettyDopplerClient;
import io.pivotal.cf.nozzle.doppler.WrappedDopplerClient;
import io.pivotal.cf.nozzle.mapper.EnvelopeSerializationMapper;
import io.pivotal.cf.nozzle.mapper.JsonSerializer;
import io.pivotal.cf.nozzle.mapper.TextSerializationMapper;
import io.pivotal.cf.nozzle.props.CfProperties;
import io.pivotal.cf.nozzle.props.FirehoseProperties;
import io.pivotal.cf.nozzle.netty.FirehoseBuilder;
import io.pivotal.cf.nozzle.netty.NettyFirehose;
import io.pivotal.cf.nozzle.service.FirehoseObserver;
import org.cloudfoundry.doppler.DopplerClient;
import org.cloudfoundry.spring.client.SpringCloudFoundryClient;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.method.P;
import reactor.core.util.Exceptions;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Responsible for all bean definitions to set up a Firehose client
 */
@Configuration
@EnableConfigurationProperties({CfProperties.class, FirehoseProperties.class})
public class FirehoseClientConfiguration {

	@Bean
	public SpringCloudFoundryClient cloudFoundryClient(CfProperties cfProps) {
		return SpringCloudFoundryClient.builder()
				.host(cfProps.getHost())
				.username(cfProps.getUser())
				.password(cfProps.getPassword())
				.skipSslValidation(cfProps.isSkipSslValidation())
				.build();
	}

	@Bean
	public FirehoseObserver firehoseObserver(FirehoseClient nettyDopplerClient, FirehoseProperties firehoseProperties) {
		return new FirehoseObserver(nettyDopplerClient, firehoseProperties);
	}



	@Bean
	//Custom implementation - defaults to this.
	public NettyDopplerClient nettyDopplerClient(NettyFirehose nettyFirehose) {
		return new NettyDopplerClient(nettyFirehose);
	}

	@Bean
	//Native cf-Java-client implementation - breaks on large payloads
	public WrappedDopplerClient cfDopplerClient(SpringCloudFoundryClient springCloudFoundryClient,
													 FirehoseProperties firehoseProperties) {
		return new WrappedDopplerClient(springCloudFoundryClient, firehoseProperties);
	}

	@Bean
	public NettyFirehose nettyFirehose(SpringCloudFoundryClient cloudFoundryClient,
									   FirehoseProperties firehoseProperties, CfProperties cfProperties) {
		String token = cloudFoundryClient.getAccessToken().block();
		URI firehoseUrl = cloudFoundryClient.getConnectionContext()
				.getRoot("doppler_logging_endpoint")
				.map(str -> {
					try {
						return new URI(str);
					} catch (URISyntaxException e) {
						throw Exceptions.propagate(e);
					}
				}).map(uri -> {
					try {
						if (uri.getScheme().equalsIgnoreCase("http")) {
							return new URI("ws", uri.getSchemeSpecificPart(), uri.getFragment());
						} else if (uri.getScheme().equalsIgnoreCase("https")) {
							return new URI("wss", uri.getSchemeSpecificPart(), uri.getFragment());
						}
					} catch (URISyntaxException e) {
						throw Exceptions.propagate(e);
					}
					return uri;
				})
				.block();

		return FirehoseBuilder.create(firehoseUrl, token)
				.subscriptionId(firehoseProperties.getSubscriptionId())
				.skipTlsValidation(cfProperties.isSkipSslValidation()).build();
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

