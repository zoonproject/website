/*

  Copyright (c) 2017, University of Oxford.
  All rights reserved.

  University of Oxford means the Chancellor, Masters and Scholars of the
  University of Oxford, having an administrative office at Wellington
  Square, Oxford OX1 2JD, UK.

  Redistribution and use in source and binary forms, with or without
  modification, are permitted provided that the following conditions are met:
   * Redistributions of source code must retain the above copyright notice,
     this list of conditions and the following disclaimer.
   * Redistributions in binary form must reproduce the above copyright notice,
     this list of conditions and the following disclaimer in the documentation
     and/or other materials provided with the distribution.
   * Neither the name of the University of Oxford nor the names of its
     contributors may be used to endorse or promote products derived from this
     software without specific prior written permission.

  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
  AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
  IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
  ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
  LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
  CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE
  GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
  HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
  LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
  OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 */
package uk.ac.ox.cs.science2020.zoon.business_manager.business;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.DeleteQuery;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.stereotype.Component;

import uk.ac.ox.cs.science2020.zoon.business_manager.BusinessIdentifiers;
import uk.ac.ox.cs.science2020.zoon.business_manager.business.artifact.Store;
import uk.ac.ox.cs.science2020.zoon.business_manager.business.artifact.util.DummyModuleProcessingUtil;
import uk.ac.ox.cs.science2020.zoon.business_manager.business.config.Configuration;
import uk.ac.ox.cs.science2020.zoon.business_manager.value.object.moduleheader.HeaderObject;
import uk.ac.ox.cs.science2020.zoon.business_manager.value.object.moduleheader.ModuleTypeVO;
import uk.ac.ox.cs.science2020.zoon.business_manager.value.object.moduleheader.MultiValueVO;
import uk.ac.ox.cs.science2020.zoon.business_manager.value.object.moduleheader.ParametersVO;
import uk.ac.ox.cs.science2020.zoon.business_manager.value.object.moduleheader.SingleValueVO;
import uk.ac.ox.cs.science2020.zoon.business_manager.value.object.moduleheader.ParametersVO.ParamInfo;
import uk.ac.ox.cs.science2020.zoon.shared.business.artifact.util.ModuleProcessingUtil;
import uk.ac.ox.cs.science2020.zoon.shared.entity.elastic.Author;
import uk.ac.ox.cs.science2020.zoon.shared.entity.elastic.module.Module;
import uk.ac.ox.cs.science2020.zoon.shared.entity.elastic.workflowcall.WorkflowCall;
import uk.ac.ox.cs.science2020.zoon.shared.value.object.artifact.workflowcall.WorkflowCallMetadataVO;
import uk.ac.ox.cs.science2020.zoon.shared.value.object.artifact.workflowcall.WorkflowCallVO;
import uk.ac.ox.cs.science2020.zoon.shared.value.type.DataIdentifier;

/**
 * 
 *
 * @author geoff
 */
@Component
public class RunOnce implements ApplicationListener<ContextRefreshedEvent> {

  @Autowired @Qualifier(BusinessIdentifiers.COMPONENT_CONFIGURATION)
  private Configuration configuration;

  @Autowired
  private ElasticsearchTemplate elasticsearchTemplate;

  // Spring-injected.
  @Value("${figshare.api}")
  private String figshareAPI;

  @Value("${figshare.search_for_tag}")
  private String figshareSearchForTag;

  @Value("${figshare.downloader}")
  private String figshareDownloader;

  private static final Log log = LogFactory.getLog(RunOnce.class);

  private String getURLContent(final String url) {
    final HttpClient client = HttpClientBuilder.create().build();
    final HttpGet request = new HttpGet(url);

    return processRequest(client, request);
  }

  private boolean containsSoughtTag(final JSONArray tags) {
    for (int i = 0; i < tags.length(); i++) {
      try {
        final String tag = tags.getString(i);
        if (figshareSearchForTag.equals(tag)) {
          return true;
        }
      } catch (JSONException e) {
        e.printStackTrace();
      }
    }

    return false;
  }

