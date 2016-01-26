package com.citytechinc.aem.bedrock.models.impl

import com.citytechinc.aem.bedrock.api.node.ComponentNode
import com.citytechinc.aem.bedrock.models.annotations.TranslatorInject
import com.citytechinc.aem.bedrock.models.i18n.LocaleResolver
import com.day.cq.i18n.I18n
import com.google.common.base.Function
import com.google.common.base.Optional
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
public final class TranslatorInjector extends AbstractTypedComponentNodeInjector<String> implements Injector,
    InjectAnnotationProcessorFactory2, AcceptsNullName {

    private BundleContext bundleContext
    private Tracker<ResourceBundleProvider> resourceBundleProviderTracker
    private Tracker<LocaleResolver> localeResolverTracker

    @Override
    public String getName() {
        TranslatorInject.NAME
    }

    @Override
    public Object getValue(final ComponentNode componentNode, final String name, final Class<String> declaredType,
                           final AnnotatedElement element, final DisposalCallbackRegistry callbackRegistry) {
        final def translatorAnnotation = element.getAnnotation(TranslatorInject)

        def value = null

        if (translatorAnnotation) {
            //if we were provided a filter string, construct our service reference filter
            final Optional<Filter> filter = translatorAnnotation.localeResolverFilter() ?
                Optional.of(bundleContext.createFilter(translatorAnnotation.localeResolverFilter())) :
                Optional.absent();

            //grab highest ranking service that matches the filter or the first service if no filter was provided
            final Optional<LocaleResolver> localeResolver = localeResolverTracker.getService { serviceReference ->
                !filter.isPresent() || filter.get().match(serviceReference)
            }

            //resolve the Locale using the found LocaleResolver
            final Optional<Locale> locale = localeResolver.present ? localeResolver.get().resolve(
                componentNode.resource, componentNode.resource.resourceResolver
            ) : Optional.absent()

            if (locale.present) {
                //find a ResourceBundle for the determined Locale
                final Optional<ResourceBundle> resourceBundle = Optional.fromNullable(
                    resourceBundleProviderTracker.services.findResult { resourceBundleProvider ->
                        resourceBundleProvider.getResourceBundle(locale.get())
                    }
                )

                if (resourceBundle.present) {
                    //grab translated value
                    final def i18n = new I18n(resourceBundle.get())
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

    @Override
    InjectAnnotationProcessor2 createAnnotationProcessor(final Object adaptable, final AnnotatedElement element) {
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
     * Activates this {@code Injector} and opens {@code ServiceTracker} objects for tracking
     * {@code ResourceBundleProvider} and {@code LocaleResolver} implementations.
     *
     * @param bundleContext The {@code BundleContext}.
     */
    @Activate
    @Modified
    protected final void activate(final BundleContext bundleContext) {
        this.bundleContext = bundleContext
        //For some reason localeResolverTracker needs to be opened first for unit tests to work.
        localeResolverTracker = new Tracker<>(LocaleResolver, bundleContext)
        localeResolverTracker.open()
        resourceBundleProviderTracker = new Tracker<>(ResourceBundleProvider, bundleContext)
        resourceBundleProviderTracker.open()
    }

    /**
     * Deactivates this {@code Injector} and closes the {@code ServiceTracker} objects if it was previously opened.
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

    /**
     * Class used to provide additional capabilities over the OSGi {@code ServiceTracker}.
     * @param < T >                                  The type of service being tracked.
     */
    private static final class Tracker<T> {
        private final ServiceTracker serviceTracker

        /**
         * Constructs a new {@code Tracker} for tracking OSGi services.
         * @param clazz The service class to track.
         * @param bundleContext The current bundle context.
         */
        private Tracker(final Class<T> clazz, final BundleContext bundleContext) {
            this.serviceTracker = new ServiceTracker(bundleContext, clazz.name, null)
        }

        /**
         * Gets the tracked OSGi services in order of OSGi service ranking.
         * @return The ordered services.
         */
        public List<T> getServices() {
            getServiceReferences().collect { serviceReference ->
                (T) serviceTracker.getService(serviceReference)
            }
        }

        /**
         * Gets a tracked OSGi service who's service reference satisfies the provided closure.  The tracked service
         * references are compared in order of OSGi service ranking.
         * @param closure Closure used to target a service reference.
         * @return The service who's service reference satisfies the closure or Optional.absent() if one wasn't found.
         */
        public Optional<T> getService(final Closure<Boolean> closure) {
            Optional.fromNullable(getServiceReferences().find(closure))
                .transform((Function) { serviceReference -> serviceTracker.getService(serviceReference) })
        }

        /**
         * Gets service references for the tracked OSGi services in order of OSGi service ranking.
         * @return The ordered service references.
         */
        public List<ServiceReference> getServiceReferences() {
            serviceTracker.serviceReferences.sort(
                false, Collections.reverseOrder()
            ).collect()
        }

        /**
         * Opens the tracker and starts tracking services.
         */
        public void open() {
            serviceTracker.open(true)
        }

        /**
         * Closes the tracker and stops tracking services.
         */
        public void close() {
            serviceTracker.close()
        }
    }
}
