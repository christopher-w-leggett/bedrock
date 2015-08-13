package com.citytechinc.aem.bedrock.models.impl

import com.citytechinc.aem.bedrock.models.specs.BedrockModelSpec
import org.apache.sling.api.SlingHttpServletRequest
import org.apache.sling.models.annotations.Model
import org.apache.sling.models.annotations.Optional

import javax.inject.Inject

class ValueMapFromRequestInjectorSpec extends BedrockModelSpec {

    @Model(adaptables = SlingHttpServletRequest)
    static class ValueMapInjectorModel {

        @Inject
        String name

        @Inject
        @Optional
        String title
    }

    def setupSpec() {
        pageBuilder.content {
            citytechinc {
                "jcr:content" {
                    component(name: "Bedrock")
                }
            }
        }

        slingContext.registerInjectActivateService(new ValueMapFromRequestInjector())
        slingContext.addModelsForPackage(this.class.package.name)
    }

    def "inject values for component"() {
        setup:
        def request = requestBuilder.build {
            path = "/content/citytechinc/jcr:content/component"
        }

        def model = request.adaptTo(ValueMapInjectorModel)

        expect:
        model.name == "Bedrock"

        and:
        !model.title
    }
}
