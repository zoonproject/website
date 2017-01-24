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
package uk.ac.ox.cs.science2020.zoon.business_manager.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import uk.ac.ox.cs.science2020.zoon.business_manager.value.object.RInvocationResultsVO;
import uk.ac.ox.cs.science2020.zoon.business_manager.value.object.artifact.MinimumArtifactDataVO;
import uk.ac.ox.cs.science2020.zoon.business_manager.value.object.moduleheader.HeaderObject;
import uk.ac.ox.cs.science2020.zoon.shared.value.object.artifact.workflowcall.WorkflowCallVO;
import uk.ac.ox.cs.science2020.zoon.shared.value.type.ArtifactType;
import uk.ac.ox.cs.science2020.zoon.shared.value.type.DataIdentifier;

/**
 * Interface to artifact services.
 *
 * @author 
 */
public interface ArtifactService {

  /**
   * Delete the module.
   *
   * @param minimumArtifactData Minimum artifact data.
   * @return Deletion outcome information.
   * @throws IllegalArgumentException If {@code null} or empty values passed. 
   */
  String deleteModule(MinimumArtifactDataVO minimumArtifactData) throws IllegalArgumentException;

  /**
   * Retrieve a collection of private modules for the user.
   * 
   * @param userId User identifier.
   * @param minimal True if only retrieving the minimal data, e.g. name, version, type.
   * @param latest True if retrieving only the latest version of a module, otherwise false to 
   *               retrieve all versions.
   * @param verifiedOnly {@code true} if verified modules only, otherwise {@code false}.
   * @return Collection of private modules available (or empty collection if none available).
   */
  // k:module name, v:module data (each item in list refers to a version of the keyed module).
  Map<String, List<Map<DataIdentifier, HeaderObject>>> retrievePrivateModules(int userId,
                                                                              boolean minimal,
                                                                              boolean latest,
                                                                              boolean verifiedOnly);

  /**
   * 
   * @param userId
   * @param minimal
   * @param latest
   * @return
   */
  Set<WorkflowCallVO> retrievePrivateWorkflowCalls(int userId, boolean minimal, boolean latest);

  /**
   * Retrieve the results of artifact verification.
   * 
   * @param verificationIdentifier Verification identifier.
   * @return Verification results, or {@code null} if not found.
   */
  RInvocationResultsVO retrieveVerificationResults(int verificationIdentifier);

  /**
   * Store an artifact.
   * 
   * @param userId User identifier.
   * @param artifactContent Artifact to store.
   * @param type Artifact type, e.g. module or workflow call.
   * @return Storage attempt textual outcome.
   * @throws IllegalArgumentException If {@code null} or empty artifact value passed. 
   */
  String saveArtifact(int userId, String artifactContent, ArtifactType type)
                      throws IllegalArgumentException;

  /**
   * Upload the specified private module to the ZOON store and optionally delete from private store.
   * 
   * @param minimumArtifactData Minimum artifact data.
   * @param moduleType Module type.
   * @param removeAfterUpload {@code true} if to remove module from temporary store after upload.
   * @return Textual information.
   * @throws IllegalArgumentException If {@code null} or blank assigned for any parameter.
   */
  String uploadPrivateModuleToZOONStore(MinimumArtifactDataVO minimumArtifactData,
                                        String moduleType, boolean removeAfterUpload) throws IllegalArgumentException;

  /**
   * Verify the artifact.
   *
   * @param minimumArtifactData Minimum artifact data.
   * @return Verification identifier.
   * @throws IllegalArgumentException If {@code null} or empty artifact value passed. 
   */
  int verifyArtifact(MinimumArtifactDataVO minimumArtifactData) throws IllegalArgumentException;

}