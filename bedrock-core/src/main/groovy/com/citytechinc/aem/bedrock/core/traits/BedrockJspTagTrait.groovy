package com.citytechinc.aem.bedrock.core.traits

import com.citytechinc.aem.prosper.tag.JspTagProxy
import com.citytechinc.aem.prosper.traits.JspTagTrait
import com.day.cq.wcm.api.PageManager

import javax.servlet.jsp.tagext.TagSupport

import static com.day.cq.wcm.tags.DefineObjectsTag.DEFAULT_CURRENT_PAGE_NAME
import static org.apache.sling.scripting.jsp.taglib.DefineObjectsTag.DEFAULT_RESOURCE_NAME

/**
 * Prosper trait to support initialization of JSP tag classes with additional Bedrock-specific attributes.
 */
trait BedrockJspTagTrait extends JspTagTrait {

    /**
     * Initialize a JSP tag support class with a <code>Resource</code> instance for the given path.
     *
     * @param tagClass tag class
     * @param path component resource path
     * @return initialized tag
     */
    JspTagProxy init(Class<TagSupport> tagClass, String path) {
        init(tagClass, path, [:])
    }

    /**
     * Initialize a JSP tag support class with a <code>Resource</code> instance for the given path.
     *
     * @param tagClass tag class
     * @param path component resource path
     * @param additionalPageContextAttributes additional attributes to set in the mocked page context
     * @return
     */
    JspTagProxy init(Class<TagSupport> tagClass, String path, Map<String, Object> additionalPageContextAttributes) {
        def resource = resourceResolver.getResource(path)
        def currentPage = resourceResolver.adaptTo(PageManager).getContainingPage(resource)

        additionalPageContextAttributes[DEFAULT_RESOURCE_NAME] = resource
        additionalPageContextAttributes[DEFAULT_CURRENT_PAGE_NAME] = currentPage

        init(tagClass, additionalPageContextAttributes)
    }
}
