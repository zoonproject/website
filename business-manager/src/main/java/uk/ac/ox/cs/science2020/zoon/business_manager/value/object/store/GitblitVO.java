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
package uk.ac.ox.cs.science2020.zoon.business_manager.value.object.store;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.map.MultiKeyMap;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.PushCommand;
import org.eclipse.jgit.api.errors.AbortedByHookException;
import org.eclipse.jgit.api.errors.ConcurrentRefUpdateException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.NoFilepatternException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.api.errors.NoMessageException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.api.errors.UnmergedPathsException;
import org.eclipse.jgit.api.errors.WrongRepositoryStateException;
import org.eclipse.jgit.dircache.DirCache;
import org.eclipse.jgit.errors.NoWorkTreeException;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.transport.PushResult;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.treewalk.filter.PathFilter;
import org.eclipse.jgit.util.FileUtils;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;

import com.gitblit.Constants.AccessRestrictionType;
import com.gitblit.Constants.AuthorizationControl;
import com.gitblit.client.GitblitClient;
import com.gitblit.client.GitblitRegistration;
import com.gitblit.models.RepositoryModel;
import com.gitblit.utils.RpcUtils;

import uk.ac.ox.cs.science2020.zoon.business_manager.business.artifact.Store;
import uk.ac.ox.cs.science2020.zoon.business_manager.business.artifact.util.ArtifactUtil;
import uk.ac.ox.cs.science2020.zoon.business_manager.business.artifact.util.DummyModuleProcessingUtil;
import uk.ac.ox.cs.science2020.zoon.business_manager.business.parsing.modules.ModulesParsingGateway;
import uk.ac.ox.cs.science2020.zoon.business_manager.business.parsing.modules.ParseCandidate;
import uk.ac.ox.cs.science2020.zoon.business_manager.value.object.artifact.MinimumArtifactDataVO;
import uk.ac.ox.cs.science2020.zoon.business_manager.value.object.moduleheader.HeaderObject;
import uk.ac.ox.cs.science2020.zoon.shared.value.object.ActionOutcomeVO;
import uk.ac.ox.cs.science2020.zoon.shared.value.object.artifact.workflowcall.WorkflowCallVO;
import uk.ac.ox.cs.science2020.zoon.shared.value.type.ArtifactType;
import uk.ac.ox.cs.science2020.zoon.shared.value.type.DataIdentifier;
import uk.ac.ox.cs.science2020.zoon.shared.value.type.ModuleType;

/**
 * gitblit (https://github.com/gitblit) git repository.
 *
 * @author 
 */
public class GitblitVO implements Store {

  private static final MultiKeyMap artifactVerificationCache = new MultiKeyMap();
  private static final String cacheKeyJSON = "cacheKeyJSON";
  private static final String cacheKeyVerified = "cacheKeyVerified";

  private static final String defaultName = "gitblit";

  private static final String tmpFilePrefix = "zoonTmpFile";

  private final String applicationURL;
  private final String repositoryName;
  private final String repositoryPath;
  private final String repositoryURL;
  private final String adminName;
  private final String adminPassword;
  private final String storeName;
  private final boolean hasPublicArtifacts;
  private final boolean temporaryStore;
  private final boolean zoonStore;

  @Autowired
  private ModulesParsingGateway modulesParsingGateway;

  private static final Log log = LogFactory.getLog(GitblitVO.class);

  /**
   * Initialising constructor.
   * 
   * @param applicationURL
   * @param repositoryName
   * @param repositoryPath
   * @param repositoryURL
   * @param adminName
   * @param adminPassword
   * @param storeName Illustrative name, e.g. ZOON repository (optional).
   * @param hasPublicArtifacts {@code true} if store contains public artifacts, otherwise {@code false}
   * @param temporaryStore
   * @param zoonStore Indicator if this is the principal ZOON repository.
   * @throws IllegalArgumentException If any parameter is <code>null</code> or empty.
   */
  public GitblitVO(final String applicationURL, final String repositoryName,
                   final String repositoryPath, final String repositoryURL, final String adminName,
                   final String adminPassword, final String storeName,
                   final boolean hasPublicArtifacts, final boolean temporaryStore,
                   final boolean zoonStore) {
    if (StringUtils.isBlank(applicationURL) || StringUtils.isBlank(repositoryName) ||
        StringUtils.isBlank(repositoryPath) || StringUtils.isBlank(repositoryURL) ||
        StringUtils.isBlank(adminName) || StringUtils.isBlank(adminPassword)) {
      throw new IllegalArgumentException("GitBlitVO constructor received an invalid parameter!");
    }
    this.applicationURL = applicationURL;
    this.repositoryName = repositoryName;
    this.repositoryPath = repositoryPath;
    this.repositoryURL = repositoryURL;
    this.adminName = adminName;
    this.adminPassword = adminPassword;
    this.storeName = StringUtils.isBlank(storeName) ? defaultName : storeName;
    this.hasPublicArtifacts = hasPublicArtifacts;
    this.temporaryStore = temporaryStore;
    this.zoonStore = zoonStore;
  }

  private void cacheLoad(final ArtifactStoreVO artifactStore) {
    final int userId = artifactStore.getUserId();
    final String name = artifactStore.getArtifactName();
    final String version = artifactStore.getArtifactVersion();
    final ArtifactType type = artifactStore.getArtifactType();
    final Object parsedData = artifactStore.getParsedData();

    log.debug("~cacheLoad() : '" + getName() + "' store '" + artifactStore.retrieveMinimumInfo() + "'.");

    if (artifactVerificationCache.containsKey(userId, type, name, version)) {
      log.info("~cacheLoad() : Cached item already exists for '" + artifactStore.retrieveMinimumInfo() + "'.");
    }

    final Map<String, Object> artifactVerificationCacheObj = new HashMap<String, Object>();
    artifactVerificationCacheObj.put(cacheKeyJSON, parsedData);
    artifactVerificationCacheObj.put(cacheKeyVerified, false);
    artifactVerificationCache.put(userId, type, name, version, artifactVerificationCacheObj);
  }

