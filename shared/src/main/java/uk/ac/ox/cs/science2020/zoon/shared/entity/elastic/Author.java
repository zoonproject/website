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
package uk.ac.ox.cs.science2020.zoon.shared.entity.elastic;

import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldIndex;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * Artifact author.
 *
 * @author geoff
 */
public class Author {

  @Field(type=FieldType.String, index=FieldIndex.not_analyzed, store=true)
  private String authorName;
  private String email;

  /**
   * Default constructor.
   */
  protected Author() {}

  /**
   * Initialising constructor.
   * 
   * @param authorName Name.
   * @param email email.
   */
  public Author(final String authorName, final String email) {
    setAuthorName(authorName);
    setEmail(email);
  }

  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "Author [authorName=" + authorName + ", email=" + email + "]";
  }

  /**
   * Retrieve the author name.
   * 
   * @return Author name.
   */
  public String getAuthorName() {
    return authorName;
  }

  /**
   * Assign the author name.
   * 
   * @param authorName Name to assign.
   */
  public void setAuthorName(final String authorName) {
    this.authorName = authorName;
  }

  /**
   * Retrieve the author email.
   * 
   * @return Author email.
   */
  public String getEmail() {
    return email;
  }

  /**
   * Assign the author email.
   * 
   * @param email Email to assign.
   */
  public void setEmail(final String email) {
    this.email = email;
  }
}