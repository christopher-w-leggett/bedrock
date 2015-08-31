package com.citytechinc.aem.bedrock.models.utils

import org.apache.sling.api.SlingHttpServletRequest
import org.apache.sling.api.resource.Resource

import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

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

    static boolean isDeclaredTypeCollection(Type declaredType) {
        if (declaredType instanceof ParameterizedType) {
            def parameterizedType = (ParameterizedType) declaredType
            def collectionType = (Class) parameterizedType.rawType

            return Collection.isAssignableFrom(collectionType)
        }

        return false
    }

    static Class getDeclaredClassForDeclaredType(Type declaredType) {
        if (isDeclaredTypeCollection(declaredType)) {
            def parameterizedType = (ParameterizedType) declaredType
            return (Class) parameterizedType.getActualTypeArguments()[0]
        }

        return (Class) declaredType
    }

    private ModelUtils() {

    }
}
