## Component Framework

### Overview

Component JSPs should contain only the HTML markup and JSTL tags necessary to render the component and it's view permutations, rather than Java "scriptlet" blocks containing business logic.  To facilitate the separation of controller logic from presentation, Bedrock provides a custom JSP tag to associate a Java class (or "backing bean") to a component JSP.  The library also provides an abstract component class containing accessors and convenience methods for objects that are typically available in the JSP page context (e.g. current page, current node, Sling resource resolver, etc.).  Decorated instances for the current page and component node implement common use cases to reduce boilerplate code and encourage the use of established conventions.  This allows the developer to focus on project-specific concerns rather than reimplementing functionality that is frequently required for a typical AEM implementation but may not be provided by the AEM APIs.

### Usage

The component JSP needs to include the Bedrock `global.jsp` to define the tag namespace and ensure that required variables are set in the page context.

Component Java classes can be instantiated in one of two ways:

* Include the `<bedrock:component/>` tag in the JSP as shown below.
* Define a `className` attribute in the `.content.xml` descriptor file for the component and annotate the Java class with the `com.citytechinc.aem.bedrock.api.components.annotations.AutoInstantiate` annotation.

In the latter case, the `global.jsp` will instantiate the component class via the `<bedrock:defineObjects/>` tag included therein.

    <%@include file="/apps/bedrock/components/global.jsp"%>

    <bedrock:component className="com.projectname.components.content.Navigation" name="navigation"/>

    <h1>${navigation.title}</h1>

    <ul>
        <c:forEach items="${navigation.pages}" var="page">
            <li><a href="${page.href}">${page.title}</a></li>
        </c:forEach>
    </ul>

The backing Java class for the component should expose getters for the values that required to render the component's view.

    package com.projectname.components.content;

    import com.citytechinc.aem.bedrock.core.components.AbstractComponent;
    import com.citytechinc.aem.bedrock.api.content.page.PageDecorator;

    import java.util.List;

	@Model(adaptables=[Resource,SlingHttpServletRequest],
		defaultInjectionStrategy=DefaultInjectionStrategy.OPTIONAL)
    public final class Navigation extends AbstractComponent {

        public String getTitle() {
            return get("title", "");
        }

        public List<PageDecorator> getPages() {
            return getCurrentPage().getChildren(true);
        }
    }

### Abstract Component Java Class

The `AbstractComponent` class should be extended by all component backing classes.  In addition to the numerous getters for retrieving and transforming properties on the current component node, the base class also exposes `getComponent` methods to acquire instances of other components from either an absolute path or a `ComponentNode`.

    final PageDecorator homepage = request.getPageManager().getPage("/content/home");

    // get the component node for the Homepage Latest News component
    final Optional<ComponentNode> latestNewsComponentNode = homepage.getComponentNode("latestnews");

    if (latestNewsComponentNode.isPresent()) {
        // get an instance of the Latest News component for the given component node
        final LatestNews latestNews = getComponent(latestNewsComponentNode.get(), LatestNews.class).get();
    }

See the [Javadoc](http://code.citytechinc.com/bedrock/apidocs/com/citytechinc/aem/bedrock/core/components/AbstractComponent.html) for details of the available methods.

### Sightly Support

Components extending the `AbstractComponent` class can be used interchangeably with both Sightly templates and JSPs.

    <div data-sly-use.navigation="com.projectname.components.content.Navigation">
        <h1>${navigation.title}</h1>
    </div>

### Development Guidelines

* Component beans should be **read-only**; since components are generally accessed by an anonymous user in publish mode.  Repository write operations should be performed only in author mode (and replicated only when a page is activated by a content author).  Since component classes are executed in both author and publish modes, ideally one should consider alternative approaches to performing write operations in a component bean:
    * Delegate write operations to an OSGi service that is bound to an administrative session.
    * Refactor the component to perform dialog-based content modifications by attaching a listener to the appropriate [dialog event](http://dev.day.com/content/docs/en/cq/current/widgets-api/index.html?class=CQ.Dialog), e.g. 'beforesubmit'.
    * Register a [JCR event listener](http://www.day.com/maven/jsr170/javadocs/jcr-2.0/javax/jcr/observation/ObservationManager.html) to trigger event-based repository updates.
* Classes should remain stateless and contain no setters.  Since the lifecycle of a component is bound to a request, state should be maintained client-side using cookies, HTML5 web storage, or DOM data attributes.