  @SuppressWarnings("unchecked")
  private Map<String, Object> cacheRead(final MinimumArtifactDataVO minimumArtifactData) {
    final int userId = minimumArtifactData.getUserId();
    final String name = minimumArtifactData.getArtifactName();
    final String version = minimumArtifactData.getArtifactVersion();
    final ArtifactType type = minimumArtifactData.getArtifactType();

    return (Map<String, Object>) artifactVerificationCache.get(userId, type, name, version);
  }

  private void cacheWrite(final MinimumArtifactDataVO minimumArtifactData, final boolean outcome) {
    final int userId = minimumArtifactData.getUserId();
    final String name = minimumArtifactData.getArtifactName();
    final String version = minimumArtifactData.getArtifactVersion();
    final ArtifactType type = minimumArtifactData.getArtifactType();

    final Map<String, Object> cacheObject = cacheRead(minimumArtifactData);
    if (cacheObject == null) {
      throw new IllegalStateException("Somehow ended up without an object in the data cache for '" + minimumArtifactData.toString() + "'.");
    }

    final Map<String, Object> artifactVerificationCacheObj = new HashMap<String, Object>();
    artifactVerificationCacheObj.put(cacheKeyJSON, cacheObject.get(cacheKeyJSON));
    artifactVerificationCacheObj.put(cacheKeyVerified, outcome);
    artifactVerificationCache.put(userId, type, name, version, artifactVerificationCacheObj);
  }

  private static String createCommitInfo(final String name, final String version) {
    return "[".concat(name).concat("][").concat(version).concat("]");
  }

