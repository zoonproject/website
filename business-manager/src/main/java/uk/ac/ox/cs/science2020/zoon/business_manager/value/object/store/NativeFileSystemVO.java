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
package uk.ac.ox.cs.science2020.zoon.business_manager.value.object.store;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import uk.ac.ox.cs.science2020.zoon.business_manager.business.artifact.Store;
import uk.ac.ox.cs.science2020.zoon.business_manager.business.artifact.util.DummyModuleProcessingUtil;
import uk.ac.ox.cs.science2020.zoon.business_manager.business.error.ModuleHeaderException;
import uk.ac.ox.cs.science2020.zoon.business_manager.business.parsing.modules.ModulesParsingGateway;
import uk.ac.ox.cs.science2020.zoon.business_manager.business.parsing.modules.ParseCandidate;
import uk.ac.ox.cs.science2020.zoon.business_manager.value.object.artifact.MinimumArtifactDataVO;
import uk.ac.ox.cs.science2020.zoon.business_manager.value.object.moduleheader.HeaderObject;
import uk.ac.ox.cs.science2020.zoon.shared.value.object.ActionOutcomeVO;
import uk.ac.ox.cs.science2020.zoon.shared.value.object.artifact.workflowcall.WorkflowCallVO;
import uk.ac.ox.cs.science2020.zoon.shared.value.type.ArtifactType;
import uk.ac.ox.cs.science2020.zoon.shared.value.type.DataIdentifier;

/**
 *
 *
 * @author geoff
 */
// appCtx.config.xml
public class NativeFileSystemVO implements Store {

  private static final String defaultName = "File system";

  private final String location;
  private final String storeName;
  private final boolean hasPublicArtifacts;
  private final boolean temporaryStore;
  private final boolean zoonStore;

  @Autowired
  private ModulesParsingGateway modulesParsingGateway;

  // Spring-injected.
  @Value("${modules.initial_load_count}")
  private int initialLoadCount;

  private static final Log log = LogFactory.getLog(NativeFileSystemVO.class);

  /**
   * Initialising constructor
   * 
   * @param location File system location.
   * @param storeName Illustrative name, e.g. ZOON repository (optional).
   * @param hasPublicArtifacts {@code true} if store contains public artifacts, otherwise {@code false}
   * @param temporaryStore True if users can write to the store, otherwise false.
   * @param zoonStore Indicator if this is the principal ZOON repository.
   */
  protected NativeFileSystemVO(final String location, final String storeName,
                               final boolean hasPublicArtifacts, final boolean temporaryStore,
                               final boolean zoonStore) {
    if (StringUtils.isBlank(location)) {
      throw new IllegalArgumentException("Native file system location cannot be a null/empty value!");
    }
    this.location = location;
    this.storeName = StringUtils.isEmpty(storeName) ? defaultName : storeName;
    this.hasPublicArtifacts = hasPublicArtifacts;
    this.temporaryStore = temporaryStore;
    this.zoonStore = zoonStore;
  }

  /* (non-Javadoc)
   * @see uk.ac.ox.cs.science2020.zoon.business_manager.business.artifact.Store#deleteArtifact(uk.ac.ox.cs.science2020.zoon.business_manager.value.object.artifact.MinimumArtifactDataVO)
   */
  @Override
  public ActionOutcomeVO deleteArtifact(final MinimumArtifactDataVO minimumArtifactData) {
    log.debug("~deleteArtifact() : Invoked.");

    throw new UnsupportedOperationException(getName() + " artifact deletion not currently implemented!");
  }

  /* (non-Javadoc)
   * @see uk.ac.ox.cs.science2020.zoon.business_manager.business.artifact.Store#getName()
   */
  @Override
  public String getName() {
    return storeName;
  }

  /* (non-Javadoc)
   * @see uk.ac.ox.cs.science2020.zoon.business_manager.business.artifact.Store#hasPublicArtifacts()
   */
  @Override
  public boolean hasPublicArtifacts() {
    return hasPublicArtifacts;
  }

