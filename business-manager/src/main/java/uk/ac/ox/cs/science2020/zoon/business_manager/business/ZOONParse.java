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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.integration.annotation.Payload;

import uk.ac.ox.cs.science2020.zoon.business_manager.BusinessIdentifiers;
//import uk.ac.ox.cs.science2020.zoon.business_manager.business.monitor.ParseFileMonitor;
import uk.ac.ox.cs.science2020.zoon.business_manager.business.monitor.ParseProcessMonitor;
import uk.ac.ox.cs.science2020.zoon.business_manager.business.monitor.ParseVREOutputMonitor;
import uk.ac.ox.cs.science2020.zoon.business_manager.business.util.FileUtil;

/**
 *
 *
 * @author geoff
 */
public class ZOONParse implements ApplicationContextAware {

  //public static final String PARSE_SCRIPT_OUTPUT_FILENAME = "VO.txt";
  public static final String PARSE_SCRIPT_OUTPUT_JSON = "VO.json";
  private static final String vreOutputFilenamePrefix = "ZOON_OUTPUT."; 

  private static final Map<Integer, Boolean> systemsProcessState = new ConcurrentHashMap<Integer, Boolean>();
  // Parse information written to file
  //private static final Map<Integer, List<String>> jobOutputFileLines = new HashMap<Integer, List<String>>();
  // Parse information written to stdout
  private static final Map<Integer, List<String>> jobVREOutputLines = new HashMap<Integer, List<String>>();
  // There's only one line of JSON text in the output file.
  private static final Map<Integer, String> jobJSONLines = new HashMap<Integer, String>();

  private ApplicationContext applicationContext;

  @Value("${monitor_parse_output.frequency}")
  private String monitorOutputFrequency;

  @Value("${monitor_parse_process.frequency}")
  private String monitorProcessFrequency;

  // Spring-injected.
  @Value("${parse.dir}")
  private String baseParseDir;

  @Value("${parse.prepare.sh}")
  private String parsePrepareSh;

  @Value("${parse.runner.sh}")
  private String parseRunnerSh;

  // e.g. /home/me/R/initiate_check.R
  @Value("${parse.R}")
  private String parseR;

  private static final AtomicInteger count = new AtomicInteger(0);

  private static final Log log = LogFactory.getLog(ZOONParse.class);

  @PostConstruct
  protected void postConstruct() {
    if (StringUtils.isBlank(parseR)) {
      throw new IllegalStateException("No module parse R script in spring.properties!");
    }
    if (StringUtils.isBlank(baseParseDir)) {
      throw new IllegalStateException("No parse directory specified in spring.properties!");
    }
    if (StringUtils.isBlank(parsePrepareSh)) {
      throw new IllegalStateException("No parse prepare script specified in spring.properties!");
    }
    if (StringUtils.isBlank(parseRunnerSh)) {
      throw new IllegalStateException("No parse runner script specified in spring.properties!");
    }

    log.info("~postConstruct() : Parse module R script set to '" + parseR + "'.");
    log.info("~postConstruct() : Base Parse directory set to '" + baseParseDir + "'.");
    log.info("~postConstruct() : Parse prepare script set to '" + parsePrepareSh + "'.");
    log.info("~postConstruct() : Parse runner script set to '" + parseRunnerSh + "'.");
  }

  /**
   * Assign the parsing systems process as finished.
   * 
   * @param parseidentifier Parse identifier.
   * @see ParseProcessMonitor
   */
  public void assignSystemsProcessFinished(final int parseIdentifier) {
    log.debug("~assignSystemsProcessFinished() : [" + parseIdentifier + "] : Invoked.");
    log.debug("~assignSystemsProcessFinished() : Before '" + systemsProcessState.toString() + "'.");

    systemsProcessState.put(Integer.valueOf(parseIdentifier), Boolean.TRUE);

    log.debug("~assignSystemsProcessFinished() : After '" + systemsProcessState.toString() + "'.");
}

  /**
   * Remove the data from the static vars after use.
   * 
   * @param parseIdentifier Parse identifier.
   */
  public void cleanUpStatics(final int parseIdentifier) {
    log.debug("~cleanUpStatics() : [" + parseIdentifier + "] : Invoked.");
    log.debug("~cleanUpStatics() : Before [" + parseIdentifier + "] : '" + systemsProcessState.toString() + "'.");

    final Integer intPI = Integer.valueOf(parseIdentifier);
    jobJSONLines.remove(intPI);
    jobVREOutputLines.remove(intPI);
    systemsProcessState.remove(intPI);

    log.debug("~cleanUpStatics() : After [" + parseIdentifier + "] : '" + systemsProcessState.toString() + "'.");
  }

