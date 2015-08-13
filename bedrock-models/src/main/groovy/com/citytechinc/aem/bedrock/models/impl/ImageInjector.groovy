package com.citytechinc.aem.bedrock.models.impl

import com.citytechinc.aem.bedrock.api.node.ComponentNode
import com.citytechinc.aem.bedrock.models.annotations.ImageInject
import com.day.cq.wcm.foundation.Image
import com.google.common.base.Predicate
import org.apache.felix.scr.annotations.Component
import org.apache.felix.scr.annotations.Property
import org.apache.felix.scr.annotations.Service
import org.apache.sling.api.resource.Resource
import org.apache.sling.models.spi.AcceptsNullName
import org.apache.sling.models.spi.DisposalCallbackRegistry
import org.apache.sling.models.spi.Injector
import org.apache.sling.models.spi.injectorspecific.AbstractInjectAnnotationProcessor2
import org.apache.sling.models.spi.injectorspecific.InjectAnnotationProcessor2
import org.apache.sling.models.spi.injectorspecific.StaticInjectAnnotationProcessorFactory
import org.osgi.framework.Constants

import java.lang.reflect.AnnotatedElement

@Component
@Service(Injector)
@Property(name = Constants.SERVICE_RANKING, intValue = 4000)
class ImageInjector extends AbstractTypedComponentNodeInjector<Image> implements Injector,
    StaticInjectAnnotationProcessorFactory, AcceptsNullName {

    @Override
    String getName() {
        ImageInject.NAME
    }

    @Override
    Object getValue(ComponentNode componentNode, String name, Class<Image> declaredType,
        AnnotatedElement element, DisposalCallbackRegistry callbackRegistry) {
        def imageAnnotation = element.getAnnotation(ImageInject)
        def path = imageAnnotation?.path()

        Image image
        Resource resource

        if (imageAnnotation?.inherit()) {
            def componentNodeInherit = componentNode.findAncestor(new Predicate<ComponentNode>() {
                @Override
                boolean apply(ComponentNode cn) {
                    path ? cn.isHasImage(path) : cn.hasImage
                }
            })

            if (componentNodeInherit.present) {
                resource = componentNodeInherit.get().resource
            }
        } else {
            resource = componentNode.resource
        }

        if (resource) {
            if (path) {
                image = new Image(resource, path)
            } else {
                image = new Image(resource)
            }

            if (image.hasContent()) {
                return image
            }
        }

        null
    }

    @Override
    InjectAnnotationProcessor2 createAnnotationProcessor(AnnotatedElement element) {
        // check if the element has the expected annotation
        def annotation = element.getAnnotation(ImageInject)

        annotation ? new ImageAnnotationProcessor(annotation) : null
    }

    private static class ImageAnnotationProcessor extends AbstractInjectAnnotationProcessor2 {

        private final ImageInject annotation

        ImageAnnotationProcessor(ImageInject annotation) {
            this.annotation = annotation
        }

        @Override
        Boolean isOptional() {
            annotation.optional()
        }
    }
}
