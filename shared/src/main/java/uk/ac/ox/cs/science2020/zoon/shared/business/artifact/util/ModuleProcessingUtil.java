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
package uk.ac.ox.cs.science2020.zoon.shared.business.artifact.util;

import java.util.Arrays;
import java.util.List;

/**
 * Utility to help with module processing tasks.
 *
 * @author geoff
 */
public class ModuleProcessingUtil {

  public static final String EMAIL_BACKSLASHED = "\\email";
  private static final List<String> delimiters = Arrays.asList(new String[] { ",", "\'", EMAIL_BACKSLASHED });

  /**
   * Split up what should be single author data and maybe email into separate author name and email
   * if present.
   * 
   * @param fullText Author + email text.
   * @return Constituent parts : Element 0 = Author, Element 1 = Email.
   */
  public static String[] extractAuthorComponents(final String fullText) {
    String[] authorComponents = { "", "" };
    boolean delimiterFound = false;
    for (final String delimiter : delimiters) {
      if (fullText.contains(delimiter)) {
        delimiterFound = true;
        final String splitOn = EMAIL_BACKSLASHED.equals(delimiter) ? "\\".concat(EMAIL_BACKSLASHED) : delimiter;
        final String[] components = fullText.split(splitOn);
        authorComponents[0] = components[0].trim();
        String email = components[1].trim();
        email = email.replace(EMAIL_BACKSLASHED, "");
        email = email.replace("{", "");
        email = email.replace("@@", "@");
        email = email.replace("}", "");
        authorComponents[1] = email;
        break;
      }
    }
    if (!delimiterFound) {
      authorComponents[0] = fullText;
    }

    return authorComponents;
  }
}