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

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import uk.ac.ox.cs.science2020.zoon.business_manager.BusinessIdentifiers;
import uk.ac.ox.cs.science2020.zoon.business_manager.business.SimulationProcessingGateway;
import uk.ac.ox.cs.science2020.zoon.business_manager.entity.Simulation;
import uk.ac.ox.cs.science2020.zoon.business_manager.manager.SimulationManager;

/**
 * 
 *
 * @author geoff
 */
@Component(BusinessIdentifiers.COMPONENT_SIMULATION_SERVICE)
public class SimulationServiceImpl implements SimulationService {

  @Autowired @Qualifier(BusinessIdentifiers.COMPONENT_SIMULATION_MANAGER)
  private SimulationManager simulationManager;

  @Autowired @Qualifier(BusinessIdentifiers.COMPONENT_SIMULATION_PROCESSING_GATEWAY)
  private SimulationProcessingGateway simulationProcessingGateway;

  private static final Log log = LogFactory.getLog(SimulationServiceImpl.class);

  /* (non-Javadoc)
   * @see uk.ac.ox.cs.science2020.zoon.business_manager.service.SimulationService#findBySimulationId(long)
   */
  public Simulation findBySimulationId(final long simulationId) {
    log.debug("~findBySimulationId() : Invoked.");

    return simulationManager.findBySimulationId(simulationId);
  }

  /* (non-Javadoc)
   * @see uk.ac.ox.cs.science2020.zoon.business_manager.service.SimulationService#recordOutputLines(long, java.util.List)
   */
  public boolean recordOutputLines(final long simulationId, final List<String> outputLines) {
    log.debug("~recordOutputLines() : Invoked for simulation '" + simulationId + "'.");

    return simulationManager.recordOutputLines(simulationId, outputLines);
  }

  /* (non-Javadoc)
   * @see uk.ac.ox.cs.science2020.zoon.business_manager.service.SimulationService#processSimulation(long)
   */
  public Simulation processSimulation(final long simulationId) {
    final String logPrefix = "~processSimulation() : Simulation#" + simulationId + " : ";
    log.debug(logPrefix);

    // reset system flag to avoid re-selection on polling.
    Simulation simulation = findBySimulationId(simulationId);
    simulation.setRequestProcessed();
    log.debug(logPrefix.concat("Updating as request processed."));
    simulation = simulationManager.saveSimulation(simulation);

    simulationProcessingGateway.regularSimulation(simulation);

    return simulation;
  }

  /* (non-Javadoc)
   * @see uk.ac.ox.cs.science2020.zoon.business_manager.service.SimulationService#runSimulation(java.lang.String)
   */
  public long runSimulation(final String parameter) {
    log.debug("~runSimulation() : Invoked with parameter '" + parameter + "'.");

    Simulation simulation = simulationManager.createNewSimulation(parameter);
    simulation = simulationManager.saveSimulation(simulation);

    final Long simulationId = simulation.getId();
    if (simulationId != null) {
      return simulationId;
    } else {
      final String errorMessage = "Simulation manager didn't give a new Simulation an id!";
      log.error("~runSimulation() : " + errorMessage);
      throw new UnsupportedOperationException(errorMessage); 
    }
  }

  /* (non-Javadoc)
   * @see uk.ac.ox.cs.science2020.zoon.business_manager.service.SimulationService#setProcessFinished(long)
   */
  public void setProcessFinished(final long simulationId) {
    log.debug("~setProcessFinished() : Invoked for simulation '" + simulationId + "'.");

    simulationManager.setProcessFinished(simulationId);
  }
}