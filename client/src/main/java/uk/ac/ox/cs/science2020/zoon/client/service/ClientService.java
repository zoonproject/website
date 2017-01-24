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

import java.util.List;
import java.util.Map;

import uk.ac.ox.cs.science2020.zoon.client.exception.BusinessManagerWSInvocationException;
import uk.ac.ox.cs.science2020.zoon.client.exception.NoConnectionException;
import uk.ac.ox.cs.science2020.zoon.client.value.object.ModuleActionVO;
import uk.ac.ox.cs.science2020.zoon.client.value.object.ModulePublishVO;
import uk.ac.ox.cs.science2020.zoon.client.value.object.VerificationResultsVO;
import uk.ac.ox.cs.science2020.zoon.client.value.object.user.UserDetails;
import uk.ac.ox.cs.science2020.zoon.shared.value.object.ActionOutcomeVO;
import uk.ac.ox.cs.science2020.zoon.shared.value.object.artifact.module.ModuleVO;
import uk.ac.ox.cs.science2020.zoon.shared.value.object.artifact.workflowcall.WorkflowCallVO;
import uk.ac.ox.cs.science2020.zoon.shared.value.type.ModuleType;

/**
 * Interface to client services.
 * 
 * @author geoff
 */
public interface ClientService {

  /**
   * Add an identity to the user.
   * 
   * @param userName User name.
   * @param identity Identity to add.
   * @param email Optional email to associate with identity.
   * @return Outcome of operation.
   */
  ActionOutcomeVO addUserIdentity(String userName, String identity, String email);

  /**
   * Delete a private module.
   * 
   * @param userName User name.
   * @param moduleDelete Module deletion value object.
   * @return Verification identifier.
   * @throws BusinessManagerWSInvocationException
   * @throws NoConnectionException
   * @throws IllegalArgumentException If a {@code null} or empty argument is passed.
   */
  String deleteModule(String userName, ModuleActionVO moduleDelete)
                      throws BusinessManagerWSInvocationException, NoConnectionException,
                             IllegalArgumentException;

  /**
   * Delete a user.
   * 
   * @param userName User to delete.
   * @return {@code true} if deleted, otherwise {@code false}.
   */
  boolean deleteUser(String userName);

  /**
   * User registration request.
   * 
   * @param identityName ZOON identity name.
   * @param email Email address.
   * @param userName Name for the system.
   * @param password Password.
   * @return {@code null} if user registered, otherwise textual rejection information.
   */
  String registerUser(String identityName, String email, String userName, String password);

  /**
   * Retrieve all public repository modules and any user-specific ones if {@code userName} provided.
   * 
   * 
   * @param userName User name (Optional).
   * @param latestOnly If {@code true}, retrieve only the latest module version in the collection of
   *                   {@code moduleVO}s, otherwise return all versions.
   * @return Retrieve all accessible modules.
   * @throws BusinessManagerWSInvocationException
   * @throws NoConnectionException
   */
  Map<String, List<ModuleVO>> retrieveAllModules(String userName, boolean latestOnly)
                                                 throws BusinessManagerWSInvocationException,
                                                        NoConnectionException;

  /**
   * Retrieve a module.
   * 
   * @param userName User name.
   * @param moduleName Name of module.
   * @param moduleVersion Version of module.
   * @param retrievePrivate {@code true} if to retrieve private (requires user name), otherwise
   *                        {@code false} for public.
   * @return Module specified, otherwise {@code null} if not found.
   * @throws BusinessManagerWSInvocationException
   * @throws IllegalArgumentException If {@code null} is passed for module name, or invalid combination.
   * @throws NoConnectionException
   */
  ModuleVO retrieveModule(String userName, String moduleName, String moduleVersion,
                          boolean retrievePrivate)
                          throws BusinessManagerWSInvocationException,
                                 IllegalArgumentException, NoConnectionException;

  /**
   * 
   * @return
   * @throws BusinessManagerWSInvocationException
   * @throws NoConnectionException
   */
  Map<String, String> retrieveModuleTypes() throws BusinessManagerWSInvocationException,
                                                   NoConnectionException;

  /**
   * Retrieve a registered non-admin user by identifier, e.g. ZOON Developers.
   * 
   * @param identity Identity.
   * @return User with specified identity, or {@code null} if not found.
   */
  UserDetails retrieveNonAdminUserByIdentity(String identity);

  /**
   * Retrieve public modules data (from the elastic database).
   * 
   * @param userDetails Modules with the specified author names, otherwise {@code null} if all
   *                    public modules.
   * @return Public modules, or empty collection if none found.
   */
  List<ModuleVO> retrievePublicModules(UserDetails userDetails);

