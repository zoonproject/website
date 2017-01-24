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
package uk.ac.ox.cs.science2020.zoon.client.controller.ajax.authenticated;

import java.io.IOException;
import java.security.Principal;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import uk.ac.ox.cs.science2020.zoon.client.ClientIdentifiers;
import uk.ac.ox.cs.science2020.zoon.client.controller.AbstractMessageSourceAwareController;
import uk.ac.ox.cs.science2020.zoon.client.controller.authenticated.ManageModules;
import uk.ac.ox.cs.science2020.zoon.client.controller.util.ControllerUtil;
import uk.ac.ox.cs.science2020.zoon.client.controller.util.JSON;
import uk.ac.ox.cs.science2020.zoon.client.exception.BusinessManagerWSInvocationException;
import uk.ac.ox.cs.science2020.zoon.client.exception.NoConnectionException;
import uk.ac.ox.cs.science2020.zoon.client.service.ClientService;
import uk.ac.ox.cs.science2020.zoon.client.value.object.ModuleActionVO;
import uk.ac.ox.cs.science2020.zoon.client.value.object.ModulePublishVO;
import uk.ac.ox.cs.science2020.zoon.client.value.object.VerificationResultsVO;
import uk.ac.ox.cs.science2020.zoon.shared.value.object.artifact.module.ModuleVO;

/**
 * AJAX module management controller.
 *
 * @author geoff
 * @see ManageModules
 */
@Controller
@RequestMapping(ClientIdentifiers.URL_PREFIX_MANAGE_MODULES_AJAX)
public class ManageModulesAJAX extends AbstractMessageSourceAwareController {

  /**
   * Maximum file upload size (in bytes).
   */
  // TODO : Make max file upload size configurable.
  public static final int MAX_FILE_UPLOAD_SIZE = 15000;

  private static final String mimeTypeAppOct = "application/octet-stream";

  @Autowired @Qualifier(ClientIdentifiers.COMPONENT_CLIENT_SERVICE)
  private ClientService clientService;

  private static final Log log = LogFactory.getLog(ManageModulesAJAX.class);

  /**
   * Delete a private module.
   * 
   * @param principal User's java security principal.
   * @param locale Browser locale.
   * @param model UI model.
   * @param deleteModule Value object to delete.
   * @return JSON view name.
   */
  @RequestMapping(value=ClientIdentifiers.ACTION_MODULE_DELETE,
                  method=RequestMethod.POST)
  public String deleteModule(final Principal principal, final Locale locale, final Model model,
                             final @RequestBody
                                   ModuleActionVO deleteModule) {
    log.debug("~deleteModule() : Request to delete module '" + deleteModule + "'.");

    final String userName = ControllerUtil.retrieveUserName(principal);

    if (ControllerUtil.isValidUserName(userName)) {
      try {
        final String outcome = clientService.deleteModule(userName, deleteModule);
        model.addAttribute(ClientIdentifiers.MODEL_ATTRIBUTE_DELETE_MODULE,
                           new JSON(outcome, null));
      } catch (IllegalArgumentException e) {
        model.addAttribute(ClientIdentifiers.MODEL_ATTRIBUTE_DELETE_MODULE,
                           new JSON(null, queryMessageSource(ClientIdentifiers.ERROR_BUS_MGR_SOMETHING_WRONG,
                                                             new Object[] { e.getMessage() },
                                                             locale)));
      } catch (BusinessManagerWSInvocationException e) {
        model.addAttribute(ClientIdentifiers.MODEL_ATTRIBUTE_DELETE_MODULE,
                           new JSON(null, queryMessageSource(ClientIdentifiers.ERROR_BUS_MGR_SOMETHING_WRONG,
                                                             new Object[] { e.getMessage() },
                                                             locale)));
      } catch (NoConnectionException e) {
        model.addAttribute(ClientIdentifiers.MODEL_ATTRIBUTE_DELETE_MODULE,
                           new JSON(null, queryMessageSource(ClientIdentifiers.ERROR_BUS_MGR_OUT_OF_CONTACT,
                                                             new Object[] { e.getMessage() },
                                                             locale)));
      }
    } else {
      log.error("~deleteModule() : Invalid user name '" + userName + "' encountered!");
      model.addAttribute(ClientIdentifiers.MODEL_ATTRIBUTE_DELETE_MODULE,
                         new JSON(null, queryMessageSource(ClientIdentifiers.ERROR_BUS_MGR_SOMETHING_WRONG,
                                                           new Object[] { "System error! Invalid user name!" },
                                                           locale)));
    }

    return ClientIdentifiers.VIEW_NAME_JSON;
  }

