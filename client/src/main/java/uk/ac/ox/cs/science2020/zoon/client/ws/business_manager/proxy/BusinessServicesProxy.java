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
package uk.ac.ox.cs.science2020.zoon.client.ws.business_manager.proxy;

import java.util.List;
import java.util.Map;

import uk.ac.ox.cs.science2020.zoon.client.exception.BusinessManagerWSInvocationException;
import uk.ac.ox.cs.science2020.zoon.client.exception.NoConnectionException;
import uk.ac.ox.cs.science2020.zoon.client.value.object.ModulePublishVO;
import uk.ac.ox.cs.science2020.zoon.client.value.object.VerificationResultsVO;
import uk.ac.ox.cs.science2020.zoon.shared.value.object.artifact.module.ModuleVO;
import uk.ac.ox.cs.science2020.zoon.shared.value.object.artifact.workflowcall.WorkflowCallVO;
import uk.ac.ox.cs.science2020.zoon.shared.value.type.ArtifactType;

/**
 * Business Manager manager interface.
 * 
 * @author geoff
 */
public interface BusinessServicesProxy {

  public static final String UNDEFINED = "undefined";

  /**
   * Delete a private module.
   * 
   * @param userName User name.
   * @param moduleName Module name.
   * @param moduleVersion Module version.
   * @return Textual information of outcome.
   * @throws BusinessManagerWSInvocationException Business Manager problem.
   * @throws NoConnectionException Connection to Business Manager problem.
   */
  String deleteModule(String userName, String moduleName, String moduleVersion)
                      throws BusinessManagerWSInvocationException, NoConnectionException;

  /**
   * Retrieve a private module.
   * 
   * @param userName User name.
   * @param moduleName Module name.
   * @param moduleVersion Module version.
   * @return Private module, or {@code null} if not found.
   * @throws BusinessManagerWSInvocationException Business Manager problem.
   * @throws NoConnectionException Connection to Business Manager problem.
   */
  String retrieveModule(String userName, String moduleName, String moduleVersion)
                        throws BusinessManagerWSInvocationException, NoConnectionException;

  /**
   * Retrieve private modules only.
   * 
   * @param userName User name.
   * @param verifiedOnly Retrieve only verified private modules if {@code true}, otherwise all. 
   * @return User's private modules, empty if none found.
   * @throws BusinessManagerWSInvocationException Business Manager problem.
   * @throws NoConnectionException Connection to Business Manager problem.
   */
  Map<String, List<ModuleVO>> retrievePrivateModules(String userName, boolean verifiedOnly)
                                                     throws BusinessManagerWSInvocationException,
                                                            NoConnectionException;

  /**
   * Retrieve private workflow calls only.
   * 
   * @param userName User name.
   * @return User's private workflow calls, empty if none found.
   * @throws BusinessManagerWSInvocationException Business Manager problem.
   * @throws NoConnectionException Connection to Business Manager problem.
   */
  Map<String, List<WorkflowCallVO>> retrievePrivateWorkflowCalls(String userName)
                                                                 throws BusinessManagerWSInvocationException,
                                                                        NoConnectionException;

  /**
   * Retrieve the module types.
   * 
   * @return Collection of module types, empty if none found.
   * 
   * @throws BusinessManagerWSInvocationException Business Manager problem.
   * @throws NoConnectionException Connection to Business Manager problem.
   */
  Map<String, String> retrieveModuleTypes() throws BusinessManagerWSInvocationException,
                                                   NoConnectionException;

  /**
   * Retrieve the output of the verification process.
   * 
   * @param verificationIdentifier Verification identifier.
   * @return Verification information object, or {@code null} if verification identifier not valid.
   * @throws BusinessManagerWSInvocationException Business Manager problem.
   * @throws NoConnectionException Connection to Business Manager problem.
   */
  VerificationResultsVO retrieveVerificationOutput(int verificationIdentifier)
                                                   throws BusinessManagerWSInvocationException,
                                                          NoConnectionException;

  /**
   * Save a module or workflow call.
   * 
   * @param userName User name.
   * @param artifactContent Artifact content.
   * @param artifactType Artifact type.
   * @return Action outcome.
   * @throws BusinessManagerWSInvocationException Business Manager problem.
   * @throws NoConnectionException Connection to Business Manager problem.
   */
  String saveArtifact(String userName, String artifactContent, ArtifactType artifactType)
                      throws BusinessManagerWSInvocationException, NoConnectionException;

  /**
   * Save a module in the ZOON repository.
   * 
   * @param userName Current user name.
   * @param modulePublish Module data in preparation for publishing.
   * @return Information message from the processing.
   * @throws BusinessManagerWSInvocationException Business Manager problem.
   * @throws NoConnectionException Connection to Business Manager problem.
   */
  String saveModuleInZOONStore(String userName, ModulePublishVO modulePublish)
                               throws BusinessManagerWSInvocationException,
                                      NoConnectionException;

  /**
   * Verify a private module..
   * 
   * @param userName User name.
   * @param moduleName Module name.
   * @param moduleVersion Module version.
   * @return Verification identifier.
   * @throws BusinessManagerWSInvocationException Business Manager problem.
   * @throws NoConnectionException Connection to Business Manager problem.
   */
  int verifyModule(String userName, String moduleName, String moduleVersion)
                   throws BusinessManagerWSInvocationException, NoConnectionException;
}