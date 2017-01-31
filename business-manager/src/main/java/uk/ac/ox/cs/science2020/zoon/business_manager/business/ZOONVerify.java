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
import uk.ac.ox.cs.science2020.zoon.business_manager.business.monitor.VerificationFileMonitor;
import uk.ac.ox.cs.science2020.zoon.business_manager.business.monitor.VerificationVREOutputMonitor;
import uk.ac.ox.cs.science2020.zoon.business_manager.business.monitor.VerificationProcessMonitor;
import uk.ac.ox.cs.science2020.zoon.business_manager.business.util.FileUtil;
import uk.ac.ox.cs.science2020.zoon.business_manager.value.object.artifact.MinimumArtifactDataVO;

/**
 *
 *
 * @author geoff
 */
public class ZOONVerify implements ApplicationContextAware {

  public static final String VERIFICATION_SCRIPT_OUTPUT_FILENAME = "VO.txt";
  public static final String VERIFICATION_SCRIPT_OUTPUT_JSON = "VO.json";
  private static final String vreOutputFilenamePrefix = "ZOON_OUTPUT."; 

  private static final Map<Integer, Boolean> verificationState = new HashMap<Integer, Boolean>();
  // Validation information written to file
  private static final Map<Integer, List<String>> jobOutputFileLines = new HashMap<Integer, List<String>>();
  // Validation information written to stdout
  private static final Map<Integer, List<String>> jobVREOutputLines = new HashMap<Integer, List<String>>();
  // There's only one line of JSON text in the output file.
  private static final Map<Integer, String> jobJSONLines = new HashMap<Integer, String>();
  private static final Map<Integer, MinimumArtifactDataVO> minimumArtifactDatas = new HashMap<Integer, MinimumArtifactDataVO>();

  private ApplicationContext applicationContext;

  @Value("${monitor_output.frequency}")
  private String monitorOutputFrequency;

  @Value("${monitor_process.frequency}")
  private String monitorProcessFrequency;

  // Spring-injected.
  @Value("${verify.dir}")
  private String baseVerifyDir;

  @Value("${verify.prepare.sh}")
  private String verifyPrepareSh;

  @Value("${verify.runner.sh}")
  private String verifyRunnerSh;

  // e.g. /home/me/R/initiate_check.R
  @Value("${verify.R}")
  private String verifyR;

  private static final AtomicInteger count = new AtomicInteger(0);

  private static final Log log = LogFactory.getLog(ZOONVerify.class);

  @PostConstruct
  protected void postConstruct() {
    if (StringUtils.isBlank(verifyR)) {
      throw new IllegalStateException("No module verification R script in spring.properties!");
    }
    if (StringUtils.isBlank(baseVerifyDir)) {
      throw new IllegalStateException("No verify directory specified in spring.properties!");
    }
    if (StringUtils.isBlank(verifyPrepareSh)) {
      throw new IllegalStateException("No verify prepare script specified in spring.properties!");
    }
    if (StringUtils.isBlank(verifyRunnerSh)) {
      throw new IllegalStateException("No verify runner script specified in spring.properties!");
    }

    log.info("~postConstruct() : Verify module R script set to '" + verifyR + "'.");
    log.info("~postConstruct() : Base Verify directory set to '" + baseVerifyDir + "'.");
    log.info("~postConstruct() : Verify prepare script set to '" + verifyPrepareSh + "'.");
    log.info("~postConstruct() : Verify runner script set to '" + verifyRunnerSh + "'.");
  }

  /**
   * Assign the verification process as finished.
   * 
   * @param identifier Identifier.
   * @see VerificationProcessMonitor
   */
  public void assignProcessFinished(final int identifier) {
    log.debug("~assignProcessFinished() : Invoked for '" + identifier + "'.");

    verificationState.put(identifier, true);
  }

  /**
   * Remove the data from the static vars after use.
   * 
   * @param identifier Verification identifier.
   */
  public void cleanUpStatics(final int identifier) {
    log.debug("~cleanUpStatics() : Invoked for '" + identifier + "'.");

    jobJSONLines.remove(identifier);
    jobOutputFileLines.remove(identifier);
    jobVREOutputLines.remove(identifier);
    verificationState.remove(identifier);
  }

