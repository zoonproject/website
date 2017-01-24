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
package uk.ac.ox.cs.science2020.zoon.client.controller.authenticated;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import uk.ac.ox.cs.science2020.zoon.client.ClientIdentifiers;
import uk.ac.ox.cs.science2020.zoon.client.controller.ajax.authenticated.ManageModulesAJAX;

/**
 * Authenticated module management pages.
 *
 * @author geoff
 * @see ManageModulesAJAX
 */
@Controller
@RequestMapping(ClientIdentifiers.URL_PREFIX_MANAGE_MODULES)
public class ManageModules {

  private static final Log log = LogFactory.getLog(ManageModules.class);

  /**
   * View the module management (delete and store in private) page.
   * 
   * @return Page name.
   */
  @RequestMapping(method=RequestMethod.GET,
                  value=ClientIdentifiers.VIEW_MODULE_MANAGE)
  public String viewModuleManage() {
    log.debug("~viewModuleManage() : Invoked.");

    return ClientIdentifiers.PAGE_MODULE_MANAGE;
  }

  /**
   * View the module management (upload to ZOON) page.
   * 
   * @return Page name.
   */
  @RequestMapping(method=RequestMethod.GET,
                  value=ClientIdentifiers.VIEW_MODULE_UPLOAD_ZOON)
  public String viewModuleUploadZOON() {
    log.debug("~viewModuleUploadZOON() : Invoked.");

    return ClientIdentifiers.PAGE_MODULE_UPLOAD_ZOON;
  }

  /**
   * View the module management (verification) page.
   * 
   * @return Page name.
   */
  @RequestMapping(method=RequestMethod.GET,
                  value=ClientIdentifiers.VIEW_MODULE_VERIFY)
  public String viewModuleVerify() {
    log.debug("~viewModuleVerify() : Invoked.");

    return ClientIdentifiers.PAGE_MODULE_VERIFY;
  }
}