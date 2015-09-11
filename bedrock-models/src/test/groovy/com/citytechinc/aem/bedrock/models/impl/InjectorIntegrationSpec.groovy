package com.citytechinc.aem.bedrock.models.impl

import com.citytechinc.aem.bedrock.api.page.PageManagerDecorator
import com.citytechinc.aem.bedrock.models.specs.BedrockModelSpec
import com.day.cq.tagging.TagManager
import org.apache.sling.api.SlingHttpServletRequest
import org.apache.sling.models.annotations.Model

import javax.inject.Inject

class InjectorIntegrationSpec extends BedrockModelSpec {

    @Model(adaptables = SlingHttpServletRequest)
    static class InjectorIntegrationComponent {

        @Inject
        PageManagerDecorator pageManager

        @Inject
        TagManager tagManager
    }

    def "injected values from multiple injectors are correct types"() {
        setup:
        def request = requestBuilder.build()
        def model = request.adaptTo(InjectorIntegrationComponent)

        expect:
        model.pageManager instanceof PageManagerDecorator

        and:
        model.tagManager instanceof TagManager
    }
}
