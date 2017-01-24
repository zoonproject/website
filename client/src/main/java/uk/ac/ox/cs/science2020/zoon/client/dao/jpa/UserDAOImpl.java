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
package uk.ac.ox.cs.science2020.zoon.client.dao.jpa;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Repository;

import uk.ac.ox.cs.science2020.zoon.client.ClientIdentifiers;
import uk.ac.ox.cs.science2020.zoon.client.dao.UserDAO;
import uk.ac.ox.cs.science2020.zoon.client.entity.Authority;
import uk.ac.ox.cs.science2020.zoon.client.entity.Identity;
import uk.ac.ox.cs.science2020.zoon.client.entity.User;
import uk.ac.ox.cs.science2020.zoon.client.exception.UsernameAlreadyExistsException;
import uk.ac.ox.cs.science2020.zoon.client.value.object.user.UserDetails;
import uk.ac.ox.cs.science2020.zoon.shared.value.object.ActionOutcomeVO;

/**
 * User management DAO implementation.
 *
 * @author geoff
 */
// See appCtx.database.xml for transaction advice.
@Repository(ClientIdentifiers.COMPONENT_USER_DAO)
public class UserDAOImpl implements UserDAO {

  // Needs to match src/main/webapp/WEB-INF/spring/appCtx.root-security.xml and DatabasePasswordSecurer
  private static final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
  private static final String roleAdministrator = "ROLE_ADMINISTRATOR";
  private static final String roleUser = "ROLE_USER";
  private static final Authority authorityAdministrator = new Authority(roleAdministrator);
  private static final Authority authorityUser = new Authority(roleUser);

  @Autowired @Qualifier("zoonSessionFactory")
  private SessionFactory sessionFactory;

  private static final Log log = LogFactory.getLog(UserDAOImpl.class);

  /* (non-Javadoc)
   * @see uk.ac.ox.cs.science2020.zoon.client.dao.UserDAO#add(uk.ac.ox.cs.science2020.zoon.client.entity.User)
   */
  @Override
  public User add(final User userToAdd) throws UsernameAlreadyExistsException {
    log.debug("~add() : Adding user '" + userToAdd.toString() + "'.");

    final Session session = sessionFactory.getCurrentSession();
    final List<User> existingUsers = queryUserByUsername(userToAdd.getUsername(), session);
    log.debug("~add() : '" + existingUsers.toString() + "'.");

    if (!existingUsers.isEmpty()) {
      throw new UsernameAlreadyExistsException("A user '" + userToAdd.toString() + "' already exists!");
    }

    userToAdd.addAuthority(authorityUser);
    userToAdd.setPassword(passwordEncoder.encode(userToAdd.getPassword()));

    session.save(userToAdd);
    session.flush();

    return userToAdd;
  }

  /* (non-Javadoc)
   * @see uk.ac.ox.cs.science2020.zoon.client.dao.UserDAO#addIdentity(java.lang.String, uk.ac.ox.cs.science2020.zoon.client.entity.Identity)
   */
  @Override
  public ActionOutcomeVO addIdentity(final String userName, final Identity newIdentity) {
    log.debug("~addIdentity() : Invoked with '" + userName + "', '" + newIdentity + "'.");

    ActionOutcomeVO outcome = null;

    final Session session = sessionFactory.getCurrentSession();
    final List<User> existingUsers = queryUserByUsername(userName, session);
    if (existingUsers.size() == 1) {
      final User user = existingUsers.get(0);
      final Set<Identity> existingIdentities = user.getIdentities();

      if (existingIdentities.contains(newIdentity)) {
        outcome = new ActionOutcomeVO(false, "Identity already exists for this user");
      } else {
        user.addIdentity(newIdentity);

        session.update(user);
        session.flush();

        outcome = new ActionOutcomeVO(true, "Updated with new identity");
      }
    } else {
      outcome = new ActionOutcomeVO(false, "More than one user has the username '" + userName + "'");
    }

    return outcome;
  }

  /* (non-Javadoc)
   * @see uk.ac.ox.cs.science2020.zoon.client.dao.UserDAO#delete(java.lang.String)
   */
  @Override
  public boolean delete(final String userName) {
    log.debug("~delete() : Invoked for '" + userName + "'.");

    final Session session = sessionFactory.getCurrentSession();
    final List<User> existingUsers = queryUserByUsername(userName, session);
    final int orig_size = existingUsers.size();
    if (orig_size == 1) {
      final User existingUser = existingUsers.get(0);
      if (!existingUser.getAuthorities().contains(authorityAdministrator)) {
        for (final Authority authority : existingUser.getAuthorities()) {
          log.debug("~delete() : Deleting '" + authority.toString() + "'.");
          session.delete(authority);
        }
        for (final Identity identity : existingUser.getIdentities()) {
          log.debug("~delete() : Deleting '" + identity.toString() + "'.");
          session.delete(identity);
        }
        log.debug("~delete() : Deleting '" + existingUser.toString() + "'.");
        session.delete(existingUser);
      }
    } else {
      log.warn("~delete() : There were '" + orig_size + "' users with user name '" + userName + "'!!");
    }
    session.flush();

    return (orig_size == (retrieveUsersByUsername(userName).size() + 1));
  }

