package io.pivotal.cf.nozzle.service;


import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.ExecutionException;

/**
 * Responsible for caching the application name
 *
 * @author Biju Kunjummen
 */
@Service
public class AppDetailsCachingService {

	private AppDetailsService delegatedService;
	private LoadingCache<String, String> applicationNameCache;

	private final Integer MAX_CACHE_SIZE = 2000;
	private final Integer DEFAULT_TIMEOUT = 2000;

	@Autowired
	public AppDetailsCachingService(AppDetailsService delegatedService) {
		this.delegatedService = delegatedService;
		this.applicationNameCache = CacheBuilder.newBuilder()
				.maximumSize(MAX_CACHE_SIZE)
				.build(
						new CacheLoader<String, String>() {
							public String load(String applicationId) {
								return AppDetailsCachingService.this.delegatedService
										.getApplicationName(applicationId)
										.block(Duration.of(DEFAULT_TIMEOUT, ChronoUnit.MILLIS));
							}
						});
	}

	public String getApplicationName(String applicationId) {
		try {
			return this.applicationNameCache.get(applicationId);
		} catch (Exception e) {
			return "";
		}
	}
}