  /**
   * Retrieve a collection of uploaded (private) artifacts and load into model.
   * 
   * @param principal User security principal.
   * @param locale Browser locale.
   * @param model UI model.
   * @return JSON view name.
   */
  @RequestMapping(value=ClientIdentifiers.RENDER_MODULES_RETRIEVE_PRIVATE,
                  method=RequestMethod.GET)
  public String retrievePrivateModules(final Principal principal, final Locale locale,
                                       final Model model) {
    log.debug("~retrievePrivateModules() : Invoked.");

    final String userName = ControllerUtil.retrieveUserName(principal);
    if (ControllerUtil.isValidUserName(userName)) {
      try {
        final Map<String, List<ModuleVO>> privateModules = clientService.retrievePrivateModules(userName,
                                                                                                false);
        try {
          final String returnJSON = new ObjectMapper().writeValueAsString(privateModules);
          log.debug("~retrievePrivateModules() : JSON '" + returnJSON + "'.");
          model.addAttribute(ClientIdentifiers.MODEL_ATTRIBUTE_PRIVATE_MODULES,
                             new JSON(returnJSON, null));
        } catch (Exception exception) {
          final String errorMessage = exception.getMessage();
          log.error("~retrievePrivateModules() : Exception '" + errorMessage + "'.");
          model.addAttribute(ClientIdentifiers.MODEL_ATTRIBUTE_PRIVATE_MODULES,
                             new JSON(null, errorMessage));
        }
      } catch (BusinessManagerWSInvocationException e) {
        model.addAttribute(ClientIdentifiers.MODEL_ATTRIBUTE_PRIVATE_MODULES,
                           new JSON(null, queryMessageSource(ClientIdentifiers.ERROR_BUS_MGR_SOMETHING_WRONG,
                                                             new Object[] { e.getMessage() },
                                                             locale)));
      } catch (NoConnectionException e) {
        model.addAttribute(ClientIdentifiers.MODEL_ATTRIBUTE_PRIVATE_MODULES,
                           new JSON(null, queryMessageSource(ClientIdentifiers.ERROR_BUS_MGR_OUT_OF_CONTACT,
                                                             new Object[] {}, locale)));
      }
    } else {
      model.addAttribute(ClientIdentifiers.MODEL_ATTRIBUTE_PRIVATE_MODULES,
                         new JSON(null, queryMessageSource(ClientIdentifiers.ERROR_BUS_MGR_SOMETHING_WRONG,
                                                           new Object[] { "Invalid User id!" },
                                                           locale)));
    }

    return ClientIdentifiers.VIEW_NAME_JSON;
  }

  /**
   * Retrieve a collection of uploaded and verified artifacts and load into model.
   * 
   * @param principal User security principal.
   * @param locale Browser locale.
   * @param model UI model.
   * @return JSON view name.
   */
  @RequestMapping(value=ClientIdentifiers.RENDER_MODULES_RETRIEVE_PRIVATE_VERIFIED,
                  method=RequestMethod.GET)
  public String retrievePrivateVerifiedModules(final Principal principal, final Locale locale,
                                               final Model model) {
    log.debug("~retrievePrivateVerifiedModules() : Invoked.");

    final String userName = ControllerUtil.retrieveUserName(principal);
    if (ControllerUtil.isValidUserName(userName)) {
      try {
        final Map<String, List<ModuleVO>> privateModules = clientService.retrievePrivateModules(userName,
                                                                                                true);
        try {
          final String returnJSON = new ObjectMapper().writeValueAsString(privateModules);
          log.debug("~retrievePrivateVerifiedModules() : JSON '" + returnJSON + "'.");
          model.addAttribute(ClientIdentifiers.MODEL_ATTRIBUTE_PRIVATE_MODULES,
                             new JSON(returnJSON, null));
        } catch (Exception exception) {
          final String errorMessage = exception.getMessage();
          log.error("~retrievePrivateVerifiedModules() : Exception '" + errorMessage + "'.");
          model.addAttribute(ClientIdentifiers.MODEL_ATTRIBUTE_PRIVATE_MODULES,
                             new JSON(null, errorMessage));
        }
      } catch (BusinessManagerWSInvocationException e) {
        model.addAttribute(ClientIdentifiers.MODEL_ATTRIBUTE_PRIVATE_MODULES,
                           new JSON(null, queryMessageSource(ClientIdentifiers.ERROR_BUS_MGR_SOMETHING_WRONG,
                                                             new Object[] { e.getMessage() },
                                                             locale)));
      } catch (NoConnectionException e) {
         model.addAttribute(ClientIdentifiers.MODEL_ATTRIBUTE_PRIVATE_MODULES,
                            new JSON(null, queryMessageSource(ClientIdentifiers.ERROR_BUS_MGR_OUT_OF_CONTACT,
                                                              new Object[] {}, locale)));
      }
    } else {
      model.addAttribute(ClientIdentifiers.MODEL_ATTRIBUTE_PRIVATE_MODULES,
                         new JSON(null, queryMessageSource(ClientIdentifiers.ERROR_BUS_MGR_SOMETHING_WRONG,
                                                           new Object[] { "Invalid User id!" },
                                                           locale)));
    }

    return ClientIdentifiers.VIEW_NAME_JSON;
  }

