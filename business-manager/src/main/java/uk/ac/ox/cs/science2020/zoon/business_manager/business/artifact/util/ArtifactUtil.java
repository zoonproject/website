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
package uk.ac.ox.cs.science2020.zoon.business_manager.business.artifact.util;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import uk.ac.ox.cs.science2020.zoon.business_manager.value.object.moduleheader.HeaderObject;
import uk.ac.ox.cs.science2020.zoon.business_manager.value.object.moduleheader.ModuleTypeVO;
import uk.ac.ox.cs.science2020.zoon.business_manager.value.object.moduleheader.SingleValueVO;
import uk.ac.ox.cs.science2020.zoon.shared.value.type.DataIdentifier;
import uk.ac.ox.cs.science2020.zoon.shared.value.type.ModuleType;

/**
 * 
 *
 * @author 
 */
public class ArtifactUtil {

  private static final Log log = LogFactory.getLog(ArtifactUtil.class);

  /*
  private static void appendToMV(final ModuleHeader moduleHeader, final String appendValue,
                                 final Map<ModuleHeader, HeaderObject> latest) {
    if (latest.containsKey(moduleHeader)) {
      final HeaderObject headerObject = latest.get(moduleHeader);
      final MultiValueVO multiValueVO = (MultiValueVO) headerObject;
      multiValueVO.getComments().add(appendValue);
    } else {
      latest.put(moduleHeader, new MultiValueVO(appendValue));
    }
  }
  */

  /**
   * 
   * 
   * @param headerObjects
   * @return
   */
  public static String getModuleVersion(final Map<DataIdentifier, HeaderObject> headerObjects) {
    log.debug("~getModuleVersion() : Invoked.");
    final SingleValueVO singleValueVO = (SingleValueVO) headerObjects.get(DataIdentifier.VERSION);
    if (singleValueVO == null) {
      throw new IllegalStateException("Dodgy Module? Can't find the Module version!");
    }
    return singleValueVO.getComment();
  }

  /**
   * 
   * 
   * @param headerObjects
   * @return
   */
  public static ModuleType getModuleType(final Map<DataIdentifier, HeaderObject> headerObjects) {
    log.debug("~getModuleType() : Invoked.");
    final ModuleTypeVO moduleTypeVO = (ModuleTypeVO) headerObjects.get(DataIdentifier.TYPE);
    if (moduleTypeVO == null) {
      throw new IllegalStateException("Dodgy Module? Can't find the Module type!");
    }
    return moduleTypeVO.getModuleType();
  }
}