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

package com.adobe.granite.translation.connector.bootstrap.core.impl;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.commons.osgi.PropertiesUtil;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.granite.crypto.CryptoException;
import com.adobe.granite.crypto.CryptoSupport;
import com.adobe.granite.translation.api.TranslationConfig;
import com.adobe.granite.translation.api.TranslationConstants.TranslationMethod;
import com.adobe.granite.translation.api.TranslationException;
import com.adobe.granite.translation.api.TranslationService;
import com.adobe.granite.translation.api.TranslationServiceFactory;
import com.adobe.granite.translation.connector.bootstrap.core.BootstrapTranslationCloudConfig;
import com.adobe.granite.translation.core.TranslationCloudConfigUtil;

@Service
@Component(label = "Bootstrap Translation Connector Factory", metatype = true, immediate = true)
@Properties(value = {
    @Property(name = "service.description", value = "Bootstrap translation service"),
    @Property(name = TranslationServiceFactory.PROPERTY_TRANSLATION_FACTORY, value = "Bootstrap Connector",
            label = "Bootstrap Translation Factory Name", description = "The Unique ID associated with this "
                    + "Translation Factory Connector")})
public class BootstrapTranslationServiceFactoryImpl implements TranslationServiceFactory {

    protected String factoryName;

    @Reference
    TranslationCloudConfigUtil cloudConfigUtil;

    @Reference
    TranslationConfig translationConfig;
    
    @Reference
    CryptoSupport cryptoSupport;
    
    @Reference
    ResourceResolverFactory resourceResolverFactory;

    private List<TranslationMethod> supportedTranslationMethods;

    public BootstrapTranslationServiceFactoryImpl()
    {
        log.trace("BootstrapTranslationServiceFactoryImpl.");

        supportedTranslationMethods = new ArrayList<TranslationMethod>();
        supportedTranslationMethods.add(TranslationMethod.HUMAN_TRANSLATION);
        supportedTranslationMethods.add(TranslationMethod.MACHINE_TRANSLATION);
    }

    private static final Logger log = LoggerFactory.getLogger(BootstrapTranslationServiceFactoryImpl.class);

    @Override
    public TranslationService createTranslationService(TranslationMethod translationMethod, String cloudConfigPath)
        throws TranslationException
    {
        log.trace("BootstrapTranslationServiceFactoryImpl.createTranslationService");

        BootstrapTranslationCloudConfig bootstrapCloudConfg =
            (BootstrapTranslationCloudConfig) cloudConfigUtil.getCloudConfigObjectFromPath(
                    BootstrapTranslationCloudConfig.class, cloudConfigPath);

        String dummyConfigId = "";
        String dummyServerUrl = "";
        String previewPath = "";
        
        if (bootstrapCloudConfg != null)
        {
            dummyConfigId = bootstrapCloudConfg.getDummyConfigId();
            dummyServerUrl = bootstrapCloudConfg.getDummyServerUrl();
            previewPath = bootstrapCloudConfg.getPreviewPath();
            
        }
        
        if (cryptoSupport != null) {
            try {
                if(cryptoSupport.isProtected(dummyConfigId)) {
                    dummyConfigId=cryptoSupport.unprotect(dummyConfigId);
                }else {
                    log.trace("Dummy Config ID is not protected");
                }
            }catch (CryptoException e) {
                log.error("Error while decrypting the client secret {}", e);
            }
        }

        Map<String, String> availableLanguageMap = new HashMap<String, String>();
        Map<String, String> availableCategoryMap = new HashMap<String, String>();
        return new BootstrapTranslationServiceImpl(availableLanguageMap, availableCategoryMap, factoryName, dummyConfigId, dummyServerUrl, previewPath,
             translationConfig, resourceResolverFactory);
    }

    @Override
    public List<TranslationMethod> getSupportedTranslationMethods() {
        log.trace("BootstrapTranslationServiceFactoryImpl.getSupportedTranslationMethods");
        return supportedTranslationMethods;
    }

    @Override
    public Class<?> getServiceCloudConfigClass() {
        log.trace("BootstrapTranslationServiceFactoryImpl.getServiceCloudConfigClass");
        return BootstrapTranslationCloudConfig.class;
    }

    protected void activate(final ComponentContext ctx) {
        log.trace("Starting function: activate");
        final Dictionary<?, ?> properties = ctx.getProperties();

        factoryName =
            PropertiesUtil.toString(properties.get(TranslationServiceFactory.PROPERTY_TRANSLATION_FACTORY),"");

        if (log.isTraceEnabled()) {
            log.trace("Activated TSF with the following:");
            log.trace("Factory Name: {}", factoryName);
        }
    }
    
    @Override
    public String getServiceFactoryName() {
        log.trace("Starting function: getServiceFactoryName");
        return factoryName;
    }
}