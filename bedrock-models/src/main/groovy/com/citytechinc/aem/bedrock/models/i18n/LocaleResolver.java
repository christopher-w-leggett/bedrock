package com.citytechinc.aem.bedrock.models.i18n;

import com.google.common.base.Optional;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;

import java.util.Locale;

/**
 * A {@code LocaleResolver} is responsible for locating a locale from a resource.
 */
public interface LocaleResolver {
    /**
     * Resolves a locale for the provided resource.
     *
     * @param resource         The resource to resolve the locale for.
     * @param resourceResolver The resource resolver.
     * @return The locale or Optional.absent() if one could not be found.
     */
    Optional<Locale> resolve(final Resource resource, final ResourceResolver resourceResolver);
}
