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
package uk.ac.ox.cs.science2020.zoon.business_manager.value.object;

/**
 *
 *
 * @author geoff
 */
public class RInvocationResultsVO {

  private final boolean jobCompleted;
  private final String output;
  private final String result;
  private final String json;
  private final boolean verified;

  /**
   * Initialising constructor.
   * 
   * @param jobCompleted Is the invocation completed, i.e. process ended and files read in.
   * @param output The stderr/stdout output of the invocation process.
   * @param result Textual output of the R script.
   * @param json JSON output.
   * @param verified {@code true} if module has been verified, otherwise {@code false}.
   */
  public RInvocationResultsVO(final boolean jobCompleted, final String output, final String result,
                              final String json, final boolean verified) {
    this.jobCompleted = jobCompleted;
    this.output = output;
    this.result = result;
    this.json = json;
    this.verified = verified;
  }

  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "RInvocationResultsVO [jobCompleted=" + jobCompleted + ", output=" + output
        + ", result=" + result + ", json=" + json + ", verified=" + verified
        + "]";
  }

  /**
   * @return the jobCompleted
   */
  public boolean isJobCompleted() {
    return jobCompleted;
  }

  /**
   * @return the output
   */
  public String getOutput() {
    return output;
  }

  /**
   * @return the result
   */
  public String getResult() {
    return result;
  }

  /**
   * @return the json
   */
  public String getJson() {
    return json;
  }

  /**
   * @return the verified
   */
  public boolean isVerified() {
    return verified;
  }
}