package com.citytechinc.aem.bedrock.api.page;

import com.citytechinc.aem.bedrock.api.Accessible;
import com.citytechinc.aem.bedrock.api.ImageSource;
import com.citytechinc.aem.bedrock.api.Inheritable;
import com.citytechinc.aem.bedrock.api.Linkable;
import com.citytechinc.aem.bedrock.api.Traversable;
import com.citytechinc.aem.bedrock.api.link.ImageLink;
import com.citytechinc.aem.bedrock.api.link.Link;
import com.citytechinc.aem.bedrock.api.link.NavigationLink;
import com.citytechinc.aem.bedrock.api.link.builders.LinkBuilder;
import com.citytechinc.aem.bedrock.api.node.ComponentNode;
import com.citytechinc.aem.bedrock.api.page.enums.TitleType;
import com.day.cq.wcm.api.Page;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;

import java.util.List;

/**
 * Decorates the CQ <code>Page</code> interface with additional convenience methods for traversing the content hierarchy
 * and getters for Bedrock classes.
 */
public interface PageDecorator extends Page, Accessible, Inheritable, Linkable, ImageSource, Traversable<PageDecorator> {

    /**
     * Get the child pages of the current page.
     *
     * @return all child pages of current page or empty list if none exist
     */
    List<PageDecorator> getChildren();

    /**
     * Get the child pages of the current page, excluding children that are not "displayable" (i.e. hidden in nav).
     *
     * @param displayableOnly if true, only pages that are not hidden in navigation will be returned
     * @return child pages of current page or empty list if none exist
     */
    List<PageDecorator> getChildren(boolean displayableOnly);

    /**
     * Get the child pages of the current page filtered using the given predicate.
     *
     * @param predicate predicate to filter pages on
     * @return filtered list of child pages or empty list if none exist
     */
    List<PageDecorator> getChildren(Predicate<PageDecorator> predicate);

    /**
     * Get the component node for the "jcr:content" node for this page.  If the page does not have a content node, an
     * "absent" Optional is returned.
     *
     * @return optional component node for page content
     */
    Optional<ComponentNode> getComponentNode();

    /**
     * Get the component node for the node at the given path relative to the "jcr:content" node for this page.  If the
     * node does not exist, an "absent" Optional is returned.
     *
     * @return optional component node for resource relative to page content
     */
    Optional<ComponentNode> getComponentNode(String relativePath);

    /**
     * Get a link for this page with an attached image source.
     *
     * @param imageSource image source to set on the returned image link
     * @return image link with the provided image source
     */
    ImageLink getImageLink(String imageSource);

    /**
     * Get a link with a specified title type for this item.
     *
     * @param titleType type of title to set on link
     * @return link
     */
    Link getLink(TitleType titleType);

    /**
     * Get a link with a specified title type for this item.
     *
     * @param titleType type of title to set on link
     * @param mapped if true, the <code>Link</code> path will be routed through the resource resolver to determine the
     * mapped path (e.g. without leading "/content").
     * @return link
     */
    Link getLink(TitleType titleType, boolean mapped);

    /**
     * Get a link builder for the current resource path.
     *
     * @param titleType type of title to set on builder
     * @return builder instance for this item
     */
    LinkBuilder getLinkBuilder(TitleType titleType);

    /**
     * Get a link builder for the current resource path.
     *
     * @param titleType type of title to set on builder
     * @param mapped if true, the <code>Link</code> path will be routed through the resource resolver to determine the
     * mapped path (e.g. without leading "/content").
     * @return builder instance for this item
     */
    LinkBuilder getLinkBuilder(TitleType titleType, boolean mapped);

    /**
     * Get a navigation link for this page.  The returned link will use the navigation title as the link title,
     * defaulting to the JCR title if it does not exist.
     *
     * @return navigation link
     */
    NavigationLink getNavigationLink();

    /**
     * Get a navigation link for this page containing an active state.  The returned link will use the navigation title
     * as the link title, defaulting to the JCR title if it does not exist.
     *
     * @param isActive active state to be set on returned link
     * @return navigation link
     */
    NavigationLink getNavigationLink(boolean isActive);

    /**
     * Get a navigation link for this page containing an active state.  The returned link will use the navigation title
     * as the link title, defaulting to the JCR title if it does not exist.
     *
     * @param isActive active state to be set on returned link
     * @param mapped if true, the <code>NavigationLink</code> path will be routed through the resource resolver to
     * determine the mapped path (e.g. without leading "/content").
     * @return navigation link
     */
    NavigationLink getNavigationLink(boolean isActive, boolean mapped);

    /**
     * Get the template path for this page.  This method is preferred over getTemplate().getPath(), which is dependent
     * on access to /apps and will therefore fail in publish mode.
     *
     * @return value of cq:template property or empty string if none exists
     */
    String getTemplatePath();

    /**
     * Get the title with the given type for this page.  If the title value is empty or non-existent, an absent
     * <code>Optional</code> is returned.
     *
     * @param titleType type of title to retrieve
     * @return title value or absent <code>Optional</code>
     */
    Optional<String> getTitle(TitleType titleType);

    // overrides for returning decorated types

    /**
     * Returns the absolute parent page. If no page exists at that level, <code>null</code> is returned.
     * <p>
     * Example (this path == /content/geometrixx/en/products)
     * <pre>
     * | level | returned                        |
     * |     0 | /content                        |
     * |     1 | /content/geometrixx             |
     * |     2 | /content/geometrixx/en          |
     * |     3 | /content/geometrixx/en/products |
     * |     4 | null                            |
     * </pre>
     *
     * @param level hierarchy level of the parent page to retrieve
     * @return the respective parent page or <code>null</code>
     */
    @Override
    PageDecorator getAbsoluteParent(int level);

    /**
     * Convenience method that returns the manager of this page.
     *
     * @return the page manager
     */
    @Override
    PageManagerDecorator getPageManager();

    /**
     * Returns the parent page if it's resource adapts to page.
     *
     * @return the parent page or <code>null</code>
     */
    @Override
    PageDecorator getParent();

    /**
     * Returns the relative parent page. If no page exists at that level, <code>null</code> is returned.
     * <p>
     * Example (this path == /content/geometrixx/en/products)
     * <pre>
     * | level | returned                        |
     * |     0 | /content/geometrixx/en/products |
     * |     1 | /content/geometrixx/en          |
     * |     2 | /content/geometrixx             |
     * |     3 | /content                        |
     * |     4 | null                            |
     * </pre>
     *
     * @param level hierarchy level of the parent page to retrieve
     * @return the respective parent page or <code>null</code>
     */
    @Override
    PageDecorator getParent(int level);
}
