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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.egit.github.core.Blob;
import org.eclipse.egit.github.core.Commit;
import org.eclipse.egit.github.core.CommitUser;
import org.eclipse.egit.github.core.Reference;
import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.RepositoryBranch;
import org.eclipse.egit.github.core.RepositoryCommit;
import org.eclipse.egit.github.core.RepositoryContents;
import org.eclipse.egit.github.core.RepositoryId;
import org.eclipse.egit.github.core.Tree;
import org.eclipse.egit.github.core.TreeEntry;
import org.eclipse.egit.github.core.TypedResource;
import org.eclipse.egit.github.core.User;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.CommitService;
import org.eclipse.egit.github.core.service.ContentsService;
import org.eclipse.egit.github.core.service.DataService;
import org.eclipse.egit.github.core.service.RepositoryService;
import org.eclipse.egit.github.core.service.UserService;
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
public class GitHubVO implements Store {

  private static final String defaultName = "GitHub";

  private final String credentialsUser;
  private final String credentialsPassword;
  private final String repositoryOwner;
  private final String repositoryName;
  private final String repositoryBranch;
  private final String contentPath;
  private final String storeName;
  private final boolean hasPublicArtifacts;
  private final boolean temporaryStore;
  private final boolean zoonStore;

  @Autowired
  private ModulesParsingGateway modulesParsingGateway;

  // Spring-injected.
  @Value("${modules.initial_load_count}")
  private int initialLoadCount;

  private static final Log log = LogFactory.getLog(GitHubVO.class);

