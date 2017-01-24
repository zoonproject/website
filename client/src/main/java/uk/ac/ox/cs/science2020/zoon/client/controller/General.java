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

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.HandlerMapping;

import uk.ac.ox.cs.science2020.zoon.client.ClientIdentifiers;
import uk.ac.ox.cs.science2020.zoon.client.controller.util.JSON;
import uk.ac.ox.cs.science2020.zoon.client.service.ClientService;
import uk.ac.ox.cs.science2020.zoon.shared.value.object.artifact.module.ModuleVO;

/**
 * Controller to show the general information pages.
 * 
 * @author geoff
 */
@Controller
public class General extends AbstractMessageSourceAwareController {

  @Autowired @Qualifier(ClientIdentifiers.COMPONENT_CLIENT_SERVICE)
  private ClientService clientService;

  private static final Log log = LogFactory.getLog(General.class);

  /**
   * Request received to show the about page.
   * 
   * @return Page name to view.
   */
  @RequestMapping(value=ClientIdentifiers.VIEW_ABOUT,
                  method=RequestMethod.GET)
  public String showAboutPage() {
    return ClientIdentifiers.PAGE_ABOUT;
  }

  /**
   * Request received to show the contact page.
   * 
   * @return Page name to view.
   */
  @RequestMapping(value=ClientIdentifiers.VIEW_CONTACT,
                  method=RequestMethod.GET)
  public String showContactPage() {
    return ClientIdentifiers.PAGE_CONTACT;
  }

  /**
   * Request received to show the login page.
   * 
   * @return Login page name.
   */
  @RequestMapping(value=ClientIdentifiers.VIEW_LOGIN,
                  method=RequestMethod.GET)
  public String showLoginPage() {
    log.debug("~showLoginPage() : Invoked.");

    return ClientIdentifiers.PAGE_LOGIN;
  }

  /**
   * Request received to show the modules page.
   * 
   * @return Page name to view.
   */
  @RequestMapping(value=ClientIdentifiers.VIEW_MODULES,
                  method=RequestMethod.GET)
  public String showModulesPage() {
    return ClientIdentifiers.PAGE_MODULES;
  }

  /**
   * Request received to show the privacy page.
   * 
   * @return Page name to view.
   */
  @RequestMapping(value=ClientIdentifiers.VIEW_PRIVACY,
                  method=RequestMethod.GET)
  public String showPrivacyPage() {
    return ClientIdentifiers.PAGE_PRIVACY;
  }

  /**
   * Request received to show the tutorials page.
   * 
   * @return Page name to view.
   */
  @RequestMapping(value=ClientIdentifiers.VIEW_TUTORIALS,
                  method=RequestMethod.GET)
  public String showTutorialsPage() {
    return ClientIdentifiers.PAGE_TUTORIALS;
  }

  /**
   * Request received to show the workflow calls page.
   * 
   * @return Page name to view.
   * @see WorkflowCallCtlr#viewWorkflowCalls(HttpServletRequest, Model)
   */
  @RequestMapping(value=ClientIdentifiers.VIEW_WORKFLOW_CALLS,
                  method=RequestMethod.GET)
  public String showWorkflowCallsPage() {
    return ClientIdentifiers.PAGE_WORKFLOW_CALLS;
  }

  /**
   * Request to view a module.
   * 
   * @param request HTTP request.
   * @param principal User security principal.
   * @param model UI model.
   * @return Page name to view.
   */
  @RequestMapping(value=ClientIdentifiers.VIEW_MODULE + "/**",
                  method=RequestMethod.GET)
  public String viewModule(final HttpServletRequest request, final Principal principal,
                           final Model model) {
    final String fullURL = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
    log.debug("~viewModule() : Invoked with '" + fullURL + "'.");
    final String[] components = fullURL.split(ClientIdentifiers.VIEW_MODULE.concat("/"));
    final String moduleDetails = components[1];

    String moduleName = moduleDetails;
    String moduleVersion = null;
    boolean retrievePrivate = false;

    if (moduleDetails.contains("/")) {
      final String[] eachDetail = moduleDetails.split("/");
      if (eachDetail.length != 3) {
        final String errorMessage = "Could not determine the Module to show";
        log.error("~viewModule() : ".concat(errorMessage));
        model.addAttribute(ClientIdentifiers.MODEL_ATTRIBUTE_MODULE,
                           new JSON(null, errorMessage));
      }
      moduleName = eachDetail[0];
      moduleVersion = eachDetail[1];
      retrievePrivate = Boolean.valueOf(eachDetail[2]);
    }
    log.debug("~viewModule() : Module name '" + moduleName + "', version '" + moduleVersion + "', private '" + retrievePrivate + "'.");

    final String userName = principal == null ? null : principal.getName();

    String returnPage = ClientIdentifiers.PAGE_MODULE;
    try {
      final ModuleVO module = clientService.retrieveModule(userName, moduleName, moduleVersion,
                                            retrievePrivate);
      model.addAttribute(ClientIdentifiers.MODEL_ATTRIBUTE_MODULE, module);
    } catch (Exception e) {
      log.error("~viewModule() : Exception '" + e.getMessage() + "' - redirecting to error/error.jsp!");
      returnPage = Error.errorGeneric;
    }

    return returnPage;
  }
}