  /**
   * Dump output lines to log file.
   * 
   * @param parseIdentifier Parse identifier.
   */
  public void dumpVREOutputLines(final int parseIdentifier) {
    log.debug("~dumpOutputLines() : [" + parseIdentifier + "] : Invoked.");

    log.debug("~dumpOutputLines() : '" + StringUtils.join(jobVREOutputLines.get(Integer.valueOf(parseIdentifier)), "\n").toString() + "'.");
  }

  private boolean invokeTomsRParseScript(final String temporaryDirectory, final String artifactName,
                                         final int parseIdentifier) {
    log.debug("~invokeTomsRParseScript() : [" + parseIdentifier + "] : Invoked.");

    final StringBuffer commandLine = new StringBuffer();
    commandLine.append("./Rscript.sh").append(" ").append(parseR).append(" ").append(artifactName) 
               //.append(" ").append(temporaryDirectory).append(PARSE_SCRIPT_OUTPUT_FILENAME)
               .append(" ").append(temporaryDirectory).append(PARSE_SCRIPT_OUTPUT_JSON);

    final String fullCommandLine = commandLine.toString();

    log.debug("~invokeTomsRParseScript() : Command line '" + fullCommandLine + "'");

    final String[] localRunnerArgs = new String[4];

    localRunnerArgs[0] = parseRunnerSh;
    localRunnerArgs[1] = temporaryDirectory;
    localRunnerArgs[2] = String.valueOf(parseIdentifier);
    localRunnerArgs[3] = fullCommandLine;

    final String invocation = StringUtils.join(localRunnerArgs, " ");

    boolean invoked = false;
    log.debug("~invokeTomsRParseScript() : Args '" + invocation + "'.");
    final Runtime runtime = Runtime.getRuntime();
    try {
      // This invokes the parse runner which writes the parse identifier and process id to zoon_procdir/parse-<id>
      runtime.exec(localRunnerArgs);
      invoked = true;
    } catch (IOException ioe) {
      final String errorMessage = "Invocation of local parse runner failed with message '" + ioe.getMessage() + "'";
      log.error("~invokeTomsRParseScript(String) : " + errorMessage);
    }
    return invoked;
  }

  /**
   * Indicator to show if the identified process has finished by checking against the parse state.
   * <p>
   * Note that this indicates if the systems process has stopped, it does not indicate that all
   * generated files have been read in!
   * 
   * @param parseIdentifier Parse identifier.
   * @return {@code true} if the process is finished, otherwise {@code false}.
   */
  public boolean isSystemsProcessFinished(final int parseIdentifier) {
    log.debug("~isSystemsProcessFinished() : [" + parseIdentifier + "] : Invoked.");
    log.debug("~isSystemsProcessFinished() : Before [" + parseIdentifier + "] : '" + systemsProcessState.toString() + "'.");

    final Integer intPI = Integer.valueOf(parseIdentifier);
    if (!systemsProcessState.containsKey(intPI)) {
      throw new UnsupportedOperationException("Job statuses doesn't contain the identifier '" + parseIdentifier + "'.");
    }

    log.debug("~isSystemsProcessFinished() : After [" + parseIdentifier + "] : '" + systemsProcessState.toString() + "'.");
    final Boolean systemsProcessFinished = systemsProcessState.get(intPI);
    log.debug("~isSystemsProcessFinished() : Systems process is finished '" + systemsProcessFinished + "'.");
    return systemsProcessFinished;
  }

  /**
   * Read the JSON output file and store content.
   * 
   * @param parseIdentifier Parsing identifier.
   */
  public void loadJSONOutput(final int parseIdentifier) {
    final Integer intPI = Integer.valueOf(parseIdentifier);
    assert (jobJSONLines.containsKey(intPI)) : "Can't store JSON output if no Map entry!";
    log.debug("~loadJSONOutput() : [" + parseIdentifier + "] : Invoked.");

    final String temporaryDirectory = FileUtil.retrieveJobDirectory(baseParseDir, parseIdentifier);
    final String filePath = temporaryDirectory.concat(PARSE_SCRIPT_OUTPUT_JSON);
    log.debug("~loadJSONOutput() : Looking for file '" + filePath + "'.");
    final String fileContent = FileUtil.retrieveFileContent(filePath);
    if (fileContent != null) {
      log.debug("~loadJSONOutput() : Loaded '" + fileContent + "'.");
      jobJSONLines.put(intPI, fileContent);
    }
  }

