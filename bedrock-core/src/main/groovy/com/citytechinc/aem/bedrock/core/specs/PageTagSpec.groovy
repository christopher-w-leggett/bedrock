package com.citytechinc.aem.bedrock.core.specs

import com.citytechinc.aem.bedrock.core.adapter.BedrockAdapterFactory
import com.citytechinc.aem.bedrock.api.page.PageManagerDecorator
import com.citytechinc.aem.prosper.specs.JspTagSpec
import org.apache.sling.api.adapter.AdapterFactory

import static com.day.cq.wcm.tags.DefineObjectsTag.DEFAULT_CURRENT_PAGE_NAME

/**
 * Spock specification for testing CQ page-based tag support classes.
 */
abstract class PageTagSpec extends JspTagSpec {

    @Override
    Collection<AdapterFactory> addAdapterFactories() {
        [new BedrockAdapterFactory()]
    }

    /**
     * Set a <code>PageDecorator</code> for the given path in the <code>PageContext</code> for the tag under test.
     *
     * @param path page path
     */
    void setupPage(String path) {
        def page = resourceResolver.adaptTo(PageManagerDecorator).getPage(path)

        tag.pageContext.getAttribute(DEFAULT_CURRENT_PAGE_NAME) >> page
    }
}