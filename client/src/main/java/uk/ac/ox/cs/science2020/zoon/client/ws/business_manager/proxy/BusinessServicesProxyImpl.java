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

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.ws.client.WebServiceIOException;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;
import org.springframework.ws.client.support.interceptor.ClientInterceptor;
import org.springframework.ws.soap.client.SoapFaultClientException;

import uk.ac.ox.cs.science2020.zoon.business_manager.ws._1.ArtifactStoreRequest;
import uk.ac.ox.cs.science2020.zoon.business_manager.ws._1.ArtifactStoreResponse;
import uk.ac.ox.cs.science2020.zoon.business_manager.ws._1.DeleteModuleRequest;
import uk.ac.ox.cs.science2020.zoon.business_manager.ws._1.DeleteModuleResponse;
import uk.ac.ox.cs.science2020.zoon.business_manager.ws._1.ModuleAdditionalData;
import uk.ac.ox.cs.science2020.zoon.business_manager.ws._1.ModuleData;
import uk.ac.ox.cs.science2020.zoon.business_manager.ws._1.ModuleTypeData;
import uk.ac.ox.cs.science2020.zoon.business_manager.ws._1.ModuleTypesRequest;
import uk.ac.ox.cs.science2020.zoon.business_manager.ws._1.ModuleTypesResponse;
import uk.ac.ox.cs.science2020.zoon.business_manager.ws._1.MultiValue;
import uk.ac.ox.cs.science2020.zoon.business_manager.ws._1.NamedModules;
import uk.ac.ox.cs.science2020.zoon.business_manager.ws._1.NamedWorkflowCalls;
import uk.ac.ox.cs.science2020.zoon.business_manager.ws._1.ObjectFactory;
import uk.ac.ox.cs.science2020.zoon.business_manager.ws._1.Parameters;
import uk.ac.ox.cs.science2020.zoon.business_manager.ws._1.PrivateModulesRequest;
import uk.ac.ox.cs.science2020.zoon.business_manager.ws._1.PrivateModulesResponse;
import uk.ac.ox.cs.science2020.zoon.business_manager.ws._1.PrivateWorkflowCallsRequest;
import uk.ac.ox.cs.science2020.zoon.business_manager.ws._1.PrivateWorkflowCallsResponse;
import uk.ac.ox.cs.science2020.zoon.business_manager.ws._1.SaveModuleInZOONStoreRequest;
import uk.ac.ox.cs.science2020.zoon.business_manager.ws._1.SaveModuleInZOONStoreResponse;
import uk.ac.ox.cs.science2020.zoon.business_manager.ws._1.SingleValue;
import uk.ac.ox.cs.science2020.zoon.business_manager.ws._1.VerifyArtifactRequest;
import uk.ac.ox.cs.science2020.zoon.business_manager.ws._1.VerifyArtifactResponse;
import uk.ac.ox.cs.science2020.zoon.business_manager.ws._1.VerifyModuleResultsRequest;
import uk.ac.ox.cs.science2020.zoon.business_manager.ws._1.VerifyModuleResultsResponse;
import uk.ac.ox.cs.science2020.zoon.business_manager.ws._1.VersionedModuleData;
import uk.ac.ox.cs.science2020.zoon.business_manager.ws._1.VersionedWorkflowCallData;
import uk.ac.ox.cs.science2020.zoon.client.ClientIdentifiers;
import uk.ac.ox.cs.science2020.zoon.client.exception.BusinessManagerWSInvocationException;
import uk.ac.ox.cs.science2020.zoon.client.exception.NoConnectionException;
import uk.ac.ox.cs.science2020.zoon.client.value.object.ModulePublishVO;
import uk.ac.ox.cs.science2020.zoon.client.value.object.VerificationResultsVO;
import uk.ac.ox.cs.science2020.zoon.shared.business.artifact.util.ModuleProcessingUtil;
import uk.ac.ox.cs.science2020.zoon.shared.value.object.artifact.AuthorVO;
import uk.ac.ox.cs.science2020.zoon.shared.value.object.artifact.module.ModuleVO;
import uk.ac.ox.cs.science2020.zoon.shared.value.object.artifact.workflowcall.WorkflowCallVO;
import uk.ac.ox.cs.science2020.zoon.shared.value.type.ArtifactType;
import uk.ac.ox.cs.science2020.zoon.shared.value.type.DataIdentifier;
import uk.ac.ox.cs.science2020.zoon.shared.value.type.ModuleType;

