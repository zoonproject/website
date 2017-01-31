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
package uk.ac.ox.cs.science2020.zoon.client.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.HandlerMapping;

import uk.ac.ox.cs.science2020.zoon.client.ClientIdentifiers;
import uk.ac.ox.cs.science2020.zoon.client.binding.UserRegistration;
import uk.ac.ox.cs.science2020.zoon.client.controller.util.ControllerUtil;
import uk.ac.ox.cs.science2020.zoon.client.entity.Identity;
import uk.ac.ox.cs.science2020.zoon.client.service.ClientService;
import uk.ac.ox.cs.science2020.zoon.client.value.object.user.UserDetails;
import uk.ac.ox.cs.science2020.zoon.shared.value.object.artifact.AuthorVO;
import uk.ac.ox.cs.science2020.zoon.shared.value.object.artifact.module.ModuleVO;
import uk.ac.ox.cs.science2020.zoon.shared.value.object.artifact.workflowcall.WorkflowCallVO;

/**
 * User controller.
 *
 * @author geoff
 */
@Controller
public class User {

  @Autowired @Qualifier(ClientIdentifiers.COMPONENT_CLIENT_SERVICE)
  private ClientService clientService;

  private static final Log log = LogFactory.getLog(User.class);

  /**
   * Register a user.
   * 
   * @param model UI model.
   * @param userRegistration User registration object.
   * @return Page name to view.
   */
  @RequestMapping(value=ClientIdentifiers.ACTION_USER_REGISTER,
                  method=RequestMethod.POST)
  public String registerUser(final Model model,
                             final @ModelAttribute(ClientIdentifiers.MODEL_ATTRIBUTE_USER)
                                   UserRegistration userRegistration) {
    log.debug("~registerUser() : Invoked with '" + userRegistration.toString() + "'.");
    String returnPage = ClientIdentifiers.PAGE_USER_REGISTER;

    if (!userRegistration.hasAllValuesAssigned()) {
      model.addAttribute(ClientIdentifiers.MODEL_ATTRIBUTE_USER, userRegistration);

      final String response = "Please provide values for user name, identity name, email and password!";
      model.addAttribute(ClientIdentifiers.MODEL_ATTRIBUTE_MESSAGE_ERROR, response);
    } else {
      String response = clientService.registerUser(userRegistration.getIdentityName(),
                                                   userRegistration.getEmail(),
                                                   userRegistration.getUserName(),
                                                   userRegistration.getPassword());
      boolean registrationFailed = true;
      if (response == null) {
        registrationFailed = false;
        response = "Registered! We'll contact you at this address : ".concat(userRegistration.getEmail()); 
        model.addAttribute(ClientIdentifiers.MODEL_ATTRIBUTE_MESSAGE_INFO, response);
      } else {
        model.addAttribute(ClientIdentifiers.MODEL_ATTRIBUTE_MESSAGE_ERROR, response);
      }

      if (!registrationFailed) {
        returnPage = ClientIdentifiers.PAGE_INDEX;
      }
    }

    return returnPage;
  }

  /**
   * Show the user registration page.
   * 
   * @param model UI model.
   * @return Page name to view.
   */
  @RequestMapping(value=ClientIdentifiers.VIEW_USER_REGISTER,
                  method=RequestMethod.GET)
  public String showUserRegisterPage(final Model model) {
    log.debug("~showUserRegisterPage() : Invoked.");

    model.addAttribute(ClientIdentifiers.MODEL_ATTRIBUTE_USER, new UserRegistration());

    return ClientIdentifiers.PAGE_USER_REGISTER;
  }

  /**
   * Request received to show the profile page based on the provided identity.
   * 
   * @param request HTTP request.
   * @param model UI model.
   * @param principal User security principal.
   * @return Page name to view.
   */
  @RequestMapping(method=RequestMethod.GET,
                  value=ClientIdentifiers.VIEW_PROFILE_BY_IDENTITY + "/**")
  public String viewProfileByIdentity(final HttpServletRequest request, final Model model,
                                      final Principal principal) {
    final String fullURL = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
    log.debug("~viewProfileByIdentity() : Invoked for '" + fullURL + "'.");
    final String[] components = fullURL.split(ClientIdentifiers.VIEW_PROFILE_BY_IDENTITY.concat("/"));
    final String profileIdentity = components[1];

    return viewProfile(profileIdentity, model, principal);
  }

