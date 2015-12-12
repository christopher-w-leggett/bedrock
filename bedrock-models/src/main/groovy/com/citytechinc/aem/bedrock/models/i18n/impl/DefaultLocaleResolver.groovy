package com.citytechinc.aem.bedrock.models.i18n.impl

import com.citytechinc.aem.bedrock.models.i18n.LocaleResolver
import com.day.cq.wcm.api.Page
import com.day.cq.wcm.api.PageManager
import com.google.common.base.Optional
import org.apache.sling.api.resource.Resource
import org.apache.sling.api.resource.ResourceResolver

class DefaultLocaleResolver implements LocaleResolver {
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