  private WorkflowCallMetadataVO grabContentByURL(final String url, final Long figshareId) {
    log.debug("~grabContentByURL() : Invoked for '" + url + "'.");

    WorkflowCallMetadataVO workflowCallMetadataVO = null;

    final String figshareData = getURLContent(url);
    if (figshareData == null) {
      return workflowCallMetadataVO;
    }

    JSONObject jsonObject = null;
    try {
      jsonObject = new JSONObject(figshareData);
    } catch (JSONException e) {
      e.printStackTrace();
    }

    if (jsonObject == null) {
      log.debug("~grabContentByURL() : Could not convert figshare data '" + figshareData + "' to a JSON object");
      return workflowCallMetadataVO;
    }

    try {
      final String status = jsonObject.getString("status");
      final JSONArray tags = jsonObject.getJSONArray("tags");
      final boolean isActive = jsonObject.getBoolean("is_active");
      final boolean isPublic = jsonObject.getBoolean("is_public");
      final boolean isConfidential = jsonObject.getBoolean("is_confidential");

      if ("public".equalsIgnoreCase(status) && isActive && isPublic && !isConfidential &&
          containsSoughtTag(tags)) {
        
      }

      /*
      final String embargoDate = jsonObject.getString("embargo_date");
      final String citation = jsonObject.getString("citation");
      final String embargoReason = jsonObject.getString("embargo_reason");
      final JSONArray references = jsonObject.getJSONArray("references");
      final long id = jsonObject.getLong("id");
      final JSONArray customFields = jsonObject.getJSONArray("custom_fields");
      final long size = jsonObject.getLong("size");
      final String metadataReason = jsonObject.getString("metadata_reason");
      final String funding = jsonObject.getString("funding");
      final String figshareURL = jsonObject.getString("figshare_url");
      final String embargoType = jsonObject.getString("embargo_type");
      final String title = jsonObject.getString("title");
      final int definedType = jsonObject.getInt("defined_type");
      final boolean isEmbargoed = jsonObject.getBoolean("is_embargoed");
      final int version = jsonObject.getInt("version");
      final String confidentialReason = jsonObject.getString("confidential_reason");
      final String description = jsonObject.getString("description");
      final String publishedDate = jsonObject.getString("published_date");
      final String modifiedDate = jsonObject.getString("modified_date");
      final String doi = jsonObject.getString("doi");
      final boolean hasLinkedFile = jsonObject.getBoolean("has_linked_file");
      final String url_ = jsonObject.getString("url");
      final String createdDate = jsonObject.getString("created_date");
      final boolean isMetadataRecord = jsonObject.getBoolean("is_metadata_record");

      log.debug("~grabContentByURL() : status '" + status + "'.");
      log.debug("~grabContentByURL() : embargoDate '" + embargoDate + "'.");
      log.debug("~grabContentByURL() : citation '" + citation + "'.");
      log.debug("~grabContentByURL() : embargoReason '" + embargoReason + "'.");
      log.debug("~grabContentByURL() : id '" + id + "'.");
      log.debug("~grabContentByURL() : size '" + size + "'.");
      log.debug("~grabContentByURL() : metadataReason '" + metadataReason + "'.");
      log.debug("~grabContentByURL() : funding '" + funding + "'.");
      log.debug("~grabContentByURL() : figshareURL '" + figshareURL + "'.");
      log.debug("~grabContentByURL() : embargoType '" + embargoType + "'.");
      log.debug("~grabContentByURL() : title '" + title + "'.");
      log.debug("~grabContentByURL() : definedType '" + definedType + "'.");
      log.debug("~grabContentByURL() : isEmbargoed '" + isEmbargoed + "'.");
      log.debug("~grabContentByURL() : version '" + version + "'.");
      log.debug("~grabContentByURL() : confidentialReason '" + confidentialReason + "'.");
      log.debug("~grabContentByURL() : description '" + description + "'.");
      log.debug("~grabContentByURL() : publishedDate '" + publishedDate + "'.");
      log.debug("~grabContentByURL() : isActive '" + isActive + "'.");
      log.debug("~grabContentByURL() : isPublic '" + isPublic + "'.");
      log.debug("~grabContentByURL() : modifiedDate '" + modifiedDate + "'.");
      log.debug("~grabContentByURL() : isConfidential '" + isConfidential + "'.");
      log.debug("~grabContentByURL() : doi '" + doi + "'.");
      log.debug("~grabContentByURL() : hasLinkedFile '" + hasLinkedFile + "'.");
      log.debug("~grabContentByURL() : url_ '" + url_ + "'.");
      log.debug("~grabContentByURL() : createdDate '" + createdDate + "'.");
      log.debug("~grabContentByURL() : isMetadataRecord '" + isMetadataRecord + "'.");
      */

      final JSONArray files = jsonObject.getJSONArray("files");
      for (int i = 0; i < files.length(); i++) {
        final JSONObject file = (JSONObject) files.get(i);
        final long fileId = file.getLong("id");
        final String fileName = file.getString("name");
        final long fileSize = file.getLong("size");

        log.debug("~grabContentByURL() : fileId '" + fileId + "'.");
        log.debug("~grabContentByURL() : fileName '" + fileName + "'.");
        log.debug("~grabContentByURL() : fileSize '" + fileSize + "'.");

        if (!StringUtils.isBlank(fileName) && fileName.endsWith(".txt")) {
          // Arbitrary max size is 10000!
          if (fileSize >= 10000) {
            throw new UnsupportedOperationException("File size >= 10000 - Rejecting!");
          }
          final String fileContent = getURLContent(figshareDownloader + fileId);
          final Set<String> components = new HashSet(Arrays.asList(fileContent.split("\n")));
          final Map<String, String> componentMap = new HashMap<String, String>();
          for (final String component : components) {
            final String[] subComponents = component.split(": ", 2);
            if (subComponents.length == 2) {
              componentMap.put(subComponents[0], subComponents[1]);
            }
          }
          workflowCallMetadataVO = new WorkflowCallMetadataVO(componentMap, figshareId);
          log.debug("~grabContentByURL() : Using '" + workflowCallMetadataVO.toString() + "'.");
          break;
        }
      }

      /*
      final JSONArray categories = jsonObject.getJSONArray("categories");

      final JSONObject license = jsonObject.getJSONObject("license");
      */
    } catch (JSONException e) {
      e.printStackTrace();
    }

    return workflowCallMetadataVO;
  }