  // Generic profile viewing code
  private String viewProfile(final String profileIdentity, final Model model,
                             final Principal principal) {
    final String loggedInUserName = ControllerUtil.retrieveUserName(principal);

    String returnPage = ClientIdentifiers.PAGE_USER_PROFILE;
    try {
      final UserDetails profileUser = clientService.retrieveNonAdminUserByIdentity(profileIdentity);
      final boolean hasProfileUser = profileUser != null;

      boolean showPrivate = false;
      if (loggedInUserName != null && hasProfileUser &&
          loggedInUserName.equals(profileUser.getUsername())) {
        showPrivate = true;
      }
      model.addAttribute(ClientIdentifiers.MODEL_ATTRIBUTE_USER, profileUser);
      log.debug("~viewProfile() : Retrieved profile user '" + profileUser + "'.");

      /*
       * Modules.
       */
      final List<ModuleVO> allModules = new ArrayList<ModuleVO>();
      if (profileUser != null) {
        // Retrieve public modules which the profile user has authored.
        allModules.addAll(clientService.retrievePublicModules(profileUser));
      }
      if (showPrivate) {
        for (final Map.Entry<String, List<ModuleVO>> moduleEntry : clientService.retrievePrivateModules(profileUser.getUsername(),
                                                                                                        false)
                                                                                .entrySet()) {
          allModules.addAll(moduleEntry.getValue());
        }
      }

      model.addAttribute(ClientIdentifiers.MODEL_ATTRIBUTE_MODULES, allModules);

      /*
       * Workflow calls.
       */
      if (hasProfileUser) {
        final Map<String, List<WorkflowCallVO>> workflowCalls = clientService.retrieveWorkflowCalls(profileUser.getUsername(),
                                                                                                    null, null, null);
        log.debug("~viewProfile() : Workflow Calls '" + workflowCalls.toString() + "'.");
        final List<WorkflowCallVO> workflowCallObjs = new ArrayList<WorkflowCallVO>();
        for (Map.Entry<String, List<WorkflowCallVO>> workflowCallEntry : workflowCalls.entrySet()) {
          final List<WorkflowCallVO> workflowCallVersions = workflowCallEntry.getValue();
          for (final WorkflowCallVO workflowCallVersion : workflowCallVersions) {
            final boolean isPrivate = workflowCallVersion.isPrivateArtifact();
            if (isPrivate) {
              // It's a private workflow call of the profiled person ....
              if (showPrivate) {
                // ... and the current user is the profiled person!
                workflowCallObjs.add(workflowCallVersion);
              }
            } else {
              // It's a public workflow call, so check the authors.
              final Set<String> identities = new HashSet<String>();
              for (final Identity identity : profileUser.getIdentities()) {
                identities.add(identity.getIdentity());
              }
              for (final AuthorVO author : workflowCallVersion.getAuthors()) {
                final String authorName = author.getAuthorName();
                if (identities.contains(authorName)) {
                  workflowCallObjs.add(workflowCallVersion);
                  break;
                }
              }
            }
          }
        }
        model.addAttribute(ClientIdentifiers.MODEL_ATTRIBUTE_WORKFLOW_CALLS, workflowCallObjs);
      }
    } catch (Exception e) {
      log.error("~viewProfile() : Exception '" + e.getMessage() + "' - redirecting to the error page!");
      returnPage = Error.errorGeneric;
    }

    return returnPage;
  }

  /**
   * Request received to show the profile page based on the provided username.
   * 
   * @param request HTTP request.
   * @param model UI model.
   * @param principal User security principal.
   * @return Page name to view.
   */
  @RequestMapping(method=RequestMethod.GET,
                  value=ClientIdentifiers.VIEW_PROFILE_BY_USERNAME + "/**")
  public String viewProfileByUsername(final HttpServletRequest request, final Model model,
                                      final Principal principal) {
    final String fullURL = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
    log.debug("~viewProfileByUsername() : Invoked for '" + fullURL + "'.");
    final String[] components = fullURL.split(ClientIdentifiers.VIEW_PROFILE_BY_USERNAME.concat("/"));
    final String userName = components[1];

    log.debug("~viewProfileByUsername() : Invoked for '" + userName + "'.");

    String returnPage = ClientIdentifiers.PAGE_USER_PROFILE;
    try {
      final List<UserDetails> userDetails = clientService.retrieveUsersByUsername(userName);
      if (userName.equals(principal.getName())) {
        model.addAttribute(ClientIdentifiers.MODEL_ATTRIBUTE_USER_CURRENTLY_LOGGED_IN, true);
      }
      if (userDetails != null && !userDetails.isEmpty()) {
        final UserDetails userDetail = userDetails.get(0);
        final Set<Identity> identities = userDetail.getIdentities();
        if (identities != null && !identities.isEmpty()) {
          viewProfile(identities.iterator().next().getIdentity(), model, principal);
        } else {
          log.debug("~viewProfileByUsername() : No or empty identities for userDetail '" + userDetail + "'.");
        }
      } else {
        log.debug("~viewProfileByUsername() : No or empty user details for user named '" + userName + "'.");
      }
    } catch (Exception e) {
      log.error("~viewProfileByUsername() : Exception '" + e.getMessage() + "' - redirecting to error/error.jsp!");
      returnPage = Error.errorGeneric;
    }

    return returnPage;
  }
}