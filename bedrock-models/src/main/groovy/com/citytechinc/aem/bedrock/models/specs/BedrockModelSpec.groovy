package com.citytechinc.aem.bedrock.models.specs

import com.citytechinc.aem.bedrock.core.specs.BedrockSpec
import com.citytechinc.aem.bedrock.models.traits.BedrockModelTrait

/**
 * Specs may extend this class to support injection of Bedrock dependencies in Sling model-based components.
 */
abstract class BedrockModelSpec extends BedrockSpec implements BedrockModelTrait {

    def setupSpec() {
        registerDefaultInjectors()
        addModelsForPackage(this.class.package.name)
    }
}