  /**
   * Retrieve verification output and load into model.
   * 
   * @param verificationIdentifier Verification identifier.
   * @param principal User security principal.
   * @param locale Browser locale.
   * @param model UI model.
   * @return JSON view name.
   */
  @RequestMapping(value=ClientIdentifiers.RENDER_VERIFICATION_OUTPUT_RETRIEVE + "/" + "{" + ClientIdentifiers.PARAM_NAME_VERIFICATION_IDENTIFIER + "}",
                  method=RequestMethod.GET)
  public String retrieveVerificationOutput(final @PathVariable(ClientIdentifiers.PARAM_NAME_VERIFICATION_IDENTIFIER)
                                                 String verificationIdentifier,
                                           final Principal principal, final Locale locale,
                                           final Model model) {
    log.debug("~retrieveVerificationOutput() : Invoked.");

    final String userName = ControllerUtil.retrieveUserName(principal);
    if (ControllerUtil.isValidUserName(userName)) {
      try {
        final int identifier = Integer.valueOf(verificationIdentifier);
        final VerificationResultsVO results = clientService.retrieveVerificationOutput(identifier);
        try {
          final String returnJSON = new ObjectMapper().writeValueAsString(results);
          log.debug("~retrieveVerificationOutput() : JSON '" + returnJSON + "'.");
          model.addAttribute(ClientIdentifiers.MODEL_ATTRIBUTE_VERIFICATION_OUTPUT,
                             new JSON(returnJSON, null));
        } catch (Exception exception) {
          final String errorMessage = exception.getMessage();
          log.error("~retrieveVerification() : Exception '" + errorMessage + "'.");
          model.addAttribute(ClientIdentifiers.MODEL_ATTRIBUTE_PRIVATE_MODULES,
                             new JSON(null, errorMessage));
        }
      } catch (BusinessManagerWSInvocationException e) {
        model.addAttribute(ClientIdentifiers.MODEL_ATTRIBUTE_VERIFICATION_OUTPUT,
                           new JSON(null, queryMessageSource(ClientIdentifiers.ERROR_BUS_MGR_SOMETHING_WRONG,
                                                             new Object[] { e.getMessage() },
                                                             locale)));
      } catch (NoConnectionException e) {
         model.addAttribute(ClientIdentifiers.MODEL_ATTRIBUTE_VERIFICATION_OUTPUT,
                            new JSON(null, queryMessageSource(ClientIdentifiers.ERROR_BUS_MGR_OUT_OF_CONTACT,
                                                              new Object[] {}, locale)));
      }
    } else {
      model.addAttribute(ClientIdentifiers.MODEL_ATTRIBUTE_VERIFICATION_OUTPUT,
                         new JSON(null, queryMessageSource(ClientIdentifiers.ERROR_BUS_MGR_SOMETHING_WRONG,
                                                           new Object[] { "Invalid User id!" },
                                                           locale)));
    }

    return ClientIdentifiers.VIEW_NAME_JSON;
  }

