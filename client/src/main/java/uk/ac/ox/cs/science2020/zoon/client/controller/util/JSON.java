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

/**
 * 
 */
package uk.ac.ox.cs.science2020.zoon.client.controller.util;

import java.io.Serializable;

/**
 * Utility bean to place into the return MVC model.
 *
 * @author geoff
 */
public class JSON implements Serializable {

  private static final long serialVersionUID = 1L;

  private final String json;
  private final String exception;

  /**
   * Initialising constructor.
   * 
   * @param json JSON string.
   * @param exception Exception message.
   * @throws IllegalArgumentException If both or neither parameters supplied.
   */
  public JSON(final String json, final String exception) {
    if ((json != null && exception != null) ||
        (json == null && exception == null)) {
      throw new IllegalArgumentException("Either a JSON string or an exception message required!");
    }

    this.json = json;
    this.exception = exception;
  }

  /**
   * Retrieve the JSON string.
   * 
   * @return JSON string (or {@code null} if not assigned).
   */
  public String getJson() {
    return json;
  }

  /**
   * Retrieve the exception message.
   * 
   * @return Exception message (or {@code null} if not assigned).
   */
  public String getException() {
    return exception;
  }
}