  /* (non-Javadoc)
   * @see uk.ac.ox.cs.science2020.zoon.business_manager.business.artifact.Store#deleteArtifact(uk.ac.ox.cs.science2020.zoon.business_manager.value.object.artifact.MinimumArtifactDataVO)
   */
  @Override
  public ActionOutcomeVO deleteArtifact(final MinimumArtifactDataVO minimumArtifactData) {
    log.debug("~deleteArtifact() : Invoked.");
    final GitblitRegistration gitblitRegistration = new GitblitRegistration(adminName, applicationURL,
                                                                            adminName,
                                                                            adminPassword.toCharArray());
    final GitblitClient gitblitClient = new GitblitClient(gitblitRegistration);
    try {
      gitblitClient.login();
    } catch (IOException e) {
      e.printStackTrace();
      final String errorMessage = "Couldn't connect to '" + applicationURL + "'";
      log.error("~deleteArtifact() : ".concat(errorMessage));
      return new ActionOutcomeVO(false, errorMessage);
    }

    RepositoryModel zoonArtifactsRepositoryModel = gitblitClient.getRepository(repositoryName.concat(".git"));
    if (zoonArtifactsRepositoryModel == null) {
      return new ActionOutcomeVO(false, "System error! Gitblit repository non-existent!");
    } else {
      log.debug("~deleteArtifact() : Repository retrieved.");
    }

    final FileRepositoryBuilder builder = new FileRepositoryBuilder();
    final String zoonArtifactsPath = repositoryPath;
    final File zoonArtifactsPathFile = new File(zoonArtifactsPath);

    Repository repository = null;
    try {
      repository = builder.setGitDir(zoonArtifactsPathFile)
                          .readEnvironment()
                          .findGitDir()
                          .build();
    } catch (IOException e) {
      e.printStackTrace();
      final String errorMessage = "Couldn't build repository from '" + zoonArtifactsPath + "' due to '" + e.getMessage() + "'.";
      log.error("~deleteArtifact() : ".concat(errorMessage));
      return new ActionOutcomeVO(false, errorMessage);
    }

    log.debug("~deleteArtifact() : Repository found.");
    assert (repository.isBare()) : "Application expects to work on a bare repository!";

    final UsernamePasswordCredentialsProvider adminCredentials = new UsernamePasswordCredentialsProvider(adminName,
                                                                                                         adminPassword);

    File tmpFile = null;
    try {
      tmpFile = File.createTempFile(tmpFilePrefix, "");
      tmpFile.delete();
    } catch (IOException e) {
      e.printStackTrace();
      final String errorMessage = "IOException creating+deleting tmp file '" + e.getMessage() + "'.";
      log.error("~deleteArtifact() : ".concat(errorMessage));
      return new ActionOutcomeVO(false, errorMessage);
    }

    final CloneCommand cloneCommand = Git.cloneRepository();
    cloneCommand.setBare(false);
    cloneCommand.setCloneAllBranches(true);
    cloneCommand.setURI(repositoryURL);
    cloneCommand.setDirectory(tmpFile);
    cloneCommand.setCredentialsProvider(adminCredentials);
    cloneCommand.setBranch("master");

    Git clonedGit = null;
    String errorMessage = null;
    try {
      clonedGit = cloneCommand.call();
    } catch (InvalidRemoteException e) {
      e.printStackTrace();
      errorMessage = "InvalidRemoteException '" + e.getMessage() + "'.";
    } catch (TransportException e) {
      e.printStackTrace();
      errorMessage = "TransportException '" + e.getMessage() + "'.";
    } catch (GitAPIException e) {
      e.printStackTrace();
      errorMessage = "GitAPIException '" + e.getMessage() + "'.";
    }
    if (errorMessage != null) {
      log.error("~deleteArtifact() : ".concat(errorMessage));
      repository.close();
      return new ActionOutcomeVO(false, errorMessage);
    }

    final String artifactPath = filePathConstructor(minimumArtifactData);
    log.debug("~deleteArtifact() : Artifact path is '" + artifactPath + "'.");

    try {
      clonedGit.rm().addFilepattern(artifactPath).call();
    } catch (NoFilepatternException e) {
      e.printStackTrace();
      errorMessage = "NoFilepatternException '" + e.getMessage() + "'.";
    } catch (GitAPIException e) {
      e.printStackTrace();
      errorMessage = "GitAPIException '" + e.getMessage() + "'.";
    }
    if (errorMessage != null) {
      log.warn("~deleteArtifact() : ".concat(errorMessage));
      clonedGit.close();
      repository.close();
      return new ActionOutcomeVO(false, errorMessage);
    }

    final String artifactInfo = createCommitInfo(minimumArtifactData.getArtifactName(),
                                                 minimumArtifactData.getArtifactVersion());
    RevCommit revCommit = null;
    try {
      log.debug("~storeTemporaryArtifact() : About to commit.");
      revCommit = clonedGit.commit()
                           .setMessage("Delete ".concat(artifactInfo))
                           .call();
    } catch (NoHeadException e) {
      e.printStackTrace();
      errorMessage = "NoHeadException '" + e.getMessage() + "'.";
    } catch (NoMessageException e) {
      e.printStackTrace();
      errorMessage = "NoMessageException '" + e.getMessage() + "'.";
    } catch (UnmergedPathsException e) {
      e.printStackTrace();
      errorMessage = "UnmergedPathsException '" + e.getMessage() + "'.";
    } catch (ConcurrentRefUpdateException e) {
      e.printStackTrace();
      errorMessage = "ConcurrentRefUpdateException '" + e.getMessage() + "'.";
    } catch (WrongRepositoryStateException e) {
      e.printStackTrace();
      errorMessage = "WrongRepositoryStateException '" + e.getMessage() + "'.";
    } catch (AbortedByHookException e) {
      e.printStackTrace();
      errorMessage = "AbortedByHookException '" + e.getMessage() + "'.";
    } catch (GitAPIException e) {
      e.printStackTrace();
      errorMessage = "GitAPIException '" + e.getMessage() + "'.";
    }

    if (errorMessage != null) {
      log.warn("~deleteArtifact() : ".concat(errorMessage));
      clonedGit.close();
      repository.close();
      return new ActionOutcomeVO(false, errorMessage);
    }

    final PushCommand push = clonedGit.push();
    push.setCredentialsProvider(adminCredentials);
    push.setRemote(repositoryURL);
    push.setRemote("origin");

    Iterable<PushResult> pushResults = null;
    try {
      log.debug("~storeTemporaryArtifact() : About to push.");
      pushResults = push.call();
    } catch (InvalidRemoteException e) {
      e.printStackTrace();
      errorMessage = "InvalidRemoteException '" + e.getMessage() + "'.";
    } catch (TransportException e) {
      e.printStackTrace();
      errorMessage = "TransportException '" + e.getMessage() + "'.";
    } catch (GitAPIException e) {
      e.printStackTrace();
      errorMessage = "GitAPIException '" + e.getMessage() + "'.";
    }

    if (errorMessage != null) {
      log.warn("~deleteArtifact() : ".concat(errorMessage));
      clonedGit.close();
      repository.close();
      return new ActionOutcomeVO(false, errorMessage);
    }

    clonedGit.close();
    repository.close();

    if (revCommit != null) {
      log.debug("~deleteArtifact() : revCommit '" + revCommit.getFullMessage() + "'.");
    }
    if (pushResults != null) {
      for (final PushResult pushResult : pushResults) {
        log.debug("~deleteArtifact() : push message '" + pushResult.getMessages() + "'.");
      }
    }

    try {
      log.debug("~deleteArtifact() : About to remove tmpFile '" + tmpFile + "'.");
      FileUtils.delete(tmpFile, FileUtils.RECURSIVE);
    } catch (IOException e) {
      log.error("~deleteArtifact() : IOException '" + e.getMessage() + "'.");
      e.printStackTrace();
    }

    return new ActionOutcomeVO(true, "Deleted " + artifactInfo + " from temporary repository");
  }

  /* (non-Javadoc)
   * @see uk.ac.ox.cs.science2020.zoon.business_manager.business.artifact.Store#getName()
   */
  @Override
  public String getName() {
    return storeName;
  }

