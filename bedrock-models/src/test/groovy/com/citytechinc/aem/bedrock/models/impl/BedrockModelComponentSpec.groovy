package com.citytechinc.aem.bedrock.models.impl

import com.citytechinc.aem.bedrock.models.specs.BedrockModelSpec
import spock.lang.Unroll

@Unroll
class BedrockModelComponentSpec extends BedrockModelSpec {

    def setupSpec() {
        pageBuilder.content {
            citytechinc {
                "jcr:content" {
                    component("jcr:title": "Testing Component")
                }
            }
        }
    }

    def "get title from component"() {
        setup:
        def component = getResource("/content/citytechinc/jcr:content/component").adaptTo(BedrockModelComponent)

        expect:
        component.title == "Testing Component"
    }
}
