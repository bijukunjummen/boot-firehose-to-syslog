package io.pivotal.cf.nozzle.service;

import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.client.v2.applications.ApplicationEntity;
import org.cloudfoundry.client.v2.applications.ApplicationsV2;
import org.cloudfoundry.client.v2.applications.GetApplicationRequest;
import org.cloudfoundry.client.v2.applications.GetApplicationResponse;
import org.junit.Test;
import reactor.core.publisher.Mono;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class AppDetailsServiceTest {

	@Test
	public void retrieveAppDetails() {

		CloudFoundryClient cfClient = mock(CloudFoundryClient.class);
		ApplicationsV2 mockApplicationsV3 = mock(ApplicationsV2.class);
		when(cfClient.applicationsV2()).thenReturn(mockApplicationsV3);
		GetApplicationResponse getAppResponse = GetApplicationResponse.builder()
				.entity(ApplicationEntity.builder()
						.name("testApp")
						.build())
				.build();
		when(mockApplicationsV3.get(any(GetApplicationRequest.class))).thenReturn(Mono.just(getAppResponse));
		AppDetailsService appDetailsService = new CfAppDetailsService(cfClient);
		assertThat(appDetailsService.getApplicationName("testid").block()).isEqualTo("testApp");
	}
}
