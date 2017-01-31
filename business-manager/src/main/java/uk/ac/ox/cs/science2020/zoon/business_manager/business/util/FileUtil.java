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
package uk.ac.ox.cs.science2020.zoon.business_manager.business.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import uk.ac.ox.cs.science2020.zoon.business_manager.BusinessIdentifiers;

/**
 * File utility class.
 *
 * @author Geoff Williams
 */
public class FileUtil {

  private static final String DELIMETER = "|";
  private static final String SPLITTER = "|".equals(DELIMETER) ? "\\|" : DELIMETER;

  /** Process information map key for app manager id - referenced in appCtx.int.processMonitoring.xml. */
  public static final String PROCESS_INFO_IDENTIFIER = "PROCESS_INFO-IDENTIFIER";
  /** Process information map key for process id - referenced in appCtx.int.processMonitoring.xml. */
  public static final String PROCESS_INFO_PROCESS_ID = "PROCESS_INFO-PROCESS_ID";

  private static final Log log = LogFactory.getLog(FileUtil.class);

  public class DirFilter implements FilenameFilter {
    private Pattern pattern;
    public DirFilter(final String regex) {
      pattern = Pattern.compile(regex);
    }
    public boolean accept(final File dir, final String name) {
      return pattern.matcher(new File(name).getName()).matches();
    }
  }

  @SuppressWarnings("rawtypes")
  public class AlphabeticComparator implements Comparator {
    public int compare(Object o1, Object o2) {
      String s1 = (String) o1;
      String s2 = (String) o2;
      return s1.toLowerCase().compareTo(s2.toLowerCase());
    }
  }

  /**
   * Delete a file (or recursively delete a directory).
   * 
   * @param file File object pointing to a file or directory.
   */
  public static void delete(final File file) {
    if (file == null) return;
    final String fileName = file.getName();
    log.debug("~delete() : Incoming '" + fileName + "'.");

    if (file.exists()) {
      if (file.isDirectory()) {
        for (final File listedFile : file.listFiles()) {
          final String listedFileName = listedFile.getName();
          if (listedFile.isDirectory()) {
            delete(listedFile);
          } else {
            log.debug("~delete() : Delete file '" + listedFileName + "'.");
            if (!listedFile.delete()) {
              log.warn("~delete() : Could not delete '" + listedFileName + "'.");
            }
          }
        }
      }
      //log.debug("~delete() : canRead '" + file.canRead() + "'.");
      //log.debug("~delete() : canWrite '" + file.canWrite() + "'.");
      //log.debug("~delete() : canExecute '" + file.canExecute() + "'.");
      if (!file.delete()) {
        log.warn("~delete() : Could not delete '" + fileName + "'.");
      }
    }
  }

  /**
   * Retrieve the App manager job directory.
   * 
   * @param baseDir Base directory.
   * @param identifier App Manager identifier.
   * @return Constructed job directory.
   */
  public static String retrieveAppManagerDir(final String baseDir, final long identifier) {
    return FileUtil.retrieveJobDirectory(baseDir, identifier);
  }

  @SuppressWarnings("unchecked")
  public static String[] retrieveFiles(final DirFilter dirFilter,
                                       final AlphabeticComparator alphabeticComparator,
                                       final String directory, final boolean ordered) {
    log.debug("~retrieveFiles(..) : Determine files to retrieve");
    final File dir = new File(directory);
    if (!dir.isDirectory()) {
      final String errorMessage = "Directory '" + directory.toString() + "' isn't a directory!!";
      log.warn("~retrieveFiles(..) : " + errorMessage);
      throw new UnsupportedOperationException(errorMessage);
    }

    final String[] listing = dir.list(dirFilter);
    if (ordered) Arrays.sort(listing, alphabeticComparator);

    return listing;
  }

  /**
   * Retrieve the content of the file as a String.
   * 
   * @param filePath
   * @return
   */
  public static String retrieveFileContent(final String filePath) {
    log.debug("~retrieveFileContent() : Invoked for file '" + filePath + "'.");
    String fileContent = null;

    try {
      fileContent = FileUtils.readFileToString(new File(filePath));
    } catch (IOException e) {
      final String errorMessage = "Error reading file '" + filePath + "' : '" + e.getMessage() + "'.";
      log.error("~retrieveFileContent() : " + errorMessage);
    }

    return fileContent;
  }

  /**
   * 
   * @param baseDir
   * @param identifier
   * @return Job directory (with trailing slash appended).
   */
  public static String retrieveJobDirectory(final String baseDir, final long identifier) {
    log.trace("~retrieveJobDirectory(String, String) : From '" + baseDir + "', '" + identifier + "'");

    final StringBuffer jobDirectory = new StringBuffer();
    jobDirectory.append(baseDir);
    jobDirectory.append(identifier);
    jobDirectory.append(BusinessIdentifiers.FILE_SEPARATOR);

    final String path = jobDirectory.toString();
    log.trace("~retrieveJobDirectory(String, String) : Job directory is '" + path + "'");

    return path;
  }

  public static Map<String, String> retrieveProcessInfo(final File file) {
    Scanner scanner = null;
    try {
      scanner = new Scanner(file);
    } catch (FileNotFoundException fileNotFoundException) {
      final String errorMessage = "Failed to read file '" + file.toString() + "' - error '" + fileNotFoundException.getMessage() + "'";
      log.fatal("~retrieveProcessInfo() : " + errorMessage);
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
      log.debug("~retrieveProcessInfo() : Contents '" + procFileContents + "'");
    } finally {
      scanner.close();
    }

    // Process the contents of the process data file.
    final Map<String, String> processInfo = new HashMap<String, String>(2);
    if (procFileContents != null) {
      String identifier = null;
      String processId = null;

      if (procFileContents.contains(DELIMETER)) {
        if (procFileContents.startsWith(DELIMETER)) {
          log.error("~retrieveProcessInfo() : Cannot determine Identifier from process data file content.");
          processId = procFileContents.split(SPLITTER)[1];
        } else if (procFileContents.endsWith(DELIMETER)) {
          log.error("~retrieveProcessInfo() : Cannot determine ProcessId from process data file content.");
          identifier = procFileContents.split(SPLITTER)[0];
        } else {
          final String[] procData = procFileContents.split(SPLITTER);
          identifier = procData[0];
          processId = procData[1];
        }
      } else {
        log.error("~retrieveProcessInfo() : Cannot determine Identifier or ProcessId from process data file content.");
      }

      if (identifier != null)
        processInfo.put(PROCESS_INFO_IDENTIFIER, identifier);
      if (processId != null)
        processInfo.put(PROCESS_INFO_PROCESS_ID, processId);
    }

    /*
     * Determine if overall success and persist the status (if there's enough information to do so). 
     */
    if (processInfo.containsKey(PROCESS_INFO_IDENTIFIER) && 
        processInfo.containsKey(PROCESS_INFO_PROCESS_ID)) {
      log.debug("~retrieveProcessInfo() : Success! Identifier and process Id found.");
    } else {
      log.warn("~retrieveProcessInfo() : Fail! Identifier and/or process Id not found.");
      processInfo.clear();
    }

    return processInfo;
  }
}