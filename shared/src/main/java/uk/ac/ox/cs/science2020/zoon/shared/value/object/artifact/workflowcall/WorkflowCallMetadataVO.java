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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import uk.ac.ox.cs.science2020.zoon.shared.value.object.artifact.AuthorVO;
import uk.ac.ox.cs.science2020.zoon.shared.value.type.workflowcall.Argument;

/**
 * Object holding Workflow Call representations, currently from Figshare.
 *
 * @author geoff
 */
public class WorkflowCallMetadataVO {

  private enum DataKey {
    title(new String[] { "title" } ),
    created(new String[] { "created" } ),
    description(new String[] { "description" } ),
    authors(new String[] { "authors" } ),
    categories(new String[] { "categories" } ),
    tags(new String[] { "tags" } ),
    call(new String[] { "call" } ),
    moduleVersions(new String[] { "module versions" } );

    private Set<String> alternatives = new HashSet<String>();

    private DataKey(final String[] alternatives) {
      this.alternatives.addAll(new HashSet<String>(Arrays.asList(alternatives)));
    }
  }

  private final String title;
  private final String created;
  private final String description;
  private final String authors;
  private final String categories;
  private final String tags;
  private final WorkflowCallVO workflowCallVO;
  private final String versions;

  private static final Log log = LogFactory.getLog(WorkflowCallMetadataVO.class);

  /**
   * Initialising constructor.
   * 
   * @param data Data parsed from the metadata, e.g. k:Title; v: Anopheles analysis
   * @param figshareId Figshare Id.
   */
  public WorkflowCallMetadataVO(final Map<String, String> data, final Long figshareId) {
    String title = null;
    String created = null;
    String description = null;
    String authors = null;
    String categories = null;
    String tags = null;
    String call = null;
    String versions = null;
    for (final Map.Entry<String, String> dataEntry : data.entrySet()) {
      final String key = dataEntry.getKey();

      if (!StringUtils.isBlank(key)) {
        for (final DataKey dataKey : DataKey.values()) {
          boolean found = false;
          for (final String dataKeyAlternative : dataKey.alternatives) {
            if (key.equalsIgnoreCase(dataKeyAlternative)) {
              found = true;
              break;
            }
          }
          if (found) {
            final String value = dataEntry.getValue();
            switch (dataKey) {
              case title:
                title = value;
                break;
              case created:
                created = value;
                break;
              case description:
                description = value;
                break;
              case authors:
                authors = value;
                break;
              case categories:
                categories = value;
                break;
              case tags:
                tags = value;
                break;
              case call:
                call = value;
                break;
              case moduleVersions:
                versions = value;
                break;
            }
          }
        }
      }
    }

    this.title = title;
    this.created = created;
    this.description = description;
    this.authors = authors;
    this.categories = categories;
    this.tags = tags;

    this.workflowCallVO = parseCallText(title, WorkflowCallVO.DEFAULT_WORKFLOW_CALL_VERSION_NUMBER,
                                        authors, created, description, call, figshareId); 
    this.versions = versions;
  }

  /**
   * Indicate if the workflow has been assigned all necessary data.
   * 
   * @return {@code true} if all necessary data is present, otherwise {@code false}.
   */
  protected boolean isComplete() {
    boolean validState = true;
    if (StringUtils.isBlank(title) || StringUtils.isBlank(created) ||
        StringUtils.isBlank(description) || StringUtils.isBlank(authors) ||
        StringUtils.isBlank(categories) || StringUtils.isBlank(tags) ||
        !workflowCallVO.isComplete() || StringUtils.isBlank(versions)) {
      validState = false;
    }
    return validState;
  }

