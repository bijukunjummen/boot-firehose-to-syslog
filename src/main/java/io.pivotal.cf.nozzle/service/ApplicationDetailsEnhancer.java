package io.pivotal.cf.nozzle.service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.cloudfoundry.doppler.Envelope;
import org.cloudfoundry.doppler.EventType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.pivotal.cf.nozzle.doppler.WrappedEnvelope;
import io.pivotal.cf.nozzle.model.AppDetail;
import reactor.core.publisher.Mono;

/**
 * Responsible for enhancing event details with the name of the application if feasible
 */

@Service
public class ApplicationDetailsEnhancer {

	private final AppDetailsCachingService appDetailsCachingService;
//
//	@Autowired
//	private AppDetailsService appDetailsService;


	@Autowired
	public ApplicationDetailsEnhancer(AppDetailsCachingService appDetailsCachingService) {
		this.appDetailsCachingService = appDetailsCachingService;
	}

	public Mono<WrappedEnvelope> enhanceWithApplicationName(Envelope envelope) {
		EventType eventType = envelope.getEventType();
		switch (eventType) {
			case HTTP_START_STOP:
				return enhanceWithApplicationNameUUID(envelope, envelope.getHttpStartStop().getApplicationId());
			case LOG_MESSAGE:
				return enhanceWithApplicationName(envelope, envelope.getLogMessage().getApplicationId());
			case CONTAINER_METRIC:
				return enhanceWithApplicationName(envelope, envelope.getContainerMetric().getApplicationId());
			case VALUE_METRIC:
			case COUNTER_EVENT:
			case ERROR:
			default:
				return enhanceWithApplicationName(envelope, null);
		}
	}

	public Mono<WrappedEnvelope> enhanceWithApplicationName(Envelope envelope, String applicationId) {
		if (applicationId != null) {
			Mono<AppDetail> appDetailMono = this.appDetailsCachingService.getApplicationDetail(applicationId);
			return appDetailMono.map(appDetail -> {
				Map<String, String> fields = new HashMap<>();
				fields.put("applicationName", appDetail.getApplicationName());
				fields.put("org", appDetail.getOrg());
				fields.put("space", appDetail.getSpace());
				return new WrappedEnvelope(envelope, fields);
			});
		}
		return Mono.just(new WrappedEnvelope(envelope));
	}

	public Mono<WrappedEnvelope> enhanceWithApplicationNameUUID(Envelope envelope, UUID applicationId) {
		if (applicationId != null) {
			return enhanceWithApplicationName(envelope, applicationId.toString());
		}
		return Mono.just(new WrappedEnvelope(envelope));
	}
}
