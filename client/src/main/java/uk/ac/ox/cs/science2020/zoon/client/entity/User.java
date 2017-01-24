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
package uk.ac.ox.cs.science2020.zoon.client.entity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

/**
 * Spring security users.
 *
 * @author 
 */
@Entity
@NamedQueries({
  @NamedQuery(name=User.QUERY_RETRIEVE_ALL,
              query="SELECT user FROM User As user"),
  @NamedQuery(name=User.QUERY_USERS_BY_USERNAME,
              query="SELECT user FROM User AS user " +
                    "  WHERE " + User.PROPERTY_USERNAME + " = :" + User.PROPERTY_USERNAME),
  @NamedQuery(name=User.QUERY_USERS_BY_IDENTITY,
              query="SELECT user FROM User AS user " +
                    "  INNER JOIN user.identities identities " + 
                    "    WHERE identities." + Identity.PROPERTY_IDENTITY + " = :" + Identity.PROPERTY_IDENTITY)
})
@Table(name="users") // As per the META-INF/data/spring-security/local/initialize.*.sql
public class User {

  public static final int DISABLED = 0;
  public static final int ENABLED = 1;

  public static final int MAX_LENGTH_PASSWORD = 60;
  // User email being used as system username
  public static final int MAX_LENGTH_USER_NAME = Identity.MAX_LENGTH_EMAIL;
  public static final int MIN_LENGTH_PASSWORD = 5;
  public static final int MIN_LENGTH_USER_NAME = Identity.MIN_LENGTH_EMAIL;

  public static final String PROPERTY_USERNAME = "username";
  public static final String PROPERTY_IDENTITIES = "identities";
  public static final String QUERY_RETRIEVE_ALL = "user.queryRetrieveAll";
  public static final String QUERY_USERS_BY_IDENTITY = "user.queryUsersByIdentity";
  public static final String QUERY_USERS_BY_USERNAME = "user.queryUsersByUsername";

  @Id
  @Column(name=PROPERTY_USERNAME, unique=true, nullable=false, length=MAX_LENGTH_USER_NAME)
  private String username;

  @Column(name="password", nullable=false, length=MAX_LENGTH_PASSWORD)
  private String password;

  @Column(name="enabled", nullable=false)
  private int enabled;

  @OneToMany(fetch=FetchType.LAZY, mappedBy="user")
  @Cascade({CascadeType.SAVE_UPDATE})
  private Set<Authority> authorities = new HashSet<Authority>();

  @OneToMany(fetch=FetchType.LAZY, mappedBy="user")
  @Cascade({CascadeType.SAVE_UPDATE})
  private Set<Identity> identities = new HashSet<Identity>();

  /**
   * Default constructor.
   */
  protected User() {}

  /**
   * Initialising constructor.
   * 
   * @param identities ZOON identities.
   * @param username (System) user name. 
   * @param password Password.
   * @param enabled {@code 1} if enabled, otherwise {@code 0}.
   */
  public User(final Set<Identity> identities, final String username, final String password,
              final int enabled) {
    this.username = username;
    this.password = password;
    this.enabled = enabled;
    if (identities != null) {
      for (final Identity identity : identities) {
        identity.setUser(this);
      }
      this.identities = identities;
    }
  }

  /**
   * Indicator of presence of valid values in those assigned.
   * 
   * @param includeProperties Include the verification of object properties, e.g. identities.
   * @return Collection (empty if none found) of invalid values.
   */
  public List<String> hasValidValues(final boolean includeProperties) {
    final List<String> invalidValues = new ArrayList<String>();

    if (getUsername() == null || getUsername().length() < MIN_LENGTH_USER_NAME) {
      invalidValues.add("User name has minimum length of '" + MIN_LENGTH_USER_NAME + "'");
    } else if (getUsername().length() > MAX_LENGTH_USER_NAME) {
      invalidValues.add("User name has maximum length of '" + MAX_LENGTH_USER_NAME + "'");
    }
    if (getPassword() == null || getPassword().length() < MIN_LENGTH_PASSWORD) {
      invalidValues.add("Password has minimum length of '" + MIN_LENGTH_PASSWORD + "'");
    } else if (getPassword().length() > MAX_LENGTH_PASSWORD) {
      invalidValues.add("Password has maximum length of '" + MAX_LENGTH_PASSWORD + "'");
    }

    if (includeProperties) {
      for (final Identity identity : this.identities) {
        invalidValues.addAll(identity.hasValidValues(includeProperties));
      }
    }

    return invalidValues;
  }

  /**
   * Add an authority -- respecting bi-directional relationship.
   * 
   * @param authority Authority to add.
   */
  public void addAuthority(final Authority authority) {
    authority.setUser(this);
    this.authorities.add(authority);
  }

  /**
   * Add identities -- respecting bi-directional relationships.
   * 
   * @param newIdentities New identities to add.
   */
  public void addIdentities(final Set<Identity> newIdentities) {
    for (final Identity newIdentity : newIdentities) {
      addIdentity(newIdentity);
    }
  }

  /**
   * Add an identity -- respecting bi-directional relationship. 
   *
   * @param newIdentity New identity to add.
   */
  public void addIdentity(final Identity newIdentity) {
    newIdentity.setUser(this);
    getIdentities().add(newIdentity);
  }

  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "User [username=" + username + ", password="
        + password + ", enabled=" + enabled + ", identities=" + identities
        + ", authorities=" + authorities + "]";
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(final String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(final String password) {
    this.password = password;
  }

  public int getEnabled() {
    return enabled;
  }

  public void setEnabled(final int enabled) {
    assert (enabled == DISABLED || enabled == ENABLED) : "Illegal value '" + enabled + "' used in assigning user enabled flag!";

    this.enabled = enabled;
  }

  public Set<Identity> getIdentities() {
    return identities;
  }

  public void setIdentities(final Set<Identity> identities) {
    this.identities = identities; 
  }

  public Set<Authority> getAuthorities() {
    return authorities;
  }

  public void setAuthorities(final Set<Authority> authorities) {
    this.authorities = authorities;
  }
}