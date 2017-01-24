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
package uk.ac.ox.cs.science2020.zoon.shared.value.object.artifact.workflowcall;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import uk.ac.ox.cs.science2020.zoon.shared.entity.elastic.workflowcall.VerbModules;
import uk.ac.ox.cs.science2020.zoon.shared.entity.elastic.workflowcall.WorkflowCall;
import uk.ac.ox.cs.science2020.zoon.shared.value.object.artifact.AbstractArtifactVO;
import uk.ac.ox.cs.science2020.zoon.shared.value.object.artifact.AuthorVO;
import uk.ac.ox.cs.science2020.zoon.shared.value.type.ArtifactType;
import uk.ac.ox.cs.science2020.zoon.shared.value.type.ModuleType;
import uk.ac.ox.cs.science2020.zoon.shared.value.type.workflowcall.Argument;
import uk.ac.ox.cs.science2020.zoon.shared.value.type.workflowcall.Verb;

/**
 *
 *
 * @author geoff
 */
public class WorkflowCallVO extends AbstractArtifactVO {

  private enum ProcessingType {
    module,
    forceReproducible
  }

  public static final String DEFAULT_WORKFLOW_CALL_VERSION_NUMBER = "1";
  public static final String MODULE_VERSION_JSON_KEY = "__version";

  public static final String JSON_KEY_NAME = "name";
  public static final String JSON_KEY_DESCRIPTION = "description";
  public static final String JSON_KEY_VERSION = "version";
  public static final String JSON_KEY_WORKFLOW = "workflow";

  private static final boolean defaultForceReproducible = false;

  private final String description;
  private final VerbModulesVO occurrence;
  private final VerbModulesVO covariate;
  private final VerbModulesVO process;
  private final VerbModulesVO model;
  private final VerbModulesVO output;
  private final Boolean forceReproducible;
  private final Long figshareId;

  private static final Log log = LogFactory.getLog(WorkflowCallVO.class);

  /**
   * Initialising constructor -- content derived from elastic entity.
   * <p>
   * <b>Note :</b> It is assumed that data in the elastic repository represents the latest versions
   * of the workflow calls and therefore not private artifacts.
   * 
   * @param workflowCall Elastic workflow call.
   */
  public WorkflowCallVO(final WorkflowCall workflowCall) {
    super(workflowCall.getName(), workflowCall.getVersion(),
          AuthorVO.retrieveAuthorVOs(workflowCall.getAuthors()), workflowCall.getSubmitted(),
          workflowCall.getContent(), ArtifactType.WORKFLOW_CALL, false);
    String description = null;
    VerbModulesVO occurrence = null;
    VerbModulesVO covariate = null;
    VerbModulesVO process = null;
    VerbModulesVO model = null;
    VerbModulesVO output = null;
    Boolean forceReproducible = null;
    try {
      final WorkflowCallVO extracted = extractWorkflowFromJSON(getContent(), false);
      description = extracted.getDescription();
      occurrence = extracted.getOccurrence();
      covariate = extracted.getCovariate();
      process = extracted.getProcess();
      model = extracted.getModel();
      output = extracted.getOutput();
      forceReproducible = extracted.isForceReproducible();
    } catch (JSONException e) {
      log.warn("~WorkflowCallVO() : '" + workflowCall.getName() + "' JSON parsing exception '" + e.getMessage() + "'.");
      e.printStackTrace();
    }
    this.description = description;
    this.occurrence = occurrence;
    this.covariate = covariate;
    this.process = process;
    this.model = model;
    this.output = output;
    this.forceReproducible = forceReproducible;
    this.figshareId = workflowCall.getFigshareId();
  }

