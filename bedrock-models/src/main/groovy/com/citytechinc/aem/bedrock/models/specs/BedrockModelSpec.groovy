package com.citytechinc.aem.bedrock.models.specs

import com.citytechinc.aem.bedrock.core.specs.BedrockSpec
import com.citytechinc.aem.bedrock.models.i18n.impl.DefaultLocaleResolver
import com.citytechinc.aem.bedrock.models.impl.AdaptableInjector
import com.citytechinc.aem.bedrock.models.impl.ComponentInjector
import com.citytechinc.aem.bedrock.models.impl.EnumInjector
import com.citytechinc.aem.bedrock.models.impl.ImageInjector
import com.citytechinc.aem.bedrock.models.impl.InheritInjector
import com.citytechinc.aem.bedrock.models.impl.LinkInjector
import com.citytechinc.aem.bedrock.models.impl.ModelListInjector
import com.citytechinc.aem.bedrock.models.impl.ReferenceInjector
import com.citytechinc.aem.bedrock.models.impl.TagInjector
import com.citytechinc.aem.bedrock.models.impl.TranslatorInjector
import com.citytechinc.aem.bedrock.models.impl.ValueMapFromRequestInjector

import static org.osgi.framework.Constants.SERVICE_RANKING

/**
 * Specs may extend this class to support injection of Bedrock dependencies in Sling model-based components.
 */
abstract class BedrockModelSpec extends BedrockSpec {

    /**
     * Register default Bedrock injectors and all <code>@Model>/code>-annotated classes for the current package.
     */
    def setupSpec() {
        registerDefaultInjectors()

        slingContext.addModelsForPackage(this.class.package.name)
    }

    /**
     * Register the default set of Bedrock injector services.
     */
    void registerDefaultInjectors() {
        slingContext.with {
            registerInjector(new ComponentInjector(), Integer.MAX_VALUE)
            registerInjector(new AdaptableInjector(), Integer.MIN_VALUE)
            registerInjector(new TagInjector(), 800)
            registerInjector(new EnumInjector(), 4000)
            registerInjector(new ImageInjector(), 4000)
            registerInjector(new InheritInjector(), 4000)
            registerInjector(new LinkInjector(), 4000)
            registerInjector(new ReferenceInjector(), 4000)
            registerInjector(new ModelListInjector(), 999)
            registerInjector(new ValueMapFromRequestInjector(), 2500)
            registerInjectActivateService(new DefaultLocaleResolver(), [(SERVICE_RANKING): 4000])
            registerInjector(new TranslatorInjector(), 4000)
        }
    }
}
