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

import java.util.Date;
import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import uk.ac.ox.cs.science2020.zoon.business_manager.BusinessIdentifiers;
import uk.ac.ox.cs.science2020.zoon.business_manager.entity.Simulation;
import uk.ac.ox.cs.science2020.zoon.business_manager.service.SimulationService;
import uk.ac.ox.cs.science2020.zoon.business_manager.ws.schema.jaxb.ObjectFactory;
import uk.ac.ox.cs.science2020.zoon.business_manager.ws.schema.jaxb.SimulationNonResultsRequest;
import uk.ac.ox.cs.science2020.zoon.business_manager.ws.schema.jaxb.SimulationNonResultsResponse;
import uk.ac.ox.cs.science2020.zoon.business_manager.ws.schema.jaxb.SimulationNonResultsResponseStructure;
import uk.ac.ox.cs.science2020.zoon.business_manager.ws.schema.jaxb.SimulationStructure;

/**
 * 
 *
 * @author geoff
 */
public class SimulationNonResultsRequestProcessor {

  @Autowired @Qualifier(BusinessIdentifiers.COMPONENT_SIMULATION_SERVICE)
  private SimulationService simulationService;

  // Makes web service JAXB objects available.
  private static final ObjectFactory objectFactory = new ObjectFactory();

  private static final Log log = LogFactory.getLog(SimulationNonResultsRequestProcessor.class);

  /** Default constructor */
  protected SimulationNonResultsRequestProcessor() {}

  public SimulationNonResultsResponse processSimulationNonResultsRequest(final SimulationNonResultsRequest request) {
    log.debug("~processSimulationNonResultsRequest() : Invoked.");

    final long simulationId = request.getSimulationId();
    log.debug("~processSimulationNonResultsRequest() : Invoked for '" + simulationId + "'.");
    
    final SimulationNonResultsResponse response = objectFactory.createSimulationNonResultsResponse();

    final Simulation simulation = simulationService.findBySimulationId(simulationId);
    if (simulation != null) {
      final SimulationStructure simulationStructure = objectFactory.createSimulationStructure();
      simulationStructure.setCompleted(retrieveXMLGregorianCalendar(simulation.getCompleted()));
      simulationStructure.setId(simulation.getId());
      simulationStructure.setOutput(simulation.getOutput());
      simulationStructure.setParameter(simulation.getParameter());
      simulationStructure.setPersisted(retrieveXMLGregorianCalendar(simulation.getPersisted()));
      simulationStructure.setRequestProcessed(simulation.isRequestProcessed());

      final SimulationNonResultsResponseStructure responseStructure = objectFactory.createSimulationNonResultsResponseStructure();
      responseStructure.setSimulationStructure(simulationStructure);
      response.setSimulationNonResultsResponseStructure(responseStructure);
    } else {
      log.warn("~processSimuluationNonResultsRequest() : Simulation with id '" + simulationId + "' not found!");
    }
    return response;
  }

  private XMLGregorianCalendar retrieveXMLGregorianCalendar(final Date date) {
    XMLGregorianCalendar xmlDate = null;
    if (date != null) {
      final GregorianCalendar gcDate = new GregorianCalendar();
      gcDate.setTime(date);
      try {
        xmlDate = DatatypeFactory.newInstance().newXMLGregorianCalendar(gcDate);
      } catch (DatatypeConfigurationException e) {
        log.error("~retrieveXMLGregorianCalendar() : Exception '" + e.getMessage() + "'.");
        e.printStackTrace();
        throw new UnsupportedOperationException(e.getMessage());
      }
    }
    return xmlDate;
  }
}