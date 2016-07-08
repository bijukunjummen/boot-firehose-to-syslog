package io.pivotal.cf.nozzle.service;

import io.pivotal.cf.nozzle.doppler.Envelope;
import io.pivotal.cf.nozzle.doppler.EventType;
import io.pivotal.cf.nozzle.doppler.WrappedEnvelope;
import io.pivotal.cf.nozzle.model.AppDetail;
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

	public WrappedEnvelope enhanceWithApplicationName(Envelope envelope) {
		EventType eventType = envelope.getEventType();
		switch (eventType) {
			case HttpStart:
				return enhanceWithApplicationNameUUID(envelope, envelope.getHttpStart().getApplicationId());
			case HttpStop:
				return enhanceWithApplicationNameUUID(envelope, envelope.getHttpStop().getApplicationId());
			case HttpStartStop:
				return enhanceWithApplicationNameUUID(envelope, envelope.getHttpStartStop().getApplicationId());
			case LogMessage:
				return enhanceWithApplicationName(envelope, envelope.getLogMessage().getApplicationId());
			case ContainerMetric:
				return enhanceWithApplicationName(envelope, envelope.getContainerMetric().getApplicationId());
			case ValueMetric:
			case CounterEvent:
			case Error:
			default:
				return enhanceWithApplicationName(envelope, null);
		}
	}

	public WrappedEnvelope enhanceWithApplicationName(Envelope envelope, String applicationId) {
		if (applicationId != null) {
			AppDetail appDetail = this.appDetailsCachingService.getApplicationDetail(Objects.toString(applicationId));
			if (appDetail != null) {
				Map<String, String> fields = new HashMap<>();
				fields.put("applicationName", appDetail.getApplicationName());
				fields.put("org", appDetail.getOrg());
				fields.put("space", appDetail.getSpace());
				return new WrappedEnvelope(envelope, fields);
			}
		}
		return new WrappedEnvelope(envelope);
	}

	public WrappedEnvelope enhanceWithApplicationNameUUID(Envelope envelope, UUID applicationId) {
		if (applicationId != null) {
			return enhanceWithApplicationName(envelope, applicationId.toString());
		}
		return new WrappedEnvelope(envelope);
	}
}