  /**
   * 
   * @param userId
   * @param name
   * @param version
   * @param moduleType
   * @return
   */
  public boolean hasModule(final int userId, final String name,
                           final String version, final ModuleType moduleType) {
    log.debug("~hasModule() : Invoked.");
    boolean hasModule = false;

    final Map<String, List<Map<DataIdentifier, HeaderObject>>> modulesRetrieved = retrievePrivateModules(userId,
                                                                                                         true,
                                                                                                         false);
    for (final Map.Entry<String, List<Map<DataIdentifier, HeaderObject>>> eachRetrieved : modulesRetrieved.entrySet()) {
      final String retrievedModuleName = eachRetrieved.getKey();
      if (retrievedModuleName.equals(name)) {
        log.debug("~hasModule() : Matched on module name '" + name + "'.");
        for (final Map<DataIdentifier, HeaderObject> headerObjects : eachRetrieved.getValue()) {
          String retrievedModuleVersion = null;
          try {
            retrievedModuleVersion = ArtifactUtil.getModuleVersion(headerObjects);
          } catch (IllegalStateException e) {
            // TODO : Remove this!!
          }
          final boolean skipVersionTest = retrievedModuleVersion == null;
          log.debug("~hasModule() : Comparing required module version '" + version  + "', to version '" + retrievedModuleVersion + "'.");
          if (skipVersionTest || retrievedModuleVersion.equals(version)) {
            ModuleType retrievedModuleType = null;
            try {
              retrievedModuleType = ArtifactUtil.getModuleType(headerObjects);
            } catch (IllegalStateException e) {
              // TODO : Remove this!!
            }
            final boolean skipTypeTest = retrievedModuleType == null;
            log.debug("~hasModule() : Comparing required module type '" + moduleType  + "', to type '" + retrievedModuleType + "'.");
            if (skipTypeTest || retrievedModuleType.compareTo(moduleType) == 0) {
              log.debug("~hasModule() : Matched!!!");
              hasModule = true;
              break;
            }
          }
        }
      }
      if (hasModule) {
        break;
      }
    }

    return hasModule;
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

    log.error("~loadPublicModulesOnStartup() : Loading of modules from '" + getName() + "' on startup not yet implemented!");
    throw new UnsupportedOperationException("Operation not yet implemented");
  }

  /* (non-Javadoc)
   * @see uk.ac.ox.cs.science2020.zoon.business_manager.business.artifact.Store#retrieveArtifact(uk.ac.ox.cs.science2020.zoon.business_manager.value.object.artifact.MinimumArtifactDataVO)
   */
  @Override
  public String retrieveArtifact(final MinimumArtifactDataVO minimumArtifactData) {
    log.debug("~retrieveArtifact() : Invoked.");
    String artifact = null;

    final Repository repository = retrieveRepository();
    final ObjectReader objectReader = repository.newObjectReader();

    final RevWalk revWalk = new RevWalk(repository);
    final TreeWalk treeWalk = new TreeWalk(repository);
    try {
      final ObjectId lastCommitId = repository.resolve(Constants.HEAD);
      final RevCommit headCommit = revWalk.parseCommit(lastCommitId);

      /*
      final Ref headRef = repository.getRef(Constants.MASTER);
      final RevCommit headCommit = revWalk.parseCommit(headRef.getObjectId());
      */

      final RevTree headTree = headCommit.getTree();
      treeWalk.addTree(headTree);
      treeWalk.setRecursive(true);
      // e.g. 92668751/MODULE/FishForDinner/1.0/FishForDinner
      final String path = filePathConstructor(minimumArtifactData);

      log.debug("~retrieveArtifact() : Path is '" + path + "'.");
      treeWalk.setFilter(PathFilter.create(path));
      if (treeWalk.next()) {
        final ObjectId objectId = treeWalk.getObjectId(0);

        final byte[] moduleData = objectReader.open(objectId).getBytes();
        artifact = new String(moduleData, "utf-8");
      }
    } catch (IOException e) {
      // TODO : Properly handle exceptions
      log.error("~retrieveArtifact() : Error '" + e.getMessage() + "'.");
      e.printStackTrace();
    } finally {
      objectReader.close();
      treeWalk.close();
      revWalk.close();
    }

    log.debug("~retrieveArtifact() : Artifact '" + artifact + "'.");
    return artifact;
  }

  private String[] pathDestructor(final String path) {
    return path.split("/");
  }

  //private String pathConstructor(final int userId, final ArtifactType artifactType) {
  //  return ;
  //}
  //private String pathConstructor(final int userId, final ArtifactType artifactType,
  //                               final String artifactName) {
  //  return pathConstructor(userId, artifactType).concat("/").concat(artifactName);
  //}
  private String pathConstructor(final int userId, final ArtifactType artifactType,
                                 final String artifactName, final String artifactVersion) {
    return String.valueOf(userId).concat("/").concat(artifactType.toString())
                                 .concat("/").concat(artifactName)
                                 .concat("/").concat(artifactVersion);
  }
  private String filePathConstructor(final int userId, final ArtifactType artifactType,
                                     final String artifactName, final String artifactVersion) {
    return pathConstructor(userId, artifactType, artifactName, artifactVersion)
                          .concat("/").concat(artifactName);
  }
  private String filePathConstructor(final MinimumArtifactDataVO minimumArtifactData) {
    return pathConstructor(minimumArtifactData.getUserId(), minimumArtifactData.getArtifactType(),
                           minimumArtifactData.getArtifactName(),
                           minimumArtifactData.getArtifactVersion())
                           .concat("/").concat(minimumArtifactData.getArtifactName());
  }

  private class RepoVO {
    private final String name;
    private final String version;
    private final String content;

    protected RepoVO(final String name, final String version, final String content) {
      this.name = name;
      this.version = version;
      this.content = content;
    }

    public String getName() {
      return name;
    }
    public String getVersion() {
      return version;
    }
    public String getContent() {
      return content;
    }
  }

