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

package com.adobe.granite.translation.connector.bootstrap.core.impl.config;

import com.adobe.granite.translation.api.TranslationException;
import com.adobe.granite.translation.connector.bootstrap.core.BootstrapTranslationCloudConfig;
import com.adobe.granite.translation.core.TranslationCloudConfigUtil;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.adapter.AdapterFactory;
import org.apache.sling.api.resource.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;

@Component
@Service(value = AdapterFactory.class)
public class BootstrapTranslationAdapterFactory implements AdapterFactory
{
    @Reference
    TranslationCloudConfigUtil cloudConfigUtil;

    private final Logger log = LoggerFactory.getLogger(getClass());

    private static final Class<Resource> RESOURCE_CLASS = Resource.class;
    private static final Class<Node> NODE_CLASS = Node.class;
    private static final Class<BootstrapTranslationCloudConfig> TRANSLATION_CLOUD_CONFIG_CLASS =
        BootstrapTranslationCloudConfig.class;

    @Property(name = "adapters")
    protected static final String[] ADAPTER_CLASSES = {TRANSLATION_CLOUD_CONFIG_CLASS.getName()};

    @Property(name = "adaptables")
    protected static final String[] ADAPTABLE_CLASSES = {RESOURCE_CLASS.getName(), NODE_CLASS.getName()};

    // ---------- AdapterFactory -----------------------------------------------

    public <AdapterType> AdapterType getAdapter(Object adaptable, Class<AdapterType> type)
    {
        log.trace("In function: getAdapter(Object,Claass<AdapterType>");

        if (adaptable instanceof Resource) {
            return getAdapter((Resource) adaptable, type);
        }
        if (adaptable instanceof Node) {
            return getAdapter((Node) adaptable, type);
        }
        log.debug("Unable to handle adaptable {}", adaptable.getClass().getName());
        return null;
    }

    public void setTranslationCloudConfigUtil(TranslationCloudConfigUtil configUtil)
    {
        cloudConfigUtil = configUtil;
    }

    /*
     * Adapter for Resource
     */
    @SuppressWarnings("unchecked")
    private <AdapterType> AdapterType getAdapter(Resource resource, Class<AdapterType> type)
    {
        log.trace("In function: getAdapter(Resource,Class<AdapterType>");

        if (type == TRANSLATION_CLOUD_CONFIG_CLASS
                && cloudConfigUtil.isCloudConfigAppliedOnImmediateResource(resource,
                    BootstrapTranslationCloudConfig.RESOURCE_TYPE))
        {
            try
            {
                return (AdapterType) new BootstrapTranslationCloudConfigImpl(resource);
            }
            catch (TranslationException te)
            {
                log.info(te.getMessage(), te);
                return null;
            }
        }

        log.debug("Unable to adapt to resource of type {}", type.getName());
        return null;
    }
}