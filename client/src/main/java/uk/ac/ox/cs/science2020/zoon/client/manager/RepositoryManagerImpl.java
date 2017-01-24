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

/**
 * 
 */
package uk.ac.ox.cs.science2020.zoon.client.manager;

import static org.elasticsearch.index.query.QueryBuilders.boolQuery;
import static org.elasticsearch.index.query.QueryBuilders.matchQuery;
import static org.elasticsearch.index.query.QueryBuilders.nestedQuery;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Component;

import uk.ac.ox.cs.science2020.zoon.client.ClientIdentifiers;
import uk.ac.ox.cs.science2020.zoon.client.dao.elastic.repository.ModuleRepository;
import uk.ac.ox.cs.science2020.zoon.client.dao.elastic.repository.WorkflowCallRepository;
import uk.ac.ox.cs.science2020.zoon.client.entity.Identity;
import uk.ac.ox.cs.science2020.zoon.shared.entity.elastic.module.Module;
import uk.ac.ox.cs.science2020.zoon.shared.entity.elastic.workflowcall.WorkflowCall;
import uk.ac.ox.cs.science2020.zoon.shared.value.object.artifact.module.ModuleVO;
import uk.ac.ox.cs.science2020.zoon.shared.value.object.artifact.workflowcall.WorkflowCallVO;

/**
 * Implementation of the elastic repository manager (containing public module and workflow call
 * data).
 *
 * @author geoff
 */
@Component(ClientIdentifiers.COMPONENT_REPOSITORY_MANAGER)
public class RepositoryManagerImpl implements RepositoryManager {

  @Autowired
  private ElasticsearchTemplate elasticsearchTemplate;

  @Autowired
  private ModuleRepository moduleRepository;

  @Autowired
  private WorkflowCallRepository workflowCallRespository;

  private static final SortBuilder moduleNameSortBuilder = SortBuilders
                                                           .fieldSort("name")
                                                           .order(SortOrder.ASC);

  private static final Log log = LogFactory.getLog(RepositoryManagerImpl.class);

  /* (non-Javadoc)
   * @see uk.ac.ox.cs.science2020.zoon.manager.RepositoryManager#findAll()
   */
  @Override
  public List<ModuleVO> findAll() {
    log.debug("~findAll() : Invoked.");

    final List<ModuleVO> allModules = new ArrayList<ModuleVO>();
    for (final Module module : moduleRepository.findAll(new Sort(new Sort.Order(Sort.Direction.ASC, "name")))) {
      allModules.add(new ModuleVO(module));
    }

    return allModules;
  }

  /* (non-Javadoc)
   * @see uk.ac.ox.cs.science2020.zoon.manager.RepositoryManager#findAll(java.util.Set)
   */
  @Override
  public List<ModuleVO> findAll(final Set<Identity> identities) {
    final Set<ModuleVO> uniqueModules = new HashSet<ModuleVO>();

    try {
      //elasticsearchTemplate.putMapping(Module.class);
      //elasticsearchTemplate.refresh(Module.class, true);
      for (final Identity identity : identities) {
        log.debug("~findAll() : Identity '" + identity + "'.");
        final BoolQueryBuilder boolQueryBuilder = boolQuery();
        // matchQuery == case sensitive, termQuery == case insensitive
        boolQueryBuilder.must(nestedQuery("authors", matchQuery("authors.authorName", identity.getIdentity())));
        /*
        final QueryBuilder queryBuilder = nestedQuery("authors",
                                                      boolQuery().must(termQuery("authors.name",
                                                                                 identity)));
        */
        final SearchQuery searchQuery = new NativeSearchQueryBuilder()
                                            .withPageable(new PageRequest(0, 100))
                                            .withQuery(boolQueryBuilder)
                                            .withSort(moduleNameSortBuilder)
                                            .build();
        final Page<Module> pages = elasticsearchTemplate.queryForPage(searchQuery, Module.class);
        if (pages.getTotalElements() > 0) {
          log.debug("~findAll() : TotalElements : '" + pages.getTotalElements() + "'.");
          for (final Module module : pages.getContent()) {
            log.debug("~findAll() : Found '" + module.toString() + "'.");
            uniqueModules.add(new ModuleVO(module));
          }
        } else {
          log.debug("~findAll() : Nothing found for '" + identity.getIdentity() + "'.");
        }
        /*
        final List<Module> modules = elasticsearchTemplate.queryForList(searchQuery, Module.class);
        if (!modules.isEmpty()) {
          for (final Module module : modules) {
            log.debug("~retrieveElasticModules() : Found '" + module.toString() + "'.");
          }
          elasticModules.addAll(modules);
        }
        */
        /*
        List<Module> modules = elasticsearchTemplate.queryForList(new NativeSearchQueryBuilder()
                                                                      .withQuery(QueryBuilders.termQuery("moduleName", "LocalRaster"))
                                                                      .build(),
                                                                  Module.class);
        log.debug("~retrieveElasticModules() : LocalRaster '" + modules.size() + "'.");
        */
      }
    } catch (Exception e) {
      log.error("~findAll() : Exception '" + e.getMessage() + "'.");
    }

    return new ArrayList<ModuleVO>(uniqueModules);
  }

