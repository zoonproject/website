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
package uk.ac.ox.cs.science2020.zoon.client.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import uk.ac.ox.cs.science2020.zoon.client.ClientIdentifiers;
import uk.ac.ox.cs.science2020.zoon.client.controller.util.ControllerUtil;
import uk.ac.ox.cs.science2020.zoon.client.entity.Identity;
import uk.ac.ox.cs.science2020.zoon.client.exception.BusinessManagerWSInvocationException;
import uk.ac.ox.cs.science2020.zoon.client.exception.NoConnectionException;
import uk.ac.ox.cs.science2020.zoon.client.exception.UserIdentityAlreadyExistsException;
import uk.ac.ox.cs.science2020.zoon.client.manager.BusinessManagerManager;
import uk.ac.ox.cs.science2020.zoon.client.manager.MailManager;
import uk.ac.ox.cs.science2020.zoon.client.manager.RepositoryManager;
import uk.ac.ox.cs.science2020.zoon.client.manager.UserManager;
import uk.ac.ox.cs.science2020.zoon.client.value.object.ModuleActionVO;
import uk.ac.ox.cs.science2020.zoon.client.value.object.ModulePublishVO;
import uk.ac.ox.cs.science2020.zoon.client.value.object.VerificationResultsVO;
import uk.ac.ox.cs.science2020.zoon.client.value.object.user.UserDetails;
import uk.ac.ox.cs.science2020.zoon.shared.value.object.ActionOutcomeVO;
import uk.ac.ox.cs.science2020.zoon.shared.value.object.artifact.AbstractArtifactVO;
import uk.ac.ox.cs.science2020.zoon.shared.value.object.artifact.AuthorVO;
import uk.ac.ox.cs.science2020.zoon.shared.value.object.artifact.module.ModuleVO;
import uk.ac.ox.cs.science2020.zoon.shared.value.object.artifact.workflowcall.WorkflowCallVO;
import uk.ac.ox.cs.science2020.zoon.shared.value.type.ArtifactType;
import uk.ac.ox.cs.science2020.zoon.shared.value.type.ModuleType;

/**
 * Client service implementation.
 * 
 * @author geoff
 */
@Component(ClientIdentifiers.COMPONENT_CLIENT_SERVICE)
public class ClientServiceImpl implements ClientService {

  @Autowired @Qualifier(ClientIdentifiers.COMPONENT_BUSINESS_MANAGER_MANAGER)
  private BusinessManagerManager businessManagerManager;

  @Autowired @Qualifier(ClientIdentifiers.COMPONENT_MAIL_MANAGER)
  private MailManager mailManager;

  @Autowired @Qualifier(ClientIdentifiers.COMPONENT_REPOSITORY_MANAGER)
  private RepositoryManager repositoryManager;

  @Autowired @Qualifier(ClientIdentifiers.COMPONENT_USER_MANAGER)
  private UserManager userManager;

  private static final Log log = LogFactory.getLog(ClientServiceImpl.class);

  /* (non-Javadoc)
   * @see uk.ac.ox.cs.science2020.zoon.client.service.ClientService#addUserIdentity(java.lang.String, java.lang.String, java.lang.String)
   */
  @Override
  public ActionOutcomeVO addUserIdentity(final String userName, final String identity, final String email) {
    log.debug("~addUserIdentity() : Invoked for '" + userName + "', identity '" + identity + "', email '" + email + "'.");

    ActionOutcomeVO actionOutcome = null;
    try {
      actionOutcome = userManager.addUserIdentity(userName, identity, email);
    } catch (UserIdentityAlreadyExistsException e) {
      actionOutcome = new ActionOutcomeVO(false, e.getMessage());
    }

    return actionOutcome;
  }

  @SuppressWarnings("unchecked")
  private void appendToResults(final Map<String, Object> results, final String group,
                               final String name, final Object dataObj) {
    Map<String, Object> subSet;
    if (results.containsKey(group)) {
      subSet = (Map<String, Object>) results.get(group);
      subSet.put(name, dataObj);
    } else {
      subSet = new TreeMap<String, Object>();
      subSet.put(name, dataObj);
      results.put(group, subSet);
    }
  }

