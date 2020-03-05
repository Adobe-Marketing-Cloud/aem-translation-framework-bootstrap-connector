/*
 ADOBE CONFIDENTIAL
 Copyright 2018 Adobe Systems Incorporated
 All Rights Reserved.
 NOTICE:  All information contained herein is, and remains
 the property of Adobe Systems Incorporated and its suppliers,
 if any. The intellectual and technical concepts contained
 herein are proprietary to Adobe Systems Incorporated and its
 suppliers and may be covered by U.S. and Foreign Patents,
 patents in process, and are protected by trade secret or copyright law.
 Dissemination of this information or reproduction of this material
 is strictly forbidden unless prior written permission is obtained
 from Adobe Systems Incorporated.
 */

package com.adobe.granite.translation.connector.bootstrap.ui.models;

import com.adobe.granite.translation.connector.bootstrap.core.BootstrapTranslationCloudConfig;
import com.day.cq.commons.jcr.JcrConstants;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;

/*
 *  Sling Model used in editform.html sightly file for fetching the bootstrap cloud config input fields for populating the form
 *  For more info about Sling Model refer https://sling.apache.org/documentation/bundles/models.html#osgi-service-filters
 */

@Model(adaptables = SlingHttpServletRequest.class)
public class BootStrapTranslationConnectorModel {

    private static final Logger logger = LoggerFactory.getLogger(BootStrapTranslationConnectorModel.class);

    @Self
    private SlingHttpServletRequest request;

    private ResourceResolver resourceResolver;
    private String bootStrapConfigPath;
    private Resource bootStrapConfigResource;

    @PostConstruct
    public void postConstruct() throws Exception {
        bootStrapConfigPath = request.getRequestPathInfo().getSuffix();
        resourceResolver = request.getResourceResolver();
        bootStrapConfigResource = resourceResolver.getResource(bootStrapConfigPath);
    }

    /*
     *  Get the server url for the configuration
     */
    public String getServerUrl() {
        return BootStrapModelUtils.getStringPropertyFromContent(bootStrapConfigResource, BootstrapTranslationCloudConfig.PROPERTY_DUMMY_SERVER_URL, logger);
    }

    public String getServiceId() {
        return BootStrapModelUtils.getStringPropertyFromContent(bootStrapConfigResource, BootstrapTranslationCloudConfig.PROPERTY_DUMMY_CONFIG_ID, logger);
    }

    public String getPreviewDirectory() {
        return BootStrapModelUtils.getStringPropertyFromContent(bootStrapConfigResource, BootstrapTranslationCloudConfig.PROPERTY_PREVIEW_PATH, logger);
    }

    /*
     *  form action attribute (post path where the configuration input values would be saved), jcr:content node of the configuration for bootstrap
     */
    public String getFormPostPath() {
        return bootStrapConfigPath + '/' + JcrConstants.JCR_CONTENT;
    }

}
