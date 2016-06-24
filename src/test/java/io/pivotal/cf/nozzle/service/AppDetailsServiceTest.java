package io.pivotal.cf.nozzle.service;

import io.pivotal.cf.nozzle.model.AppDetail;
import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.client.v2.applications.ApplicationEntity;
import org.cloudfoundry.client.v2.applications.ApplicationsV2;
import org.cloudfoundry.client.v2.applications.GetApplicationRequest;
import org.cloudfoundry.client.v2.applications.GetApplicationResponse;
import org.cloudfoundry.client.v2.organizations.GetOrganizationRequest;
import org.cloudfoundry.client.v2.organizations.GetOrganizationResponse;
import org.cloudfoundry.client.v2.organizations.OrganizationEntity;
import org.cloudfoundry.client.v2.organizations.Organizations;
import org.cloudfoundry.client.v2.spaces.GetSpaceRequest;
import org.cloudfoundry.client.v2.spaces.GetSpaceResponse;
import org.cloudfoundry.client.v2.spaces.SpaceEntity;
import org.cloudfoundry.client.v2.spaces.Spaces;
import org.junit.Test;
import reactor.core.publisher.Mono;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class AppDetailsServiceTest {

	@Test
	public void retrieveAppDetails() {

		CloudFoundryClient cfClient = mock(CloudFoundryClient.class);
		ApplicationsV2 mockApplicationsV2 = mock(ApplicationsV2.class);
		when(cfClient.applicationsV2()).thenReturn(mockApplicationsV2);
		GetApplicationResponse getAppResponse = GetApplicationResponse.builder()
				.entity(ApplicationEntity.builder()
						.name("testApp")
						.spaceId("spaceId")
						.build())
				.build();

		when(mockApplicationsV2.get(any(GetApplicationRequest.class))).thenReturn(Mono.just(getAppResponse));

		Spaces mockSpaces = mock(Spaces.class);
		GetSpaceResponse getSpaceResponse = GetSpaceResponse.builder().entity(SpaceEntity.builder().name("space1").organizationId("org1").build()).build();
		when(mockSpaces.get(any(GetSpaceRequest.class))).thenReturn(Mono.just(getSpaceResponse));
		when(cfClient.spaces()).thenReturn(mockSpaces);

		Organizations mockOrgs = mock(Organizations.class);

		GetOrganizationResponse getOrganizationResponse = GetOrganizationResponse.builder()
				.entity(OrganizationEntity.builder().name("org1").build())
				.build();

		when(mockOrgs.get(any(GetOrganizationRequest.class))).thenReturn(Mono.just(getOrganizationResponse));
		when(cfClient.organizations()).thenReturn(mockOrgs);


		AppDetailsService appDetailsService = new CfAppDetailsService(cfClient);
		assertThat(appDetailsService.getApplicationDetail("testid").block()).isEqualTo(new AppDetail("testApp", "org1", "space1"));
	}

	@Test
	public void retrieveAppDetailsWithException() {

		CloudFoundryClient cfClient = mock(CloudFoundryClient.class);
		ApplicationsV2 mockApplicationsV2 = mock(ApplicationsV2.class);
		when(cfClient.applicationsV2()).thenReturn(mockApplicationsV2);
		GetApplicationResponse getAppResponse = GetApplicationResponse.builder()
				.entity(ApplicationEntity.builder()
						.name("testApp")
						.build())
				.build();
		Throwable anException = new RuntimeException("an explicit exception");
		when(mockApplicationsV2.get(any(GetApplicationRequest.class)))
				.thenReturn(Mono.error(anException));
		AppDetailsService appDetailsService = new CfAppDetailsService(cfClient);
		assertThat(appDetailsService.getApplicationDetail("testid").block()).isEqualTo(new AppDetail("", "", ""));
	}

}
