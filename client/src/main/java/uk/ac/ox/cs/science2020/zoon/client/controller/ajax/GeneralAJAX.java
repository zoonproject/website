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
package uk.ac.ox.cs.science2020.zoon.client.controller.ajax;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import uk.ac.ox.cs.science2020.zoon.client.ClientIdentifiers;
import uk.ac.ox.cs.science2020.zoon.client.controller.AbstractMessageSourceAwareController;
import uk.ac.ox.cs.science2020.zoon.client.controller.util.ControllerUtil;
import uk.ac.ox.cs.science2020.zoon.client.controller.util.JSON;
import uk.ac.ox.cs.science2020.zoon.client.exception.BusinessManagerWSInvocationException;
import uk.ac.ox.cs.science2020.zoon.client.exception.NoConnectionException;
import uk.ac.ox.cs.science2020.zoon.client.service.ClientService;
import uk.ac.ox.cs.science2020.zoon.shared.value.object.artifact.module.ModuleVO;
import uk.ac.ox.cs.science2020.zoon.shared.value.object.artifact.workflowcall.WorkflowCallVO;
import uk.ac.ox.cs.science2020.zoon.shared.value.type.ModuleType;

/**
 * MVC Controller responding to general unauthenticated AJAX requests from the client.
 *
 * @author geoff
 */
@Controller
@RequestMapping(ClientIdentifiers.URL_PREFIX_AJAX)
public class GeneralAJAX extends AbstractMessageSourceAwareController {

  @Autowired @Qualifier(ClientIdentifiers.COMPONENT_CLIENT_SERVICE)
  private ClientService clientService;

  private static final Log log = LogFactory.getLog(GeneralAJAX.class);

  /**
   * Retrieve all versions of public and private (to the current user) modules and load into the
   * model.
   * 
   * @param principal User security principal.
   * @param locale Browser locale.
   * @param model UI model.
   * @return JSON view name.
   */
  @RequestMapping(value=ClientIdentifiers.RENDER_MODULES_RETRIEVE,
                  method=RequestMethod.GET)
  public String retrieveAllModules(final Principal principal, final Locale locale,
                                   final Model model) {
    log.debug("~retrieveAllModules() : Invoked.");

    final String userName = ControllerUtil.retrieveUserName(principal);

    try {
      final Map<String, List<ModuleVO>> modules = clientService.retrieveAllModules(userName,
                                                                                   false);
      String modulesJSON = null;
      try {
        modulesJSON = new ObjectMapper().writeValueAsString(modules);
        log.debug("~retrieveAllModules() : JSON '" + modulesJSON + "'.");
        model.addAttribute(ClientIdentifiers.MODEL_ATTRIBUTE_MODULES,
                           new JSON(modulesJSON, null));
      } catch (JsonMappingException e) {
        log.error("~retrieveAllModules() : JsonMappingException '" + e.getMessage() + "'.");
        model.addAttribute(ClientIdentifiers.MODEL_ATTRIBUTE_MODULES,
                           new JSON(null, "Application failure converting modules for display"));
      } catch (Exception e) {
        log.error("~retrieveAllModules() : Exception '" + e.getMessage() + "' when handling JSON.");
        model.addAttribute(ClientIdentifiers.MODEL_ATTRIBUTE_MODULES,
                           new JSON(null, e.getMessage()));
      }
    } catch (BusinessManagerWSInvocationException e) {
      model.addAttribute(ClientIdentifiers.MODEL_ATTRIBUTE_MODULES,
                         new JSON(null, queryMessageSource(ClientIdentifiers.ERROR_BUS_MGR_SOMETHING_WRONG,
                                                           new Object[] { e.getMessage() },
                                                           locale)));
    } catch (NoConnectionException e) {
       model.addAttribute(ClientIdentifiers.MODEL_ATTRIBUTE_MODULES,
                          new JSON(null, queryMessageSource(ClientIdentifiers.ERROR_BUS_MGR_OUT_OF_CONTACT,
                                                            new Object[] {}, locale)));
    }

    return ClientIdentifiers.VIEW_NAME_JSON;
  }

