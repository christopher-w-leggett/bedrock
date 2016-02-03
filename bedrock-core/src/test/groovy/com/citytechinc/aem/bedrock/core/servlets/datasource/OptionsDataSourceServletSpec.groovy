package com.citytechinc.aem.bedrock.core.servlets.datasource

import com.adobe.granite.ui.components.ds.DataSource
import com.citytechinc.aem.bedrock.api.request.ComponentServletRequest
import com.citytechinc.aem.bedrock.core.servlets.optionsprovider.Option
import com.citytechinc.aem.bedrock.core.specs.BedrockSpec
import org.apache.sling.api.SlingHttpServletRequest

class OptionsDataSourceServletSpec extends BedrockSpec {

    static final def MAP = ["one": "One", "two": "Two"]

    static final def OPTIONS = Option.fromMap(MAP)

    class NoOptionsProviderServlet extends AbstractOptionsDataSourceServlet {

        @Override
        List<Option> getOptions(ComponentServletRequest request) {
            []
        }
    }

    class BasicOptionsProviderServlet extends AbstractOptionsDataSourceServlet {

        @Override
        List<Option> getOptions(ComponentServletRequest request) {
            OPTIONS
        }
    }

    def "no options"() {
        def servlet = new NoOptionsProviderServlet()
        def request = requestBuilder.build()
        def response = responseBuilder.build()

        when:
        servlet.doGet(request, response)

        then:
        assertDataSourceOptions(request, [])
    }

    def "options"() {
        def servlet = new BasicOptionsProviderServlet()
        def request = requestBuilder.build()
        def response = responseBuilder.build()

        when:
        servlet.doGet(request, response)

        then:
        assertDataSourceOptions(request, OPTIONS)
    }

    void assertDataSourceOptions(SlingHttpServletRequest request, List<Option> options) {
        def dataSource = request.getAttribute(DataSource.class.name) as DataSource
        def resources = dataSource.iterator()

        assert resources.size() == options.size()

        resources.eachWithIndex { resource, i ->
            def properties = resource.valueMap
            def option = options.get(i)

            assert option.text == properties.get("text", "")
            assert option.value == properties.get("value", "")
        }
    }
}
