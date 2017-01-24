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
package uk.ac.ox.cs.science2020.zoon.business_manager.business.config;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import uk.ac.ox.cs.science2020.zoon.business_manager.BusinessIdentifiers;
import uk.ac.ox.cs.science2020.zoon.business_manager.business.artifact.Store;

/**
 * Manage the configuration options.
 *
 * @author geoff
 */
//Values declared in config/appCtx.config.xml
@Component(BusinessIdentifiers.COMPONENT_CONFIGURATION)
public class Configuration {

  @Autowired
  private Set<Store> artifactStores = new HashSet<Store>();

  @Value("${modules.preload_test}")
  private Boolean preloadTest;

  private Store temporaryStore;
  private Store zoonStore;

  private static final Log log = LogFactory.getLog(Configuration.class);

  /**
   * Retrieve the collection of artifact stores.
   * 
   * @return Unmodifiable collection of artifact stores, or empty collection if none defined.
   */
  public Set<Store> getArtifactStores() {
    return Collections.unmodifiableSet(artifactStores);
  }

  /**
   * Retrieve the temporary artifact store.
   * 
   * @return The writable temporary artifact store, or {@code null} if not defined.
   */
  public Store getTemporaryStore() {
    return temporaryStore;
  }

  /**
   * Retrieve the ZOON repository.
   * 
   * @return The ZOON repository, or {@code null} if not defined.
   */
  public Store getZOONStore() {
    return zoonStore;
  }

  /**
   * @param store the store to set
   */
  @Autowired(required=true)
  public void setArtifactStores(final Set<Store> artifactStores) {
    if (artifactStores == null || artifactStores.isEmpty()) {
      throw new IllegalStateException("At least one artifact store must be defined");
    }
    for (final Store artifactStore : artifactStores) {
      log.info("~postConstruct() : Store '" + artifactStore.toString() + "'.");
      if (artifactStore.isTemporaryStore()) {
        if (temporaryStore != null) {
          throw new IllegalStateException("Only one artifact store can be designated as the temporary artifact store");
        }
        temporaryStore = artifactStore;
      }
      if (artifactStore.isZOONStore()) {
        if (zoonStore != null) {
          throw new IllegalStateException("Only one ZOON store can be designated");
        }
        zoonStore = artifactStore;
      }
    }

    this.artifactStores.addAll(artifactStores);
  }
}