  /**
   * Retrieve only the latest public and private (to the current user) modules.
   * 
   * @param locale
   * @param model
   * @param principal
   * @return
   */
  @RequestMapping(value=ClientIdentifiers.RENDER_MODULES_RETRIEVE_LATEST,
                  method=RequestMethod.GET)
  public String retrieveLatestModules(final Principal principal, final Locale locale,
                                      final Model model) {
    log.debug("~retrieveLatestModules() : Invoked.");

    final String userName = ControllerUtil.retrieveUserName(principal);

    try {
      final List<ModuleVO> modules = new ArrayList<ModuleVO>();
      for (final Map.Entry<String, List<ModuleVO>> moduleEntry : clientService.retrieveAllModules(userName,
                                                                                                  true)
                                                                              .entrySet()) {
        modules.addAll(moduleEntry.getValue());
      }

      String modulesJSON = null;
      try {
        modulesJSON = new ObjectMapper().writeValueAsString(modules);
        log.debug("~retrieveLatestModules() : JSON '" + modulesJSON + "'.");
        model.addAttribute(ClientIdentifiers.MODEL_ATTRIBUTE_MODULES,
                           new JSON(modulesJSON, null));
      } catch (JsonMappingException e) {
        log.error("~retrieveLatestModules() : JsonMappingException '" + e.getMessage() + "'.");
        model.addAttribute(ClientIdentifiers.MODEL_ATTRIBUTE_MODULES,
                           new JSON(null, "Application failure converting modules for display"));
      } catch (Exception e) {
        log.error("~retrieveLatestModules() : Exception '" + e.getMessage() + "' when handling JSON.");
        model.addAttribute(ClientIdentifiers.MODEL_ATTRIBUTE_MODULES,
                           new JSON(null, e.getMessage()));
      }
    } catch (BusinessManagerWSInvocationException e) {
      model.addAttribute(ClientIdentifiers.MODEL_ATTRIBUTE_MODULES,
                         new JSON(null, queryMessageSource(ClientIdentifiers.ERROR_BUS_MGR_SOMETHING_WRONG,
                                  new Object[] { e.getMessage() }, locale)));
    } catch (NoConnectionException e) {
       model.addAttribute(ClientIdentifiers.MODEL_ATTRIBUTE_MODULES,
                          new JSON(null, queryMessageSource(ClientIdentifiers.ERROR_BUS_MGR_OUT_OF_CONTACT,
                                   new Object[] {}, locale)));
    }

    return ClientIdentifiers.VIEW_NAME_JSON;
  }

  /**
   * Retrieve all the module types and load into the model.
   * 
   * @param locale Browser locale.
   * @param model UI model.
   * @return JSON view name.
   */
  @RequestMapping(value=ClientIdentifiers.RENDER_MODULE_TYPES_RETRIEVE,
                  method=RequestMethod.GET)
  public String retrieveModuleTypes(final Locale locale, final Model model) {
    log.debug("~retrieveModuleTypes() : Invoked.");

    try {
      final Map<String, String> moduleTypes = clientService.retrieveModuleTypes();
      String moduleTypesJSON = null;
      try {
        moduleTypesJSON = new ObjectMapper().writeValueAsString(moduleTypes);
        log.debug("~retrieveModuleTypes() : JSON '" + moduleTypesJSON + "'.");
        model.addAttribute(ClientIdentifiers.MODEL_ATTRIBUTE_MODULETYPES,
                           new JSON(moduleTypesJSON, null));
      } catch (JsonMappingException e) {
        log.error("~retrieveModuleTypes() : JsonMappingException '" + e.getMessage() + "'.");
        model.addAttribute(ClientIdentifiers.MODEL_ATTRIBUTE_MODULETYPES,
                           new JSON(null, "Application failure converting module types for display"));
      } catch (Exception e) {
        log.error("~retrieveModuleTypes() : Exception '" + e.getMessage() + "' when handling JSON.");
        model.addAttribute(ClientIdentifiers.MODEL_ATTRIBUTE_MODULETYPES,
                           new JSON(null, e.getMessage()));
      }
    } catch (BusinessManagerWSInvocationException e) {
      model.addAttribute(ClientIdentifiers.MODEL_ATTRIBUTE_MODULETYPES,
                         new JSON(null, queryMessageSource(ClientIdentifiers.ERROR_BUS_MGR_SOMETHING_WRONG,
                                  new Object[] { e.getMessage() }, locale)));
    } catch (NoConnectionException e) {
       model.addAttribute(ClientIdentifiers.MODEL_ATTRIBUTE_MODULETYPES,
                          new JSON(null, queryMessageSource(ClientIdentifiers.ERROR_BUS_MGR_OUT_OF_CONTACT,
                                   new Object[] {}, locale)));
    }

    return ClientIdentifiers.VIEW_NAME_JSON;
  }

