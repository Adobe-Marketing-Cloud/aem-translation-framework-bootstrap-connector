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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Collections;

import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.granite.crypto.CryptoException;
import com.adobe.granite.crypto.CryptoSupport;
import com.adobe.granite.translation.api.TranslationConfig;
import com.adobe.granite.translation.api.TranslationConstants.TranslationMethod;
import com.adobe.granite.translation.api.TranslationException;
import com.adobe.granite.translation.api.TranslationService;
import com.adobe.granite.translation.api.TranslationServiceFactory;
import com.adobe.granite.translation.bootstrap.tms.core.BootstrapTmsService;
import com.adobe.granite.translation.connector.bootstrap.core.BootstrapTranslationCloudConfig;
import com.adobe.granite.translation.connector.bootstrap.core.impl.config.BootstrapTranslationCloudConfigImpl;
import com.adobe.granite.translation.connector.bootstrap.core.impl.LiltApiClient;
import com.adobe.granite.translation.core.TranslationCloudConfigUtil;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.LoginException;

@Component(service = TranslationServiceFactory.class, immediate = true, configurationPid = "com.adobe.granite.translation.connector.bootstrap.core.impl.BootstrapTranslationServiceFactoryImpl", property = {
		Constants.SERVICE_DESCRIPTION + "=Configurable settings for the Bootstrap Translation connector",
		"label" + "=Bootstrap Translation Connector Factory" })

@Designate(ocd=BootstrapServiceConfiguration.class)
public class BootstrapTranslationServiceFactoryImpl implements TranslationServiceFactory {

	protected String factoryName;

	protected Boolean isPreviewEnabled;

	protected Boolean isPseudoLocalizationDisabled;

	protected String exportFormat;

	@Reference
	TranslationCloudConfigUtil cloudConfigUtil;

	@Reference
	TranslationConfig translationConfig;

	@Reference
	CryptoSupport cryptoSupport;

	@Reference
	private ResourceResolverFactory resolverFactory;

	@Reference
	BootstrapTmsService bootstrapTmsService;

	private List<TranslationMethod> supportedTranslationMethods;

	private BootstrapServiceConfiguration config;

	public BootstrapTranslationServiceFactoryImpl() {
		log.trace("BootstrapTranslationServiceFactoryImpl.");

		supportedTranslationMethods = new ArrayList<TranslationMethod>();
		supportedTranslationMethods.add(TranslationMethod.HUMAN_TRANSLATION);
	}

	private static final Logger log = LoggerFactory.getLogger(BootstrapTranslationServiceFactoryImpl.class);

	@Override
	public TranslationService createTranslationService(TranslationMethod translationMethod, String cloudConfigPath)
			throws TranslationException {
		log.trace("BootstrapTranslationServiceFactoryImpl.createTranslationService");


		BootstrapTranslationCloudConfig bootstrapCloudConfg = null;
		LiltApiClient liltApiClient = null;

		String liltConfigId = "";
		String liltServerUrl = "";
		String previewPath = "";

    try {
      Map<String, Object> param = new HashMap<String, Object>();
      param.put(ResourceResolverFactory.SUBSERVICE, "bootstrap-service");
      ResourceResolver resourceResolver = resolverFactory.getServiceResourceResolver(param);
      Resource res = resourceResolver.getResource(cloudConfigPath);
      bootstrapCloudConfg = (BootstrapTranslationCloudConfig) cloudConfigUtil
        .getCloudConfigObjectFromPath(res, BootstrapTranslationCloudConfig.class, cloudConfigPath);
      if (res != null) {
        bootstrapCloudConfg = new BootstrapTranslationCloudConfigImpl(res);
      }
    } catch (LoginException e) {
      log.error("Error while resolving config resource {}", e);
    }

		if (bootstrapCloudConfg != null) {
			liltConfigId = bootstrapCloudConfg.getLiltConfigId();
			liltServerUrl = bootstrapCloudConfg.getLiltServerUrl();
			previewPath = bootstrapCloudConfg.getPreviewPath();
			liltApiClient = new LiltApiClient(liltServerUrl, liltConfigId);
		}

		if (cryptoSupport != null) {
			try {
				if (cryptoSupport.isProtected(liltConfigId)) {
					liltConfigId = cryptoSupport.unprotect(liltConfigId);
				} else {
					log.trace("Lilt Config ID is not protected");
				}
			} catch (CryptoException e) {
				log.error("Error while decrypting the client secret {}", e);
			}
		}

		Map<String, String> availableLanguageMap = new HashMap<String, String>();
		Map<String, String> availableCategoryMap = new HashMap<String, String>();
		return new BootstrapTranslationServiceImpl(availableLanguageMap, availableCategoryMap, factoryName,
				isPreviewEnabled, isPseudoLocalizationDisabled, exportFormat, liltConfigId, liltServerUrl,
        previewPath, translationConfig, liltApiClient, cloudConfigPath);
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

	@Activate
	protected void activate(BootstrapServiceConfiguration config) {
		log.trace("Starting function: activate");

		this.config = config;

		factoryName = config.getTranslationFactory();

		isPreviewEnabled = config.isPreviewEnabled();

		isPseudoLocalizationDisabled = config.isPseudoLocalizationDisabled();

		exportFormat = config.getExportFormat();

		if (log.isTraceEnabled()) {
			log.trace("Activated TSF with the following:");
			log.trace("Factory Name: {}", factoryName);
			log.trace("Preview Enabled: {}", isPreviewEnabled);
			log.trace("Psuedo Localization Disabled: {}", isPseudoLocalizationDisabled);
			log.trace("Export Format: {}", exportFormat);
		}
	}

	@Override
	public String getServiceFactoryName() {
		log.trace("Starting function: getServiceFactoryName");
		return factoryName;
	}
}
