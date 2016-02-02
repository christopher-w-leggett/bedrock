package com.citytechinc.aem.bedrock.core.servlets.datasource;

import com.adobe.granite.ui.components.ds.DataSource;
import com.adobe.granite.ui.components.ds.SimpleDataSource;
import com.adobe.granite.ui.components.ds.ValueMapResource;
import com.citytechinc.aem.bedrock.core.servlets.optionsprovider.Option;
import com.day.cq.commons.jcr.JcrConstants;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceMetadata;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.api.wrappers.ValueMapDecorator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.servlet.ServletException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 */
public abstract class AbstractOptionsDataSourceServlet extends SlingSafeMethodsServlet {

    public abstract List<Option> getOptions(final SlingHttpServletRequest request);

    @Override
    protected void doGet(@Nonnull final SlingHttpServletRequest request,
        @Nonnull final SlingHttpServletResponse response) throws ServletException, IOException {
        final ResourceResolver resourceResolver = request.getResourceResolver();

        final List<Option> options = getOptions(request);

        final List<Resource> resources = Lists.transform(options, new Function<Option, Resource>() {
            @Nullable
            @Override
            public Resource apply(final Option option) {
                final Map<String, Object> map = new HashMap<>();

                map.put("value", option.getValue());
                map.put("text", option.getText());

                final ValueMap valueMap = new ValueMapDecorator(map);

                return new ValueMapResource(resourceResolver, new ResourceMetadata(), JcrConstants.NT_UNSTRUCTURED,
                    valueMap);
            }
        });

        final DataSource dataSource = new SimpleDataSource(resources.iterator());

        request.setAttribute(DataSource.class.getName(), dataSource);
    }
}
