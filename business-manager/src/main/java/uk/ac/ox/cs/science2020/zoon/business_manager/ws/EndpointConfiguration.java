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
package uk.ac.ox.cs.science2020.zoon.business_manager.ws;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ws.server.EndpointInterceptor;
import org.springframework.ws.server.endpoint.MessageEndpoint;
import org.springframework.ws.server.endpoint.mapping.UriEndpointMapping;

import uk.ac.ox.cs.science2020.zoon.business_manager.BusinessIdentifiers;

/**
 * Web service configurer which assigns an endpoint to which all incoming SOAP messages will be 
 * sent.
 * <p>
 * Pro Spring Integration, Apress [ISBN 978-1-4302-3345-9] (Updated! See @see)
 * 
 * @see <a href="https://jira.springsource.org/browse/SPR-8539?focusedCommentId=75569&page=com.atlassian.jira.plugin.system.issuetabpanels:comment-tabpanel#comment-75569">jira.springsource.org</a>
 * @author Geoff Williams
 */
@Configuration
public class EndpointConfiguration {

  // appCtx.ws.security-incoming.xml
  @Autowired(required=false)
  @Qualifier(BusinessIdentifiers.COMPONENT_BUS_MANAGER_INBOUND_GTWY_INTERCEPTOR)
  private EndpointInterceptor wsInboundInterceptor;

  private List<EndpointInterceptor> wsInboundInterceptors = new ArrayList<EndpointInterceptor>();

  // appCtx.int.xml
  @Value("#{" + BusinessIdentifiers.COMPONENT_WS_INBOUND_GATEWAY + "}")
  private MessageEndpoint wsInboundGateway;

  private static final Log log = LogFactory.getLog(EndpointConfiguration.class);

  @PostConstruct
  private void postConstruct() {
    if (wsInboundInterceptor != null) {
      wsInboundInterceptors.add(wsInboundInterceptor);
    }
    for (final EndpointInterceptor endpointInterceptor : wsInboundInterceptors) {
      log.info("~EndpointConfiguration() : Interceptor '" + endpointInterceptor + "' assigned for inbound.");
    }
  }

  /**
   * Configure the inbound gateway as the bean which is going to handle incoming requests. 
   * 
   * @return Inbound web service gateway (as a message endpoint).
   */
  @Bean
  public UriEndpointMapping uriEndpointMapping() {
    log.debug("~uriEndpointMapping() : Configuring with wsInboundGateway '" + wsInboundGateway + "'.");
    final UriEndpointMapping uriEndpointMapping = new UriEndpointMapping();
    uriEndpointMapping.setDefaultEndpoint(wsInboundGateway);
    uriEndpointMapping.setInterceptors((EndpointInterceptor[]) wsInboundInterceptors.toArray(new EndpointInterceptor[wsInboundInterceptors.size()]));
    return uriEndpointMapping;
  }
}