package com.citytechinc.aem.bedrock.models.impl

import com.citytechinc.aem.bedrock.api.node.ComponentNode
import com.citytechinc.aem.bedrock.models.annotations.ReferenceInject
import com.citytechinc.aem.bedrock.models.utils.ModelUtils
import groovy.util.logging.Slf4j
import org.apache.felix.scr.annotations.Component
import org.apache.felix.scr.annotations.Property
import org.apache.felix.scr.annotations.Service
import org.apache.sling.api.resource.Resource
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy
import org.apache.sling.models.spi.DisposalCallbackRegistry
import org.apache.sling.models.spi.injectorspecific.AbstractInjectAnnotationProcessor2
import org.apache.sling.models.spi.injectorspecific.InjectAnnotationProcessor2
import org.apache.sling.models.spi.injectorspecific.InjectAnnotationProcessorFactory2
import org.osgi.framework.Constants

import javax.inject.Named
import java.lang.reflect.AnnotatedElement
import java.lang.reflect.Type

@Component
@Service
@Property(name = Constants.SERVICE_RANKING, intValue = 4000)
@Slf4j("LOG")
class ReferenceInjector extends AbstractComponentNodeInjector implements InjectAnnotationProcessorFactory2 {

    @Override
    Object getValue(ComponentNode componentNode, String name, Type declaredType, AnnotatedElement element, DisposalCallbackRegistry callbackRegistry) {
        def annotation = element.getAnnotation(ReferenceInject)
        def propertyName = element.getAnnotation(Named)?.value() ?: name
        def declaredClass = ModelUtils.getDeclaredClassForDeclaredType(declaredType)

        if (annotation) {
            def references = annotation.inherit() ? componentNode.getAsListInherited(propertyName, String) : componentNode.getAsList(propertyName, String)
            def referencedObjects = references.collect {
                def referencedResource = it.startsWith("/") ?
                        componentNode.resource.resourceResolver.getResource(it) :
                        componentNode.resource.resourceResolver.getResource(componentNode.resource, it)

                if (!referencedResource) {
                        LOG.warn("Reference " + it + " did not resolve to an accessible Resource")
                }
                if (referencedResource && declaredClass != Resource) {
                    def adaptedObject = referencedResource.adaptTo(declaredClass)

                    if (!adaptedObject) {
                        LOG.warn("Resource at " + referencedResource.path + " could not be adapted to an instance of " + declaredClass.name)
                    }

                    return adaptedObject
                }

                return referencedResource
            }

            if (referencedObjects) {

                if (!ModelUtils.isDeclaredTypeCollection(declaredType)) {
                    return referencedObjects[0]
                }

                return referencedObjects

            }
        }

        return null
    }

    @Override
    InjectAnnotationProcessor2 createAnnotationProcessor(Object adaptable, AnnotatedElement element) {
        def annotation = element.getAnnotation(ReferenceInject)

        annotation ? new ReferenceAnnotationProcessor(annotation) : null
    }

    @Override
    String getName() {
        ReferenceInject.NAME
    }

    private static class ReferenceAnnotationProcessor extends AbstractInjectAnnotationProcessor2 {

        private final ReferenceInject annotation

        ReferenceAnnotationProcessor(ReferenceInject annotation) {
            this.annotation = annotation
        }

        @Override
        public InjectionStrategy getInjectionStrategy() {
            return annotation.injectionStrategy()
        }

    }

}
