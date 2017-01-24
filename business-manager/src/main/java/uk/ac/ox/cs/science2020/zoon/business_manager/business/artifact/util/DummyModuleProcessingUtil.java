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
package uk.ac.ox.cs.science2020.zoon.business_manager.business.artifact.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.json.JSONObject;

import uk.ac.ox.cs.science2020.zoon.business_manager.business.error.ModuleHeaderException;
import uk.ac.ox.cs.science2020.zoon.business_manager.value.object.moduleheader.HeaderObject;
import uk.ac.ox.cs.science2020.zoon.business_manager.value.object.moduleheader.ModuleTypeVO;
import uk.ac.ox.cs.science2020.zoon.business_manager.value.object.moduleheader.MultiValueVO;
import uk.ac.ox.cs.science2020.zoon.business_manager.value.object.moduleheader.ParametersVO;
import uk.ac.ox.cs.science2020.zoon.business_manager.value.object.moduleheader.SingleValueVO;
import uk.ac.ox.cs.science2020.zoon.business_manager.value.type.ModuleHeader;
import uk.ac.ox.cs.science2020.zoon.shared.business.artifact.util.ModuleProcessingUtil;
import uk.ac.ox.cs.science2020.zoon.shared.value.type.DataIdentifier;
import uk.ac.ox.cs.science2020.zoon.shared.value.type.ModuleType;

/**
 * This is the utility which Tom will need to replace with a R-coded module parsing system to pass
 * back the required identifiers.
 *
 * @author 
 */
public class DummyModuleProcessingUtil {

  private static final String authorConjunctionAmpersand = " & ";
  private static final String authorConjunctionAnd = " and ";
  private static final String authorConjunctionReplacement = "¬";

  private static final String moduleHeaderCandidateNote = "note";
  private static final String moduleHeaderCandidateSection = "section";

  private static final String parsedJSONKeyAuthor = "author";
  private static final String parsedJSONKeyDescription = "description";
  private static final String parsedJSONKeyDetails = "details";
  private static final String parsedJSONKeyFamily = "family";
  private static final String parsedJSONKeyName = "name";
  private static final String parsedJSONKeyParamDescription = "description";
  private static final String parsedJSONKeyParamDot = "param.";
  private static final String parsedJSONKeyParamName = "name";
  private static final String parsedJSONKeyReferences = "references";
  private static final String parsedJSONKeyReturn = "return";
  private static final String parsedJSONKeySectionDot = "section.";
  private static final String parsedJSONKeySectionVersion = "version";
  private static final String parsedJSONKeySectionDataType = "data type";
  private static final String parsedJSONKeySectionDateSubmitted = "date submitted";
  private static final String parsedJSONKeySeeAlso = "seealso";
  private static final String parsedJSONKeySrcRef = "srcref";
  private static final String parsedJSONKeyTitle = "title";

  private static final Set<String> authorConjunctions = new HashSet<String>();
  private static final Map<String, ModuleHeader> roxygenIdentifiers = new HashMap<String, ModuleHeader>();
  private static final Set<ModuleHeader> minimumHeaderLines = new HashSet<ModuleHeader>();
  private static final String tomsParsingFail1 = "Parsing failed, here are the error messages:";
  private static final String tomsParsingFail2 = "This module has no roxygen metadata";

  private static final Log log = LogFactory.getLog(DummyModuleProcessingUtil.class);

  static {
    final Set<String> minimumHeaderNameCollection = new HashSet<String>();
    for (final ModuleHeader moduleHeader : ModuleHeader.values()) {
      if (moduleHeader.isRequired()) {
        minimumHeaderLines.add(moduleHeader);
        minimumHeaderNameCollection.add(moduleHeader.name());
      }
      if (moduleHeader.getRoxygenIdentifier() != null) {
        roxygenIdentifiers.put(moduleHeader.getRoxygenIdentifier(), moduleHeader);
      }
    }

    authorConjunctions.add(authorConjunctionAmpersand);
    authorConjunctions.add(authorConjunctionAnd);
  }

