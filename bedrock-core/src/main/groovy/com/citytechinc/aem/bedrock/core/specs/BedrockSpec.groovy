package com.citytechinc.aem.bedrock.core.specs

import com.citytechinc.aem.bedrock.api.node.ComponentNode
import com.citytechinc.aem.bedrock.api.page.PageDecorator
import com.citytechinc.aem.bedrock.api.page.PageManagerDecorator
import com.citytechinc.aem.bedrock.core.adapter.BedrockAdapterFactory
import com.citytechinc.aem.prosper.mixins.JspTagMixin
import com.citytechinc.aem.prosper.specs.ProsperSpec
import com.citytechinc.aem.prosper.tag.JspTagProxy
import com.day.cq.wcm.api.PageManager
import org.apache.sling.api.adapter.AdapterFactory
import spock.lang.Shared

import javax.servlet.jsp.tagext.TagSupport

import static com.day.cq.wcm.tags.DefineObjectsTag.DEFAULT_CURRENT_PAGE_NAME
import static org.apache.sling.scripting.jsp.taglib.DefineObjectsTag.DEFAULT_BINDINGS_NAME
import static org.apache.sling.scripting.jsp.taglib.DefineObjectsTag.DEFAULT_RESOURCE_NAME

/**
 * Spock specification for testing Bedrock-based components and services.
 */
abstract class BedrockSpec extends ProsperSpec {

    @Shared
    PageManagerDecorator pageManagerDecorator

    @Shared
    JspTagMixin jspTag

    @Override
    Collection<AdapterFactory> addAdapterFactories() {
        [new BedrockAdapterFactory()]
    }

    def setupSpec() {
        pageManagerDecorator = resourceResolver.adaptTo(PageManagerDecorator)
    }

    ComponentNode getComponentNode(String path) {
        resourceResolver.getResource(path).adaptTo(ComponentNode)
    }

    @Override
    PageDecorator getPage(String path) {
        pageManagerDecorator.getPage(path)
    }

    @Override
    PageManagerDecorator getPageManager() {
        pageManagerDecorator
    }

    // delegate methods

    public <T extends TagSupport> JspTagProxy init(Class<T> tagClass, String path) {
        init(tagClass, path, [:])
    }

    public <T extends TagSupport> JspTagProxy init(Class<T> tagClass, String path,
        Map<String, Object> additionalPageContextAttributes) {
        def resource = resourceResolver.getResource(path)
        def currentPage = resourceResolver.adaptTo(PageManager).getContainingPage(resource)

        additionalPageContextAttributes[DEFAULT_RESOURCE_NAME] = resource
        additionalPageContextAttributes[DEFAULT_CURRENT_PAGE_NAME] = currentPage
        additionalPageContextAttributes[DEFAULT_BINDINGS_NAME] = bindingsBuilder.build {
            setPath(path)
        }

        jspTag.init(tagClass, additionalPageContextAttributes)
    }
}
