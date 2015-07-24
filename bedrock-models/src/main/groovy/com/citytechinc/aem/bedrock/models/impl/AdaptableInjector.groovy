package com.citytechinc.aem.bedrock.models.impl

import com.citytechinc.aem.bedrock.models.utils.ModelUtils
import groovy.util.logging.Slf4j
import org.apache.felix.scr.annotations.Component
import org.apache.felix.scr.annotations.Property
import org.apache.felix.scr.annotations.Service
import org.apache.sling.models.spi.DisposalCallbackRegistry
import org.apache.sling.models.spi.Injector
import org.osgi.framework.Constants

import java.lang.reflect.AnnotatedElement
import java.lang.reflect.Type

/**
 * Injector for objects that are adaptable from the Sling resource resolver.
 */
@Component
@Service(Injector)
@Property(name = Constants.SERVICE_RANKING, intValue = Integer.MIN_VALUE)
@Slf4j("LOG")
class AdaptableInjector implements Injector {

    @Override
    String getName() {
        "adaptable"
    }

    @Override
    Object getValue(Object adaptable, String name, Type type, AnnotatedElement element,
        DisposalCallbackRegistry registry) {
        def value = null

        if (type instanceof Class) {
            def clazz = type as Class

            def resourceResolver = ModelUtils.getResource(adaptable)?.resourceResolver

            if (resourceResolver) {
                value = resourceResolver.adaptTo(clazz)
            }
        }

        value
    }
}
