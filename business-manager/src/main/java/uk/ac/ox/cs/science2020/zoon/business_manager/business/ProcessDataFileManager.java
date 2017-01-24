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
package uk.ac.ox.cs.science2020.zoon.business_manager.business;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.Selectors;
import org.apache.commons.vfs2.VFS;
import org.springframework.integration.annotation.Payload;
import org.springframework.integration.annotation.ServiceActivator;

// import uk.ac.ox.cs.science2020.zoon.business_manager.BusinessIdentifiers;
// import uk.ac.ox.cs.science2020.zoon.business_manager.entity.RunStatus;

/**
 * Process data filesystem file manager.
 *
 * @author Geoff Williams
 */
public class ProcessDataFileManager {

  //@Autowired @Qualifier(BusinessIdentifiers.COMPONENT_APP_MANAGER_DAO)
  //private AppManagerDAO appManagerDAO;

  private static final String DELIMETER = "|";
  private static final String SPLITTER = "|".equals(DELIMETER) ? "\\|" : DELIMETER;

  /** Process information map key for app manager id - referenced in appCtx.int.processMonitoring.xml. */
  public static final String PROCESS_INFO_APP_MANAGER_ID = "PROCESS_INFO-APP_MANAGER_ID";
  /** Process information map key for process id - referenced in appCtx.int.processMonitoring.xml. */
  public static final String PROCESS_INFO_PROCESS_ID = "PROCESS_INFO-PROCESS_ID";

  private static final Log log = LogFactory.getLog(ProcessDataFileManager.class);

  /**
   * Handles the reading of filesystem file contents and processing the data therein before 
   * deleting the file.
   * 
   * @param file Process data filesystem file.
   * @return Internal representation of process data file contents.
   */
  @ServiceActivator
  public Map<String, String> manageProcessDataFile(@Payload File file) {
    log.debug("~int.manageProcessDataFile() : Invoked.");
    log.trace("~int.manageProcessDataFile() : Thread '" + Thread.currentThread().getName() + "'.");

    String fileCanonicalPath = null;
    try {
      fileCanonicalPath = file.getCanonicalPath();
      log.debug("~int.manageProcessDataFile() : File canonical path '" + fileCanonicalPath + "'");
    } catch (IOException e) {
      final String errorMessage = "IO Exception retrieving file canonical path : '" + e.getMessage() + "'";
      log.warn("~int.manageProcessDataFile() : " + errorMessage);
      e.printStackTrace();
      throw new UnsupportedOperationException(errorMessage);
    }

    /*
     * Read in the process data file contents
     */
    Scanner scanner = null;
    try {
      scanner = new Scanner(file);
    } catch (FileNotFoundException fileNotFoundException) {
      final String errorMessage = "Failed to read file '" + file.toString() + "' - error '" + fileNotFoundException.getMessage() + "'";
      log.fatal("~int.manageProcessDataFile() : " + errorMessage);
      fileNotFoundException.printStackTrace();
      throw new UnsupportedOperationException(errorMessage);
    }

    final StringBuilder fileContents = new StringBuilder((int) file.length());
    String procFileContents = null;
    try {
      while(scanner.hasNextLine()) {
        fileContents.append(scanner.nextLine());
      }
      procFileContents = fileContents.toString();
      log.debug("~int.manageProcessDataFile() : Contents '" + procFileContents + "'");
    } finally {
      scanner.close();
    }

    /*
     * Process the contents of the process data file then delete the file.
     */
    final Map<String, String> processInfo = new HashMap<String, String>(2);
    if (procFileContents != null) {
      String appManagerId = null;
      String processId = null;

      if (procFileContents.contains(DELIMETER)) {
        if (procFileContents.startsWith(DELIMETER)) {
          log.error("~int.manageProcessDataFile() : Cannot determine AppManagerId from process data file content.");
          processId = procFileContents.split(SPLITTER)[1];
        } else if (procFileContents.endsWith(DELIMETER)) {
          log.error("~int.manageProcessDataFile() : Cannot determine ProcessId from process data file content.");
          appManagerId = procFileContents.split(SPLITTER)[0];
        } else {
          final String[] procData = procFileContents.split(SPLITTER);
          appManagerId = procData[0];
          processId = procData[1];
        }
      } else {
        log.error("~int.manageProcessDataFile() : Cannot determine AppManagerId or ProcessId from process data file content.");
      }

      if (appManagerId != null)
        processInfo.put(PROCESS_INFO_APP_MANAGER_ID, appManagerId);
      if (processId != null)
        processInfo.put(PROCESS_INFO_PROCESS_ID, processId);

      log.debug("~int.manageProcessDataFile() : Deleting process data file");
      // delete the file as it's no longer required
      try {
        final FileSystemManager fileSystemManager = VFS.getManager();
        final FileObject fileObject = fileSystemManager.resolveFile(fileCanonicalPath);
        fileObject.delete(Selectors.SELECT_SELF);
      } catch (FileSystemException e) {
        log.warn("~int.manageProcessDataFile() : FS Exception during process data file deletion : '" + e.getMessage() + "'");
        e.printStackTrace();
      }
    }

    /*
     * Determine if overall success and persist the status (if there's enough information to do so). 
     */
    boolean processingFailure = false;
    if (processInfo.containsKey(PROCESS_INFO_APP_MANAGER_ID)) {
      final long appManagerId = new Long(processInfo.get(PROCESS_INFO_APP_MANAGER_ID));

      String statusMessage = null;
      String level = null;

      final String processId = processInfo.get(PROCESS_INFO_PROCESS_ID); 
      if (processId == null) {
        statusMessage = "No process id was found in process data file - simulation failure!";
        processingFailure = true;
        //level = RunStatus.ERROR_PREFIX;
      } else {
        //appManagerDAO.saveSimulationProcessId(appManagerId, processId);

        statusMessage = "Process data file read and processed successfully";
        //level = RunStatus.DEBUG_PREFIX;
      }
      //final RunStatus runStatus = new RunStatus(appManagerId, null, level, statusMessage);
      //appManagerDAO.addStatus(runStatus);
    } else {
      // can't even persist a status update
      processingFailure = true;
    }

    if (processingFailure) {
      /*
       * TODO : Improve failure recovery procedure, e.g...
       *        1) Return VRE_INFO or VRE_OUTPUT?!
       *        2) Remove ~/vre_run/<port>/<app manager id>
       */
      final String errorMessage = "Simulation processing failure problem";
      log.error("~int.manageProcessDataFile() : " + errorMessage);
    }

    return processInfo;
  }
}