  /* (non-Javadoc)
   * @see uk.ac.ox.cs.science2020.zoon.business_manager.business.artifact.Store#isTemporaryStore()
   */
  @Override
  public boolean isTemporaryStore() {
    return temporaryStore;
  }

  /* (non-Javadoc)
   * @see uk.ac.ox.cs.science2020.zoon.business_manager.business.artifact.Store#isZOONStore()
   */
  @Override
  public boolean isZOONStore() {
    return zoonStore;
  }

  /* (non-Javadoc)
   * @see uk.ac.ox.cs.science2020.zoon.business_manager.business.artifact.Store#loadPublicModulesOnStartup()
   */
  @Override
  public Map<String, List<Map<DataIdentifier, HeaderObject>>> loadPublicModulesOnStartup() {
    log.debug("~loadPublicModulesOnStartup() : Invoked.");
    if (!hasPublicArtifacts()) {
      return new HashMap<String, List<Map<DataIdentifier, HeaderObject>>>();
    }

    final String directoryName = location;
    final File directory = new File(directoryName);

    final Map<String, List<String>> artifactFileContent = new HashMap<String, List<String>>();

    if (directory.isDirectory()) {
      final String[] artifactFiles = directory.list();

      Scanner artifactFileScanner = null;
      int count = 0;
      for (final String artifactFileName : Arrays.asList(artifactFiles)) {
        if (++count > initialLoadCount) {
          log.warn("~loadPublicModulesOnStartup() : Preventing full load of more than '" + initialLoadCount + "' modules!");
          break;
        }
        final String artifactFilePath = directoryName.concat(artifactFileName);
        //log.debug("~retrieveArtifacts() : File '" + artifactFilePath + "'.");
        final File artifactFile = new File(artifactFilePath);

        try {
          artifactFileScanner = new Scanner(artifactFile);
        } catch (FileNotFoundException fileNotFoundException) {
          log.error("~loadPublicModulesOnStartup() : File '" + artifactFilePath + "' not found.");
        }

        final List<String> artifactLines = new ArrayList<String>();
        try {
          while (artifactFileScanner.hasNextLine()) {
            artifactLines.add(artifactFileScanner.nextLine());
          }
        } finally {
          artifactFileScanner.close();
        }
        log.debug("~loadPublicModulesOnStartup() : Loading in '" + artifactFileName + "'.");

        artifactFileContent.put(artifactFileName, artifactLines);
        artifactFileScanner = null;
      }
    } else {
      log.error("~loadPublicModulesOnStartup() : '" + directory.getAbsolutePath() + "' is not a directory!");
    }

    final Map<String, List<Map<DataIdentifier, HeaderObject>>> retrievedArtifacts = new HashMap<String, List<Map<DataIdentifier, HeaderObject>>>();

    if (!artifactFileContent.isEmpty()) {
      final Set<ParseCandidate> modulesForParsing = new HashSet<ParseCandidate>();

      for (final Map.Entry<String, List<String>> eachArtifactFileContent : artifactFileContent.entrySet()) {
        final String artifactFileName = eachArtifactFileContent.getKey();
        final List<String> artifactContentLines = eachArtifactFileContent.getValue();

        final String artifactContent = StringUtils.join(artifactContentLines, "\n");
        modulesForParsing.add(new ParseCandidate(artifactFileName, null, artifactContent));
      }

      final Set<ParseCandidate> parseResults = modulesParsingGateway.runModulesParsing(modulesForParsing);

      for (final ParseCandidate parseResult : parseResults) {
        log.debug("~loadPublicModulesOnStartup() : Parse result '" + parseResult.toString() + "'.");
        if (parseResult.retrieveResultJSON() == null) {
          log.error("~loadPublicModulesOnStartup() : No JSON generated!");
          log.error(parseResult);
          throw new UnsupportedOperationException("No results!");
        }

        try {
          DummyModuleProcessingUtil.aggregateModuleData(retrievedArtifacts, getName(),
                                                        parseResult.retrieveResultJSON(),
                                                        parseResult.getArtifactContent(),
                                                        parseResult.isResultVerified());
        } catch (ModuleHeaderException e) {
          // TODO : Properly handle exceptions
          //throw new UnsupportedOperationException("Artifact header problems : '" + e.getMessage() + "'?!");
        }
      }
    }

    return retrievedArtifacts;
  }

