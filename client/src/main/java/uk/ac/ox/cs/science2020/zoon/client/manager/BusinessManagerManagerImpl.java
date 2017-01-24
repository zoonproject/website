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

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import uk.ac.ox.cs.science2020.zoon.client.ClientIdentifiers;
import uk.ac.ox.cs.science2020.zoon.client.exception.BusinessManagerWSInvocationException;
import uk.ac.ox.cs.science2020.zoon.client.exception.NoConnectionException;
import uk.ac.ox.cs.science2020.zoon.client.value.object.ModulePublishVO;
import uk.ac.ox.cs.science2020.zoon.client.value.object.VerificationResultsVO;
import uk.ac.ox.cs.science2020.zoon.client.ws.business_manager.proxy.BusinessServicesProxy;
import uk.ac.ox.cs.science2020.zoon.shared.value.object.artifact.module.ModuleVO;
import uk.ac.ox.cs.science2020.zoon.shared.value.object.artifact.workflowcall.WorkflowCallVO;
import uk.ac.ox.cs.science2020.zoon.shared.value.type.ArtifactType;

/**
 * Business Manager service implementation.
 * 
 * @author geoff
 */
@Component(ClientIdentifiers.COMPONENT_BUSINESS_MANAGER_MANAGER)
public class BusinessManagerManagerImpl implements BusinessManagerManager {

  @Autowired @Qualifier(ClientIdentifiers.COMPONENT_BUSINESS_SERVICES_PROXY) 
  private BusinessServicesProxy businessServicesProxy;

  private static final Log log = LogFactory.getLog(BusinessManagerManagerImpl.class);

  
  /* (non-Javadoc)
   * @see uk.ac.ox.cs.science2020.zoon.client.manager.BusinessManagerManager#deleteModule(java.lang.String, java.lang.String, java.lang.String)
   */
  @Override
  public String deleteModule(final String userName, final String moduleName,
                             final String moduleVersion) throws BusinessManagerWSInvocationException,
                                                                NoConnectionException {
    log.debug("~deleteModule() : Invoked.");

    return businessServicesProxy.deleteModule(userName, moduleName, moduleVersion);
  }

  /* (non-Javadoc)
   * @see uk.ac.ox.cs.science2020.zoon.client.service.BusinessManagerManager#retrieveModuleTypes()
   */
  @Override
  public Map<String, String> retrieveModuleTypes() throws BusinessManagerWSInvocationException,
                                                          NoConnectionException {
    log.debug("~retrieveModuleTypes() : Invoked.");

    return businessServicesProxy.retrieveModuleTypes();
  }

  /* (non-Javadoc)
   * @see uk.ac.ox.cs.science2020.zoon.client.manager.BusinessManagerManager#retrievePrivateModules(java.lang.String, boolean)
   */
  @Override
  public Map<String, List<ModuleVO>> retrievePrivateModules(final String userName,
                                                            final boolean verifiedOnly)
                                                            throws BusinessManagerWSInvocationException,
                                                                   NoConnectionException,
                                                                   NullPointerException {
    log.debug("~retrievePrivateModules() : Invoked for user name '" + userName + "'.");

    return businessServicesProxy.retrievePrivateModules(userName, verifiedOnly);
  }

  /* (non-Javadoc)
   * @see uk.ac.ox.cs.science2020.zoon.client.service.BusinessManagerManager#retrievePrivateWorkflowCalls(java.lang.String)
   */
  @Override
  public Map<String, List<WorkflowCallVO>> retrievePrivateWorkflowCalls(final String userName)
                                                                        throws BusinessManagerWSInvocationException,
                                                                               NoConnectionException,
                                                                               NullPointerException {
    log.debug("~retrievePrivateWorkflowCalls() : Invoked for userName '" + userName + "'.");
    if (StringUtils.isBlank(userName)) {
      throw new NullPointerException("User name must be provided to retrieve private workflow calls!");
    }

    return businessServicesProxy.retrievePrivateWorkflowCalls(userName);
  }

  /* (non-Javadoc)
   * @see uk.ac.ox.cs.science2020.zoon.client.service.BusinessManagerManager#retrieveVerificationOutput(int)
   */
  @Override
  public VerificationResultsVO retrieveVerificationOutput(final int verificationIdentifier)
                                                          throws BusinessManagerWSInvocationException,
                                                                 NoConnectionException {
    log.debug("~retrieveVerificationOutput() : Invoked.");

    return businessServicesProxy.retrieveVerificationOutput(verificationIdentifier);
  }

  /* (non-Javadoc)
   * @see uk.ac.ox.cs.science2020.zoon.client.manager.BusinessManagerManager#saveArtifact(java.lang.String, java.lang.String, uk.ac.ox.cs.science2020.zoon.shared.value.type.ArtifactType)
   */
  @Override
  public String saveArtifact(final String userName, final String artifactContent,
                             final ArtifactType artifactType)
                             throws BusinessManagerWSInvocationException, NoConnectionException {
    log.debug("~saveArtifact() : Invoked.");

    return businessServicesProxy.saveArtifact(userName, artifactContent, artifactType);
  }

  /* (non-Javadoc)
   * @see uk.ac.ox.cs.science2020.zoon.client.manager.BusinessManagerManager#saveModuleInZOONStore(java.lang.String, uk.ac.ox.cs.science2020.zoon.client.value.object.ModulePublishVO)
   */
  @Override
  public String saveModuleInZOONStore(final String userName, final ModulePublishVO modulePublish)
                                      throws BusinessManagerWSInvocationException,
                                             NoConnectionException {
    log.debug("~saveModuleInZOONStore() : Invoked.");

    return businessServicesProxy.saveModuleInZOONStore(userName, modulePublish);
  }

  /* (non-Javadoc)
   * @see uk.ac.ox.cs.science2020.zoon.client.service.BusinessManagerManager#verifyModule(java.lang.String, java.lang.String, java.lang.String)
   */
  @Override
  public int verifyModule(final String userName, final String moduleName, final String moduleVersion)
                          throws BusinessManagerWSInvocationException, NoConnectionException {
    log.debug("~verifyModule() : Invoked.");

    final int identifier = businessServicesProxy.verifyModule(userName, moduleName, moduleVersion);

    log.debug("~verifyModule() : Verification identifier '" + identifier + "'.");

    return identifier;
  }
}