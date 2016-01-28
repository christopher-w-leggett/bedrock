package com.citytechinc.aem.bedrock.models.traits

import com.citytechinc.aem.bedrock.models.impl.AdaptableInjector
import com.citytechinc.aem.bedrock.models.impl.ComponentInjector
import com.citytechinc.aem.bedrock.models.impl.EnumInjector
import com.citytechinc.aem.bedrock.models.impl.ImageInjector
import com.citytechinc.aem.bedrock.models.impl.InheritInjector
import com.citytechinc.aem.bedrock.models.impl.LinkInjector
import com.citytechinc.aem.bedrock.models.impl.ModelListInjector
import com.citytechinc.aem.bedrock.models.impl.ReferenceInjector
import com.citytechinc.aem.bedrock.models.impl.TagInjector
import com.citytechinc.aem.bedrock.models.impl.ValueMapFromRequestInjector
import com.citytechinc.aem.prosper.context.ProsperSlingContext
import org.apache.sling.models.spi.Injector

import static org.osgi.framework.Constants.SERVICE_RANKING

trait BedrockModelTrait {

    abstract ProsperSlingContext getSlingContext()

    void registerDefaultInjectors() {
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
    }

    void registerInjector(Injector injector, Integer serviceRanking) {
        slingContext.registerInjectActivateService(injector, [(SERVICE_RANKING): serviceRanking])
    }

    void addModelsForPackage(String packageName) {
        slingContext.addModelsForPackage(packageName)
    }
}