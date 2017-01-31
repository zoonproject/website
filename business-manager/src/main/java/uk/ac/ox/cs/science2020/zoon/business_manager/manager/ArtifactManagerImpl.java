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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import uk.ac.ox.cs.science2020.zoon.business_manager.BusinessIdentifiers;
import uk.ac.ox.cs.science2020.zoon.business_manager.business.ZOONParse;
import uk.ac.ox.cs.science2020.zoon.business_manager.business.ZOONVerify;
import uk.ac.ox.cs.science2020.zoon.business_manager.business.artifact.util.DummyModuleProcessingUtil;
import uk.ac.ox.cs.science2020.zoon.business_manager.business.error.ModuleHeaderException;
import uk.ac.ox.cs.science2020.zoon.business_manager.value.type.ModuleHeader;
import uk.ac.ox.cs.science2020.zoon.business_manager.value.object.RInvocationResultsVO;
import uk.ac.ox.cs.science2020.zoon.business_manager.value.object.artifact.MinimumArtifactDataVO;
import uk.ac.ox.cs.science2020.zoon.business_manager.value.object.moduleheader.HeaderObject;
import uk.ac.ox.cs.science2020.zoon.business_manager.value.object.store.ArtifactStoreVO;
import uk.ac.ox.cs.science2020.zoon.shared.value.object.ActionOutcomeVO;
import uk.ac.ox.cs.science2020.zoon.shared.value.object.artifact.workflowcall.WorkflowCallVO;
import uk.ac.ox.cs.science2020.zoon.shared.value.type.ArtifactType;
import uk.ac.ox.cs.science2020.zoon.shared.value.type.DataIdentifier;
import uk.ac.ox.cs.science2020.zoon.shared.value.type.ModuleType;

/**
 * Artifact management implementation.
 *
 * @author gef
 */
@Component(BusinessIdentifiers.COMPONENT_ARTIFACT_MANAGER)
public class ArtifactManagerImpl implements ArtifactManager {

  private static final String arbitraryFileName = "UploadedFile";

  @Autowired @Qualifier(BusinessIdentifiers.COMPONENT_CONFIGURATION_MANAGER)
  private ConfigurationManager configurationManager;

  @Autowired @Qualifier(BusinessIdentifiers.COMPONENT_ZOON_PARSE)
  private ZOONParse zoonParse;

  @Autowired @Qualifier(BusinessIdentifiers.COMPONENT_ZOON_VERIFY)
  private ZOONVerify zoonVerify;

  private static final Log log = LogFactory.getLog(ArtifactManagerImpl.class);

  /* (non-Javadoc)
   * @see uk.ac.ox.cs.science2020.zoon.business_manager.manager.ArtifactManager#cleanUpParseStatics(int)
   */
  @Override
  public void cleanUpParseStatics(final int parseIdentifier) {
    log.debug("~cleanUpParseStatics() : Invoked for '" + parseIdentifier + "'.");
    zoonParse.cleanUpStatics(parseIdentifier);
  }

  public void cleanUpVerificationStatics(final int verificationIdentifier) {
    log.debug("~cleanUpVerificationStatics() : Invoked for '" + verificationIdentifier + "'.");
    zoonVerify.cleanUpStatics(verificationIdentifier);
  }

  /* (non-Javadoc)
   * @see uk.ac.ox.cs.science2020.zoon.business_manager.manager.ArtifactManager#deleteModule(uk.ac.ox.cs.science2020.zoon.business_manager.value.object.artifact.MinimumArtifactDataVO, uk.ac.ox.cs.science2020.zoon.shared.value.type.ModuleType)
   */
  @Override
  public ActionOutcomeVO deleteModule(final MinimumArtifactDataVO minimumArtifactData,
                                      final ModuleType moduleType) {
    log.debug("~deleteModule() : Invoked.");
    // TODO : Handle module type.

    return configurationManager.retrieveTemporaryStore().deleteArtifact(minimumArtifactData);
  }

