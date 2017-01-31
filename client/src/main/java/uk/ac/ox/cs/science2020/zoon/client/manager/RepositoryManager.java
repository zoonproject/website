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
package uk.ac.ox.cs.science2020.zoon.client.manager;

import java.util.List;
import java.util.Map;
import java.util.Set;

import uk.ac.ox.cs.science2020.zoon.client.entity.Identity;
import uk.ac.ox.cs.science2020.zoon.shared.value.object.artifact.module.ModuleVO;
import uk.ac.ox.cs.science2020.zoon.shared.value.object.artifact.workflowcall.WorkflowCallVO;

/**
 * Management interface for querying the elastic (public data) repository.
 *
 * @author geoff
 */
public interface RepositoryManager {

  /**
   * Find all public modules.
   * 
   * @return Collection of modules, empty if none found.
   */
  List<ModuleVO> findAll();

  /**
   * Retrieve all public modules by identity (matching identities to author names).
   * 
   * @param identities Identities to match to module author names.
   * @return Collection of matched modules, empty if none found.
   */
  List<ModuleVO> findAll(Set<Identity> identities);

  /**
   * Find module by module name and version.
   * 
   * @param moduleName Module name
   * @param moduleVersion Module version.
   * @return Module with specified name and version, or {@code null} if not found.
   */
  ModuleVO findModuleByNameAndVersion(String moduleName, String moduleVersion);

  /**
   * Find workflow call by workflow call name and version.
   * 
   * @param workflowCallName Workflow call name
   * @param workflowCallVersion Workflow call version.
   * @return Workflow call with specified name and version, or {@code null} if not found.
   */
  WorkflowCallVO findWorkflowCallByNameAndVersion(String workflowCallName,
                                                  String workflowCallVersion);

  /**
   * Retrieve all public repository workflow calls.
   * 
   * @return Collection of public workflow calls, empty if none found.
   */
  Map<String, List<WorkflowCallVO>> retrieveWorkflowCalls();

}