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
 * Value object extending the module properties value object, used when publishing a module.
 *
 * @author geoff
 */
public class ModulePublishVO extends ModuleActionVO {

  private static final long serialVersionUID = 1L;

  private boolean removeAfterUpload;

  /**
   * Default constructor.
   */
  protected ModulePublishVO() {}

  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "ModulePublishVO [removeAfterUpload=" + removeAfterUpload
        + ", toString()=" + super.toString() + "]";
  }

  /**
   * Is the module to be removed from the private repository after upload to the public repository.
   * 
   * @return {@code true} if to remove, otherwise {@code false}.
   */
  public boolean isRemoveAfterUpload() {
    return removeAfterUpload;
  }

  /**
   * Assign the flag to remove (or leave) the module from the private repository once it's been
   * uploaded to the public repository.
   * 
   * @param removeAfterUpload {@code true} if to remove after upload, otherwise {@code false}.
   */
  public void setRemoveAfterUpload(final boolean removeAfterUpload) {
    this.removeAfterUpload = removeAfterUpload;
  }
}