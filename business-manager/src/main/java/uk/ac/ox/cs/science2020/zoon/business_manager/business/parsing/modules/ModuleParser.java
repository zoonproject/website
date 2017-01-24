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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.integration.annotation.Header;
import org.springframework.integration.annotation.Payload;

import uk.ac.ox.cs.science2020.zoon.business_manager.BusinessIdentifiers;
import uk.ac.ox.cs.science2020.zoon.business_manager.manager.ArtifactManager;
import uk.ac.ox.cs.science2020.zoon.business_manager.value.object.RInvocationResultsVO;
import uk.ac.ox.cs.science2020.zoon.shared.value.type.ArtifactType;

/**
 *
 *
 * @author geoff
 */
public class ModuleParser {

  @Autowired @Qualifier(BusinessIdentifiers.COMPONENT_ARTIFACT_MANAGER)
  private ArtifactManager artifactManager;

  private static final Log log = LogFactory.getLog(ModuleParser.class);

  /**
   * Parse module with given name, version (optional) and content.
   * 
   * @param moduleName Module name.
   * @param moduleVersion Module version (or {@code null} if not known).
   * @param moduleContent Module content.
   * @return Parsing candidate, with parsing outcome included.
   */
  public ParseCandidate parseModule(final @Header(required=true,
                                                  value=BusinessIdentifiers.SI_HDR_MODULE_NAME)
                                          String moduleName,
                                    final @Header(required=false,
                                                  value=BusinessIdentifiers.SI_HDR_MODULE_VERSION)
                                          String moduleVersion,
                                    final @Payload String moduleContent) {

    final Integer parseIdentifier = artifactManager.initiateArtifactParse(ArtifactType.MODULE,
                                                                          moduleName,
                                                                          moduleContent);

    boolean jobCompleted = false;
    RInvocationResultsVO results = null;
    while (!jobCompleted) {
      results = artifactManager.retrieveParseResults(parseIdentifier);
      // We're not finished until the process is finished and the JSON file has been read in.
      // i.e. the process can finish before the JSON file is read in!
      if (results.isJobCompleted()) {
        jobCompleted = true;
        log.debug("~loadPublicModulesOnStartup() : Parsing job completed '" + jobCompleted + "'.");
      }
      try {
        log.debug("~loadPublicModulesOnStartup() : Sleeping!");
        Thread.sleep(500);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
    artifactManager.cleanUpParseStatics(parseIdentifier);

    final ParseCandidate parseCandidateResult = new ParseCandidate(moduleName, moduleVersion,
                                                                   moduleContent);
    parseCandidateResult.setParseResults(results);

    return parseCandidateResult;
  }
}