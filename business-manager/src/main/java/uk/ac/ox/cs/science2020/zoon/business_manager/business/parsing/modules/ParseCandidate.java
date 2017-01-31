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
package uk.ac.ox.cs.science2020.zoon.business_manager.business.parsing.modules;

import uk.ac.ox.cs.science2020.zoon.business_manager.value.object.RInvocationResultsVO;

/**
 *
 *
 * @author geoff
 */
public class ParseCandidate {

  private final String artifactName;
  private String artifactVersion;
  private final String artifactContent;
  private RInvocationResultsVO parseResults;

  /**
   * Initialising constructor.
   * 
   * @param artifactName Artifact name.
   * @param artifactVersion Artifact version (or {@code null} if not known).
   * @param artifactContent Artifact content.
   */
  /*
   * In cases such as reading from GitHub, we don't know the version until after parsing, whereas
   * when reading from Gitblit we do know the version, so keep it handy.
   */
  public ParseCandidate(final String artifactName,
                        final String artifactVersion,
                        final String artifactContent) {
    this.artifactName = artifactName;
    this.artifactVersion = artifactVersion;
    this.artifactContent = artifactContent;
  }

  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "ParseCandidate [artifactName=" + artifactName
        + ", artifactVersion=" + artifactVersion + ", artifactContent="
        + artifactContent + ", parseResults=" + parseResults + "]";
  }

  /**
   * Retrieve parse results JSON.
   * 
   * @return Parse results JSON (or {@code null} if not available).
   */
  public String retrieveResultJSON() {
    if (parseResults == null) {
      return null;
    }
    return parseResults.getJson();
  }

  /**
   * Retrieve if parse results are verified.
   * 
   * @return Verified indicator (or {@code null} if not available).
   */
  public Boolean isResultVerified() {
    if (parseResults == null) {
      return null;
    }
    return parseResults.isVerified();
  }

  /**
   * @return the parseResults
   */
  public RInvocationResultsVO getParseResults() {
    return parseResults;
  }

  /**
   * @param parseResults the parseResults to set
   */
  public void setParseResults(final RInvocationResultsVO parseResults) {
    this.parseResults = parseResults;
  }

  /**
   * @return the artifactContent
   */
  public String getArtifactContent() {
    return artifactContent;
  }

  /**
   * @return the artifactName
   */
  public String getArtifactName() {
    return artifactName;
  }

  /**
   * @return the artifactVersion
   */
  public String getArtifactVersion() {
    return artifactVersion;
  }

  /**
   * @param artifactVersion the artifactVersion to set
   */
  public void setArtifactVersion(final String artifactVersion) {
    this.artifactVersion = artifactVersion;
  }
}