  /**
   * Initialising constructor.
   * <p>
   * Used when creating a private workflow call VO in client received from business-manager. 
   * 
   * @param name Artifact name.
   * @param version Artifact version.
   * @param content Workflow call content.
   * @throws IllegalArgumentException If required arguments are {@code null} or empty.
   */
  public WorkflowCallVO(final String name, final String version, final String content) 
                        throws IllegalArgumentException {
    super(name, version, null, null, content, ArtifactType.WORKFLOW_CALL, true);
    String description = null;
    VerbModulesVO occurrence = null;
    VerbModulesVO covariate = null;
    VerbModulesVO process = null;
    VerbModulesVO model = null;
    VerbModulesVO output = null;
    Boolean forceReproducible = null;
    try {
      final WorkflowCallVO extracted = extractWorkflowFromJSON(getContent(), false);
      description = extracted.getDescription();
      occurrence = extracted.getOccurrence();
      covariate = extracted.getCovariate();
      process = extracted.getProcess();
      model = extracted.getModel();
      output = extracted.getOutput();
      forceReproducible = extracted.isForceReproducible();
    } catch (JSONException e) {
      log.warn("~WorkflowCallVO() : '" + name + "' JSON parsing exception '" + e.getMessage() + "'.");
      e.printStackTrace();
    }
    this.description = description;
    this.occurrence = occurrence;
    this.covariate = covariate;
    this.process = process;
    this.model = model;
    this.output = output;
    this.forceReproducible = forceReproducible;
    this.figshareId = null;
  }

  /**
   * Initialising constructor.
   * 
   * @param name
   * @param version
   * @param authors
   * @param submitted
   * @param description
   * @param content
   * @param occurrence
   * @param covariate
   * @param process
   * @param model
   * @param output
   * @param forceReproducible
   * @param privateArtifact
   * @param figshareId
   * @throws IllegalArgumentException
   */
  public WorkflowCallVO(final String name, final String version, final List<AuthorVO> authors,
                        final String submitted, final String description, final String content,
                        final VerbModulesVO occurrence, final VerbModulesVO covariate,
                        final VerbModulesVO process, final VerbModulesVO model,
                        final VerbModulesVO output, final Boolean forceReproducible,
                        final boolean privateArtifact, final Long figshareId)
                        throws IllegalArgumentException {
    super(name, version, authors, submitted, content, ArtifactType.WORKFLOW_CALL, privateArtifact);
    this.description = description;
    this.occurrence = occurrence;
    this.covariate = covariate;
    this.process = process;
    this.model = model;
    this.output = output;
    this.forceReproducible = forceReproducible;
    this.figshareId = figshareId;
  }

  /**
   * Retrieve the elastic entity from the value object.
   * 
   * @return Elastic entity representation.
   */
  public WorkflowCall asWorkflowCall() {
    final JSONObject workflowCall = new JSONObject();
    final JSONObject workflow = new JSONObject();

    try {
      workflowCall.put(JSON_KEY_NAME, getName());
      workflowCall.put(JSON_KEY_DESCRIPTION, getDescription());
      workflowCall.put(JSON_KEY_VERSION, getVersion());

      workflow.put(Argument.occurrence.toString(), getOccurrence().asJSONObject());
      workflow.put(Argument.covariate.toString(), getCovariate().asJSONObject());
      workflow.put(Argument.process.toString(), getProcess().asJSONObject());
      workflow.put(Argument.model.toString(), getModel().asJSONObject());
      workflow.put(Argument.output.toString(), getOutput().asJSONObject());

      workflow.put(Argument.forceReproducible.toString(), isForceReproducible());

      workflowCall.put(JSON_KEY_WORKFLOW, workflow);
    } catch (JSONException e) {
      e.printStackTrace();
      throw new UnsupportedOperationException("WorkflowCallVO is not handling '" + e.getMessage() + "' exception!");
    }

    final List<VerbModules> verbModules = new ArrayList<VerbModules>();
    for (final VerbModulesVO eachVerbModulesVO : retrieveVerbModules(null)) {
      if (eachVerbModulesVO != null) {
        verbModules.add(eachVerbModulesVO.asVerbModules());
      }
    }

    return new WorkflowCall(getName(), getVersion(), AuthorVO.retrieveAuthors(getAuthors()),
                            getSubmitted(),  workflowCall.toString(), verbModules, figshareId);
  }

