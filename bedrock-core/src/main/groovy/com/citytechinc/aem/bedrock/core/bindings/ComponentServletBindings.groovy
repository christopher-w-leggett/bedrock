package com.citytechinc.aem.bedrock.core.bindings

import com.citytechinc.aem.bedrock.api.request.ComponentServletRequest
import com.day.cq.wcm.api.designer.Designer
import com.day.cq.wcm.commons.WCMUtils

import javax.script.Bindings

import static com.adobe.cq.sightly.WCMBindings.COMPONENT
import static com.adobe.cq.sightly.WCMBindings.COMPONENT_CONTEXT
import static com.adobe.cq.sightly.WCMBindings.CURRENT_DESIGN
import static com.adobe.cq.sightly.WCMBindings.CURRENT_STYLE
import static com.adobe.cq.sightly.WCMBindings.DESIGNER
import static com.adobe.cq.sightly.WCMBindings.EDIT_CONTEXT
import static org.apache.sling.api.scripting.SlingBindings.REQUEST
import static org.apache.sling.api.scripting.SlingBindings.RESOURCE
import static org.apache.sling.api.scripting.SlingBindings.RESPONSE

/**
 * A bindings instance created from a component servlet containing all of the bindings necessary to instantiate a
 * <code>ComponentRequest</code>.
 */
final class ComponentServletBindings implements Bindings {

    @Delegate
    private final Map<String, Object> map = [:]

    ComponentServletBindings(ComponentServletRequest request) {
        map.put(REQUEST, request.slingRequest)
        map.put(RESPONSE, request.slingResponse)
        map.put(RESOURCE, request.resource)

        def componentContext = WCMUtils.getComponentContext(request.slingRequest)

        map.put(COMPONENT_CONTEXT, componentContext)
        map.put(COMPONENT, componentContext?.component)
        map.put(EDIT_CONTEXT, componentContext?.editContext)

        def designer = request.resourceResolver.adaptTo(Designer)

        map.put(DESIGNER, designer)

        def currentPage = request.currentPage
        def currentDesign = designer.getDesign(currentPage)
        def currentStyle = !componentContext || !currentDesign || !componentContext.cell ? null : currentDesign.getStyle(componentContext.cell)

        map.put(CURRENT_DESIGN, currentDesign)
        map.put(CURRENT_STYLE, currentStyle)
    }

    @Override
    def put(String key, value) {
        map.put(key, value)
    }
}