  /* (non-Javadoc)
   * @see uk.ac.ox.cs.science2020.zoon.client.service.ClientService#deleteModule(java.lang.String, uk.ac.ox.cs.science2020.zoon.client.value.object.ModuleActionVO)
   */
  @Override
  public String deleteModule(final String userName, final ModuleActionVO moduleDelete)
                             throws BusinessManagerWSInvocationException, NoConnectionException,
                                    IllegalArgumentException {
    if (StringUtils.isBlank(userName) || !moduleDelete.hasValidContent()) {
      throw new IllegalArgumentException("User name, module name, module version must be provided in order to delete a module!");
    }

    final String moduleName = moduleDelete.getName();
    final String moduleVersion = moduleDelete.getVersion();

    log.debug("~deleteModule() : Invoked for '" + moduleName + "', '" + moduleVersion + "'.");

    return businessManagerManager.deleteModule(userName, moduleName, moduleVersion);
  }

  /* (non-Javadoc)
   * @see uk.ac.ox.cs.science2020.zoon.client.service.ClientService#deleteUser(java.lang.String)
   */
  @Override
  public boolean deleteUser(final String userName) {
    log.debug("~deleteUser() : Invoked for '" + userName + "'.");

    return userManager.deleteUser(userName);
  }

  // Filter modules.
  private static Map<String, List<ModuleVO>> filterModules(final Map<String, List<ModuleVO>> allModules,
                                                           final boolean hasUserName,
                                                           final boolean latestOnly) {
    final Map<String, List<ModuleVO>> latestModules = new HashMap<String, List<ModuleVO>>();

    for (final Map.Entry<String, List<ModuleVO>> moduleEntry : allModules.entrySet()) {
      final String moduleName = moduleEntry.getKey();
      final List<ModuleVO> versionedModules = moduleEntry.getValue();

      final List<ModuleVO> filteredModules = new ArrayList<ModuleVO>();
      if (latestOnly) {
        // Retrieve the latest public version of the module
        for (final ModuleVO eachModule : versionedModules) {
          if (!eachModule.isPrivateArtifact() && eachModule.isLatest()) {
            filteredModules.add(eachModule);
            break;
          }
        }
      } else {
        // Retrieve all public versions of the module.
        for (final ModuleVO eachModule : versionedModules) {
          if (!eachModule.isPrivateArtifact()) {
            filteredModules.add(eachModule);
          }
        }
      }

      if (hasUserName) {
        // Retrieve any private version of the module.
        for (final ModuleVO eachModule : versionedModules) {
          if (eachModule.isPrivateArtifact()) {
            filteredModules.add(eachModule);
          }
        }
      }

      latestModules.put(moduleName, filteredModules);
    }

    return latestModules;
  }
  
  /* (non-Javadoc)
   * @see uk.ac.ox.cs.science2020.zoon.client.service.ClientService#registerUser(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
   */
  @Override
  public String registerUser(final String identityName, final String email, final String userName,
                             final String password) {
    log.debug("~registerUser() : Invoked for user name '" + userName + ", email '" + email + "', identityName '" + identityName + "'.");

    final String registrationOutcome = userManager.registerUser(identityName, email, userName,
                                                                password);
    if (registrationOutcome == null) {
      log.debug("~registerUser() : Null outcome from registration - sending email.");
      //mailManager.sendEmail(identityName, email, userName, EMAIL_TYPE.REGISTRATION);
    }

    return registrationOutcome;
  }

