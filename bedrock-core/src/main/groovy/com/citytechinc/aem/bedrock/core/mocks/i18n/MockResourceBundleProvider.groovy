package com.citytechinc.aem.bedrock.core.mocks.i18n

import org.apache.sling.api.resource.ResourceResolver
import org.apache.sling.i18n.ResourceBundleProvider

/**
 * A mock <code>ResourceBundleProvider</code> implementation that will return <code>MockResourceBundle</code>
 * implementations configured to work with the provided translator dictionary located under the resource bundle path.
 */
public final class MockResourceBundleProvider implements ResourceBundleProvider {
    private final String resourceBundlePath
    private final ResourceResolver resourceResolver

    /**
     * Constructs a new <code>MockResourceBundleProvider</code>.
     * @param resourceBundlePath The path to locate the translations.
     * @param resourceResolver The resource resolver used to resolve resources.
     */
    public MockResourceBundleProvider(final String resourceBundlePath, final ResourceResolver resourceResolver) {
        this.resourceBundlePath = resourceBundlePath
        this.resourceResolver = resourceResolver
    }

    /**
     * Returns the default locale.
     * @return Locale.ENGLISH.
     */
    @Override
    public Locale getDefaultLocale() {
        Locale.ENGLISH
    }

    /**
     * Gets a <code>MockResourceBundle</code> for the provided <code>Locale</code>
     * @param locale The <code>Locale</code> to get the resource bundle for.
     * @return The <code>MockResourceBudle</code>
     */
    @Override
    public ResourceBundle getResourceBundle(final Locale locale) {
        new MockResourceBundle(locale ?: getDefaultLocale(), resourceBundlePath, resourceResolver)
    }

    /**
     * Same as <code>getResourceBundle(final Locale locale)</code> and ignores baseName parameter.
     * @param baseName Ignored.
     * @param locale The <code>Locale</code> to get the resource bundle for.
     * @return The <code>MockResourceBundle</code>
     */
    @Override
    public ResourceBundle getResourceBundle(final String baseName, final Locale locale) {
        new MockResourceBundle(locale ?: getDefaultLocale(), resourceBundlePath, resourceResolver)
    }
}
