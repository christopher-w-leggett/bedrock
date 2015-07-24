package com.citytechinc.aem.bedrock.core.components

import com.citytechinc.aem.bedrock.core.specs.BedrockSpec
import com.day.cq.wcm.api.designer.Designer
import spock.lang.Ignore
import spock.lang.Unroll

@Ignore
@Unroll
class ComponentSpec extends BedrockSpec {

    static class UselessComponent extends AbstractComponent {

    }

    static class AnotherUselessComponent extends AbstractComponent {

    }

    @Override
    Map<Class, Closure> addResourceResolverAdapters() {
        [(Designer): {
            [getDesign: { null }] as Designer
        }]
    }

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
        def component = sightly.init(TestComponent) {
            path = "/content/citytechinc/jcr:content/component"
        }

        expect:
        component.title == "Testing Component"
    }
}
