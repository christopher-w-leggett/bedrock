package com.citytechinc.aem.bedrock.core.services.cache

import static com.google.common.base.Preconditions.checkNotNull

import java.lang.reflect.Field

import org.slf4j.Logger

import com.google.common.cache.Cache
import com.google.common.cache.CacheStats
import com.google.common.collect.ImmutableList
import com.google.common.collect.Lists

trait CacheServiceTrait implements CacheService{

	@Override
	boolean clearAllCaches() {
		boolean cleared = false
		for (final Field field : collectFields(this.getClass())) {
			if (isCache(field)) {
				try {
					getCache(field).invalidateAll()
					cleared = true
				} catch (final Exception e) {
					getLogger().error(
							"An error has occurred while attempting to invalidate cache values for " + field.getName()
							+ " in the class " + this.getClass().getName() + ".", e)
				}
			}
		}
		return cleared
	}

	@Override
	boolean clearSpecificCache(final String cacheVariableName) {
		checkNotNull(cacheVariableName, "cache name must not be null")

		boolean cleared = false

		for (final Field field : collectFields(this.getClass())) {
			if (isNamedCache(field, cacheVariableName)) {
				try {
					getCache(field).invalidateAll()
					cleared = true
				} catch (final Exception e) {
					getLogger().error(
							"An error has occurred while attempting to invalidate cache values for " + field.getName()
							+ " in the class " + this.getClass().getName() + ".", e)
				}
			}
		}

		return cleared
	}

	@Override
	Long getCacheSize(final String cacheVariableName) {
		checkNotNull(cacheVariableName, "cache name must not be null")

		Long cacheSize = 0L

		for (final Field field : collectFields()) {
			if (isNamedCache(field, cacheVariableName)) {
				try {
					cacheSize = getCache(field).size()
				} catch (final Exception e) {
					getLogger().error("An error has occurred while attempting retrieve cache size for " + field
							.getName() + " in the class " + this.getClass().getName() + ".", e)
				}
			}
		}

		return cacheSize
	}

	@Override
	CacheStats getCacheStats(final String cacheVariableName) {
		checkNotNull(cacheVariableName, "cache name must not be null")

		CacheStats cacheStats = null

		for (final Field field : collectFields()) {
			if (isNamedCache(field, cacheVariableName)) {
				try {
					cacheStats = getCache(field).stats()
				} catch (final Exception e) {
					getLogger().error("An error has occurred while attempting retrieve cache statistics for " + field
							.getName() + " in the class " + this.getClass().getName() + ".", e)
				}
			}
		}

		return cacheStats
	}

	@Override
	List<String> listCaches() {
		final ImmutableList.Builder<String> cachesBuilder = new ImmutableList.Builder<String>()

		for (final Field field : collectFields()) {
			if (isCache(field)) {
				cachesBuilder.add(field.getName())
			}
		}

		return cachesBuilder.build()
	}

	abstract Logger getLogger()

	private List<Field> collectFields() {
		return collectFields(this.getClass())
	}

	private static List<Field> collectFields(final Class clazz) {
		final List<Field> fields = Lists.newArrayList()

		if (clazz != null) {
			fields.addAll(Arrays.asList(clazz.getDeclaredFields()))
			fields.addAll(collectFields(clazz.getSuperclass()))
		}

		return fields
	}

	private static boolean isNamedCache(final Field field, final String cacheVariableName) {
		return isCache(field) && cacheVariableName.equals(field.getName())
	}

	private static boolean isCache(final Field field) {
		return isCacheType(field) || isAssignableFromCache(field)
	}

	private static boolean isCacheType(final Field field) {
		return field.getType() == Cache.class
	}

	private static boolean isAssignableFromCache(final Field field) {
		return Cache.class.isAssignableFrom(field.getType())
	}

	private Cache getCache(final Field field) throws IllegalAccessException {
		field.setAccessible(true)

		return (Cache) field.get(this)
	}
}
