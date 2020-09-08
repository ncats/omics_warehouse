package gov.nih.ncats.omics.warehouse.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * The Treatment class holds fields describing a treatment associated with a biological sample
 * A treatment is associated with one Sample. A Sample may have many Treatments.
 * @param treatmentId an omics platform unique id
 * @param sampleId the omics platform unique id for the associated sample.
 * @param sampleCode is a user defined identifier for a sample within an omics Experiment. The sampleCode is a field in the parent Sample object.
 * @param varName a user defined name for the treatment
 * @param varType describes the nature of the treatment (compound, environmental (temp, atmosphere, etc.), media)
 * @param varData is a String representation of the data associated with the treatment variable, e.g. 0.01, 10, "media X".
 * @param varUnit represents optional units on the varData, e.g. uM, %, uL.
 * @param startTime indicates a relative time that the treatment started, e.g. 0 or 24
 * @param endTime indicates a relative time that the treatment ended, e.g. 5 or 48
 * @param timeUnit indicates the units associated with the time, e.g. d, h
 * 
 * @author braistedjc
 *
 */
@Entity
@Table(name="omics_adm.treatment")
public class Treatment implements Comparable {
	
	@Id
	@Column(name = "treatment_id")
	private Long treatmentId;
	@Column(name = "sample_id")
	private Long sampleId;
	@Column(name = "var_name")
	private String varName;
	@Column(name = "var_type")
	private String varType;
	@Column(name = "var_data")
	private String varData;
	@Column(name = "var_unit")
	private String varUnit;
	@Column(name = "start_time")
	private Float startTime;
	@Column(name = "end_time")
	private Float endTime;
	@Column(name = "time_unit")
	private String timeUnit;
	
	@Transient
	private String sampleCode;
	
	public Long getSampleId() {
		return sampleId;
	}
	public void setSampleId(Long sampleId) {
		this.sampleId = sampleId;
	}
	public String getVarName() {
		return varName;
	}
	public void setVarName(String varName) {
		this.varName = varName;
	}
	public String getVarType() {
		return varType;
	}
	public void setVarType(String varType) {
		this.varType = varType;
	}
	public String getVarData() {
		return varData;
	}
	public void setVarData(String varData) {
		this.varData = varData;
	}
	public String getVarUnit() {
		return varUnit;
	}
	public void setVarUnit(String varUnit) {
		this.varUnit = varUnit;
	}
	public Float getStartTime() {
		return startTime;
	}
	public void setStartTime(Float startTime) {
		this.startTime = startTime;
	}
	public Float getEndTime() {
		return endTime;
	}
	public void setEndTime(Float endTime) {
		this.endTime = endTime;
	}
	public String getTimeUnit() {
		return timeUnit;
	}
	public void setTimeUnit(String timeUnit) {
		this.timeUnit = timeUnit;
	}
	public Long getTreatmentId() {
		return treatmentId;
	}
	public void setTreatmentId(Long treatmentId) {
		this.treatmentId = treatmentId;
	}

	public String getSampleCode() {
		return sampleCode;
	}
	public void setSampleCode(String sampleCode) {
		this.sampleCode = sampleCode;
	}
	public int compareTo(Object other) {
		if(other instanceof Treatment) {
			return this.getTreatmentId().compareTo(((Treatment)other).getTreatmentId());
		}
		else
			return 1;
	}
	
	public String toString() {
		String objRep = "";
		objRep += "sampleid:" + sampleId;
		objRep += " | varName:" + varName;
		objRep += " | varType:" + varType;
		objRep += " | varData:" + varData;
		objRep += " | varDataUnit:" + varUnit;
		objRep += " | startTime:" + startTime;
		objRep += " | endTime:" + endTime;
		objRep += " | timeUnit:" + timeUnit;		
		return objRep;
	}
}
