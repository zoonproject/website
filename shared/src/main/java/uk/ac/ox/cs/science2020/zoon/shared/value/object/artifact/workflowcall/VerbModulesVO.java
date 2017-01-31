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
package uk.ac.ox.cs.science2020.zoon.shared.value.object.artifact.workflowcall;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.json.JSONObject;

import uk.ac.ox.cs.science2020.zoon.shared.entity.elastic.workflowcall.ModuleVersions;
import uk.ac.ox.cs.science2020.zoon.shared.entity.elastic.workflowcall.VerbModules;
import uk.ac.ox.cs.science2020.zoon.shared.value.type.workflowcall.Verb;

/**
 * Hold the relationship between the workflow call verb and the module/params, e.g.
 * Chain(OneHundredBackground, CrossValidate).
 *
 * @author 
 */
public class VerbModulesVO {

  private final Verb verb;
  private final List<ModuleParamsVO> moduleParams = new ArrayList<ModuleParamsVO>();

  private static final Log log = LogFactory.getLog(VerbModulesVO.class);

  /**
   * Initialising constructor.
   * 
   * @param verb Verb, e.g. select.
   * @param moduleParams Collection of module names and their parameter names.
   * @throws IllegalArgumentException If arguments are {@code null} or empty.
   */
  public VerbModulesVO(final Verb verb, final List<ModuleParamsVO> moduleParams) {
    if (verb == null) {
      throw new IllegalArgumentException("Verb is required for VerbModulesVO creation");
    }
    if (moduleParams == null || moduleParams.isEmpty()) {
      throw new IllegalArgumentException("Module/Params are required for VerbModulesVO creation");
    }

    this.verb = verb;
    this.moduleParams.addAll(moduleParams);
  }

  /**
   * Retrieve the content as a JSON representation.
   * 
   * @return JSON representation of content.
   */
  public JSONObject asJSONObject() {
    final JSONObject jsonObject = new JSONObject();

    switch (verb) {
      case select :
        if (moduleParams.size() != 1) {
          final String errorMessage = "Expecting module and params size for verb '" + verb + "' to be 1, not '" + moduleParams.size() + "'";
          log.error("~asJSONObject() : ".concat(errorMessage));
          throw new IllegalStateException(errorMessage);
        }
        break;
      case Chain :
      case list :
        if (moduleParams.size() < 2) {
          final String errorMessage = "Expecting module and params size for verb '" + verb + "' to < 2, not '" + moduleParams.size() + "'";
          log.error("~asJSONObject() : ".concat(errorMessage));
          throw new IllegalStateException(errorMessage);
        }
        break;
      default :
        throw new UnsupportedOperationException("Converting verb '" + verb + "' to JSONObject not yet implemented!");
    }

    try {
      /* Important to use a linked hash map to retain order of module names */
      final Map<String, Map<String, String>> forJSON = new LinkedHashMap<String, Map<String, String>>();
      for (final ModuleParamsVO module : moduleParams) {
        final String moduleName = module.getModuleName();
        final Set<ModuleParamVO> moduleParameters = module.getParameters();
        final Map<String, String> forJSONParams = new HashMap<String, String>(moduleParameters.size());
        for (final ModuleParamVO parameter : moduleParameters) {
          forJSONParams.put(parameter.getName(), parameter.getValue());
        }
        forJSON.put(moduleName, forJSONParams);
      }
      // Note the use of an object even if it's a 'select' verb.
      jsonObject.put(verb.toString(), new JSONObject(forJSON));
    } catch (JSONException e) {
      e.printStackTrace();
      log.error("~asJSONObject() : Exception converting '" + verb + "' to JSON.");
      throw new IllegalStateException(e.getMessage());
    }

    return jsonObject;
  }

  /**
   * Retrieve value object an elastic entity.
   * 
   * @return Elastic version of value object.
   */
  public VerbModules asVerbModules() {
    final List<ModuleVersions> moduleVersions = new ArrayList<ModuleVersions>();
    for (final ModuleParamsVO moduleParams : getModuleParams()) {
      moduleVersions.add(new ModuleVersions(moduleParams.getModuleName(),
                                            moduleParams.getModuleVersion()));
    }
    return new VerbModules(getVerb().toString(), moduleVersions);
  }

  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "Module [verb=" + verb + ", moduleParams=" + moduleParams + "]";
  }

  /**
   * @return the verb
   */
  public Verb getVerb() {
    return verb;
  }

  /**
   * @return the moduleParams
   */
  public List<ModuleParamsVO> getModuleParams() {
    return Collections.unmodifiableList(moduleParams);
  }
}