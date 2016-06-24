package io.pivotal.cf.nozzle.service;

import io.pivotal.cf.nozzle.model.AppDetail;
import org.cloudfoundry.client.v3.applications.Application;
import reactor.core.publisher.Mono;

/**
 * Responsible for retrieving the application details given the application id
 *
 * @author Biju Kunjummen
 */
public interface AppDetailsService {
	Mono<AppDetail> getApplicationDetail(String applicationId);
}