  /**
   * Parse the Workflow Call text.
   * 
   * @param title Title.
   * @param version Version.
   * @param authorsText Authors text string.
   * @param submitted Date submitted/published (or {@code null} if not available).
   * @param description Description.
   * @param workflowCallText Workflow call text as it appears in the metadata.
   * @param figshareId
   * @return Parsed WorkflowCallVO object.
   */
  public static WorkflowCallVO parseCallText(final String title, final String version,
                                             final String authorsText, final String submitted,
                                             final String description,
                                             final String workflowCallText,
                                             final Long figshareId) {
    String compressedWorkflowCall = workflowCallText.replace(" ", "");

    if (!compressedWorkflowCall.substring(0, 9).equalsIgnoreCase("workflow(")) {
      log.debug("~parseCallText() : Call must begin with text 'workflow('!");
      return null;
    }
    if (!compressedWorkflowCall.endsWith(")")) {
      log.debug("~parseCallText() : Call must end with text ')'!");
      return null;
    }

    compressedWorkflowCall = compressedWorkflowCall.substring(9, compressedWorkflowCall.length() - 1);
    final String ucWorkflowCall = compressedWorkflowCall.toUpperCase();

    /*
     * Place string-indexed locations of argument sequences, e.g. ',process=' into collection.
     * Convert to upper case for argument comparison.
     */
    final Map<Integer, Argument> locator = new TreeMap<Integer, Argument>();
    for (final Argument argument : Argument.values()) {
      final String text1 = ",".concat(argument.toString()).concat("=").toUpperCase();
      final int located1 = ucWorkflowCall.indexOf(text1);
      if (located1 > -1) {
        locator.put(located1, argument);
        continue;
      }

      final String text2 = argument.toString().concat("=").toUpperCase();
      final int located2 = ucWorkflowCall.indexOf(text2);
      if (located2 > -1) {
        locator.put(located2, argument);
        continue;
      }

      log.debug("~parseCallText() : No argument found in workflow call corresponding to '" + argument + "'.");
      return null;
    }

    // Transfer the starting indexes into an ordered set.
    final Set<Integer> locations = new TreeSet<Integer>(locator.keySet());
    final List<String> indexRanges = new ArrayList<String>(locations.size());

    StringBuffer currentIndex = new StringBuffer();
    Integer finalIndex = null;
    // Build up a collection of start/end index locations, e.g. [0,30, 31,46, 47,75, 76,100, ...
    for (final Integer location : locations) {
      if (currentIndex.length() == 0) {
        currentIndex.append(location).append(",");
      } else {
        currentIndex.append(location);
        indexRanges.add(currentIndex.toString());
        // Add one after the first argument because there's leading commas, e.g. ,covariate=
        final Integer compensateForCommaIndex = location + 1;
        // Reset for next
        currentIndex = new StringBuffer().append(compensateForCommaIndex).append(",");
        // Record for last section.
        finalIndex = compensateForCommaIndex;
      }
    }
    indexRanges.add(finalIndex.toString() + "," + Integer.valueOf(compressedWorkflowCall.length()).toString());

    // Extract the sections
    final Map<Argument, String> sections = new HashMap<Argument, String>();

    final List<Argument> arguments = new ArrayList<Argument>(locator.values());

    int count = 0;
    for (final String indexRange : indexRanges) {
      final String[] rangeComponents = indexRange.split(",");
      final String section = compressedWorkflowCall.substring(Integer.valueOf(rangeComponents[0]),
                                                    Integer.valueOf(rangeComponents[1]));
      // There's still the original text in there, e.g. occurrence=UKAnophelesPlumbeus
      final String[] sectionComponents = section.split("=", 2);
      if (sectionComponents.length == 2) {
        sections.put(arguments.get(count++), sectionComponents[1]);
      }
    }

    final List<AuthorVO> authors = new ArrayList<AuthorVO>();
    if (!StringUtils.isBlank(authorsText)) {
      authors.add(new AuthorVO(authorsText, null));
    }

    return WorkflowCallVO.extractWorkflowFromMetadata(title, version, authors, submitted,
                                                      description, workflowCallText, sections,
                                                      figshareId);
  }

  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "WorkflowCallMetadataVO [title=" + title + ", created=" + created
        + ", description=" + description + ", authors=" + authors
        + ", categories=" + categories + ", tags=" + tags + ", workflowCallVO=" + workflowCallVO
        + ", versions=" + versions + "]";
  }

  /**
   * @return the title
   */
  public String getTitle() {
    return title;
  }

  /**
   * @return the created
   */
  public String getCreated() {
    return created;
  }

  /**
   * @return the description
   */
  public String getDescription() {
    return description;
  }

  /**
   * @return the authors
   */
  public String getAuthors() {
    return authors;
  }

  /**
   * @return the categories
   */
  public String getCategories() {
    return categories;
  }

  /**
   * @return the tags
   */
  public String getTags() {
    return tags;
  }

  /**
   * @return the call
   */
  public WorkflowCallVO getWorkflowCallVO() {
    return workflowCallVO;
  }

  /**
   * @return the versions
   */
  public String getVersions() {
    return versions;
  }
}