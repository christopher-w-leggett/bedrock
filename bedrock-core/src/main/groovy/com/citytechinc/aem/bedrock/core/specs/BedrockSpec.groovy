package com.citytechinc.aem.bedrock.core.specs

import com.citytechinc.aem.bedrock.api.node.ComponentNode
import com.citytechinc.aem.bedrock.api.page.PageDecorator
import com.citytechinc.aem.bedrock.api.page.PageManagerDecorator
import com.citytechinc.aem.bedrock.core.adapter.BedrockAdapterFactory
import com.citytechinc.aem.prosper.specs.ProsperSpec

/**
 * Spock specification for testing Bedrock-based components and services.
 */
abstract class BedrockSpec extends ProsperSpec {

    def setupSpec() {
        slingContext.registerAdapterFactory(new BedrockAdapterFactory(), BedrockAdapterFactory.ADAPTABLE_CLASSES,
            BedrockAdapterFactory.ADAPTER_CLASSES)
    }

    ComponentNode getComponentNode(String path) {
        resourceResolver.getResource(path).adaptTo(ComponentNode)
    }

    @Override
    PageDecorator getPage(String path) {
        pageManager.getPage(path)
    }

    @Override
    PageManagerDecorator getPageManager() {
        resourceResolver.adaptTo(PageManagerDecorator)
    }
}
