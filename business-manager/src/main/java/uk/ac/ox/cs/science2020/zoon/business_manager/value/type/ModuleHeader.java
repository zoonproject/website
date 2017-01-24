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
package uk.ac.ox.cs.science2020.zoon.business_manager.value.type;

/**
 * ZOON/R/Roxygen-defined module header names.
 *
 * @author geoff
 */
public enum ModuleHeader {
  NAME("Module name", "@name", true),
  AUTHOR("Module author", "@author", false),
  VERSION("Module version", null, true),     // Note that version information appears in @section
  DESCRIPTION("Module description", "@description", false),
  SEEALSO("See also information", "@seealso", false),
  TYPE("Module type", null, false),
  PARAM("Module parameter description", "@param", false),
  RETURN("Module return value", "@return", false),
  INCLUDE("Include", "@include", false),
  EXPORT("Export", "@export", false),
  EXPORTCLASS("Export class", "@exportClass", false),
  EXPORTMETHOD("Export method", "@exportMethod", false),
  S3METHOD("S3 method", "@S3method", false),
  IMPORT("Import", "@import", false),
  IMPORTFROM("Import from", "@importFrom", false),
  IMPORTCLASSESFROM("Import classes from", "@importClassesFrom", false),
  IMPORTMETHODSFROM("Import methods from", "@importMethodsFrom", false),
  FIELD("Field", "@field", false),
  INHERITPARAMS("Inherit params", "@inheritParams", false),
  METHOD("Method", "@method", false),
  EXAMPLES("Examples", "@examples", false),
  EXAMPLE("Example", "@example", false),
  NOTE("Note", "@note", false),
  SECTION("Section", "@section", false),   // Version information extracted from here.
  BACKREF("Backref", "@backref", false),
  KEYWORDS("Keywords", "@keywords", false),
  ALIAS("Alias", "@alias", false),
  ALIASES("Aliases", "@aliases", false),
  SLOT("Slot", "@slot", false),
  CONCEPT("Concept", "@concept", false),
  CONCEPTS("Concepts", "@concepts", false),
  REFERENCES("References", "@references", false),
  FAMILY("Family", "@family", true),
  TEMPLATE("Template", "@template", false),
  TEMPLATEVAR("Template var", "@templateVar", false),
  RDNAME("RDname", "@rdname", false),
  DESCRIBEIN("Describe in", "@describeIn", false),
  TITLE("Title", "@title", false),
  DETAILS("Details", "@details", false),
  USAGE("Usage", "@usage", false),
  DOCTYPE("Doctype", "@docType", false),
  FORMAT("Format", "@format", false),
  SOURCE("Source", "@source", false);

  // Textual description
  private final String description;
  // Identified by
  private final String roxygenIdentifier;
  // Whether the header is required
  private final boolean required;

  // Initialising constructor
  private ModuleHeader(final String description, final String roxygenIdentifier,
                           final boolean required) {
    this.description = description;
    this.roxygenIdentifier = roxygenIdentifier;
    this.required = required;
  }

  /**
   * Retrieve the description of this enum value.
   * 
   * @return Description of enum value.
   */
  public String getDescription() {
    return this.description;
  }

  /**
   * Retrieve the header's roxygen identifier.
   * 
   * @return roxygen text which identifies the header, e.g. <code>@param</code>, or <code>null</code>
   *         if not defined.
   */
  public String getRoxygenIdentifier() {
    return roxygenIdentifier;
  }

  /**
   * Indicator that the header must be defined in the module.
   * 
   * @return True if header must be defined, otherwise false.
   */
  public boolean isRequired() {
    return required;
  }
}