  /* (non-Javadoc)
   * @see uk.ac.ox.cs.science2020.zoon.client.service.ClientService#retrieveAllModules(java.lang.String, boolean)
   */
  @Override
  public Map<String, List<ModuleVO>> retrieveAllModules(final String userName,
                                                        final boolean latestOnly)
                                                        throws BusinessManagerWSInvocationException,
                                                               NoConnectionException {
    log.debug("~retrieveAllModules() : Invoked.");

    final Map<String, List<ModuleVO>> modules = new HashMap<String, List<ModuleVO>>();

    // Have a look around elastic!
    for (final ModuleVO moduleVO : repositoryManager.findAll()) {
      final String moduleName = moduleVO.getName();
      if (modules.containsKey(moduleName)) {
        modules.get(moduleName).add(moduleVO);
      } else {
        final List<ModuleVO> newModuleCollection = new ArrayList<ModuleVO>();
        newModuleCollection.add(moduleVO);
        modules.put(moduleName, newModuleCollection);
      }
    }

    if (!StringUtils.isBlank(userName)) {
      // Have a look around gitblit
      for (final Map.Entry<String, List<ModuleVO>> privateModules : retrievePrivateModules(userName, false).entrySet()) {
        final String moduleName = privateModules.getKey();
        if (modules.containsKey(moduleName)) {
          modules.get(moduleName).addAll(privateModules.getValue());
        } else {
          final List<ModuleVO> newModuleCollection = new ArrayList<ModuleVO>();
          newModuleCollection.addAll(privateModules.getValue());
          modules.put(moduleName, newModuleCollection);
        }
      }
    }

    // Set the module flag which indicates if the module appears in a workflow call.
    final Map<String, List<WorkflowCallVO>> workflowCalls = retrieveWorkflowCalls(userName, null,
                                                                                  null, null);
    for (final Map.Entry<String, List<WorkflowCallVO>> versionedWorkflowCallEntries : workflowCalls.entrySet()) {
      final List<WorkflowCallVO> versionedWorkflowCalls = versionedWorkflowCallEntries.getValue();
      for (final WorkflowCallVO workflowCall : versionedWorkflowCalls) {
        for (final Map.Entry<String, List<ModuleVO>> moduleEntries : modules.entrySet()) {
          final String moduleName = moduleEntries.getKey();
          for (final ModuleVO module : moduleEntries.getValue()) {
            if (workflowCall.referencesModule(moduleName, module.getVersion(),
                                              module.getModuleType())) {
              module.setWorkflowed(true);
            }
          }
        }
      }
    }

    return filterModules(modules, !StringUtils.isBlank(userName), latestOnly);
  }

  /* (non-Javadoc)
   * @see uk.ac.ox.cs.science2020.zoon.client.service.ClientService#retrieveModule(java.lang.String, java.lang.String, java.lang.String, boolean)
   */
  @Override
  public ModuleVO retrieveModule(final String userName, final String moduleName,
                                 final String moduleVersion, final boolean retrievePrivate)
                                 throws BusinessManagerWSInvocationException,
                                        IllegalArgumentException, NoConnectionException {
    log.debug("~retrieveModule() : User '" + userName + "', requesting '" + moduleName + "', version '" + moduleVersion + "'.");
    if (StringUtils.isBlank(moduleName)) {
      throw new IllegalArgumentException("Module retrieval requires at least a module name.");
    }

    ModuleVO found = null;
    if (retrievePrivate) {
      // Find private based on user, module name and version.
      // TODO : Should just retrieve a single value from private repos!!
      for (final Map.Entry<String, List<ModuleVO>> versionedModules : 
           retrievePrivateModules(userName, false).entrySet()) {
        if (moduleName.equals(versionedModules.getKey())) {
          for (final ModuleVO versionedModule : versionedModules.getValue()) {
            final String availableVersion = versionedModule.getVersion();
            if (moduleVersion.equals(availableVersion)) {
              found = versionedModule;
              log.debug("~retrieveModule() : Returning private module '" + found.toString() + "'.");
              break;
            }
          }
        }
        if (found != null) {
          break;
        }
      }
    } else {
      // Find public based on module name.
      if (AbstractArtifactVO.LATEST.equals(moduleVersion)) {
        log.debug("~retrieveModule() : Searching for latest!");
        for (final ModuleVO eachModule : repositoryManager.findAll()) {
          final String name = eachModule.getName();
          if (moduleName.equals(name)) {
            found = eachModule;
            log.debug("~retrieveModule() : Returning public module '" + found.toString() + "'.");
            break;
          }
        }
      } else {
        found = repositoryManager.findModuleByNameAndVersion(moduleName, moduleVersion);
      }
    }

    return found;
  }

  /* (non-Javadoc)
   * @see uk.ac.ox.cs.science2020.zoon.client.service.ClientService#retrieveModuleTypes()
   */
  @Override
  public Map<String, String> retrieveModuleTypes() throws BusinessManagerWSInvocationException,
                                                          NoConnectionException {
    log.debug("~retrieveModuleTypes() : Invoked.");

    return businessManagerManager.retrieveModuleTypes();
  }