  /**
   * Retrieve private modules only.
   * 
   * @param userName User name.
   * @param verifiedOnly Retrieve only verified private modules if {@code true}, otherwise all. 
   * @return User's private modules.
   * @throws BusinessManagerWSInvocationException
   * @throws NoConnectionException
   * @throws IllegalArgumentException If {@code null} or empty user name parameter supplied.
   */
  Map<String, List<ModuleVO>> retrievePrivateModules(String userName,
                                                     boolean verifiedOnly)
                                                     throws BusinessManagerWSInvocationException,
                                                            NoConnectionException,
                                                            IllegalArgumentException;

  /**
   * Retrieve all registered users.
   * 
   * @return All registered users, or empty collection if none found.
   */
  List<UserDetails> retrieveUsers();

  /**
   * Retrieve the collection of user identities based on their singular user name.
   * 
   * @param userName
   * @return
   */
  List<UserDetails> retrieveUsersByUsername(String userName);

  /**
   * Retrieve the output of the verification process.
   * 
   * @param verificationIdentifier Verification identifier.
   * @return Verification information object.
   * @throws BusinessManagerWSInvocationException
   * @throws NoConnectionException
   */
  VerificationResultsVO retrieveVerificationOutput(int verificationIdentifier)
                                                   throws BusinessManagerWSInvocationException,
                                                          NoConnectionException;

  /**
   * Retrieve the specified workflow call.
   * 
   * @param userName Current user name.
   * @param workflowCallName Name of workflow call.
   * @param workflowCallVersion Version of workflow call.
   * @param retrievePrivate {@code true} if to retrieve private (requires user name), otherwise
   *                        {@code false} for public.
   * @return Corresponding workflow call, or {@code null} if doesn't exist or not "visible".
   * @throws BusinessManagerWSInvocationException Problem communicating with business manager.
   * @throws IllegalArgumentException If {@code null} workflow call name passed.
   * @throws NoConnectionException If cannot connect to application manager.
   */
  WorkflowCallVO retrieveWorkflowCall(String userName, String workflowCallName,
                                      String workflowCallVersion, boolean retrievePrivate)
                                      throws BusinessManagerWSInvocationException,
                                             IllegalArgumentException, NoConnectionException;

  /**
   * Retrieve all public repository workflow calls and any user-specific ones if {@code userName}
   * is provided.
   * <p>
   * If {@code moduleName} (and {@code moduleVersion}) is/are provided then only return the
   * workflows which reference it.
   * 
   * @param userName Optional user name.
   * @param moduleName Optional module name.
   * @param moduleVersion Optional module version.
   * @param moduleType Type of module.
   * @return Retrieve all accessible workflow calls.
   * @throws BusinessManagerWSInvocationException
   * @throws NoConnectionException
   */
  Map<String, List<WorkflowCallVO>> retrieveWorkflowCalls(String userName, String moduleName,
                                                          String moduleVersion,
                                                          ModuleType moduleType)
                                                          throws BusinessManagerWSInvocationException,
                                                                 NoConnectionException;

  /**
   * Save a module in the private store.
   * 
   * @param userName Current user name.
   * @param moduleContent Content of module.
   * @return Information message from the processing.
   * @throws BusinessManagerWSInvocationException
   * @throws NoConnectionException
   */
  String saveModuleInPrivateStore(String userName, String moduleContent)
                                  throws BusinessManagerWSInvocationException,
                                         NoConnectionException;

  /**
   * Save a module in the ZOON repository.
   * 
   * @param userName Current user name.
   * @param modulePublish Module data in preparation for publishing.
   * @return Information message from the processing.
   * @throws BusinessManagerWSInvocationException
   * @throws NoConnectionException
   * @throws IllegalArgumentException
   */
  String saveModuleInZOONStore(String userName, ModulePublishVO modulePublish)
                               throws BusinessManagerWSInvocationException,
                                      NoConnectionException, IllegalArgumentException;

  /**
   * Store the workflow call in the temporary private store.
   * 
   * @param userName
   * @param workflow call
   * @return
   * @throws BusinessManagerWSInvocationException
   * @throws NoConnectionException
   */
  String saveWorkflowCallInPrivateStore(String userName, String workflowCall)
                                        throws BusinessManagerWSInvocationException,
                                               NoConnectionException;

  /**
   * 
   * @param searchTerm
   * @return
   * @throws BusinessManagerWSInvocationException
   * @throws NoConnectionException
   */
  Map<String, Object> search(String searchTerm)
                             throws BusinessManagerWSInvocationException, NoConnectionException;

  /**
   * Assign a user's enabled status.
   * 
   * @param name Username.
   * @param enabled Enabled status.
   */
  void setUserEnabled(String name, boolean enabled);

  /**
   * Verify a private module.
   * 
   * @param userName User name (non-{@code null}).
   * @param moduleName Module name.
   * @param moduleVersion Module version.
   * @return Verification identifier.
   * @throws BusinessManagerWSInvocationException
   * @throws NoConnectionException
   * @throws IllegalArgumentException If a {@code null} or empty argument is passed.
   */
  int verifyModule(String userName, String moduleName, String moduleVersion)
                   throws BusinessManagerWSInvocationException, NoConnectionException,
                          IllegalArgumentException;

}