  /**
   * Used in the following circumstances :
   * <ul>
   *   <li>Parsing private workflow call JSON (for transferring to client)</li>
   *   <li>Parsing elastic workflow call content</li>
   *   <li>Creating a VO when a private workflow call is received by client</li>
   * </ul>
   * 
   * @param workflowCallJSON
   * @param privateArtifact {@code true} if workflow call is private, otherwise {@code false}.
   * @return
   * @throws JSONException
   */
  /* Used for parsing private workflow calls. */
  public static WorkflowCallVO extractWorkflowFromJSON(final String workflowCallJSON,
                                                       final boolean privateArtifact)
                                                       throws JSONException {
    final JSONObject jsonObject = new JSONObject(workflowCallJSON);
    final String name = jsonObject.getString(JSON_KEY_NAME);
    final String version = jsonObject.getString(JSON_KEY_VERSION);
    final String description = jsonObject.getString(JSON_KEY_DESCRIPTION);
    final JSONObject workflowCallObj = jsonObject.getJSONObject(JSON_KEY_WORKFLOW);

    final Iterator<?> callArguments = workflowCallObj.keys();

    VerbModulesVO occurrence = null;
    VerbModulesVO covariate = null;
    VerbModulesVO process = null;
    VerbModulesVO model = null;
    VerbModulesVO output = null;
    Boolean forceReproducible = null;

    while (callArguments.hasNext()) {
      // e.g. occurrence, covariate, forceReproducible
      final String callArgument = (String) callArguments.next();
      Argument argument = null;
      try {
        argument = Argument.valueOf(callArgument);
      } catch (Exception e) {
        final String errorMessage = "Unable to translate call argument '" + callArgument + "' to a system Argument in '" + Argument.values().toString() + "'";
        log.error("~extractWorkflowFromJSON() : ".concat(errorMessage));
        throw new UnsupportedOperationException(errorMessage);
      }

      ProcessingType processingType = null;
      switch (argument) {
        case covariate :
        case model :
        case occurrence :
        case output :
        case process :
          processingType = ProcessingType.module;
          break;
        case forceReproducible :
          processingType = ProcessingType.forceReproducible;
          break;
        default :
          final String errorMessage = "Unrecognised argument type of '" + argument + "' encountered.";
          log.error("~extractWorkflowFromJSON() : ".concat(errorMessage));
          throw new UnsupportedOperationException(errorMessage);
      }

      switch (processingType) {
        case forceReproducible :
          forceReproducible = workflowCallObj.getBoolean(callArgument);
          break;
        case module :
          // There should only be one of 'chain', 'list', etc.
          final JSONObject moduleTypeActions = (JSONObject) workflowCallObj.get(callArgument);
          final Iterator<?> typeActions = moduleTypeActions.keys();

          VerbModulesVO verbModulesVO = null;
          while (typeActions.hasNext()) {
            final String action = (String) typeActions.next();

            final List<ModuleParamsVO> workflowCallModules = new ArrayList<ModuleParamsVO>();

            final Object provisional = moduleTypeActions.get(action);
            if (provisional instanceof JSONObject) {
              log.debug("~extractWorkflowFromJSON() : New style!!!");
              final JSONObject modulesAndParams = (JSONObject) moduleTypeActions.get(action);

              final Iterator<?> moduleParamIterator = modulesAndParams.keys();
              while (moduleParamIterator.hasNext()) {
                final String moduleName = (String) moduleParamIterator.next();
                String moduleVersion = null;
                final JSONObject moduleParamsAndVersion = modulesAndParams.getJSONObject(moduleName);
                final Iterator<?> paramAndVersionIterator = moduleParamsAndVersion.keys();

                final Set<ModuleParamVO> parameters = new HashSet<ModuleParamVO>();
                while (paramAndVersionIterator.hasNext()) {
                  final String paramOrVersionName = (String) paramAndVersionIterator.next();
                  final String paramOrVersionValue = moduleParamsAndVersion.getString(paramOrVersionName);
                  if (MODULE_VERSION_JSON_KEY.equals(paramOrVersionName)) {
                    moduleVersion = paramOrVersionValue;
                  } else {
                    parameters.add(new ModuleParamVO(paramOrVersionName, paramOrVersionValue));
                  }
                }

                workflowCallModules.add(new ModuleParamsVO(moduleName, moduleVersion, parameters));
              }
            } else if (provisional instanceof JSONArray) {
              log.debug("~extractWorkflowFromJSON() : Old style!!!");
              final JSONArray modules = (JSONArray) moduleTypeActions.get(action);

              for (int moduleIdx = 0; moduleIdx < modules.length(); moduleIdx++) {
                final String module = modules.getString(moduleIdx);
                // In the old style we don't know what module version is being used in the workflow call!
                workflowCallModules.add(new ModuleParamsVO(module, null, null));
              }
            }

            Verb verb = null;
            try {
              verb = Verb.valueOf(action);
            } catch (Exception e) {
              final String errorMessage = "Couldn't determine Verb from action '" + action + "'";
              log.error("~extractWorkflowFromJSON() : ".concat(errorMessage));
              throw new UnsupportedOperationException(errorMessage);
            }

            verbModulesVO = new VerbModulesVO(verb, workflowCallModules);
          }

          switch (argument) {
            case covariate :
              covariate = verbModulesVO;
              break;
            case model :
              model = verbModulesVO;
              break;
            case occurrence :
              occurrence = verbModulesVO;
              break;
            case output :
              output = verbModulesVO;
              break;
            case process :
              process = verbModulesVO;
              break;
            case forceReproducible :
              // Ignore
              break;
          }
          break;
      }
    }

    final List<AuthorVO> authors = new ArrayList<AuthorVO>();
    final String submitted = null;

    final WorkflowCallVO extracted = new WorkflowCallVO(name, version, authors, submitted,
                                                        description, workflowCallJSON,
                                                        occurrence, covariate, process, model,
                                                        output, forceReproducible, privateArtifact,
                                                        null);

    log.debug("~extractWorkflowFromJSON() : Extracted '" + extracted.toString() + "'.");

    return extracted;
  }

