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
package uk.ac.ox.cs.science2020.zoon.business_manager.value.object.store;

import org.apache.commons.lang3.StringUtils;

import uk.ac.ox.cs.science2020.zoon.business_manager.value.object.artifact.MinimumArtifactDataVO;
import uk.ac.ox.cs.science2020.zoon.shared.value.type.ArtifactType;

/**
 *
 *
 * @author geoff
 */
public class ArtifactStoreVO extends MinimumArtifactDataVO {

  private final String artifactContent;
  private final Object parsedData;

  /**
   * Initialising constructor.
   * 
   * @param userId User identifier.
   * @param artifactName Artifact name.
   * @param artifactVersion Artifact version.
   * @param artifactContent Artifact content.
   * @param artifactType Artifact type.
   * @param artifactContent Artifact content.
   * @param parsedData Data from artifact parsing, e.g. Tom's JSON for modules.
   * @throws IllegalArgumentException If any arguments are empty or {@code null}.
   */
  public ArtifactStoreVO(final int userId, final String artifactName, final String artifactVersion,
                         final ArtifactType artifactType, final String artifactContent,
                         final Object parsedData) throws IllegalArgumentException {
    super(userId, artifactName, artifactVersion, artifactType);
    if (StringUtils.isBlank(artifactContent)) {
      throw new IllegalArgumentException("Artifact content is required!");
    }
    this.artifactContent = artifactContent;
    this.parsedData = parsedData;
  }

  /**
   * Retrieve minimum information about artifact being stored.
   * 
   * @return Minimum information about artifact being stored.
   */
  public String retrieveMinimumInfo() {
    return super.toString();
  }

  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "ArtifactStoreVO [artifactContent=" + artifactContent + ", parsedData=" + parsedData + "]";
  }

  /**
   * Retrieve the artifact content.
   * 
   * @return Artifact content.
   */
  public String getArtifactContent() {
    return artifactContent;
  }

  /**
   * Retrieve the data from artifact parsing, e.g. Tom's JSON for modules.
   * 
   * @return Parsed data.
   */
  public Object getParsedData() {
    return parsedData;
  }
}