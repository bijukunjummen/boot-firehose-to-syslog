package io.pivotal.cf.nozzle.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import io.pivotal.cf.nozzle.model.AppDetail;
import reactor.core.publisher.Mono;

/**
 * Responsible for caching the application name
 *
 * @author Biju Kunjummen
 */
@Service
public class AppDetailsCachingService {

	private final AppDetailsService delegatedService;
	private final Cache<String, AppDetail> applicationNameCache;

	private final Integer MAX_CACHE_SIZE = 2000;

	@Autowired
	public AppDetailsCachingService(AppDetailsService delegatedService) {
		this.delegatedService = delegatedService;
		this.applicationNameCache = CacheBuilder.newBuilder()
				.maximumSize(MAX_CACHE_SIZE)
				.build();
	}

	public Mono<AppDetail> getApplicationDetail(String applicationId) {
		AppDetail appDetail = this.applicationNameCache.getIfPresent(applicationId);
		if (appDetail != null) {
			return Mono.just(appDetail);
		}
		return this.delegatedService.getApplicationDetail(applicationId).doOnNext(detail -> this.applicationNameCache.put(applicationId, detail));
	}
}
