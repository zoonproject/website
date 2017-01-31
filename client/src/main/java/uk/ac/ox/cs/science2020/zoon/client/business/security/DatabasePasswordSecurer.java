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
package uk.ac.ox.cs.science2020.zoon.client.business.security;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Bean used to encode plain text passwords on system startup.
 *
 * @author geoff
 */
public class DatabasePasswordSecurer extends JdbcDaoSupport {

  // Needs to match src/main/webapp/WEB-INF/spring/appCtx.root-security.xml and UserDAOImpl
  private static final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

  public static final String DEF_USERS = "select username,password from users";
  public static final String UPDATE_USERS = "update users set password = ? where username = ?";

  private static final Log log = LogFactory.getLog(DatabasePasswordSecurer.class);

  /**
   * Secure the plain text passwords.
   */
  public void encodePasswords() {
    getJdbcTemplate().query(DEF_USERS, new RowCallbackHandler() {
      @Override
      public void processRow(final ResultSet resultSet) throws SQLException {
        final String username = resultSet.getString(1);
        final String password = resultSet.getString(2);
        final String encodedPassword = passwordEncoder.encode(password);
        getJdbcTemplate().update(UPDATE_USERS, encodedPassword, username);
        log.info("~processRow() : User '" + username + "' has had password encoded.");
      }
    });
  }
}