  /* (non-Javadoc)
   * @see uk.ac.ox.cs.science2020.zoon.client.service.ClientService#retrieveNonAdminUserByIdentity(java.lang.String)
   */
  @Override
  public UserDetails retrieveNonAdminUserByIdentity(final String identity) {
    log.debug("~retrieveNonAdminUserByIdentity() : Invoked for '" + identity + "'.");
    if (StringUtils.isBlank(identity)) {
      throw new IllegalArgumentException("Identity must be provided when retrieving non-admin user by identity!");
    }

    return userManager.retrieveNonAdminUserByIdentity(identity);
  }

  /* (non-Javadoc)
   * @see uk.ac.ox.cs.science2020.zoon.client.service.ClientService#retrievePrivateModules(java.lang.String, boolean)
   */
  @Override
  public Map<String, List<ModuleVO>> retrievePrivateModules(final String userName,
                                                            final boolean verifiedOnly)
                                                            throws BusinessManagerWSInvocationException,
                                                                   NoConnectionException,
                                                                   IllegalArgumentException {
    if (StringUtils.isBlank(userName)) {
      throw new IllegalArgumentException("A user name must be provided for private module retrieval!");
    }
    log.debug("~retrievePrivateModules() : Invoked for user name '" + userName + "'.");

    return businessManagerManager.retrievePrivateModules(userName, verifiedOnly);
  }

  /* (non-Javadoc)
   * @see uk.ac.ox.cs.science2020.zoon.client.service.ClientService#retrievePublicModules(uk.ac.ox.cs.science2020.zoon.client.value.object.user.UserDetails)
   */
  @Override
  public List<ModuleVO> retrievePublicModules(final UserDetails userDetails) {
    log.debug("~retrievePublicModules() : Invoked.");

    final List<ModuleVO> publicModules = new ArrayList<ModuleVO>();

    if (userDetails == null) {
      publicModules.addAll(repositoryManager.findAll());
    } else {
      publicModules.addAll(repositoryManager.findAll(userDetails.getIdentities()));
    }

    return publicModules;
  }
  /* (non-Javadoc)
   * @see uk.ac.ox.cs.science2020.zoon.client.service.ClientService#retrieveUsers()
   */
  @Override
  public List<UserDetails> retrieveUsers() {
    log.debug("~retrieveUsers() : Invoked.");

    return userManager.retrieveUsers();
  }

  /* (non-Javadoc)
   * @see uk.ac.ox.cs.science2020.zoon.client.service.ClientService#retrieveUsersByUsername(java.lang.String)
   */
  @Override
  public List<UserDetails> retrieveUsersByUsername(final String userName) {
    log.debug("~retrieveUsersByUsername() : Invoked for user name '" + userName + "'.");
    if (StringUtils.isBlank(userName)) {
      throw new IllegalArgumentException("User name required when retrieving users by user name!");
    }

    return userManager.retrieveUsersByUsername(userName);
  }

  /* (non-Javadoc)
   * @see uk.ac.ox.cs.science2020.zoon.client.service.ClientService#retrieveVerificationOutput(int)
   */
  @Override
  public VerificationResultsVO retrieveVerificationOutput(final int verificationIdentifier)
                                                          throws BusinessManagerWSInvocationException,
                                                                 NoConnectionException {
    log.debug("~retrieveVerificationOutput() : Invoked for '" + verificationIdentifier + "'.");

    return businessManagerManager.retrieveVerificationOutput(verificationIdentifier);
  }


