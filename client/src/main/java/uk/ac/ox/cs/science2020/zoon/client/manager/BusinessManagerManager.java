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
package uk.ac.ox.cs.science2020.zoon.client.manager;

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
 * Interface to business manager services.
 * <p>
 * <b>Note</b> : Business manager services generally handle the private modules and workflow calls.
 * Public modules and workflow calls are generally retrieved via the {@linkplain RepositoryManager}
 * calls to the elastic repository.
 * 
 * @author geoff
 */
public interface BusinessManagerManager {

  /**
   * Delete a private module.
   * 
   * @param userName User name .
   * @param moduleName Module name.
   * @param moduleVersion Module version.
   * @return Textual information of action outcome.
   * @throws BusinessManagerWSInvocationException Business Manager problem.
   * @throws NoConnectionException Connection to Business Manager problem.
   */
  String deleteModule(String userName, String moduleName, String moduleVersion)
                      throws BusinessManagerWSInvocationException, NoConnectionException;

  /**
   * Retrieve module types.
   * 
   * @return Collection of module types.
   * @throws BusinessManagerWSInvocationException Business Manager problem.
   * @throws NoConnectionException Connection to Business Manager problem.
   */
  Map<String, String> retrieveModuleTypes() throws BusinessManagerWSInvocationException,
                                                   NoConnectionException;

  /**
   * Retrieve private modules only.
   * 
   * @param userName User name.
   * @param verifiedOnly Retrieve only verified private modules if {@code true}, otherwise all. 
   * @return User's private modules.
   * @throws BusinessManagerWSInvocationException Business Manager problem.
   * @throws NoConnectionException Connection to Business Manager problem.
   * @throws NullPointerException If no user name is provided.
   */
  Map<String, List<ModuleVO>> retrievePrivateModules(String userName,
                                                     boolean verifiedOnly)
                                                     throws BusinessManagerWSInvocationException,
                                                            NoConnectionException,
                                                            NullPointerException;

  /**
   * Retrieve private workflow calls only.
   * 
   * @param userName User name.
   * @return User's private workflow calls.
   * @throws BusinessManagerWSInvocationException Business Manager problem.
   * @throws NoConnectionException Connection to Business Manager problem.
   * @throws NullPointerException If no user name is provided.
   */
  Map<String, List<WorkflowCallVO>> retrievePrivateWorkflowCalls(String userName)
                                                                 throws BusinessManagerWSInvocationException,
                                                                        NoConnectionException,
                                                                        NullPointerException;

  /**
   * Retrieve the output of the verification process.
   * 
   * @param verificationIdentifier Verification identifier.
   * @return Verification information object.
   * @throws BusinessManagerWSInvocationException Business Manager problem.
   * @throws NoConnectionException Connection to Business Manager problem.
   */
  VerificationResultsVO retrieveVerificationOutput(int verificationIdentifier) throws BusinessManagerWSInvocationException,
                                                                                      NoConnectionException;

  /**
   * Persist the artifact in the private repository.
   * 
   * @param userName User name.
   * @param artifactContent Artifact string representation.
   * @param artifactType Artifact type, e.g. module, workflow call.
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
   * @param modulePublish Module data for publishing.
   * @return Action outcome.
   * @throws BusinessManagerWSInvocationException Business Manager problem.
   * @throws NoConnectionException Connection to Business Manager problem.
   */
  String saveModuleInZOONStore(String userName, ModulePublishVO modulePublish)
                               throws BusinessManagerWSInvocationException,
                                      NoConnectionException;

  /**
   * Verify a private module.
   * 
   * @param userName User name (non-{@code null}).
   * @param moduleName Module name.
   * @param moduleVersion Module version.
   * @return Verification identifier.
   * @throws BusinessManagerWSInvocationException Business Manager problem.
   * @throws NoConnectionException Connection to Business Manager problem.
   */
  int verifyModule(String userName, String moduleName, String moduleVersion)
                   throws BusinessManagerWSInvocationException, NoConnectionException;

}