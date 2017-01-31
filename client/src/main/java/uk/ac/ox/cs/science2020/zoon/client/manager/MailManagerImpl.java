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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Component;

import uk.ac.ox.cs.science2020.zoon.client.ClientIdentifiers;

/**
 * Implementation of the mail management operations.
 *
 * @author geoff
 */
@Component(ClientIdentifiers.COMPONENT_MAIL_MANAGER)
public class MailManagerImpl implements MailManager {

  @Autowired @Qualifier(ClientIdentifiers.COMPONENT_MAIL_SENDER)
  private MailSender mailSender;

  @Autowired @Qualifier(ClientIdentifiers.COMPONENT_MAIL_REGISTRATION_TEMPLATE)
  private SimpleMailMessage templateRegistrationMail;

  private static final Log log = LogFactory.getLog(MailManagerImpl.class);

  /* (non-Javadoc)
   * @see uk.ac.ox.cs.science2020.zoon.client.manager.MailManager#sendEmail(java.lang.String, java.lang.String, java.lang.String, uk.ac.ox.cs.science2020.zoon.client.manager.MailManager.EMAIL_TYPE)
   */
  @Override
  public void sendEmail(final String identityName, final String email, final String userName,
                        final EMAIL_TYPE emailType) throws MailException {
    log.debug("~sendEmail() : Invoked for type '" + emailType + "'.");

    switch (emailType) {
      case REGISTRATION :
        final SimpleMailMessage registrationMail = new SimpleMailMessage(templateRegistrationMail);
        registrationMail.setText("Registration. Identity '" + identityName  + "', Email '" + email +
                                 "', userName '" + userName + "'.");
        try {
          mailSender.send(registrationMail);
        } catch (Exception e) {
          // TODO : Implement failure to send registration email handling.
          log.error("~sendEmail() : Failure to send registration email due to '" + e.getMessage() + "'.");
        }

        break;
      default :
        log.error("~sendEmail() : Unrecognized email type of '" + emailType + "' requested.");
        break;
    }
  }
}