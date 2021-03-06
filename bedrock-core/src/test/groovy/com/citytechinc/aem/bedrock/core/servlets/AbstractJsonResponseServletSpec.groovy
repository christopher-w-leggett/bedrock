package com.citytechinc.aem.bedrock.core.servlets

import com.citytechinc.aem.bedrock.core.specs.BedrockSpec
import groovy.json.JsonBuilder
import org.apache.sling.api.SlingHttpServletRequest
import org.apache.sling.api.SlingHttpServletResponse

import javax.servlet.ServletException
import java.text.SimpleDateFormat

import static com.citytechinc.aem.bedrock.core.servlets.AbstractJsonResponseServlet.DEFAULT_DATE_FORMAT

class AbstractJsonResponseServletSpec extends BedrockSpec {

    static final def DATE = new Date()

    static final def MAP = [one: "Hello.", two: DATE]

    def "write json response"() {
        setup:
        def request = requestBuilder.build()
        def response = responseBuilder.build()

        def servlet = new AbstractJsonResponseServlet() {
            @Override
            protected void doGet(SlingHttpServletRequest slingRequest,
                SlingHttpServletResponse slingResponse) throws ServletException, IOException {
                writeJsonResponse(response, MAP)
            }
        }

        when:
        servlet.doGet(request, response)

        then:
        response.contentAsString == new JsonBuilder(
            [one: "Hello.", two: formatDate(DEFAULT_DATE_FORMAT, Locale.US)]).toString()
    }

    def "write json response with date format"() {
        setup:
        def request = requestBuilder.build()
        def response = responseBuilder.build()

        def servlet = new AbstractJsonResponseServlet() {
            @Override
            protected void doGet(SlingHttpServletRequest slingRequest,
                SlingHttpServletResponse slingResponse) throws ServletException, IOException {
                writeJsonResponse(response, MAP, "yyyy")
            }
        }

        when:
        servlet.doGet(request, response)

        then:
        response.contentAsString == new JsonBuilder(
            [one: "Hello.", two: formatDate("yyyy", Locale.US)]).toString()
    }

    def "write json response with date format and locale"() {
        setup:
        def request = requestBuilder.build()
        def response = responseBuilder.build()

        def servlet = new AbstractJsonResponseServlet() {
            @Override
            protected void doGet(SlingHttpServletRequest slingRequest,
                SlingHttpServletResponse slingResponse) throws ServletException, IOException {
                writeJsonResponse(response, MAP, "yyyy", Locale.CHINA)
            }
        }

        when:
        servlet.doGet(request, response)

        then:
        response.contentAsString == new JsonBuilder(
            [one: "Hello.", two: formatDate("yyyy", Locale.CHINA)]).toString()
    }

    private static def formatDate(String dateFormat, Locale locale) {
        new SimpleDateFormat(dateFormat, locale).format(DATE)
    }
}
