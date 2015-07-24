package com.citytechinc.aem.bedrock.core.adapter

import com.citytechinc.aem.bedrock.api.node.BasicNode
import com.citytechinc.aem.bedrock.api.node.ComponentNode
import com.citytechinc.aem.bedrock.api.page.PageDecorator
import com.citytechinc.aem.bedrock.api.page.PageManagerDecorator
import com.citytechinc.aem.bedrock.core.node.impl.DefaultBasicNode
import com.citytechinc.aem.bedrock.core.node.impl.DefaultComponentNode
import com.citytechinc.aem.bedrock.core.page.impl.DefaultPageDecorator
import com.citytechinc.aem.bedrock.core.page.impl.DefaultPageManagerDecorator
import com.day.cq.wcm.api.Page
import org.apache.felix.scr.annotations.Component
import org.apache.felix.scr.annotations.Properties
import org.apache.felix.scr.annotations.Property
import org.apache.felix.scr.annotations.Service
import org.apache.sling.api.SlingConstants
import org.apache.sling.api.adapter.AdapterFactory
import org.apache.sling.api.resource.Resource
import org.apache.sling.api.resource.ResourceResolver
import org.osgi.framework.Constants

@Component
@Service(AdapterFactory)
@Properties([
    @Property(name = Constants.SERVICE_DESCRIPTION, value = "Bedrock Core Adapter Factory"),
    @Property(name = SlingConstants.PROPERTY_ADAPTABLE_CLASSES, value = [
        "org.apache.sling.api.resource.Resource",
        "org.apache.sling.api.resource.ResourceResolver"
    ]),
    @Property(name = SlingConstants.PROPERTY_ADAPTER_CLASSES, value = [
        "com.citytechinc.aem.bedrock.api.page.PageManagerDecorator",
        "com.citytechinc.aem.bedrock.api.page.PageDecorator",
        "com.citytechinc.aem.bedrock.api.node.ComponentNode",
        "com.citytechinc.aem.bedrock.api.node.BasicNode"
    ])
])
final class BedrockAdapterFactory implements AdapterFactory {

    @Override
    <AdapterType> AdapterType getAdapter(Object adaptable, Class<AdapterType> type) {
        def result

        if (adaptable instanceof ResourceResolver) {
            result = getResourceResolverAdapter((ResourceResolver) adaptable, type)
        } else if (adaptable instanceof Resource) {
            result = getResourceAdapter((Resource) adaptable, type)
        } else {
            result = null
        }

        result
    }

    private static <AdapterType> AdapterType getResourceResolverAdapter(ResourceResolver resourceResolver,
        Class<AdapterType> type) {
        def result

        if (type == PageManagerDecorator) {
            result = new DefaultPageManagerDecorator(resourceResolver) as AdapterType
        } else {
            result = null
        }

        result
    }

    private static <AdapterType> AdapterType getResourceAdapter(Resource resource, Class<AdapterType> type) {
        def result

        if (type == PageDecorator) {
            def page = resource.adaptTo(Page)

            result = page == null ? null : new DefaultPageDecorator(page) as AdapterType
        } else if (type == ComponentNode) {
            result = new DefaultComponentNode(resource) as AdapterType
        } else if (type == BasicNode) {
            result = new DefaultBasicNode(resource) as AdapterType
        } else {
            result = null
        }

        result
    }
}
