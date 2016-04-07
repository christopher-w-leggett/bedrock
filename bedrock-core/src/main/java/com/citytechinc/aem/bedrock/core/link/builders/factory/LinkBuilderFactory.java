package com.citytechinc.aem.bedrock.core.link.builders.factory;

import com.citytechinc.aem.bedrock.api.link.Link;
import com.citytechinc.aem.bedrock.api.link.builders.LinkBuilder;
import com.citytechinc.aem.bedrock.api.page.enums.TitleType;
import com.citytechinc.aem.bedrock.core.link.builders.impl.DefaultLinkBuilder;
import com.day.cq.wcm.api.Page;
import org.apache.sling.api.resource.Resource;

import static com.citytechinc.aem.bedrock.core.constants.PropertyConstants.REDIRECT_TARGET;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Factory for acquiring <code>LinkBuilder</code> instances.
 */
public final class LinkBuilderFactory {

    /**
     * Get a builder instance for an existing <code>Link</code>.  The path, extension, title, and target are copied from
     * the link argument.
     *
     * @param link existing link
     * @return builder
     */
    public static LinkBuilder forLink(final Link link) {
        checkNotNull(link);

        return new DefaultLinkBuilder(link.getPath()).setExtension(link.getExtension()).setTitle(link.getTitle())
            .setTarget(link.getTarget());
    }

    /**
     * Get a builder instance for a page.  If the page contains a redirect, the builder will contain the redirect target
     * rather than the page path.
     *
     * @param page page
     * @return builder containing the path of the given page
     */
    public static LinkBuilder forPage(final Page page) {
        return forPage(page, false, TitleType.TITLE);
    }

    /**
     * Get a builder instance for a page using the specified title type on the returned builder.
     *
     * @param page page
     * @param titleType type of page title to set on the builder
     * @return builder containing the path and title of the given page
     */
    public static LinkBuilder forPage(final Page page, final TitleType titleType) {
        return forPage(page, false, titleType);
    }

    /**
     * Get a builder instance for a page.  If the page contains a redirect, the builder will contain the redirect target
     * rather than the page path.
     *
     * @param page page
     * @param mapped if true, link path will be mapped through resource resolver
     * @return builder containing the mapped path of the given page
     */
    public static LinkBuilder forPage(final Page page, final boolean mapped) {
        return forPage(page, mapped, TitleType.TITLE);
    }

    /**
     * Get a builder instance for a page using the specified title type on the returned builder.
     *
     * @param page page
     * @param mapped if true, link path will be mapped through resource resolver
     * @param titleType type of page title to set on the builder
     * @return builder containing the path and title of the given page
     */
    public static LinkBuilder forPage(final Page page, final boolean mapped, final TitleType titleType) {
        final String title = checkNotNull(page).getProperties().get(titleType.getPropertyName(), page.getTitle());

        return new DefaultLinkBuilder(getPagePath(page, mapped)).setTitle(title);
    }

    /**
     * Get a builder instance for a path.
     *
     * @param path content or external path
     * @return builder containing the given path
     */
    public static LinkBuilder forPath(final String path) {
        return new DefaultLinkBuilder(checkNotNull(path));
    }

    /**
     * Get a builder instance for a resource.
     *
     * @param resource resource
     * @return builder containing the path of the given resource
     */
    public static LinkBuilder forResource(final Resource resource) {
        return forResource(resource, false);
    }

    /**
     * Get a builder instance for a resource using the mapped path on the returned builder.
     *
     * @param resource resource
     * @param mapped if true, link path will be mapped through resource resolver
     * @return builder containing the mapped path of the given resource
     */
    public static LinkBuilder forResource(final Resource resource, final boolean mapped) {
        checkNotNull(resource);

        final String path = resource.getPath();
        final String mappedPath = mapped ? resource.getResourceResolver().map(path) : path;

        return new DefaultLinkBuilder(mappedPath);
    }

    private static String getPagePath(final Page page, final boolean mapped) {
        final String redirect = page.getProperties().get(REDIRECT_TARGET, "");
        final String path = redirect.isEmpty() ? page.getPath() : redirect;

        final String result;

        if (mapped) {
            result = page.adaptTo(Resource.class).getResourceResolver().map(path);
        } else {
            result = path;
        }

        return result;
    }

    private LinkBuilderFactory() {

    }
}