  private Set<RepoVO> retrievePrivateArtifacts(final int userId, final boolean minimal,
                                                    final ArtifactType type) {

    final Set<RepoVO> retrieved = new HashSet<RepoVO>();

    final Repository repository = retrieveRepository();
    if (repository == null) {
      return retrieved;
    }

    final RevWalk revWalk = new RevWalk(repository);
    final TreeWalk treeWalk = new TreeWalk(repository);
    final ObjectReader objectReader = repository.newObjectReader();

    log.debug("~retrievePrivateArtifacts() : RevWalk '" + revWalk + "'.");
    log.debug("~retrievePrivateArtifacts() : TreeWalk '" + treeWalk + "'.");
    log.debug("~retrievePrivateArtifacts() : ObjectReader '" + objectReader + "'.");

    try {
      /* If the following come back as nulls verify the correct gitblit repositoryPath config value!
      for (final Map.Entry<String, Ref> refs : repository.getRefDatabase().getRefs(RefDatabase.ALL).entrySet()) {
        log.debug("~retrieveArtifacts() : Key '" + refs.getKey() + "'.");
        log.debug("~retrieveArtifacts() : Val '" + refs.getValue() + "'.");
      }
      */
      final Ref headRef = repository.getRef(Constants.MASTER);
      log.debug("~retrievePrivateArtifacts() : Ref '" + headRef + "'.");
      final RevCommit headCommit = revWalk.parseCommit(headRef.getObjectId());
      final RevTree headTree = headCommit.getTree();
  
      treeWalk.addTree(headTree);
      treeWalk.setRecursive(true);
      while (treeWalk.next()) {
        if (treeWalk.isSubtree()) {
          log.debug("~retrievePrivateArtifacts() : dir: " + treeWalk.getPathString());
          treeWalk.enterSubtree();
        } else {
          final String pathString = treeWalk.getPathString();
          final String[] pathParts = pathDestructor(pathString);
          log.debug("~retrievePrivateArtifacts() : Path string : '" + pathString + "'.");
          if (pathParts.length == 5) {
            final String supposedUserId = pathParts[0];
            ArtifactType supposedArtifactType = null;
            try {
              supposedArtifactType = ArtifactType.valueOf(pathParts[1]);
            } catch (Exception e) {
              
            }
            final String supposedArtifactName = pathParts[2];
            final String supposedArtifactVersion = pathParts[3];

            if (String.valueOf(userId).equalsIgnoreCase(supposedUserId) &&
                supposedArtifactType != null && type.compareTo(supposedArtifactType) == 0) {
              log.debug("~retrievePrivateArtifacts() : User '" + userId + "' has artifact '" + supposedArtifactName + "', version '" + supposedArtifactVersion + "'.");
              final byte[] artifactData = objectReader.open(treeWalk.getObjectId(0)).getBytes();
              final String latestArtifactContent = new String(artifactData, "utf-8");
              retrieved.add(new RepoVO(supposedArtifactName, supposedArtifactVersion,
                                       latestArtifactContent));
            } else {
              log.debug("~retrievePrivateArtifacts() : User is not owner of '" + pathString + "'.");
            }
          } else {
            log.warn("~retrievePrivateArtifacts() : There were '" + pathParts.length + "' pathString - expecting 3!");
          }
        }
      }
    } catch (Exception e) {
      // TODO : Properly handle exceptions
      log.error("~retrievePrivateArtifacts() : Error '" + e.getMessage() + "'.");
      e.printStackTrace();
    } finally {
      objectReader.close();
      treeWalk.close();
      revWalk.close();
    }

    return retrieved;
  }

  /* (non-Javadoc)
   * @see uk.ac.ox.cs.science2020.zoon.business_manager.business.artifact.Store#retrievePrivateModules(int, boolean, boolean)
   */
  @Override
  public Map<String, List<Map<DataIdentifier, HeaderObject>>> retrievePrivateModules(final int userId,
                                                                                     final boolean minimal,
                                                                                     final boolean verifiedOnly) {
    log.debug("~retrievePrivateModules() : Invoked for user id '" + userId + "'.");

    final Set<RepoVO> allRepoVO = retrievePrivateArtifacts(userId, minimal, ArtifactType.MODULE);

    final Map<String, List<Map<DataIdentifier, HeaderObject>>> retrievedModules = new HashMap<String, List<Map<DataIdentifier, HeaderObject>>>();
    if (allRepoVO.isEmpty()) {
      return retrievedModules;
    }

    final Set<ParseCandidate> missingFromCache = new HashSet<ParseCandidate>();
    for (final RepoVO repoVO : allRepoVO) {
      final String artifactName = repoVO.getName();
      final String artifactVersion = repoVO.getVersion();
      final String artifactContent = repoVO.getContent();

      String parsedModuleJSON = null;
      Boolean parsedModuleVerified = null;

      final MinimumArtifactDataVO minimumArtifactData = new MinimumArtifactDataVO(userId,
                                                                                  artifactName,
                                                                                  artifactVersion,
                                                                                  ArtifactType.MODULE);
      Map<String, Object> cacheObject = cacheRead(minimumArtifactData);
      if (cacheObject == null) {
        missingFromCache.add(new ParseCandidate(artifactName, artifactVersion, artifactContent));
      } else {
        log.debug("~retrievePrivateModules() : Retrieving parse results from cache!");
        final Map<String, Object> artifactVerificationCacheObj = cacheObject;
        parsedModuleJSON = (String) artifactVerificationCacheObj.get(cacheKeyJSON);
        parsedModuleVerified = (Boolean) artifactVerificationCacheObj.get(cacheKeyVerified);
      }

      if (!verifiedOnly || (verifiedOnly && parsedModuleVerified)) {
        try {
          DummyModuleProcessingUtil.aggregateModuleData(retrievedModules, getName(),
                                                        parsedModuleJSON, artifactContent,
                                                        parsedModuleVerified);
        } catch (Exception e) {
          final String errorMessage = "Error during dummy module processing '" + e.getMessage() + "'.";
          e.printStackTrace();
          log.error("~retrievePrivateModules() : ".concat(errorMessage));
        }
      } else {
        log.debug("~retrievePrivateModules() : Ignoring because verifiedOnly '" + verifiedOnly + "', and parsedModuleVerified '" + parsedModuleVerified + "'.");
      }
    }

    if (!missingFromCache.isEmpty()) {
      log.info("~retrievePrivateModules() : Items missing from cache, re-parsing and loading into cache!");
      final Set<ParseCandidate> parseResults = modulesParsingGateway.runModulesParsing(missingFromCache);

      for (final ParseCandidate parseResult : parseResults) {
        log.debug("~retrievePrivateModules() : Parse result '" + parseResult.toString() + "'.");
        final String parsedModuleJSON = parseResult.retrieveResultJSON();
        if (parsedModuleJSON == null) {
          log.error("~retrievePrivateModules() : No JSON generated for '" + parseResult + "'.");
          throw new UnsupportedOperationException("No results!");
        }
  
        log.debug("~retrievePrivateModules() : Going to load parse output into cache.");
        final boolean parsedModuleVerified = parseResult.isResultVerified();

        final String artifactName = parseResult.getArtifactName();
        final String artifactVersion = parseResult.getArtifactVersion();
        final String artifactContent = parseResult.getArtifactContent();

        cacheLoad(new ArtifactStoreVO(userId, artifactName, artifactVersion, ArtifactType.MODULE,
                                      artifactContent, parsedModuleJSON));

        final MinimumArtifactDataVO minimumArtifactData = new MinimumArtifactDataVO(userId,
                                                                                    parseResult.getArtifactName(),
                                                                                    parseResult.getArtifactVersion(),
                                                                                    ArtifactType.MODULE);
        cacheWrite(minimumArtifactData, parsedModuleVerified);

        if (!verifiedOnly || (verifiedOnly && parsedModuleVerified)) {
          try {
            DummyModuleProcessingUtil.aggregateModuleData(retrievedModules, getName(),
                                                          parsedModuleJSON,
                                                          parseResult.getArtifactContent(),
                                                          parsedModuleVerified);
          } catch (Exception e) {
            final String errorMessage = "Error during dummy module processing '" + e.getMessage() + "'.";
            e.printStackTrace();
            log.error("~retrievePrivateModules() : ".concat(errorMessage));
          }
        } else {
          log.debug("~retrievePrivateModules() : Ignoring because verifiedOnly '" + verifiedOnly + "', and parsedModuleVerified '" + parsedModuleVerified + "'.");
        }
      }
    }

    return retrievedModules;
  }

