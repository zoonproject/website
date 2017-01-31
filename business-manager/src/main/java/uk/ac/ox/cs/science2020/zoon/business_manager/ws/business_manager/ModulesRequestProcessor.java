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
package uk.ac.ox.cs.science2020.zoon.business_manager.ws.business_manager;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import uk.ac.ox.cs.science2020.zoon.business_manager.BusinessIdentifiers;
import uk.ac.ox.cs.science2020.zoon.business_manager.service.ArtifactService;
import uk.ac.ox.cs.science2020.zoon.business_manager.service.ConfigurationService;
import uk.ac.ox.cs.science2020.zoon.business_manager.value.object.RInvocationResultsVO;
import uk.ac.ox.cs.science2020.zoon.business_manager.value.object.artifact.MinimumArtifactDataVO;
import uk.ac.ox.cs.science2020.zoon.business_manager.value.object.moduleheader.HeaderObject;
import uk.ac.ox.cs.science2020.zoon.business_manager.value.object.moduleheader.ModuleTypeVO;
import uk.ac.ox.cs.science2020.zoon.business_manager.value.object.moduleheader.MultiValueVO;
import uk.ac.ox.cs.science2020.zoon.business_manager.value.object.moduleheader.ParametersVO;
import uk.ac.ox.cs.science2020.zoon.business_manager.value.object.moduleheader.SingleValueVO;
import uk.ac.ox.cs.science2020.zoon.business_manager.value.object.moduleheader.ParametersVO.ParamInfo;
import uk.ac.ox.cs.science2020.zoon.business_manager.ws.schema.jaxb.DeleteModuleRequest;
import uk.ac.ox.cs.science2020.zoon.business_manager.ws.schema.jaxb.DeleteModuleResponse;
import uk.ac.ox.cs.science2020.zoon.business_manager.ws.schema.jaxb.ModuleAdditionalData;
import uk.ac.ox.cs.science2020.zoon.business_manager.ws.schema.jaxb.ModuleData;
import uk.ac.ox.cs.science2020.zoon.business_manager.ws.schema.jaxb.ModuleTypeData;
import uk.ac.ox.cs.science2020.zoon.business_manager.ws.schema.jaxb.ModuleTypesRequest;
import uk.ac.ox.cs.science2020.zoon.business_manager.ws.schema.jaxb.ModuleTypesResponse;
import uk.ac.ox.cs.science2020.zoon.business_manager.ws.schema.jaxb.PrivateModulesRequest;
import uk.ac.ox.cs.science2020.zoon.business_manager.ws.schema.jaxb.PrivateModulesResponse;
import uk.ac.ox.cs.science2020.zoon.business_manager.ws.schema.jaxb.MultiValue;
import uk.ac.ox.cs.science2020.zoon.business_manager.ws.schema.jaxb.NamedModules;
import uk.ac.ox.cs.science2020.zoon.business_manager.ws.schema.jaxb.ObjectFactory;
import uk.ac.ox.cs.science2020.zoon.business_manager.ws.schema.jaxb.Parameters;
import uk.ac.ox.cs.science2020.zoon.business_manager.ws.schema.jaxb.SaveModuleInZOONStoreRequest;
import uk.ac.ox.cs.science2020.zoon.business_manager.ws.schema.jaxb.SaveModuleInZOONStoreResponse;
import uk.ac.ox.cs.science2020.zoon.business_manager.ws.schema.jaxb.SingleValue;
import uk.ac.ox.cs.science2020.zoon.business_manager.ws.schema.jaxb.VerifyArtifactRequest;
import uk.ac.ox.cs.science2020.zoon.business_manager.ws.schema.jaxb.VerifyArtifactResponse;
import uk.ac.ox.cs.science2020.zoon.business_manager.ws.schema.jaxb.VerifyModuleResultsRequest;
import uk.ac.ox.cs.science2020.zoon.business_manager.ws.schema.jaxb.VerifyModuleResultsResponse;
import uk.ac.ox.cs.science2020.zoon.business_manager.ws.schema.jaxb.VersionedModuleData;
import uk.ac.ox.cs.science2020.zoon.shared.value.type.ArtifactType;
import uk.ac.ox.cs.science2020.zoon.shared.value.type.DataIdentifier;
import uk.ac.ox.cs.science2020.zoon.shared.value.type.ModuleType;

/**
 * 
 *
 * @author geoff
 */
// see spring/ctx/integration/appCtx.int.xml
public class ModulesRequestProcessor {

  @Autowired @Qualifier(BusinessIdentifiers.COMPONENT_ARTIFACT_SERVICE)
  private ArtifactService artifactService;

  @Autowired @Qualifier(BusinessIdentifiers.COMPONENT_CONFIGURATION_SERVICE)
  private ConfigurationService configurationService;

  private static final ObjectFactory objectFactory = new ObjectFactory();

  private static final Log log = LogFactory.getLog(ModulesRequestProcessor.class);

