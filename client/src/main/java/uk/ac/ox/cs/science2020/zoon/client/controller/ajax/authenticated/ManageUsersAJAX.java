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
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import uk.ac.ox.cs.science2020.zoon.client.ClientIdentifiers;
import uk.ac.ox.cs.science2020.zoon.client.controller.util.ControllerUtil;
import uk.ac.ox.cs.science2020.zoon.client.controller.util.JSON;
import uk.ac.ox.cs.science2020.zoon.client.service.ClientService;
import uk.ac.ox.cs.science2020.zoon.shared.value.object.ActionOutcomeVO;

/**
 * AJAX user management controller.
 *
 * @author geoff
 */
@Controller
@RequestMapping(ClientIdentifiers.URL_PREFIX_MANAGE_USERS_AJAX)
public class ManageUsersAJAX {

  @Autowired @Qualifier(ClientIdentifiers.COMPONENT_CLIENT_SERVICE)
  private ClientService clientService;

  private static final Log log = LogFactory.getLog(ManageUsersAJAX.class);


  /**
   * Create a user identity and load response into model.
   * 
   * @param model UI model.
   * @param principal User security principal.
   * @param requestParams JSON data containing user identity.
   * @return JSON view name.
   */
  @RequestMapping(value=ClientIdentifiers.ACTION_IDENTITY_CREATE,
                  method=RequestMethod.POST)
  public String createIdentity(final Model model, final Principal principal,
                               final @RequestBody
                                     String requestParams) {
    log.debug("~createIdentity() : Invoked for '" + requestParams + "'.");

    final String userName = ControllerUtil.retrieveUserName(principal);

    if (!StringUtils.isBlank(requestParams) && !StringUtils.isBlank(userName)) {
      try {
        final JSONObject identityParams = new JSONObject(requestParams);
        final String identity = identityParams.getString(ClientIdentifiers.PARAM_NAME_USER_IDENTITY);
        final String email = identityParams.getString(ClientIdentifiers.PARAM_NAME_USER_EMAIL);
        if (!StringUtils.isBlank(identity)) {
          final ActionOutcomeVO outcome = clientService.addUserIdentity(userName, identity, email);
          String json = null;
          String exception = null;
          if (outcome.isSuccess()) {
            final Map<String, String> response = new HashMap<String, String>();
            response.put("outcome", outcome.getInformation());
            json = new ObjectMapper().writeValueAsString(response);
          } else {
            exception = outcome.getInformation();
          }
          model.addAttribute(ClientIdentifiers.MODEL_ATTRIBUTE_CREATE_IDENTITY, new JSON(json,
                                                                                         exception));

        } else {
          model.addAttribute(ClientIdentifiers.MODEL_ATTRIBUTE_CREATE_IDENTITY,
                             new JSON(null, "An identity value is required!"));
        }
      } catch (Exception e) {
        model.addAttribute(ClientIdentifiers.MODEL_ATTRIBUTE_CREATE_IDENTITY,
                           new JSON(null, e.getMessage()));
      }
    }

    return ClientIdentifiers.VIEW_NAME_JSON;
  }

  /**
   * Delete a user.
   * 
   * @param model View model.
   * @param userName User to delete.
   * @return JSON view name.
   */
  // http://stackoverflow.com/questions/16332092/spring-mvc-pathvariable-with-dot-is-getting-truncated
  @RequestMapping(value=ClientIdentifiers.ACTION_USER_DELETING + 
                        "{" + ClientIdentifiers.PARAM_NAME_USER_NAME + ":.+}",
                  method=RequestMethod.POST)
  public String deleteUser(final Model model,
                           final @PathVariable(ClientIdentifiers.PARAM_NAME_USER_NAME)
                                 String userName) {
    log.debug("~deleteUser() : User '" + userName + "'.");

    model.addAttribute(ClientIdentifiers.MODEL_ATTRIBUTE_USER_DELETED,
                       clientService.deleteUser(userName));

    return ClientIdentifiers.VIEW_NAME_JSON;
  }

  /**
   * Enable/disable a user.
   * 
   * @param userName User to enable/disable.
   * @param enabling {@code true} if to enable, otherwise {@code false}.
   * @return JSON view name.
   */
  @RequestMapping(value=ClientIdentifiers.ACTION_USER_ENABLING + 
                        "{" + ClientIdentifiers.PARAM_NAME_USER_NAME + "}/" +
                        "{" + ClientIdentifiers.PARAM_NAME_USER_ENABLING + "}",
                  method=RequestMethod.POST)
  public String enableUser(final @PathVariable(ClientIdentifiers.PARAM_NAME_USER_NAME)
                                 String userName,
                           final @PathVariable(ClientIdentifiers.PARAM_NAME_USER_ENABLING)
                                 Boolean enabling) {
    log.debug("~enableUser() : User '" + userName + "', '" + enabling + "'.");

    clientService.setUserEnabled(userName, enabling);

    return ClientIdentifiers.VIEW_NAME_JSON;
  }
}