  /**
   * Retrieve the search results and load into the model.
   * 
   * @param locale Browser locale.
   * @param model UI model.
   * @param searchTerm Search term.
   * @return JSON view name.
   */
  @RequestMapping(value=ClientIdentifiers.RENDER_SEARCH,
                  method=RequestMethod.GET)
  public String retrieveSearchResults(final Locale locale, final Model model,
                                      final @RequestParam
                                            String searchTerm) {
    log.debug("~retrieveSearchResults() : Invoked.");

    try {
      final Map<String, Object> searchResults = clientService.search(searchTerm);
      String searchResultsJSON = null;
      try {
        searchResultsJSON = new ObjectMapper().writeValueAsString(searchResults);
        log.debug("~retrieveSearchResults() : JSON '" + searchResultsJSON + "'.");
        model.addAttribute(ClientIdentifiers.MODEL_ATTRIBUTE_SEARCH_RESULTS,
                           new JSON(searchResultsJSON, null));
      } catch (JsonMappingException e) {
        log.error("~retrieveSearchResults() : JsonMappingException '" + e.getMessage() + "'.");
        model.addAttribute(ClientIdentifiers.MODEL_ATTRIBUTE_SEARCH_RESULTS,
                           new JSON(null, "Application failure converting search results for display"));
      } catch (Exception e) {
        log.error("~retrieveSearchResults() : Exception '" + e.getMessage() + "' when handling JSON.");
        model.addAttribute(ClientIdentifiers.MODEL_ATTRIBUTE_SEARCH_RESULTS,
                           new JSON(null, e.getMessage()));
      }
    } catch (BusinessManagerWSInvocationException e) {
      model.addAttribute(ClientIdentifiers.MODEL_ATTRIBUTE_SEARCH_RESULTS,
                         new JSON(null, queryMessageSource(ClientIdentifiers.ERROR_BUS_MGR_SOMETHING_WRONG,
                                                           new Object[] { e.getMessage() },
                                                           locale)));
    } catch (NoConnectionException e) {
       model.addAttribute(ClientIdentifiers.MODEL_ATTRIBUTE_SEARCH_RESULTS,
                          new JSON(null, queryMessageSource(ClientIdentifiers.ERROR_BUS_MGR_OUT_OF_CONTACT,
                                                            new Object[] {}, locale)));
    }

    return ClientIdentifiers.VIEW_NAME_JSON;
  }

  /**
   * Retrieve workflow calls and load in the model.
   * <p>
   * If {@code moduleData} is specified then only retrieve workflow calls which reference the 
   * specified module.
   * 
   * @param principal User security principal.
   * @param locale Browser locale.
   * @param model UI model.
   * @param moduleData Optional module data.
   * @return JSON view name.
   */
  @RequestMapping(value=ClientIdentifiers.RENDER_RETRIEVE_WORKFLOW_CALLS,
                  method=RequestMethod.GET)
  public String retrieveWorkflowCalls(final Principal principal, final Locale locale,
                                      final Model model,
                                      final @RequestBody
                                            String moduleData) {
    log.debug("~retrieveWorkflowCalls() : Invoked '" + moduleData + "'.");

    final String userName = ControllerUtil.retrieveUserName(principal);

    final Map<String, List<WorkflowCallVO>> workflowCalls = new HashMap<String, List<WorkflowCallVO>>();
    try {
      String moduleName = null;
      String moduleVersion = null;
      ModuleType moduleType = null;
      if (!StringUtils.isBlank(moduleData)) {
        try {
          final JSONObject moduleDataJSON = new JSONObject(moduleData);
          moduleName = moduleDataJSON.getString("name");
          moduleVersion = moduleDataJSON.getString("version");
          moduleType = ModuleType.valueOf(moduleDataJSON.getString("type"));
        } catch (JSONException e) {
        }
      }

      if (!ControllerUtil.isValidUserName(userName)) {
        workflowCalls.putAll(clientService.retrieveWorkflowCalls(null, moduleName, moduleVersion,
                                                                 moduleType));
      } else {
        workflowCalls.putAll(clientService.retrieveWorkflowCalls(userName, moduleName, moduleVersion,
                                                                 moduleType));
      }
    } catch (BusinessManagerWSInvocationException e) {
      model.addAttribute(ClientIdentifiers.MODEL_ATTRIBUTE_WORKFLOW_CALLS,
                         new JSON(null, queryMessageSource(ClientIdentifiers.ERROR_BUS_MGR_SOMETHING_WRONG,
                                                           new Object[] { e.getMessage() }, locale)));
    } catch (NoConnectionException e) {
      model.addAttribute(ClientIdentifiers.MODEL_ATTRIBUTE_WORKFLOW_CALLS,
                         new JSON(null, queryMessageSource(ClientIdentifiers.ERROR_BUS_MGR_OUT_OF_CONTACT,
                                                           new Object[] {}, locale)));
    }

    String resultsJSON = null;
    try {
      resultsJSON = new ObjectMapper().writeValueAsString(workflowCalls);
    } catch (Exception e) {
      model.addAttribute(ClientIdentifiers.MODEL_ATTRIBUTE_WORKFLOW_CALLS,
                         new JSON(null, e.getMessage()));
    }
    model.addAttribute(ClientIdentifiers.MODEL_ATTRIBUTE_WORKFLOW_CALLS, new JSON(resultsJSON,
                                                                                  null));

    return ClientIdentifiers.VIEW_NAME_JSON;
  }
}