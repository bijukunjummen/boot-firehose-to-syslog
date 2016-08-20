package io.pivotal.cf.nozzle.doppler;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import org.cloudfoundry.doppler.Envelope;

import java.util.Map;

public class WrappedEnvelope {

	@JsonUnwrapped
	private final Envelope envelope;
	private final Map<String, String> additionalFields;

	public WrappedEnvelope(Envelope envelope, Map<String, String> additionalFields) {
		this.envelope = envelope;
		this.additionalFields = additionalFields;
	}

	public WrappedEnvelope(Envelope envelope) {
		this(envelope, null);
	}


	public Envelope getEnvelope() {
		return envelope;
	}

	public Map<String, String> getAdditionalFields() {
		return additionalFields;
	}

}
