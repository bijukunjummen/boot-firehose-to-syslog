package io.pivotal.cf.nozzle.service;

import io.pivotal.cf.nozzle.model.AppDetail;
import org.junit.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AppDetailsCachingServiceTest {

	@Test
	public void testRetrieveAppDetailsFromCache() {

		AppDetailsService appDetailsService = mock(AppDetailsService.class);

		when(appDetailsService.getApplicationDetail("testid1"))
				.thenReturn(Mono.just(new AppDetail("testApp1", "testSpace", "testOrg")));
		when(appDetailsService.getApplicationDetail("testid2"))
				.thenReturn(Mono.just(new AppDetail("testApp2", "testSpace", "testOrg")));
		when(appDetailsService.getApplicationDetail("testid3"))
				.thenReturn(Mono.just(new AppDetail("testApp3", "testSpace", "testOrg")));

		AppDetailsCachingService appDetailsCachingService = new AppDetailsCachingService(
				appDetailsService);

		StepVerifier.create(appDetailsCachingService.getApplicationDetail("testid1"))
				.expectNextMatches(
						appDetail -> appDetail.getApplicationName().equals("testApp1"))
				.expectComplete().verify();

		StepVerifier.create(appDetailsCachingService.getApplicationDetail("testid2"))
				.expectNextMatches(
						appDetail -> appDetail.getApplicationName().equals("testApp2"))
				.expectComplete().verify();

		StepVerifier.create(appDetailsCachingService.getApplicationDetail("testid3"))
				.expectNextMatches(
						appDetail -> appDetail.getApplicationName().equals("testApp3"))
				.expectComplete().verify();

		 when(appDetailsService.getApplicationDetail("testid3")).thenReturn(Mono.just(new
		 AppDetail("testADifferentValue", "testSpace", "testOrg")));

		 assertThat(appDetailsService.getApplicationDetail("testid3").block().getApplicationName()).isEqualTo("testADifferentValue");

		// //should retrieve the cached value..
		StepVerifier.create(appDetailsCachingService.getApplicationDetail("testid3"))
				.expectNextMatches(
						appDetail -> appDetail.getApplicationName().equals("testApp3"))
				.expectComplete().verify();
	}
}
