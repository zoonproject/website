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

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;

import uk.ac.ox.cs.science2020.zoon.business_manager.BusinessIdentifiers;

/**
 *
 *
 * @author geoff
 */
public class ModulesSplitter {

  private static final Log log = LogFactory.getLog(ModulesSplitter.class);

  /**
   * Split the collection of module parsing candidates.
   * 
   * @param parseCandidates Module parsing candidates.
   * @return Corresponding SI messages.
   */
  public Set<Message<String>> split(final Set<ParseCandidate> parseCandidates) {
    final Set<Message<String>> splitModules = new HashSet<Message<String>>();
    for (final ParseCandidate parseCandidate : parseCandidates) {
      final String moduleName = parseCandidate.getArtifactName();
      final String moduleVersion = parseCandidate.getArtifactVersion();
      final String moduleContent = parseCandidate.getArtifactContent();
      log.debug("~split() : Splitting '" + moduleName + "'.");
      final Message<String> splitModule = MessageBuilder.withPayload(moduleContent)
                                                        .setHeader(BusinessIdentifiers.SI_HDR_MODULE_NAME,
                                                                   moduleName)
                                                        .setHeader(BusinessIdentifiers.SI_HDR_MODULE_VERSION,
                                                                   moduleVersion)
                                                        .build();
      splitModules.add(splitModule);
    }
    return splitModules;
  }
}