  /* (non-Javadoc)
   * @see uk.ac.ox.cs.science2020.zoon.business_manager.business.artifact.Store#retrieveArtifact(uk.ac.ox.cs.science2020.zoon.business_manager.value.object.artifact.MinimumArtifactDataVO)
   */
  @Override
  public String retrieveArtifact(final MinimumArtifactDataVO minimumArtifactData) {
    log.error("~retrieveArtifact() : Retrieval of artifact from '" + getName() + "' not yet implemented!");

    throw new UnsupportedOperationException("Operation not yet implemented");
  }

  /* (non-Javadoc)
   * @see uk.ac.ox.cs.science2020.zoon.business_manager.business.artifact.Store#retrievePrivateModules(int, boolean, boolean)
   */
  @Override
  public Map<String, List<Map<DataIdentifier, HeaderObject>>> retrievePrivateModules(final int userId,
                                                                                     final boolean minimal,
                                                                                     final boolean verifiedOnly) {
    log.error("~retrievePrivateModules() : Retrieval of private modules from '" + getName() + "' not yet implemented!");

    throw new UnsupportedOperationException("Operation not yet implemented");
  }

  /* (non-Javadoc)
   * @see uk.ac.ox.cs.science2020.zoon.business_manager.business.artifact.Store#retrievePrivateWorkflowCalls(int, boolean)
   */
  @Override
  public Set<WorkflowCallVO> retrievePrivateWorkflowCalls(final int userId, final boolean minimal) {
    log.debug("~retrievePrivateWorkflowCalls() : Invoked for '" + userId + "', minimal '" + minimal + "'.");

    throw new UnsupportedOperationException("Operation not yet implemented");
  }

  /* (non-Javadoc)
   * @see uk.ac.ox.cs.science2020.zoon.business_manager.business.artifact.Store#storePermanentArtifact(java.lang.String, java.lang.String, uk.ac.ox.cs.science2020.zoon.business_manager.value.type.ArtifactType)
   */
  @Override
  public ActionOutcomeVO storePermanentArtifact(final String artifactName, final String content,
                                                final ArtifactType type) {
    log.error("~storePermanentArtifact() : Invoked.");

    throw new UnsupportedOperationException("Operation not yet implemented");
  }

  /* (non-Javadoc)
   * @see uk.ac.ox.cs.science2020.zoon.business_manager.business.artifact.Store#storeTemporaryArtifact(uk.ac.ox.cs.science2020.zoon.business_manager.value.object.artifact.ArtifactStoreVO)
   */
  @Override
  public String storeTemporaryArtifact(final ArtifactStoreVO artifactStore) {
    log.error("~storeTemporaryArtifact() : Storing of modules in '" + getName() + "' not yet implemented!");

    throw new UnsupportedOperationException("Operation not yet implemented");
  }

  /* (non-Javadoc)
   * @see uk.ac.ox.cs.science2020.zoon.business_manager.business.artifact.Store#writeVerificationOutcome(uk.ac.ox.cs.science2020.zoon.business_manager.value.object.artifact.MinimumArtifactDataVO, boolean)
   */
  @Override
  public void writeVerificationOutcome(final MinimumArtifactDataVO minimumArtifactData,
                                       final boolean outcome) {
    log.debug("~writeVerificationOutcome() : Invoked.");

    throw new UnsupportedOperationException("Operation not yet implemented");
  }

  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "NativeFileSystemVO [location=" + location + ", storeName="
        + storeName + ", hasPublicArtifacts=" + hasPublicArtifacts
        + ", temporaryStore=" + temporaryStore + ", zoonStore=" + zoonStore
        + ", initialLoadCount=" + initialLoadCount + "]";
  }
}