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

/**
 * 
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import uk.ac.ox.cs.science2020.zoon.business_manager.BusinessIdentifiers;
import uk.ac.ox.cs.science2020.zoon.business_manager.business.ZOONVerify;

/**
 * Monitor the content of the VRE_OUTPUT output file.
 *
 * @author 
 */
@Component(BusinessIdentifiers.COMPONENT_VERIFICATION_VREOUTPUT_MONITOR)
@Scope("prototype")
public class VerificationVREOutputMonitor {

  @Autowired @Qualifier(BusinessIdentifiers.COMPONENT_ZOON_VERIFY)
  private ZOONVerify zoonVerify;

  private Timer daemonTimer;
  private int identifier;
  private String verificationDirectory;
  private String outputFileLocation;
  private int fileNotFoundCount = 0;
  private int readLineCount = 0;

  private static final int arbitraryMaxNumberOfTimesToFindOutputFile = 1;

  private static final Log log = LogFactory.getLog(VerificationVREOutputMonitor.class);

  VerificationVREOutputMonitor() {
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
            while (scanner.hasNextLine()) {
              final String line = scanner.nextLine();
              currReadLineCount++;
              if (currReadLineCount > readLineCount) {
                unreadLines.add(line);
              }
            }
            readLineCount = currReadLineCount;
          } finally {
            log.debug("~run() : Closing scanner.");
            scanner.close();
          }
        }
        try {
          log.debug("~run() : Closing filereader.");
          fileReader.close();
        } catch (IOException e) {
          log.warn("~run() : Exception closing FileReader '" + e.getMessage() + "'.");
          e.printStackTrace();
        }

        if (!unreadLines.isEmpty()) {
          zoonVerify.recordVREOutputLines(identifier, unreadLines);
        }

        if (zoonVerify.isProcessFinished(identifier)) {
          log.debug("~run() : Output lines appended + Verification indicates completed = Canceling the output monitor timer.");
          daemonTimer.cancel();
          zoonVerify.dumpVREOutputLines(identifier);
          zoonVerify.loadJSONOutput(identifier);
          zoonVerify.tidyUp(verificationDirectory);
        }
      }
    }
  }

  /**
   * Assign the identifier.
   * 
   * @param identifier Verification identifier. 
   */
  public void setIdentifier(final int identifier) {
    this.identifier = identifier;
  }

  public void setSchedule(final String monitorFrequency) {
    // Delay output monitoring by a second after the process monitor starts
    this.daemonTimer.schedule(new QueryTask(), VerificationProcessMonitor.PROCESS_MONITOR_DELAY + 1000,
                              new Integer(monitorFrequency) * 1000);
  }

  public void setOutputDetails(final String verificationDirectory, final String outputFileName) {
    this.verificationDirectory = verificationDirectory;
    this.outputFileLocation = verificationDirectory.concat(outputFileName);
  }
}