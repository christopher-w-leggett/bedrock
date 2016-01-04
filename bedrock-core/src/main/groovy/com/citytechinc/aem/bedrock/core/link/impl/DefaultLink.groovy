package com.citytechinc.aem.bedrock.core.link.impl

import com.citytechinc.aem.bedrock.api.link.Link
import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility
import com.fasterxml.jackson.annotation.JsonInclude

import groovy.transform.Immutable

@Immutable
class DefaultLink implements Link {

	String path

	String extension

	String suffix

	String href

	List<String> selectors

	String queryString

	boolean external

	String target

	String title

	Map<String, String> properties

	@Override
	boolean isEmpty() {
		!href
	}
}
