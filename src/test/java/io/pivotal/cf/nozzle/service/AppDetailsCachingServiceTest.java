package io.pivotal.cf.nozzle.service;

import io.pivotal.cf.nozzle.model.AppDetail;
import org.junit.Test;
import reactor.core.publisher.Mono;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AppDetailsCachingServiceTest {

	@Test
	public void testRetrieveAppDetailsFromCache() {

		AppDetailsService appDetailsService = mock(AppDetailsService.class);

		when(appDetailsService.getApplicationDetail("testid1")).thenReturn(Mono.just(new AppDetail("testApp1", "testSpace", "testOrg")));
		when(appDetailsService.getApplicationDetail("testid2")).thenReturn(Mono.just(new AppDetail("testApp2", "testSpace", "testOrg")));
		when(appDetailsService.getApplicationDetail("testid3")).thenReturn(Mono.just(new AppDetail("testApp3", "testSpace", "testOrg")));

		AppDetailsCachingService appDetailsCachingService = new AppDetailsCachingService(appDetailsService);

		assertThat(appDetailsCachingService.getApplicationDetail("testid1").getApplicationName()).isEqualTo("testApp1");
		assertThat(appDetailsCachingService.getApplicationDetail("testid2").getApplicationName()).isEqualTo("testApp2");
		assertThat(appDetailsCachingService.getApplicationDetail("testid3").getApplicationName()).isEqualTo("testApp3");

		when(appDetailsService.getApplicationDetail("testid3")).thenReturn(Mono.just(new AppDetail("testADifferentValue", "testSpace", "testOrg")));

		assertThat(appDetailsService.getApplicationDetail("testid3").block().getApplicationName()).isEqualTo("testADifferentValue");

		//should retrieve the cached value..
		assertThat(appDetailsCachingService.getApplicationDetail("testid3").getApplicationName()).isEqualTo("testApp3");
	}
}
