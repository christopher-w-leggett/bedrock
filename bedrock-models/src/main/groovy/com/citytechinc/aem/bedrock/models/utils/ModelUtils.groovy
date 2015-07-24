package com.citytechinc.aem.bedrock.models.utils

import org.apache.sling.api.SlingHttpServletRequest
import org.apache.sling.api.resource.Resource

/**
 * Model utility functions.
 */
class ModelUtils {

    static SlingHttpServletRequest getRequest(Object adaptable) {
        def request = null

        if (adaptable instanceof SlingHttpServletRequest) {
            request = adaptable as SlingHttpServletRequest
        }

        request
    }

    static Resource getResource(Object adaptable) {
        def resource = null

        if (adaptable instanceof Resource) {
            resource = adaptable as Resource
        } else if (adaptable instanceof SlingHttpServletRequest) {
            resource = (adaptable as SlingHttpServletRequest).resource
        }

        resource
    }

    private ModelUtils() {

    }
}
