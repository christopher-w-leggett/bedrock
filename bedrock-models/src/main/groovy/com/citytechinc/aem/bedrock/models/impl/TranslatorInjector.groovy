package com.citytechinc.aem.bedrock.models.impl

import com.citytechinc.aem.bedrock.api.node.ComponentNode
import com.citytechinc.aem.bedrock.models.annotations.TranslatorInject
import com.day.cq.i18n.I18n
import com.google.common.base.Optional
import groovy.transform.TupleConstructor
import org.apache.felix.scr.annotations.Activate
import org.apache.felix.scr.annotations.Component
import org.apache.felix.scr.annotations.Deactivate
import org.apache.felix.scr.annotations.Property
import org.apache.felix.scr.annotations.Service
import org.apache.sling.i18n.ResourceBundleProvider
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy
import org.apache.sling.models.spi.AcceptsNullName
import org.apache.sling.models.spi.DisposalCallbackRegistry
import org.apache.sling.models.spi.Injector
import org.apache.sling.models.spi.injectorspecific.AbstractInjectAnnotationProcessor2
import org.apache.sling.models.spi.injectorspecific.InjectAnnotationProcessor2
import org.apache.sling.models.spi.injectorspecific.InjectAnnotationProcessorFactory2
import org.osgi.framework.BundleContext
import org.osgi.framework.Constants
import org.osgi.util.tracker.ServiceTracker

import java.lang.reflect.AnnotatedElement

@Component
@Service(Injector)
@Property(name = Constants.SERVICE_RANKING, intValue = 4000)
class TranslatorInjector extends AbstractTypedComponentNodeInjector<String> implements Injector,
    InjectAnnotationProcessorFactory2, AcceptsNullName {

    private def resourceBundleProviderTracker;

    @Override
    String getName() {
        TranslatorInject.NAME
    }

    @Override
    Object getValue(ComponentNode componentNode, String name, Class<String> declaredType,
                    AnnotatedElement element, DisposalCallbackRegistry callbackRegistry) {
        def translatorAnnotation = element.getAnnotation(TranslatorInject)

        def locale = translatorAnnotation.localeResolver().newInstance().resolve(
            componentNode.resource, componentNode.resource.resourceResolver
        )

        def value = null

        if (locale.present) {
            def resourceBundle = getResourceBundle(locale.get())
            if (resourceBundle.present) {
                def i18n = new I18n(resourceBundle.get())
                try {
                    value = i18n.get(translatorAnnotation.key())
                } catch (final MissingResourceException e) {
                    //LOGGER.error("Could not find translation for key '" + key + "'.");
                }
            }
        }

        value
    }

    @SuppressWarnings("unchecked")
    private Optional<ResourceBundle> getResourceBundle(Locale locale) {
        def resourceBundle = Optional.absent()

        def resourceBundleProviders = resourceBundleProviderTracker.tracked.values()
        for (def resourceBundleProvider : resourceBundleProviders) {
            if (!resourceBundle.present) {
                resourceBundle = Optional.fromNullable(resourceBundleProvider.getResourceBundle(locale))
            }
        }

        resourceBundle
    }

    @Override
    InjectAnnotationProcessor2 createAnnotationProcessor(Object adaptable, AnnotatedElement element) {
        // check if the element has the expected annotation
        def annotation = element.getAnnotation(TranslatorInject)

        annotation ? new TranslatorAnnotationProcessor(annotation) : null
    }

    @TupleConstructor
    private static class TranslatorAnnotationProcessor extends AbstractInjectAnnotationProcessor2 {

        TranslatorInject annotation

        @Override
        InjectionStrategy getInjectionStrategy() {
            annotation.injectionStrategy()
        }
    }

    /**
     * Activates this {@code Injector} and opens a {@code ServiceTracker} for tracking
     * {@code ResourceBundleProvider} implementations.
     *
     * @param bundleContext The {@code BundleContext}.
     */
    @Activate
    @SuppressWarnings("unchecked")
    protected final void activate(BundleContext bundleContext) {
        resourceBundleProviderTracker = new ServiceTracker(
            bundleContext,
            ResourceBundleProvider.class.getName(),
            null
        )
        resourceBundleProviderTracker.open()
    }

    /**
     * Deactivates this {@code Injector} and closes the {@code ServiceTracker} if it was previously opened.
     */
    @Deactivate
    protected final void deactivate() {
        if (resourceBundleProviderTracker != null) {
            resourceBundleProviderTracker.close()
            resourceBundleProviderTracker = null
        }
    }
}