  private void loadModules() {
    final Map<String, List<Map<DataIdentifier, HeaderObject>>> publicModules = new HashMap<String, List<Map<DataIdentifier, HeaderObject>>>();

    for (final Store artifactStore : configuration.getArtifactStores()) {
      final String storeName = artifactStore.getName();
      log.debug("~loadModules() : Querying '" + storeName + "'.");
      if (!artifactStore.hasPublicArtifacts()) {
        log.debug("~loadModules() : Ignoring store '" + storeName + "' as not containing public artifacts.");
        continue;
      }

      final Map<String, List<Map<DataIdentifier, HeaderObject>>> retrieved = artifactStore.loadPublicModulesOnStartup();

      log.debug("~loadModules() : Retrieved '" + retrieved.keySet() + "'.");
      publicModules.putAll(retrieved);
    }

    final List<IndexQuery> moduleIndexQueries = new ArrayList<IndexQuery>();
    for (final Map.Entry<String, List<Map<DataIdentifier, HeaderObject>>> publicModule : publicModules.entrySet()) {
      final String name = publicModule.getKey();
      final List<Map<DataIdentifier, HeaderObject>> allVersionedData = publicModule.getValue();
      for (final Map<DataIdentifier, HeaderObject> eachVersionedData : allVersionedData) {
        String version = "";
        String type = "";
        String submitted = "";
        String location = "";
        List<Author> eAuthors = new ArrayList<Author>();
        List<String> eDescriptions = new ArrayList<String>();
        List<String> eReturnValues = new ArrayList<String>();
        Map<String, String> parameters = new HashMap<String, String>();
        String references = "";

        final String content = null;
        final String source = null;

        for (final Map.Entry<DataIdentifier, HeaderObject> eachVersionedDataEntry : eachVersionedData.entrySet()) {
          final DataIdentifier dataIdentifier = eachVersionedDataEntry.getKey();
          final HeaderObject headerObject = eachVersionedDataEntry.getValue();

          switch (dataIdentifier) {
            case DESCRIPTION :
              eDescriptions.addAll(((MultiValueVO) headerObject).getComments());
              break;
            case AUTHOR :
              for (final String authorName : ((MultiValueVO) headerObject).getComments()) {
                final String[] authorComponents = ModuleProcessingUtil.extractAuthorComponents(authorName);
                eAuthors.add(new Author(authorComponents[0], authorComponents[1]));
              }
              break;
            case VERSION :
              version = ((SingleValueVO) headerObject).getComment();
              break;
            case TYPE :
              type = ((ModuleTypeVO) headerObject).getModuleType().toString();
              break;
            case SUBMITTED :
              submitted = ((SingleValueVO) headerObject).getComment();
              break;
            case PARAM :
              for (final ParamInfo paramInfo : ((ParametersVO) headerObject).getParamInfo()) {
                parameters.put(paramInfo.getName(), paramInfo.getDescription());
              }
              break;
            case RETURN :
              eReturnValues.addAll(((MultiValueVO) headerObject).getComments());
              break;
            case LOCATION :
              location = ((SingleValueVO) headerObject).getComment();
              break;
            case REFERENCES :
              references = ((SingleValueVO) headerObject).getComment();
              break;
            default :
              break;
          }
        }

        final IndexQuery indexQuery = new IndexQuery();
        final Module eModule = new Module(name, version, type, parameters, location, eReturnValues,
                                          source, submitted, eAuthors, eDescriptions, references,
                                          content);
        indexQuery.setObject(eModule);
        moduleIndexQueries.add(indexQuery);
        log.debug("~loadModules() : " + eModule.toString());

        //artifactRepository.save(eModule);
        // curl -XGET 'http://localhost:9200/zoon/module/_search?size=1000&pretty=true'
      }
    }

    if (moduleIndexQueries.isEmpty()) {
      log.warn("~loadModules() : No modules found. Nothing to populate ElasticSearch with!");
    } else {
      elasticsearchTemplate.bulkIndex(moduleIndexQueries);
      elasticsearchTemplate.refresh(Module.class, true);
    }
  }

