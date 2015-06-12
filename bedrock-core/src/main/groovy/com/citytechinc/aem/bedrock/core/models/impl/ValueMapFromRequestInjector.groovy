package com.citytechinc.aem.bedrock.core.models.impl

import org.apache.felix.scr.annotations.Component
import org.apache.felix.scr.annotations.Property
import org.apache.felix.scr.annotations.Service
import org.apache.sling.api.SlingHttpServletRequest
import org.apache.sling.models.spi.DisposalCallbackRegistry
import org.apache.sling.models.spi.Injector
import org.osgi.framework.Constants

import java.lang.reflect.AnnotatedElement
import java.lang.reflect.Type

@Component
@Service
@Property(name = Constants.SERVICE_RANKING, intValue = 2500)
class ValueMapFromRequestInjector implements Injector {

    @Override
    String getName() {
        "valuemap"
    }

    @Override
    Object getValue(Object adaptable, String name, Type type, AnnotatedElement element,
        DisposalCallbackRegistry callbackRegistry) {
        def value = null

        if (adaptable instanceof SlingHttpServletRequest) {
            def request = adaptable as SlingHttpServletRequest

            if (request.resource) {
                def map = request.resource.valueMap

                if (map && type instanceof Class<?>) {
                    value = map.get(name, (Class<?>) type)
                }
            }
        }

        value
    }
}
