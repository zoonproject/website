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
package uk.ac.ox.cs.science2020.zoon.business_manager.business.artifact;

import java.util.List;
import java.util.Map;
import java.util.Set;

import uk.ac.ox.cs.science2020.zoon.business_manager.value.object.artifact.MinimumArtifactDataVO;
import uk.ac.ox.cs.science2020.zoon.business_manager.value.object.moduleheader.HeaderObject;
import uk.ac.ox.cs.science2020.zoon.business_manager.value.object.store.ArtifactStoreVO;
import uk.ac.ox.cs.science2020.zoon.shared.value.object.ActionOutcomeVO;
import uk.ac.ox.cs.science2020.zoon.shared.value.object.artifact.workflowcall.WorkflowCallVO;
import uk.ac.ox.cs.science2020.zoon.shared.value.type.ArtifactType;
import uk.ac.ox.cs.science2020.zoon.shared.value.type.DataIdentifier;

/**
 * Interface to indicate that the object represents a data store.
 *
 * @author 
 */
public interface Store {

  /**
   * Retrieve the store name.
   * 
   * @return Name of artifact store.
   */
  String getName();

  /**
   * Indicate if the store contains public artifacts.
   * 
   * @return {@code true} if store contains public artifacts, otherwise {@code false}.
   */
  boolean hasPublicArtifacts();

  /**
   * Indicate if store is a temporary store for private artifacts.
   * 
   * @return {@code true} if artifacts can be stored temporarily, otherwise {@code false}.
   */
  boolean isTemporaryStore();

  /**
   * Indicate if this is the principal ZOON repository.
   * 
   * @return {@code true} if the ZOON repository, otherwise {@code false}.
   */
  boolean isZOONStore();

  /**
   * Remove the specified artifact.
   * 
   * @param minimumArtifactData Minimum artifact data.
   * @return Value object containing outcome of remove action.
   */
  ActionOutcomeVO deleteArtifact(MinimumArtifactDataVO minimumArtifactData);

  /**
   * Load modules (for elasticsearch) on startup.
   * <p>
   * The intention is for modules to be read from somewhere, perhaps the ZOON GitHub or a local
   * filesystem clone of it, and then passed through Tom's module verifier to ensure all modules
   * are properly defined, and for the results to be placed into elasticsearch.
   */
  Map<String, List<Map<DataIdentifier, HeaderObject>>> loadPublicModulesOnStartup();

  /**
   * Retrieve the specified artifact.
   * 
   * @param minimumArtifactData Minimum artifact data.
   * @return Requested artifact, or {@code null} if not available.
   */
  String retrieveArtifact(MinimumArtifactDataVO minimumArtifactData);

  /**
   * Retrieve private the modules from the store.
   * 
   * @param userId User identifier.
   * @param minimal True if only retrieving the minimal data, e.g. name, version, type.
   * @param verifiedOnly {@code true} if verified modules only, otherwise {@code false}.
   * @return Collection of all the modules available (or empty collection if none available).
   */
  Map<String, List<Map<DataIdentifier, HeaderObject>>> retrievePrivateModules(int userId,
                                                                              boolean minimal,
                                                                              boolean verifiedOnly);

  /**
   * Retrieve private the workflow calls from the store.
   * 
   * @param userId User identifier.
   * @param minimal True if only retrieving the minimal data, e.g. name, version.
   * @return Collection of all the workflow calls available (or empty collection if none available).
   */
  Set<WorkflowCallVO> retrievePrivateWorkflowCalls(int userId, boolean minimal);

  /**
   * Store the artifact in the ZOON repository.
   * 
   * @param artifactName Artifact name.
   * @param content Artifact content, e.g. raw module or workflow call.
   * @param type Artifact type.
   * @return Value object containing outcome of store action.
   */
  ActionOutcomeVO storePermanentArtifact(String artifactName, String content, ArtifactType type);

  /**
   * Store an artifact in the writable temporary store.
   * 
   * @param artifactStore Artifact data to store.
   * @return Storage outcome.
   */
  String storeTemporaryArtifact(ArtifactStoreVO artifactStore);

  /**
   * Write verification outcome.
   * 
   * @param minimumArtifactData Minimum artifact data.
   * @param outcome {@code true} if verification responded 'success', otherwise {@code false}.
   */
  void writeVerificationOutcome(MinimumArtifactDataVO minimumArtifactData, boolean outcome);
}