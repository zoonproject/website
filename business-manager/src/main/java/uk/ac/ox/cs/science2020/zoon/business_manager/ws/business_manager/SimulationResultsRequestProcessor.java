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
package uk.ac.ox.cs.science2020.zoon.business_manager.ws.business_manager;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;

import uk.ac.ox.cs.science2020.zoon.business_manager.BusinessIdentifiers;
import uk.ac.ox.cs.science2020.zoon.business_manager.entity.Simulation;
import uk.ac.ox.cs.science2020.zoon.business_manager.service.SimulationService;
import uk.ac.ox.cs.science2020.zoon.business_manager.ws.schema.jaxb.ObjectFactory;
import uk.ac.ox.cs.science2020.zoon.business_manager.ws.schema.jaxb.SimulationResultsRequest;
import uk.ac.ox.cs.science2020.zoon.business_manager.ws.schema.jaxb.SimulationResultsResponse;

/**
 * 
 *
 * @author geoff
 */
public class SimulationResultsRequestProcessor {

  // Spring injected.
  @Value("${results.dir}")
  private String resultsDir;

  @Autowired @Qualifier(BusinessIdentifiers.COMPONENT_SIMULATION_SERVICE)
  private SimulationService simulationService;

  // Makes web service JAXB objects available.
  private static final ObjectFactory objectFactory = new ObjectFactory();

  private static final Log log = LogFactory.getLog(SimulationResultsRequestProcessor.class);

  /** Default constructor */
  protected SimulationResultsRequestProcessor() {}

  public SimulationResultsResponse processSimulationResultsRequest(final SimulationResultsRequest request) {
    log.debug("~processSimulationResultsRequest() : Invoked.");

    final long simulationId = request.getSimulationId();
    log.debug("~processSimulationResultsRequest() : Invoked for '" + simulationId + "'.");

    final SimulationResultsResponse response = objectFactory.createSimulationResultsResponse();

    final Simulation simulation = simulationService.findBySimulationId(simulationId);
    if (simulation != null) {
      final String rplotsPDFLocation = resultsDir.concat(Long.valueOf(simulationId).toString()).
                                                  concat("/").concat("Rplots.pdf");
      log.debug("~processSimulationResultsRequest() : PDF location is '" + rplotsPDFLocation + "'.");
      response.setRplotsPDF(new DataHandler(new FileDataSource(rplotsPDFLocation)));
      log.debug("~processSimulationResultsRequest() : PDF added?");
    } else {
      log.warn("~processSimulationResultsRequest() : Simulation with id '" + simulationId + "' not found!");
    }
    return response;
  }
}