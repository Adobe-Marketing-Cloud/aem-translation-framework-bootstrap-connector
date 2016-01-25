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

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.jcr.Binary;
import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.ValueFactory;

import org.apache.jackrabbit.JcrConstants;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.jcr.resource.JcrResourceUtil;
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
    
    private static final String TAG_METADATA = "/tag-metadata";
    
    private static final String ASSET_METADATA = "/asset-metadata";

    private static final String I18NCOMPONENTSTRINGDICT = "/i18n-dictionary";
    private static final String SERVICE_LABEL = "bootstrap";
    private static final String SERVICE_ATTRIBUTION = "Translation By Bootstrap";
    private String dummyConfigId = "";
    private String dummyServerUrl = "";
    private String previewPath = "";
    private final static String BOOTSTRAP_SERVICE = "bootstrap-service";
    Session session = null;

    class TranslationJobDetails {
        String strName;
        String strDescprition;
        String strSourceLang;
        String strDestinationLang;
    };

    class TranslationScopeImpl implements TranslationScope {
        @Override
        public int getWordCount() {
            log.trace("TranslationScopeImpl.getWordCount");

            Random rand = new Random();
            return rand.nextInt(100);
        }

        @Override
        public int getImageCount() {
            log.trace("TranslationScopeImpl.getImageCount");

            Random rand = new Random();
            return rand.nextInt(100);
        }

        @Override
        public int getVideoCount() {
            log.trace("TranslationScopeImpl.getVideoCount");

            Random rand = new Random();
            return rand.nextInt(100);
        }

        @Override
        public Map<String, String> getFinalScope() {
            log.trace("TranslationScopeImpl.getFinalScope");

            Map<String, String> newScope = new LinkedHashMap<String, String>();
            newScope.put("ICE Words", "0");
            newScope.put("100% Words", "0");
            newScope.put("New Words", "85");
            newScope.put("Repeated Words", "0");
            newScope.put("TranslationScope:CostEstimate", "USD 150.00");
            newScope.put("TranslationScope:DetailsLink","https://github.com/Adobe-Marketing-Cloud/aem-translation-framework-bootstrap-connector");

            return newScope;
        }
    }

    // Constructor
    public BootstrapTranslationServiceImpl(Map<String, String> availableLanguageMap,
        Map<String, String> availableCategoryMap, String name, String dummyConfigId, String dummyServerUrl, String previewPath,
        TranslationConfig translationConfig, ResourceResolverFactory resourceResolverFactory) {
        super(availableLanguageMap, availableCategoryMap, name, SERVICE_LABEL, SERVICE_ATTRIBUTION,
            BootstrapTranslationCloudConfigImpl.ROOT_PATH, TranslationMethod.MACHINE_TRANSLATION, translationConfig);

        log.trace("BootstrapTranslationServiceImpl.");
        log.trace("dummyConfigId: " + dummyConfigId);
        log.trace("dummyServerUrl: " + dummyServerUrl);
        log.trace("previewPath: " + previewPath);


        try {
            ResourceResolver resolver = resourceResolverFactory.getServiceResourceResolver(
                    Collections.singletonMap(ResourceResolverFactory.SUBSERVICE,
                            (Object) BOOTSTRAP_SERVICE));
            session = resolver.adaptTo(Session.class);
        } catch (LoginException e) {
            log.error("Login Exception",e);
        }
           
        this.dummyConfigId = dummyConfigId;
        this.dummyServerUrl = dummyServerUrl;
        this.previewPath = previewPath;
    }

    @Override
    public Map<String, String> supportedLanguages() {
        log.trace("BootstrapTranslationServiceImpl.supportedLanguages");

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
        log.trace("BootstrapTranslationServiceImpl.isDirectionSupported");
        // It should return true, if translation provider provides translation from sourceLanguage to targetLanguage
// otherwise false
        return true;
    }

    @Override
    public String detectLanguage(String toDetectSource, TranslationConstants.ContentType contentType)
        throws TranslationException {
        log.trace("BootstrapTranslationServiceImpl.detectLanguage");

        // French language code
        return "fr";
    }

    @Override
    public TranslationResult translateString(String sourceString, String sourceLanguage, String targetLanguage,
        TranslationConstants.ContentType contentType, String contentCategory) throws TranslationException {
        log.trace("BootstrapTranslationServiceImpl.translateString");

        String translatedString = new StringBuilder(sourceString).reverse().toString();
        return new TranslationResultImpl(translatedString, sourceLanguage, targetLanguage, contentType,
            contentCategory, sourceString, TranslationResultImpl.UNKNOWN_RATING, null);
    }

    @Override
    public TranslationResult[] translateArray(String[] sourceStringArr, String sourceLanguage, String targetLanguage,
        TranslationConstants.ContentType contentType, String contentCategory) throws TranslationException {
        log.trace("BootstrapTranslationServiceImpl.translateArray");

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
        log.trace("BootstrapTranslationServiceImpl.getAllStoredTranslations");

        throw new TranslationException("This function is not implemented",
            TranslationException.ErrorCode.SERVICE_NOT_IMPLEMENTED);
    }

    @Override
    public void storeTranslation(String[] originalText, String sourceLanguage, String targetLanguage,
        String[] updatedTranslation, TranslationConstants.ContentType contentType, String contentCategory,
        String userId, int rating, String path) throws TranslationException {
        log.trace("BootstrapTranslationServiceImpl.storeTranslation");

        throw new TranslationException("This function is not implemented",
            TranslationException.ErrorCode.SERVICE_NOT_IMPLEMENTED);
    }

    @Override
    public void storeTranslation(String originalText, String sourceLanguage, String targetLanguage,
        String updatedTranslation, TranslationConstants.ContentType contentType, String contentCategory,
        String userId, int rating, String path) throws TranslationException {
        log.trace("BootstrapTranslationServiceImpl.storeTranslation");

        throw new TranslationException("This function is not implemented",
            TranslationException.ErrorCode.SERVICE_NOT_IMPLEMENTED);
    }

    @Override
    public String createTranslationJob(String name, String description, String strSourceLanguage,
        String strTargetLanguage, Date dueDate, TranslationState state, TranslationMetadata jobMetadata)
        throws TranslationException {
        log.trace("BootstrapTranslationServiceImpl.createTranslationJob");
        // Just cleaning up the name to remove the extra spaces and square brackets
        name = name.toLowerCase().replaceAll("\\s", "-").replaceAll("\\[.*\\]_", "");
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd'/"+name+"'");
        Date date = new Date();
        String translationJobName = "/var/bootstrap-tms/"+formatter.format(date);
        log.debug("Job Name: {}", translationJobName);
        Node jcrNode;
        try {
            jcrNode = JcrResourceUtil.createPath(translationJobName, "sling:Folder", JcrConstants.NT_UNSTRUCTURED, session, false);
            if(dueDate !=null) {
                JcrResourceUtil.setProperty(jcrNode, "DUE_DATE", dueDate.toString());    
            }
            session.save();
        } catch (RepositoryException e) {
            log.error("Repository Exception",e);
        }
        return translationJobName;
        
    }

    @Override
    public TranslationScope getFinalScope(String strTranslationJobID) throws TranslationException {
        log.trace("BootstrapTranslationServiceImpl.getFinalScope");

        return new TranslationScopeImpl();
    }

    @Override
    public TranslationStatus updateTranslationJobState(String strTranslationJobID, TranslationState state)
        throws TranslationException {
        return state.getStatus();
    }

    @Override
    public TranslationStatus getTranslationJobStatus(String strTranslationJobID) throws TranslationException {
        log.trace("BootstrapTranslationServiceImpl.getTranslationJobStatus");
        
        try {
            Node jobNode = session.getNode(strTranslationJobID);
            log.debug("STATUS: {}",jobNode.getProperty("STATUS"));
        } catch (PathNotFoundException e) {
            e.printStackTrace();
        } catch (RepositoryException e) {
            e.printStackTrace();
        }
        
        return TranslationStatus.SCOPE_COMPLETED;        
        
    }

    @Override
    public CommentCollection<Comment> getTranslationJobCommentCollection(String strTranslationJobID) {
        log.trace("BootstrapTranslationServiceImpl.getTranslationJobCommentCollection");
        return null;
    }

    @Override
    public void addTranslationJobComment(String strTranslationJobID, Comment comment) throws TranslationException {
        log.trace("BootstrapTranslationServiceImpl.addTranslationJobComment");

        throw new TranslationException("This function is not implemented",
            TranslationException.ErrorCode.SERVICE_NOT_IMPLEMENTED);
    }

    @Override
    public InputStream getTranslatedObject(String strTranslationJobID, TranslationObject translationObj)
        throws TranslationException {
        log.trace("BootstrapTranslationServiceImpl.getTranslatedObject");
        throw new TranslationException("This function is not implemented",
            TranslationException.ErrorCode.SERVICE_NOT_IMPLEMENTED);
    }

    @Override
    public String uploadTranslationObject(String strTranslationJobID, TranslationObject translationObject)
        throws TranslationException {

        String objectPath = strTranslationJobID+getObjectPath(translationObject);
        Node jcrNode;
        try {
            log.debug("ObjectPath: {}",objectPath);
            jcrNode = JcrResourceUtil.createPath(objectPath, JcrConstants.NT_UNSTRUCTURED, JcrConstants.NT_UNSTRUCTURED, session, false);
            JcrResourceUtil.setProperty(jcrNode, "STATUS", TranslationStatus.TRANSLATION_IN_PROGRESS.toString());
            
            InputStream inputStream = null;
            if(translationObject.getMimeType().startsWith("text")){
            	inputStream = translationObject.getTranslationObjectXMLInputStream();
            }else {
            	// For Binary assets, use the XLIFFInputStream v 2.0
            	inputStream = translationObject.getTranslationObjectXLIFFInputStream("2.0");
            }
            
            if(inputStream!=null) {
            	ValueFactory valueFactory = session.getValueFactory();               
                Binary contentValue = valueFactory.createBinary(inputStream);
                Node contentNode = jcrNode.addNode(JcrConstants.JCR_CONTENT, JcrConstants.NT_RESOURCE);
                contentNode.setProperty(JcrConstants.JCR_DATA, contentValue);
            }
            session.save();
        } catch (RepositoryException e) {
            log.error("Repository Exception",e);
        }
        
        return objectPath; 
    }

    @Override
    public TranslationStatus updateTranslationObjectState(String strTranslationJobID,
        TranslationObject translationObject, TranslationState state) throws TranslationException {
        log.trace("BootstrapTranslationServiceImpl.updateTranslationObjectState");
        return state.getStatus();
    }

    @Override
    public TranslationStatus getTranslationObjectStatus(String strTranslationJobID,
        TranslationObject translationObject) throws TranslationException {

        String objectPath = strTranslationJobID+getObjectPath(translationObject);
        String status = "";
        try {
            status = session.getNode(objectPath).getProperty("STATUS").getString();
        } catch (PathNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (RepositoryException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();

        }
            return TranslationConstants.TranslationStatus.fromString(status);
    }

    @Override
    public TranslationStatus[] updateTranslationObjectsState(String strTranslationJobID,
        TranslationObject[] translationObjects, TranslationState[] states) throws TranslationException {
        log.trace("BootstrapTranslationServiceImpl.updateTranslationObjectsState");

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
        log.trace("BootstrapTranslationServiceImpl.getTranslationObjectsStatus");

        TranslationStatus[] retStatus = new TranslationStatus[translationObjects.length];
        for (int index = 0; index < translationObjects.length; index++) {
            retStatus[index] = getTranslationObjectStatus(strTranslationJobID, translationObjects[index]);
        }
        return retStatus;
    }

    @Override
    public CommentCollection<Comment> getTranslationObjectCommentCollection(String strTranslationJobID,
        TranslationObject translationObject) throws TranslationException {
        log.trace("BootstrapTranslationServiceImpl.getTranslationObjectCommentCollection");

        throw new TranslationException("This function is not implemented",
            TranslationException.ErrorCode.SERVICE_NOT_IMPLEMENTED);
    }

    @Override
    public void addTranslationObjectComment(String strTranslationJobID, TranslationObject translationObject,
        Comment comment) throws TranslationException {
        log.trace("BootstrapTranslationServiceImpl.addTranslationObjectComment");

        throw new TranslationException("This function is not implemented",
            TranslationException.ErrorCode.SERVICE_NOT_IMPLEMENTED);
    }

    @Override
    public void updateTranslationJobMetadata(String strTranslationJobID, TranslationMetadata jobMetadata,
        TranslationMethod translationMethod) throws TranslationException {
        log.trace("BootstrapTranslationServiceImpl.updateTranslationJobMetadata");

        throw new TranslationException("This function is not implemented",
            TranslationException.ErrorCode.SERVICE_NOT_IMPLEMENTED);
    }

    
    private String getObjectPath (TranslationObject translationObject){
        
        if(translationObject.getTranslationObjectSourcePath()!= null && !translationObject.getTranslationObjectSourcePath().isEmpty()){
            return  translationObject.getTranslationObjectSourcePath();
        }
        else if(translationObject.getTitle().equals("TAGMETADATA")){
            return TAG_METADATA;
        }
        else if(translationObject.getTitle().equals("ASSETMETADATA")){
            return ASSET_METADATA;
        } 
        else if(translationObject.getTitle().equals("I18NCOMPONENTSTRINGDICT")){
            return I18NCOMPONENTSTRINGDICT;
        }
        return null;
    }    

    @Override
    public void updateDueDate(String strTranslationJobID, Date date)
            throws TranslationException {
            log.debug("NEW DUE DATE:{}",date);
    }
    
    private static void writePreview(TranslationObject translationObject, String strOutput) throws IOException  {

           ZipInputStream zis = translationObject.getTranslationObjectPreview();
           
           ZipEntry entry;
           while ((entry = zis.getNextEntry()) != null) {
               log.debug("Unzipping: {}", entry.getName());

               int size;
               byte[] buffer = new byte[2048];
               log.debug("ENTRY NAME: {}", entry.getName());
               FileOutputStream fos = new FileOutputStream(entry.getName());
               BufferedOutputStream bos = new BufferedOutputStream(fos, buffer.length);
               while ((size = zis.read(buffer, 0, buffer.length)) != -1) {
                 bos.write(buffer, 0, size);
               }
               bos.flush();
               bos.close();
             }
           zis.close();
    }
    
}