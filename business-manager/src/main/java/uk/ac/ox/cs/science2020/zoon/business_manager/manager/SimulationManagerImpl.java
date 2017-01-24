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
package uk.ac.ox.cs.science2020.zoon.business_manager.manager;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.ox.cs.science2020.zoon.business_manager.BusinessIdentifiers;
import uk.ac.ox.cs.science2020.zoon.business_manager.dao.SimulationDAO;
import uk.ac.ox.cs.science2020.zoon.business_manager.entity.Simulation;

/**
 * 
 *
 * @author geoff
 */
@Component(BusinessIdentifiers.COMPONENT_SIMULATION_MANAGER)
@Transactional(readOnly=true)
public class SimulationManagerImpl implements SimulationManager {

  @Autowired @Qualifier(BusinessIdentifiers.COMPONENT_SIMULATION_DAO)
  private SimulationDAO simulationDAO;

  private static final Log log = LogFactory.getLog(SimulationManagerImpl.class);

  /* (non-Javadoc)
   * @see uk.ac.ox.cs.science2020.zoon.business_manager.manager.SimulationManager#findBySimulationId(long)
   */
  public Simulation findBySimulationId(final long simulationId) {
    log.debug("~findBySimulationId() : Invoked.");

    return simulationDAO.findBySimulationId(simulationId);
  }

  /* (non-Javadoc)
   * @see uk.ac.ox.cs.science2020.zoon.business_manager.manager.SimulationManager#createNewSimulation(java.lang.String)
   */
  public Simulation createNewSimulation(final String parameter) {
    log.debug("~createNewSimulation() : Invoked with parameter '" + parameter + "'.");

    final Simulation simulation = new Simulation(parameter);

    return simulation;
  }

  /* (non-Javadoc)
   * @see uk.ac.ox.cs.science2020.zoon.business_manager.manager.SimulationManager#recordOutputLines(long, java.util.List)
   */
  @Transactional(readOnly=false)
  public boolean recordOutputLines(final long simulationId, final List<String> outputLines) {
    log.debug("~recordOutputLines() : Invoked for simulation id '" + simulationId + "'.");

    return simulationDAO.recordOutputLines(simulationId, outputLines);
  }

  /* (non-Javadoc)
   * @see uk.ac.ox.cs.science2020.zoon.business_manager.manager.SimulationManager#saveSimulation(uk.ac.ox.cs.science2020.zoon.business_manager.entity.Simulation)
   */
  @Transactional(readOnly=false)
  public Simulation saveSimulation(final Simulation simulation) {
    log.debug("~saveSimulation() : Invoked for '" + simulation.toString() + "'.");

    return simulationDAO.store(simulation);
  }

  /* (non-Javadoc)
   * @see uk.ac.ox.cs.science2020.zoon.business_manager.manager.SimulationManager#setProcessFinished(long)
   */
  @Transactional(readOnly=false)
  public void setProcessFinished(long simulationId) {
    log.debug("~setProcessFinished() : Invoked for simulation '" + simulationId + "'.");

    simulationDAO.setProcessFinished(simulationId);
  }
}