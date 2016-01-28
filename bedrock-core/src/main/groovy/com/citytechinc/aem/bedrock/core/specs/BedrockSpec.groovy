package com.citytechinc.aem.bedrock.core.specs

import com.citytechinc.aem.bedrock.api.node.ComponentNode
import com.citytechinc.aem.bedrock.api.page.PageDecorator
import com.citytechinc.aem.bedrock.api.page.PageManagerDecorator
import com.citytechinc.aem.bedrock.core.adapter.BedrockAdapterFactory
import com.citytechinc.aem.prosper.specs.ProsperSpec
import org.apache.sling.api.adapter.AdapterFactory
import spock.lang.Shared

/**
 * Spock specification for testing Bedrock-based components and services.
 */
abstract class BedrockSpec extends ProsperSpec {

    @Shared
    PageManagerDecorator pageManagerDecorator

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
}