  /**
   * Dump output lines to log file.
   * 
   * @param identifier Verification identifier.
   */
  public void dumpVREOutputLines(final int identifier) {
    log.debug("~dumpOutputLines() : '" + StringUtils.join(jobVREOutputLines.get(identifier), "\n").toString() + "'.");
  }

  private boolean invokeTomsRVerificationScript(final String temporaryDirectory, final String artifactName,
                                                final int identifier) {
    final StringBuffer commandLine = new StringBuffer();
    commandLine.append("./Rscript.sh").append(" ").append(verifyR).append(" ").append(artifactName) 
               .append(" ").append(temporaryDirectory).append(VERIFICATION_SCRIPT_OUTPUT_FILENAME)
               .append(" ").append(temporaryDirectory).append(VERIFICATION_SCRIPT_OUTPUT_JSON);

    final String fullCommandLine = commandLine.toString();

    log.debug("~invokeTomsRVerificationScript() : Command line '" + fullCommandLine + "'");

    final String[] localRunnerArgs = new String[4];

    localRunnerArgs[0] = verifyRunnerSh;
    localRunnerArgs[1] = temporaryDirectory;
    localRunnerArgs[2] = String.valueOf(identifier);
    localRunnerArgs[3] = fullCommandLine;

    final String invocation = StringUtils.join(localRunnerArgs, " ");

    boolean invoked = false;
    log.debug("~invokeTomsRVerificationScript() : Args '" + invocation + "'.");
    final Runtime runtime = Runtime.getRuntime();
    try {
      runtime.exec(localRunnerArgs);
      invoked = true;
    } catch (IOException ioe) {
      final String errorMessage = "Invocation of local verify runner failed with message '" + ioe.getMessage() + "'";
      log.error("~invokeTomsRVerificationScript(String) : " + errorMessage);
    }
    return invoked;
  }

  /**
   * Indicator to show if the identified process has finished by checking against the verification
   * state.
   * 
   * @param identifier Verification identifier.
   * @return <tt>true</tt> if the process is finished, otherwise <tt>false</tt>.
   */
  public boolean isProcessFinished(final int identifier) {
    if (!verificationState.containsKey(identifier)) {
      throw new UnsupportedOperationException("Job statuses doesn't contain the identifier '" + identifier + "'.");
    }

    return verificationState.get(identifier);
  }

  /**
   * Read the JSON output file and store content.
   * @param identifier
   */
  public void loadJSONOutput(final int identifier) {
    assert (jobJSONLines.containsKey(identifier)) : "Can't store JSON output if no Map entry!";
    log.debug("~loadJSONOutput() : Invoked for identifier '" + identifier + "'.");

    final String temporaryDirectory = FileUtil.retrieveJobDirectory(baseVerifyDir, identifier);
    final String filePath = temporaryDirectory.concat(VERIFICATION_SCRIPT_OUTPUT_JSON);
    log.debug("~loadJSONOutput() : Looking for file '" + filePath + "'.");
    final String fileContent = FileUtil.retrieveFileContent(filePath);
    if (fileContent != null) {
      log.debug("~loadJSONOutput() : Loaded '" + fileContent + "'.");
      jobJSONLines.put(identifier, fileContent);
    }
  }

  /**
   * Verify file containing process count number and process id.
   * 
   * @param file File containing details.
   */
  public void manageVerifyFile(@Payload File file) {
    log.debug("~manageVerifyFile() : Invoked.");

    final Map<String, String> processInfo = FileUtil.retrieveProcessInfo(file);
    FileUtil.delete(file);

    if (!processInfo.isEmpty()) {
      final int identifier = Integer.valueOf(processInfo.get(FileUtil.PROCESS_INFO_IDENTIFIER));
      final String processId = processInfo.get(FileUtil.PROCESS_INFO_PROCESS_ID);

      // This monitors the system process data.
      monitorVerificationProcess(processId, identifier);
      // This monitors whatever gets sent to stdout/stderr.
      monitorVerificationVREOutput(identifier);
      // This monitors whatever gets written to the verification R script output.
      monitorVerificationOutputFile(identifier);
    }
  }

