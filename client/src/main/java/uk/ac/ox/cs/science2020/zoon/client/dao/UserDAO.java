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
package uk.ac.ox.cs.science2020.zoon.client.dao;

import java.util.List;

import uk.ac.ox.cs.science2020.zoon.client.entity.Identity;
import uk.ac.ox.cs.science2020.zoon.client.entity.User;
import uk.ac.ox.cs.science2020.zoon.client.exception.UsernameAlreadyExistsException;
import uk.ac.ox.cs.science2020.zoon.client.value.object.user.UserDetails;
import uk.ac.ox.cs.science2020.zoon.shared.value.object.ActionOutcomeVO;

/**
 * User management DAO.
 *
 * @author geoff
 */
public interface UserDAO {

  /**
   * Add a user.
   * 
   * @param user New user.
   * @return Added user.
   * @throws UsernameAlreadyExistsException If username already exists.
   */
  User add(User user) throws UsernameAlreadyExistsException;

  /**
   * Add the identity to the user.
   * 
   * @param userName Name of user to add identity to.
   * @param newIdentity New identity to add.
   * @return Outcome.
   */
  ActionOutcomeVO addIdentity(String userName, Identity newIdentity);

  /**
   * Delete a user.
   * 
   * @param userName User to delete.
   * @return {@code true} if deleted, otherwise {@code false}.
   */
  boolean delete(String userName);

  /**
   * Retrieve a registered non-admin user by identifier, e.g. ZOON Developers.
   * 
   * @param identity Identity.
   * @return User with specified identity, or {@code null} if not found.
   */
  UserDetails retrieveNonAdminUserByIdentity(String identity);

  /**
   * Retrieve registered user by username.
   * 
   * @param Username.
   * @return Registered user, or {@code null} if not found.
   */
  UserDetails retrieveUser(String name);

  /**
   * Retrieve all registered users.
   * 
   * @return All registered users, or empty collection if none found.
   */
  List<UserDetails> retrieveUsers();

  /**
   * Retrieve the collection of user identities based on their singular user name.
   * 
   * @param userName
   * @return
   */
  List<UserDetails> retrieveUsersByUsername(String userName);

  /**
   * Assign a user's enabled status.
   * 
   * @param name Username.
   * @param enabled Enabled status.
   */
  void setEnabled(String name, boolean enabled);
}