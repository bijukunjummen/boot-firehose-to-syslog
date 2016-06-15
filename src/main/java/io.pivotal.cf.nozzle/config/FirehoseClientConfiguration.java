package io.pivotal.cf.nozzle.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.pivotal.cf.nozzle.doppler.WrappedDopplerClient;
import io.pivotal.cf.nozzle.mapper.EnvelopeSerializationMapper;
import io.pivotal.cf.nozzle.mapper.JsonSerializer;
import io.pivotal.cf.nozzle.mapper.TextSerializationMapper;
import io.pivotal.cf.nozzle.props.CfProperties;
import io.pivotal.cf.nozzle.props.FirehoseProperties;
import org.cloudfoundry.spring.client.SpringCloudFoundryClient;
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
	public WrappedDopplerClient dopplerClient(SpringCloudFoundryClient cloudFoundryClient) {
		return new WrappedDopplerClient(cloudFoundryClient);
	}

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