  /**
   * Parse file containing process count number and process id.
   * 
   * @param file File containing details.
   */
  public void manageParseFile(@Payload File file) {
    log.debug("~manageParseFile() : Invoked.");

    final Map<String, String> processInfo = FileUtil.retrieveProcessInfo(file);
    FileUtil.delete(file);

    if (!processInfo.isEmpty()) {
      final int parseIdentifier = Integer.valueOf(processInfo.get(FileUtil.PROCESS_INFO_IDENTIFIER));
      log.debug("~manageParseFile() : [" + parseIdentifier + "] : Process info read in from file.");
      final String processId = processInfo.get(FileUtil.PROCESS_INFO_PROCESS_ID);

      // This monitors the system process data.
      monitorParseProcess(processId, parseIdentifier);
      // This monitors whatever gets sent to stdout/stderr.
      monitorParseVREOutput(parseIdentifier);
      // This monitors whatever gets written to the parse R script output.
      //monitorParseOutputFile(parseIdentifier);
    }
  }

  private void monitorParseVREOutput(final int parseIdentifier) {
    log.debug("~monitorParseVREOutput() : Invoked for identifier '" + parseIdentifier + "'.");

    final String temporaryDirectory = FileUtil.retrieveJobDirectory(baseParseDir, parseIdentifier);

    final ParseVREOutputMonitor parseVREOutputMonitor = this.applicationContext.getBean(BusinessIdentifiers.COMPONENT_PARSE_VREOUTPUT_MONITOR,
                                                                                        ParseVREOutputMonitor.class);
    final String outputFileName = vreOutputFilenamePrefix + parseIdentifier;
    log.debug("~monitorParseVREOutput() : Temporary parse directory is '" + temporaryDirectory + "'.");
    log.debug("~monitorParseVREOutput() : Output file name is '" + outputFileName + "'.");
    parseVREOutputMonitor.setOutputDetails(temporaryDirectory, outputFileName);
    parseVREOutputMonitor.setSchedule(monitorOutputFrequency);
    parseVREOutputMonitor.setIdentifier(parseIdentifier);
  }

  /*
  private void monitorParseOutputFile(final int parseIdentifier) {
    log.debug("~monitorParseOutputFile() : Invoked for identifier '" + parseIdentifier + "'.");

    final String temporaryDirectory = FileUtil.retrieveJobDirectory(baseParseDir, parseIdentifier);

    final ParseFileMonitor parseFileMonitor = this.applicationContext.getBean(BusinessIdentifiers.COMPONENT_PARSE_FILE_MONITOR,
                                                                              ParseFileMonitor.class);
    parseFileMonitor.setIdentifier(parseIdentifier);
    parseFileMonitor.setSchedule(monitorOutputFrequency);
    parseFileMonitor.setParseFileName(temporaryDirectory.concat(PARSE_SCRIPT_OUTPUT_FILENAME));
  }
  */

  private void monitorParseProcess(final String processId, final int parseIdentifier) {
    log.debug("~monitorParseProcess() : Invoked for process '" + processId + "', identifier '" + parseIdentifier + "'.");

    final ParseProcessMonitor parseProcessMonitor = this.applicationContext.getBean(BusinessIdentifiers.COMPONENT_PARSE_PROCESS_MONITOR,
                                                                                    ParseProcessMonitor.class);
    parseProcessMonitor.setProcessId(processId);
    parseProcessMonitor.setIdentifier(parseIdentifier);
    parseProcessMonitor.setSchedule(monitorProcessFrequency);
  }

  /**
   * Parse the module.
   * 
   * @param name Name of module.
   * @param module Module to parse.
   */
  public Integer parseModule(final String name, final String module) {
    log.debug("~parseModule() : Invoked.");
    log.debug("~parseModule() : Before '" + systemsProcessState.toString() + "'.");

    final int identifier = count.incrementAndGet();

    // e.g. ~/zoon/parse/1/
    final String temporaryDirectory = FileUtil.retrieveJobDirectory(baseParseDir, identifier);

    // Copy stuff to ~/zoon/parse/1/
    prepareParseDirectoryContent(temporaryDirectory);

    // Write artifact to directory ~/zoon/parse/1/
    final boolean written = writeArtifactToDirectory(temporaryDirectory, name, module);

    boolean invoked = false;
    if (written) {
      // Run stuff in ~/zoon/parse/1/
      invoked = invokeTomsRParseScript(temporaryDirectory, name, identifier);

      final Integer intPI = Integer.valueOf(identifier);
      systemsProcessState.put(intPI, Boolean.FALSE);
      jobVREOutputLines.put(intPI, new ArrayList<String>());
      //jobOutputFileLines.put(identifier, new ArrayList<String>());
      jobJSONLines.put(intPI, null);
    }

    log.debug("~parseModule() : Tom's script was" + (invoked ? "" : " not") + " invoked.");

    return identifier;
  }

