package com.citytechinc.aem.bedrock.core.services.cache

import com.google.common.cache.Cache
import com.google.common.cache.CacheStats
import com.google.common.collect.ImmutableList
import org.slf4j.Logger

import java.lang.reflect.Field

import static com.google.common.base.Preconditions.checkNotNull

trait CacheServiceTrait implements CacheService {

    @Override
    boolean clearAllCaches() {
        boolean cleared = false

        collectFields(this.class).each { field ->
            if (isCache(field)) {
                try {
                    getCache(field).invalidateAll()
                    cleared = true
                } catch (Exception e) {
                    logger.error("An error has occurred while attempting to invalidate cache values for " +
                        "${field.name} in the class ${this.class.name}.", e)
                }
            }
        }

        cleared
    }

    @Override
    boolean clearSpecificCache(String cacheVariableName) {
        checkNotNull(cacheVariableName, "cache name must not be null")

        boolean cleared = false

        collectFields(this.class).each { field ->
            if (isNamedCache(field, cacheVariableName)) {
                try {
                    getCache(field).invalidateAll()
                    cleared = true
                } catch (Exception e) {
                    logger.error("An error has occurred while attempting to invalidate cache values for " +
                        "${field.name} in the class ${this.class.name}.", e)
                }
            }
        }

        cleared
    }

    @Override
    Long getCacheSize(String cacheVariableName) {
        checkNotNull(cacheVariableName, "cache name must not be null")

        def cacheSize = 0L

        collectFields().each { field ->
            if (isNamedCache(field, cacheVariableName)) {
                try {
                    cacheSize = getCache(field).size()
                } catch (Exception e) {
                    logger.error("An error has occurred while attempting retrieve cache size for ${field.name} in " +
                        "the class ${this.class.name}.", e)
                }
            }
        }

        cacheSize
    }

    @Override
    CacheStats getCacheStats(String cacheVariableName) {
        checkNotNull(cacheVariableName, "cache name must not be null")

        def cacheStats = null

        collectFields().each { field ->
            if (isNamedCache(field, cacheVariableName)) {
                try {
                    cacheStats = getCache(field).stats()
                } catch (Exception e) {
                    logger.error("An error has occurred while attempting retrieve cache statistics for ${field.name} " +
                        "in the class ${this.class.name}.", e)
                }
            }
        }

        cacheStats
    }

    @Override
    List<String> listCaches() {
        def cachesBuilder = new ImmutableList.Builder<String>()

        collectFields().each { field ->
            if (isCache(field)) {
                cachesBuilder.add(field.name)
            }
        }

        cachesBuilder.build()
    }

    abstract Logger getLogger()

    private List<Field> collectFields() {
        collectFields(this.class)
    }

    private static List<Field> collectFields(Class clazz) {
        def fields = []

        if (clazz) {
            fields.addAll(clazz.declaredFields as List)
            fields.addAll(collectFields(clazz.superclass))
        }

        fields
    }

    private static boolean isNamedCache(Field field, String cacheVariableName) {
        isCache(field) && cacheVariableName == field.name
    }

    private static boolean isCache(Field field) {
        isCacheType(field) || isAssignableFromCache(field)
    }

    private static boolean isCacheType(Field field) {
        field.type == Cache
    }

    private static boolean isAssignableFromCache(Field field) {
        Cache.isAssignableFrom(field.type)
    }

    private Cache getCache(Field field) {
        field.setAccessible(true)

        (Cache) field.get(this)
    }
}
