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
package uk.ac.ox.cs.science2020.zoon.business_manager.value.object.artifact;

import org.apache.commons.lang3.StringUtils;

import uk.ac.ox.cs.science2020.zoon.shared.value.type.ArtifactType;

/**
 *
 *
 * @author geoff
 */
public class MinimumArtifactDataVO {

  private final int userId;
  private final String artifactName;
  private final String artifactVersion;
  private final ArtifactType artifactType;

  /**
   * Initialising constructor.
   * 
   * @param userId User identifier.
   * @param artifactName Artifact name.
   * @param artifactVersion Artifact version.
   * @param artifactContent Artifact content.
   * @param artifactType Artifact type.
   * @throws IllegalArgumentException If any required arguments are empty or {@code null}.
   */
  public MinimumArtifactDataVO(final int userId, final String artifactName,
                               final String artifactVersion, final ArtifactType artifactType)
                               throws IllegalArgumentException {
    if (StringUtils.isBlank(artifactName) || StringUtils.isBlank(artifactVersion) ||
        artifactType == null) {
      throw new IllegalArgumentException("Artifact name, version, type are all required!");
    }
    this.userId = userId;
    this.artifactName = artifactName;
    this.artifactVersion = artifactVersion;
    this.artifactType = artifactType;
  }

  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "ArtifactStoreVO [userId=" + userId + ", artifactName="
        + artifactName + ", artifactVersion=" + artifactVersion
        + ", artifactType=" + artifactType + "]";
  }

  /**
   * Retrieve the user identifier.
   * 
   * @return User identifier.
   */
  public int getUserId() {
    return userId;
  }

  /**
   * Retrieve the artifact name.
   * 
   * @return Artifact name.
   */
  public String getArtifactName() {
    return artifactName;
  }

  /**
   * Retrieve the artifact version.
   * 
   * @return Artifact version.
   */
  public String getArtifactVersion() {
    return artifactVersion;
  }

  /**
   * Retrieve the artifact type.
   * 
   * @return Artifact type.
   */
  public ArtifactType getArtifactType() {
    return artifactType;
  }
}