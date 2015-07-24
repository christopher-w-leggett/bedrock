package com.citytechinc.aem.bedrock.core.servlets

import com.citytechinc.aem.bedrock.api.request.ComponentServletRequest
import com.citytechinc.aem.bedrock.core.specs.BedrockSpec
import groovy.json.JsonBuilder

import javax.servlet.ServletException

class AbstractComponentServletSpec extends BedrockSpec {

    static final def MAP = [one: 1, two: 2]

    def setupSpec() {
        pageBuilder.content {
            citytechinc {
                "jcr:content" {
                    component("jcr:title": "Testing Component")
                }
            }
        }
    }

    def "process get"() {
        setup:
        def slingRequest = requestBuilder.build()
        def slingResponse = responseBuilder.build()

        def servlet = new AbstractComponentServlet() {
            @Override
            protected void processGet(ComponentServletRequest request) throws ServletException, IOException {
                new JsonBuilder(MAP).writeTo(request.slingResponse.writer)
            }
        }

        when:
        servlet.doGet(slingRequest, slingResponse)

        then:
        slingResponse.contentAsString == new JsonBuilder(MAP).toString()
    }
}