  /* (non-Javadoc)
   * @see uk.ac.ox.cs.science2020.zoon.business_manager.business.artifact.Store#retrievePrivateWorkflowCalls(int, boolean)
   */
  @Override
  public Set<WorkflowCallVO> retrievePrivateWorkflowCalls(final int userId, final boolean minimal) {
    log.debug("~retrievePrivateWorkflowCalls() : Invoked for '" + userId + "', minimal '" + minimal + "'.");

    final Set<RepoVO> allRepoVO = retrievePrivateArtifacts(userId, minimal,
                                                           ArtifactType.WORKFLOW_CALL);

    final Set<WorkflowCallVO> privateWorkflows = new HashSet<WorkflowCallVO>();
    if (allRepoVO.isEmpty()) {
      return privateWorkflows;
    }

    for (final RepoVO repoContent : allRepoVO) {
      final String artifactName = repoContent.getName();
      final String artifactContent = repoContent.getContent();

      try {
        final WorkflowCallVO workflowCall = WorkflowCallVO.extractWorkflowFromJSON(artifactContent,
                                                                                   true);
        privateWorkflows.add(workflowCall);
        assert (artifactName.equals(workflowCall.getName())) : "Gitblit name '" + artifactName + "' vs. Workflow Call name '" + workflowCall.getName() + "' mismatch!";
      } catch (JSONException e) {
        log.error("~retrievePrivateWorkflowCalls() : Exception extracting workflow call from JSON '" + e.getMessage() + "'.");
      }
    }

    return privateWorkflows;
  }

