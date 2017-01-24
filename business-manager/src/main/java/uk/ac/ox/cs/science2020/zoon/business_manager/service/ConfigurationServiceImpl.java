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
package uk.ac.ox.cs.science2020.zoon.business_manager.service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import uk.ac.ox.cs.science2020.zoon.business_manager.BusinessIdentifiers;
import uk.ac.ox.cs.science2020.zoon.shared.value.type.ModuleType;

/**
 * Implementation of the configuration service interface.
 *
 * @author geoff
 */
@Component(BusinessIdentifiers.COMPONENT_CONFIGURATION_SERVICE)
public class ConfigurationServiceImpl implements ConfigurationService {

  private static final Log log = LogFactory.getLog(ConfigurationServiceImpl.class);

  /* (non-Javadoc)
   * @see uk.ac.ox.cs.science2020.zoon.business_manager.service.ConfigurationService#retrieveModuleTypes()
   */
  @Override
  public Set<ModuleType> retrieveModuleTypes() {
    final Set<ModuleType> moduleTypes = new HashSet<ModuleType>(ModuleType.values().length);
    for (final ModuleType moduleType : Arrays.asList(ModuleType.values())) {
      moduleTypes.add(moduleType);
    }
    log.debug("~retrieveModuleTypes() : Retrieved '" + moduleTypes.toString() + "'.");
    return moduleTypes;
  }
}