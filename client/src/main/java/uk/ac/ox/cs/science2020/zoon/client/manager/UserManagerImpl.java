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
package uk.ac.ox.cs.science2020.zoon.client.manager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import uk.ac.ox.cs.science2020.zoon.client.ClientIdentifiers;
import uk.ac.ox.cs.science2020.zoon.client.dao.UserDAO;
import uk.ac.ox.cs.science2020.zoon.client.entity.Identity;
import uk.ac.ox.cs.science2020.zoon.client.entity.User;
import uk.ac.ox.cs.science2020.zoon.client.exception.UserIdentityAlreadyExistsException;
import uk.ac.ox.cs.science2020.zoon.client.exception.UsernameAlreadyExistsException;
import uk.ac.ox.cs.science2020.zoon.client.value.object.user.UserDetails;
import uk.ac.ox.cs.science2020.zoon.shared.value.object.ActionOutcomeVO;

/**
 * Implementation of the user management interface.
 *
 * @author geoff
 */
// Check out META-INF/spring/ctx/data/appCtx.database.xml for transaction mgmt!
@Component(ClientIdentifiers.COMPONENT_USER_MANAGER)
public class UserManagerImpl implements UserManager {

  private static final int userNotEnabled = 0;
  protected static final String REGISTRATION_FAILED_RESPONSE = "Sorry! Registration failed.";

  @Autowired @Qualifier(ClientIdentifiers.COMPONENT_USER_DAO)
  private UserDAO userDAO;

  private static final Log log = LogFactory.getLog(UserManagerImpl.class);

  /* (non-Javadoc)
   * @see uk.ac.ox.cs.science2020.zoon.client.manager.UserManager#addUserIdentity(java.lang.String, java.lang.String, java.lang.String)
   */
  @Override
  public ActionOutcomeVO addUserIdentity(final String userName, final String identity,
                                         final String email)
                                         throws UserIdentityAlreadyExistsException {
    log.debug("~addUserIdentity() : Invoked.");
    if (retrieveNonAdminUserByIdentity(identity) != null) {
      throw new UserIdentityAlreadyExistsException();
    }

    final Set<String> emails = new HashSet<String>();
    if (!StringUtils.isBlank(email)) {
      emails.add(email);
    }
    final Identity newIdentity = new Identity(identity, emails);

    return userDAO.addIdentity(userName, newIdentity);
  }

  /* (non-Javadoc)
   * @see uk.ac.ox.cs.science2020.zoon.client.manager.UserManager#deleteUser(java.lang.String)
   */
  @Override
  public boolean deleteUser(final String userName) {
    log.debug("~deleteUser() : Invoked for '" + userName + "'.");

    return userDAO.delete(userName);
  }

  /* (non-Javadoc)
   * @see uk.ac.ox.cs.science2020.zoon.client.manager.UserManager#registerUser(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
   */
  @Override
  public String registerUser(final String identityName, final String email, final String userName,
                             final String password) {
    log.debug("~registerUser() : Invoked.");

    String response = REGISTRATION_FAILED_RESPONSE;

    final List<String> requirements = new ArrayList<String>();
    if (StringUtils.isBlank(identityName)) {
      requirements.add("Identity name");
    }
    if (StringUtils.isBlank(email)) {
      requirements.add("Email");
    }
    if (StringUtils.isBlank(userName)) {
      requirements.add("User name");
    }
    if (StringUtils.isBlank(password)) {
      requirements.add("Password");
    }

    if (!requirements.isEmpty()) {
      response += " ".concat(StringUtils.join(requirements, ", ")).concat(" required for user registration.");
    } else {
      final Set<String> emails = new HashSet<String>();
      emails.add(email);
      final Set<Identity> identities = new HashSet<Identity>();
      identities.add(new Identity(identityName, emails));
      final User newUser = new User(identities, userName, password, userNotEnabled);
  
      final List<String> invalidValues = newUser.hasValidValues(true);
      if (!invalidValues.isEmpty()) {
        response += " ".concat(StringUtils.join(invalidValues, ", "));
      } else {
        try {
          userDAO.add(newUser);
          response = null;
        } catch (UsernameAlreadyExistsException e) {
          response += " ".concat("Specified user name '" + email + "' already exists!");
        }
      }
    }

    return response;
  }

  /* (non-Javadoc)
   * @see uk.ac.ox.cs.science2020.zoon.client.service.UserManager#retrieveUsersByUsername(java.lang.String)
   */
  @Override
  public List<UserDetails> retrieveUsersByUsername(final String userName) {
    log.debug("~retrieveUsersByUsername() : Invoked for userName '" + userName + "'.");

    return userDAO.retrieveUsersByUsername(userName);
  }

  /* (non-Javadoc)
   * @see uk.ac.ox.cs.science2020.zoon.client.service.UserManager#retrieveNonAdminUserByIdentity(java.lang.String)
   */
  @Override
  public UserDetails retrieveNonAdminUserByIdentity(final String identity) {
    log.debug("~retrieveNonAdminUserByIdentity() : Invoked for '" + identity + "'.");

    return userDAO.retrieveNonAdminUserByIdentity(identity);
  }

  /* (non-Javadoc)
   * @see uk.ac.ox.cs.science2020.zoon.client.service.UserManager#retrieveUser(java.lang.String)
   */
  @Override
  public UserDetails retrieveUser(final String name) {
    log.debug("~retrieveUser() : Invoked for user '" + name + "'.");

    return userDAO.retrieveUser(name);
  }

  /* (non-Javadoc)
   * @see uk.ac.ox.cs.science2020.zoon.client.service.UserManager#retrieveUsers()
   */
  @Override
  public List<UserDetails> retrieveUsers() {
    log.debug("~retrieveUsers() : Invoked.");

    return userDAO.retrieveUsers();
  }

  /* (non-Javadoc)
   * @see uk.ac.ox.cs.science2020.zoon.client.service.UserManager#setEnabled(java.lang.String, boolean)
   */
  @Override
  public void setEnabled(final String name, final boolean enabled) {
    log.debug("~setEnabled() : Setting '" + name + "' to '" + enabled + "'.");

    userDAO.setEnabled(name, enabled);
  }
}