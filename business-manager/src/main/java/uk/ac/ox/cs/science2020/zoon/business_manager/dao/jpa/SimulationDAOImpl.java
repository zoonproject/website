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
package uk.ac.ox.cs.science2020.zoon.business_manager.dao.jpa;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;

import uk.ac.ox.cs.science2020.zoon.business_manager.BusinessIdentifiers;
import uk.ac.ox.cs.science2020.zoon.business_manager.dao.SimulationDAO;
import uk.ac.ox.cs.science2020.zoon.business_manager.entity.Simulation;

/**
 * 
 *
 * @author geoff
 */
@Repository(BusinessIdentifiers.COMPONENT_SIMULATION_DAO)
public class SimulationDAOImpl implements SimulationDAO {

  @PersistenceContext
  private EntityManager entityManager;

  private static final Log log = LogFactory.getLog(SimulationDAOImpl.class);

  /* (non-Javadoc)
   * @see uk.ac.ox.cs.science2020.zoon.business_manager.dao.SimulationDAO#findBySimulationId(long)
   */
  public Simulation findBySimulationId(final long simulationId) {
    log.debug("~findBySimulationId() : Invoked for simulation with id '" + simulationId + "'.");

    return entityManager.find(Simulation.class, simulationId);
  }

  /* (non-Javadoc)
   * @see uk.ac.ox.cs.science2020.zoon.business_manager.dao.SimulationDAO#recordOutputLines(long, java.util.List)
   */
  public boolean recordOutputLines(final long simulationId, final List<String> outputLines) {
    log.debug("~recordOutputLines : Invoked for simulation id '" + simulationId + "'.");

    Simulation simulation = findBySimulationId(simulationId);

    if (simulation != null) {
      simulation.addOutput(outputLines);
      simulation = store(simulation);
    } else {
      final String errorMessage = "Simulation with identifier '" + simulationId + "' cannot be found!";
      log.error("~recordOutputLines() : " + errorMessage);
    }

    return simulation.hasCompleted();
  }

  /* (non-Javadoc)
   * @see uk.ac.ox.cs.science2020.zoon.business_manager.dao.SimulationDAO#setProcessFinished(long)
   */
  public void setProcessFinished(final long simulationId) {
    log.debug("~setProcessFinished() : Invoked for simulation id '" + simulationId + "'.");

    final Simulation simulation = findBySimulationId(simulationId);

    if (simulation != null) {
      simulation.setCompleted();
      store(simulation);
    } else {
      final String errorMessage = "Simulation with identifier '" + simulationId + "' cannot be found!";
      log.error("~setProcessFinished() : " + errorMessage);
    }
  }


  /* (non-Javadoc)
   * @see uk.ac.ox.cs.science2020.zoon.business_manager.dao.SimulationDAO#store(uk.ac.ox.cs.science2020.zoon.business_manager.entity.Simulation)
   */
  public Simulation store(Simulation simulation) {
    assert (simulation != null) : "Invalid attempt to store a null Simulation object.";
    log.debug("~store() : Invoked for simulation '" + simulation.toString() + "'.");

    if (simulation.getId() != null) {
      return entityManager.merge(simulation);
    } else {
      entityManager.persist(simulation);
      return simulation;
    }
  }

}