package io.pivotal.cf.nozzle.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.pivotal.cf.nozzle.doppler.Envelope;
import org.cloudfoundry.doppler.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
	public String serialize(Envelope<? extends Event> envelope) {
		try {
			return this.objectMapper.writeValueAsString(envelope);
		} catch (JsonProcessingException e) {
			//Ignore for now.
		}
		return "";
	}
}