  /**
   * Request to delete a module.
   * 
   * @param request Incoming WS request.
   * @return Outgoing WS response.
   */
  public DeleteModuleResponse deleteModule(final DeleteModuleRequest request) {
    log.debug("~deleteModuleRequest() : Invoked.");

    final String moduleName = request.getModuleName();
    final String moduleVersion = request.getModuleVersion();
    final int userId = request.getUserId();

    final MinimumArtifactDataVO minimumArtifactData = new MinimumArtifactDataVO(userId, moduleName,
                                                                                moduleVersion,
                                                                                ArtifactType.MODULE);
    final String outcome = artifactService.deleteModule(minimumArtifactData);

    final DeleteModuleResponse response = objectFactory.createDeleteModuleResponse();
    response.setOutcomeInformation(outcome);

    return response;
  }

  /**
   * 
   * @param request
   * @return
   */
  public ModuleTypesResponse retrieveModuleTypes(final ModuleTypesRequest request) {
    log.debug("~retrieveModuleTypes() : Invoked.");

    final ModuleTypesResponse response = objectFactory.createModuleTypesResponse();
    final Set<ModuleType> moduleTypes = configurationService.retrieveModuleTypes();

    if (!moduleTypes.isEmpty()) {
      for (final ModuleType moduleType : moduleTypes) {
        final String moduleTypeName = moduleType.name();
        final String moduleTypeDescription = moduleType.getDescription();
        final ModuleTypeData moduleTypeData = objectFactory.createModuleTypeData();
        moduleTypeData.setName(moduleTypeName);
        moduleTypeData.setDescription(moduleTypeDescription);
        response.getModuleTypeData().add(moduleTypeData);
      }
    }

    return response;
  }

  /**
   * 
   * @param modulesRequest
   * @return
   */
  public PrivateModulesResponse retrievePrivateModules(final PrivateModulesRequest modulesRequest) {
    log.debug("~retrievePrivateModules() : Invoked.");

    final BigInteger userId = modulesRequest.getUserId();
    final boolean minimalData = modulesRequest.isMinimal() == null ? false : modulesRequest.isMinimal();

    final Integer useUserId = userId == null ? null : Integer.valueOf(userId.intValue());

    final boolean latest = modulesRequest.isLatestOnly();

    final boolean verifiedOnly = modulesRequest.isVerifiedOnly();

    final PrivateModulesResponse modulesResponse = objectFactory.createPrivateModulesResponse();
    final Map<String, List<Map<DataIdentifier, HeaderObject>>> artifacts = new HashMap<String, List<Map<DataIdentifier, HeaderObject>>>();
    if (useUserId != null) {
      artifacts.putAll(artifactService.retrievePrivateModules(useUserId, minimalData, latest,
                                                              verifiedOnly));
    }

    log.debug("~retrievePrivateModules() : Artifacts '" + artifacts + "'.");

    if (!artifacts.isEmpty()) {
      for (final Map.Entry<String, List<Map<DataIdentifier, HeaderObject>>> artifact : artifacts.entrySet()) {
        final String artifactName = artifact.getKey();
        final List<Map<DataIdentifier, HeaderObject>> allVersionedModuleData = artifact.getValue();

        final NamedModules namedModules = objectFactory.createNamedModules();
        namedModules.setModuleName(artifactName);

        if (!allVersionedModuleData.isEmpty()) {
          for (final Map<DataIdentifier, HeaderObject> versionedArtifact : allVersionedModuleData) {

            final VersionedModuleData versionedModuleData = objectFactory.createVersionedModuleData();
            final List<ModuleData> allModuleData = versionedModuleData.getModuleData();
            final ModuleAdditionalData moduleAdditionalData = objectFactory.createModuleAdditionalData();
            versionedModuleData.setModuleAdditionalData(moduleAdditionalData);

            for (final DataIdentifier dataIdentifier : versionedArtifact.keySet()) {
              final HeaderObject headerObject = versionedArtifact.get(dataIdentifier);

              switch (dataIdentifier) {
                case AUTHOR :
                case DESCRIPTION :
                  final ModuleData moduleDataMV = objectFactory.createModuleData();
                  final MultiValue multiValue = objectFactory.createMultiValue();
                  multiValue.setName(dataIdentifier.name());
                  for (final String comment : ((MultiValueVO) headerObject).getComments()) {
                    multiValue.getValues().add(comment);
                  }
                  moduleDataMV.getMultiValue().add(multiValue);
                  allModuleData.add(moduleDataMV);
                  break;
                case DETAILS :
                case DOCTYPE :
                case TITLE :
                case NAME :
                case REFERENCES :
                  final ModuleData moduleDataSV = objectFactory.createModuleData();
                  final SingleValue singleValueSV = objectFactory.createSingleValue();
                  singleValueSV.setName(dataIdentifier.name());
                  singleValueSV.setValue(((SingleValueVO) headerObject).getComment());
                  moduleDataSV.getSingleValue().add(singleValueSV);
                  allModuleData.add(moduleDataSV);
                  break;
                case PARAM :
                  final ModuleData moduleDataP = objectFactory.createModuleData();
                  final Parameters parameters = objectFactory.createParameters();
                  for (final ParamInfo paramInfo : ((ParametersVO) headerObject).getParamInfo()) {
                    final String paramName = paramInfo.getName();
                    final String paramDescription = paramInfo.getDescription();
                    final SingleValue singleValueP = objectFactory.createSingleValue();
                    singleValueP.setName(paramName);
                    singleValueP.setValue(paramDescription);
                    parameters.getSingleValue().add(singleValueP);
                  }
                  moduleDataP.setParameters(parameters);
                  allModuleData.add(moduleDataP);
                  break;
                case RETURN :
                  final ModuleData moduleDataMV2 = objectFactory.createModuleData();
                  final MultiValue multiValue2 = objectFactory.createMultiValue();
                  multiValue2.setName(dataIdentifier.name());
                  for (final String returnValue : ((MultiValueVO) headerObject).getComments()) {
                    multiValue2.getValues().add(returnValue);
                  }
                  moduleDataMV2.getMultiValue().add(multiValue2);
                  allModuleData.add(moduleDataMV2);
                  break;
                case CONTENT :
                  moduleAdditionalData.setContent(((SingleValueVO) headerObject).getComment());
                  break;
                case LOCATION :
                  moduleAdditionalData.setLocation(((SingleValueVO) headerObject).getComment());
                  break;
                case TYPE :
                  namedModules.setModuleType(((ModuleTypeVO) headerObject).getModuleType().name());
                  break;
                case VERIFIED :
                  moduleAdditionalData.setVerified(Boolean.valueOf(((SingleValueVO) headerObject).getComment()));
                  break;
                case VERSION :
                  versionedModuleData.setVersion(((SingleValueVO) headerObject).getComment());
                  break;
                default :
                  final String errorMessage = "Unrecognised DataIdentifier '" + dataIdentifier + "'.";
                  log.warn("~retrievePrivateModules() : " + errorMessage);
              }
            }

            // TODO : Another version-related todo
            if (!versionedArtifact.containsKey(DataIdentifier.VERSION)) {
              versionedModuleData.setVersion("undefined");
            }

            namedModules.getVersionedModuleData().add(versionedModuleData);
          }
        }

        modulesResponse.getNamedModules().add(namedModules);
      }
    }

    return modulesResponse;
  }

