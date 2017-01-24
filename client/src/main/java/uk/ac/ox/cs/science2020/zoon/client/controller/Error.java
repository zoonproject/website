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
package uk.ac.ox.cs.science2020.zoon.client.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Controller handling view resolution when unexpected errors occur.
 * <p>
 * Generally this is in response to the web.xml error-page element values.
 *
 * @author geoff
 */
@Controller
public class Error {

  private static final String errorDir = "error/";
  /**
   * Generic error page.
   */
  protected static final String errorGeneric = errorDir.concat("error");
  private static final String error403 = errorDir.concat("403");
  private static final String error404 = errorDir.concat("404");
  private static final String error500 = errorDir.concat("500");

  private static final Log log = LogFactory.getLog(Error.class);

  /**
   * Handle a generic error.
   * 
   * @return View to show (in WEB-INF/views).
   */
  @RequestMapping(value="error")
  public String handleError() {
    log.warn("~handleError() : Invoked.");

    return errorGeneric;
  }

  /**
   * Handle a HTTP 403.
   * 
   * @return View to show (in WEB-INF/views).
   */
  @RequestMapping(value="error403",
                  method=RequestMethod.POST)
  public String handle403() {
    log.warn("~handle403() : Invoked.");

    return error403;
  }

  /**
   * Handle a HTTP 404 (Not Found).
   * 
   * @return View to show (in WEB-INF/views).
   */
  @RequestMapping(value="error404")
  public String handle404() {
    log.warn("~handle404() : Invoked.");

    return error404;
  }

  /**
   * Handle a HTTP 500 (Internal Server Error).
   * 
   * @return View to show (in WEB-INF/views).
   */
  @RequestMapping(value="error500")
  public String handle500() {
    log.warn("~handle500() : Invoked.");

    return error500;
  }
}