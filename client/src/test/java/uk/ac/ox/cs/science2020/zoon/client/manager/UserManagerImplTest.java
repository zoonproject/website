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
package uk.ac.ox.cs.science2020.zoon.client.manager;

import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.createStrictControl;
import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.easymock.Capture;
import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import uk.ac.ox.cs.science2020.zoon.client.dao.UserDAO;
import uk.ac.ox.cs.science2020.zoon.client.entity.User;
import uk.ac.ox.cs.science2020.zoon.client.exception.UsernameAlreadyExistsException;
import uk.ac.ox.cs.science2020.zoon.client.manager.UserManager;
import uk.ac.ox.cs.science2020.zoon.client.manager.UserManagerImpl;

/**
 *
 *
 * @author geoff
 */
public class UserManagerImplTest {

  private IMocksControl mocksControl;
  private UserDAO mockUserDAO;
  private UserManager userManager;

  @Before
  public void setUp() {
    userManager = new UserManagerImpl();
    mocksControl = createStrictControl();

    mockUserDAO = mocksControl.createMock(UserDAO.class);
    ReflectionTestUtils.setField(userManager, "userDAO", mockUserDAO);
  }

  @Test
  public void testRegisterUser() throws UsernameAlreadyExistsException {
    String dummyIdentity = null;
    String dummyEmail = null;
    String dummyUserName = null;
    String dummyPassword = null;

    String information = userManager.registerUser(dummyIdentity, dummyEmail, dummyUserName,
                                                  dummyPassword);

    assertTrue(information.startsWith(UserManagerImpl.REGISTRATION_FAILED_RESPONSE));

    // Test a bad email
    dummyIdentity = "dummyIdentity";
    dummyUserName = "dummyUserName";
    dummyPassword = "dummyPassword";
    dummyEmail = "dummy@email.com";

    Capture<User> capturedUser = new Capture<User>();
    expect(mockUserDAO.add(capture(capturedUser))).andReturn(null);

    mocksControl.replay();

    information = userManager.registerUser(dummyIdentity, dummyEmail, dummyUserName, dummyPassword);

    mocksControl.verify();

    assertFalse(UserManagerImpl.REGISTRATION_FAILED_RESPONSE.equals(information));
    User newUser = (User) capturedUser.getValue();
    assertTrue(dummyUserName.equals(newUser.getUsername()));
    assertTrue(dummyPassword.equals(newUser.getPassword()));
    assertSame(0, newUser.getEnabled());

    mocksControl.reset();
  }
}