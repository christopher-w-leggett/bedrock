package com.citytechinc.aem.bedrock.core.tags

import com.citytechinc.aem.bedrock.core.components.TestComponent
import com.citytechinc.aem.bedrock.core.specs.BedrockSpec
import com.citytechinc.aem.bedrock.core.traits.BedrockJspTagTrait
import spock.lang.Unroll

import javax.servlet.jsp.PageContext

@Unroll
class ComponentTagSpec extends BedrockSpec implements BedrockJspTagTrait {

    def setupSpec() {
        pageBuilder.content {
            home {
                "jcr:content" {
                    component()
                }
            }
        }
    }

    def "get component instance"() {
        setup:
        def proxy = init(ComponentTag, "/content/home/jcr:content/component")
        def tag = proxy.tag as ComponentTag

        tag.with {
            className = TestComponent.class.name
            name = "testComponent"
        }

        when:
        tag.doEndTag()

        then:
        proxy.pageContext.getAttribute("testComponent") instanceof TestComponent
    }

    def "get component instance with scope"() {
        setup:
        def proxy = init(ComponentTag, "/content/home/jcr:content/component")
        def tag = proxy.tag as ComponentTag

        tag.with {
            className = TestComponent.class.name
            name = "testComponent"
            scope = testScope
        }

        when:
        tag.doEndTag()

        then:
        proxy.pageContext.getAttribute("testComponent", scopeValue as Integer) instanceof TestComponent

        where:
        testScope     | scopeValue
        "page"        | PageContext.PAGE_SCOPE
        "request"     | PageContext.REQUEST_SCOPE
        "session"     | PageContext.SESSION_SCOPE
        "application" | PageContext.APPLICATION_SCOPE
    }
}