  private void monitorVerificationVREOutput(final int identifier) {
    log.debug("~monitorVerificationOutput() : Invoked for identifier '" + identifier + "'.");

    final String temporaryDirectory = FileUtil.retrieveJobDirectory(baseVerifyDir, identifier);

    final VerificationVREOutputMonitor verificationVREOutputMonitor = this.applicationContext.getBean(BusinessIdentifiers.COMPONENT_VERIFICATION_VREOUTPUT_MONITOR,
                                                                                                VerificationVREOutputMonitor.class);
    final String outputFileName = vreOutputFilenamePrefix + identifier;
    log.debug("~monitorVerificationOutput() : Temporary verification directory is '" + temporaryDirectory + "'.");
    log.debug("~monitorVerificationOutput() : Output file name is '" + outputFileName + "'.");
    verificationVREOutputMonitor.setOutputDetails(temporaryDirectory, outputFileName);
    verificationVREOutputMonitor.setSchedule(monitorOutputFrequency);
    verificationVREOutputMonitor.setIdentifier(identifier);
  }

  private void monitorVerificationOutputFile(final int identifier) {
    log.debug("~monitorVerificationOutputFile() : Invoked for identifier '" + identifier + "'.");

    final String temporaryDirectory = FileUtil.retrieveJobDirectory(baseVerifyDir, identifier);

    final VerificationFileMonitor verificationFileMonitor = this.applicationContext.getBean(BusinessIdentifiers.COMPONENT_VERIFICATION_FILE_MONITOR,
                                                                                            VerificationFileMonitor.class);
    verificationFileMonitor.setIdentifier(identifier);
    verificationFileMonitor.setSchedule(monitorOutputFrequency);
    verificationFileMonitor.setVerificationFileName(temporaryDirectory.concat(VERIFICATION_SCRIPT_OUTPUT_FILENAME));
  }

  private void monitorVerificationProcess(final String processId, final int identifier) {
    log.debug("~monitorVerificationProcess() : Invoked for process '" + processId + "', identifier '" + identifier + "'.");

    final VerificationProcessMonitor verificationProcessMonitor = this.applicationContext.getBean(BusinessIdentifiers.COMPONENT_VERIFICATION_PROCESS_MONITOR,
                                                                                                  VerificationProcessMonitor.class);
    verificationProcessMonitor.setProcessId(processId);
    verificationProcessMonitor.setIdentifier(identifier);
    verificationProcessMonitor.setSchedule(monitorProcessFrequency);
  }

  private void prepareVerifyDirectoryContent(final String temporaryDirectory) {
    log.debug("~prepareVerifyDirectoryContent() : Invoked for '" + temporaryDirectory + "'.");

    final Runtime runtime = Runtime.getRuntime();
    final String[] preparerArgs = new String[2];
    preparerArgs[0] = verifyPrepareSh;
    preparerArgs[1] = temporaryDirectory;

    try {
      final Process process = runtime.exec(preparerArgs);
      process.waitFor();
    } catch (IOException e) {
      log.error("~prepareVerifyDirectoryContent() : IOException '" + e.getMessage() + "'.");
      e.printStackTrace();
    } catch (InterruptedException e) {
      log.error("~prepareVerifyDirectoryContent() : InterruptedException '" + e.getMessage() + "'.");
      e.printStackTrace();
    }
  }

  /**
   * Record the file output generated from a module verification process.
   * 
   * @param identifier Verification identifier.
   * @param unreadLines Newly available file output lines.
   */
  public void recordVerifyFileOutputLines(final int identifier, final List<String> unreadLines) {
    log.debug("~recordVerifyFileOutputLines() : '" + identifier + "', '" + unreadLines.toString() + "'.");

    jobOutputFileLines.get(identifier).addAll(unreadLines);
  }

