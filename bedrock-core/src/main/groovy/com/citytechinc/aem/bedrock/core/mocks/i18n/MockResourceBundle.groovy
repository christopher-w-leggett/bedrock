package com.citytechinc.aem.bedrock.core.mocks.i18n

import org.apache.sling.api.resource.Resource
import org.apache.sling.api.resource.ResourceResolver
import org.apache.sling.i18n.impl.ResourceBundleEnumeration

/**
 * A mock <code>ResourceBundle</code> implementation.
 */
public final class MockResourceBundle extends ResourceBundle {
    private static final Locale DEFAULT_LOCALE = Locale.ENGLISH
    private static final String PROP_KEY = "sling:key";
    private static final String PROP_MESSAGE = "sling:message";

    private final Locale locale
    private final Map<String, Object> resources

    /**
     * Constructs a new <code>MockResourceBundle</code> for the provided <code>Locale</code> that uses the provided
     * <code>ResourceResolver</code> when resolving resources.
     * @param locale The <code>Locale</code> this resource bundle is based on.
     * @param resourceResolver The resource resolver.
     */
    public MockResourceBundle(final Locale locale, final String resourceBundlePath,
                              final ResourceResolver resourceResolver) {
        this.locale = locale
        resources = getResources(locale, resourceBundlePath, resourceResolver)
    }

    @Override
    public Locale getLocale() {
        locale
    }

    @Override
    protected Set<String> handleKeySet() {
        resources.keySet()
    }

    @Override
    public Enumeration<String> getKeys() {
        new ResourceBundleEnumeration(resources.keySet(), null)
    }

    @Override
    protected Object handleGetObject(final String key) {
        resources.get(key)
    }

    private static Map<String, Object> getResources(final Locale locale, final String resourceBundlePath,
                                                    final ResourceResolver resourceResolver) {
        final def resources = [:]

        final def languageRoot = getLanguageRoot(locale, resourceBundlePath, resourceResolver)
        if (languageRoot) {
            languageRoot.listChildren().each { resource ->
                resources.put(
                    resource.valueMap.get(PROP_KEY, resource.getName()),
                    resource.valueMap.get(PROP_MESSAGE, String.class)
                );
            }
        }

        resources
    }

    private static Resource getLanguageRoot(final Locale locale, final String resourceBundlePath,
                                            final ResourceResolver resourceResolver) {
        final def languageRoot

        final def currentLanguageRoot = resourceResolver.getResource(resourceBundlePath + '/' + locale.toString())
        if (currentLanguageRoot != null) {
            languageRoot = currentLanguageRoot
        } else {
            if (locale.getVariant()) {
                languageRoot = getLanguageRoot(new Locale(locale.getLanguage(), locale.getCountry()),
                    resourceBundlePath, resourceResolver)
            } else if (locale.getCountry()) {
                languageRoot = getLanguageRoot(new Locale(locale.getLanguage()), resourceBundlePath, resourceResolver)
            } else if (!DEFAULT_LOCALE.getLanguage().equals(locale.getLanguage())) {
                languageRoot = getLanguageRoot(DEFAULT_LOCALE, resourceBundlePath, resourceResolver)
            } else {
                languageRoot = null
            }
        }

        languageRoot
    }
}