  private void loadWorkflowCalls() {
    final HttpClient client = HttpClientBuilder.create().build();
    final HttpPost request = new HttpPost(figshareAPI);
    //request.setHeader("Content-Type", "application/json");
    final String json = "{ \"search_for\": \"".concat(figshareSearchForTag).concat("\" }");
    HttpEntity entity = null;
    try {
      entity = new ByteArrayEntity(json.getBytes("UTF-8"));
    } catch (UnsupportedEncodingException e1) {
      e1.printStackTrace();
    }
    if (entity == null) {
      return;
    }

    request.setEntity(entity);

    // e.g. curl "https://api.figshare.com/v2/articles/search" -d '{ "search_for": "zoonWorkflow"}'
    final String response = processRequest(client, request);
    if (response == null) {
      return;
    }

    final String responseJSON = response.toString();
    log.debug("~loadWorkflowCalls() : Response JSON '" + responseJSON + "'.");
    JSONArray jsonArray = null;
    try {
      jsonArray = new JSONArray(responseJSON);
    } catch (JSONException e) {
      e.printStackTrace();
      return;
    }

    final List<WorkflowCallMetadataVO> workflowCallMetadataVOs = new ArrayList<WorkflowCallMetadataVO>();

    for (int i = 0; i < jsonArray.length(); i++) {
      try {
        final JSONObject result = (JSONObject) jsonArray.get(i);
        final String url = result.getString("url");
        final String doi = result.getString("doi");
        final Long id = result.getLong("id");
        final String publishedDate = result.getString("published_date");
        final String title = result.getString("title");

        log.debug("~loadWorkflowCalls() : '" + url + "'.");
        log.debug("~loadWorkflowCalls() : '" + doi + "'.");
        log.debug("~loadWorkflowCalls() : '" + id + "'.");
        log.debug("~loadWorkflowCalls() : '" + publishedDate + "'.");
        log.debug("~loadWorkflowCalls() : '" + title + "'.");

        final WorkflowCallMetadataVO workflowCallMetadataVO = grabContentByURL(url, id);
        if (workflowCallMetadataVO != null) {
          workflowCallMetadataVOs.add(workflowCallMetadataVO);
        }

      } catch (JSONException e) {
        e.printStackTrace();
      }
    }

    final List<IndexQuery> workflowIndexQueries = new ArrayList<IndexQuery>();
    for (final WorkflowCallMetadataVO workflowCallMetadataVO : workflowCallMetadataVOs) {
      final IndexQuery workflowIndexQuery = new IndexQuery();

      final WorkflowCallVO workflowCallVO = workflowCallMetadataVO.getWorkflowCallVO();
      final WorkflowCall workflowCall = workflowCallVO.asWorkflowCall();

      workflowIndexQuery.setObject(workflowCall);
      workflowIndexQueries.add(workflowIndexQuery);
    }

    elasticsearchTemplate.bulkIndex(workflowIndexQueries);
    elasticsearchTemplate.refresh(WorkflowCall.class, true);
  }

