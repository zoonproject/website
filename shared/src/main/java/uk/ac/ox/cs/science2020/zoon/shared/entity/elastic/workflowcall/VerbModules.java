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
package uk.ac.ox.cs.science2020.zoon.shared.entity.elastic.workflowcall;

import java.util.ArrayList;
import java.util.List;

/**
 * Holder of associations between a module verb (e.g. 'select', 'list') and the collection of module name/version
 * associations.
 * <p>
 * For example there is a 'list' of 'module1,module1version', 'module2, module2version', etc.
 *
 * @author geoff
 */
public class VerbModules {

  private String verb;
  private List<ModuleVersions> moduleVersions = new ArrayList<ModuleVersions>();

  /**
   * Default constructor.
   */
  protected VerbModules() {}

  /**
   * Initialising constructor.
   * 
   * @param verb Verb, e.g. 'list', 'select'.
   * @param moduleVersions Module versions.
   */
  public VerbModules(final String verb, final List<ModuleVersions> moduleVersions) {
    setVerb(verb);
    setModuleVersions(moduleVersions);
  }

  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "VerbModules [verb=" + verb + ", moduleVersions=" + moduleVersions + "]";
  }

  /**
   * Retrieve the verb, e.g. 'list', 'select', etc.
   * 
   * @return Association verb.
   */
  public String getVerb() {
    return verb;
  }

  /**
   * Assign the verb, e.g. 'list', 'select', etc..
   * 
   * @param verb The verb linking the named and versioned modules. 
   */
  public void setVerb(final String verb) {
    this.verb = verb;
  }

  /**
   * Retrieve the collection of named and versioned modules.
   * 
   * @return Collection of named and versioned modules.
   */
  public List<ModuleVersions> getModuleVersions() {
    return moduleVersions;
  }

  /**
   * Assign the collection of named and versioned modules.
   * 
   * @param modules Collection of named and versioned modules.
   */
  public void setModuleVersions(final List<ModuleVersions> moduleVersions) {
    this.moduleVersions = moduleVersions;
  }
}