  /**
   * Upload a module to the private store and load outcome into model.
   * 
   * @param request Multipart HTTP servlet request.
   * @param principal User security principal.
   * @param locale Browser locale.
   * @param model UI model.
   * @return JSON view name.
   */
  @RequestMapping(value=ClientIdentifiers.ACTION_MODULE_UPLOAD_TO_PRIVATE,
                  method=RequestMethod.POST)
  public String uploadModuleToPrivate(final MultipartHttpServletRequest request,
                                      final Principal principal, final Locale locale,
                                      final Model model) {
    log.debug("~uploadModuleToPrivate() : Invoked.");

    final String userName = ControllerUtil.retrieveUserName(principal);
    if (!ControllerUtil.isValidUserName(userName)) {
      model.addAttribute(ClientIdentifiers.MODEL_ATTRIBUTE_UPLOAD_MODULE,
                         new JSON(null,
                                  queryMessageSource(ClientIdentifiers.ERROR_BUS_MGR_SOMETHING_WRONG,
                                  new Object[] { "Cannot determine the user identifier" }, locale)));
    } else {
      try {
        String returnMessage = "undefined";

        final Iterator<String> filenameIterator = request.getFileNames();
        if (filenameIterator != null) {
          final MultipartFile multipartFile = request.getFile(filenameIterator.next());
          if (multipartFile != null && !multipartFile.isEmpty()) {
            if (mimeTypeAppOct.equalsIgnoreCase(multipartFile.getContentType())) {
              try {
                final String originalFilename = multipartFile.getOriginalFilename();
                log.debug("~uploadModuleToPrivate() : '" + originalFilename + "'.");
                final String moduleContent = new String(multipartFile.getBytes());
                log.debug("~uploadModuleToPrivate() : '" + moduleContent + "'.");
                try {
                  returnMessage = clientService.saveModuleInPrivateStore(userName, moduleContent);
                } catch (IllegalArgumentException e) {
                  returnMessage = "Could not upload module. Cause is '" + e.getMessage() + "'.";
                }
              } catch (IOException e) {
                return "Failed to upload module. Cause '" + e.getMessage() + "'.";
              }
            } else {
              returnMessage = "MultipartFile must be of type '" + mimeTypeAppOct + "', not '" + multipartFile.getContentType() + "'!";
            }
          } else {
            returnMessage = "MultipartFile had no content!";
          }
        } else {
          returnMessage = "No filenames found in request!";
        }

        model.addAttribute(ClientIdentifiers.MODEL_ATTRIBUTE_UPLOAD_MODULE,
                           new JSON(returnMessage, null));
      } catch (BusinessManagerWSInvocationException e) {
        model.addAttribute(ClientIdentifiers.MODEL_ATTRIBUTE_UPLOAD_MODULE,
                           new JSON(null, queryMessageSource(ClientIdentifiers.ERROR_BUS_MGR_SOMETHING_WRONG,
                                                             new Object[] { e.getMessage() },
                                                             locale)));
      } catch (NoConnectionException e) {
         model.addAttribute(ClientIdentifiers.MODEL_ATTRIBUTE_UPLOAD_MODULE,
                            new JSON(null, queryMessageSource(ClientIdentifiers.ERROR_BUS_MGR_OUT_OF_CONTACT,
                                                              new Object[] {}, locale)));
      }
    }

    return ClientIdentifiers.VIEW_NAME_JSON;
  }