  /* (non-Javadoc)
   * @see org.springframework.context.ApplicationListener#onApplicationEvent(org.springframework.context.ApplicationEvent)
   */
  @Override
  public void onApplicationEvent(final ContextRefreshedEvent arg0) {
    log.debug("~onApplicationEvent() : Invoked.");

    try {
      final DeleteQuery deleteQuery = new DeleteQuery();
      deleteQuery.setIndex("zoon");
      deleteQuery.setType("workflow_call");
      deleteQuery.setQuery(QueryBuilders.matchAllQuery());
      elasticsearchTemplate.delete(deleteQuery);
      deleteQuery.setType("module");
      deleteQuery.setQuery(QueryBuilders.matchAllQuery());
      elasticsearchTemplate.delete(deleteQuery);
    } catch (Exception e) {
      // If it's an empty db already, ignore.
      log.info("~onApplicationEvent() : Exception '" + e.getMessage() + "'.");
    }

    elasticsearchTemplate.deleteIndex(Module.class);
    elasticsearchTemplate.deleteIndex(WorkflowCall.class);
    elasticsearchTemplate.createIndex(Module.class);
    elasticsearchTemplate.createIndex(WorkflowCall.class);
    elasticsearchTemplate.putMapping(Module.class);
    elasticsearchTemplate.putMapping(WorkflowCall.class);

    loadModules();
    loadWorkflowCalls();
  }

  private static String processRequest(final HttpClient client, final HttpRequestBase request) {
    HttpResponse response = null;
    try {
      response = client.execute(request);
    } catch (Exception e) {
      e.printStackTrace();
    }
    BufferedReader reader = null;
    if (response != null) {
      try {
        reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
      } catch (UnsupportedOperationException e) {
        e.printStackTrace();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    final StringBuffer urlContentSB = new StringBuffer();
    if (reader != null) {
      try {
        String line = "";
        while ((line = reader.readLine()) != null) {
          if (!line.endsWith("\n")) {
            line = line.concat("\n");
          }
          urlContentSB.append(line);
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    if (urlContentSB.length() == 0) {
      log.debug("~processRequest() : No data url '" + request.getURI() + "'!");
      return null;
    }

    return urlContentSB.toString();
  }
}