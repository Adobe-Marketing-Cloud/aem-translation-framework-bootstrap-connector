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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

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
import com.adobe.granite.translation.bootstrap.tms.core.BootstrapTmsConstants;
import com.adobe.granite.translation.bootstrap.tms.core.BootstrapTmsService;
import com.adobe.granite.translation.connector.bootstrap.core.impl.config.BootstrapTranslationCloudConfigImpl;
import com.adobe.granite.translation.connector.bootstrap.core.impl.LiltApiClient;
import com.adobe.granite.translation.connector.bootstrap.core.impl.LiltApiClient.SourceFile;
import com.adobe.granite.translation.core.common.AbstractTranslationService;
import com.adobe.granite.translation.core.common.TranslationResultImpl;

public class BootstrapTranslationServiceImpl extends AbstractTranslationService implements TranslationService {
    private static final Logger log = LoggerFactory.getLogger(BootstrapTranslationServiceImpl.class);
    
    private static final String TAG_METADATA = "/tag-metadata";
    
    private static final String ASSET_METADATA = "/asset-metadata";

    private static final String I18NCOMPONENTSTRINGDICT = "/i18n-dictionary";
    private static final String SERVICE_LABEL = "Lilt";
    private static final String SERVICE_ATTRIBUTION = "Translation By Lilt";
    private String liltConfigId = "";
    private String liltServerUrl = "";
    private String previewPath = "";
    private Boolean isPreviewEnabled = false;
    private Boolean isPseudoLocalizationDisabled = false;
    private String exportFormat = BootstrapConstants.EXPORT_FORMAT_XML;
    private String cloudConfigPath;
    private LiltApiClient liltApiClient;
    private final static String BOOTSTRAP_SERVICE = "bootstrap-service";


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
            newScope.put("New Words", Integer.toString(getWordCount()));
            newScope.put("Repeated Words", "0");
            newScope.put("TranslationScope:CostEstimate", "USD 73.00");
            newScope.put("TranslationScope:DetailsLink","https://github.com/Adobe-Marketing-Cloud/aem-translation-framework-bootstrap-connector");

