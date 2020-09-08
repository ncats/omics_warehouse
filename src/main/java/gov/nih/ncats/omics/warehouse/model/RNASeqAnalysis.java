package gov.nih.ncats.omics.warehouse.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * Container class for an RNASeqAnalysis, holding metadata and an ordered collection of AnalysisConditionGroup objects containing
 * references to samples and treatments.
 * 
 * @author braistedjc
 * 
 */
@Entity
@Table(name="omics_adm.dim_analysis")
public class RNASeqAnalysis {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "analysis_id")	
	private Long analysisId;
	
	@Column(name = "label")
	private String analysisName;
	
	@Column(name = "description")
	private String description;

	@Column(name = "expt_id")
	private Long exptId;
	
	@Column(name = "project_id")
	private Long projectId;
	
	@Column(name = "analysis_date")
	private Date analysisDate;

	@OneToMany(cascade = {CascadeType.ALL})
	@JoinColumn(name="analysis_id")
	private List <AnalysisConditionGroup> conditionGroups;
	
	public RNASeqAnalysis() {
		conditionGroups = new ArrayList<AnalysisConditionGroup>();
	}
	
	public Long getAnalysisId() {
		return analysisId;
	}
	public void setAnalysisId(Long analysisId) {
		this.analysisId = analysisId;
	}
	public String getAnalysisName() {
		return analysisName;
	}
	public void setAnalysisName(String analysisName) {
		this.analysisName = analysisName;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Long getExptId() {
		return exptId;
	}
	public void setExptId(Long exptId) {
		this.exptId = exptId;
	}
	public Long getProjectId() {
		return projectId;
	}
	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}
	public Date getAnalysisDate() {
		return analysisDate;
	}
	public void setAnalysisDate(Date analysisDate) {
		this.analysisDate = analysisDate;
	}
	public List<AnalysisConditionGroup> getConditionGroups() {
		return conditionGroups;
	}
	public void setConditionGroups(List<AnalysisConditionGroup> conditions) {
		this.conditionGroups = conditions;
	}	
}