  /**
   * 
   * 
   * @param allModuleData
   * @param moduleRepository Source repository of module data.
   * @param parsedModuleJSON JSON string output of Tom's R module parsing script.
   * @param moduleContent Content of module.
   * @param verified {@code true} if module has been verified, otherwise {@code false}.
   * @throws ModuleHeaderException
   */
  public static void aggregateModuleData(final Map<String, List<Map<DataIdentifier, HeaderObject>>> allModuleData,
                                         final String moduleRepository,
                                         final String parsedModuleJSON,
                                         final String moduleContent,
                                         final boolean verified)
                                         throws ModuleHeaderException {
    log.debug("~aggregateModuleData() : Invoked.");

    if (parsedModuleJSON == null) {
      log.error("~aggregateModuleData() : Parsed Module JSON is null!");
      return;
    }

    final Map<ModuleHeader, HeaderObject> latest = processParsingOutput(parsedModuleJSON);
    if (latest.isEmpty()) {
      log.warn("~aggregateModuleData() : No parsed JSON available. Cannot aggregate '" + parsedModuleJSON + "!");
      return;
    }

    final Map<DataIdentifier, HeaderObject> latestModuleData = new HashMap<DataIdentifier, HeaderObject>();
    latestModuleData.put(DataIdentifier.LOCATION, new SingleValueVO(moduleRepository));

    // 1. The minimum amount of data to return.
    for (final ModuleHeader minimalHeader : minimumHeaderLines) {
      DataIdentifier dataIdentifier = null;
      HeaderObject headerObject = latest.get(minimalHeader);
      switch (minimalHeader) {
        case NAME :
          dataIdentifier = DataIdentifier.NAME;
          break;
        case FAMILY :
          dataIdentifier = DataIdentifier.TYPE;
          break;
        case VERSION :
          dataIdentifier = DataIdentifier.VERSION;
          break;
        default :
          throw new UnsupportedOperationException("Unknown how to map from a '" + minimalHeader + "' to a DataIdentifier.");
      }
      latestModuleData.put(dataIdentifier, headerObject);
    }

    // Now the rest!
    latestModuleData.put(DataIdentifier.CONTENT, new SingleValueVO(moduleContent));
    latestModuleData.put(DataIdentifier.VERIFIED, new SingleValueVO(Boolean.valueOf(verified).toString()));

    for (final ModuleHeader moduleHeader : ModuleHeader.values()) {
      if (minimumHeaderLines.contains(moduleHeader)) {
        // We're covered this base!
        continue;
      }

      HeaderObject headerObject = latest.get(moduleHeader);
      if (headerObject == null) {
        continue;
      }

      DataIdentifier dataIdentifier = null;
      switch (moduleHeader) {
        case AUTHOR :
          dataIdentifier = DataIdentifier.AUTHOR;
          break;
        case SOURCE :
          dataIdentifier = DataIdentifier.SOURCE;
          break;
        case DESCRIPTION :
          dataIdentifier = DataIdentifier.DESCRIPTION;
          break;
        case PARAM :
          dataIdentifier = DataIdentifier.PARAM;
          break;
        case RETURN :
          dataIdentifier = DataIdentifier.RETURN;
          break;
        case DETAILS :
          dataIdentifier = DataIdentifier.DETAILS;
          break;
        case DOCTYPE :
          dataIdentifier = DataIdentifier.DOCTYPE;
          break;
        case TITLE :
          dataIdentifier = DataIdentifier.TITLE;
          break;
        case NOTE :
          dataIdentifier = DataIdentifier.SUBMITTED;
          break;
        case REFERENCES :
          dataIdentifier = DataIdentifier.REFERENCES;
          break;
        case ALIAS :
        case ALIASES :
        case BACKREF :
        case CONCEPT :
        case CONCEPTS :
        case DESCRIBEIN :
        case EXAMPLE :
        case EXAMPLES :
        case EXPORT :
        case EXPORTCLASS :
        case EXPORTMETHOD :
        case FAMILY :
          // Family handled in minimal header
        case FIELD :
        case FORMAT :
        case IMPORT :
        case IMPORTCLASSESFROM :
        case IMPORTFROM :
        case IMPORTMETHODSFROM :
        case INCLUDE :
        case INHERITPARAMS :
        case KEYWORDS :
        case METHOD :
        case NAME :
          // Name handled in minimal header
        case RDNAME :
        case S3METHOD :
        case SECTION :
        case SEEALSO :
        case SLOT :
        case TEMPLATE :
        case TEMPLATEVAR :
        case TYPE :
        case USAGE :
        case VERSION :
          // Version handled in minimal header
          break;
        default :
          throw new UnsupportedOperationException("Unrecognised module header of '" + moduleHeader + "' encountered.");
      }
      if (dataIdentifier != null) {
        if (latestModuleData.containsKey(dataIdentifier)) {
          log.debug("~aggregateModuleData() : Assuming '" + dataIdentifier + "' already specified in minimal data!");
        } else {
          latestModuleData.put(dataIdentifier, headerObject);
        }
      }
    }

    // Now aggregate with what's already been read in.
    // TODO : Version-specific aggregation!!
    final String moduleName = getModuleName(latest);
    if (allModuleData.containsKey(moduleName)) {
      allModuleData.get(moduleName).add(latestModuleData);
    } else {
      final List<Map<DataIdentifier, HeaderObject>> newModuleDataCollection = new ArrayList<Map<DataIdentifier, HeaderObject>>();
      newModuleDataCollection.add(latestModuleData);
      allModuleData.put(moduleName, newModuleDataCollection);
    }
  }

