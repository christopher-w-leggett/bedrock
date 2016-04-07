package com.citytechinc.aem.bedrock.models.impl

import com.citytechinc.aem.bedrock.api.node.ComponentNode
import com.citytechinc.aem.bedrock.models.annotations.TranslatorInject
import com.citytechinc.aem.bedrock.models.i18n.LocaleResolver
import com.day.cq.i18n.I18n
import com.google.common.base.Optional
import groovy.transform.TupleConstructor
import groovy.util.logging.Slf4j
import org.apache.felix.scr.annotations.Activate
import org.apache.felix.scr.annotations.Component
import org.apache.felix.scr.annotations.Deactivate
import org.apache.felix.scr.annotations.Modified
import org.apache.felix.scr.annotations.Property
import org.apache.felix.scr.annotations.Reference
import org.apache.felix.scr.annotations.ReferenceCardinality
import org.apache.felix.scr.annotations.ReferencePolicy
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

import java.lang.reflect.AnnotatedElement
import java.util.concurrent.CopyOnWriteArrayList

@Component
@Service(Injector)
@Property(name = Constants.SERVICE_RANKING, intValue = 4000)
@Slf4j("LOG")
public final class TranslatorInjector extends AbstractTypedComponentNodeInjector<String> implements Injector,
    InjectAnnotationProcessorFactory2, AcceptsNullName {

    @Reference(cardinality = ReferenceCardinality.OPTIONAL_MULTIPLE,
        policy = ReferencePolicy.DYNAMIC,
        bind = "bindResourceBundleProvider",
        unbind = "unbindResourceBundleProvider",
        referenceInterface = ResourceBundleProvider.class)
    private CopyOnWriteArrayList<ServiceReference<ResourceBundleProvider>> resourceBundleProviders = new CopyOnWriteArrayList<>();

    @Reference(cardinality = ReferenceCardinality.OPTIONAL_MULTIPLE,
        policy = ReferencePolicy.DYNAMIC,
        bind = "bindLocaleResolver",
        unbind = "unbindLocaleResolver",
        referenceInterface = LocaleResolver.class)
    private CopyOnWriteArrayList<ServiceReference<LocaleResolver>> localeResolvers = new CopyOnWriteArrayList<>();

    private BundleContext bundleContext

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
            final Optional<LocaleResolver> localeResolver = Optional.fromNullable(
                localeResolvers.sort(false, Collections.reverseOrder()).findResult { serviceReference ->
                    !filter.present || filter.get().match(serviceReference) ?
                        bundleContext.getService(serviceReference) :
                        null
                }
            )

            //resolve the Locale using the found LocaleResolver
            final Optional<Locale> locale = localeResolver.present ?
                localeResolver.get().resolve(componentNode.resource, componentNode.resource.resourceResolver) :
                Optional.absent()

            if (locale.present) {
                //find a ResourceBundle for the determined Locale
                final Optional<ResourceBundle> resourceBundle = Optional.fromNullable(
                    resourceBundleProviders.sort(false, Collections.reverseOrder()).findResult { serviceReference ->
                        bundleContext.getService(serviceReference).getResourceBundle(locale.get())
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

    protected void bindResourceBundleProvider(final ServiceReference<ResourceBundleProvider> serviceReference) {
        resourceBundleProviders.add(serviceReference);
    }

    protected void unbindResourceBundleProvider(final ServiceReference<ResourceBundleProvider> serviceReference) {
        resourceBundleProviders.remove(serviceReference);
    }

    protected void bindLocaleResolver(final ServiceReference<LocaleResolver> serviceReference) {
        localeResolvers.add(serviceReference);
    }

    protected void unbindLocaleResolver(final ServiceReference<LocaleResolver> serviceReference) {
        localeResolvers.remove(serviceReference);
    }

    /**
     * Activates this {@code Injector}.
     *
     * @param bundleContext The {@code BundleContext}.
     */
    @Activate
    @Modified
    protected final void activate(final BundleContext bundleContext) {
        this.bundleContext = bundleContext
    }

    /**
     * Deactivates this {@code Injector}.
     */
    @Deactivate
    protected final void deactivate() {
        bundleContext = null
    }
}
