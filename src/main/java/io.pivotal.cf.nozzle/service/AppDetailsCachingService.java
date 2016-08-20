package io.pivotal.cf.nozzle.service;


import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import io.pivotal.cf.nozzle.model.AppDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Responsible for caching the application name
 *
 * @author Biju Kunjummen
 */
@Service
public class AppDetailsCachingService {

	private final AppDetailsService delegatedService;
	private final LoadingCache<String, AppDetail> applicationNameCache;

	private final Integer MAX_CACHE_SIZE = 2000;
	private final Integer DEFAULT_TIMEOUT = 2000;

	private ExecutorService executorService;

	@Autowired
	public AppDetailsCachingService(AppDetailsService delegatedService) {
		this.delegatedService = delegatedService;
		this.executorService = Executors.newFixedThreadPool(2);
		Scheduler scheduler = Schedulers.fromExecutor(executorService);
		this.applicationNameCache = CacheBuilder.newBuilder()
				.maximumSize(MAX_CACHE_SIZE)
				.build(
						new CacheLoader<String, AppDetail>() {
							public AppDetail load(String applicationId) {
								AppDetail retrievedAppDetail = AppDetailsCachingService.this.delegatedService
										.getApplicationDetail(applicationId)
										.subscribeOn(scheduler)
										.block(Duration.of(DEFAULT_TIMEOUT, ChronoUnit.MILLIS));
								return retrievedAppDetail;
							}
						});
	}

	public AppDetail getApplicationDetail(String applicationId) {
		try {
			return this.applicationNameCache.get(applicationId);
		} catch (Exception e) {
			return new AppDetail("", "", "");
		}
	}
}
