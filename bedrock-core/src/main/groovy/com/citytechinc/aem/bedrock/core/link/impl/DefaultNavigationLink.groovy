package com.citytechinc.aem.bedrock.core.link.impl

import com.citytechinc.aem.bedrock.api.link.Link
import com.citytechinc.aem.bedrock.api.link.NavigationLink
import groovy.transform.Immutable

@Immutable(knownImmutableClasses = [Link])
class DefaultNavigationLink implements NavigationLink {

    @Delegate
    Link link

    boolean active

    List<NavigationLink> children
}
