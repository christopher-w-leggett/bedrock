## Component Framework

### Overview

Component JSPs should contain only the HTML markup and JSTL tags necessary to render the component and it's view permutations, rather than Java "scriptlet" blocks containing business logic.  To facilitate the separation of controller logic from presentation, the CQ Library provides a custom JSP tag to associate a Java class (or "backing bean") to a component JSP.  The library also provides an abstract template class containing accessors and convenience methods for objects that are typically available in the JSP page context (e.g. current page, current node, Sling resource resolver, etc.).  Decorator instances for the current page and component node implement common use cases to reduce boilerplate code and encourage the use of established conventions.  This allows the developer to focus on project-specific concerns rather than reimplementing functionality that is frequently required for a typical CQ implementation but may not be provided by the CQ APIs.

### Usage

The component JSP needs to include the CQ Library `global.jsp` to define the tag namespace and ensure that required variables are set in the page context.

    <%@include file="/apps/cq-library/components/global.jsp"%>

    <ct:component className="com.companyname.cq.components.content.Navigation" name="navigation"/>

    <h1>${navigation.title}</h1>

    <ul>
        <c:forEach items="${navigation.pages}" var="page">
            <li><a href="${page.href}">${page.title}</a></li>
        </c:forEach>
    </ul>

The backing Java class for the component should expose getters for the values that required to render the component's view.

    package com.companyname.cq.library.components.content;

    import com.citytechinc.cq.library.components.AbstractComponent;
    import com.citytechinc.cq.library.content.page.PageDecorator;
    import com.citytechinc.cq.library.content.request.ComponentRequest;

    import java.util.List;

    public final class Navigation extends AbstractComponent {

        public Navigation(final ComponentRequest request) {
            super(request);
        }

        public String getTitle() {
            return get("title", "");
        }

        public List<PageDecorator> getPages() {
            return currentPage.getChildren(true);
        }
    }

### Abstract Component Java Class

The `AbstractComponent` class should be extended by all component backing classes.  This base class enforces the creation of a single argument constructor that takes a `ComponentRequest` argument, which is required by the `<ct:component/>` JSP tag to instantiate the component class and provide the required page context attributes.  The additional `ComponentNode` constructor allows for component classes to instantiate other component classes directly.

    final PageDecorator homepage = request.getPageManager().getPage("/content/home");

    // get the component node for the Homepage Latest News component
    final ComponentNode latestNewsComponentNode = homepage.getComponentNode("latestnews");

    // get an instance of the Latest News component for the given component node
    final LatestNews latestNews = new LatestNews(latestNewsComponentNode);

See the [Javadoc](http://code.citytechinc.com/cq-library/apidocs/com/citytechinc/cq/library/components/AbstractComponent.html) for details of the available methods.