package com.citytechinc.aem.bedrock.models.impl

import javax.inject.Inject

import com.citytechinc.aem.bedrock.api.page.PageDecorator
import com.citytechinc.aem.bedrock.models.annotations.TagInject
import com.citytechinc.aem.bedrock.models.specs.BedrockModelSpec
import com.day.cq.tagging.Tag
import com.day.cq.tagging.TagManager
import com.day.cq.tagging.servlets.TagListServlet

import org.apache.sling.api.resource.Resource
import org.apache.sling.models.annotations.DefaultInjectionStrategy
import org.apache.sling.models.annotations.Model

class TagInjectorSpec extends BedrockModelSpec {
	@Model(adaptables = Resource, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
	static class Component {
		@Inject
		Tag singleTag

		@Inject
		List<Tag> tagList

		@TagInject(inherit=true)
		Tag singleTagInheirt

		@TagInject(inherit=true)
		List<Tag> tagListInheirt
	}

	def setupSpec() {
		pageBuilder.content {
			citytechinc {
				"jcr:content" {
					component(
							singleTag: "beers:lager",
							tagList: [
								"beers:lager",
								"beers:stout",
								"beers:ale"
							],
							singleTagInheirt: "beers:porter",
							tagListInheirt: [
								"beers:ale",
								"beers:porter",
								"beers:lager"
							]
							)
				}
				page1 { "jcr:content" { component() } }
			}
		}
		nodeBuilder.etc{
			tags {
				beers ("sling:resourceType" : "cq/tagging/components/tag", title: "Beers"){
					lager ("sling:resourceType" : "cq/tagging/components/tag", title: "Lager")
					stout ("sling:resourceType" : "cq/tagging/components/tag", title: "Stout")
					porter ("sling:resourceType" : "cq/tagging/components/tag", title: "Porter")
					ale ("sling:resourceType" : "cq/tagging/components/tag", title: "Ale")
				}
			}
		}
	}

	def "all tags populated from root"() {
		setup:
		def resource = resourceResolver.resolve("/content/citytechinc/jcr:content/component")
		def component = resource.adaptTo(Component)

		expect:
		component.singleTag.path == "/etc/tags/beers/lager"
		component.singleTagInheirt.path == "/etc/tags/beers/porter"
		component.tagList.size == 3
		component.tagList[0].path == "/etc/tags/beers/lager"
		component.tagList[1].path == "/etc/tags/beers/stout"
		component.tagList[2].path == "/etc/tags/beers/ale"

		component.tagListInheirt.size == 3
		component.tagListInheirt[0].path == "/etc/tags/beers/ale"
		component.tagListInheirt[1].path == "/etc/tags/beers/porter"
		component.tagListInheirt[2].path == "/etc/tags/beers/lager"
	}

	def "all inherited tags populated"(){
		setup:
		def resource = resourceResolver.resolve("/content/citytechinc/page1/jcr:content/component")
		def component = resource.adaptTo(Component)

		expect:
		component.singleTag == null
		component.singleTagInheirt.path == "/etc/tags/beers/porter"
		component.tagList == null

		component.tagListInheirt.size == 3
		component.tagListInheirt[0].path == "/etc/tags/beers/ale"
		component.tagListInheirt[1].path == "/etc/tags/beers/porter"
		component.tagListInheirt[2].path == "/etc/tags/beers/lager"
	}
}
