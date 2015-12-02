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

import java.io.InputStream;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.granite.comments.Comment;
import com.adobe.granite.comments.CommentCollection;
import com.adobe.granite.translation.api.TranslationConfig;
import com.adobe.granite.translation.api.TranslationConstants;
import com.adobe.granite.translation.api.TranslationConstants.TranslationMethod;
import com.adobe.granite.translation.api.TranslationConstants.TranslationStatus;
import com.adobe.granite.translation.api.TranslationException;
import com.adobe.granite.translation.api.TranslationMetadata;
import com.adobe.granite.translation.api.TranslationObject;
import com.adobe.granite.translation.api.TranslationResult;
import com.adobe.granite.translation.api.TranslationScope;
import com.adobe.granite.translation.api.TranslationService;
import com.adobe.granite.translation.api.TranslationState;
import com.adobe.granite.translation.connector.bootstrap.core.impl.config.BootstrapTranslationCloudConfigImpl;
import com.adobe.granite.translation.core.common.AbstractTranslationService;
import com.adobe.granite.translation.core.common.TranslationResultImpl;


public class BootstrapTranslationServiceImpl extends AbstractTranslationService implements TranslationService {
    private static final Logger log = LoggerFactory.getLogger(BootstrapTranslationServiceImpl.class);

    private static final String SERVICE_LABEL = "bootstrap";
    private static final String SERVICE_ATTRIBUTION = "Translation By Bootstrap";
    private String dummyConfigId = "";
    private String dummyServerUrl = "";
    

    class TranslationJobDetails {
        String strName;
        String strDescprition;
        String strSourceLang;
        String strDestinationLang;
    };

    class TranslationScopeImpl implements TranslationScope {
        @Override
        public int getWordCount() {
            log.info("TranslationScopeImpl.getWordCount");

            Random rand = new Random();
            return rand.nextInt(100);
        }

        @Override
        public int getImageCount() {
            log.info("TranslationScopeImpl.getImageCount");

            Random rand = new Random();
            return rand.nextInt(100);
        }

        @Override
        public int getVideoCount() {
            log.info("TranslationScopeImpl.getVideoCount");

            Random rand = new Random();
            return rand.nextInt(100);
        }

        @Override
        public Map<String, String> getFinalScope() {
            log.info("TranslationScopeImpl.getFinalScope");

            Map<String, String> newScope = new HashMap<String, String>();
            newScope.put("asdasd", "asdasdasddasdasd");
            newScope.put("souosj", "sfhas hldg lkdsjg");
            newScope.put("12fska f", "asjfa slkfjkasjfkl ajdgk;");
            return newScope;
        }
    }

    // Constructor
    public BootstrapTranslationServiceImpl(Map<String, String> availableLanguageMap,
        Map<String, String> availableCategoryMap, String name, String dummyConfigId, String dummyServerUrl,
        TranslationConfig translationConfig) {
        super(availableLanguageMap, availableCategoryMap, name, SERVICE_LABEL, SERVICE_ATTRIBUTION,
            BootstrapTranslationCloudConfigImpl.ROOT_PATH, TranslationMethod.MACHINE_TRANSLATION, translationConfig);

        log.info("BootstrapTranslationServiceImpl.");
        log.info("dummyConfigId: " + dummyConfigId);
        log.info("dummyServerUrl: " + dummyServerUrl);

        this.dummyConfigId = dummyConfigId;
        this.dummyServerUrl = dummyServerUrl;
    }

    @Override
    public Map<String, String> supportedLanguages() {
        log.info("BootstrapTranslationServiceImpl.supportedLanguages");

        if (availableLanguageMap.size() <= 0) {
            availableLanguageMap.put("en", "en");
            availableLanguageMap.put("de", "de");
            availableLanguageMap.put("fr", "fr");
            availableLanguageMap.put("ja", "ja");
            availableLanguageMap.put("ko", "ko");
        }

        return Collections.unmodifiableMap(availableLanguageMap);
    }

