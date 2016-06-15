package io.pivotal.cf.nozzle.service;

import io.pivotal.cf.nozzle.doppler.Envelope;
import io.pivotal.cf.nozzle.doppler.EventType;
import io.pivotal.cf.nozzle.doppler.WrappedEnvelope;
import org.cloudfoundry.doppler.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Responsible for enhancing event details with the name of the application if feasible
 */

@Service
public class ApplicationNameEnhancer {

	private final AppDetailsCachingService appDetailsCachingService;

	@Autowired
	public ApplicationNameEnhancer(AppDetailsCachingService appDetailsCachingService) {
		this.appDetailsCachingService = appDetailsCachingService;
	}

	public WrappedEnvelope<? extends Event> enhanceWithApplicationName(Envelope<? extends Event> envelope) {
		EventType eventType = envelope.getEventType();
		switch (eventType) {
			case HttpStart:
				return enhanceWithApplicationNameUUID(envelope, ((HttpStart) envelope.getEvent()).getApplicationId());
			case HttpStop:
				return enhanceWithApplicationNameUUID(envelope, ((HttpStop) envelope.getEvent()).getApplicationId());
			case HttpStartStop:
				return enhanceWithApplicationNameUUID(envelope, ((HttpStartStop) envelope.getEvent()).getApplicationId());
			case LogMessage:
				return enhanceWithApplicationName(envelope, ((LogMessage) envelope.getEvent()).getApplicationId());
			case ContainerMetric:
				return enhanceWithApplicationName(envelope, ((ContainerMetric) envelope.getEvent()).getApplicationId());
			case ValueMetric:
			case CounterEvent:
			case Error:
			default:
				return enhanceWithApplicationName(envelope, null);
		}
	}

	public WrappedEnvelope<? extends Event> enhanceWithApplicationName(Envelope<? extends Event> envelope, String applicationId) {
		if (applicationId!=null) {
			String applicationName = this.appDetailsCachingService.getApplicationName(Objects.toString(applicationId));
			if (applicationName != null) {
				Map<String, String> fields = new HashMap<>();
				fields.put("applicationName", applicationName);
				return new WrappedEnvelope<>(envelope, fields);
			}
		}
		return new WrappedEnvelope<>(envelope);
	}

	public WrappedEnvelope<? extends Event> enhanceWithApplicationNameUUID(Envelope<? extends Event> envelope, UUID applicationId) {
		if (applicationId!=null) {
			return enhanceWithApplicationName(envelope, applicationId.toString());
		}
		return new WrappedEnvelope<>(envelope);
	}
}