  /* (non-Javadoc)
   * @see uk.ac.ox.cs.science2020.zoon.client.service.ClientService#retrieveWorkflowCall(java.lang.String, java.lang.String, java.lang.String, boolean)
   */
  @Override
  public WorkflowCallVO retrieveWorkflowCall(final String userName, final String workflowCallName,
                                             final String workflowCallVersion,
                                             final boolean retrievePrivate)
                                             throws BusinessManagerWSInvocationException,
                                                    IllegalArgumentException, NoConnectionException {
    log.debug("~retrieveWorkflowCall() : '" + userName + "' wants workflow call '" + workflowCallName + "', version '" + workflowCallVersion + "'.");
    if (StringUtils.isBlank(workflowCallName)) {
      throw new IllegalArgumentException("A workflow call name must be specified!");
    }
    final boolean hasUserName = !StringUtils.isBlank(userName);
    final boolean hasWorkflowCallVersion = !StringUtils.isBlank(workflowCallVersion);
    if (retrievePrivate && (!hasUserName || !hasWorkflowCallVersion)) {
      final String errorMessage = "Private modules require user name and call version identification";
      log.error("~retrieveWorkflowCall() : ".concat(errorMessage));
      throw new IllegalArgumentException(errorMessage);
    }

    WorkflowCallVO found = null;
    final Map<String, List<WorkflowCallVO>> workflowCalls = new HashMap<String, List<WorkflowCallVO>>();
    if (retrievePrivate) {
      // TODO : Should just retrieve a single value from private repos!!
      workflowCalls.putAll(businessManagerManager.retrievePrivateWorkflowCalls(userName));

      for (final Map.Entry<String, List<WorkflowCallVO>> workflowCallEntry : workflowCalls.entrySet()) {
        final String entryName = workflowCallEntry.getKey();
        if (workflowCallName.equals(entryName)) {
          for (final WorkflowCallVO versionedWorkflowCall : workflowCallEntry.getValue()) {
            if (workflowCallVersion != null &&
                workflowCallVersion.equals(versionedWorkflowCall.getVersion())) {
              found = versionedWorkflowCall;
              break;
            }
          }
        }
        if (found != null) {
          break;
        }
      }
    } else {
      // Find public based on workflow call name.
      if (AbstractArtifactVO.LATEST.equals(workflowCallVersion)) {
        log.debug("~retrieveWorkflowCall() : Searching for latest!");
        workflowCalls.putAll(repositoryManager.retrieveWorkflowCalls());
        final List<WorkflowCallVO> versionedWorkflowCalls = workflowCalls.get(workflowCallName);
        if (versionedWorkflowCalls != null) {
          if (versionedWorkflowCalls.size() == 1) {
            found = versionedWorkflowCalls.get(0);
          } else {
            throw new UnsupportedOperationException("Ability to handle empty or multi-versioned workflow call collections not yet implemented!");
          }
        }
      } else {
        found = repositoryManager.findWorkflowCallByNameAndVersion(workflowCallName,
                                                                   workflowCallVersion);
      }
    }

    return found;
  }

  private Map<String, List<WorkflowCallVO>> retrieveWorkflowCalls() throws BusinessManagerWSInvocationException,
                                                                           NoConnectionException {
    return retrieveWorkflowCalls(null, null, null, null);
  }

  /* (non-Javadoc)
   * @see uk.ac.ox.cs.science2020.zoon.client.service.ClientService#retrieveWorkflowCalls(java.lang.String, java.lang.String, java.lang.String, uk.ac.ox.cs.science2020.zoon.shared.value.type.ModuleType)
   */
  @Override
  public Map<String, List<WorkflowCallVO>> retrieveWorkflowCalls(final String userName,
                                                                 final String moduleName,
                                                                 final String moduleVersion,
                                                                 final ModuleType moduleType)
                                                                 throws BusinessManagerWSInvocationException,
                                                                        NoConnectionException {
    log.debug("~retrieveWorkflowCalls() : Invoked with user name '" + userName + "', module name '" + moduleName + "', " +
                                          "module version + '" + moduleVersion + "', module type '" + moduleType + "'.");

    final Map<String, List<WorkflowCallVO>> allWorkflowCalls = new HashMap<String, List<WorkflowCallVO>>();

    // Public/Elastic workflow calls.
    allWorkflowCalls.putAll(repositoryManager.retrieveWorkflowCalls());

    if (!StringUtils.isBlank(userName)) {
      // Private/Gitblit workflow calls.
      final Map<String, List<WorkflowCallVO>> privateWorkflowCall = businessManagerManager.retrievePrivateWorkflowCalls(userName);
      for (final Map.Entry<String, List<WorkflowCallVO>> eachPrivate : privateWorkflowCall.entrySet()) {
        final String workflowName = eachPrivate.getKey();
        final List<WorkflowCallVO> namedWorkflowCalls = eachPrivate.getValue();
        if (allWorkflowCalls.containsKey(workflowName)) {
          allWorkflowCalls.get(workflowName).addAll(namedWorkflowCalls);
        } else {
          allWorkflowCalls.put(workflowName, namedWorkflowCalls);
        }
      }
    }

    final Map<String, List<WorkflowCallVO>> returnWorkflowCalls = new HashMap<String, List<WorkflowCallVO>>();
    if (StringUtils.isBlank(moduleName)) {
      returnWorkflowCalls.putAll(allWorkflowCalls);
    } else {
      for (final Map.Entry<String, List<WorkflowCallVO>> versionedWorkflowCallEntries : allWorkflowCalls.entrySet()) {
        final String workflowCallName = versionedWorkflowCallEntries.getKey();
        for (final WorkflowCallVO workflowCall : versionedWorkflowCallEntries.getValue()) {
          if (workflowCall.referencesModule(moduleName, moduleVersion, moduleType)) {
            if (returnWorkflowCalls.containsKey(workflowCallName)) {
              returnWorkflowCalls.get(workflowCallName).add(workflowCall);
            } else {
              final List<WorkflowCallVO> filteredVersioned = new ArrayList<WorkflowCallVO>();
              filteredVersioned.add(workflowCall);
              returnWorkflowCalls.put(workflowCallName, filteredVersioned);
            }
          }
        }
      }
    }
    log.debug("~retrieveWorkflowCalls() : '" + returnWorkflowCalls.size() + "' workflow calls returned.");

    return returnWorkflowCalls;
  }

