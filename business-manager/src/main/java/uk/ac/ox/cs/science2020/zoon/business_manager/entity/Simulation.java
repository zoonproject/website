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
package uk.ac.ox.cs.science2020.zoon.business_manager.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.PrePersist;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 
 *
 * @author geoff
 */
@Entity
@NamedQueries({
  @NamedQuery(name=Simulation.QUERY_SIMULATION_BY_REQUEST_PROCESSED,
              query="SELECT simulation FROM Simulation AS simulation " +
                    "  WHERE " + Simulation.PROPERTY_REQUEST_PROCESSED + " IS FALSE")
})
public class Simulation implements Serializable {

  public static final int MAX_PARAMETER_LENGTH = 100;

  private static final long serialVersionUID = 8401300257020612468L;

  /* Consistent property name */
  public static final String PROPERTY_REQUEST_PROCESSED = "requestProcessed";

  /** Consistent query naming - Query simulation by request processed (see appCtx.sim.processRequest.xml) */
  public static final String QUERY_SIMULATION_BY_REQUEST_PROCESSED = "simulation.queryByRequestProcessed";

  // Persistence identity (Surrogate primary key)
  @TableGenerator(name="simulation_id_gen", table="sequence_pks_business_manager",
                  pkColumnName="pk_seq_name", pkColumnValue="simulation_id",
                  valueColumnName="pk_seq_value", allocationSize=1)
  @Id @GeneratedValue(strategy=GenerationType.TABLE, generator="simulation_id_gen")
  private Long id;

  // Optimistic locking concurrency control
  @Version
  private Long lockVersion;

  @Column(nullable=true, length=20000, updatable=true)
  private String output;

  @Column(nullable=false, length=MAX_PARAMETER_LENGTH, updatable=false)
  private String parameter;

  // System flag. If false Simulation run request has been received and is awaiting processing.
  // see appCtx.sim.processRequest.xml int-jpa:inbound-channel-adapter polling.
  @Column(nullable=false)
  private boolean requestProcessed;

  @Temporal(value=TemporalType.TIMESTAMP)
  private Date completed;

  @Temporal(value=TemporalType.TIMESTAMP)
  private Date persisted;

  @Transient
  private static transient final Log log = LogFactory.getLog(Simulation.class);

  /** <b>Do not invoke directly.</b> */
  protected Simulation() {}

  /**
   * Initialising constructor.
   * 
   * @param parameter Parameter.
   */
  public Simulation(final String parameter) {
    if (parameter == null || parameter.length() > MAX_PARAMETER_LENGTH) {
      throw new IllegalArgumentException("Maximum parameter length of '" + MAX_PARAMETER_LENGTH + "' exceeded.");
    }
    this.parameter = parameter;
    this.requestProcessed = false;
  }

  // internal callback method
  @PrePersist
  protected void onCreate() {
    persisted = new Date();
  }

  /**
   * Append output.
   * 
   * @param outputLines
   */
  public void addOutput(final List<String> outputLines) {
    if (outputLines != null && !outputLines.isEmpty()) {
      final String newLine = StringUtils.join(outputLines, "\n");
      if (this.output == null) {
        this.output = newLine;
      } else {
        this.output += "\n".concat(newLine);
      }
    }
  }

  public boolean hasCompleted() {
    final boolean hasCompleted = getCompleted() != null;
    log.debug("~hasCompleted() : Simulation '" + id + "' completed? '" + hasCompleted + "'.");
    return hasCompleted;
  }

  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "Simulation [id=" + id + ", lockVersion=" + lockVersion
        + ", output=" + output + ", parameter=" + parameter
        + ", requestProcessed=" + requestProcessed + ", completed=" + completed
        + ", persisted=" + persisted + "]";
  }

  /**
   * @return the id
   */
  public Long getId() {
    return id;
  }

  /**
   * @return the parameter
   */
  public String getParameter() {
    return parameter;
  }


  /**
   * Indicate whether the simulation's request processing has completed.
   * 
   * @return True if the simulation's request processing has completed, otherwise false.
   */
  public boolean isRequestProcessed() {
    return requestProcessed;
  }

  /**
   * Designate the simulation as having been through the run request processing.
   */
  public void setRequestProcessed() {
    requestProcessed = true;
  }

  /**
   * @return the persisted
   */
  public Date getPersisted() {
    return persisted;
  }

  /**
   * @return the completed
   */
  public Date getCompleted() {
    return completed;
  }

  /**
   * Assign as completed.
   */
  public void setCompleted() {
    assert getCompleted() == null : "Simulation '" + id + "' already designated as completed!'";

    this.completed = new Date();
  }

  /**
   * @return the output
   */
  public String getOutput() {
    return output;
  }
}