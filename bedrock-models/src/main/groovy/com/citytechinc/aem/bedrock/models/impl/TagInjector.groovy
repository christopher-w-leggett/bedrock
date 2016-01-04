package com.citytechinc.aem.bedrock.models.impl

import com.citytechinc.aem.bedrock.api.node.ComponentNode
import com.citytechinc.aem.bedrock.models.annotations.TagInject
import com.day.cq.tagging.Tag
import com.day.cq.tagging.TagManager

import groovy.transform.TupleConstructor
import groovy.util.logging.Slf4j

import org.apache.felix.scr.annotations.Component
import org.apache.felix.scr.annotations.Property
import org.apache.felix.scr.annotations.Reference
import org.apache.felix.scr.annotations.Service
import org.apache.sling.api.resource.ResourceResolver
import org.apache.sling.api.resource.ResourceResolverFactory
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy
import org.apache.sling.models.spi.AcceptsNullName
import org.apache.sling.models.spi.DisposalCallbackRegistry
import org.apache.sling.models.spi.Injector
import org.apache.sling.models.spi.injectorspecific.AbstractInjectAnnotationProcessor2
import org.apache.sling.models.spi.injectorspecific.InjectAnnotationProcessor2
import org.apache.sling.models.spi.injectorspecific.InjectAnnotationProcessorFactory2
import org.osgi.framework.Constants

import java.lang.reflect.AnnotatedElement
import java.lang.reflect.Type

@Component
@Service(Injector)
@Property(name = Constants.SERVICE_RANKING, intValue = 800)
@Slf4j("LOG")
class TagInjector extends AbstractComponentNodeInjector implements InjectAnnotationProcessorFactory2, AcceptsNullName,
ModelTrait {

	@Reference
	ResourceResolverFactory resourceResolverFactory

	@Override
	Object getValue(ComponentNode componentNode, String name, Type declaredType, AnnotatedElement element,
			DisposalCallbackRegistry callbackRegistry) {
		def annotation = element.getAnnotation(TagInject)
		def declaredClass = getDeclaredClassForDeclaredType(declaredType)

		ResourceResolver adminResourceResolver = resourceResolverFactory.getAdministrativeResourceResolver(null)

		try{
			if (declaredClass == Tag) {
				def tagManager = adminResourceResolver.adaptTo(TagManager)
				def tagStrings = annotation && annotation.inherit() ? componentNode.getAsListInherited(name,
						String) : componentNode.getAsList(name, String)
				def tags = tagStrings.collect { tagManager.resolve(it) }

				if (tags) {
					if (!isDeclaredTypeCollection(declaredType)) {
						return tags[0]
					}

					return tags
				}
			}

			return null
		}finally{
			adminResourceResolver.close()
		}
	}

	@Override
	InjectAnnotationProcessor2 createAnnotationProcessor(Object adaptable, AnnotatedElement element) {
		def annotation = element.getAnnotation(TagInject)

		annotation ? new TagAnnotationProcessor(annotation) : null
	}

	@Override
	String getName() {
		TagInject.NAME
	}

	@TupleConstructor
	private static class TagAnnotationProcessor extends AbstractInjectAnnotationProcessor2 {

		TagInject annotation

		@Override
		InjectionStrategy getInjectionStrategy() {
			annotation.injectionStrategy()
		}
	}
}
