package com.citytechinc.aem.bedrock.models.impl

import com.citytechinc.aem.bedrock.models.utils.ModelUtils
import org.apache.felix.scr.annotations.Component
import org.apache.felix.scr.annotations.Property
import org.apache.felix.scr.annotations.Service
import org.apache.sling.models.spi.DisposalCallbackRegistry
import org.apache.sling.models.spi.Injector
import org.osgi.framework.Constants

import java.lang.reflect.AnnotatedElement
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

@Component
@Service(Injector)
@Property(name = Constants.SERVICE_RANKING, intValue = 999)
class ModelListInjector implements Injector {

    @Override
    String getName() {
        "model-list"
    }

    @Override
    Object getValue(Object adaptable, String name, Type declaredType, AnnotatedElement element,
        DisposalCallbackRegistry callbackRegistry) {
        def value = null

        def resource = ModelUtils.getResource(adaptable)

        if (resource && declaredType instanceof ParameterizedType && (((ParameterizedType) declaredType).rawType) as
            Class == List) {
            def typeClass = getActualType((ParameterizedType) declaredType)

            def childResource = resource.getChild(name)

            if (childResource) {
                value = childResource.children.collect { grandChildResource -> grandChildResource.adaptTo(typeClass) } - null
            }
        }

        value
    }

    private static Class<?> getActualType(ParameterizedType declaredType) {
        def types = declaredType.actualTypeArguments

        types ? (Class<?>) types[0] : null
    }
}
