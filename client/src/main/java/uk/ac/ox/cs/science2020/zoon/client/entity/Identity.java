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
package uk.ac.ox.cs.science2020.zoon.client.entity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * Spring security identities.
 *
 * @author geoff
 */
@Entity
@Table(name="identities",
       uniqueConstraints=@UniqueConstraint(columnNames={Identity.PROPERTY_IDENTITY}))
// As per the META-INF/data/spring-security/local/(sample.)users.sql
public class Identity {

  public static final int MAX_LENGTH_EMAIL = 80;
  public static final int MAX_LENGTH_IDENTITY_NAME = 50;
  public static final int MIN_LENGTH_EMAIL = 5;
  public static final int MIN_LENGTH_IDENTITY_NAME = 5;

  public static final String PROPERTY_EMAIL = "email";
  public static final String PROPERTY_IDENTITY = "identity";

  @Id
  @GeneratedValue(strategy=GenerationType.AUTO)
  @Column
  private long id;

  @ManyToOne(fetch=FetchType.LAZY)
  @JoinColumn(name="username", nullable=false)
  private User user;

  @Column(name=PROPERTY_IDENTITY, length=MAX_LENGTH_IDENTITY_NAME)
  private String identity;

  @ElementCollection
  // , uniqueConstraints=@UniqueConstraint(columnNames = { PROPERTY_EMAIL })
  @CollectionTable(name=PROPERTY_EMAIL, joinColumns=@JoinColumn(name=PROPERTY_IDENTITY))
  @Column(name=PROPERTY_EMAIL, length=MAX_LENGTH_EMAIL)
  private Set<String> emails = new HashSet<String>();

  /**
   * Default constructor.
   */
  protected Identity() {};

  /**
   * Initialising constructor.
   * 
   * @param identity Identity, e.g. ZOON Developers.
   * @param emails Known email addresses for the identity.
   */
  public Identity(final String identity, final Set<String> emails) {
    this.identity = identity;
    if (emails != null) {
      this.emails = emails;
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

    if (getIdentity() == null || getIdentity().length() < MIN_LENGTH_IDENTITY_NAME) {
      invalidValues.add("ZOON identity name has minimum length of '" + MIN_LENGTH_IDENTITY_NAME + "'");
    } else if (getIdentity().length() > MAX_LENGTH_IDENTITY_NAME) {
      invalidValues.add("ZOON identity name has maximum length of '" + MAX_LENGTH_IDENTITY_NAME + "'");
    }
    for (final String email : emails) {
      if (email == null || email.length() < MIN_LENGTH_EMAIL) {
        invalidValues.add("Email has minimum length of '" + MIN_LENGTH_EMAIL + "'.");
      } else if (email.length() > MAX_LENGTH_EMAIL) {
        invalidValues.add("Email has maximum length of '" + MAX_LENGTH_EMAIL + "'.");
      }
    }

    return invalidValues;
  }

  /* (non-Javadoc)
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((identity == null) ? 0 : identity.hashCode());
    return result;
  }

  /* (non-Javadoc)
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Identity other = (Identity) obj;
    if (identity == null) {
      if (other.identity != null)
        return false;
    } else if (!identity.equals(other.identity))
      return false;
    return true;
  }

  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "Identity [id=" + id + ", identity=" + identity + ", emails=" + emails + "]";
  }

  public String getIdentity() {
    return identity;
  }

  public void setIdentity(String identity) {
    this.identity = identity;
  }

  public Set<String> getEmails() {
    return emails;
  }

  public void setEmails(Set<String> emails) {
    this.emails = emails;
  }

  /**
   * @param user the user to set
   */
  public void setUser(User user) {
    this.user = user;
  }
}