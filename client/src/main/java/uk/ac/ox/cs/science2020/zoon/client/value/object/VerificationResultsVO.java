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
package uk.ac.ox.cs.science2020.zoon.client.value.object;

/**
 * Value object containing the results of module verification (i.e. after running Tom's R script
 * which verifies modules).
 *
 * @author geoff
 */
public class VerificationResultsVO {

  private final boolean outcome;
  private final String output;
  private final String result;

  /**
   * Initialising constructor.
   * 
   * @param outcome The verification outcome, i.e. {@code true} if valid, otherwise {@code false}.
   * @param output The stderr/stdout output of the verification process.
   * @param result Textual output of the R validation script.
   */
  public VerificationResultsVO(final boolean outcome, final String output, final String result) {
    this.outcome = outcome;
    this.output = output;
    this.result = result;
  }

  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "VerificationResultsVO [outcome=" + outcome + ", output=" + output
        + ", result=" + result + "]";
  }

  /**
   * Retrieve the verification outcome.
   * 
   * @return {@code true} if verification was successful, otherwise {@code false}.
   */
  public boolean isOutcome() {
    return outcome;
  }

  /**
   * Retrieve the verification output.
   * 
   * @return Whatever Tom's script send to stderr/stdout.
   */
  public String getOutput() {
    return output;
  }

  /**
   * Retrieve the textual representation of the R validation script.
   * 
   * @return Whatever Tom's script wrote to file.
   */
  public String getResult() {
    return result;
  }
}