package com.citytechinc.aem.bedrock.models.impl

import com.citytechinc.aem.bedrock.api.node.ComponentNode
import com.citytechinc.aem.bedrock.api.page.PageManagerDecorator
import com.citytechinc.aem.bedrock.core.specs.BedrockSpec
import org.apache.sling.api.SlingHttpServletRequest
import org.apache.sling.models.annotations.DefaultInjectionStrategy
import org.apache.sling.models.annotations.Model

import javax.inject.Inject

import static org.osgi.framework.Constants.SERVICE_RANKING

class AdaptableInjectorSpec extends BedrockSpec {

    @Model(adaptables = SlingHttpServletRequest, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
    static class AdaptableModel {

        @Inject
        PageManagerDecorator pageManager

        @Inject
        ComponentNode componentNode
    }

    def setupSpec() {
        slingContext.registerInjectActivateService(new AdaptableInjector(), [(SERVICE_RANKING): Integer.MIN_VALUE])
        slingContext.addModelsForPackage(this.class.package.name)
    }

    def "get value returns null for invalid adapter type"() {
        setup:
        def request = requestBuilder.build()
        def model = request.adaptTo(AdaptableModel)

        expect:
        !model.componentNode
    }

    def "get value returns non-null for valid adapter type"() {
        setup:
        def request = requestBuilder.build()
        def model = request.adaptTo(AdaptableModel)

        expect:
        model.pageManager
    }
}
