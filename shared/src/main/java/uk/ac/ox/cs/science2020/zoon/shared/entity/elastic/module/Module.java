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
package uk.ac.ox.cs.science2020.zoon.shared.entity.elastic.module;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.elasticsearch.annotations.Document;

import uk.ac.ox.cs.science2020.zoon.shared.entity.elastic.Artifact;
import uk.ac.ox.cs.science2020.zoon.shared.entity.elastic.Author;
import uk.ac.ox.cs.science2020.zoon.shared.value.object.artifact.module.ModuleVO;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Module entity for Elastic repository.
 *
 * @author geoff
 * @see ModuleVO
 */
@Document(indexName="zoon", type="module")
public class Module extends Artifact {

  private String type;
  private Map<String, String> parameters = new HashMap<String, String>();
  private List<String> returnValues = new ArrayList<String>();
  private String location;
  private String source;
  private List<String> descriptions = new ArrayList<String>();
  private String references;
  @JsonIgnore
  private String content;

  protected Module() {}

  /**
   * Initialising constructor.
   * 
   * @param name Module name.
   * @param version version.
   * @param type Module type, e.g. 'occurrence', 'model', etc.
   * @param parameters Parameters.
   * @param location Location where module data read in from, e.g. 'ZOON copy on fs'.
   * @param returnValues Return values.
   * @param source Source. 
   * @param submitted
   * @param authors
   * @param descriptions
   * @param references
   * @param content
   */
  // Called in Business Manager RunOnce when loading modules into elastic.
  public Module(final String name, final String version, final String type,
                final Map<String, String> parameters, final String location,
                final List<String> returnValues, final String source, final String submitted,
                final List<Author> authors, final List<String> descriptions,
                final String references, final String content) {
    super(name, version, authors, submitted);
    setType(type);
    if (parameters != null && !parameters.isEmpty()) {
      setParameters(parameters);
    }
    if (returnValues != null && !returnValues.isEmpty()) {
      setReturnValues(returnValues);
    }
    setSource(source);
    setLocation(location);
    setSubmitted(submitted);
    if (descriptions != null && !descriptions.isEmpty()) {
      setDescriptions(descriptions);
    }
    setReferences(references);
    setContent(content);
  }

  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "Module [type=" + type + ", parameters=" + parameters
        + ", returnValues=" + returnValues + ", location=" + location
        + ", source=" + source + ", descriptions="
        + descriptions + ", references=" + references + ", content=" + content
        + ", toString()=" + super.toString() + "]";
  }

  /**
   * @return the type
   */
  public String getType() {
    return type;
  }

  /**
   * @param type the type to set
   */
  public void setType(final String type) {
    this.type = type;
  }

  /**
   * @return the parameters
   */
  public Map<String, String> getParameters() {
    return parameters;
  }

  /**
   * @param parameters the parameters to set
   */
  public void setParameters(final Map<String, String> parameters) {
    this.parameters = parameters;
  }

  /**
   * @return the returnValues
   */
  public List<String> getReturnValues() {
    return returnValues;
  }

  /**
   * @param returnValues the returnValues to set
   */
  public void setReturnValues(List<String> returnValues) {
    this.returnValues = returnValues;
  }

  /**
   * @return the source
   */
  public String getSource() {
    return source;
  }

  /**
   * @param source the source to set
   */
  public void setSource(String source) {
    this.source = source;
  }

  /**
   * @return the location
   */
  public String getLocation() {
    return location;
  }

  /**
   * @param location the location to set
   */
  public void setLocation(final String location) {
    this.location = location;
  }

  /**
   * @param descriptions the descriptions to set
   */
  public void setDescriptions(final List<String> descriptions) {
    this.descriptions = descriptions;
  }

  /**
   * @return the descriptions
   */
  public List<String> getDescriptions() {
    return Collections.unmodifiableList(descriptions);
  }

  /**
   * @return the references
   */
  public String getReferences() {
    return references;
  }

  /**
   * @param references the references to set
   */
  public void setReferences(final String references) {
    this.references = references;
  }

  /**
   * @return the content
   */
  public String getContent() {
    return content;
  }

  /**
   * @param content the content to set
   */
  public void setContent(final String content) {
    this.content = content;
  }
}