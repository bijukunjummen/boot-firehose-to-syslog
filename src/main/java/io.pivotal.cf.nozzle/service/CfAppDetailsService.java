package io.pivotal.cf.nozzle.service;

import io.pivotal.cf.nozzle.model.AppDetail;
import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.client.v2.applications.GetApplicationRequest;
import org.cloudfoundry.client.v2.applications.GetApplicationResponse;
import org.cloudfoundry.client.v2.organizations.GetOrganizationRequest;
import org.cloudfoundry.client.v2.organizations.GetOrganizationResponse;
import org.cloudfoundry.client.v2.spaces.GetSpaceRequest;
import org.cloudfoundry.client.v2.spaces.GetSpaceResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.tuple.Tuple;
import reactor.core.tuple.Tuple3;

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

//		Mono<GetApplicationResponse> responseMono =
		Flux<Tuple3<GetApplicationResponse, GetSpaceResponse, GetOrganizationResponse>> tuple3Flux =
				this.cloudFoundryClient.applicationsV2().get(GetApplicationRequest.
						builder()
						.applicationId(applicationId).build())
						.flatMap(appResponse ->
								this.cloudFoundryClient.spaces()
										.get(GetSpaceRequest
												.builder()
												.spaceId(appResponse.getEntity().getSpaceId())
												.build()).map(spaceResp -> Tuple.of(appResponse, spaceResp)))
						.flatMap(tup2 ->
								this.cloudFoundryClient.organizations()
										.get(GetOrganizationRequest.builder()
												.organizationId(tup2.getT2().getEntity().getOrganizationId()).build())
										.map(orgResp -> Tuple.of(tup2.t1, tup2.t2, orgResp))
						);

		return tuple3Flux
				.single()
				.map(tup3 -> {
					String appName = tup3.getT1().getEntity().getName();
					String spaceName = tup3.getT2().getEntity().getName();
					String orgName = tup3.getT3().getEntity().getName();
					return new AppDetail(appName, orgName, spaceName);
				})
				.otherwiseReturn(new AppDetail("", "", ""));
	}
}
