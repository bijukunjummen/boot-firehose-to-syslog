package io.pivotal.cf.nozzle.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.pivotal.cf.nozzle.doppler.WrappedEnvelope;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Will convert Envelope to json
 */
public class JsonSerializer implements EnvelopeSerializationMapper {

	private final ObjectMapper objectMapper;

	@Autowired
	public JsonSerializer(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	@Override
	public String serialize(WrappedEnvelope envelope) {
		try {
			return this.objectMapper.writeValueAsString(envelope);
		} catch (JsonProcessingException e) {
			//Ignore for now.
		}
		return "";
	}
}
