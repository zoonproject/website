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
package uk.ac.ox.cs.science2020.zoon.client.value.object.user;

import java.util.Set;

import uk.ac.ox.cs.science2020.zoon.client.entity.Identity;
import uk.ac.ox.cs.science2020.zoon.client.entity.User;

/**
 * Avoiding passwords emanating from database unnecessarily by using objects of this class as
 * wrappers rather than {@link User} objects.
 *
 * @author geoff
 */
public class UserDetails {

  private final String username;
  private final Set<Identity> identities;
  private final int enabled;

  /**
   * Initialising constructor.
   * 
   * @param user User to use as a basis of object construction.
   */
  public UserDetails(final User user) {
    this.username = user.getUsername();
    this.identities = user.getIdentities();
    this.enabled = user.getEnabled();
  }

  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "UserDetails [username=" + username + ", identities=" + identities + ", enabled="
        + enabled + "]";
  }

  /**
   * Retrieve the user's name.
   * 
   * @return User's name.
   */
  public String getUsername() {
    return username;
  }

  /**
   * Retrieve the user's identities.
   * 
   * @return User's identities.
   */
  public Set<Identity> getIdentities() {
    return identities;
  }

  /**
   * Indicator as to whether the user is enabled or otherwise.
   * 
   * @return {@linkplain User#ENABLED} if enabled, otherwise {@linkplain User#DISABLED}.
   */
  public int getEnabled() {
    return enabled;
  }
}