  /**
   * Attempt to save a module in the ZOON store.
   * 
   * @param request Save request.
   * @return Save response.
   */
  public SaveModuleInZOONStoreResponse saveModuleInZOONStore(final SaveModuleInZOONStoreRequest request) {
    log.debug("~saveModuleInZOONStore() : Invoked.");

    final MinimumArtifactDataVO minimumArtifactData = new MinimumArtifactDataVO(request.getUserId(),
                                                                                request.getModuleName(),
                                                                                request.getModuleVersion(),
                                                                                ArtifactType.MODULE);
    final boolean removeAfterUpload = request.isRemoveAfterUpload();

    String outcome = null;
    try {
      outcome = artifactService.uploadPrivateModuleToZOONStore(minimumArtifactData,
                                                               request.getModuleType(),
                                                               removeAfterUpload);
    } catch (Exception e) {
      outcome = "An error occured '" + e.getMessage() + "'";
    }

    final SaveModuleInZOONStoreResponse response = objectFactory.createSaveModuleInZOONStoreResponse();
    response.setProcessResponse(outcome);

    return response;
  }

  /**
   * Verify artifact.
   * 
   * @param request WS request to verify artifact. 
   * @return WS response.
   */
  public VerifyArtifactResponse verifyArtifact(final VerifyArtifactRequest request) {
    log.debug("~verifyArtifact() : Invoked.");

    final MinimumArtifactDataVO minimumArtifactData = new MinimumArtifactDataVO(request.getUserId(),
                                                                                request.getArtifactName(),
                                                                                request.getArtifactVersion(),
                                                                                ArtifactType.valueOf(request.getArtifactType().toString()));
    final int verificationIdentifier = artifactService.verifyArtifact(minimumArtifactData);

    final VerifyArtifactResponse response = objectFactory.createVerifyArtifactResponse();
    response.setVerificationIdentifier(verificationIdentifier);

    return response;
  }

  /**
   * 
   * @param request
   * @return
   */
  public VerifyModuleResultsResponse verifyModuleResults(final VerifyModuleResultsRequest request) {
    log.debug("~verifyModuleResults() : Invoked.");

    final int verificationIdentifier = request.getVerificationIdentifier();

    final RInvocationResultsVO results = artifactService.retrieveVerificationResults(verificationIdentifier);

    final VerifyModuleResultsResponse response = objectFactory.createVerifyModuleResultsResponse();

    if (results != null) {
      response.setVerificationOutcome(results.isJobCompleted());
      response.setVerificationOutput(results.getOutput());
      response.setVerificationResult(results.getResult());
    } else {
      log.warn("~verifyModuleResults() : No verification results for '" + verificationIdentifier + "'.");
    }

    return response;
  }
}