/**
 * JAXB marshalling gateway to the business manager web service.
 * 
 * @author geoff
 */
//defined in appCtx.ws.xml
public class BusinessServicesProxyImpl extends WebServiceGatewaySupport
                                       implements BusinessServicesProxy {

  // appCtx.ws.security-outgoing.xml
  @Autowired(required=false)
  @Qualifier(ClientIdentifiers.COMPONENT_BUS_MANAGER_SERVICES_INTERCEPTOR)
  private ClientInterceptor wsBusManagerServicesInterceptor;

  private static final ObjectFactory objectFactory = new ObjectFactory();

  private static final Log log = LogFactory.getLog(BusinessServicesProxyImpl.class);

  @PostConstruct
  private void postConstruct() {
    final ClientInterceptor[] wsClientInterceptors = { wsBusManagerServicesInterceptor };

    this.setInterceptors(wsClientInterceptors);
    for (int idx = 0; idx < wsClientInterceptors.length; idx++) {
      log.info("~postConstruct() : Interceptor '" + wsClientInterceptors[idx] + "' assigned for outbound.");
    }
  }

  /* (non-Javadoc)
   * @see uk.ac.ox.cs.science2020.zoon.client.ws.business_manager.proxy.BusinessServicesProxy#deleteModule(java.lang.String, java.lang.String, java.lang.String)
   */
  @Override
  public String deleteModule(final String userName, final String moduleName,
                             final String moduleVersion) throws BusinessManagerWSInvocationException,
                                                                NoConnectionException {
    log.debug("~deleteModule() : Invoked.");

    final DeleteModuleRequest request = objectFactory.createDeleteModuleRequest();
    request.setModuleName(moduleName);
    request.setModuleVersion(moduleVersion);
    request.setUserId(generateUserId(userName));

    DeleteModuleResponse response = null;
    String errorMessage = null;
    try {
      response = (DeleteModuleResponse) getWebServiceTemplate().marshalSendAndReceive(request);
    } catch (SoapFaultClientException e) {
      errorMessage = "SOAP Exception '" + e.getMessage() + "'.";
    } catch (WebServiceIOException e) {
      errorMessage = "WS IO Exception '" + e.getMessage() + "'.";
    } catch (Exception e) {
      errorMessage = "Exception '" + e.getMessage() + "'.";
    }
    if (errorMessage != null) {
      log.error("~deleteModule() : ".concat(errorMessage));
      throw new BusinessManagerWSInvocationException(errorMessage);
    }

    String outcome = null;
    if (response != null) {
      outcome = response.getOutcomeInformation();
    } else {
      outcome = "Communication failure '" + errorMessage +"'.";
    }

    return outcome;
  }

  private int generateUserId(final String userName) {
    if (userName == null) {
      throw new IllegalArgumentException("Invalid request to generate a user id from a null user name!");
    }
    return userName.hashCode();
  }

  /* (non-Javadoc)
   * @see uk.ac.ox.cs.science2020.zoon.client.ws.business_manager.proxy.BusinessServicesProxy#retrieveModule(java.lang.String, java.lang.String, java.lang.String)
   */
  @Override
  public String retrieveModule(final String userName, final String moduleName,
                               final String moduleVersion)
                               throws BusinessManagerWSInvocationException,
                                      NoConnectionException {
    log.debug("~retrieveModule() : Invoked.");
    throw new UnsupportedOperationException("unwritten section");
  }

  /* (non-Javadoc)
   * @see uk.ac.ox.cs.science2020.zoon.client.ws.business_manager/proxy/BusinessServicesProxy#retrieveModuleTypes()
   */
  @Override
  public Map<String, String> retrieveModuleTypes() throws BusinessManagerWSInvocationException,
                                                          NoConnectionException {
    log.debug("~retrieveModuleTypes() : Invoked.");

    final ModuleTypesRequest request = objectFactory.createModuleTypesRequest();

    ModuleTypesResponse response = null;
    try {
      response = (ModuleTypesResponse) getWebServiceTemplate().marshalSendAndReceive(request);
    } catch (SoapFaultClientException e) {
      log.error("~retrieveModuleTypes() : SOAP Exception '" + e.getMessage() + "'.");
      throw new BusinessManagerWSInvocationException(e.getMessage());
    } catch (WebServiceIOException e) {
      log.error("~retrieveModuleTypes() : WS IO Exception '" + e.getMessage() + "'.");
      throw new BusinessManagerWSInvocationException(e.getMessage());
    } catch (Exception e) {
      log.error("~retrieveModuleTypes() : Exception '" + e.getClass() + "' - '" + e.getMessage() + "'.");
      throw new BusinessManagerWSInvocationException(e.getMessage());
    }

    final Map<String, String> moduleTypes = new HashMap<String, String>();
    if (response != null) {
      for (final ModuleTypeData moduleTypeData : response.getModuleTypeData()) {
        moduleTypes.put(moduleTypeData.getName(), moduleTypeData.getDescription());
      }
    }

    log.debug("~retrieveModuleTypes() : Module types '" + moduleTypes + "'.");

    return moduleTypes;
  }

  /* (non-Javadoc)
   * @see uk.ac.ox.cs.science2020.zoon.client.ws.business_manager.proxy.BusinessServicesProxy#retrievePrivateModules(java.lang.String, boolean)
   */
  @Override
  public Map<String, List<ModuleVO>> retrievePrivateModules(final String userName,
                                                            final boolean verifiedOnly)
                                                            throws BusinessManagerWSInvocationException,
                                                                   NoConnectionException {
    log.debug("~retrievePrivateModules() : Invoked.");

    final PrivateModulesRequest request = objectFactory.createPrivateModulesRequest();

    request.setUserId(userName == null ? null : BigInteger.valueOf(generateUserId(userName)));
    request.setLatestOnly(Boolean.TRUE);
    request.setVerifiedOnly(verifiedOnly);

    PrivateModulesResponse response = null;
    try {
      // Handled in business-manager ws ModulesRequestProcessor.java
      response = (PrivateModulesResponse) getWebServiceTemplate().marshalSendAndReceive(request);
    } catch (SoapFaultClientException e) {
      log.error("~retrievePrivateModules() : SOAP Exception '" + e.getMessage() + "'.");
      throw new BusinessManagerWSInvocationException(e.getMessage());
    } catch (WebServiceIOException e) {
      log.error("~retrievePrivateModules() : WS IO Exception '" + e.getMessage() + "'.");
      throw new BusinessManagerWSInvocationException(e.getMessage());
    } catch (Exception e) {
      log.error("~retrievePrivateModules() : Exception '" + e.getClass() + "' - '" + e.getMessage() + "'.");
      throw new BusinessManagerWSInvocationException(e.getMessage());
    }

    final Map<String, List<ModuleVO>> privateModules = new HashMap<String, List<ModuleVO>>();

    if (response != null) {
      for (final NamedModules namedModule : response.getNamedModules()) {
        // For each module by name
        final String moduleName = namedModule.getModuleName();
        final ModuleType moduleType = ModuleType.valueOf(namedModule.getModuleType());

        final List<ModuleVO> newModuleVersions = new ArrayList<ModuleVO>();

        for (final VersionedModuleData versionedModuleData : namedModule.getVersionedModuleData()) {
          // For each version of that module
          final Map<String, String> paramMap = new HashMap<String, String>();
          final List<String> returnValues = new ArrayList<String>();
          String source = null;
          String submitted = null;
          final List<AuthorVO> authors = new ArrayList<AuthorVO>();
          final List<String> descriptions = new ArrayList<String>();
          String references = null;

          for (final ModuleData moduleData : versionedModuleData.getModuleData()) {
            // Process single-valued module header data.
            for (final SingleValue singleValue : moduleData.getSingleValue()) {
              final String dataIdentifier = singleValue.getName();
              final String value = singleValue.getValue();
              switch (DataIdentifier.valueOf(dataIdentifier)) {
                case REFERENCES :
                  references = value;
                  break;
                default :
                  log.info("~retrievePrivateModules() : Not yet interested in single-valued data identifier '" + dataIdentifier + "'.");
                  break;
              }
            }

            // Process multi-values module header data.
            for (final MultiValue multiValue : moduleData.getMultiValue()) {
              final String dataIdentifier = multiValue.getName();
              final List<String> values = multiValue.getValues();
              switch (DataIdentifier.valueOf(dataIdentifier)) {
                case RETURN :
                  returnValues.addAll(values);
                  break;
                case AUTHOR :
                  for (final String author : values) {
                    final String[] components = ModuleProcessingUtil.extractAuthorComponents(author); 
                    authors.add(new AuthorVO(components[0], components[1]));
                  }
                  break;
                case DESCRIPTION :
                  descriptions.addAll(values);
                  break;
                default :
                  log.info("~retrievePrivateModules() : Not yet interested in multi-valued data identifier '" + dataIdentifier + "'.");
                  break;
              }
            }

            // Finally parameter data.
            final Parameters parameters = moduleData.getParameters();
            if (parameters != null) {
              for (final SingleValue singleValue : parameters.getSingleValue()) {
                final String name = singleValue.getName();
                final String description = singleValue.getValue();
                paramMap.put(name, description);
                log.debug("~retrievePrivateModules() : Param map '" + paramMap + "'.");
              }
            }
          }

          String changeLog = "";
          String content = "";
          String location = "";
          Boolean verified = false;
          final ModuleAdditionalData moduleAdditionalData = versionedModuleData.getModuleAdditionalData();
          if (moduleAdditionalData != null) {
            changeLog = moduleAdditionalData.getChangeLog();
            content = moduleAdditionalData.getContent();
            location = moduleAdditionalData.getLocation();
            verified = moduleAdditionalData.isVerified();
          }

          final ModuleVO newModule = new ModuleVO(moduleName, versionedModuleData.getVersion(),
                                                  moduleType, paramMap, location, returnValues,
                                                  source, submitted, authors, descriptions,
                                                  references, content, true, verified, false);

          newModuleVersions.add(newModule);
        }
        privateModules.put(moduleName, newModuleVersions);
      }
    }

    return privateModules;
  }

  /* (non-Javadoc)
   * @see uk.ac.ox.cs.science2020.zoon.client.ws.business_manager.proxy.BusinessServicesProxy#retrievePrivateWorkflowCalls(java.lang.String)
   */
  @Override
  public Map<String, List<WorkflowCallVO>> retrievePrivateWorkflowCalls(final String userName)
                                                                        throws BusinessManagerWSInvocationException,
                                                                               NoConnectionException {
    log.debug("~retrievePrivateWorkflowCalls() : Invoked.");

    final PrivateWorkflowCallsRequest request = objectFactory.createPrivateWorkflowCallsRequest();

    request.setUserId(userName == null ? null : BigInteger.valueOf(generateUserId(userName)));
    request.setLatestOnly(Boolean.TRUE);

    PrivateWorkflowCallsResponse response = null;
    try {
      response = (PrivateWorkflowCallsResponse) getWebServiceTemplate().marshalSendAndReceive(request);
    } catch (SoapFaultClientException e) {
      log.error("~retrievePrivateWorkflowCalls() : SOAP Exception '" + e.getMessage() + "'.");
      throw new BusinessManagerWSInvocationException(e.getMessage());
    } catch (WebServiceIOException e) {
      log.error("~retrievePrivateWorkflowCalls() : WS IO Exception '" + e.getMessage() + "'.");
      throw new BusinessManagerWSInvocationException(e.getMessage());
    } catch (Exception e) {
      log.error("~retrievePrivateWorkflowCalls() : Exception '" + e.getClass() + "' - '" + e.getMessage() + "'.");
      throw new BusinessManagerWSInvocationException(e.getMessage());
    }

    final Map<String, List<WorkflowCallVO>> privateWorkflowCalls = new HashMap<String, List<WorkflowCallVO>>();
    if (response != null) {
      for (final NamedWorkflowCalls namedWorkflowCalls : response.getNamedWorkflowCalls()) {
        final String workflowCallName = namedWorkflowCalls.getWorkflowCallName();
        final List<WorkflowCallVO> newWorkflowCallVersions = new ArrayList<WorkflowCallVO>();

        for (final VersionedWorkflowCallData versionedWorkflowCallData : namedWorkflowCalls.getVersionedWorkflowCallData()) {
          /* Private workflow calls have no author / submitted data -- that's only in public 
             workflow metadata. */
          newWorkflowCallVersions.add(new WorkflowCallVO(workflowCallName,
                                                         versionedWorkflowCallData.getVersion(),
                                                         versionedWorkflowCallData.getText()));
        }

        privateWorkflowCalls.put(workflowCallName, newWorkflowCallVersions);
      }
    }

    return privateWorkflowCalls;
  }

  /* (non-Javadoc)
   * @see uk.ac.ox.cs.science2020.zoon.client.ws.business_manager.proxy.BusinessServicesProxy#retrieveVerificationOutput(int)
   */
  @Override
  public VerificationResultsVO retrieveVerificationOutput(final int verificationIdentifier)
                                                          throws BusinessManagerWSInvocationException,
                                                                 NoConnectionException {
    log.debug("~retrieveVerificationOutput() : Invoked.");

    final VerifyModuleResultsRequest request = objectFactory.createVerifyModuleResultsRequest();
    request.setVerificationIdentifier(verificationIdentifier);

    VerifyModuleResultsResponse response;
    try {
      response = (VerifyModuleResultsResponse) getWebServiceTemplate().marshalSendAndReceive(request);
    } catch (SoapFaultClientException e) {
      log.error("~retrieveVerificationOutput() : SOAP Exception '" + e.getMessage() + "'.");
      throw new BusinessManagerWSInvocationException(e.getMessage());
    } catch (WebServiceIOException e) {
      // Business Manager web service offline? 404 Not found?
      log.error("~retrieveVerificationOutput() : WS IO Exception '" + e.getMessage() + "'.");
      throw new BusinessManagerWSInvocationException(e.getMessage());
    } catch (Exception e) {
      log.error("~retrieveVerificationOutput() : Exception '" + e.getClass() + "' - '" + e.getMessage() + "'.");
      throw new BusinessManagerWSInvocationException(e.getMessage());
    }

    VerificationResultsVO verificationResultsVO = null;

    if (response != null) {
      verificationResultsVO = new VerificationResultsVO(response.isVerificationOutcome(),
                                                        response.getVerificationOutput(),
                                                        response.getVerificationResult());
      log.debug("~retrieveVerificationOutput() : Response is '" + verificationResultsVO.toString() + "'.");
    }

    return verificationResultsVO;
  }

  /* (non-Javadoc)
   * @see uk.ac.ox.cs.science2020.zoon.client.ws.business_manager.proxy.BusinessServicesProxy#saveArtifact(java.lang.String, java.lang.String, uk.ac.ox.cs.science2020.zoon.shared.value.type.ArtifactType)
   */
  @Override
  public String saveArtifact(final String userName, final String artifactContent,
                             final ArtifactType artifactType)
                             throws BusinessManagerWSInvocationException, NoConnectionException {
    log.debug("~saveArtifact() : Invoked.");

    final ArtifactStoreRequest request = objectFactory.createArtifactStoreRequest();
    request.setUserId(BigInteger.valueOf(Long.valueOf(generateUserId(userName))));
    request.setArtifact(artifactContent);
    request.setArtifactType(uk.ac.ox.cs.science2020.zoon.business_manager.ws._1.ArtifactType.valueOf(artifactType.toString()));

    ArtifactStoreResponse response = null;

    try {
      response = (ArtifactStoreResponse) getWebServiceTemplate().marshalSendAndReceive(request);
    } catch (SoapFaultClientException e) {
      log.error("~saveArtifact() : SOAP Exception '" + e.getMessage() + "'.");
      throw new BusinessManagerWSInvocationException(e.getMessage());
    } catch (WebServiceIOException e) {
      log.error("~saveArtifact() : WS IO Exception '" + e.getMessage() + "'.");
      throw new BusinessManagerWSInvocationException(e.getMessage());
    } catch (Exception e) {
      log.error("~saveArtifact() : Exception '" + e.getClass() + "' - '" + e.getMessage() + "'.");
      throw new BusinessManagerWSInvocationException(e.getMessage());
    }

    String saveOutcome = "";

    if (response != null) {
      saveOutcome = response.getOutcome();
    }

    return saveOutcome;
  }

  /* (non-Javadoc)
   * @see uk.ac.ox.cs.science2020.zoon.client.ws.business_manager.proxy.BusinessServicesProxy#saveModuleInZOONStore(java.lang.String, uk.ac.ox.cs.science2020.zoon.client.value.object.ModulePublishVO)
   */
  @Override
  public String saveModuleInZOONStore(final String userName, final ModulePublishVO modulePublish)
                                      throws BusinessManagerWSInvocationException,
                                             NoConnectionException {
    log.debug("~saveModuleInZOONStore() : Invoked.");

    final SaveModuleInZOONStoreRequest request = objectFactory.createSaveModuleInZOONStoreRequest();
    request.setModuleName(modulePublish.getName());
    request.setModuleType(modulePublish.getType());
    request.setModuleVersion(modulePublish.getVersion());
    request.setRemoveAfterUpload(modulePublish.isRemoveAfterUpload());
    request.setUserId(generateUserId(userName));

    SaveModuleInZOONStoreResponse response;
    try {
      response = (SaveModuleInZOONStoreResponse) getWebServiceTemplate().marshalSendAndReceive(request);
    } catch (SoapFaultClientException e) {
      log.error("~saveModuleInZOONStore() : SOAP Exception '" + e.getMessage() + "'.");
      throw new BusinessManagerWSInvocationException(e.getMessage());
    } catch (WebServiceIOException e) {
      log.error("~saveModuleInZOONStore() : WS IO Exception '" + e.getMessage() + "'.");
      throw new BusinessManagerWSInvocationException(e.getMessage());
    } catch (Exception e) {
      log.error("~saveModuleInZOONStore() : Exception '" + e.getClass() + "' - '" + e.getMessage() + "'.");
      throw new BusinessManagerWSInvocationException(e.getMessage());
    }

    return response.getProcessResponse();
  }

  /* (non-Javadoc)
   * @see uk.ac.ox.cs.science2020.zoon.client.ws.business_manager.proxy.BusinessServicesProxy#verifyModule(java.lang.String, java.lang.String, java.lang.String)
   */
  @Override
  public int verifyModule(final String userName, final String moduleName, 
                          final String moduleVersion) throws BusinessManagerWSInvocationException,
                                                             NoConnectionException {
    log.debug("~verifyModule() : Invoked.");

    final VerifyArtifactRequest request = objectFactory.createVerifyArtifactRequest();
    request.setUserId(generateUserId(userName));
    request.setArtifactName(moduleName);
    request.setArtifactVersion(moduleVersion);
    request.setArtifactType(uk.ac.ox.cs.science2020.zoon.business_manager.ws._1.ArtifactType.valueOf(ArtifactType.MODULE.toString()));

    VerifyArtifactResponse response;
    try {
      response = (VerifyArtifactResponse) getWebServiceTemplate().marshalSendAndReceive(request);
    } catch (SoapFaultClientException e) {
      log.error("~verifyModule() : SOAP Exception '" + e.getMessage() + "'.");
      throw new BusinessManagerWSInvocationException(e.getMessage());
    } catch (WebServiceIOException e) {
      log.error("~verifyModule() : WS IO Exception '" + e.getMessage() + "'.");
      throw new BusinessManagerWSInvocationException(e.getMessage());
    } catch (Exception e) {
      log.error("~verifyModule() : Exception '" + e.getClass() + "' - '" + e.getMessage() + "'.");
      throw new BusinessManagerWSInvocationException(e.getMessage());
    }

    int identifier = -1;

    if (response != null) {
      identifier = response.getVerificationIdentifier();
      log.debug("~verifyModule() : Verification identifier '" + identifier + "'.");
    }

    return identifier;
  }
}