  private String saveArtifactInPrivate(final String userName, final String artifactContent,
                                       final ArtifactType artifactType)
                                       throws BusinessManagerWSInvocationException,
                                              NoConnectionException {
    assert (ControllerUtil.isValidUserName(userName)) : "Invalid user name '" + userName + "' encountered!";

    return businessManagerManager.saveArtifact(userName, artifactContent, artifactType);
  }

  /* (non-Javadoc)
   * @see uk.ac.ox.cs.science2020.zoon.client.service.ClientService#saveModuleInPrivateStore(java.lang.String, java.lang.String)
   */
  @Override
  public String saveModuleInPrivateStore(final String userName, final String moduleContent)
                                         throws BusinessManagerWSInvocationException,
                                                NoConnectionException {
    log.debug("~saveModuleInPrivateStore() : Invoked with '" + moduleContent + "'.");

    return saveArtifactInPrivate(userName, moduleContent, ArtifactType.MODULE);
  }

  /* (non-Javadoc)
   * @see uk.ac.ox.cs.science2020.zoon.client.service.ClientService#saveModuleInZOONStore(java.lang.String, uk.ac.ox.cs.science2020.zoon.client.value.object.ModulePublishVO)
   */
  @Override
  public String saveModuleInZOONStore(final String userName, final ModulePublishVO modulePublish)
                                      throws BusinessManagerWSInvocationException,
                                             NoConnectionException {
    log.debug("~saveModuleInZOONStore() : Invoked.");

    if (StringUtils.isBlank(userName)) {
      throw new IllegalArgumentException("A user name must be provided when saving a module in the ZOON repository.");
    }
    if (modulePublish == null || !modulePublish.hasValidContent()) {
      throw new IllegalArgumentException("Null or invalid data detected in the module publish request.");
    }

    return businessManagerManager.saveModuleInZOONStore(userName, modulePublish);

    // TODO : Step 2. Update elastic search data.
  }

  /* (non-Javadoc)
   * @see uk.ac.ox.cs.science2020.zoon.client.service.ClientService#saveWorkflowCallInPrivateStore(java.lang.String, java.lang.String)
   */
  @Override
  public String saveWorkflowCallInPrivateStore(final String userName, final String workflowCall)
                                               throws BusinessManagerWSInvocationException,
                                                      NoConnectionException {
    log.debug("~saveWorkflowCallInPrivateStore() : Invoked for '" + userName + "'.");

    return saveArtifactInPrivate(userName, workflowCall, ArtifactType.WORKFLOW_CALL);
  }