  /* (non-Javadoc)
   * @see uk.ac.ox.cs.science2020.zoon.business_manager.manager.ArtifactManager#initiateArtifactParse(uk.ac.ox.cs.science2020.zoon.business_manager.value.type.ArtifactType, java.lang.String, java.lang.String)
   */
  @Override
  public Integer initiateArtifactParse(final ArtifactType artifactType, final String artifactName,
                                       final String artifactContent) {
    log.debug("~initiateArtifactParse() : Invoked.");

    Integer parseIdentifier = null;
    switch (artifactType) {
      case MODULE :
        parseIdentifier = zoonParse.parseModule(artifactName, artifactContent);
        break;
      case WORKFLOW_CALL :
        break;
      default :
        throw new UnsupportedOperationException("Ability to initiate parsing of '" + artifactType + "' artifact types not yet implemented!");
    }
    log.debug("~initiateArtifactParse() : Identified by '" + parseIdentifier + "'.");

    return parseIdentifier;
  }

  /* (non-Javadoc)
   * @see uk.ac.ox.cs.science2020.zoon.business_manager.manager.ArtifactManager#retrieveParseResults(int)
   */
  @Override
  public RInvocationResultsVO retrieveParseResults(final int parseIdentifier) {
    log.debug("~retrieveParseResults() : Invoked for '" + parseIdentifier + "'.");

    final boolean systemProcessEnded = zoonParse.isSystemsProcessFinished(parseIdentifier);

    final List<String> jobVREOutputLines = zoonParse.retrieveJobVREOutputLines(parseIdentifier);
    // final List<String> jobFileOutputLines = zoonParse.retrieveFileOutputLines(parseIdentifier);
    final String jsonOutputLine = zoonParse.retrieveJSONOutputLine(parseIdentifier);

    String vreOutput = null;
    if (jobVREOutputLines != null && !jobVREOutputLines.isEmpty()) {
      vreOutput = StringUtils.join(jobVREOutputLines, "\n");
    }
    /*
    String parseFileOutput = null;
    if (jobFileOutputLines != null && !jobFileOutputLines.isEmpty()) {
      parseFileOutput = StringUtils.join(jobFileOutputLines, "\n");
    }
    */
    String jsonOutput = null;
    if (!StringUtils.isBlank(jsonOutputLine)) {
      jsonOutput = jsonOutputLine.trim();
    }

    final boolean jobCompleted = systemProcessEnded && vreOutput != null && jsonOutput != null;
    log.debug("~retrieveParseResults() : Job completed? '" + jobCompleted + "'.");

    // We're only parsing... not enough to determine if module is verified or otherwise!
    final RInvocationResultsVO parseResults = new RInvocationResultsVO(jobCompleted, vreOutput,
                                                                       null, jsonOutput, false);

    return parseResults;
  }

  /* (non-Javadoc)
   * @see uk.ac.ox.cs.science2020.zoon.business_manager.manager.ArtifactManager#retrievePrivateArtifact(uk.ac.ox.cs.science2020.zoon.business_manager.value.object.artifact.MinimumArtifactDataVO)
   */
  @Override
  public String retrievePrivateArtifact(final MinimumArtifactDataVO minimumArtifactData) {
    log.debug("~retrievePrivateArtifact() : Invoked.");

    return configurationManager.retrieveTemporaryStore().retrieveArtifact(minimumArtifactData);
  }

  /* (non-Javadoc)
   * @see uk.ac.ox.cs.science2020.zoon.business_manager.manager.ArtifactManager#retrievePrivateModules(int, boolean, boolean, boolean)
   */
  @Override
  public Map<String, List<Map<DataIdentifier, HeaderObject>>> retrievePrivateModules(final int userId,
                                                                                     final boolean minimal,
                                                                                     final boolean latest,
                                                                                     final boolean verifiedOnly) {
    log.debug("~retrievePrivateModules() : Invoked.");

    return configurationManager.retrieveTemporaryStore().retrievePrivateModules(userId, minimal, verifiedOnly);
  }

