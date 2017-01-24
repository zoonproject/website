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
package uk.ac.ox.cs.science2020.zoon.business_manager.ws.business_manager;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import uk.ac.ox.cs.science2020.zoon.business_manager.BusinessIdentifiers;
import uk.ac.ox.cs.science2020.zoon.business_manager.service.ArtifactService;
import uk.ac.ox.cs.science2020.zoon.business_manager.service.ConfigurationService;
import uk.ac.ox.cs.science2020.zoon.business_manager.ws.schema.jaxb.NamedWorkflowCalls;
import uk.ac.ox.cs.science2020.zoon.business_manager.ws.schema.jaxb.ObjectFactory;
import uk.ac.ox.cs.science2020.zoon.business_manager.ws.schema.jaxb.PrivateWorkflowCallsRequest;
import uk.ac.ox.cs.science2020.zoon.business_manager.ws.schema.jaxb.PrivateWorkflowCallsResponse;
import uk.ac.ox.cs.science2020.zoon.business_manager.ws.schema.jaxb.VersionedWorkflowCallData;
import uk.ac.ox.cs.science2020.zoon.shared.value.object.artifact.workflowcall.WorkflowCallVO;

/**
 * 
 *
 * @author 
 */
//see spring/ctx/integration/appCtx.int.xml
public class WorkflowCallsRequestProcessor {

  @Autowired @Qualifier(BusinessIdentifiers.COMPONENT_ARTIFACT_SERVICE)
  private ArtifactService artifactService;

  @Autowired @Qualifier(BusinessIdentifiers.COMPONENT_CONFIGURATION_SERVICE)
  private ConfigurationService configurationService;

  private static final ObjectFactory objectFactory = new ObjectFactory();

  private static final Log log = LogFactory.getLog(WorkflowCallsRequestProcessor.class);

  /**
   * Retrieve private workflow calls.
   * 
   * @param workflowCallsRequest Request to view private workflow calls.
   * @return Private workflow calls response.
   */
  public PrivateWorkflowCallsResponse retrievePrivateWorkflowCalls(final PrivateWorkflowCallsRequest workflowCallsRequest) {
    log.debug("~retrievePrivateWorkflowCalls() : Invoked.");

    final BigInteger userId = workflowCallsRequest.getUserId();
    final boolean minimalData = workflowCallsRequest.isMinimal() == null ? false : workflowCallsRequest.isMinimal();

    final Integer useUserId = userId == null ? null : Integer.valueOf(userId.intValue());

    final boolean latest = workflowCallsRequest.isLatestOnly();

    final PrivateWorkflowCallsResponse workflowCallsResponse = objectFactory.createPrivateWorkflowCallsResponse();

    if (useUserId != null) {
      final Set<WorkflowCallVO> privateWorkflowCalls = artifactService.retrievePrivateWorkflowCalls(useUserId,
                                                                                                    minimalData,
                                                                                                    latest);
      if (!privateWorkflowCalls.isEmpty()) {

        final Map<String, Set<WorkflowCallVO>> tmpVersionedWorkflowCallData = new HashMap<String, Set<WorkflowCallVO>>();

        for (final WorkflowCallVO workflowCall : privateWorkflowCalls) {
          final String name = workflowCall.getName();
          if (tmpVersionedWorkflowCallData.containsKey(name)) {
            tmpVersionedWorkflowCallData.get(name).add(workflowCall);
          } else {
            final Set<WorkflowCallVO> contentCollection = new HashSet<WorkflowCallVO>();
            contentCollection.add(workflowCall);
            tmpVersionedWorkflowCallData.put(name, contentCollection);
          }
        }

        for (final Map.Entry<String, Set<WorkflowCallVO>> versionedWorkflowCallDataEntry : tmpVersionedWorkflowCallData.entrySet()) {
          final String name = versionedWorkflowCallDataEntry.getKey();

          final NamedWorkflowCalls namedWorkflowCalls = objectFactory.createNamedWorkflowCalls();
          namedWorkflowCalls.setWorkflowCallName(name);

          final Set<WorkflowCallVO> versionedWorkflowCalls = versionedWorkflowCallDataEntry.getValue();
          for (final WorkflowCallVO workflowCall : versionedWorkflowCalls) {
            final VersionedWorkflowCallData versionedWorkflowCallData = objectFactory.createVersionedWorkflowCallData();
            versionedWorkflowCallData.setText(workflowCall.getContent());
            versionedWorkflowCallData.setVersion(workflowCall.getVersion());
            namedWorkflowCalls.getVersionedWorkflowCallData().add(versionedWorkflowCallData);
          }
          workflowCallsResponse.getNamedWorkflowCalls().add(namedWorkflowCalls);
        }
      }
    }

    return workflowCallsResponse;
  }
}