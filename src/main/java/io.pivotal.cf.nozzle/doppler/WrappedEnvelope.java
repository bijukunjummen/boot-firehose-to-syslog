package io.pivotal.cf.nozzle.doppler;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import org.cloudfoundry.doppler.Event;

import java.util.Map;

public class WrappedEnvelope<T extends Event> {

	@JsonUnwrapped
	private final Envelope<T> envelope;
	private final Map<String, String> additionalFields;

	public WrappedEnvelope(Envelope<T> envelope, Map<String, String> additionalFields) {
		this.envelope = envelope;
		this.additionalFields = additionalFields;
	}

	public WrappedEnvelope(Envelope<T> envelope) {
		this(envelope, null);
	}


	public Envelope<T> getEnvelope() {
		return envelope;
	}

	public Map<String, String> getAdditionalFields() {
		return additionalFields;
	}

}