    @Override
    public boolean isDirectionSupported(String sourceLanguage, String targetLanguage) throws TranslationException {
        log.info("BootstrapTranslationServiceImpl.isDirectionSupported");
        // It should return true, if translation provider provides translation from sourceLanguage to targetLanguage
// otherwise false
        return true;
    }

    @Override
    public String detectLanguage(String toDetectSource, TranslationConstants.ContentType contentType)
        throws TranslationException {
        log.info("BootstrapTranslationServiceImpl.detectLanguage");

        // French language code
        return "fr";
    }

    @Override
    public TranslationResult translateString(String sourceString, String sourceLanguage, String targetLanguage,
        TranslationConstants.ContentType contentType, String contentCategory) throws TranslationException {
        log.info("BootstrapTranslationServiceImpl.translateString");

        String translatedString = new StringBuilder(sourceString).reverse().toString();
        return new TranslationResultImpl(translatedString, sourceLanguage, targetLanguage, contentType,
            contentCategory, sourceString, TranslationResultImpl.UNKNOWN_RATING, null);
    }

    @Override
    public TranslationResult[] translateArray(String[] sourceStringArr, String sourceLanguage, String targetLanguage,
        TranslationConstants.ContentType contentType, String contentCategory) throws TranslationException {
        log.info("BootstrapTranslationServiceImpl.translateArray");

        TranslationResult arrResults[] = new TranslationResultImpl[sourceStringArr.length];
        for (int i = 0; i < sourceStringArr.length; i++) {
            arrResults[i] =
                translateString(sourceStringArr[i], sourceLanguage, targetLanguage, contentType, contentCategory);
        }
        return arrResults;
    }

    @Override
    public TranslationResult[] getAllStoredTranslations(String sourceString, String sourceLanguage,
        String targetLanguage, TranslationConstants.ContentType contentType, String contentCategory, String userId,
        int maxTranslations) throws TranslationException {
        log.info("BootstrapTranslationServiceImpl.getAllStoredTranslations");

        throw new TranslationException("This function is not implemented",
            TranslationException.ErrorCode.SERVICE_NOT_IMPLEMENTED);
    }

    @Override
    public void storeTranslation(String[] originalText, String sourceLanguage, String targetLanguage,
        String[] updatedTranslation, TranslationConstants.ContentType contentType, String contentCategory,
        String userId, int rating, String path) throws TranslationException {
        log.info("BootstrapTranslationServiceImpl.storeTranslation");

        throw new TranslationException("This function is not implemented",
            TranslationException.ErrorCode.SERVICE_NOT_IMPLEMENTED);
    }

    @Override
    public void storeTranslation(String originalText, String sourceLanguage, String targetLanguage,
        String updatedTranslation, TranslationConstants.ContentType contentType, String contentCategory,
        String userId, int rating, String path) throws TranslationException {
        log.info("BootstrapTranslationServiceImpl.storeTranslation");

        throw new TranslationException("This function is not implemented",
            TranslationException.ErrorCode.SERVICE_NOT_IMPLEMENTED);
    }

    @Override
    public String createTranslationJob(String name, String description, String strSourceLanguage,
        String strTargetLanguage, Date dueDate, TranslationState state, TranslationMetadata jobMetadata)
        throws TranslationException {
        log.info("BootstrapTranslationServiceImpl.createTranslationJob");

        return "sampleTranslationJob";
    }

    @Override
    public TranslationScope getFinalScope(String strTranslationJobID) throws TranslationException {
        log.info("BootstrapTranslationServiceImpl.getFinalScope");

        return new TranslationScopeImpl();
    }

    @Override
    public TranslationStatus updateTranslationJobState(String strTranslationJobID, TranslationState state)
        throws TranslationException {
        return state.getStatus();
    }

    @Override
    public TranslationStatus getTranslationJobStatus(String strTranslationJobID) throws TranslationException {
        log.info("BootstrapTranslationServiceImpl.getTranslationJobStatus");
        throw new TranslationException("This function is not implemented",
            TranslationException.ErrorCode.SERVICE_NOT_IMPLEMENTED);
    }