  /* (non-Javadoc)
   * @see uk.ac.ox.cs.science2020.zoon.business_manager.manager.ArtifactManager#retrievePrivateWorkflowCalls(int, boolean, boolean)
   */
  @Override
  public Set<WorkflowCallVO> retrievePrivateWorkflowCalls(final int userId, final boolean minimal,
                                                          final boolean latest) {
    log.debug("~retrievePrivateWorkflowCalls() : Invoked.");

    return configurationManager.retrieveTemporaryStore().retrievePrivateWorkflowCalls(userId, minimal);
  }

  
  /* (non-Javadoc)
   * @see uk.ac.ox.cs.science2020.zoon.business_manager.manager.ArtifactManager#retrieveVerificationResults(int)
   */
  @Override
  public RInvocationResultsVO retrieveVerificationResults(final int verificationIdentifier) {
    log.debug("~retrieveVerificationResults() : Invoked for '" + verificationIdentifier + "'.");

    final boolean finished = zoonVerify.retrieveVerificationState(verificationIdentifier);
    final List<String> jobVREOutputLines = zoonVerify.retrieveJobVREOutputLines(verificationIdentifier);
    final List<String> jobFileOutputLines = zoonVerify.retrieveFileOutputLines(verificationIdentifier);
    final String jsonOutputLine = zoonVerify.retrieveJSONOutputLine(verificationIdentifier);

    String vreOutput = null;
    if (jobVREOutputLines != null && !jobVREOutputLines.isEmpty()) {
      vreOutput = StringUtils.join(jobVREOutputLines, "\n");
    }
    String verificationFileOutput = null;
    boolean verified = false;
    if (jobFileOutputLines != null && !jobFileOutputLines.isEmpty()) {
      verificationFileOutput = StringUtils.join(jobFileOutputLines, "\n");
      final String lastLine = jobFileOutputLines.get(jobFileOutputLines.size() - 1);
      verified = "success".equalsIgnoreCase(lastLine);
    }
    String jsonOutput = null;
    if (!StringUtils.isBlank(jsonOutputLine)) {
      jsonOutput = jsonOutputLine.trim();
    }

    final RInvocationResultsVO verificationResults = new RInvocationResultsVO(finished, vreOutput,
                                                                              verificationFileOutput,
                                                                              jsonOutput, verified);

    if (finished) {
      // TODO : This relies on retrieveVerificationResults being polled until completion. Should
      //        instead be using VerificationProcessMonitor to record when process ends!

      final MinimumArtifactDataVO minimumArtifactData = zoonVerify.retrieveMinimumArtifactData(verificationIdentifier);
      log.debug("~retrieveVerificationResults() : Writing verification outcome of '" + verified + "', for '" + minimumArtifactData.getArtifactName() + "'.");
      configurationManager.retrieveTemporaryStore().writeVerificationOutcome(minimumArtifactData,
                                                                             verified);
      zoonVerify.cleanUpStatics(verificationIdentifier);
    }

    return verificationResults;
  }

  /* (non-Javadoc)
   * @see uk.ac.ox.cs.science2020.zoon.business_manager.manager.ArtifactManager#saveArtifact(int, java.lang.String, uk.ac.ox.cs.science2020.zoon.business_manager.value.type.ArtifactType)
   */
  @Override
  public String saveArtifact(final int userId, final String artifactContent,
                             final ArtifactType type){
    log.debug("~saveArtifact() : Invoked.");

    String outcome = null;
    switch (type) {
      case MODULE :
        outcome = saveModule(userId, artifactContent);
        break;
      case WORKFLOW_CALL :
        outcome = saveWorkflowCall(userId, artifactContent);
        break;
      default :
        throw new UnsupportedOperationException("Unrecognised artifact type of '" + type + "' for saving! Cannot proceed.");
    }

    return outcome;
  }

  private String saveWorkflowCall(final int userId, final String workflowCallContent) {
    log.debug("~saveWorkflowCall() : Invoked.");

    String outcome = null;
    WorkflowCallVO workflowCall = null;
    try {
      workflowCall = WorkflowCallVO.extractWorkflowFromJSON(workflowCallContent, true);
      final ArtifactStoreVO artifactStore = new ArtifactStoreVO(userId, workflowCall.getName(),
                                                                workflowCall.getVersion(),
                                                                ArtifactType.WORKFLOW_CALL,
                                                                workflowCall.getContent(), null);
      outcome = configurationManager.retrieveTemporaryStore().storeTemporaryArtifact(artifactStore);
    } catch (JSONException e) {
      e.printStackTrace();
      outcome = e.getMessage();
      log.warn("~saveWorkflowCall() : JSONException '" + e.getMessage() + "'.");
    }

    return outcome;
  }

