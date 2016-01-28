package com.citytechinc.aem.bedrock.core.tags

trait PropertyTagTrait {

    String propertyName

    boolean hasPropertyName() {
        propertyName as Boolean
    }
}