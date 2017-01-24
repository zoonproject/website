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

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;

import uk.ac.ox.cs.science2020.zoon.business_manager.BusinessIdentifiers;
import uk.ac.ox.cs.science2020.zoon.business_manager.business.util.FileUtil;
import uk.ac.ox.cs.science2020.zoon.business_manager.entity.Simulation;


/**
 * 
 *
 * @author geoff
 */
public class ZOONRunPreProcessor {

  // Spring-injected.
  @Value("${base.dir}")
  private String baseDir;

  private static final String zoonScriptPrepare = "./zoon_prepare.sh";

  private static final Log log = LogFactory.getLog(ZOONRunPreProcessor.class);

  public Simulation preProcess(final Simulation simulation) {
    log.debug("~int.preProcessApPredict() : Invoked");
    log.debug("~int.preProcessApPredict() : Thread '" + Thread.currentThread().getName() + "'");
  
    // first create the run environment
    log.debug("~int.preProcessApPredict() : Base dir is '" + baseDir + "'.");
    final String jobDirectory = FileUtil.retrieveJobDirectory(baseDir, simulation.getId());
  
    final String[] preparerArgs = new String[2];
    if (BusinessIdentifiers.OS_NAME.equals(BusinessIdentifiers.LINUX)) {
      preparerArgs[0] = zoonScriptPrepare;
      preparerArgs[1] = jobDirectory;
    } else if (BusinessIdentifiers.OS_NAME.equals(BusinessIdentifiers.WINDOWS_XP)) {
      final String errorMessage = "Sorry! Cannot run scripts on '" + BusinessIdentifiers.OS_NAME + "' systems!";
      log.fatal("~int.preProcessApPredict() : " + errorMessage);
      throw new UnsupportedOperationException(errorMessage);
    } else {
      final String errorMessage = "Unrecognised os.name property of '" + BusinessIdentifiers.OS_NAME + "'";
      log.fatal("~int.preProcessApPredict() : " + errorMessage);
      throw new UnsupportedOperationException(errorMessage);
    }
  
    final Runtime runtime = Runtime.getRuntime();
    try {
      log.debug("~int.preProcessApPredict() : Pre-execution of '" + zoonScriptPrepare + "'.");
      final Process process = runtime.exec(preparerArgs);
      process.waitFor();
      log.debug("~int.preProcessApPredict() : Post-completion of '" + zoonScriptPrepare + "'.");
    } catch (IOException ioe) {
      ioe.printStackTrace();
      final String errorMessage = "Extracting ApPredict from .tgz failed with message '" + ioe.getMessage() + "'";
      log.error("~int.preProcessApPredict() : " + errorMessage);
      throw new UnsupportedOperationException(errorMessage);
    } catch (InterruptedException e) {
      e.printStackTrace();
      final String errorMessage = "Extracting ApPredict from .tgz failed with message '" + e.getMessage() + "'";
      log.error("~int.preProcessApPredict() : " + errorMessage);
      throw new UnsupportedOperationException(errorMessage);
    }
  
    return simulation;
  }
}