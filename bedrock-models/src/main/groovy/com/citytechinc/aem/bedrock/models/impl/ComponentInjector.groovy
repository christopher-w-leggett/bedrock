package com.citytechinc.aem.bedrock.models.impl

import com.citytechinc.aem.bedrock.api.node.BasicNode
import com.citytechinc.aem.bedrock.api.node.ComponentNode
import com.citytechinc.aem.bedrock.api.page.PageDecorator
import com.citytechinc.aem.bedrock.api.page.PageManagerDecorator
import com.citytechinc.aem.bedrock.models.utils.ModelUtils
import com.day.cq.wcm.api.WCMMode
import com.day.cq.wcm.api.components.ComponentContext
import com.day.cq.wcm.api.components.EditContext
import com.day.cq.wcm.api.designer.Design
import com.day.cq.wcm.api.designer.Designer
import com.day.cq.wcm.api.designer.Style
import groovy.util.logging.Slf4j
import org.apache.felix.scr.annotations.Component
import org.apache.felix.scr.annotations.Property
import org.apache.felix.scr.annotations.Service
import org.apache.sling.api.SlingHttpServletRequest
import org.apache.sling.api.resource.Resource
import org.apache.sling.api.resource.ResourceResolver
import org.apache.sling.api.resource.ValueMap
import org.apache.sling.api.scripting.SlingBindings
import org.apache.sling.models.spi.DisposalCallbackRegistry
import org.apache.sling.models.spi.Injector
import org.osgi.framework.Constants

import javax.jcr.Node
import javax.jcr.Session
import java.lang.reflect.AnnotatedElement
import java.lang.reflect.Type

import static com.adobe.cq.sightly.WCMBindings.COMPONENT
import static com.adobe.cq.sightly.WCMBindings.COMPONENT_CONTEXT
import static com.adobe.cq.sightly.WCMBindings.CURRENT_DESIGN
import static com.adobe.cq.sightly.WCMBindings.CURRENT_STYLE
import static com.adobe.cq.sightly.WCMBindings.DESIGNER
import static com.adobe.cq.sightly.WCMBindings.EDIT_CONTEXT

/**
 * Injector for objects derived from the current component context.
 */
@Component
@Service(Injector)
@Property(name = Constants.SERVICE_RANKING, intValue = Integer.MAX_VALUE)
@Slf4j("LOG")
class ComponentInjector implements Injector {

    private static final def REQUEST_INJECTABLES = [SlingHttpServletRequest, WCMMode,
        com.day.cq.wcm.api.components.Component, ComponentContext, EditContext, Designer]

    private static final def RESOURCE_INJECTABLES = [Resource, ResourceResolver, ValueMap, Node, Session, BasicNode,
         ComponentNode, PageDecorator, PageManagerDecorator]

    @Override
    String getName() {
        "component"
    }

    @Override
    Object getValue(Object adaptable, String name, Type type, AnnotatedElement element,
        DisposalCallbackRegistry registry) {
        def value = null

        if (type instanceof Class) {
            def clazz = type as Class

            if (REQUEST_INJECTABLES.contains(clazz)) {
                value = getValueForRequest(clazz, adaptable)
            } else if (RESOURCE_INJECTABLES.contains(clazz)) {
                value = getValueForResource(clazz, adaptable)
            }
        }

        value
    }

    private static def getValueForRequest(Class clazz, Object adaptable) {
        def request = ModelUtils.getRequest(adaptable)

        def value = null

        if (request) {
            def bindings = request.getAttribute(SlingBindings.class.name) as SlingBindings

            if (clazz == SlingHttpServletRequest) {
                value = request
            } else if (clazz == WCMMode) {
                value = WCMMode.fromRequest(request)
            } else if (clazz == com.day.cq.wcm.api.components.Component) {
                value = bindings[COMPONENT] as com.day.cq.wcm.api.components.Component
            } else if (clazz == ComponentContext) {
                value = bindings[COMPONENT_CONTEXT] as ComponentContext
            } else if (clazz == EditContext) {
                value = bindings[EDIT_CONTEXT] as EditContext
            } else if (clazz == Designer) {
                value = bindings[DESIGNER] as Designer
            } else if (clazz == Design) {
                value = bindings[CURRENT_DESIGN] as Design
            } else if (clazz == Style) {
                value = bindings[CURRENT_STYLE] as Style
            } else {
                LOG.debug("class = {} is not supported by this injector", clazz.name)
            }

            LOG.debug("injecting class = {} with instance = {}", clazz.name, value)
        }

        value
    }

    private static def getValueForResource(Class clazz, Object adaptable) {
        def resource = ModelUtils.getResource(adaptable)

        def value = null

        if (resource) {
            if (clazz == Resource) {
                value = resource
            } else if (clazz == ResourceResolver) {
                value = resource.resourceResolver
            } else if (clazz == ValueMap) {
                value = resource.valueMap
            } else if (clazz == Node) {
                value = resource.adaptTo(Node)
            } else if (clazz == Session) {
                value = resource.resourceResolver.adaptTo(Session)
            } else if (clazz == BasicNode) {
                value = resource.adaptTo(BasicNode)
            } else if (clazz == ComponentNode) {
                value = resource.adaptTo(ComponentNode)
            } else if (clazz == PageDecorator) {
                value = resource.resourceResolver.adaptTo(PageManagerDecorator).getContainingPage(resource)
            } else if (clazz == PageManagerDecorator) {
                value = resource.resourceResolver.adaptTo(PageManagerDecorator)
            } else {
                LOG.debug("class = {} is not supported by this injector", clazz.name)
            }

            LOG.debug("injecting class = {} with instance = {}", clazz.name, value)
        }

        value
    }
}
