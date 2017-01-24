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
package uk.ac.ox.cs.science2020.zoon.shared.value.object.artifact;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import uk.ac.ox.cs.science2020.zoon.shared.value.type.ArtifactType;

/**
 *
 *
 * @author geoff
 */
public abstract class AbstractArtifactVO {

  public static final String LATEST = "latest";

  private static final int randomMinimumArtifactNameLength = 3;
  private static final int randomMinimumArtifactVersionLength = 1;

  private final String name;
  private final String version;
  private final List<AuthorVO> authors = new ArrayList<AuthorVO>();
  private final String submitted;
  private final String content;
  private final ArtifactType type;
  private final boolean privateArtifact;

  /**
   * Initialising constructor.
   * 
   * @param name Artifact name.
   * @param version Artifact version.
   * @param authors Artifact authors. 
   * @param content Artifact content.
   * @param type Artifact type.
   * @param privateArtifact {@code true} if artifact is private, otherwise {@code false}.
   * @throws IllegalArgumentException If a null or blank value is assigned for any parameter.
   */
  public AbstractArtifactVO(final String name, final String version, final List<AuthorVO> authors,
                            final String submitted, final String content, final ArtifactType type,
                            final boolean privateArtifact)
                            throws IllegalArgumentException {
    if (StringUtils.isBlank(name) || name.trim().length() < randomMinimumArtifactNameLength) {
      throw new IllegalArgumentException("Artifact name must contain at least " + randomMinimumArtifactNameLength + " character(s).");
    }
    if (StringUtils.isBlank(version) || version.trim().length() < randomMinimumArtifactVersionLength) {
      throw new IllegalArgumentException("Artifact version must contain at least " + randomMinimumArtifactVersionLength + " character(s).");
    }
    /*
    if (StringUtils.isBlank(content)) {
      throw new IllegalArgumentException("Artifact content must be assigned!");
    }
    */
    if (type == null) {
      throw new IllegalArgumentException("Artifact type must be assigned!");
    }

    this.name = name.trim();
    this.version = version.trim();
    if (authors != null) {
      this.authors.addAll(authors);
    }
    this.submitted = submitted;
    this.content = content;
    this.type = type;
    this.privateArtifact = privateArtifact;
  }

  /**
   * Retrieve a URL derived from the module name.
   * 
   * @return the name URL
   */
  public String getNameURL() {
    String nameURL = "";
    try {
      nameURL = new URI(null, getName(), null).toASCIIString();
    } catch (URISyntaxException e) {
      e.printStackTrace();
    }
    return nameURL;
  }

  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "AbstractArtifactVO [name=" + name + ", version=" + version
        + ", content=" + content + ", type=" + type + ", privateArtifact="
        + privateArtifact + "]";
  }

  /* (non-Javadoc)
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    result = prime * result + ((type == null) ? 0 : type.hashCode());
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
    AbstractArtifactVO other = (AbstractArtifactVO) obj;
    if (name == null) {
      if (other.name != null)
        return false;
    } else if (!name.equals(other.name))
      return false;
    if (type != other.type)
      return false;
    if (version == null) {
      if (other.version != null)
        return false;
    } else if (!version.equals(other.version))
      return false;
    return true;
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
   * Retrieve the artifact version.
   * 
   * @return Artifact version.
   */
  public String getVersion() {
    return version;
  }

  /**
   * Retrieve artifact authors.
   * 
   * @return Artifact authors (or empty collection if not assigned, e.g. private Workflow Call).
   */
  public List<AuthorVO> getAuthors() {
    return authors;
  }

  /**
   * Retrieve the submitted/published date.
   * 
   * @return Submitted/Published data (or {@code null} if not assigned).
   */
  public String getSubmitted() {
    return submitted;
  }

  /**
   * @return the content
   */
  public String getContent() {
    return content;
  }

  /**
   * Retrieve the artifact type.
   * 
   * @return Artifact type
   */
  public ArtifactType getType() {
    return type;
  }

  /**
   * Indicator if artifact is private or not.
   * 
   * @return {@code true} if artifact is private, otherwise {@code false}.
   */
  public boolean isPrivateArtifact() {
    return privateArtifact;
  }
}