            return newScope;
        }
    }

    // Constructor
    public BootstrapTranslationServiceImpl(Map<String, String> availableLanguageMap,
        Map<String, String> availableCategoryMap, String name, Boolean isPreviewEnabled, Boolean isPseudoLocalizationDisabled, String exportFormat, String liltConfigId, String liltServerUrl, String previewPath,
        TranslationConfig translationConfig, LiltApiClient liltApiClient, String cloudConfigPath) {
        super(availableLanguageMap, availableCategoryMap, name, SERVICE_LABEL, SERVICE_ATTRIBUTION,
            BootstrapTranslationCloudConfigImpl.ROOT_PATH, TranslationMethod.MACHINE_TRANSLATION, translationConfig);

        log.trace("BootstrapTranslationServiceImpl.");
        log.trace("liltConfigId: {}",liltConfigId);
        log.trace("liltServerUrl: {}",liltServerUrl);
        log.trace("previewPath: {}",previewPath);
        log.trace("isPreviewEnabled: {}",isPreviewEnabled);
        log.trace("isPseudoLocalizationDisabled: {}", isPseudoLocalizationDisabled);
        log.trace("exportFormat: {}",exportFormat);
        this.liltConfigId = liltConfigId;
        this.liltServerUrl = liltServerUrl;
        this.previewPath = previewPath;
        this.liltApiClient = liltApiClient;
        this.isPseudoLocalizationDisabled = isPseudoLocalizationDisabled;
        this.isPreviewEnabled = isPreviewEnabled;
        this.exportFormat = exportFormat;
        this.cloudConfigPath = cloudConfigPath;
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
        String translatedString = "";
        if(isPseudoLocalizationDisabled == true){
        	translatedString = String.format("%s_%s_%s", sourceLanguage, sourceString, targetLanguage);
        }else {
        	// Using Pseudo translation here using accented characters
        	// translatedString = bootstrapTmsService.getAccentedText(sourceString);
        }
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
        log.trace("BootstrapTranslationServiceImpl.storeTranslation[]");
        log.debug("OriginalText is {}. Updated Transation is {}",Arrays.toString(originalText),updatedTranslation);
    }

    @Override
    public void storeTranslation(String originalText, String sourceLanguage, String targetLanguage,
        String updatedTranslation, TranslationConstants.ContentType contentType, String contentCategory,
        String userId, int rating, String path) throws TranslationException {
        log.trace("BootstrapTranslationServiceImpl.storeTranslation");
    }

    @Override
    public String createTranslationJob(String name, String description, String strSourceLanguage,
        String strTargetLanguage, Date dueDate, TranslationState state, TranslationMetadata jobMetadata)
        throws TranslationException {
        log.trace("BootstrapTranslationServiceImpl.createTranslationJob");
        String jobInfo = String.format("connector=aem,jobName=%s,srcLang=%s,trgLang=%s,config=%s", name, strSourceLanguage, strTargetLanguage, cloudConfigPath);
        if (dueDate != null) {
          String dueDateISO8601 = dueDate.toInstant().toString();
          jobInfo = String.format("%s,dueDate=%s", jobInfo, dueDateISO8601);
        }
        try {
          SourceFile[] sourceFiles = liltApiClient.getFiles(null);
        } catch (Exception e) {
          log.warn("error during createTranslationJob {}", e);
        }
        return jobInfo;
    }

    @Override
    public TranslationScope getFinalScope(String strTranslationJobID) throws TranslationException {
        log.trace("BootstrapTranslationServiceImpl.getFinalScope");

        return new TranslationScopeImpl();
    }

    @Override
    public TranslationStatus updateTranslationJobState(String strTranslationJobID, TranslationState state)
      throws TranslationException {
    	if(strTranslationJobID=="lilt"){
    		log.debug("Lilt Translation job detected");
    	} else if(strTranslationJobID == null) {
    		log.debug("Job was never sent to TMS. Updated using Export/Import");
    	} else {
    		// bootstrapTmsService.setTmsProperty(strTranslationJobID, BootstrapTmsConstants.BOOTSTRAP_TMS_STATUS, state.getStatus().toString());
    	}
    	if(state.getStatus() == TranslationStatus.COMMITTED_FOR_TRANSLATION) {
    		log.trace("Uploaded all Translation Objects in job {}",strTranslationJobID);
    	}
      return state.getStatus();
    }

    @Override
    public TranslationStatus getTranslationJobStatus(String strTranslationJobID) throws TranslationException {
        log.trace("BootstrapTranslationServiceImpl.getTranslationJobStatus");
        try {
          int imported = 0;
          int translated = 0;
          SourceFile[] files = liltApiClient.getFiles(strTranslationJobID);
          // loop backwards through the list since the most recent files will be at the end.
          for (SourceFile file : files) {
            if (file.labels.contains("status=IMPORTED")) {
              imported++;
            }
            if (file.labels.contains("status=TRANSLATED")) {
              translated++;
            }
          }
          boolean hasImported = imported > 0;
          boolean hasTranslated = translated > 0;
          if (hasImported && hasTranslated) {
            return TranslationStatus.TRANSLATED;
          }
          if (hasImported) {
            return TranslationStatus.TRANSLATION_IN_PROGRESS;
          }
        } catch (Exception e) {
          log.warn("error during getTranslationJobStatus {}", e);
        }
        return TranslationStatus.COMMITTED_FOR_TRANSLATION;
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
      String labels = String.format("%s,status=TRANSLATED", strTranslationJobID);
      String objectPath = String.format("%s.%s", getObjectPath(translationObj), exportFormat);
      try {
        SourceFile[] files = liltApiClient.getFiles(labels);
        // loop backwards through the list since the most recent files will be at the end.
        for (int i = files.length - 1; i >= 0; i--) {
          SourceFile file = files[i];
          if (Objects.equals(file.name, objectPath)) {
            log.warn("Downloading the translated lilt file {}", file.id);
            String translation = liltApiClient.downloadFile(file.id);
            return new ByteArrayInputStream(translation.getBytes());
          }
        }
      } catch (Exception e) {
        log.warn("error during getTranslatedObject {}", e);
      }
      log.info("No translations are available to download for {}", objectPath);
      return null;
    }

    public Optional<String> getFileExtension(String filename) {
      return Optional.ofNullable(filename)
        .filter(f -> f.contains("."))
        .map(f -> f.substring(filename.lastIndexOf(".") + 1));
    }

    @Override
    public String uploadTranslationObject(String strTranslationJobID, TranslationObject translationObject)
      throws TranslationException {

      InputStream inputStream;
      log.trace("Using {} InputStream", exportFormat);
      if (exportFormat.equalsIgnoreCase(BootstrapConstants.EXPORT_FORMAT_XLIFF_1_2)) {
        inputStream = translationObject.getTranslationObjectXLIFFInputStream("1.2");
      } else if (exportFormat.equalsIgnoreCase(BootstrapConstants.EXPORT_FORMAT_XLIFF_2_0)) {
        inputStream = translationObject.getTranslationObjectXLIFFInputStream("2.0");
      } else {
        inputStream = translationObject.getTranslationObjectXMLInputStream();
      }

      String objectPath = getObjectPath(translationObject);
      Optional<String> maybeExt = getFileExtension(objectPath);
      if (!maybeExt.isPresent()) {
        objectPath = String.format("%s.%s", objectPath, exportFormat);
      }
      String labels = String.format("%s,status=READY", strTranslationJobID);
      try {
        liltApiClient.uploadFile(objectPath, labels, inputStream);
      } catch (Exception e) {
        log.warn("error during uploadTranslationObject {}", e);
      }

      // Generate Preview
      if(isPreviewEnabled) {
        try {
          ZipInputStream zipInputStream = translationObject.getTranslationObjectPreview();
          if (zipInputStream != null) {
            unzipFileFromStream(zipInputStream, previewPath);
          } else {
            log.error("Got null for zipInputStream for " + getObjectPath(translationObject));
          }
        } catch (FileNotFoundException e) {
          log.error(e.getLocalizedMessage(), e);
        } catch (IOException e) {
          log.error(e.getLocalizedMessage(), e);
        }
      }
      log.trace("Preview Directory is: {}", previewPath);

      return objectPath;
    }

    @Override
    public TranslationStatus updateTranslationObjectState(String strTranslationJobID,
        TranslationObject translationObject, TranslationState state) throws TranslationException {
        log.trace("BootstrapTranslationServiceImpl.updateTranslationObjectState");
        // bootstrapTmsService.setTmsProperty(strTranslationJobID+getObjectPath(translationObject), BootstrapTmsConstants.BOOTSTRAP_TMS_STATUS, state.getStatus().toString());
        return TranslationStatus.TRANSLATED;
    }

    @Override
    public TranslationStatus getTranslationObjectStatus(String strTranslationJobID, TranslationObject translationObject)
      throws TranslationException {
      log.trace("BootstrapTranslationServiceImpl.getTranslationObjectStatus");
      try {
        String objectPath = String.format("%s.%s", getObjectPath(translationObject), exportFormat);
        int imported = 0;
        int translated = 0;
        SourceFile[] files = liltApiClient.getFiles(strTranslationJobID);
        // loop backwards through the list since the most recent files will be at the end.
        for (SourceFile file : files) {
          if (!Objects.equals(file.name, objectPath)) {
            continue;
          }
          if (file.labels.contains("status=IMPORTED")) {
            imported++;
          }
          if (file.labels.contains("status=TRANSLATED")) {
            translated++;
          }
        }
        boolean hasImported = imported > 0;
        boolean hasTranslated = translated > 0;
        if (hasImported && hasTranslated) {
          return TranslationStatus.TRANSLATED;
        }
        if (hasImported) {
          return TranslationStatus.TRANSLATION_IN_PROGRESS;
        }
        // if the object has an extension then it is an image or some sort of asset. we should consider those translated.
        Optional<String> maybeExt = getFileExtension(objectPath);
        if (maybeExt.isPresent()) {
          return TranslationStatus.TRANSLATED;
      }
      } catch (Exception e) {
        log.warn("error during getTranslationObjectStatus {}", e);
      }
      return TranslationStatus.COMMITTED_FOR_TRANSLATION;
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

    public void updateDueDate(String strTranslationJobID, Date date)
            throws TranslationException {
            log.debug("NEW DUE DATE:{}",date);
            // bootstrapTmsService.setTmsJobDuedate(strTranslationJobID, date);
    }
    
	private static void unzipFileFromStream(ZipInputStream zipInputStream, String targetPath) throws IOException {
		File dirFile = new File(targetPath + File.separator);
		if (!dirFile.exists()) {
			dirFile.mkdirs();
			log.trace("Created directory: {}",dirFile);
		}

		ZipEntry zipEntry = null;
		while (null != (zipEntry = zipInputStream.getNextEntry())) {
			String zipFileName = zipEntry.getName();
			if (zipEntry.isDirectory()) {
				File zipFolder = new File(targetPath + File.separator + zipFileName);
				if (!zipFolder.exists()) {
					zipFolder.mkdirs();
					log.trace("Created directory: {}",zipFolder);
				}
			} else {
				File file = new File(targetPath + File.separator + zipFileName);

				File parent = file.getParentFile();
				if (!parent.exists()) {
					parent.mkdirs();
				}

				FileOutputStream fos = null;
				try {
					fos = new FileOutputStream(file);
				} catch (FileNotFoundException e) {
					log.error(e.getLocalizedMessage(),e);
				}
				int readLen = 0;
				byte buffer[] = new byte[1024];
				while (-1 != (readLen = zipInputStream.read(buffer))) {
					fos.write(buffer, 0, readLen);
				}
				fos.close();
			}
		}
		zipInputStream.close();
	}
    
}
