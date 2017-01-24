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
package uk.ac.ox.cs.science2020.zoon.client.controller;

import java.util.LinkedList;
import java.util.Locale;
import java.util.Queue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.NoSuchMessageException;

/**
 * Abstract class allowing message source awareness to controllers.
 *
 * @author geoff
 */
public abstract class AbstractMessageSourceAwareController implements MessageSourceAware {

  // Spring-assigned
  private MessageSource messageSource;
  private static final int MAX_UNTRANSLATED = 100;
  private static final Queue<Object> untranslated = new LinkedList<Object>();

  private static final Log log = LogFactory.getLog(AbstractMessageSourceAwareController.class);

  /**
   * Query the message source.
   * 
   * @param messageKey Message key / Bundle identifier.
   * @param args Arguments to place into the translated text (if it exists).
   * @param locale Locale.
   * @return Translated text (or original message key if not translated).
   */
  protected String queryMessageSource(final String messageKey, final Object[] args,
                                      final Locale locale) {
    Object[] useArgs = null;
    if (args != null && args.length > 0) {

      useArgs = new Object[args.length];

      for (int argIdx = 0; argIdx < args.length; argIdx++) {
        final Object thisObject = args[argIdx];
        final String thisArg = thisObject.toString();
        log.debug("~queryMessageSource() : Checking arg '" + thisArg + "'.");
        final String argText = queryMessageSource(thisArg, null, locale);
        if (!thisArg.equals(argText)) {
          useArgs[argIdx] = argText;
        } else {
          useArgs[argIdx] = thisObject;
        }
      }
    }

    String useText = messageKey;
    // avoid unnecessary queries of the message source
    if (untranslated.contains(messageKey)) {
      log.trace("~queryMessageSource() : '" + messageKey + "' already untranslated... ignoring.");
    } else {
      try {
        useText = messageSource.getMessage(messageKey, useArgs, locale);
      } catch (NoSuchMessageException e) {
        if (untranslated.size() > MAX_UNTRANSLATED) {
          final Object evicted = untranslated.remove();
          log.debug("~queryMessageSource() : Popping '" + evicted.toString() + "' from stack.");
        }
        log.debug("~queryMessageSource() : Pushing '" + messageKey + "' to untranslated stack.");
        untranslated.add(messageKey);
      }
    }
    return useText;
  }

  /* (non-Javadoc)
   * @see org.springframework.context.MessageSourceAware#setMessageSource(org.springframework.context.MessageSource)
   */
  public void setMessageSource(final MessageSource messageSource) {
    this.messageSource = messageSource;
  }
}