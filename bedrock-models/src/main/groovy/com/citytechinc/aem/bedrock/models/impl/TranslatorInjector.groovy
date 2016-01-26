package com.citytechinc.aem.bedrock.models.impl

import com.citytechinc.aem.bedrock.api.node.ComponentNode
import com.citytechinc.aem.bedrock.models.annotations.TranslatorInject
import com.citytechinc.aem.bedrock.models.i18n.LocaleResolver
import com.day.cq.i18n.I18n
import com.google.common.base.Optional
import com.google.common.base.Strings
import groovy.transform.TupleConstructor
import groovy.util.logging.Slf4j
import org.apache.felix.scr.annotations.Activate
import org.apache.felix.scr.annotations.Component
import org.apache.felix.scr.annotations.Deactivate
import org.apache.felix.scr.annotations.Modified
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
import org.osgi.framework.Filter
import org.osgi.framework.ServiceReference
import org.osgi.util.tracker.ServiceTracker

import java.lang.reflect.AnnotatedElement

@Component
@Service(Injector)
@Property(name = Constants.SERVICE_RANKING, intValue = 4000)
@Slf4j("LOG")
class TranslatorInjector extends AbstractTypedComponentNodeInjector<String> implements Injector,
    InjectAnnotationProcessorFactory2, AcceptsNullName {

    private BundleContext bundleContext
    private Tracker<ResourceBundleProvider> resourceBundleProviderTracker
    private Tracker<LocaleResolver> localeResolverTracker

    @Override
    String getName() {
        TranslatorInject.NAME
    }

    @Override
    Object getValue(ComponentNode componentNode, String name, Class<String> declaredType,
                    AnnotatedElement element, DisposalCallbackRegistry callbackRegistry) {
        def translatorAnnotation = element.getAnnotation(TranslatorInject)

        def value = null

        if (translatorAnnotation) {
            Optional<Filter> filter = Optional.absent()
            if (!Strings.isNullOrEmpty(translatorAnnotation.localeResolverFilter())) {
                filter = Optional.of(bundleContext.createFilter(translatorAnnotation.localeResolverFilter()))
            }

            def locale = localeResolverTracker.getService { ServiceReference serviceReference ->
                !filter.isPresent() || filter.get().match(serviceReference)
            }.get().resolve(
                componentNode.resource, componentNode.resource.resourceResolver
            )

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
        }

        value
    }

    @SuppressWarnings("unchecked")
    private Optional<ResourceBundle> getResourceBundle(Locale locale) {
        def resourceBundle = Optional.absent()

        for (def resourceBundleProvider : resourceBundleProviderTracker.services) {
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
    @Modified
    protected final void activate(BundleContext bundleContext) {
        this.bundleContext = bundleContext
        //TODO: Something strange is happening where localeResolverTracker must be instantiated and opened before resourceBundleProviderTracker.
        localeResolverTracker = new Tracker<>(LocaleResolver, bundleContext)
        localeResolverTracker.open()
        resourceBundleProviderTracker = new Tracker<>(ResourceBundleProvider, bundleContext)
        resourceBundleProviderTracker.open()
    }

    /**
     * Deactivates this {@code Injector} and closes the {@code ServiceTracker} if it was previously opened.
     */
    @Deactivate
    protected final void deactivate() {
        bundleContext = null
        if (resourceBundleProviderTracker) {
            resourceBundleProviderTracker.close()
            resourceBundleProviderTracker = null
        }
        if (localeResolverTracker) {
            localeResolverTracker.close()
            localeResolverTracker = null
        }
    }

    private static final class Tracker<T> {
        private final ServiceTracker serviceTracker
        private int trackingCount = -1
        private final List<T> trackedServices = []

        private Tracker(Class<T> clazz, BundleContext bundleContext) {
            serviceTracker = new ServiceTracker(bundleContext, clazz.name, null)
        }

        public List<T> getServices() {
            updateServices()
            trackedServices
        }

        public Optional<T> getService(Closure<Boolean> closure) {
            def service = Optional.absent()

            def serviceReferences = serviceTracker.serviceReferences.sort(
                false, Collections.reverseOrder()
            )
            def serviceReference = serviceReferences.find(closure)
            if (serviceReference) {
                service = Optional.fromNullable(serviceTracker.getService(serviceReference))
            }

            service
        }

        public void open() {
            serviceTracker.open(true)
        }

        public void close() {
            serviceTracker.close()
        }

        private void updateServices() {
            //if new services have been registered, update tracked services
            if (trackingCount < serviceTracker.trackingCount) {
                synchronized (trackedServices) {
                    //if our tracking count is still different, perform the update
                    if (trackingCount < serviceTracker.trackingCount) {
                        def updater = {
                            trackedServices.clear()

                            //get tracking count that matches the registered services at the time of the update.
                            def trackingCountForUpdate = serviceTracker.trackingCount
                            def serviceReferences = serviceTracker.serviceReferences.sort(
                                false, Collections.reverseOrder()
                            )
                            serviceReferences.every { serviceReference ->
                                def service = serviceTracker.getService((ServiceReference) serviceReference)
                                if (service) {
                                    trackedServices.add((T) service)
                                }
                            }

                            trackingCountForUpdate
                        }

                        //keep updating service references if they keep updating in the tracker.
                        def trackingCountForUpdate = updater()
                        while (trackingCountForUpdate < serviceTracker.trackingCount) {
                            LOG.debug("Service references changed during update, running update again.")
                            trackingCountForUpdate = updater()
                        }

                        //update tracking count to the one used for the update
                        trackingCount = trackingCountForUpdate
                    }
                }
            }
        }
    }
}
