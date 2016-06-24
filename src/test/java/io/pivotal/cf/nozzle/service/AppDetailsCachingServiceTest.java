package io.pivotal.cf.nozzle.service;

import org.junit.Test;
import reactor.core.publisher.Mono;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AppDetailsCachingServiceTest {

	@Test
	public void testRetrieveAppDetailsFromCache() {

		AppDetailsService appDetailsService = mock(AppDetailsService.class);

		when(appDetailsService.getApplicationDetail("testid1")).thenReturn(Mono.just("testApp1"));
		when(appDetailsService.getApplicationDetail("testid2")).thenReturn(Mono.just("testApp2"));
		when(appDetailsService.getApplicationDetail("testid3")).thenReturn(Mono.just("testApp3"));

		AppDetailsCachingService appDetailsCachingService = new AppDetailsCachingService(appDetailsService);

		assertThat(appDetailsCachingService.getApplicationDetail("testid1")).isEqualTo("testApp1");
		assertThat(appDetailsCachingService.getApplicationDetail("testid2")).isEqualTo("testApp2");
		assertThat(appDetailsCachingService.getApplicationDetail("testid3")).isEqualTo("testApp3");

		when(appDetailsService.getApplicationDetail("testid3")).thenReturn(Mono.just("testADifferentValue"));

		assertThat(appDetailsService.getApplicationDetail("testid3").block()).isEqualTo("testADifferentValue");

		//should retrieve the cached value..
		assertThat(appDetailsCachingService.getApplicationDetail("testid3")).isEqualTo("testApp3");
	}
}