  private List<User> queryUserByUsername(final String userName, final Session session) {
    final Query query = session.getNamedQuery(User.QUERY_USERS_BY_USERNAME);
    query.setParameter(User.PROPERTY_USERNAME, userName);

    log.debug("~queryUserByUsername() : Before!");
    @SuppressWarnings("unchecked")
    final List<User> existingUsers = query.list();
    log.debug("~queryUserByUsername() : After!");
    for (final User existingUser : existingUsers) {
      log.debug("~queryUserByUsername() : Existing user '" + existingUser + "'.");
      for (final Identity identityEntity : existingUser.getIdentities()) {
        // Override lazy load
        identityEntity.getEmails().size();
      }
    }
    return existingUsers;
  }

  /* (non-Javadoc)
   * @see uk.ac.ox.cs.science2020.zoon.client.dao.UserDAO#retrieveNonAdminUserByIdentity(java.lang.String)
   */
  @Override
  public UserDetails retrieveNonAdminUserByIdentity(final String identity) {
    log.debug("~retrieveNonAdminUserByIdentity() : Invoked for '" + identity + "'.");

    final Session session = sessionFactory.getCurrentSession();
    final Query query = session.getNamedQuery(User.QUERY_USERS_BY_IDENTITY);
    query.setParameter(Identity.PROPERTY_IDENTITY, identity);

    @SuppressWarnings("unchecked")
    final List<User> existingUsers = query.list();

    UserDetails user = null;
    if (existingUsers.size() == 1) {
      final User provisionalUser = existingUsers.get(0);
      if (!provisionalUser.getAuthorities().contains(authorityAdministrator)) {
        for (final Identity identityEntity : provisionalUser.getIdentities()) {
          // Override lazy load
          identityEntity.getEmails().size();
        }
        user = new UserDetails(provisionalUser); 
        log.debug("~retrieveNonAdminUserByIdentity() : Retrieved '" + user.toString() + "'.");
      }
    }

    // session.flush();
    return user;
  }

  /* (non-Javadoc)
   * @see uk.ac.ox.cs.science2020.zoon.client.dao.UserDAO#retrieveUser(java.lang.String)
   */
  @Override
  public UserDetails retrieveUser(final String name) {
    log.debug("~retrieveUser() : Invoked for '" + name + ".");

    final Session session = sessionFactory.getCurrentSession();
    final List<User> existingUsers = queryUserByUsername(name, session);
    log.debug("~retrieveUser() : '" + existingUsers.toString() + "'.");

    UserDetails userDetails = null;
    if (existingUsers.size() == 1) {
      userDetails = new UserDetails(existingUsers.get(0));
    }

    // session.flush();
    return userDetails;
  }

  /* (non-Javadoc)
   * @see uk.ac.ox.cs.science2020.zoon.client.dao.UserDAO#retrieveUsers()
   */
  @Override
  public List<UserDetails> retrieveUsers() {
    log.debug("~retrieveUsers() : Invoked.");

    final Session session = sessionFactory.getCurrentSession();
    final Query query = session.getNamedQuery(User.QUERY_RETRIEVE_ALL);

    @SuppressWarnings("unchecked")
    final List<User> existingUsers = query.list();

    return transferToUserDetails(existingUsers);
  }

  /* (non-Javadoc)
   * @see uk.ac.ox.cs.science2020.zoon.client.dao.UserDAO#retrieveUsersByUsername(java.lang.String)
   */
  @Override
  public List<UserDetails> retrieveUsersByUsername(final String userName) {
    log.debug("~retrieveUsersByUsername() : Invoked for userName '" + userName + "'.");

    final Session session = sessionFactory.getCurrentSession();
    final List<User> existingUsers = queryUserByUsername(userName, session);
    log.debug("~retrieveUsersByUsername() : Found '" + existingUsers.size() + "'.");
    final List<UserDetails> users = transferToUserDetails(existingUsers);
    log.debug("~retrieveUsersByUsername() : Transferred '" + users.size() + "'.");

    return users;
  }

  /* (non-Javadoc)
   * @see uk.ac.ox.cs.science2020.zoon.client.dao.UserDAO#setEnabled(java.lang.String, boolean)
   */
  @Override
  public void setEnabled(String name, boolean enabled) {
    log.debug("~setEnabled() : Setting '" + name + "' to '" + enabled + "'.");

    final Session session = sessionFactory.getCurrentSession();
    final List<User> existingUsers = queryUserByUsername(name, session);
    log.debug("~setEnabled() : '" + existingUsers.toString() + "'.");

    if (existingUsers.size() == 1) {
      final User user = existingUsers.get(0);
      if ((user.getEnabled() == User.DISABLED && enabled) ||
          (user.getEnabled() == User.ENABLED && !enabled)) {
        user.setEnabled(enabled ? User.ENABLED : User.DISABLED);
        session.save(user);
      }
    }
    session.flush();
  }

  private List<UserDetails> transferToUserDetails(final List<User> existingUsers) {
    final List<UserDetails> users = new ArrayList<UserDetails>(existingUsers.size());

    for (final User user : existingUsers) {
      // Overcome lazy loading.
      for (final Identity identity : user.getIdentities()) {
        identity.getEmails().size();
      }
      users.add(new UserDetails(user));
    }

    return users;
  }
}