  /**
   * Initialising constructor.
   * 
   * @param credentialsUser
   * @param credentialsPassword
   * @param repositoryOwner
   * @param repositoryName
   * @param contentPath Content path (optional).
   * @param storeName Illustrative name, e.g. ZOON repository (optional).
   * @param hasPublicArtifacts {@code true} if store contains public artifacts, otherwise {@code false}
   * @param temporaryStore
   * @param zoonStore Indicator if this is the principal ZOON repository.
   * @throws IllegalArgumentException If {@code null} or blank values received for some parameters.
   */
  protected GitHubVO(final String credentialsUser, final String credentialsPassword,
                     final String repositoryOwner, final String repositoryName,
                     final String repositoryBranch,
                     final String contentPath, final String storeName,
                     final boolean hasPublicArtifacts, final boolean temporaryStore,
                     final boolean zoonStore) {
    if (StringUtils.isBlank(credentialsUser) || StringUtils.isBlank(credentialsPassword) ||
        StringUtils.isBlank(repositoryOwner) || StringUtils.isBlank(repositoryName) ||
        StringUtils.isBlank(repositoryBranch)) {
      throw new IllegalArgumentException("GitHub assignment requires at least user,password, repos owner,name and branch");
    }
    this.credentialsUser = credentialsUser;
    this.credentialsPassword = credentialsPassword;
    this.repositoryOwner = repositoryOwner;
    this.repositoryName = repositoryName;
    this.repositoryBranch = repositoryBranch;
    this.contentPath = contentPath;
    this.storeName = StringUtils.isBlank(storeName) ? defaultName : storeName;
    this.hasPublicArtifacts = hasPublicArtifacts;
    this.temporaryStore = temporaryStore;
    this.zoonStore = zoonStore;

    if (repositoryBranch.equals("master")) {
      log.warn("~GitHubVO() : You do realise that you're writing to the GitHub master branch don't you????!!!!!");
    }
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
    if (!hasPublicArtifacts()) {
      return new HashMap<String, List<Map<DataIdentifier, HeaderObject>>>();
    }

    log.debug("~loadPublicModulesOnStartup() : Invoked.");
    // Query GitHub!
    final GitHubClient gitHubClient = new GitHubClient();
    gitHubClient.setCredentials(credentialsUser, credentialsPassword);

    final RepositoryService repositoryService = new RepositoryService(gitHubClient);
    Repository repository = null;
    try { 
      repository = repositoryService.getRepository(repositoryOwner, repositoryName);
    } catch (IOException e) {
      e.printStackTrace();
      log.error("~loadPublicModulesOnStartup() : getRepository " + e.getMessage() + "'.");
    }

    final Map<String, List<Map<DataIdentifier, HeaderObject>>> retrievedArtifacts = new HashMap<String, List<Map<DataIdentifier, HeaderObject>>>();

    final List<String> artifactNames = new ArrayList<String>();
    if (repository != null) {
      log.debug("~loadPublicModulesOnStartup() : Repository found!");
      final ContentsService contentsService = new ContentsService(gitHubClient);
      List<RepositoryContents> contents = null;

      try {
        contents = contentsService.getContents(repository,
                                               contentPath,
                                               repositoryBranch);
      } catch (IOException e) {
        e.printStackTrace();
        log.error("~loadPublicModulesOnStartup() : getContents " + e.getMessage() + "'.");
      }

      if (contents != null) {
        log.debug("~loadPublicModulesOnStartup() : Content found!");
        for (final RepositoryContents content : contents) {
          final String contentPath = content.getPath();
          artifactNames.add(contentPath);
          log.debug("~loadPublicModulesOnStartup() : content @ '" + contentPath + "'.");
        }
      }

      // Hold artifact content in a temporary store so we minimise talk-time with repository. 
      final Set<ParseCandidate> modulesForParsing = new HashSet<ParseCandidate>();
      if (!artifactNames.isEmpty()) {
        int artifactCount = 0;
        for (final String artifactPath : artifactNames) {
          if (++artifactCount > initialLoadCount) {
            log.debug("~loadPublicModulesOnStartup() : Only loading '" + initialLoadCount + "' of '" + artifactNames.size() + "' modules!");
            break;
          }

          String artifactContent = null;
          try {
            contents = contentsService.getContents(repository, artifactPath);
          } catch (IOException e) {
            e.printStackTrace();
            log.error("~loadPublicModulesOnStartup() : getContents " + e.getMessage() + "'.");
          }

          if (contents != null) {
            log.debug("~loadPublicModulesOnStartup() : Content found!");
            for (final RepositoryContents content : contents) {
              artifactContent = new String(Base64.decodeBase64(content.getContent().getBytes()));
              if (artifactContent != null) {
                modulesForParsing.add(new ParseCandidate(content.getName(), null, artifactContent));
              }
            }
          }
        }
      }

      final Set<ParseCandidate> parseResults = modulesParsingGateway.runModulesParsing(modulesForParsing);

      for (final ParseCandidate parseResult : parseResults) {
        final String artifactName = parseResult.getArtifactName();
        log.debug("~loadPublicModulesOnStartup() : Parse result '" + parseResult.toString() + "'.");
        if (parseResult.retrieveResultJSON() == null) {
          log.error("~loadPublicModulesOnStartup() : No JSON generated for '" + artifactName + "'.");
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
          log.error("~loadPublicModulesOnStartup() : '" + parseResult.retrieveResultJSON() + "'.");
          log.error("~loadPublicModulesOnStartup() : '" + parseResult.getArtifactContent()  + "'.");
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
    final String errorMessage = "Retrieval of private modules from '" + getName() + "' not yet implemented!";
    log.error("~retrievePrivateModules() : ".concat(errorMessage));
    throw new UnsupportedOperationException(errorMessage);
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
    log.debug("~storePermanentArtifact() : Invoked.");

    final GitHubClient client = new GitHubClient();
    client.setCredentials(credentialsUser, credentialsPassword);

    final RepositoryService repositoryService = new RepositoryService(client);
    Repository repository = null;
    try { 
      repository = repositoryService.getRepository(repositoryOwner, repositoryName);
    } catch (IOException e) {
      e.printStackTrace();
      final String errorMessage = "Could not find repository '" + repositoryName + "' for owner '" + repositoryOwner + "'";
      log.error("~storePermanentArtifact() : ".concat(errorMessage));
      return new ActionOutcomeVO(false, errorMessage);
    }

    final DataService dataService = new DataService(client);

    // get some sha's from current state in git
    String baseCommitSha = null;
    try {
      for (final RepositoryBranch eachBranch : repositoryService.getBranches(repository)) {
        if (repositoryBranch.equals(eachBranch.getName())) {
          baseCommitSha = eachBranch.getCommit().getSha();
          break;
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
      final String errorMessage = "Could not retrieve the base commit SHA";
      log.error("~storePermanentArtifact() : ".concat(errorMessage));
      return new ActionOutcomeVO(false, errorMessage);
    }

    if (baseCommitSha == null) {
      final String errorMessage = "Could not determine the base commit SHA for the branch '" + repositoryBranch + "'";
      log.error(errorMessage);
      return new ActionOutcomeVO(false, errorMessage);
    }

    final CommitService commitService = new CommitService(client);
    RepositoryCommit baseCommit;
    try {
      baseCommit = commitService.getCommit(repository, baseCommitSha);
    } catch (IOException e) {
      e.printStackTrace();
      final String errorMessage = "Could not retrieve commit with SHA '" + baseCommitSha + "'";
      log.error("~storePermanentArtifact() : ".concat(errorMessage));
      return new ActionOutcomeVO(false, errorMessage);
    }

    final String blobContent = content;
    final String path = contentPath.concat("/").concat(artifactName).concat(".R");

    final Blob blob = new Blob();
    blob.setContent(blobContent).setEncoding(Blob.ENCODING_UTF8);
    String blob_sha;
    try {
      blob_sha = dataService.createBlob(repository, blob);
    } catch (IOException e) {
      e.printStackTrace();
      final String errorMessage = "Could not create GitHub data service Blob object";
      log.error("~storePermanentArtifact() : ".concat(errorMessage));
      return new ActionOutcomeVO(false, errorMessage);
    }

    final String treeSha = baseCommit.getSha();
    Tree baseTree;
    try {
      baseTree = dataService.getTree(repository, treeSha);
    } catch (IOException e) {
      e.printStackTrace();
      final String errorMessage = "Could not create GitHub Tree with SHA-1 of '" + treeSha + "'";
      log.error("~storePermanentArtifact() : ".concat(errorMessage));
      return new ActionOutcomeVO(false, errorMessage);
    }

    final TreeEntry treeEntry = new TreeEntry();
    treeEntry.setPath(path);
    treeEntry.setMode(TreeEntry.MODE_BLOB);
    treeEntry.setType(TreeEntry.TYPE_BLOB);
    treeEntry.setSha(blob_sha);
    treeEntry.setSize(blob.getContent().length());
    final Collection<TreeEntry> entries = new ArrayList<TreeEntry>();
    entries.add(treeEntry);

    Tree newTree;
    try {
      newTree = dataService.createTree(repository, entries, baseTree.getSha());
    } catch (IOException e) {
      e.printStackTrace();
      final String errorMessage = "GitHub data service could not create tree";
      log.error("~storePermanentArtifact() : ".concat(errorMessage));
      return new ActionOutcomeVO(false, errorMessage);
    }

    final Commit commit = new Commit();
    commit.setMessage("Upload from ZOON web interface");
    commit.setTree(newTree);

    final UserService userService = new UserService(client);
    User user;
    try {
      user = userService.getUser();
    } catch (IOException e) {
      e.printStackTrace();
      final String errorMessage = "GitHub user service couldn't retrieve the user";
      log.error("~storePermanentArtifact() : ".concat(errorMessage));
      return new ActionOutcomeVO(false, errorMessage);
    }

    final CommitUser author = new CommitUser();
    //author.setName(user.getName());
    author.setName("Geoff Williams");
    try {
      author.setEmail(userService.getEmails().get(0));
    } catch (IOException e) {
      e.printStackTrace();
      final String errorMessage = "GitHub user service couldn't retrieve the user '" + author.getName() + "' emails";
      log.error("~storePermanentArtifact() : ".concat(errorMessage));
      return new ActionOutcomeVO(false, errorMessage);
    }
    author.setDate(Calendar.getInstance().getTime());

    commit.setAuthor(author);
    commit.setCommitter(author);

    final List<Commit> listOfCommits = new ArrayList<Commit>();
    listOfCommits.add(new Commit().setSha(baseCommitSha));
    commit.setParents(listOfCommits);
    Commit newCommit;

    RepositoryId repo = RepositoryId.create(repositoryOwner, repositoryName);
    try {
      newCommit = dataService.createCommit(repo, commit);
    } catch (IOException e) {
      e.printStackTrace();
      final String errorMessage = "GitHub data service couldn't create a commit";
      log.error("~storePermanentArtifact() : ".concat(errorMessage));
      return new ActionOutcomeVO(false, errorMessage);
    }

    final TypedResource commitResource = new TypedResource();
    commitResource.setSha(newCommit.getSha());
    commitResource.setType(TypedResource.TYPE_COMMIT);
    commitResource.setUrl(newCommit.getUrl());

    Reference reference;
    try {
      reference = dataService.getReference(repo, "heads/".concat(repositoryBranch));
    } catch (IOException e) {
      e.printStackTrace();
      final String errorMessage = "GitHub data service couldn't get a reference for repository heads/master";
      log.error("~storePermanentArtifact() : ".concat(errorMessage));
      return new ActionOutcomeVO(false, errorMessage);
    }
    reference.setObject(commitResource);
    try {
      dataService.editReference(repo, reference, true);
    } catch (IOException e) {
      e.printStackTrace();
      final String errorMessage = "GitHub data service couldn't edit the reference";
      log.error("~storePermanentArtifact() : ".concat(errorMessage));
      return new ActionOutcomeVO(false, errorMessage);
    }

    return new ActionOutcomeVO(true, "Stored!");
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
    return "GitHubVO [credentialsUser=" + credentialsUser
        + ", repositoryOwner=" + repositoryOwner + ", repositoryName="
        + repositoryName + ", repositoryBranch=" + repositoryBranch
        + ", contentPath=" + contentPath + ", storeName=" + storeName
        + ", hasPublicArtifacts=" + hasPublicArtifacts + ", temporaryStore="
        + temporaryStore + ", zoonStore=" + zoonStore + "]";
  }
}