    @Override
    public CommentCollection<Comment> getTranslationJobCommentCollection(String strTranslationJobID) {
        log.info("BootstrapTranslationServiceImpl.getTranslationJobCommentCollection");
        return null;
    }

    @Override
    public void addTranslationJobComment(String strTranslationJobID, Comment comment) throws TranslationException {
        log.info("BootstrapTranslationServiceImpl.addTranslationJobComment");

        throw new TranslationException("This function is not implemented",
            TranslationException.ErrorCode.SERVICE_NOT_IMPLEMENTED);
    }

    @Override
    public InputStream getTranslatedObject(String strTranslationJobID, TranslationObject translationObj)
        throws TranslationException {
        log.info("BootstrapTranslationServiceImpl.getTranslatedObject");
        throw new TranslationException("This function is not implemented",
            TranslationException.ErrorCode.SERVICE_NOT_IMPLEMENTED);
    }

    @Override
    public String uploadTranslationObject(String strTranslationJobID, TranslationObject translationObject)
        throws TranslationException {
        log.info("BootstrapTranslationServiceImpl.uploadTranslationObject");
        throw new TranslationException("This function is not implemented",
            TranslationException.ErrorCode.SERVICE_NOT_IMPLEMENTED);

    }

    @Override
    public TranslationStatus updateTranslationObjectState(String strTranslationJobID,
        TranslationObject translationObject, TranslationState state) throws TranslationException {
        log.info("BootstrapTranslationServiceImpl.updateTranslationObjectState");
        return state.getStatus();
    }

    @Override
    public TranslationStatus getTranslationObjectStatus(String strTranslationJobID,
        TranslationObject translationObject) throws TranslationException {
        throw new TranslationException("This function is not implemented",
            TranslationException.ErrorCode.SERVICE_NOT_IMPLEMENTED);
    }

    @Override
    public TranslationStatus[] updateTranslationObjectsState(String strTranslationJobID,
        TranslationObject[] translationObjects, TranslationState[] states) throws TranslationException {
        log.info("BootstrapTranslationServiceImpl.updateTranslationObjectsState");

        TranslationStatus[] retStatus = new TranslationStatus[states.length];
        for (int index = 0; index < states.length; index++) {
            retStatus[index] =
                updateTranslationObjectState(strTranslationJobID, translationObjects[index], states[index]);
        }
        return retStatus;
    }

    @Override
    public TranslationStatus[] getTranslationObjectsStatus(String strTranslationJobID,
        TranslationObject[] translationObjects) throws TranslationException {
        log.info("BootstrapTranslationServiceImpl.getTranslationObjectsStatus");

        TranslationStatus[] retStatus = new TranslationStatus[translationObjects.length];
        for (int index = 0; index < translationObjects.length; index++) {
            retStatus[index] = getTranslationObjectStatus(strTranslationJobID, translationObjects[index]);
        }
        return retStatus;
    }

    @Override
    public CommentCollection<Comment> getTranslationObjectCommentCollection(String strTranslationJobID,
        TranslationObject translationObject) throws TranslationException {
        log.info("BootstrapTranslationServiceImpl.getTranslationObjectCommentCollection");

        throw new TranslationException("This function is not implemented",
            TranslationException.ErrorCode.SERVICE_NOT_IMPLEMENTED);
    }

    @Override
    public void addTranslationObjectComment(String strTranslationJobID, TranslationObject translationObject,
        Comment comment) throws TranslationException {
        log.info("BootstrapTranslationServiceImpl.addTranslationObjectComment");

        throw new TranslationException("This function is not implemented",
            TranslationException.ErrorCode.SERVICE_NOT_IMPLEMENTED);
    }

    @Override
    public void updateTranslationJobMetadata(String strTranslationJobID, TranslationMetadata jobMetadata,
        TranslationMethod translationMethod) throws TranslationException {
        log.info("BootstrapTranslationServiceImpl.updateTranslationJobMetadata");

        throw new TranslationException("This function is not implemented",
            TranslationException.ErrorCode.SERVICE_NOT_IMPLEMENTED);
    }
}