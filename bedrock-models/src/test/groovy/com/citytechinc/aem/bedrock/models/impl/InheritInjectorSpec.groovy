package com.citytechinc.aem.bedrock.models.impl

import javax.inject.Inject

import org.apache.sling.api.SlingHttpServletRequest
import org.apache.sling.api.resource.Resource
import org.apache.sling.models.annotations.DefaultInjectionStrategy
import org.apache.sling.models.annotations.Model
import org.apache.sling.models.annotations.Optional

import com.citytechinc.aem.bedrock.models.annotations.InheritInject
import com.citytechinc.aem.bedrock.models.impl.EnumInjectorSpec.Beer
import com.citytechinc.aem.bedrock.models.specs.BedrockModelSpec

class InheritInjectorSpec extends BedrockModelSpec {

	@Model(adaptables = Resource, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
	static class InheritModel {

		@InheritInject
		String title
	}

	def setupSpec() {
		pageBuilder.content {
			citytechinc {
				"jcr:content" { component("title": "Testing Component") }
				page1 { "jcr:content" { component() } }
			}
		}
	}

	def "Test inherit"(){
		setup:
		def resource = resourceResolver.resolve("/content/citytechinc/page1/jcr:content/component")
		def component = resource.adaptTo(InheritModel)

		expect:
		component.title == "Testing Component"
	}
}