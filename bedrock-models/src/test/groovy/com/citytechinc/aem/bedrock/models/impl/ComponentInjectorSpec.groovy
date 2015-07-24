package com.citytechinc.aem.bedrock.models.impl

import com.citytechinc.aem.bedrock.api.node.BasicNode
import com.citytechinc.aem.bedrock.api.node.ComponentNode
import com.citytechinc.aem.bedrock.api.page.PageDecorator
import com.citytechinc.aem.bedrock.api.page.PageManagerDecorator
import com.citytechinc.aem.bedrock.core.specs.BedrockSpec
import org.apache.sling.api.resource.Resource
import org.apache.sling.api.resource.ResourceResolver
import org.apache.sling.api.resource.ValueMap
import spock.lang.Shared
import spock.lang.Unroll

import javax.jcr.Node
import javax.jcr.Session

@Unroll
class ComponentInjectorSpec extends BedrockSpec {

    @Shared
    ComponentInjector injector = new ComponentInjector()

    def setupSpec() {
        pageBuilder.content {
            citytechinc {
                "jcr:content" {
                    component("jcr:title": "Testing Component")
                }
            }
        }
    }

    def "get value from resource for valid type returns non-null value"() {
        setup:
        def resource = getResource("/content/citytechinc/jcr:content/component")
        def value = injector.getValue(resource, null, type, null, null)

        expect:
        value

        where:
        type << [Resource, ResourceResolver, ValueMap, Node, Session, BasicNode, ComponentNode, PageDecorator, PageManagerDecorator]
    }

    def "get value from resource for invalid type returns null value"() {
        setup:
        def resource = getResource("/content/citytechinc/jcr:content/component")
        def value = injector.getValue(resource, null, String, null, null)

        expect:
        !value
    }
}
