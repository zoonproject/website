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
package uk.ac.ox.cs.science2020.zoon.client.binding;

import org.apache.commons.lang3.StringUtils;

/**
 * Binding object used for handling user registration.
 *
 * @author geoff
 */
public class UserRegistration {

  private String identityName;
  private String email;
  private String userName;
  private String password;

  /**
   * Default constructor.
   */
  public UserRegistration() {}

  /**
   * Initialising constructor.
   * 
   * @param identityName Identity name.
   * @param email Identity email.
   * @param password password.
   */
  public UserRegistration(final String identityName, final String email, final String password) {
    this.identityName = identityName;
    // Decision made to use email as the system user name.
    setEmail(email);
    this.password = password;
  }

  /**
   * Indicator of value presence in each required field.
   * 
   * @return {@code true} if values in all fields, otherwise {@code false}.
   */
  public boolean hasAllValuesAssigned() {
    return !(StringUtils.isBlank(getIdentityName()) && StringUtils.isBlank(getEmail()) &&
             StringUtils.isBlank(getUserName()) && StringUtils.isBlank(getPassword()));
  }

  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "UserRegistration [identityName=" + identityName + ", email="
        + email + ", userName=" + userName + "]";
  }

  /**
   * Retrieve the identity name.
   * 
   * @return Assigned identity name.
   */
  public String getIdentityName() {
    return identityName;
  }

  /**
   * Assign the identity name.
   * 
   * @param identityName Identity name to assign.
   */
  public void setIdentityName(final String identityName) {
    this.identityName = identityName;
  }

  /**
   * Retrieve the email.
   * 
   * @return Assigned email address.
   */
  public String getEmail() {
    return email;
  }

  /**
   * Assign's the user name and email to be the email.
   * <p>
   * <b>Important</b> : In this case the user's email address is being used as the user name!
   * 
   * @param email Email address.
   */
  public void setEmail(final String email) {
    this.email = email;
    this.userName = email;
  }

  /**
   * Retrieve the user name (or rather, the email address).
   * 
   * @return The user name.
   * @see {@link #setEmail(String)}
   */
  public String getUserName() {
    return userName;
  }

  /**
   * Not used! Set the username via the email!
   * 
   * @param userName the userName to set
   * @see {@link #setEmail(String)}
   */
  @Deprecated
  protected void setUserName(final String userName) {
    this.userName = userName;
  }

  /**
   * Retrieve the password.
   * 
   * @return Assigned password.
   */
  public String getPassword() {
    return password;
  }

  /**
   * Assign the password.
   * 
   * @param password Password to assign.
   */
  public void setPassword(final String password) {
    this.password = password;
  }
}