  /**
   * Extract Workflow Call data from metadata.
   * <p>
   * It's expected that if it's arriving from metadata then all data is provided, otherwise an
   * error will be thrown.
   * 
   * @param name Call name.
   * @param version Call version. 
   * @param authors Call authors.
   * @param submitted Submission/Publication date (or {@code null} if not available).
   * @param description Call description.
   * @param content 
   * @param arguments Call arguments.
   * @param figshareId
   * @throws IllegalArgumentException If the arguments don't contain all necessary data to create a VO.
   */
  public static WorkflowCallVO extractWorkflowFromMetadata(final String name, final String version,
                                                           final List<AuthorVO> authors,
                                                           final String submitted,
                                                           final String description,
                                                           final String content,
                                                           final Map<Argument, String> arguments,
                                                           final Long figshareId)
                                                           throws IllegalArgumentException {
    VerbModulesVO occurrence = null;
    VerbModulesVO covariate = null;
    VerbModulesVO process = null;
    VerbModulesVO model = null;
    VerbModulesVO output = null;
    Boolean forceReproducible = null;

    for (final Map.Entry<Argument, String> argument : arguments.entrySet()) {
      final Argument type = argument.getKey();
      // e.g. Chain(OneHundredBackground,Crossvalidate)
      final String value = argument.getValue();

      if (type == null && StringUtils.isBlank(value)) {
        log.error("~extractWorkflowFromMetadata() : Ignored empty '" + type + "'/'" + value + "' combination!");
        continue;
      }

      switch (type) {
        case covariate :
          covariate = parseVerbModules(value);
          break;
        case model :
          model = parseVerbModules(value);
          break;
        case occurrence :
          occurrence = parseVerbModules(value);
          break;
        case output :
          output = parseVerbModules(value);
          break;
        case process :
          process = parseVerbModules(value);
          break;
        case forceReproducible :
          try {
            forceReproducible = Boolean.valueOf(value).booleanValue();
          } catch (Exception e) {
            log.warn("~extractWorkflowFromMetadata() : Could not determine the forceReproducible value from '" + value + "'. Defaulting to '" + defaultForceReproducible + "'!");
          }
          break;
        default :
          throw new UnsupportedOperationException("Processing of '" + type + "' not yet implemented!");
      }
    }

    if (occurrence == null || covariate == null || process == null || model == null ||
        output == null || forceReproducible == null) {
      final String errorMessage = "Not all modules defined, or forceReproducible not defined";
      log.error("~extractWorkflowFromMetadata() : ".concat(errorMessage));
      throw new IllegalArgumentException(errorMessage);
    }

    final WorkflowCallVO extracted = new WorkflowCallVO(name, version, authors, submitted,
                                                        description, content,
                                                        occurrence, covariate, process, model,
                                                        output, forceReproducible, false,
                                                        figshareId);

    log.debug("~extractWorkflowFromMetadata() : Extracted '" + extracted.toString() + "'.");

    return extracted;
  }

