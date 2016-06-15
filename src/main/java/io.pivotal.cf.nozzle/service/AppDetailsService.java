package io.pivotal.cf.nozzle.service;

import org.cloudfoundry.client.v3.applications.Application;
import reactor.core.publisher.Mono;

/**
 * Responsible for retrieving the application details given the application id
 *
 * @author Biju Kunjummen
 */
public interface AppDetailsService {
	Mono<String> getApplicationName(String applicationId);
}
