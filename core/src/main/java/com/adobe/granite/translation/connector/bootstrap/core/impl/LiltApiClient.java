package com.adobe.granite.translation.connector.bootstrap.core.impl;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.net.URISyntaxException;
import java.util.List;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.http.client.utils.URIBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LiltApiClient {
  private static final Logger log = LoggerFactory.getLogger(LiltApiClient.class);

  String apiUrl;
  String apiKey;

  public class SourceFile {
    Integer id;
    String name;
    List<String> labels;
  }

  public LiltApiClient(String apiUrl, String apiKey) {
    this.apiUrl = apiUrl;
    this.apiKey = apiKey;
  }

  public SourceFile[] getFiles(String labels) throws IOException, URISyntaxException {
    try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
      String baseUrl = String.format("%s/files", apiUrl);
      URIBuilder req = new URIBuilder(baseUrl);
      req.setParameter("key", apiKey);
      if (labels != null) {
        req.setParameter("labels", labels);
      }
      HttpGet httpget = new HttpGet(req.build());
      log.warn("Executing request {}", httpget.getRequestLine());
      ResponseHandler<String> responseHandler = response -> {
        int status = response.getStatusLine().getStatusCode();
        if (status >= 200 && status < 300) {
          HttpEntity entity = response.getEntity();
          return entity != null ? EntityUtils.toString(entity) : null;
        } else {
          throw new ClientProtocolException("Unexpected response status: " + status);
        }
      };
      Gson gson = new Gson();
      String response = httpclient.execute(httpget, responseHandler);
      SourceFile[] sourceFiles = gson.fromJson(response, SourceFile[].class);
      return sourceFiles;
    }
  }

  public void uploadFile(String name, String labels, InputStream body) throws IOException, URISyntaxException {
    try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
      String baseUrl = String.format("%s/files", apiUrl);
      URIBuilder req = new URIBuilder(baseUrl);
      req.setParameter("key", apiKey);
      req.setParameter("name", name);
      req.setParameter("labels", labels);
      HttpPost httppost = new HttpPost(req.build());
      log.warn("Executing request {}", httppost.getRequestLine());
      InputStreamEntity reqEntity = new InputStreamEntity(body);
      reqEntity.setContentType("application/octet-stream");
      reqEntity.setChunked(true);
      httppost.setEntity(reqEntity);
      httpclient.execute(httppost);
    }
  }

  public String downloadFile(Integer fileId) throws IOException, URISyntaxException {
    try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
      String baseUrl = String.format("%s/files/download", apiUrl);
      URIBuilder req = new URIBuilder(baseUrl);
      req.setParameter("key", apiKey);
      req.setParameter("id", Integer.toString(fileId));
      HttpGet httpget = new HttpGet(req.build());
      log.warn("Executing request {}", httpget.getRequestLine());
      HttpResponse response = httpclient.execute(httpget);
      HttpEntity entity = response.getEntity();
      InputStream content = entity.getContent();
      return new BufferedReader(new InputStreamReader(content, StandardCharsets.UTF_8))
        .lines()
        .collect(Collectors.joining("\n"));
    }
  }
}