  /* (non-Javadoc)
   * @see uk.ac.ox.cs.science2020.zoon.client.service.ClientService#search(java.lang.String)
   */
  @Override
  public Map<String, Object> search(final String searchTerm) throws BusinessManagerWSInvocationException,
                                                                    NoConnectionException {
    log.debug("~search() : Invoked for '" + searchTerm + "'.");
    final String lcSearchTerm = searchTerm.toLowerCase();

    final Map<String, Object> searchResults = new HashMap<String, Object>();

    /*
     * Retrieve modules
     */
    final Map<String, List<ModuleVO>> moduleVOs = retrieveAllModules(null, true);
    if (!moduleVOs.isEmpty()) {
      for (final Map.Entry<String, List<ModuleVO>> moduleEntry : moduleVOs.entrySet()) {
        final String moduleName = moduleEntry.getKey();
        for (final ModuleVO eachModule : moduleEntry.getValue()) {
          for (final AuthorVO eachAuthor : eachModule.getAuthors()) {
            final String authorName = eachAuthor.getAuthorName();
            if (authorName.toLowerCase().contains(lcSearchTerm)) {
              appendToResults(searchResults, "profiles", authorName, null);
            }
          }
          if (eachModule.getAuthors().toString().toLowerCase().contains(lcSearchTerm)) {
          }
        }
        if (moduleName.toLowerCase().contains(lcSearchTerm)) {
          final ModuleType moduleType = moduleEntry.getValue().get(0).getModuleType();
          appendToResults(searchResults, "modules", moduleName, moduleType);
        }
      }
    }

    /*
     * Retrieve workflow calls.
     */
    final Map<String, List<WorkflowCallVO>> workflowCalls = retrieveWorkflowCalls();
    if (!workflowCalls.isEmpty()) {
      for (final Map.Entry<String, List<WorkflowCallVO>> workflowCallEntry : workflowCalls.entrySet()) {
        final String workflowCallName = workflowCallEntry.getKey();
        for (final WorkflowCallVO eachWorkflowCall : workflowCallEntry.getValue()) {
          for (final AuthorVO eachAuthorVO : eachWorkflowCall.getAuthors()) {
            final String authorName = eachAuthorVO.getAuthorName();
            if (authorName.toLowerCase().contains(lcSearchTerm)) {
              appendToResults(searchResults, "profiles", authorName, null);
            }
          }
        }
        if (workflowCallName.toLowerCase().contains(lcSearchTerm)) {
          appendToResults(searchResults, "workflow calls", workflowCallName, null);
        }
      }
    }

    final List<UserDetails> allUserDetails = userManager.retrieveUsers();
    if (!allUserDetails.isEmpty()) {
      for (final UserDetails userDetails: allUserDetails) {
        final Set<Identity> userIdentities = userDetails.getIdentities();
        for (final Identity identity : userIdentities) {
          final String identityName = identity.getIdentity();
          if (identityName.toLowerCase().contains(lcSearchTerm)) {
            appendToResults(searchResults, "profiles", identityName, null);
          }
        }
      }
    }

    return searchResults;
  }

  /* (non-Javadoc)
   * @see uk.ac.ox.cs.science2020.zoon.client.service.ClientService#setUserEnabled(java.lang.String, boolean)
   */
  @Override
  public void setUserEnabled(final String name, final boolean enabled) {
    log.debug("~setUserEnabled() : Setting '" + enabled + "' on user '" + name + "'.");
    if (StringUtils.isBlank(name)) {
      throw new IllegalArgumentException("User name must be provided when setting 'enabled' flag.");
    }

    userManager.setEnabled(name, enabled);
  }

  /* (non-Javadoc)
   * @see uk.ac.ox.cs.science2020.zoon.client.service.ClientService#verifyModule(java.lang.String, java.lang.String, java.lang.String)
   */
  @Override
  public int verifyModule(final String userName, final String moduleName, final String moduleVersion)
                          throws BusinessManagerWSInvocationException, NoConnectionException,
                                 IllegalArgumentException {
    if (StringUtils.isBlank(userName) || StringUtils.isBlank(moduleName) ||
        StringUtils.isBlank(moduleVersion)) {
      throw new IllegalArgumentException("User name, module name, module version must be provided in order to verify a module!");
    }

    log.debug("~verifyModule() : Invoked for '" + moduleName + "', '" + moduleVersion + "'.");

    int identifier = -1;

    identifier = businessManagerManager.verifyModule(userName, moduleName, moduleVersion);

    return identifier;
  }
}