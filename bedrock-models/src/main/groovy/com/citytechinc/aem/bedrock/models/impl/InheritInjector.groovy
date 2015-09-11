package com.citytechinc.aem.bedrock.models.impl

import com.citytechinc.aem.bedrock.api.node.ComponentNode
import com.citytechinc.aem.bedrock.models.annotations.InheritInject
import groovy.transform.TupleConstructor
import org.apache.felix.scr.annotations.Component
import org.apache.felix.scr.annotations.Property
import org.apache.felix.scr.annotations.Service
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy
import org.apache.sling.models.spi.DisposalCallbackRegistry
import org.apache.sling.models.spi.Injector
import org.apache.sling.models.spi.injectorspecific.AbstractInjectAnnotationProcessor2
import org.apache.sling.models.spi.injectorspecific.InjectAnnotationProcessor2
import org.apache.sling.models.spi.injectorspecific.InjectAnnotationProcessorFactory2
import org.osgi.framework.Constants

import java.lang.reflect.AnnotatedElement
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

@Component
@Service(Injector)
@Property(name = Constants.SERVICE_RANKING, intValue = 4000)
class InheritInjector extends AbstractComponentNodeInjector implements InjectAnnotationProcessorFactory2 {

    @Override
    String getName() {
        InheritInject.NAME
    }

    @Override
    Object getValue(ComponentNode componentNode, String name, Type declaredType, AnnotatedElement element,
        DisposalCallbackRegistry callbackRegistry) {
        def value = null

        if (declaredType instanceof Class && declaredType.enum) {
            def enumString = componentNode.getInherited(name, String)

            value = enumString.present ? declaredType[enumString.get()] : null
        } else if (!declaredType instanceof ParameterizedType) {
            value = componentNode.getInherited(name, declaredType).orNull()
        }

        value
    }

    @Override
    InjectAnnotationProcessor2 createAnnotationProcessor(Object adaptable, AnnotatedElement element) {
        def annotation = element.getAnnotation(InheritInject)

        annotation ? new InheritAnnotationProcessor(annotation) : null
    }

    @TupleConstructor
    private static class InheritAnnotationProcessor extends AbstractInjectAnnotationProcessor2 {

        InheritInject annotation

        @Override
        InjectionStrategy getInjectionStrategy() {
            return annotation.injectionStrategy()
        }
    }
}
