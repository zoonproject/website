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
package uk.ac.ox.cs.science2020.zoon.client.controller;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.HandlerMapping;

import uk.ac.ox.cs.science2020.zoon.client.ClientIdentifiers;
import uk.ac.ox.cs.science2020.zoon.client.controller.util.JSON;
import uk.ac.ox.cs.science2020.zoon.client.exception.BusinessManagerWSInvocationException;
import uk.ac.ox.cs.science2020.zoon.client.exception.NoConnectionException;
import uk.ac.ox.cs.science2020.zoon.client.service.ClientService;
import uk.ac.ox.cs.science2020.zoon.shared.value.object.artifact.module.ModuleVO;
import uk.ac.ox.cs.science2020.zoon.shared.value.object.artifact.workflowcall.WorkflowCallVO;

/**
 * Workflow call management pages.
 *
 * @author geoff
 */
@Controller
public class WorkflowCallCtlr extends AbstractMessageSourceAwareController {

  @Autowired @Qualifier(ClientIdentifiers.COMPONENT_CLIENT_SERVICE)
  private ClientService clientService;

  private static final Log log = LogFactory.getLog(WorkflowCallCtlr.class);

  /**
   * View workflow call.
   * <p>
   * If no request parameters are received then the request is considered a display to view the 
   * workflow call creation page.
   * 
   * @param request HTTP request.
   * @param principal User security principal.
   * @param locale Browser locale.
   * @param model UI model.
   * @return Page name to view.
   */
  @RequestMapping(value=ClientIdentifiers.VIEW_WORKFLOW_CALL + "/**",
                  method=RequestMethod.GET)
  public String viewWorkflowCall(final HttpServletRequest request, final Principal principal,
                                 final Locale locale, final Model model) {
    final String fullURL = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
    log.debug("~viewWorkflowCall() : Invoked with '" + fullURL + "'.");
    final String[] components = fullURL.split(ClientIdentifiers.VIEW_WORKFLOW_CALL.concat("/"));

    String workflowCallName = null;
    String workflowCallVersion = null;
    boolean retrievePrivate = false;

    if (components.length > 1) {
      final String workflowCallDetails = components[1];

      if (workflowCallDetails.contains("/")) {
        final String[] eachDetail = workflowCallDetails.split("/");
        if (eachDetail.length != 3) {
          final String errorMessage = "Could not determine the Workflow Call to show";
          log.error("~viewWorkflowCall() : ".concat(errorMessage));
          model.addAttribute(ClientIdentifiers.MODEL_ATTRIBUTE_MODULES,
                             new JSON(null, errorMessage));
          return ClientIdentifiers.PAGE_WORKFLOW_CALL;
        }
        workflowCallName = eachDetail[0];
        workflowCallVersion = eachDetail[1];
        retrievePrivate = Boolean.valueOf(eachDetail[2]);

        log.debug("~viewWorkflowCall() : Workflow call name '" + workflowCallName + "', version + '" + workflowCallVersion + "', retrieve private '" + retrievePrivate + "'.");
      }
    }

    final String userName = principal == null ? null : principal.getName();

    try {
      final Map<String, List<ModuleVO>> modules = clientService.retrieveAllModules(userName, false);
      final Map<String, String> moduleTypes = clientService.retrieveModuleTypes();

      final String modulesJSON = new ObjectMapper().writeValueAsString(modules);
      final String moduleTypesJSON = new ObjectMapper().writeValueAsString(moduleTypes);

      model.addAttribute(ClientIdentifiers.MODEL_ATTRIBUTE_MODULES,
                         new JSON(modulesJSON, null));
      model.addAttribute(ClientIdentifiers.MODEL_ATTRIBUTE_MODULETYPES,
                         new JSON(moduleTypesJSON, null));

      if (workflowCallName != null && workflowCallVersion != null) {
        final WorkflowCallVO workflowCall = clientService.retrieveWorkflowCall(userName,
                                                                               workflowCallName,
                                                                               workflowCallVersion,
                                                                               retrievePrivate);
        if (workflowCall != null) {
          log.debug("~viewWorkflowCall() : Workflow retrieved '" + workflowCall.toString() + "'.");
          model.addAttribute(ClientIdentifiers.MODEL_ATTRIBUTE_WORKFLOW_CALL,
                             workflowCall);
        }
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
    } catch (JsonMappingException e) {
      log.error("~viewWorkflowCall() : JsonMappingException '" + e.getMessage() + "'.");
      model.addAttribute(ClientIdentifiers.MODEL_ATTRIBUTE_MODULES,
                         new JSON(null, "Application failure converting modules for display"));
    } catch (Exception e) {
      log.error("~viewWorkflowCall() : Exception '" + e.getMessage() + "' when handling JSON.");
      model.addAttribute(ClientIdentifiers.MODEL_ATTRIBUTE_MODULES,
                         new JSON(null, e.getMessage()));
    }

    return ClientIdentifiers.PAGE_WORKFLOW_CALL;
  }

  /**
   * View the workflow calls page based on a module that references it.
   * 
   * @param request HTTP request.
   * @param model UI model.
   * @return Page name to view.
   * @see General#showWorkflowCallsPage()
   */
  @RequestMapping(value=ClientIdentifiers.VIEW_WORKFLOW_CALLS + "/**",
                  method=RequestMethod.GET)
  public String viewWorkflowCalls(final HttpServletRequest request, final Model model) {
    final String fullURL = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
    log.debug("~viewWorkflowCalls() : Invoked for '" + fullURL + "'.");
    String moduleName = null;
    String moduleVersion = null;
    String moduleType = null;
    final String[] components = fullURL.split(ClientIdentifiers.VIEW_WORKFLOW_CALLS.concat("/"));
    if (components.length == 2) {
      final String moduleDetails = components[1];
      if (moduleDetails.contains("/")) {
        final String[] eachDetail = moduleDetails.split("/");
        if (eachDetail.length == 3) {
          moduleName = eachDetail[0];
          moduleVersion = eachDetail[1];
          moduleType = eachDetail[2];
          log.debug("~viewWorkflowCalls() : Module name '" + moduleName + "', version '" + moduleVersion + "', type '" + moduleType + "'.");
        } else {
          log.warn("~viewWorkflowCalls() : Expecting module name, version and type data.");
        }
      } else {
        log.warn("~viewWorkflowCalls() : Expecting URL delimitation.");
      }
    } else {
      log.warn("~viewWorkflowCalls() : Expecting two components to the URL.");
    }

    if (!StringUtils.isBlank(moduleName) && !StringUtils.isBlank(moduleVersion) &&
        !StringUtils.isBlank(moduleType)) {
      final Map<String, String> module = new HashMap<String, String>();
      module.put("name", moduleName);
      module.put("version", moduleVersion);
      module.put("type", moduleType);

      model.addAttribute(ClientIdentifiers.MODEL_ATTRIBUTE_MODULE, module);
    }

    return ClientIdentifiers.PAGE_WORKFLOW_CALLS;
  }
}