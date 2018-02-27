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

import org.apache.jackrabbit.JcrConstants;
import org.apache.sling.api.resource.Resource;
import org.slf4j.Logger;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

/**
 * Utility class for Bootstrap connector sling model
 */

public class BootStrapModelUtils {

    // Helper for fetching properties from content node
    static String getStringPropertyFromContent(Resource resource, String property, Logger logger) {
        try {
            if (resource != null) {
                Resource contentResource = resource.getChild(JcrConstants.JCR_CONTENT);
                if (contentResource != null) {
                    Node content = contentResource.adaptTo(Node.class);
                    if (content != null && content.hasProperty(property)) {
                        return content.getProperty(property).getString();
                    }
                }
            }
        } catch (RepositoryException e) {
            logger.error("Error fetching Property {} from {}", property, resource.getPath());
        }
        return "";
    }
}
