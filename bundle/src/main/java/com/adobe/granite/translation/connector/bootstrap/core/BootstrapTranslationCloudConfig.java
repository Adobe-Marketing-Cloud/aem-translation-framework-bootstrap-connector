/*
*************************************************************************
ADOBE SYSTEMS INCORPORATED
Copyright [first year code created] Adobe Systems Incorporated
All Rights Reserved.
 
NOTICE:  Adobe permits you to use, modify, and distribute this file in accordance with the
terms of the Adobe license agreement accompanying it.  If you have received this file from a
source other than Adobe, then your use, modification, or distribution of it requires the prior
written permission of Adobe.
*************************************************************************
 */

package com.adobe.granite.translation.connector.bootstrap.core;

public interface BootstrapTranslationCloudConfig {

    public static final String PROPERTY_DUMMY_SERVER_URL = "dummyserverurl";
    public static final String PROPERTY_DUMMY_CONFIG_ID = "dummyconfigid";
    public static final String PROPERTY_PREVIEW_PATH = "previewPath";

    public static final String RESOURCE_TYPE = "bootstrap-connector/components/bootstrap-connector-cloudconfig";
    public static final String ROOT_PATH = "/etc/cloudservices/bootstrap-translation";

    String getDummyServerUrl();

    String getDummyConfigId();
   
    String getPreviewPath();
}