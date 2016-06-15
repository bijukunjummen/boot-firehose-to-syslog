package io.pivotal.cf.nozzle.service;

import org.junit.Test;
import reactor.core.publisher.Mono;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AppDetailsCachingServiceTest {

	@Test
	public void testRetrieveAppDetailsFromCache() {

		AppDetailsService appDetailsService = mock(AppDetailsService.class);

		when(appDetailsService.getApplicationName("testid1")).thenReturn(Mono.just("testApp1"));
		when(appDetailsService.getApplicationName("testid2")).thenReturn(Mono.just("testApp2"));
		when(appDetailsService.getApplicationName("testid3")).thenReturn(Mono.just("testApp3"));

		AppDetailsCachingService appDetailsCachingService = new AppDetailsCachingService(appDetailsService);

		assertThat(appDetailsCachingService.getApplicationName("testid1")).isEqualTo("testApp1");
		assertThat(appDetailsCachingService.getApplicationName("testid2")).isEqualTo("testApp2");
		assertThat(appDetailsCachingService.getApplicationName("testid3")).isEqualTo("testApp3");

		when(appDetailsService.getApplicationName("testid3")).thenReturn(Mono.just("testADifferentValue"));

		assertThat(appDetailsService.getApplicationName("testid3").block()).isEqualTo("testADifferentValue");

		//should retrieve the cached value..
		assertThat(appDetailsCachingService.getApplicationName("testid3")).isEqualTo("testApp3");
	}
}
