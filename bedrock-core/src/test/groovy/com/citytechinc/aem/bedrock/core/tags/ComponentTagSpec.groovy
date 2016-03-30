package com.citytechinc.aem.bedrock.core.tags

import com.citytechinc.aem.bedrock.core.specs.BedrockSpec
import com.citytechinc.aem.prosper.traits.JspTagTrait
import spock.lang.Unroll

import javax.servlet.jsp.PageContext

@Unroll
class ComponentTagSpec extends BedrockSpec implements JspTagTrait {

    def setupSpec() {
        pageBuilder.content {
            home {
                "jcr:content" {
                    component()
                }
            }
        }

        slingContext.addModelsForPackage(this.class.package.name)
    }

    def "get component instance"() {
        setup:
        def proxy = init(ComponentTag, "/content/home/jcr:content/component")
        def tag = proxy.tag as ComponentTag

        tag.with {
            className = BedrockComponent.class.name
            name = "bedrockComponent"
        }

        when:
        tag.doEndTag()

        then:
        proxy.pageContext.getAttribute("bedrockComponent") instanceof BedrockComponent
    }

    def "get component instance with scope"() {
        setup:
        def proxy = init(ComponentTag, "/content/home/jcr:content/component")
        def tag = proxy.tag as ComponentTag

        tag.with {
            className = BedrockComponent.class.name
            name = "bedrockComponent"
            scope = testScope
        }

        when:
        tag.doEndTag()

        then:
        proxy.pageContext.getAttribute("bedrockComponent", scopeValue as Integer) instanceof BedrockComponent

        where:
        testScope     | scopeValue
        "page"        | PageContext.PAGE_SCOPE
        "request"     | PageContext.REQUEST_SCOPE
        "session"     | PageContext.SESSION_SCOPE
        "application" | PageContext.APPLICATION_SCOPE
    }
}
