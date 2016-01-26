package com.citytechinc.aem.bedrock.models.i18n.impl

import com.citytechinc.aem.bedrock.models.i18n.LocaleResolver
import com.day.cq.wcm.api.Page
import com.day.cq.wcm.api.PageManager
import com.google.common.base.Optional
import org.apache.felix.scr.annotations.Component
import org.apache.felix.scr.annotations.Service
import org.apache.sling.api.resource.Resource
import org.apache.sling.api.resource.ResourceResolver

/**
 * A default {@code LocaleResolver} implementation that uses the out of the box AEM logic for resolving a locale.
 */
@Component
@Service(LocaleResolver)
class DefaultLocaleResolver implements LocaleResolver {
    /**
     * Uses the resource resolver provided to find the containing page for the resource and finds the locale
     * for the page.
     *
     * @param resource The resource to resolve the locale for.
     * @param resourceResolver The resource resolver.
     * @return The locale or Optional.absent() if one could not be found.
     */
    @Override
    Optional<Locale> resolve(final Resource resource, final ResourceResolver resourceResolver) {
        final Optional<Locale> locale

        final Optional<Page> resourcePage = Optional.fromNullable(
            resourceResolver.adaptTo(PageManager.class).getContainingPage(resource)
        )
        if (resourcePage.present) {
            locale = Optional.of(resourcePage.get().getLanguage(false))
        } else {
            locale = Optional.absent()
        }

        locale
    }
}
