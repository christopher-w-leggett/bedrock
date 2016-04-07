package com.citytechinc.aem.bedrock.core.tags

import com.citytechinc.aem.bedrock.core.specs.BedrockSpec
import spock.lang.Unroll

@Unroll
class PropertyTagSpec extends BedrockSpec implements JspMetaTagTrait {

    def setupSpec() {
        pageBuilder.content {
            citytechinc {
                "jcr:content" {
                    component("jcr:title": "Component")
                }
                company() {
                    "jcr:content" {
                        component()
                    }
                }
            }
        }
    }

    def "test property value"() {
        setup:
        def proxy = init(PropertyTag, path)
        def tag = proxy.tag as PropertyTag

        tag.with {
            propertyName = "jcr:title"
            inherit = testInherit
            defaultValue = testDefaultValue
        }

        when:
        tag.doEndTag()

        then:
        proxy.output == propertyValue

        where:
        path                                                 | testInherit | testDefaultValue | propertyValue
        "/content/citytechinc/jcr:content/component"         | false       | ""               | "Component"
        "/content/citytechinc/jcr:content/component"         | false       | "Default"        | "Component"
        "/content/citytechinc/jcr:content/component"         | true        | ""               | "Component"
        "/content/citytechinc/company/jcr:content/component" | false       | ""               | ""
        "/content/citytechinc/company/jcr:content/component" | false       | "Default"        | "Default"
        "/content/citytechinc/company/jcr:content/component" | true        | ""               | "Component"
        "/content/citytechinc/company/jcr:content/component" | true        | "Default"        | "Component"
    }
}
