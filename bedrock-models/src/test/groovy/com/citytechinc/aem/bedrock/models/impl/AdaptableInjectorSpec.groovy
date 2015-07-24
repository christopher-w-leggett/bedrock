package com.citytechinc.aem.bedrock.models.impl

import com.citytechinc.aem.bedrock.api.node.ComponentNode
import com.citytechinc.aem.bedrock.api.page.PageManagerDecorator
import com.citytechinc.aem.bedrock.core.specs.BedrockSpec
import spock.lang.Shared

class AdaptableInjectorSpec extends BedrockSpec {

    @Shared
    AdaptableInjector injector = new AdaptableInjector()

    def "get value returns null when resource or request is null"() {
        expect:
        !injector.getValue(null, null, PageManagerDecorator, null, null)
    }

    def "get value returns null for invalid adapter type"() {
        setup:
        def request = requestBuilder.build()

        expect:
        !injector.getValue(request, null, ComponentNode, null, null)
    }

    def "get value returns non-null for valid adapter type"() {
        setup:
        def request = requestBuilder.build()

        expect:
        injector.getValue(request, null, PageManagerDecorator, null, null)
    }
}
