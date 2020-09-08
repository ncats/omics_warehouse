package gov.nih.ncats.omics.warehouse.model;

import java.util.ArrayList;
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
 * The project class encapsulates data fields that describe an omics project.
 * It's important that a distinction be made between an omics project and an experimental run.
 * A project is a high level entity describes an area of study that addresses a rather broad set of goals within biological realm.
 * An experiment or run a data producing event that contributes data toward advancing a project.
 * 
 * @author braistedjc
 *
 */
@Entity
@Table(name = "omics_adm.dim_project")
public class Project {
	
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "project_id", updatable = false, nullable = false)
	public Long projectId;
    
    @Column(name = "user_project_code")
	public String projectCode;
    
    @Column(name="name")
	public String projectName;
    
    @Column(name="description")
	public String description;
	
    @OneToMany
	@JoinTable(name = "omics_adm.project_experiment",
	joinColumns = @JoinColumn(name = "project_id"),
	inverseJoinColumns = @JoinColumn(name = "expt_id"))
    public List <Experiment> experiments;
    
    @OneToMany
	@JoinColumn(name = "project_id")
    public List <SampleSet> sampleSets;
    
    public Project() {
    	experiments = new ArrayList <Experiment>();
    }
    
	public String getProjectCode() {
		return projectCode;
	}
	public void setProjectCode(String projectCode) {
		this.projectCode = projectCode;
	}
	public String getProjectName() {
		return projectName;
	}
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Long getProjectId() {
		return projectId;
	}
	public void setProjectId(Long dbProjectId) {
		this.projectId = dbProjectId;
	}
	
}
