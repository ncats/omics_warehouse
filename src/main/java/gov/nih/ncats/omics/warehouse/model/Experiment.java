package gov.nih.ncats.omics.warehouse.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * The Experiment class contains information that pertains to an experimental 'run' of an omics platform.
 * The Experiment in this case refers to a single run of an omics platform that generates data.
 * An example would be a bulk RNASeq NextSeq run.
 * 
 * It should be noted that multiple samples are associated with the Experiment and that the samples
 * may pertain be associated with different projects with distinct aims and interrogated biology.
 * 
 * @author braistedjc
 *
 */
@Entity
@Table(name = "omics_adm.dim_experiment")
public class Experiment {

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "expt_id")
	private Long exptId;

	@Column(name = "user_expt_code")
	private String exptCode;
	
	@Column(name = "name")	
	private String exptName;
	
	@Column(name = "description")
	private String exptDescription;

	@Column(name = "expt_run_date")
	private Date runDate;
	
	@Column(name = "technology")
	private String technology;
	
	@Column(name = "technology_platform")
	private String techPlatform;
	
	@OneToMany
	@JoinTable(name = "omics_adm.project_experiment",
			joinColumns = @JoinColumn(name = "expt_id"),
			inverseJoinColumns = @JoinColumn(name = "project_id"))
	private List <Project> parentProjects;
	
	@Column(name = "data_type")
	private String dataType;
	
	public Experiment() {
		parentProjects = new ArrayList <Project>();
	}
	
	public String getExptCode() {
		return exptCode;
	}
	public void setExptCode(String exptCode) {
		this.exptCode = exptCode;
	}
	public String getExptName() {
		return exptName;
	}
	public void setExptName(String exptName) {
		this.exptName = exptName;
	}
	public String getExptDescription() {
		return exptDescription;
	}
	public void setExptDescription(String exptDescription) {
		this.exptDescription = exptDescription;
	}
	public Date getRunDate() {
		return runDate;
	}
	public void setRunDate(Date runDate) {
		this.runDate = runDate;
	}
	public String getTechPlatform() {
		return techPlatform;
	}
	public void setTechPlatform(String techPlatform) {
		this.techPlatform = techPlatform;
	}
	public Long getExptId() {
		return exptId;
	}
	public void setExptId(Long exptId) {
		this.exptId = exptId;
	}
	public List<Project> getParentProjects() {
		return parentProjects;
	}
	public void setParentProjects(List<Project> parentProjects) {
		this.parentProjects = parentProjects;
	}

	public String getTechnology() {
		return technology;
	}
	
	public void setTechnology(String technology) {
		this.technology = technology;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}
	
	
}
