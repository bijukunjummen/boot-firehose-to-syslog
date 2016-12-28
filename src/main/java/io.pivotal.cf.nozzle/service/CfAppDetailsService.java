package io.pivotal.cf.nozzle.service;

import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.client.v2.applications.GetApplicationRequest;
import org.cloudfoundry.client.v2.applications.GetApplicationResponse;
import org.cloudfoundry.client.v2.organizations.GetOrganizationRequest;
import org.cloudfoundry.client.v2.organizations.GetOrganizationResponse;
import org.cloudfoundry.client.v2.spaces.GetSpaceRequest;
import org.cloudfoundry.client.v2.spaces.GetSpaceResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.pivotal.cf.nozzle.model.AppDetail;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuple3;
import reactor.util.function.Tuples;

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

	public Mono<AppDetail> getApplicationDetail(String applicationId) {
		Mono<GetApplicationResponse> applicationResponseMono = this.cloudFoundryClient
				.applicationsV2().get(GetApplicationRequest.builder().applicationId(applicationId).build());



		Mono<Tuple2<GetApplicationResponse, GetSpaceResponse>> appAndSpaceMono = applicationResponseMono
				.and(appResponse -> this.cloudFoundryClient.spaces()
						.get(GetSpaceRequest.builder()
								.spaceId(appResponse.getEntity().getSpaceId()).build()));

		Mono<Tuple3<GetApplicationResponse, GetSpaceResponse, GetOrganizationResponse>> t3 =
				appAndSpaceMono.then(tup2 -> this.cloudFoundryClient.organizations()
								.get(GetOrganizationRequest.builder()
										.organizationId(tup2.getT2().getEntity()
												.getOrganizationId())
										.build())
								.map(orgResp -> Tuples.of(tup2.getT1(), tup2.getT2(),
										orgResp)));

		return t3
				.map(tup3 -> {
					String appName = tup3.getT1().getEntity().getName();
					String spaceName = tup3.getT2().getEntity().getName();
					String orgName = tup3.getT3().getEntity().getName();
					return new AppDetail(appName, orgName, spaceName);
				}).otherwiseReturn(new AppDetail("", "", ""));

	}
}
