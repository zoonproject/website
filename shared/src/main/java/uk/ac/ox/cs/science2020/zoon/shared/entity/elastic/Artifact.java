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
package uk.ac.ox.cs.science2020.zoon.shared.entity.elastic;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldIndex;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * Abstract base class for modules and workflow calls.
 *
 * @author geoff
 */
public abstract class Artifact {

  @Id
  private String id;

  // Default analysing otherwise, e.g. http://stackoverflow.com/questions/21933787/elasticsearch-not-returning-results-for-terms-query-against-string-property
  @Field(type=FieldType.String, index=FieldIndex.not_analyzed, store=true)
  private String name;
  @Field(type=FieldType.String, index=FieldIndex.not_analyzed, store=true)
  private String version;
  @Field(type=FieldType.Nested, index=FieldIndex.not_analyzed, store=true)
  private List<Author> authors = new ArrayList<Author>();
  @Field(type=FieldType.String, index=FieldIndex.not_analyzed, store=true)
  private String submitted;

  /**
   * Default constructor.
   */
  protected Artifact() {}

  /**
   * Initialising constructor.
   * 
   * @param name Name.
   * @param version Version.
   * @param authors Authors.
   * @param submitted Date submitted.
   */
  public Artifact(final String name, final String version, final List<Author> authors,
                  final String submitted) {
    setName(name);
    setVersion(version);
    setAuthors(authors);
    setSubmitted(submitted);
  }

  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "Artifact [id=" + id + ", name=" + name + ", version=" + version
        + ", authors=" + authors + ", submitted=" + submitted + "]";
  }

  /* (non-Javadoc)
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    result = prime * result + ((version == null) ? 0 : version.hashCode());
    return result;
  }

  /* (non-Javadoc)
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Artifact other = (Artifact) obj;
    if (name == null) {
      if (other.name != null)
        return false;
    } else if (!name.equals(other.name))
      return false;
    if (version == null) {
      if (other.version != null)
        return false;
    } else if (!version.equals(other.version))
      return false;
    return true;
  }

  /**
   * Retrieve the artifact identifier.
   * 
   * @return Artifact identifier.
   */
  public String getId() {
    return id;
  }

  /**
   * Assign the artifact identifier.
   * 
   * @param id Artifact identifier to assign.
   */
  public void setId(final String id) {
    this.id = id;
  }

  /**
   * Retrieve the artifact name.
   * 
   * @return Artifact name.
   */
  public String getName() {
    return name;
  }

  /**
   * Assign artifact name.
   * 
   * @param name Artifact name to set.
   */
  public void setName(final String name) {
    this.name = name;
  }

  /**
   * Retrieve the artifact version.
   * 
   * @return Version.
   */
  public String getVersion() {
    return version;
  }

  /**
   * Assign the artifact version.
   * 
   * @param version Version to assign.
   */
  public void setVersion(final String version) {
    this.version = version;
  }

  /**
   * Retrieve the artifact author(s).
   * 
   * @return Retrieve the artifact author(s).
   */
  public List<Author> getAuthors() {
    return authors;
  }

  /**
   * Assign the artifact author(s).
   * 
   * @param authors Author(s) to assign.
   */
  public void setAuthors(final List<Author> authors) {
    this.authors = authors;
  }

  /**
   * Retrieve the submitted date.
   * 
   * @return Submitted date.
   */
  public String getSubmitted() {
    return submitted;
  }

  /**
   * Assign the submitted date.
   * 
   * @param submitted Submitted date.
   */
  public void setSubmitted(final String submitted) {
    this.submitted = submitted;
  }
}