  /**
   * Record the VRE_OUTPUT generated from a module verification process.
   * 
   * @param identifier Verification identifier.
   * @param unreadLines Newly available output lines.
   */
  public void recordVREOutputLines(final int identifier, final List<String> unreadLines) {
    jobVREOutputLines.get(identifier).addAll(unreadLines);
  }

  /**
   * Retrieve the file output lines for the module verification process.
   * 
   * @param verificationIdentifier Verification identifier.
   * @return File output lines, or <tt>null</tt>/empty collection if none available.
   */
  public List<String> retrieveFileOutputLines(final int verificationIdentifier) {
    log.debug("~retrieveFileOutputLines() : Invoked for '" + verificationIdentifier + "'.");

    return jobOutputFileLines.get(verificationIdentifier);
  }

  /**
   * Retrieve the output lines for the module verification process.
   * 
   * @param verificationIdentifier Verification identifier.
   * @return Output lines, or <tt>null</tt>/empty collection if none available.
   */
  public List<String> retrieveJobVREOutputLines(final int verificationIdentifier) {
    log.debug("~retrieveJobVREOutputLines() : Invoked for '" + verificationIdentifier + "'.");

    return jobVREOutputLines.get(verificationIdentifier);
  }

  /**
   * 
   * @param verificationIdentifier
   * @return
   */
  public String retrieveJSONOutputLine(final int verificationIdentifier) {
    log.debug("~retrieveJSONOutputLine() : Invoked for '" + verificationIdentifier + "'.");

    return jobJSONLines.get(verificationIdentifier);
  }

  /**
   * Retrieve the minimum artifact data associated with verification identifier.
   * 
   * @param verificationIdentifier Verification identifier.
   * @return Minimum artifact data associated with verification identifier.
   */
  public MinimumArtifactDataVO retrieveMinimumArtifactData(final int verificationIdentifier) {
    return minimumArtifactDatas.get(verificationIdentifier);
  }

  /**
   * Retrieve the state (finished or not) for the module verification.
   * 
   * @param verificationIdentifier Verification identifier.
   * @return <tt>true</tt> if verification finished, otherwise <tt>false</tt> or <tt>null</tt>.
   */
  public Boolean retrieveVerificationState(final int verificationIdentifier) {
    log.debug("~retrieveVerificationState() : Invoked for '" + verificationIdentifier + "'.");

    return verificationState.get(verificationIdentifier);
  }

  /**
   * Remove the temporary validation structure.
   * 
   * @param temporaryDirectory Path of directory which is used as the temporary processing.
   */
  public void tidyUp(final String temporaryDirectory) {
    log.debug("~tidyUp() : Removing '" + temporaryDirectory + "'.");

    FileUtil.delete(new File(temporaryDirectory));
  }

  /**
   * Verify the module.
   * 
   * @param minimumArtifactData Minimum artifact data.
   * @param moduleContent Content of module to verify.
   * @return Unique verification identifier.
   */
  public Integer verifyModule(final MinimumArtifactDataVO minimumArtifactData,
                              final String moduleContent) {
    log.debug("~verifyModule() : Invoked.");

    final int identifier = count.incrementAndGet();

    // e.g. ~/zoon/verify/1/
    final String temporaryDirectory = FileUtil.retrieveJobDirectory(baseVerifyDir, identifier);

    // Copy stuff to ~/zoon/verify/1/
    prepareVerifyDirectoryContent(temporaryDirectory);

    // Write artifact to directory ~/zoon/verify/1/
    final String name = minimumArtifactData.getArtifactName();
    final boolean written = writeArtifactToDirectory(temporaryDirectory, name, moduleContent);

    boolean invoked = false;
    if (written) {
      // Run stuff in ~/zoon/verify/1/
      invoked = invokeTomsRVerificationScript(temporaryDirectory, name, identifier);

      verificationState.put(identifier, false);
      jobVREOutputLines.put(identifier, new ArrayList<String>());
      jobOutputFileLines.put(identifier, new ArrayList<String>());
      jobJSONLines.put(identifier, null);
      minimumArtifactDatas.put(identifier, minimumArtifactData);
    }

    log.debug("~verifyModule() : Tom's script was" + (invoked ? "" : " not") + " invoked.");

    return identifier;
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