  /**
   * Upload a module to the ZOON public repository and load outcome into model.
   * 
   * @param principal User security principal.
   * @param locale Browser locale.
   * @param model UI model.
   * @param modulePublish Value object holding module publish information.
   * @return JSON view name.
   */
  @RequestMapping(value=ClientIdentifiers.ACTION_MODULE_UPLOAD_TO_ZOON,
                  method=RequestMethod.POST)
    public String uploadModuleToZOON(final Principal principal, final Locale locale,
                                     final Model model,
                                     final @RequestBody
                                           ModulePublishVO modulePublish) {
    log.debug("~uploadModuleToZOON() : Invoked with '" + modulePublish.toString() + "'.");

    final String userName = ControllerUtil.retrieveUserName(principal);

    String response = "no response";
    try {
      response = clientService.saveModuleInZOONStore(userName, modulePublish);
    } catch (IllegalArgumentException exception) {
      final String errorMessage = exception.getMessage();
      log.error("~uploadModuleToZOON() : Exception '" + errorMessage + "'.");
      model.addAttribute(ClientIdentifiers.MODEL_ATTRIBUTE_UPLOAD_MODULE_TO_ZOON,
                         new JSON(null, errorMessage));
    } catch (BusinessManagerWSInvocationException e) {
      model.addAttribute(ClientIdentifiers.MODEL_ATTRIBUTE_UPLOAD_MODULE_TO_ZOON,
                         new JSON(null, queryMessageSource(ClientIdentifiers.ERROR_BUS_MGR_SOMETHING_WRONG,
                                                           new Object[] { e.getMessage() },
                                                           locale)));
    } catch (NoConnectionException e) {
       model.addAttribute(ClientIdentifiers.MODEL_ATTRIBUTE_UPLOAD_MODULE_TO_ZOON,
                          new JSON(null, queryMessageSource(ClientIdentifiers.ERROR_BUS_MGR_OUT_OF_CONTACT,
                                                            new Object[] {}, locale)));
    }

    final Map<String, String> modelAttribute = new HashMap<String, String>();
    modelAttribute.put("response", response);
    String returnJSON;
    try {
      returnJSON = new ObjectMapper().writeValueAsString(modelAttribute);
      model.addAttribute(ClientIdentifiers.MODEL_ATTRIBUTE_UPLOAD_MODULE_TO_ZOON,
                         new JSON(returnJSON, null));
    } catch (Exception exception) {
      final String errorMessage = exception.getMessage();
      log.error("~uploadModuleToZOON() : Exception '" + errorMessage + "'.");
      model.addAttribute(ClientIdentifiers.MODEL_ATTRIBUTE_UPLOAD_MODULE_TO_ZOON,
                         new JSON(null, errorMessage));
    }

    return ClientIdentifiers.VIEW_NAME_JSON;
  }

  /**
   * Verify artifact.
   * 
   * @param principal User security principal.
   * @param locale Browser locale.
   * @param model UI model.
   * @param artifactData Artifact data.
   * @return JSON view name.
   */
  @RequestMapping(value=ClientIdentifiers.ACTION_ARTIFACT_VERIFY + "/" + "{" + ClientIdentifiers.PARAM_NAME_ARTIFACT_DATA + ":.+}",
                  method=RequestMethod.POST)
  public String verifyArtifact(final Principal principal, final Locale locale, final Model model,
                               final @PathVariable(ClientIdentifiers.PARAM_NAME_ARTIFACT_DATA)
                                     String artifactData) {
    log.debug("~verifyArtifact() : Request to verify '" + artifactData + "'.");

    final String userName = ControllerUtil.retrieveUserName(principal);

    if (!ControllerUtil.isValidUserName(userName)) {
      model.addAttribute(ClientIdentifiers.MODEL_ATTRIBUTE_VERIFY_MODULE,
      new JSON(null, queryMessageSource(ClientIdentifiers.ERROR_BUS_MGR_SOMETHING_WRONG,
                                        new Object[] { "Cannot determine the user identifier" },
                                        locale)));
    } else {
      // TODO : Use ModuleActionVO technique... as deleteModule.
      final String[] parsed = artifactData.split("\\^");
      assert (parsed.length == 3) : "Expecting four items of module data to arrive!";

      final String moduleName = parsed[1];
      final String moduleVersion = parsed[2];

      try {
        final int identifier = clientService.verifyModule(userName, moduleName, moduleVersion);
        model.addAttribute(ClientIdentifiers.MODEL_ATTRIBUTE_VERIFY_MODULE,
                           new JSON(String.valueOf(identifier), null));
      } catch (BusinessManagerWSInvocationException e) {
        model.addAttribute(ClientIdentifiers.MODEL_ATTRIBUTE_VERIFY_MODULE,
                           new JSON(null, queryMessageSource(ClientIdentifiers.ERROR_BUS_MGR_SOMETHING_WRONG,
                                                             new Object[] { e.getMessage() },
                                                             locale)));
      } catch (NoConnectionException e) {
        model.addAttribute(ClientIdentifiers.MODEL_ATTRIBUTE_VERIFY_MODULE,
                           new JSON(null, queryMessageSource(ClientIdentifiers.ERROR_BUS_MGR_OUT_OF_CONTACT,
                                                             new Object[] {}, locale)));
      }
    }

    return ClientIdentifiers.VIEW_NAME_JSON;
  }
}