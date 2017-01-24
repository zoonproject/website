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
package uk.ac.ox.cs.science2020.zoon.shared.value.object.artifact.module;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uk.ac.ox.cs.science2020.zoon.shared.entity.elastic.module.Module;
import uk.ac.ox.cs.science2020.zoon.shared.value.object.artifact.AbstractArtifactVO;
import uk.ac.ox.cs.science2020.zoon.shared.value.object.artifact.AuthorVO;
import uk.ac.ox.cs.science2020.zoon.shared.value.type.ArtifactType;
import uk.ac.ox.cs.science2020.zoon.shared.value.type.ModuleType;

/**
 * Alternative value object representation of a module.
 * <p>
 * Used when more information is required than can be stored in a {@link Module} object.
 *
 * @author geoff
 */
public class ModuleVO extends AbstractArtifactVO {

  private final ModuleType moduleType;
  private final Map<String, String> parameters = new HashMap<String, String>();
  private final List<String> returnValues = new ArrayList<String>();
  private final String location;
  private final String source;
  private final List<String> descriptions = new ArrayList<String>();
  private final String references;
  private final String content;

  private final boolean verified;
  private final boolean latest;
  private boolean workflowed;

  /**
   * Initialising constructor -- value object content derived from elastic entity.
   * <p>
   * <b>Note :</b> It is assumed that data in the elastic repository represents the latest versions
   * of the modules.
   * 
   * @param module Elastic module.
   */
  public ModuleVO(final Module module) {
    this(module.getName(), module.getVersion(), ModuleType.valueOf(module.getType()),
         module.getParameters(), module.getLocation(), module.getReturnValues(), module.getSource(),
         module.getSubmitted(), AuthorVO.retrieveAuthorVOs(module.getAuthors()),
         module.getDescriptions(), module.getReferences(), module.getContent(), false, true, true);
  }

  /**
   * Initialising constructor -- value object content derived from properties.
   * 
   * @param name Name.
   * @param version Version.
   * @param moduleType Type.
   * @param parameters Parameter collection.
   * @param location Location where module data read in from, e.g. 'ZOON copy on fs'.
   * @param returnValues Return values.
   * @param source Source. TODO : What's this!? 
   * @param submitted Date submitted.
   * @param authors Author(s).
   * @param descriptions Description(s).
   * @param references References.
   * @param content 
   * @param privateModule Flag to indicate if private module.
   * @param verified Flag to indicate if module verified positively.
   * @param workflowed {@code true} if appears in a public workflow call, otherwise {@code false}
   * @param latest {@code true} if latest <b>public</b> version of the module, otherwise
   *               {@code false}.
   */
  public ModuleVO(final String name, final String version, final ModuleType moduleType,
                  final Map<String, String> parameters, final String location,
                  final List<String> returnValues, final String source, final String submitted,
                  final List<AuthorVO> authors, final List<String> descriptions,
                  final String references, final String content, final boolean privateModule,
                  final boolean verified, final boolean latest) {
    super(name, version, authors, submitted, content, ArtifactType.MODULE, privateModule);
    this.moduleType = moduleType;
    if (parameters != null) {
      this.parameters.putAll(parameters);
    }
    this.location = location;
    if (returnValues != null) {
      this.returnValues.addAll(returnValues);
    }
    this.source = source;
    if (descriptions != null) {
      this.descriptions.addAll(descriptions);
    }
    this.references = references;
    this.content = content;
    this.verified = verified;
    this.latest = latest;
  }

  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "ModuleVO [moduleType=" + moduleType + ", parameters=" + parameters
        + ", returnValues=" + returnValues + ", location=" + location
        + ", source=" + source + ", descriptions=" + descriptions
        + ", references=" + references + ", content=" + content + ", verified="
        + verified + ", latest=" + latest + ", workflowed=" + workflowed
        + ", toString()=" + super.toString() + "]";
  }

  /**
   * @return the moduleType
   */
  public ModuleType getModuleType() {
    return moduleType;
  }

  /**
   * @return the parameters
   */
  public Map<String, String> getParameters() {
    return parameters;
  }

  /**
   * @return the returnValues
   */
  public List<String> getReturnValues() {
    return returnValues;
  }

  /**
   * @return the location
   */
  public String getLocation() {
    return location;
  }

  /**
   * @return the source
   */
  public String getSource() {
    return source;
  }

  /**
   * @return the descriptions
   */
  public List<String> getDescriptions() {
    return descriptions;
  }

  /**
   * @return the references
   */
  public String getReferences() {
    return references;
  }

  /**
   * @return the content
   */
  public String getContent() {
    return content;
  }

  /**
   * @return the verified
   */
  public boolean isVerified() {
    return verified;
  }

  /**
   * Return flag indicating if latest <b>public</b> version of the module.
   * 
   * @return {@code true} if latest public version, otherwise {@code false}.
   */
  public boolean isLatest() {
    return latest;
  }

  /**
   * Assign flag to indicate if module appears in a public workflow.
   * 
   * @param workflowed {@code true} if appears in public workflow, otherwise {@code false}.
   */
  public void setWorkflowed(final boolean workflowed) {
    this.workflowed = workflowed;
  }

  /**
   * Retrieve flag to indicate if module appears in a public workflow.
   * 
   * @return {@code true} if appears in a public workflow, otherwise {@code false}.
   */
  public boolean isWorkflowed() {
    return workflowed;
  }
}