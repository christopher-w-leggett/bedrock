package com.citytechinc.aem.bedrock.core.tags

import com.citytechinc.aem.prosper.tag.JspTagProxy
import com.citytechinc.aem.prosper.traits.JspTagTrait

import javax.servlet.jsp.tagext.TagSupport

import static com.day.cq.wcm.tags.DefineObjectsTag.DEFAULT_XSSAPI_NAME

trait JspMetaTagTrait extends JspTagTrait {

    @Override
    JspTagProxy init(Class<TagSupport> tagClass, String path) {
        init(tagClass, path, [:])
    }

    @Override
    JspTagProxy init(Class<TagSupport> tagClass, String path, Map<String, Object> additionalPageContextAttributes) {
        additionalPageContextAttributes[DEFAULT_XSSAPI_NAME] = new MockXssApi()

        super.init(tagClass, path, additionalPageContextAttributes)
    }
}