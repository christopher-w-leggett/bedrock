package com.citytechinc.aem.bedrock.core.link.impl

import com.citytechinc.aem.bedrock.api.link.ImageLink
import com.citytechinc.aem.bedrock.api.link.Link
import groovy.transform.Immutable

@Immutable(knownImmutableClasses = [Link])
class DefaultImageLink implements ImageLink {

    @Delegate
    Link link

    String imageSource
}
