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

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.stereotype.Component;

import uk.ac.ox.cs.science2020.zoon.business_manager.BusinessIdentifiers;
import uk.ac.ox.cs.science2020.zoon.business_manager.business.ProcessDataFileManager;
import uk.ac.ox.cs.science2020.zoon.business_manager.business.util.FileUtil;

/**
 * Kick off the monitoring of system processes.
 *
 * @author Geoff Williams
 */
@Component
public class OutputMonitorInvoker implements ApplicationContextAware {

  private ApplicationContext applicationContext;

  // Spring-injected.
  @Value("${base.dir}")
  private String baseDir;

  @Value("${results.dir}")
  private String resultsDir;

  @Value("${monitor_output.frequency}")
  private String monitorOutputFrequency;

  private static final int arbitraryMinimumOutputDirectoryLocationLength = 10;
  private static final Log log = LogFactory.getLog(OutputMonitorInvoker.class);

  /**
   * Kick off the monitoring of the system process output.
   * 
   * @param processData Internal representation of process data file contents.
   */
  @ServiceActivator
  public void activateOutputMonitor(final Map<String, String> processData) {
    log.debug("~activateOutputMonitor() : Invoked.");

    final String dataSimulationId = processData.get(ProcessDataFileManager.PROCESS_INFO_APP_MANAGER_ID);
    if (dataSimulationId == null || baseDir == null || 
        baseDir.length() < arbitraryMinimumOutputDirectoryLocationLength) {
      final String errorMessage = "Output monitor has been assigned illegal values!!!";
      log.error("~activateOutputMonitor() : " + errorMessage); 
      throw new RuntimeException(errorMessage);
    }
    final int simulationId = new Integer(dataSimulationId).intValue();

    final String outputDirectory = FileUtil.retrieveJobDirectory(baseDir, simulationId);

    final OutputMonitor outputMonitor = this.applicationContext.getBean(BusinessIdentifiers.COMPONENT_OUTPUT_MONITOR,
                                                                        OutputMonitor.class);
    final String outputFileName = "VRE_OUTPUT.".concat(dataSimulationId);
    log.debug("~activateOutputMonitor() : Output directory is '" + outputDirectory + "'.");
    log.debug("~activateOutputMonitor() : Output file name is '" + outputFileName + "'.");
    outputMonitor.setOutputDetails(outputDirectory, outputFileName, resultsDir);
    outputMonitor.setSchedule(monitorOutputFrequency);
    outputMonitor.setSimulationId(simulationId);
  }

  /* (non-Javadoc)
   * @see org.springframework.context.ApplicationContextAware#setApplicationContext(org.springframework.context.ApplicationContext)
   */
  public void setApplicationContext(final ApplicationContext applicationContext)
                                    throws BeansException {
    this.applicationContext = applicationContext;
  }
}