  /**
   * Retrieve the module name from the header objects.
   * 
   * @param headerObjects Module header objects.
   * @return Module name.
   * @throws IllegalStateException If no module name found.
   */
  public static String getModuleName(final Map<ModuleHeader, HeaderObject> headerObjects)
                                     throws IllegalStateException {
    final SingleValueVO singleValueVO = (SingleValueVO) headerObjects.get(ModuleHeader.NAME);
    if (singleValueVO == null) {
      throw new IllegalStateException("Dodgy Module? Can't find the Module name!");
    }
    return singleValueVO.getComment();
  }

  /**
   * Retrieve the module version from the header objects.
   * 
   * @param headerObjects Module header objects.
   * @return Module version.
   * @throws IllegalStateException If no module version found.
   */
  public static String getModuleVersion(final Map<ModuleHeader, HeaderObject> headerObjects) {
    final SingleValueVO singleValueVO = (SingleValueVO) headerObjects.get(ModuleHeader.VERSION);
    if (singleValueVO == null) {
      throw new IllegalStateException("Dodgy Module? Can't find the Module version!");
    }
    return singleValueVO.getComment();
  }

  /**
   * Takes Tom's module-parsing JSON output as {@code parsingOutput} and loads up a collection of 
   * module header objects.
   * 
   * @param parsingOutput Tom's module-parsing output (JSON format if successful, otherwise unknown!).
   * @return Collection of module header objects.
   * @throws ModuleHeaderException If problems processing message headers.
   */
  public static Map<ModuleHeader, HeaderObject> processParsingOutput(final String parsingOutput)
                                                                     throws ModuleHeaderException {
    log.debug("~parseJSONContent() : Invoked.");
    final Map<ModuleHeader, HeaderObject> parsedHeader = new HashMap<ModuleHeader, HeaderObject>();
    log.debug("~parseJSONContent() : Module Content '" + parsingOutput + "'.");

    final Set<String> problems = new HashSet<String>();
    if (parsingOutput.startsWith(tomsParsingFail1)) {
      final String infoMessage = "Parsing content failed : '" + parsingOutput + "'.";
      log.info("~parseJSONContent() : ".concat(infoMessage));
      problems.add(infoMessage);
      throw new ModuleHeaderException(problems);
    }
    if (parsingOutput.startsWith(tomsParsingFail2)) {
      final String infoMessage = "Parsing content failed : '" + parsingOutput + "'.";
      log.info("~parseJSONContent() : ".concat(infoMessage));
      problems.add(infoMessage);
      throw new ModuleHeaderException(problems);
    }

    final Map<Integer, JSONObject> sortedParams = new TreeMap<Integer, JSONObject>();
    try {
      final JSONObject jsonObject = new JSONObject(parsingOutput);
      final Iterator<?> jsonKeys = jsonObject.keys();

      while (jsonKeys.hasNext()) {
        final String jsonKey = (String) jsonKeys.next();
        String moduleHeaderCandidate = jsonKey;
        final Object keyedJSONObject = jsonObject.get(jsonKey);

        HeaderObject recognisedObject = null;
        if (parsedJSONKeyTitle.equalsIgnoreCase(jsonKey) ||
            parsedJSONKeyName.equalsIgnoreCase(jsonKey) ||
            parsedJSONKeyDetails.equalsIgnoreCase(jsonKey)) {
          recognisedObject = new SingleValueVO((String) keyedJSONObject);
        } else if (parsedJSONKeyReferences.equalsIgnoreCase(jsonKey)) {
          recognisedObject = new SingleValueVO((String) keyedJSONObject);
        } else if (parsedJSONKeyAuthor.equalsIgnoreCase(jsonKey)) {
          // Sometimes, e.g. LocalRaster, the authors are separated by ' and '!
          final String rawAuthors = (String) keyedJSONObject;
          String authors = rawAuthors;
          String email = null;
          final String[] splitOnEmail = rawAuthors.split("\\".concat(ModuleProcessingUtil.EMAIL_BACKSLASHED));
          if (splitOnEmail.length == 2) {
            // If there's only a single email (the majority case), consider the author(s) to be the
            // text before backslashed email text.
            authors = splitOnEmail[0];
            email = splitOnEmail[1];
          }
          // Substitute the author conjuntion text (" and " or " & ") with a special char.
          if (authors.contains(authorConjunctionReplacement)) {
            authors = authors.replace(authorConjunctionReplacement, authorConjunctionAnd);
          }
          for (final String conjunction : authorConjunctions) {
            if (authors.contains(conjunction)) {
              authors = authors.replace(conjunction, authorConjunctionReplacement);
            }
          }
          if (authors.contains(authorConjunctionReplacement)) {
            // Authors contained the " and " or " & " text, i.e. multiple authors.
            String[] authorComponents = authors.split(authorConjunctionReplacement);

            MultiValueVO authorHeaderObject = null;
            for (final String eachAuthor : authorComponents) {
              if (authorHeaderObject == null) {
                String useAuthor = eachAuthor;
                if (email != null) {
                  // Append the single email to first or two authors.
                  useAuthor += ModuleProcessingUtil.EMAIL_BACKSLASHED.concat(email);
                }
                authorHeaderObject = new MultiValueVO(useAuthor);
              } else {
                authorHeaderObject.getComments().add(eachAuthor);
              }
            }
            recognisedObject = authorHeaderObject;
          } else {
            recognisedObject = new MultiValueVO((String) keyedJSONObject);
          }
        } else if (parsedJSONKeyDescription.equalsIgnoreCase(jsonKey) ||
                   parsedJSONKeyReturn.equalsIgnoreCase(jsonKey) ||
                   parsedJSONKeySeeAlso.equalsIgnoreCase(jsonKey)) {
          recognisedObject = new MultiValueVO((String) keyedJSONObject);
        } else if (parsedJSONKeySrcRef.equalsIgnoreCase(jsonKey)) {
          // Not sure what to do with this!
        } else if (parsedJSONKeyFamily.equalsIgnoreCase(jsonKey)) {
          final String moduleTypeCandidate = (String) keyedJSONObject;

          boolean foundSynonym = false;
          for (final ModuleType moduleType : ModuleType.values()) {
            for (final String synonym : moduleType.getSynonyms()) {
              if (synonym.equalsIgnoreCase(moduleTypeCandidate)) {
                recognisedObject = new ModuleTypeVO(moduleType);
                foundSynonym = true;
                break;
              }
            }
            if (foundSynonym) {
              break;
            }
          }
          if (!foundSynonym) {
            problems.add("Couldn't determine family of '" + jsonKey + "'.");
          }
        } else if (jsonKey.startsWith(parsedJSONKeySectionDot)) {
          final String fullString = (String) keyedJSONObject;
          final String[] parts = fullString.split(":");
          if (parts.length > 1) {
            final String part1 = parts[0];
            final String part2 = fullString.substring(fullString.indexOf(":") + 1).trim();
            if (parsedJSONKeySectionVersion.equalsIgnoreCase(part1)) {
              moduleHeaderCandidate = part1;
              recognisedObject = new SingleValueVO(part2);
            } else if (parsedJSONKeySectionDateSubmitted.equalsIgnoreCase(part1)) {
              moduleHeaderCandidate = moduleHeaderCandidateNote;
              recognisedObject = new SingleValueVO(part2);
            } else if (parsedJSONKeySectionDataType.equalsIgnoreCase(part1)) {
              moduleHeaderCandidate = moduleHeaderCandidateSection;
              recognisedObject = new MultiValueVO(part2);
            }
          }
        } else if (jsonKey.startsWith(parsedJSONKeyParamDot)) {
          final String rawParamNumber = jsonKey.replace(parsedJSONKeyParamDot, "");
          Integer paramNumber = null;
          try {
            paramNumber = Integer.valueOf(rawParamNumber);
            sortedParams.put(paramNumber, (JSONObject) keyedJSONObject);
          } catch (NumberFormatException e) {
            log.error("~parseJSONContent() : Failed to extract a number from '" + jsonKey + "'.");
          }
        } else {
          log.warn("~parseJSONContent() : Unrecognised key '" + jsonKey + "'.");
          problems.add("Unrecognised '" + jsonKey + "'.");
        }

        if (recognisedObject != null) {
          log.debug("~parseJSONContent() : Candidate '" + moduleHeaderCandidate + "', object '" + recognisedObject + "'.");
          final ModuleHeader moduleHeader = ModuleHeader.valueOf(moduleHeaderCandidate.toUpperCase());
          if (parsedHeader.containsKey(moduleHeader)) {
            final String errorMessage = "Overwriting the module header value for '" + moduleHeader + "'";
            log.warn("~parseJSONContent() : " + errorMessage);
            problems.add(errorMessage);
          }
          parsedHeader.put(moduleHeader, recognisedObject);
        } else {
          // This incorrectly flags up warnings for 'param.?' encounters.
          // srcref encounters are ignored.
          log.info("~parseJSONContent() : Couldn't translate JSON key '" + jsonKey + "'.");
        }
      }
      if (!sortedParams.isEmpty()) {
        ParametersVO parametersVO = null;
        for (final Map.Entry<Integer, JSONObject> sortedParam : sortedParams.entrySet()) {
          final JSONObject parameterJSONObject = sortedParam.getValue();
          final String name = parameterJSONObject.getString(parsedJSONKeyParamName);
          if (name != null && !name.startsWith(".")) {
            final String description = parameterJSONObject.getString(parsedJSONKeyParamDescription);
            if (parametersVO == null) {
              parametersVO = new ParametersVO(name, description);
            } else {
              parametersVO.getParamInfo().add(parametersVO.new ParamInfo(name, description));
            }
          } else {
            log.debug("~parseJSONContent() : Ignoring parameter '" + name + "'.");
          }
        }
        parsedHeader.put(ModuleHeader.PARAM, parametersVO);
      }
    } catch (JSONException e) {
      final String errorMessage = "Error parsing JSON '" + e.getMessage() + "'";
      log.error("~parseJSONContent() : " + errorMessage);
      problems.add(errorMessage);
    }

    if (!problems.isEmpty()) {
      throw new ModuleHeaderException(problems);
    }

    return parsedHeader;
  }
}