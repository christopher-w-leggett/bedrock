package com.citytechinc.aem.bedrock.models.impl

import groovy.util.logging.Slf4j

import java.lang.reflect.AnnotatedElement
import java.lang.reflect.Type

import javax.inject.Named

import org.apache.felix.scr.annotations.Component
import org.apache.felix.scr.annotations.Property
import org.apache.felix.scr.annotations.Service
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy
import org.apache.sling.models.spi.AcceptsNullName
import org.apache.sling.models.spi.DisposalCallbackRegistry
import org.apache.sling.models.spi.injectorspecific.AbstractInjectAnnotationProcessor2
import org.apache.sling.models.spi.injectorspecific.InjectAnnotationProcessor2
import org.apache.sling.models.spi.injectorspecific.InjectAnnotationProcessorFactory2
import org.osgi.framework.Constants

import com.citytechinc.aem.bedrock.api.node.ComponentNode
import com.citytechinc.aem.bedrock.models.annotations.TagInject
import com.citytechinc.aem.bedrock.models.utils.ModelUtils
import com.day.cq.tagging.Tag
import com.day.cq.tagging.TagManager

@Component
@Service
@Property(name = Constants.SERVICE_RANKING, intValue = 800)
@Slf4j("LOG")
class TagInjector extends AbstractComponentNodeInjector implements InjectAnnotationProcessorFactory2, AcceptsNullName {

	@Override
	Object getValue(ComponentNode componentNode, String name, Type declaredType, AnnotatedElement element, DisposalCallbackRegistry callbackRegistry) {
		def annotation = element.getAnnotation(TagInject)
		def declaredClass = ModelUtils.getDeclaredClassForDeclaredType(declaredType)

		if (declaredClass == Tag) {
			def tagManager = componentNode.resource.resourceResolver.adaptTo(TagManager)
			def tagStrings = annotation && annotation.inherit() ? componentNode.getAsListInherited(name, String) : componentNode.getAsList(name, String)
			def tags = tagStrings.collect { tagManager.resolve(it) }

			if (tags) {

				if (!ModelUtils.isDeclaredTypeCollection(declaredType)) {
					return tags[0]
				}

				return tags
			}
		}

		return null
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

	private static class TagAnnotationProcessor extends AbstractInjectAnnotationProcessor2 {

		private final TagInject annotation

		TagAnnotationProcessor(TagInject annotation) {
			this.annotation = annotation
		}

		@Override
		public InjectionStrategy getInjectionStrategy() {
			return annotation.injectionStrategy()
		}
	}
}
