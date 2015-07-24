package com.citytechinc.aem.bedrock.models.impl

import com.citytechinc.aem.bedrock.api.link.Link
import com.citytechinc.aem.bedrock.api.node.ComponentNode
import com.citytechinc.aem.bedrock.core.link.builders.factory.LinkBuilderFactory
import com.citytechinc.aem.bedrock.models.annotations.LinkInject
import com.google.common.base.Optional
import org.apache.felix.scr.annotations.Component
import org.apache.felix.scr.annotations.Property
import org.apache.felix.scr.annotations.Service
import org.apache.sling.models.spi.AcceptsNullName
import org.apache.sling.models.spi.DisposalCallbackRegistry
import org.apache.sling.models.spi.Injector
import org.apache.sling.models.spi.injectorspecific.AbstractInjectAnnotationProcessor
import org.apache.sling.models.spi.injectorspecific.InjectAnnotationProcessor
import org.apache.sling.models.spi.injectorspecific.InjectAnnotationProcessorFactory
import org.osgi.framework.Constants

import java.lang.reflect.AnnotatedElement

@Component
@Service
@Property(name = Constants.SERVICE_RANKING, intValue = 4000)
class LinkInjector extends AbstractTypedComponentNodeInjector<Link> implements Injector,
    InjectAnnotationProcessorFactory, AcceptsNullName {

	@Override
    String getName() {
		LinkInject.NAME
	}

	@Override
    Object getValue(ComponentNode componentNode, String name, Class<Link> declaredType, AnnotatedElement element,
        DisposalCallbackRegistry callbackRegistry) {
		def injectAnnotation = element.getAnnotation(LinkInject)

		Optional<String> pathOptional

		String title = null

		if (injectAnnotation) {
			if (injectAnnotation.inherit()) {
				pathOptional = componentNode.getInherited(name, String)
				if (injectAnnotation.titleProperty()) {
					title = componentNode.getInherited(injectAnnotation.titleProperty(), String).orNull()
				}
			} else {
				pathOptional = componentNode.get(name, String)
				if (injectAnnotation.titleProperty()) {
					title = componentNode.get(injectAnnotation.titleProperty(), String).orNull()
				}
			}
		} else {
			pathOptional = componentNode.get(name, String)
		}

		if (pathOptional.present) {
			def linkBuilder = LinkBuilderFactory.forPath(pathOptional.get()).setTitle(title)

			return linkBuilder.build()
		}

		null
	}

	@Override
    InjectAnnotationProcessor createAnnotationProcessor(Object adaptable, AnnotatedElement element) {
		// check if the element has the expected annotation
		def annotation = element.getAnnotation(LinkInject)

		annotation ? new LinkAnnotationProcessor(annotation) : null
	}

	private static class LinkAnnotationProcessor extends AbstractInjectAnnotationProcessor {

		private final LinkInject annotation

		LinkAnnotationProcessor(LinkInject annotation) {
			this.annotation = annotation
		}

		@Override
		Boolean isOptional() {
			annotation.optional()
		}
	}
}