  private Repository retrieveRepository() {
    /**
    final GitblitRegistration gitblitRegistration = new GitblitRegistration(adminName, applicationURL,
                                                                            adminName,
                                                                            adminPassword.toCharArray());
    final GitblitClient gitblitClient = new GitblitClient(gitblitRegistration);
    try {
      gitblitClient.login();
    } catch (IOException e) {
      log.error("~retrieveRepository() : Couldn't connect to '" + applicationURL + "'");
      e.printStackTrace();
    }

    final RepositoryModel zoonArtifactsRepositoryModel = gitblitClient.getRepository(repositoryName.concat(".git"));
    if (zoonArtifactsRepositoryModel == null) {
      log.error("~retrieveRepository() : Couldn't retrieve the repository '" + repositoryName + "'.");
      return null;
    }
    */

    final FileRepositoryBuilder builder = new FileRepositoryBuilder();
    final String zoonModulesPath = repositoryPath;
    final File zoonModulesPathFile = new File(zoonModulesPath);

    Repository repository = null;
    try {
      repository = builder.setGitDir(zoonModulesPathFile)
                          .readEnvironment() // scan environment GIT_* variables
                          .findGitDir() // scan up the file system tree
                          .build();
    } catch (IOException e) {
      log.error("~retrieveRepository() : Couldn't build repository from '" + zoonModulesPath + "' due to '" + e.getMessage() + "'."); 
      e.printStackTrace();
    }

    return repository;
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
    log.debug("~storeTemporaryArtifact() : '" + getName() + "' store '" + artifactStore.retrieveMinimumInfo() + "'.");
    cacheLoad(artifactStore);

    final int userId = artifactStore.getUserId();
    final String name = artifactStore.getArtifactName();
    final String version = artifactStore.getArtifactVersion();
    final ArtifactType type = artifactStore.getArtifactType();
    final String content = artifactStore.getArtifactContent();

    final GitblitRegistration gitblitRegistration = new GitblitRegistration(adminName, applicationURL,
                                                                            adminName,
                                                                            adminPassword.toCharArray());
    final GitblitClient gitblitClient = new GitblitClient(gitblitRegistration);
    try {
      gitblitClient.login();
    } catch (IOException e) {
      log.error("~storeTemporaryArtifact() : Couldn't connect to '" + applicationURL + "'");
      e.printStackTrace();
    }

    RepositoryModel zoonArtifactsRepositoryModel = gitblitClient.getRepository(repositoryName.concat(".git"));
    if (zoonArtifactsRepositoryModel == null) {
      // Create the zoon_artifacts repository
      zoonArtifactsRepositoryModel = new RepositoryModel();
      zoonArtifactsRepositoryModel.name = repositoryName;
      zoonArtifactsRepositoryModel.description = "ZOON user artifacts";
      zoonArtifactsRepositoryModel.addOwner(adminName);
      zoonArtifactsRepositoryModel.accessRestriction = AccessRestrictionType.PUSH;
      zoonArtifactsRepositoryModel.authorizationControl = AuthorizationControl.AUTHENTICATED;

      try {
        final boolean success = RpcUtils.createRepository(zoonArtifactsRepositoryModel, applicationURL,
                                                          adminName, adminPassword.toCharArray());
        log.info("~storeTemporaryArtifact() : Repository creation successful? '" + success + "'.");
      } catch (IOException e) {
        log.error("~storeTemporaryArtifact() : createRepository fail '" + e.getMessage() + "'.");
        e.printStackTrace();
      }
    } else {
      log.debug("~storeTemporaryArtifact() : Repository retrieved.");
    }

    final FileRepositoryBuilder builder = new FileRepositoryBuilder();
    final String zoonArtifactsPath = repositoryPath;
    final File zoonArtifactsPathFile = new File(zoonArtifactsPath);

    Repository repository = null;
    try {
      repository = builder.setGitDir(zoonArtifactsPathFile)
                          .readEnvironment()
                          .findGitDir()
                          .build();
    } catch (IOException e) {
      log.error("~storeTemporaryArtifact() : Couldn't build repository from '" + zoonArtifactsPath + "' due to '" + e.getMessage() + "'."); 
      e.printStackTrace();
    }

    
    if (repository != null) {
      log.debug("~storeTemporaryArtifact() : Repository found.");
      assert (repository.isBare()) : "Application expects to work on a bare repository!";

      final UsernamePasswordCredentialsProvider adminCredentials = new UsernamePasswordCredentialsProvider(adminName,
                                                                                                           adminPassword);

      File tmpFile = null;
      try {
        tmpFile = File.createTempFile(tmpFilePrefix, "");
        tmpFile.delete();
      } catch (IOException e) {
        log.error("~storeTemporaryArtifact() : IOException creating+deleting tmp file '" + e.getMessage() + "'.");
        e.printStackTrace();
      }

      if (tmpFile == null) {
        final String errorMessage = "Could not create the zoonTmpFile '" + tmpFilePrefix + "'.";
        log.error("~storeTemporaryArtifact() : " + errorMessage);
        return errorMessage;
      } else {
        log.debug("~storeTemporaryArtifact() : Created zoonTmpFile '" + tmpFile.getAbsolutePath() + "'.");
      }

      final CloneCommand cloneCommand = Git.cloneRepository();
      cloneCommand.setBare(false);
      cloneCommand.setCloneAllBranches(true);
      cloneCommand.setURI(repositoryURL);
      cloneCommand.setDirectory(tmpFile);
      cloneCommand.setCredentialsProvider(adminCredentials);
      cloneCommand.setBranch("master");
      Git clonedGit = null;
      try {
        clonedGit = cloneCommand.call();
      } catch (InvalidRemoteException e) {
        log.error("~storeTemporaryArtifact() : InvalidRemoteException '" + e.getMessage() + "'.");
        e.printStackTrace();
      } catch (TransportException e) {
        log.error("~storeTemporaryArtifact() : TransportException '" + e.getMessage() + "'.");
        e.printStackTrace();
      } catch (GitAPIException e) {
        log.error("~storeTemporaryArtifact() : GitAPIException '" + e.getMessage() + "'.");
        e.printStackTrace();
      }

      if (clonedGit == null) {
        final String errorMessage = "Could not clone the reposistory '" + repositoryURL + "'.";
        log.error("~storeTemporaryArtifact() : " + errorMessage); 
        return errorMessage;
      } else {
        log.debug("~storeTemporaryArtifact() : Git cloning successful.");
      }

      // Create the user + artifact type directory (if it doesn't exist), e.g. 9823923/MODULE/CWBZimbabwe/1.0
      final String artifactPathBase = pathConstructor(userId, type, name, version);
      final Repository clonedRepository = clonedGit.getRepository();
      try {
        FileUtils.mkdirs(new File(clonedRepository.getWorkTree(), artifactPathBase));
        log.debug("~storeTemporaryArtifact() : Creation of user directory successful.");
      } catch (NoWorkTreeException e) {
        log.error("~storeTemporaryArtifact() : NoWorkTreeException '" + e.getMessage() + "'.");
        e.printStackTrace();
      } catch (IOException e) {
        log.error("~storeTemporaryArtifact() : MkDir IOException '" + e.getMessage() + "'.");
        e.printStackTrace();
      }

      final String artifactFilePath = filePathConstructor(userId, type, name, version);
      log.debug("~storeTemporaryArtifact() : Artifact file path is '" + artifactFilePath + "'.");

      // Create the new artifact path 9823923/MODULE/FishForDinner/1.0/FileForDinner
      final File file = new File(clonedRepository.getWorkTree(), artifactFilePath);
      try {
        FileUtils.createNewFile(file);
      } catch (IOException e) {
        log.error("~storeTemporaryArtifact() : CreateNewfile IOException '" + e.getMessage() + "'.");
        e.printStackTrace();
      }

      PrintWriter writer = null;
      try {
        writer = new PrintWriter(file);
      } catch (FileNotFoundException e) {
        log.error("~storeTemporaryArtifact() : FileNotFoundException '" + e.getMessage() + "'.");
        e.printStackTrace();
      }

      if (writer == null) {
        final String errorMessage = "Couldn't create a writer to write to '" + file.getAbsolutePath() + "'.";
        log.error("~storeTemporaryArtifact() : " + errorMessage);
        return errorMessage;
      }

      writer.print(content);
      writer.close();

      DirCache dirCache = null;
      try {
        log.debug("~storeTemporaryArtifact() : About to add file pattern '" + artifactFilePath + "'.");
        dirCache = clonedGit.add().addFilepattern(artifactFilePath).call();
      } catch (NoFilepatternException e) {
        log.error("~storeTemporaryArtifact() : NoFilepatternException '" + e.getMessage() + "'.");
        e.printStackTrace();
      } catch (GitAPIException e) {
        log.error("~storeTemporaryArtifact() : GitAPIException '" + e.getMessage() + "'.");
        e.printStackTrace();
      }

      RevCommit revCommit = null;
      try {
        log.debug("~storeTemporaryArtifact() : About to commit .");
        revCommit = clonedGit.commit().setMessage("Store " + createCommitInfo(name, version)).call();
      } catch (NoHeadException e) {
        log.error("~storeTemporaryArtifact() : NoHeadException '" + e.getMessage() + "'.");
        e.printStackTrace();
      } catch (NoMessageException e) {
        log.error("~storeTemporaryArtifact() : NoMessageException '" + e.getMessage() + "'.");
        e.printStackTrace();
      } catch (UnmergedPathsException e) {
        log.error("~storeTemporaryArtifact() : UnmergedPathsException '" + e.getMessage() + "'.");
        e.printStackTrace();
      } catch (ConcurrentRefUpdateException e) {
        log.error("~storeTemporaryArtifact() : ConcurrentRefUpdateException '" + e.getMessage() + "'.");
        e.printStackTrace();
      } catch (WrongRepositoryStateException e) {
        log.error("~storeTemporaryArtifact() : WrongRepositoryStateException '" + e.getMessage() + "'.");
        e.printStackTrace();
      } catch (AbortedByHookException e) {
        log.error("~storeTemporaryArtifact() : AbortedByHookException '" + e.getMessage() + "'.");
        e.printStackTrace();
      } catch (GitAPIException e) {
        log.error("~storeTemporaryArtifact() : GitAPIException '" + e.getMessage() + "'.");
        e.printStackTrace();
      }

      final PushCommand push = clonedGit.push();
      push.setCredentialsProvider(adminCredentials);
      push.setRemote(repositoryURL);
      push.setRemote("origin");

      Iterable<PushResult> pushResults = null;
      try {
        log.debug("~storeTemporaryArtifact() : About to push.");
        pushResults = push.call();
      } catch (InvalidRemoteException e) {
        log.error("~storeTemporaryArtifact() : InvalidRemoteException '" + e.getMessage() + "'.");
        e.printStackTrace();
      } catch (TransportException e) {
        log.error("~storeTemporaryArtifact() : TransportException '" + e.getMessage() + "'.");
        e.printStackTrace();
      } catch (GitAPIException e) {
        log.error("~storeTemporaryArtifact() : GitAPIException '" + e.getMessage() + "'.");
        e.printStackTrace();
      }

      clonedGit.close();

      if (dirCache != null) {
        log.debug("~storeTemporaryArtifact() : dirCache '" + dirCache.toString() + "'.");
      }
      if (revCommit != null) {
        log.debug("~storeTemporaryArtifact() : revCommit '" + revCommit.getFullMessage() + "'.");
      }
      if (pushResults != null) {
        for (final PushResult pushResult : pushResults) {
          log.debug("~storeTemporaryArtifact() : push message '" + pushResult.getMessages() + "'.");
        }
      }

      try {
        log.debug("~storeTemporaryArtifact() : About to remove tmpFile '" + tmpFile + "'.");
        FileUtils.delete(tmpFile, FileUtils.RECURSIVE);
      } catch (IOException e) {
        log.error("~storeTemporaryArtifact() : IOException '" + e.getMessage() + "'.");
        e.printStackTrace();
      }

      repository.close();
    }

    return "Artifact stored successfully";
  }

  /* (non-Javadoc)
   * @see uk.ac.ox.cs.science2020.zoon.business_manager.business.artifact.Store#writeVerificationOutcome(uk.ac.ox.cs.science2020.zoon.business_manager.value.object.artifact.MinimumArtifactDataVO, boolean)
   */
  @Override
  public void writeVerificationOutcome(final MinimumArtifactDataVO minimumArtifactData,
                                       final boolean outcome) {
    log.debug("~writeVerificationOutcome() : Invoked.");

    cacheWrite(minimumArtifactData, outcome);
  }

  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "GitblitVO [applicationURL=" + applicationURL + ", repositoryName="
        + repositoryName + ", repositoryPath=" + repositoryPath
        + ", repositoryURL=" + repositoryURL + ", adminName=" + adminName
        + ", storeName=" + storeName + ", hasPublicArtifacts="
        + hasPublicArtifacts + ", temporaryStore=" + temporaryStore
        + ", zoonStore=" + zoonStore + "]";
  }
}