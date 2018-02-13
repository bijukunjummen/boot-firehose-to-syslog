package io.pivotal.cf.nozzle.service;

import io.pivotal.cf.nozzle.model.AppDetail;
import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.client.v2.applications.ApplicationEntity;
import org.cloudfoundry.client.v2.applications.GetApplicationRequest;
import org.cloudfoundry.client.v2.organizations.GetOrganizationRequest;
import org.cloudfoundry.client.v2.organizations.OrganizationEntity;
import org.cloudfoundry.client.v2.spaces.GetSpaceRequest;
import org.cloudfoundry.client.v2.spaces.SpaceEntity;
import org.cloudfoundry.util.ResourceUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuples;

import static org.cloudfoundry.util.tuple.TupleUtils.function;

/**
 * Responsible for retrieving the application details given the application id
 *
 * @author Biju Kunjummen
 */

@Service
public class CfAppDetailsService implements AppDetailsService {

	private final CloudFoundryClient cloudFoundryClient;
	private static final Logger LOGGER = LoggerFactory
			.getLogger(CfAppDetailsService.class);

	@Autowired
	public CfAppDetailsService(CloudFoundryClient cloudFoundryClient) {
		this.cloudFoundryClient = cloudFoundryClient;
	}

	public Mono<AppDetail> getApplicationDetail(String applicationId) {
		Mono<AppDetail> appDetailMono = getApplication(applicationId)
				.flatMap(app -> getSpace(app.getSpaceId())
						.map(space -> Tuples.of(space, app)))
				.flatMap(function(
						(space, app) -> getOrganization(space.getOrganizationId())
								.map(org -> Tuples.of(org, space, app))))
				.map(function((org, space, app) -> new AppDetail(app.getName(),
						org.getName(), space.getName())))
				.onErrorResume(t -> {
					LOGGER.info(t.getMessage(), t);
					return Mono.just(new AppDetail("", "", ""));
				});

		return appDetailMono;

	}

	Mono<ApplicationEntity> getApplication(String applicationId) {
		return this.cloudFoundryClient.applicationsV2()
				.get(GetApplicationRequest.builder().applicationId(applicationId).build())
				.map(ResourceUtils::getEntity);
	}

	Mono<SpaceEntity> getSpace(String spaceid) {
		return this.cloudFoundryClient.spaces()
				.get(GetSpaceRequest.builder().spaceId(spaceid).build())
				.map(ResourceUtils::getEntity);
	}

	Mono<OrganizationEntity> getOrganization(String orgId) {
		return this.cloudFoundryClient.organizations()
				.get(GetOrganizationRequest.builder().organizationId(orgId).build())
				.map(ResourceUtils::getEntity);
	}

}
