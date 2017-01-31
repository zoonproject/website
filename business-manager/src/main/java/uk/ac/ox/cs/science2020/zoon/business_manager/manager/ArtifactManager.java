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
package uk.ac.ox.cs.science2020.zoon.business_manager.manager;

import java.util.List;
import java.util.Map;
import java.util.Set;

import uk.ac.ox.cs.science2020.zoon.business_manager.value.object.RInvocationResultsVO;
import uk.ac.ox.cs.science2020.zoon.business_manager.value.object.artifact.MinimumArtifactDataVO;
import uk.ac.ox.cs.science2020.zoon.business_manager.value.object.moduleheader.HeaderObject;
import uk.ac.ox.cs.science2020.zoon.shared.value.object.ActionOutcomeVO;
import uk.ac.ox.cs.science2020.zoon.shared.value.object.artifact.workflowcall.WorkflowCallVO;
import uk.ac.ox.cs.science2020.zoon.shared.value.type.ArtifactType;
import uk.ac.ox.cs.science2020.zoon.shared.value.type.DataIdentifier;
import uk.ac.ox.cs.science2020.zoon.shared.value.type.ModuleType;

/**
 * Artifact management interface.
 *
 * @author 
 */
public interface ArtifactManager {

  /**
   * Clean up statics after artifact parsing has completed.
   * 
   * @param parseIdentifier Parse identifier.
   */
  void cleanUpParseStatics(int parseIdentifier);

  /**
   * Clean up statics after artifact verification has completed.
   * 
   * @param verificationIdentifier Verification identifier.
   */
  void cleanUpVerificationStatics(int verificationIdentifier);

  /**
   * Remove the specified module from the temporary store.
   * 
   * @param minimumArtifactData Minimum artifact data.
   * @param moduleType Module type.
   * @return Value object containing outcome of upload action.
   */
  ActionOutcomeVO deleteModule(MinimumArtifactDataVO minimumArtifactData, ModuleType moduleType);

  /**
   * Initiate the parsing of the artifact, retrieving the identifier by which to query periodically
   * for parsing progress.
   * 
   * @param artifactType Type of artifact.
   * @param artifactName Artifact name.
   * @param artifactContent Artifact content.
   * @return Parse identifier.
   */
  Integer initiateArtifactParse(ArtifactType artifactType, String artifactName,
                                String artifactContent);

  /**
   * Retrieve the results of the R module parsing.
   * 
   * @param parseIdentifier Parsing identifier.
   * @return Parse results, or {@code null} if not found.
   */
  RInvocationResultsVO retrieveParseResults(int parseIdentifier);

  /**
   * Retrieve the private artifact.
   *
   * @param minimumArtifactData Minimum artifact data.
   * @return Artifact if exists, otherwise {@code null}.
   */
  String retrievePrivateArtifact(MinimumArtifactDataVO minimumArtifactData);

  /**
   * Retrieve a collection of every module available.
   * 
   * @param userId User identifier (Optional).
   * @param minimal True if only retrieving the minimal data, e.g. name, version, type.
   * @param latest True if retrieving only the latest versions, otherwise false to retrieve all
   *               versions.
   * @param verifiedOnly {@code true} if verified modules only, otherwise {@code false}.
   * @return Collection of all the modules available (or empty collection if none available).
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
   * @param artifactContent Content of artifact.
   * @param type Artifact type.
   * @return Storage attempt textual outcome.
   */
  String saveArtifact(final int userId, final String artifactContent, final ArtifactType type);

  /**
   * Upload the specified private module to the ZOON store.
   * 
   * @param minimumArtifactData The minimum artifact data.
   * @param moduleType Module type.
   * @return Value object containing outcome of upload action.
   */
  ActionOutcomeVO uploadPrivateModuleToZOONStore(MinimumArtifactDataVO minimumArtifactData,
                                                 ModuleType moduleType);

  /**
   * Verify the artifact.
   *
   * @param minimumArtifactData The minimum artifact data.
   * @return Verification identifier.
   * @throws IllegalArgumentException If {@code null} or empty artifact args passed. 
   */
  int verifyArtifact(MinimumArtifactDataVO minimumArtifactData) throws IllegalArgumentException;

}