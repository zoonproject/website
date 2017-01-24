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

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.vfs2.AllFileSelector;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.Selectors;
import org.apache.commons.vfs2.VFS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import uk.ac.ox.cs.science2020.zoon.business_manager.BusinessIdentifiers;
import uk.ac.ox.cs.science2020.zoon.business_manager.entity.Simulation;
import uk.ac.ox.cs.science2020.zoon.business_manager.service.SimulationService;

/**
 * Output monitor which periodically reads the file created by zoon_local_runner.sh. 
 *
 * @author Geoff Williams
 */
@Component(BusinessIdentifiers.COMPONENT_OUTPUT_MONITOR)
@Scope("prototype")
public class OutputMonitor {

  @Autowired @Qualifier(BusinessIdentifiers.COMPONENT_SIMULATION_SERVICE)
  private SimulationService simulationService;

  private Timer daemonTimer;
  private int simulationId;
  private String outputDirectory;
  private String outputFileLocation;
  private String resultsDirectory;
  private int fileNotFoundCount = 0;
  private int readLineCount = 0;
  private static final int arbitraryMaxNumberOfTimesToFindOutputFile = 5;

  private static final Log log = LogFactory.getLog(OutputMonitor.class);

  OutputMonitor() {
    this.daemonTimer = new Timer(true);
  }

  private class QueryTask extends TimerTask {
    public void run() {
      log.debug("~run() : Invoked. Looking for '" + outputFileLocation + "'");

      FileReader fileReader = null;
      try {
        fileReader = new FileReader(outputFileLocation);
        log.debug("~run() : Output file  '" + outputFileLocation + "' found");
      } catch (final FileNotFoundException fileNotFoundException) {
        fileNotFoundCount++;
        log.warn("~run() : Output file  '" + outputFileLocation + "' not found - attempt '" + fileNotFoundCount + "'");
        if (fileNotFoundCount >= arbitraryMaxNumberOfTimesToFindOutputFile) {
          log.warn("~run() : Output file  '" + outputFileLocation + "' still not found - shutting down output monitor!");
          daemonTimer.cancel();
        }
      }

      if (fileReader != null) {
        final Scanner scanner = new Scanner(fileReader);
        final List<String> unreadLines = new ArrayList<String>();

        if (scanner != null) {
          try {
            int currReadLineCount = 0;
            while(scanner.hasNextLine()) {
              final String line = scanner.nextLine();
              currReadLineCount++;
              if (currReadLineCount > readLineCount) {
                unreadLines.add(line);
              }
            }
            readLineCount = currReadLineCount;
          } finally {
            log.trace("~run() : Closing scanner.");
            scanner.close();
          }
        }
        try {
          log.trace("~run() : Closing filereader.");
          fileReader.close();
        } catch (IOException e) {
          log.warn("~run() : Exception closing FileReader '" + e.getMessage() + "'.");
          e.printStackTrace();
        }

        final Simulation simulation = simulationService.findBySimulationId(simulationId);
        boolean shutdown = simulation.hasCompleted();
        if (!unreadLines.isEmpty()) {
          shutdown = simulationService.recordOutputLines(simulationId, unreadLines);
        }
        if (shutdown) {
          log.debug("~run() : Output lines appended + Simulation indicates completed = Canceling the output monitor timer.");
          daemonTimer.cancel();

          // Copy Rplots.pdf for long-term storage and then delete the output files!
          try {
            final FileSystemManager fileSystemManager = VFS.getManager();

            // (Re)create a results directory for simulation.
            final String resultsDirName = resultsDirectory.concat(Integer.toString(simulationId));
            log.debug("~run() : Checking for '" + resultsDirName + "'.");
            final FileObject resultsFileObject = fileSystemManager.resolveFile(resultsDirName);

            if (resultsFileObject.exists()) {
              log.debug("~run() : '" + resultsDirName + "' exists. About to delete it!");
              resultsFileObject.delete(Selectors.SELECT_ALL);
            }
            log.debug("~run() : Creating '" + resultsDirName + "'.");
            resultsFileObject.createFolder();

            // Move the results PDF to the long-term storage results directory
            final String pdfFileName = outputDirectory.concat("Rplots.pdf");
            log.debug("~run() : About to copy '" + pdfFileName + "'.");
            final FileObject pdfFileObject = fileSystemManager.resolveFile(pdfFileName);
            final FileObject copiedFileObject = resultsFileObject.resolveFile("Rplots.pdf");
            copiedFileObject.copyFrom(pdfFileObject, new AllFileSelector());

            final FileObject fileObject = fileSystemManager.resolveFile(outputDirectory);
            log.debug("~run() : About to delete '" + outputDirectory + "'.");
            fileObject.delete(Selectors.SELECT_ALL);
          } catch (FileSystemException e) {
            log.warn("~run() : FS Exception during file deletion : '" + e.getMessage() + "'.");
            e.printStackTrace();
          }
        }
      }
    }
  }

  /**
   * @param simulationId the simulationId to set
   */
  public void setSimulationId(int simulationId) {
    this.simulationId = simulationId;
  }

  public void setSchedule(final String monitorFrequency) {
    // delay output monitoring by a second after the process monitor starts
    this.daemonTimer.schedule(new QueryTask(), ProcessMonitor.PROCESS_MONITOR_DELAY + 1000,
                              new Integer(monitorFrequency) * 1000);
  }

  public void setOutputDetails(final String outputDirectory, final String outputFileName,
                               final String resultsDirectory) {
    this.outputDirectory = outputDirectory;
    this.outputFileLocation = outputDirectory.concat(outputFileName);
    this.resultsDirectory = resultsDirectory;
  }
}