  /* (non-Javadoc)
   * @see uk.ac.ox.cs.science2020.zoon.client.manager.RepositoryManager#findModuleByNameAndVersion(java.lang.String, java.lang.String)
   */
  @Override
  public ModuleVO findModuleByNameAndVersion(final String moduleName, final String moduleVersion) {
    log.debug("~findModuleByNameAndVersion() : Invoked with '" + moduleName + "'.");

    final Module module = moduleRepository.findByNameAndVersion(moduleName, moduleVersion);
    ModuleVO found = null;
    if (module != null) {
      log.debug("~findModuleByNameAndVersion() : Module with name '" + moduleName + "' found!");
      found = new ModuleVO(module);
    }

    return found;
  }

  
  /* (non-Javadoc)
   * @see uk.ac.ox.cs.science2020.zoon.client.manager.RepositoryManager#findWorkflowCallByNameAndVersion(java.lang.String, java.lang.String)
   */
  @Override
  public WorkflowCallVO findWorkflowCallByNameAndVersion(final String workflowCallName,
                                                         final String workflowCallVersion) {
    log.debug("~findWorkflowCallByNameAndVersion() : Invoked with '" + workflowCallName + "', '" + workflowCallVersion + "'.");

    final WorkflowCall workflowCall = workflowCallRespository.findByNameAndVersion(workflowCallName,
                                                                                   workflowCallVersion);
    WorkflowCallVO found = null;
    if (workflowCall != null) {
      log.debug("~findWorkflowCallByNameAndVersion() : Workflow call with name '" + workflowCallName + "', version '" + workflowCallVersion + "', found!");
      found = new WorkflowCallVO(workflowCall);
    } else {
      log.info("~findWorkflowCallByNameAndVersion() : Workflow call with name '" + workflowCallName + "', version '" + workflowCallVersion + "', not found!");
    }

    return found;
  }

  /* (non-Javadoc)
   * @see uk.ac.ox.cs.science2020.zoon.client.manager.RepositoryManager#retrieveWorkflowCalls()
   */
  @Override
  public Map<String, List<WorkflowCallVO>> retrieveWorkflowCalls() {
    log.debug("~retrieveWorkflowCalls() : Invoked.");

    final Map<String, List<WorkflowCallVO>> workflowCalls = new HashMap<String, List<WorkflowCallVO>>();
    for (final WorkflowCall workflowCall : workflowCallRespository.findAll()) {
      final String workflowCallName = workflowCall.getName();

      if (workflowCalls.containsKey(workflowCallName)) {
        final List<WorkflowCallVO> existingWorkflowCalls = workflowCalls.get(workflowCallName);
        existingWorkflowCalls.add(new WorkflowCallVO(workflowCall));
      } else {
        final List<WorkflowCallVO> newWorkflowCalls = new ArrayList<WorkflowCallVO>();
        newWorkflowCalls.add(new WorkflowCallVO(workflowCall));
        workflowCalls.put(workflowCallName, newWorkflowCalls);
      }
    }

    return workflowCalls;
  }
}