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
package uk.ac.ox.cs.science2020.zoon.shared.entity.elastic.workflowcall;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldIndex;
import org.springframework.data.elasticsearch.annotations.FieldType;

import uk.ac.ox.cs.science2020.zoon.shared.entity.elastic.Artifact;
import uk.ac.ox.cs.science2020.zoon.shared.entity.elastic.Author;
import uk.ac.ox.cs.science2020.zoon.shared.value.object.artifact.workflowcall.WorkflowCallVO;

/**
 * Workflow Call entity for elastic repository.
 *
 * @author geoff
 * @see WorkflowCallVO
 */
@Document(indexName="zoon", type="workflow_call")
public class WorkflowCall extends Artifact {

  private String content;
  @Field(type=FieldType.Nested, index=FieldIndex.not_analyzed, store=true)
  private List<VerbModules> verbModules = new ArrayList<VerbModules>();
  private Long figshareId;

  /**
   * Default constructor.
   */
  protected WorkflowCall() {}

  /**
   * Initialising constructor.
   * 
   * @param name Workflow Call name.
   * @param version Workflow Call version.
   * @param authors Author(s).
   * @param submitted Date submitted/published (or {@code null} if not available).
   * @param content Partial representation ('name', 'description', 'version' and 'workflow') of 
   *                Workflow Call content (in JSON format).
   * @param verbModules Ordered collection (i.e. 'occurrence' first -> 'output' last) of the verbs 
   *                    ('select', 'list', 'Chain') and their module name/version associations.
   * @param figshareId Figshare id.
   */
  // Called in Business Manager RunOnce (indirectly via the WorkflowCallVO.asWorkflowCall() method)
  // when loading workflow calls into elastic.
  public WorkflowCall(final String name, final String version, final List<Author> authors,
                      final String submitted, final String content,
                      final List<VerbModules> verbModules, final Long figshareId) {
    super(name, version, authors, submitted);
    setContent(content);
    setVerbModules(verbModules);
    setFigshareId(figshareId);
  }

  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "WorkflowCall [content=" + content + ", verbModules=" + verbModules
        + ", figshareId=" + figshareId + ", toString()=" + super.toString()
        + "]";
  }

  /**
   * Retrieve the partial representation ('name', 'description', 'version' and 'workflow call') of
   * Workflow Call content in JSON format.
   * 
   * @return the content
   */
  public String getContent() {
    return content;
  }

  /**
   * Assign the partial representation ('name', 'description', 'version' and 'workflow call') of
   * Workflow Call content in JSON format.
   * 
   * @param content Content to set.
   */
  public void setContent(final String content) {
    this.content = content;
  }

  /**
   * Retrieve an ordered collection of verbs ('list', 'select', etc) and associated 
   * module name/versions. Each collection item represents the module types (e.g. 'occurrence',
   * 'covariate', etc). 
   * 
   * @return Ordered collection of verb and module name/versions for module types. 
   */
  public List<VerbModules> getVerbModules() {
    return verbModules;
  }

  /**
   * Assign an ordered collection of verbs ('list', 'select', etc) and associated module 
   * name/versions.
   * 
   * @param verbModules Collection to assign.
   */
  public void setVerbModules(final List<VerbModules> verbModules) {
    this.verbModules = verbModules;
  }

  /**
   * Retrieve the figshare id.
   * 
   * @return Figshare id, or {@code null} if not assigned).
   */
  public Long getFigshareId() {
    return figshareId;
  }

  /**
   * Assign the figshare id.
   * 
   * @param figshareId Figshare id to assign.
   */
  public void setFigshareId(final Long figshareId) {
    this.figshareId = figshareId;
  }
}