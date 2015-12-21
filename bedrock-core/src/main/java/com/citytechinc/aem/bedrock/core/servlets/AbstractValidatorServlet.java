package com.citytechinc.aem.bedrock.core.servlets;

import com.google.common.collect.ImmutableMap;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import java.io.IOException;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Validators extending this class need to add the following SCR annotation to register the servlet:
 * <p>
 * <pre>
 * {@literal @}SlingServlet(resourceTypes = "bedrock/components/content/example", selectors = "validator",
 * extensions = "json", methods = "GET")
 * </pre>
 * <p>
 * The "resourceTypes" annotation will need to be updated to match the resource type of the component being validated.
 * The component dialog.xml can call the the validator for a given dialog field using the following function:
 * <p>
 * <pre>
 * {@code
 * <name jcr:primaryType="cq:Widget" fieldLabel="Name" name="./name" xtype="textfield"
 *     validator="function(value) {
 *         return Bedrock.Utilities.Dialog.validateField(this, value, 'Name is invalid');
 *     }" />
 * }
 * </pre>
 */
public abstract class AbstractValidatorServlet extends AbstractJsonResponseServlet {

    private static final long serialVersionUID = 1L;

    private static final Logger LOG = LoggerFactory.getLogger(AbstractValidatorServlet.class);

    @Override
    protected final void doGet(final SlingHttpServletRequest request, final SlingHttpServletResponse response)
        throws ServletException, IOException {
        final String value = checkNotNull(request.getRequestParameter("value"), "value parameter must be non-null")
            .getString();
        final String path = request.getResource().getPath();

        final boolean valid = isValid(request, path, value);

        LOG.debug("path = {}, is valid = {}", path, valid);

        writeJsonResponse(response, ImmutableMap.of("valid", valid));
    }

    /**
     * Validate the given value for this request and path.
     *
     * @param request servlet request
     * @param path path to current component being validated
     * @param value input value to validate
     * @return true if value is valid, false otherwise
     */
    protected abstract boolean isValid(final SlingHttpServletRequest request, final String path, final String value);
}
