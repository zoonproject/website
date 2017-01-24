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
import java.util.Collections;
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
import uk.ac.ox.cs.science2020.zoon.business_manager.business.ZOONParse;

/**
 *
 *
 * @author geoff
 */
@Component(BusinessIdentifiers.COMPONENT_PARSE_FILE_MONITOR)
@Scope("prototype")
public class ParseFileMonitor {

  private Timer daemonTimer;
  private int identifier;
  private String parseFileName;
  private int fileNotFoundCount = 0;
  private List<String> linesRead = new ArrayList<String>();

  private static final String parseFileNotFound = " Parse file not found - ";  
  private static final int arbitraryMaxNumberOfTimesToFindOutputFile = 2;

  private static final Log log = LogFactory.getLog(ParseFileMonitor.class);

  @Autowired @Qualifier(BusinessIdentifiers.COMPONENT_ZOON_PARSE)
  private ZOONParse zoonParse;

  ParseFileMonitor() {
    this.daemonTimer = new Timer(true);
  }

  private class QueryTask extends TimerTask {
    public void run() {
      log.debug("~run() : Invoked. Looking for '" + parseFileName + "'");

      FileReader fileReader = null;
      try {
        fileReader = new FileReader(parseFileName);
        log.debug("~run() : Parse file '" + parseFileName + "' found");
      } catch (final FileNotFoundException fileNotFoundException) {
        fileNotFoundCount++;
        log.warn("~run() : Parse file '" + parseFileName + "' not found - attempt '" + fileNotFoundCount + "'");

        String status = "";
        if (fileNotFoundCount == arbitraryMaxNumberOfTimesToFindOutputFile) {
          status = status.concat(parseFileNotFound).concat("last warning so monitor shutting down!");
          log.debug("~run() : About to cancel the timer");
          daemonTimer.cancel();
        } else if (fileNotFoundCount < arbitraryMaxNumberOfTimesToFindOutputFile) {
          status = status.concat(parseFileNotFound).concat("warning '" + fileNotFoundCount + "' of '" + arbitraryMaxNumberOfTimesToFindOutputFile + "'");
        }
        log.debug("~run() : '" + status + "'.");
      }

      if (fileReader != null) {
        final Scanner scanner = new Scanner(fileReader);
        final List<String> unReadLines = new ArrayList<String>();

        if (scanner != null) {
          final List<String> fixedLinesRead = Collections.unmodifiableList(linesRead);
          try {
            while(scanner.hasNextLine()) {
              final String line = scanner.nextLine();
              if (!fixedLinesRead.contains(line)) {
                unReadLines.add(line);
                linesRead.add(line);
              }
            }
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
        if (!unReadLines.isEmpty()) {
          //zoonParse.recordParseFileOutputLines(identifier, unReadLines);
        } else {
          log.warn("~run() : Empty!");
        }
      }
    }
  }

  /**
   * Assign the identifier.
   * 
   * @param identifier Parse identifier. 
   */
  public void setIdentifier(final int identifier) {
    this.identifier = identifier;
  }

  /**
   * Assign the time scheduling.
   * 
   * @param monitorFrequency Monitor frequency.
   */
  public void setSchedule(final String monitorFrequency) {
    this.daemonTimer.schedule(new QueryTask(), 500, 
                              Long.valueOf(monitorFrequency));
  }

  /**
   * Assign the name of the parse file we're monitoring.
   * 
   * @param parseFileName Name of file to monitor.
   */
  public void setParseFileName(final String parseFileName) {
    this.parseFileName = parseFileName;
  }  
}