package com.citytechinc.aem.bedrock.core.services.cache;

import java.util.List;

import com.google.common.cache.CacheStats;

public interface CacheService {

	/**
	 * Clear all caches.
	 */
	boolean clearAllCaches();

	/**
	 * @param cacheVariableName cache name
	 */
	boolean clearSpecificCache(String cacheVariableName);

	/**
	 * @param cacheVariableName cache name
	 * @return cache size
	 */
	Long getCacheSize(String cacheVariableName);

	/**
	 * @param cacheVariableName cache name
	 * @return cache stats
	 */
	CacheStats getCacheStats(String cacheVariableName);

	/**
	 * @return list of cache names
	 */
	List<String> listCaches();
}