package com.citytechinc.aem.bedrock.models.impl

import com.citytechinc.aem.bedrock.api.node.ComponentNode
import com.citytechinc.aem.bedrock.models.annotations.TranslatorInject
import com.day.cq.i18n.I18n
import com.google.common.base.Optional
import groovy.transform.TupleConstructor
import groovy.util.logging.Slf4j
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
import org.osgi.framework.ServiceReference
import org.osgi.util.tracker.ServiceTracker

import java.lang.reflect.AnnotatedElement

@Component
@Service(Injector)
@Property(name = Constants.SERVICE_RANKING, intValue = 4000)
@Slf4j("LOG")
class TranslatorInjector extends AbstractTypedComponentNodeInjector<String> implements Injector,
    InjectAnnotationProcessorFactory2, AcceptsNullName {

    private ServiceTracker resourceBundleProviderTracker
    private int trackingCount = -1
    private final List<ResourceBundleProvider> resourceBundleProviders = []

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
                    value = i18n.get(translatorAnnotation.text(), translatorAnnotation.comment())
                } catch (final MissingResourceException e) {
                    LOG.error("Could not find translation for text '" + translatorAnnotation.text() + "'.", e)
                }
            }
        }

        value
    }

    @SuppressWarnings("unchecked")
    private Optional<ResourceBundle> getResourceBundle(Locale locale) {
        def resourceBundle = Optional.absent()

        updateResourceBundleProviders()
        for (def resourceBundleProvider : resourceBundleProviders) {
            if (!resourceBundle.present) {
                resourceBundle = Optional.fromNullable(resourceBundleProvider.getResourceBundle(locale))
            }
        }

        resourceBundle
    }

    private void updateResourceBundleProviders() {
        //if new services have been registered, update our map
        if (trackingCount < resourceBundleProviderTracker.trackingCount) {
            synchronized (resourceBundleProviders) {
                //if our tracking count is still different, perform the update
                if (trackingCount < resourceBundleProviderTracker.trackingCount) {
                    def updater = {
                        resourceBundleProviders.clear()

                        //TODO: Something is wrong with the sorting here, I think it is throwing an exception
                        //get tracking count that matches the registered services at the time of the update.
                        def trackingCountForUpdate = resourceBundleProviderTracker.trackingCount
                        def serviceReferences = resourceBundleProviderTracker.serviceReferences.sort(
                            false, Collections.reverseOrder()
                        )
                        serviceReferences.every { serviceReference ->
                            def service = resourceBundleProviderTracker.getService((ServiceReference) serviceReference)
                            if (service) {
                                resourceBundleProviders.add((ResourceBundleProvider) service)
                            }
                        }

                        trackingCountForUpdate
                    }

                    //keep updating service references if they keep updating in the tracker.
                    def trackingCountForUpdate = updater()
                    while (trackingCountForUpdate < resourceBundleProviderTracker.trackingCount) {
                        LOG.debug("ResourceBundleProvider references changed during update, running update again.")
                        trackingCountForUpdate = updater()
                    }

                    //update tracking count to the one used for the update
                    trackingCount = trackingCountForUpdate
                }
            }
        }
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
        resourceBundleProviders.clear()
        trackingCount = -1
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
        trackingCount = -1
        resourceBundleProviders.clear()
    }
}