  /**
   * Indicate if the Workflow Call has all necessary data assigned.
   * 
   * @return {@code true} if a valid workflow call, otherwise {@code false}.
   */
  public boolean isComplete() {
    // Parent data (should have been!) verified on construction.
    if (getCovariate() != null && getModel() != null && getOccurrence() != null &&
        getOutput() != null && getProcess() != null && isForceReproducible() != null) {
      return true;
    }

    log.info("~isComplete() : Incomplete '" + this.toString() + "'!");
    return false;
  }

  /**
   * Parse the content of verb-modules strings.
   * 
   * @param value Verb-Modules strings, e.g. {@code Chain(OneHundredBackground,Crossvalidate)}.
   * @return Value object reflecting string content.
   * @throws IllegalArgumentException
   */
  public static VerbModulesVO parseVerbModules(final String verbModules) {
    if (StringUtils.isBlank(verbModules)) {
      throw new IllegalArgumentException("Blank verbModules string passed to parsing routine");
    }

    // Remove spaces in string.
    final String compressedVerbModules = verbModules.replace(" ", "");

    Verb verb = null;
    for (final Verb eachVerb : Verb.values()) {
      // We're looking for 'Chain(' or 'list(' initial texts
      final String checkFor = eachVerb.toString().concat("(");
      if (compressedVerbModules.startsWith(checkFor)) {
        verb = eachVerb;
        break;
      }
    }

    /* Going to assume that if verbModule doesn't start with Chain( or list( then it's a select */
    if (verb == null) {
      log.info("~parseVerbModules() : No verb (e.g. Chain) at start of '" + verbModules + "'. Assuming 'select'.");
      verb = Verb.select;
    }

    final List<ModuleParamsVO> modules = new ArrayList<ModuleParamsVO>();
    switch (verb) {
      case Chain :
      case list :
        // Strip the opening and closing brackets.
        final int verbLength = verb.toString().length();
        final String modulesOnlyText = compressedVerbModules.substring(verbLength);
        final String firstChar = modulesOnlyText.substring(0, 1);
        final int startLastCharAt = modulesOnlyText.length() - 1;
        final String lastChar = modulesOnlyText.substring(startLastCharAt, startLastCharAt + 1);

        if ("(".equals(firstChar) && ")".equals(lastChar)) {
          final String bracketsRemovedValue = modulesOnlyText.substring(1, modulesOnlyText.length() - 1);
          if (bracketsRemovedValue.contains("(")) {
            // TODO : Parsing of Figshare workflow calls containing modules with parameter values!
            final String errorMessage = "Not yet implemented ability to parse Figshare workflow call data containing module parameter values!";
            log.error("~parseVerbModules() : ".concat(errorMessage));
            throw new UnsupportedOperationException(errorMessage);
          }
          log.debug("~parseVerbModule() : bracketsRemovedValue '" + bracketsRemovedValue + "'.");
          for (final String moduleName : Arrays.asList(bracketsRemovedValue.split(","))) {
            modules.add(new ModuleParamsVO(moduleName, null, null));
          }
        } else {
          final String errorMessage = "Parsing verbModules '" + verbModules + "' for '" + verb + "' as brackets not found";
          log.error("~parseVerbModule() : ".concat(errorMessage));
          throw new UnsupportedOperationException(errorMessage);
        }
        break;
      case select :
        modules.add(new ModuleParamsVO(compressedVerbModules, null, null));
        break;
    }

    final VerbModulesVO verbModulesVO = new VerbModulesVO(verb, modules);
    log.debug("~parseVerbModules() : Created '" + verbModulesVO.toString() + "' from '" + verbModules + "'.");

    return verbModulesVO;
  }

