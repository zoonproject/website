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

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import uk.ac.ox.cs.science2020.zoon.business_manager.BusinessIdentifiers;
import uk.ac.ox.cs.science2020.zoon.business_manager.manager.ArtifactManager;
import uk.ac.ox.cs.science2020.zoon.business_manager.value.object.RInvocationResultsVO;
import uk.ac.ox.cs.science2020.zoon.business_manager.value.object.artifact.MinimumArtifactDataVO;
import uk.ac.ox.cs.science2020.zoon.business_manager.value.object.moduleheader.HeaderObject;
import uk.ac.ox.cs.science2020.zoon.shared.value.object.ActionOutcomeVO;
import uk.ac.ox.cs.science2020.zoon.shared.value.object.artifact.workflowcall.WorkflowCallVO;
import uk.ac.ox.cs.science2020.zoon.shared.value.type.ArtifactType;
import uk.ac.ox.cs.science2020.zoon.shared.value.type.DataIdentifier;
import uk.ac.ox.cs.science2020.zoon.shared.value.type.ModuleType;

/**
 * Implementation of artifact service interface.
 *
 * @author 
 */
@Component(BusinessIdentifiers.COMPONENT_ARTIFACT_SERVICE)
public class ArtifactServiceImpl implements ArtifactService {

  @Autowired @Qualifier(BusinessIdentifiers.COMPONENT_ARTIFACT_MANAGER)
  private ArtifactManager artifactManager;

  private static final Log log = LogFactory.getLog(ArtifactServiceImpl.class);

  /* (non-Javadoc)
   * @see uk.ac.ox.cs.science2020.zoon.business_manager.service.ArtifactService#deleteModule(uk.ac.ox.cs.science2020.zoon.business_manager.value.object.artifact.MinimumArtifactDataVO)
   */
  @Override
  public String deleteModule(final MinimumArtifactDataVO minimumArtifactData)
                             throws IllegalArgumentException {
    if (minimumArtifactData == null) {
      throw new IllegalArgumentException("Minimum artifact data required for private module deletion!");
    }

    final ActionOutcomeVO deleteOutcome = artifactManager.deleteModule(minimumArtifactData, null);

    return deleteOutcome.getInformation();
  }

  /* (non-Javadoc)
   * @see uk.ac.ox.cs.science2020.zoon.business_manager.service.ArtifactService#retrievePrivateModules(int, boolean, boolean, boolean)
   */
  @Override
  public Map<String, List<Map<DataIdentifier, HeaderObject>>> retrievePrivateModules(final int userId,
                                                                                     final boolean minimal,
                                                                                     final boolean latest,
                                                                                     final boolean verifiedOnly) {

    log.debug("~retrievePrivateModules() : Invoked with userId '" + userId + "', minimal '" + minimal + "', latest '" + latest + "', verified '" + verifiedOnly + "'.");

    return artifactManager.retrievePrivateModules(userId, minimal, latest, verifiedOnly);
  }

  /* (non-Javadoc)
   * @see uk.ac.ox.cs.science2020.zoon.business_manager.service.ArtifactService#retrievePrivateWorkflowCalls(int, boolean, boolean)
   */
  @Override
  public Set<WorkflowCallVO> retrievePrivateWorkflowCalls(final int userId, final boolean minimal,
                                                          final boolean latest) {
    log.debug("~retrievePrivateWorkflowCalls() : Invoked with userId '" + userId + "', minimal '" + minimal + "', latest '" + latest + "'.");

    return artifactManager.retrievePrivateWorkflowCalls(userId, minimal, latest);
  }

  /* (non-Javadoc)
   * @see uk.ac.ox.cs.science2020.zoon.business_manager.service.ArtifactService#retrieveVerificationResults(int)
   */
  @Override
  public RInvocationResultsVO retrieveVerificationResults(final int verificationIdentifier) {
    log.debug("~retrieveVerificationResults() : Invoked for '" + verificationIdentifier + "'.");

    return artifactManager.retrieveVerificationResults(verificationIdentifier);
  }

  /* (non-Javadoc)
   * @see uk.ac.ox.cs.science2020.zoon.business_manager.service.ArtifactService#saveArtifact(int, java.lang.String, uk.ac.ox.cs.science2020.zoon.shared.value.type.ArtifactType)
   */
  @Override
  public String saveArtifact(final int userId, final String artifactContent,
                             final ArtifactType type) throws IllegalArgumentException {
    log.debug("~saveArtifact() : Invoked.");
    if (StringUtils.isBlank(artifactContent) || type == null) {
      throw new IllegalArgumentException("Null or empty artifact content or type passed as parameter!");
    }

    return artifactManager.saveArtifact(userId, artifactContent, type);
  }

  /* (non-Javadoc)
   * @see uk.ac.ox.cs.science2020.zoon.business_manager.service.ArtifactService#uploadPrivateModuleToZOONStore(uk.ac.ox.cs.science2020.zoon.business_manager.value.object.artifact.MinimumArtifactDataVO, java.lang.String, boolean)
   */
  @Override
  public String uploadPrivateModuleToZOONStore(final MinimumArtifactDataVO minimumArtifactData,
                                               final String moduleType,
                                               final boolean removeAfterUpload)
                                               throws IllegalArgumentException {
    log.debug("~uploadPrivateModuleToZOONStore() : Invoked.");
    if (minimumArtifactData == null) {
      throw new IllegalArgumentException("Invalid attempt to upload a private module without all relevant data!");
    }
    if (StringUtils.isBlank(moduleType)) {
      throw new IllegalArgumentException("Invalid attempt to upload an untyped private module!");
    }

    ModuleType typedModule = null;
    try {
      typedModule = ModuleType.valueOf(moduleType);
    } catch (Exception e) {
      throw new IllegalArgumentException("Specified module type of '" + moduleType + "' was not recognised!");
    }

    final StringBuffer outcome = new StringBuffer();

    final ActionOutcomeVO uploadOutcome = artifactManager.uploadPrivateModuleToZOONStore(minimumArtifactData,
                                                                                         typedModule);

    outcome.append(uploadOutcome.getInformation());
    if (uploadOutcome.isSuccess() && removeAfterUpload) {
      final ActionOutcomeVO removeOutcome = artifactManager.deleteModule(minimumArtifactData,
                                                                         typedModule);
      outcome.append(" (").append(removeOutcome.getInformation()).append(")");
    }

    return outcome.toString();
  }

  /* (non-Javadoc)
   * @see uk.ac.ox.cs.science2020.zoon.business_manager.service.ArtifactService#verifyArtifact(uk.ac.ox.cs.science2020.zoon.business_manager.value.object.artifact.MinimumArtifactDataVO)
   */
  @Override
  public int verifyArtifact(final MinimumArtifactDataVO minimumArtifactData)
                            throws IllegalArgumentException {
    log.debug("~verifyArtifact() : Invoked for '" + minimumArtifactData + "'."); 
    if (minimumArtifactData == null) {
      throw new IllegalArgumentException("Null minimum artifact data object encountered for verification!");
    }

    return artifactManager.verifyArtifact(minimumArtifactData);
  }
}