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
package uk.ac.ox.cs.science2020.zoon.business_manager.business.monitor;

import java.io.FileReader;
import java.io.FileNotFoundException;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import uk.ac.ox.cs.science2020.zoon.business_manager.BusinessIdentifiers;
import uk.ac.ox.cs.science2020.zoon.business_manager.service.SimulationService;

/**
 * Periodic checker of the native system process status.
 *
 * @author Geoff Williams
 */
@Component(BusinessIdentifiers.COMPONENT_PROCESS_MONITOR)
@Scope("prototype")
public class ProcessMonitor {

  private Timer daemonTimer;
  private int simulationId;
  private String processFileName;

  @Autowired @Qualifier(BusinessIdentifiers.COMPONENT_SIMULATION_SERVICE)
  private SimulationService simulationService;

  public static final int PROCESS_MONITOR_DELAY = 1000;

  private static final Log log = LogFactory.getLog(ProcessMonitor.class);

  ProcessMonitor() {
    this.daemonTimer = new Timer(true);
  }

  //
  private class QueryTask extends TimerTask {
    public void run() {
      log.debug("~run() : Invoked. Looking for '" + processFileName + "'");

      try {
        new FileReader(processFileName);
      } catch (final FileNotFoundException fileNotFoundException) {
        log.debug("~run() : About to cancel the timer");
        daemonTimer.cancel();

        log.debug("~run() : File not found! Assuming processing finished.");
        simulationService.setProcessFinished(simulationId);
      }
    }
  }

  /**
   * @param simulationId the simulationId to set
   */
  public void setSimulationId(int simulationId) {
    this.simulationId = simulationId;
  }

  /**
   * 
   * @param monitorFrequency
   */
  public void setSchedule(final String monitorFrequency) {
    this.daemonTimer.schedule(new QueryTask(), PROCESS_MONITOR_DELAY,
                              new Integer(monitorFrequency) * 1000);
  }

  /**
   * @param processId the processId to set
   */
  public void setProcessId(final String processId) {
    this.processFileName = "/proc/".concat(processId).concat("/status");
  }
}