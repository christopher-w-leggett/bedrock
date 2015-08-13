package com.citytechinc.aem.bedrock.models.specs

import com.citytechinc.aem.bedrock.core.specs.BedrockSpec
import com.citytechinc.aem.bedrock.models.impl.AdaptableInjector
import com.citytechinc.aem.bedrock.models.impl.ComponentInjector
import com.citytechinc.aem.bedrock.models.impl.EnumInjector
import com.citytechinc.aem.bedrock.models.impl.ImageInjector
import com.citytechinc.aem.bedrock.models.impl.InheritInjector
import com.citytechinc.aem.bedrock.models.impl.LinkInjector
import com.citytechinc.aem.bedrock.models.impl.ModelListInjector
import com.citytechinc.aem.bedrock.models.impl.ValueMapFromRequestInjector

import static org.osgi.framework.Constants.SERVICE_RANKING

/**
 * Specs may extend this class to support injection of Bedrock dependencies in Sling model-based components.
 */
abstract class BedrockModelSpec extends BedrockSpec {

    def setupSpec() {
        slingContext.with {
            registerInjectActivateService(new ComponentInjector(), [(SERVICE_RANKING): Integer.MAX_VALUE])
            registerInjectActivateService(new AdaptableInjector(), [(SERVICE_RANKING): Integer.MIN_VALUE])
            registerInjectActivateService(new EnumInjector(), [(SERVICE_RANKING): 4000])
            registerInjectActivateService(new ImageInjector(), [(SERVICE_RANKING): 4000])
            registerInjectActivateService(new InheritInjector(), [(SERVICE_RANKING): 4000])
            registerInjectActivateService(new LinkInjector(), [(SERVICE_RANKING): 4000])
            registerInjectActivateService(new ModelListInjector(), [(SERVICE_RANKING): 999])
            registerInjectActivateService(new ValueMapFromRequestInjector(), [(SERVICE_RANKING): 2500])
            addModelsForPackage(this.class.package.name)
        }
    }
}
