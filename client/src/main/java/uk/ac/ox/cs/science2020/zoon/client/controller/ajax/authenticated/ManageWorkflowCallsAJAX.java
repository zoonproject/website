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
package uk.ac.ox.cs.science2020.zoon.client.controller.ajax.authenticated;

import java.security.Principal;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import uk.ac.ox.cs.science2020.zoon.client.ClientIdentifiers;
import uk.ac.ox.cs.science2020.zoon.client.controller.AbstractMessageSourceAwareController;
import uk.ac.ox.cs.science2020.zoon.client.controller.util.JSON;
import uk.ac.ox.cs.science2020.zoon.client.exception.BusinessManagerWSInvocationException;
import uk.ac.ox.cs.science2020.zoon.client.exception.NoConnectionException;
import uk.ac.ox.cs.science2020.zoon.client.service.ClientService;

/**
 * AJAX controller for workflow call management.
 *
 * @author geoff
 */
@Controller
@RequestMapping(ClientIdentifiers.URL_PREFIX_MANAGE_WORKFLOW_CALLS_AJAX)
public class ManageWorkflowCallsAJAX extends AbstractMessageSourceAwareController {

  @Autowired @Qualifier(ClientIdentifiers.COMPONENT_CLIENT_SERVICE)
  private ClientService clientService;

  private static final Log log = LogFactory.getLog(ManageUsersAJAX.class);

  /**
   * Upload a workflow call to the private repository.
   * 
   * @param principal User security principal.
   * @param locale Browser locale.
   * @param model UI model.
   * @param workflowCallJSON JSON representation of the workflow call.
   * @return JSON view name.
   */
  @RequestMapping(value=ClientIdentifiers.ACTION_UPLOAD_WORKFLOW_CALL_TO_PRIVATE,
                  method=RequestMethod.POST)
  public String uploadWorkflowCallToPrivate(final Principal principal, final Locale locale,
                                            final Model model,
                                            final @RequestBody
                                                  String workflowCallJSON) {
    log.debug("~uploadWorkflowCallToPrivate() : '" + workflowCallJSON + "'.");
    final String userName = principal == null ? null : principal.getName();
    if (!StringUtils.isBlank(workflowCallJSON) && !StringUtils.isBlank(userName)) {
      try {
        final String saveResponse = clientService.saveWorkflowCallInPrivateStore(userName,
                                                                                 workflowCallJSON);
        try {
          final Map<String, String> responseMap = new HashMap<String, String>();
          responseMap.put("job", saveResponse);
          final String response = new ObjectMapper().writeValueAsString(responseMap);
          log.debug("~uploadWorkflowCallToPrivate() : JSON '" + response + "'.");
          model.addAttribute(ClientIdentifiers.MODEL_ATTRIBUTE_WORKFLOW_CALL_SAVE_RESPONSE,
                             new JSON(response, null));
        } catch (JsonMappingException e) {
          log.error("~uploadWorkflowCallToPrivate() : JsonMappingException '" + e.getMessage() + "'.");
          model.addAttribute(ClientIdentifiers.MODEL_ATTRIBUTE_WORKFLOW_CALL_SAVE_RESPONSE,
                             new JSON(null, "Application failure converting modules for display"));
        } catch (Exception e) {
          log.error("~uploadWorkflowCallToPrivate() : Exception '" + e.getMessage() + "' when handling JSON.");
          model.addAttribute(ClientIdentifiers.MODEL_ATTRIBUTE_WORKFLOW_CALL_SAVE_RESPONSE,
                             new JSON(null, e.getMessage()));
        }
      } catch (BusinessManagerWSInvocationException e) {
        model.addAttribute(ClientIdentifiers.MODEL_ATTRIBUTE_WORKFLOW_CALL_SAVE_RESPONSE,
                           new JSON(null, queryMessageSource(ClientIdentifiers.ERROR_BUS_MGR_SOMETHING_WRONG,
                                                             new Object[] { e.getMessage() },
                                                             locale)));
      } catch (NoConnectionException e) {
        model.addAttribute(ClientIdentifiers.MODEL_ATTRIBUTE_WORKFLOW_CALL_SAVE_RESPONSE,
                           new JSON(null, queryMessageSource(ClientIdentifiers.ERROR_BUS_MGR_OUT_OF_CONTACT,
                                                             new Object[] {}, locale)));
      }
    }

    return ClientIdentifiers.VIEW_NAME_JSON;
  }
}