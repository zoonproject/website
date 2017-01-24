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
package uk.ac.ox.cs.science2020.zoon.business_manager;

/**
 * Consistent named values.
 *
 * @author geoff
 */
public class BusinessIdentifiers {

  public static final String OS_NAME = System.getProperty("os.name");
  public static final String FILE_SEPARATOR = System.getProperty("file.separator");

  public static final String LINUX = "Linux";
  public static final String WINDOWS_XP = "Windows XP";

  /** Component name for the app manager DAO */
  public static final String COMPONENT_APP_MANAGER_DAO = "appManagerDAO";
  /** Component name for the artifact service implementation */
  public static final String COMPONENT_ARTIFACT_SERVICE = "artifactServiceImpl";
  /** Component name for the artifact manager implementation */
  public static final String COMPONENT_ARTIFACT_MANAGER = "artifactManagerImpl";
  /** Component name for the business Manager WS WSS4J incoming gateway security interceptor
      <p>
      See also (sample.)appCtx.ws.security-incoming.xml */
  public static final String COMPONENT_BUS_MANAGER_INBOUND_GTWY_INTERCEPTOR = "wsInboundGatewayInterceptor";
  /** Component - Configuration settings */
  public static final String COMPONENT_CONFIGURATION = "configuration";
  /** Component - Configuration manager */
  public static final String COMPONENT_CONFIGURATION_MANAGER = "configurationManager";
  /** Component - Configuration service */
  public static final String COMPONENT_CONFIGURATION_SERVICE = "configurationService";
  /** Component name for output monitor */
  public static final String COMPONENT_OUTPUT_MONITOR = "outputMonitor";
  /** Component - Parse file monitor. */
  public static final String COMPONENT_PARSE_FILE_MONITOR = "parseFileMonitor";
  /** Component - Parse output monitor. */
  public static final String COMPONENT_PARSE_VREOUTPUT_MONITOR = "parseVREOutputMonitor";
  /** Component - Parse process monitor. */
  public static final String COMPONENT_PARSE_PROCESS_MONITOR = "parseProcessMonitor";
  /** Component name for process monitor */
  public static final String COMPONENT_PROCESS_MONITOR = "processMonitor";
  /** Component - Simulation DAO. */
  public static final String COMPONENT_SIMULATION_DAO = "simulationDAOImpl";
  /** Component - Simulation manager. */
  public static final String COMPONENT_SIMULATION_MANAGER = "simulationManagerImpl";
  /** Component - Simulation processing gateway */
  public static final String COMPONENT_SIMULATION_PROCESSING_GATEWAY = "simulationProcessingGateway";
  /** Component - Simulation service. */
  public static final String COMPONENT_SIMULATION_SERVICE = "simulationServiceImpl";
  /** Component - Web service inbound gateway. */
  public static final String COMPONENT_WS_INBOUND_GATEWAY = "wsInboundGateway";
  /** Component - Verification file monitor. */
  public static final String COMPONENT_VERIFICATION_FILE_MONITOR = "verificationFileMonitor";
  /** Component - Verification output monitor. */
  public static final String COMPONENT_VERIFICATION_VREOUTPUT_MONITOR = "verificationVREOutputMonitor";
  /** Component - Verification process monitor. */
  public static final String COMPONENT_VERIFICATION_PROCESS_MONITOR = "verificationProcessMonitor";
  /** Component - ZOON parse module. */
  public static final String COMPONENT_ZOON_PARSE = "zoonParse";
  /** Component - ZOON verify module. */
  public static final String COMPONENT_ZOON_VERIFY = "zoonVerify";

  /** Spring Integration message header key : Simulation id. */
  public static final String SI_HDR_SIMULATION_ID = "hdr_business_manager_simulationId";
  /** Spring Integration message header key : Module name. */
  public static final String SI_HDR_MODULE_NAME = "hdr_business_manager_moduleName";
  /** Spring Integration message header key : Module version. */
  public static final String SI_HDR_MODULE_VERSION = "hdr_business_manager_moduleVersion";

  private BusinessIdentifiers() {}

}