  private String saveModule(final int userId, final String moduleContent) {
    log.debug("~saveModule() : Invoked.");

    final Integer parseIdentifier = initiateArtifactParse(ArtifactType.MODULE, arbitraryFileName,
                                                          moduleContent);

    RInvocationResultsVO parseResults = null;
    boolean parsingCompleted = false;
    while (!parsingCompleted) {
      parseResults = retrieveParseResults(parseIdentifier);
      parsingCompleted = parseResults.isJobCompleted();
      log.debug("~saveModule() : Parsing not completed so cannot save module. Waiting 1s.");
      try {
        Thread.sleep(500);
      } catch (InterruptedException e1) {
        e1.printStackTrace();
      }
    }
    cleanUpParseStatics(parseIdentifier);

    final String parsedModuleJSON = parseResults.getJson();

    final Map<ModuleHeader, HeaderObject> headerObjects = new HashMap<ModuleHeader, HeaderObject>();
    try {
      headerObjects.putAll(DummyModuleProcessingUtil.processParsingOutput(parsedModuleJSON));
    } catch (ModuleHeaderException e) {
      final StringBuffer problemBuffer = new StringBuffer();
      if (!e.getProblems().isEmpty()) {
        for (final String problem : e.getProblems()) {
          problemBuffer.append(" ".concat(problem));
        }
      } else {
        final String errorMessage = "Undefined problems reading module header";
        log.error("~saveModule() : ".concat(errorMessage));
        problemBuffer.append(errorMessage);
      }

      return problemBuffer.toString();
    }

    final String moduleName = DummyModuleProcessingUtil.getModuleName(headerObjects);
    final String moduleVersion = DummyModuleProcessingUtil.getModuleVersion(headerObjects);

    ArtifactStoreVO artifactStore = null;
    try {
      artifactStore = new ArtifactStoreVO(userId, moduleName, moduleVersion, ArtifactType.MODULE,
                                          moduleContent, parsedModuleJSON);
    } catch (IllegalArgumentException e) {
      final String infoMessage = "Could not save module due to '" + e.getMessage() + "'.";
      log.info("~saveModule() : ".concat(infoMessage));
      return infoMessage;
    }
    return configurationManager.retrieveTemporaryStore().storeTemporaryArtifact(artifactStore);
  }

  /* (non-Javadoc)
   * @see uk.ac.ox.cs.science2020.zoon.business_manager.manager.ArtifactManager#uploadPrivateModuleToZOONStore(uk.ac.ox.cs.science2020.zoon.business_manager.value.object.artifact.MinimumArtifactDataVO, uk.ac.ox.cs.science2020.zoon.business_manager.value.type.ModuleType)
   */
  @Override
  public ActionOutcomeVO uploadPrivateModuleToZOONStore(final MinimumArtifactDataVO minimumArtifactData,
                                                        final ModuleType moduleType) {
    log.debug("~uploadPrivateModuleToZOONStore() : Invoked.");

    final String artifact = retrievePrivateArtifact(minimumArtifactData);

    ActionOutcomeVO uploadOutcome = null;
    if (artifact == null) {
      uploadOutcome = new ActionOutcomeVO(false,
                                          "Sorry! The artifact was not found so could not upload");
    } else {
      // TODO : Introduce versioning.
      uploadOutcome = configurationManager.retrieveZOONStore().storePermanentArtifact(minimumArtifactData.getArtifactName(),
                                                                                      artifact,
                                                                                      ArtifactType.MODULE);
    }

    return uploadOutcome;
  }

  /* (non-Javadoc)
   * @see uk.ac.ox.cs.science2020.zoon.business_manager.manager.ArtifactManager#verifyArtifact(uk.ac.ox.cs.science2020.zoon.business_manager.value.object.artifact.MinimumArtifactDataVO)
   */
  @Override
  public int verifyArtifact(final MinimumArtifactDataVO minimumArtifactData)
                            throws IllegalArgumentException {
    log.debug("~verifyArtifact() : Invoked.");

    final String content = retrievePrivateArtifact(minimumArtifactData);

    if (content == null) {
      throw new UnsupportedOperationException("Verification failed: Couldn't retrieve the Module!");
    }

    Integer identifier = -99; 
    final ArtifactType artifactType = minimumArtifactData.getArtifactType();
    switch (artifactType) {
      case MODULE :
        identifier = zoonVerify.verifyModule(minimumArtifactData, content);
        break;
      case WORKFLOW_CALL :
        throw new UnsupportedOperationException("Workflow Call verification not yet implemented!");
        //break;
      default :
        throw new UnsupportedOperationException("Unrecogised module type '" + artifactType + "' - cannot verify!");
    }

    return identifier;
  }
}