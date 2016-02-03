package com.citytechinc.aem.bedrock.models.impl

import com.citytechinc.aem.bedrock.core.components.AbstractComponent
import org.apache.sling.api.resource.Resource
import org.apache.sling.models.annotations.Model

@Model(adaptables = Resource)
class BedrockModelComponent extends AbstractComponent {

    String getTitle() {
        get("jcr:title", "")
    }
}
