package io.pivotal.cf.nozzle.service;

import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.client.v2.applications.GetApplicationRequest;
import org.cloudfoundry.client.v2.applications.GetApplicationResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * Responsible for retrieving the application details given the application id
 *
 * @author Biju Kunjummen
 */

@Service
public class CfAppDetailsService implements AppDetailsService {

	private final CloudFoundryClient cloudFoundryClient;

	@Autowired
	public CfAppDetailsService(CloudFoundryClient cloudFoundryClient) {
		this.cloudFoundryClient = cloudFoundryClient;
	}

	public Mono<String> getApplicationName(String applicationId) {
		Mono<GetApplicationResponse> responseMono = this.cloudFoundryClient.applicationsV2().get(GetApplicationRequest.
				builder()
				.applicationId(applicationId).build());

		return responseMono
				.map(appResponse -> appResponse.getEntity().getName())
				.otherwiseReturn("").defaultIfEmpty("");
	}
}