  /**
   * Indicate if this workflow call references the specified module, either by name, or name and
   * version.
   * 
   * @param moduleName Module name.
   * @param moduleVersion Optional module version.
   * @return {@code true} if module referenced, otherwise false.
   */
  public boolean referencesModule(final String moduleName, final String moduleVersion,
                                  final ModuleType moduleType) {
    log.debug("~referencesModule() : Seeking module name '" + moduleName + "', version '" + moduleVersion + "' in workflow.");
    boolean referencesModule = false;
    // Traverse the workflow call's verb-modules to find specified module.
    for (final VerbModulesVO eachVerbModuleVO : retrieveVerbModules(moduleType)) {
      if (eachVerbModuleVO != null) {
        for (final ModuleParamsVO eachModuleParamsVO : eachVerbModuleVO.getModuleParams()) {
          final String eachModuleName = eachModuleParamsVO.getModuleName();
          final String eachModuleVersion = eachModuleParamsVO.getModuleVersion();
          if (eachModuleName.equals(moduleName) &&
              (eachModuleVersion == null || moduleVersion.equals(eachModuleVersion))) {
            // If eachModuleVersion == null then it's a public workflow call with no module version data!
            log.debug("~referencesModule() : '" + getName() + "/" + getVersion() + "' references '" + moduleName + "" + moduleVersion + "'!");
            referencesModule = true;
            break;
          }
        }
        if (referencesModule) {
          break;
        }
      }
    }

    return referencesModule;
  }

  private List<VerbModulesVO> retrieveVerbModules(final ModuleType moduleType) {
    final List<VerbModulesVO> verbModulesVOs = new ArrayList<VerbModulesVO>();
    if (moduleType == null) {
      verbModulesVOs.add(getOccurrence());
      verbModulesVOs.add(getCovariate());
      verbModulesVOs.add(getProcess());
      verbModulesVOs.add(getModel());
      verbModulesVOs.add(getOutput());
    } else {
      switch (moduleType) {
        case occurrence :
          verbModulesVOs.add(getOccurrence());
          break;
        case covariate :
          verbModulesVOs.add(getCovariate());
          break;
        case process :
          verbModulesVOs.add(getProcess());
          break;
        case model :
          verbModulesVOs.add(getModel());
          break;
        case output :
          verbModulesVOs.add(getOutput());
          break;
        default :
          throw new UnsupportedOperationException("Unrecognised module type of '" + moduleType + "'.");
      }
    }
    return verbModulesVOs;
  }

  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "WorkflowCallVO [description=" + description + ", occurrence="
        + occurrence + ", covariate=" + covariate + ", process=" + process
        + ", model=" + model + ", output=" + output + ", forceReproducible="
        + forceReproducible + ", toString()=" + super.toString() + "]";
  }

  /**
   * @return the description
   */
  public String getDescription() {
    return description;
  }

  /**
   * @return the occurrence
   */
  public VerbModulesVO getOccurrence() {
    return occurrence;
  }

  /**
   * Retrieve a single-quote-escaped representation of the content.
   * 
   * @return The content, with any single quotes escaped.
   */
  public String getContentEscaped() {
    log.debug("~getContentEscaped() : Invoked.");
    String content = getContent();
    if (content != null && content.contains("'")) {
      content = content.replace("\'", "\\\'");
    }
    return content;
  }

  /**
   * @return the covariate
   */
  public VerbModulesVO getCovariate() {
    return covariate;
  }

  /**
   * @return the process
   */
  public VerbModulesVO getProcess() {
    return process;
  }

  /**
   * @return the model
   */
  public VerbModulesVO getModel() {
    return model;
  }

  /**
   * @return the output
   */
  public VerbModulesVO getOutput() {
    return output;
  }

  /**
   * @return the forceReproducible
   */
  public Boolean isForceReproducible() {
    return forceReproducible;
  }

  /**
   * @return the figshare id
   */
  public Long getFigshareId() {
    return figshareId;
  }
}