  private void prepareParseDirectoryContent(final String temporaryDirectory) {
    log.debug("~prepareParseDirectoryContent() : Invoked for '" + temporaryDirectory + "'.");

    final Runtime runtime = Runtime.getRuntime();
    final String[] preparerArgs = new String[2];
    preparerArgs[0] = parsePrepareSh;
    preparerArgs[1] = temporaryDirectory;

    try {
      final Process process = runtime.exec(preparerArgs);
      process.waitFor();
    } catch (IOException e) {
      log.error("~prepareParseDirectoryContent() : IOException '" + e.getMessage() + "'.");
      e.printStackTrace();
    } catch (InterruptedException e) {
      log.error("~prepareParseDirectoryContent() : InterruptedException '" + e.getMessage() + "'.");
      e.printStackTrace();
    }
  }

  /**
   * Record the file output generated from a module parse process.
   * 
   * @param parseIdentifier Parse identifier.
   * @param unreadLines Newly available file output lines.
  public void recordParseFileOutputLines(final int parseIdentifier, final List<String> unreadLines) {
    log.debug("~recordParseFileOutputLines() : '" + parseIdentifier + "', '" + unreadLines.toString() + "'.");

    jobOutputFileLines.get(parseIdentifier).addAll(unreadLines);
  }
   */

  /**
   * Record the VRE_OUTPUT generated from a module parsing process.
   * 
   * @param parseIdentifier Parsing identifier.
   * @param unreadLines Newly available output lines.
   */
  public void recordVREOutputLines(final int parseIdentifier, final List<String> unreadLines) {
    jobVREOutputLines.get(parseIdentifier).addAll(unreadLines);
  }

  /**
   * Retrieve the file output lines for the module parsing process.
   * 
   * @param parseIdentifier Parse identifier.
   * @return File output lines, or {@code null}/empty collection if none available.
  public List<String> retrieveFileOutputLines(final int parseIdentifier) {
    log.debug("~retrieveFileOutputLines() : Invoked for '" + parseIdentifier + "'.");

    return jobOutputFileLines.get(parseIdentifier);
  }
   */

  /**
   * Retrieve the output lines for the module parsing process.
   * 
   * @param parseIdentifier Parse identifier.
   * @return Output lines, or {@code null}/empty collection if none available.
   */
  public List<String> retrieveJobVREOutputLines(final int parseIdentifier) {
    log.debug("~retrieveJobVREOutputLines() : Invoked for '" + parseIdentifier + "'.");

    return jobVREOutputLines.get(Integer.valueOf(parseIdentifier));
  }

  /**
   * Retrieve JSON output.
   * 
   * @param parseIdentifier
   * @return JSON output, or {@code null} if not available.
   */
  public String retrieveJSONOutputLine(final int parseIdentifier) {
    log.debug("~retrieveJSONOutputLine() : Invoked for '" + parseIdentifier + "'.");

    return jobJSONLines.get(parseIdentifier);
  }

  /**
   * Remove the temporary parse structure.
   * 
   * @param temporaryDirectory Path of directory which is used as the temporary processing.
   */
  public void tidyUp(final String temporaryDirectory) {
    log.debug("~tidyUp() : Removing '" + temporaryDirectory + "'.");

    FileUtil.delete(new File(temporaryDirectory));
  }

  private boolean writeArtifactToDirectory(final String temporaryDirectory, final String artifactName,
                                           final String artifact) {
    log.debug("~writeArtifactToDirectory() : Invoked.");

    final File file = new File(temporaryDirectory.concat(artifactName));

    boolean writeSuccess = false;

    BufferedWriter bw = null;
    try {
      if (!file.exists()) {
        log.debug("~writeArtifactToDirectory() : Creating new file.");
        file.createNewFile();
      }

      final FileWriter fw = new FileWriter(file.getAbsoluteFile());
      bw = new BufferedWriter(fw);
      bw.write(artifact);
      writeSuccess = true;
    } catch (IOException e) {
      log.error("~writeArtifactToDirectory() : Error creating new file '" + e.getMessage() + "'.");
      e.printStackTrace();
    } finally {
      if (bw != null) {
        try {
          bw.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }

    return writeSuccess;
  }

  /* (non-Javadoc)
   * @see org.springframework.context.ApplicationContextAware#setApplicationContext(org.springframework.context.ApplicationContext)
   */
  @Override
  public void setApplicationContext(final ApplicationContext applicationContext)
                                    throws BeansException {
    this.applicationContext = applicationContext;
  }
}