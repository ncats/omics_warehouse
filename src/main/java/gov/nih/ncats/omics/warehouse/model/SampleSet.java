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
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;

import gov.nih.ncats.omics.warehouse.model.util.SampleToSampleSetLinker;

/**
 * The SampleSet class holds high level information on a collection of samples.
 * Typically these samples are associated with a particular project and represent a set of samples within an Experiment.
 * @param sampleSetCode is a user assigned code for the set of samples
 * @param setName is a short user-assigned name for the collection of samples.
 * @isFullExperiementSet indicates if the SampleSet holds all samples included in the parent experiment. 0 = false, 1 = true
 * @samples an array of Sample objects.
 * 
 * @author braistedjc
 *
 */
@Entity
@Table(name = "omics_adm.dim_sample_set")
public class SampleSet {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name= "sample_set_id")
	private Long sampleSetId;
	
	@Column(name = "sample_set_code")
	private String sampleSetCode;
	
	@Column(name = "set_name")
	private String setName;
	
	@Column(name= "project_id")
	private Long projectId;
	
	@Column(name= "expt_id")
	private Long exptId;
	
	@Column(name= "invest_id")
	private Long investigatorId;
	
	@Column(name = "is_full_expt_set")
	private Integer isFullExperimentSet;
	
	@Column(name = "generation_process")
	private String generationProcess;

	@OneToMany(cascade=CascadeType.ALL)
	@JoinColumn(name="sample_set_id")
	private List <SampleToSampleSetLinker> linkages;

	@OneToMany
    @JoinTable(name="omics_adm.sample_set2sample",
    			joinColumns = @JoinColumn(name = "sample_set_id"),
    			inverseJoinColumns = @JoinColumn(name = "sample_id"))
	private List <Sample> samples;
	
//	@OneToMany
//    @JoinTable(name="omics_adm.sample_set2sample",
//	joinColumns = @JoinColumn(name = "sample_set_id"),
//	inverseJoinColumns = @JoinColumn(name = "sample_id"))
//	private List <Integer> ordinal;



	@Column(name="update_date")
	@UpdateTimestamp
	private Date updateDate;
	
	@JsonIgnore
	@Transient
	private String projectCode;

	@JsonIgnore
	@Transient
	private String exptCode;
	
	@JsonIgnore
	@Transient
	private String investigator;
	
	@JsonIgnore
	@Transient
	private Project parentProject;
	
	@JsonIgnore
	@Transient
	private Person parentInvestigator;
	
	@JsonIgnore
	@Transient
	private Experiment parentExperiment;
	
	public SampleSet() {
		samples = new ArrayList<Sample>();
		linkages = new ArrayList<SampleToSampleSetLinker>();
	}
	
//	public void addSampleInOrder(Sample sample) {
//		samples.add(sample);
//		order.add(samples.size()-1); //may drop this if assumed samples are ordered in set
//	}
	
	public Long getProjectId() {
		return projectId;
	}
	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}
	public Long getExptId() {
		return exptId;
	}
	public void setExptId(Long exptId) {
		this.exptId = exptId;
	}
	public List<Sample> getSamples() {
		return samples;
	}
	
	@JsonIgnore
	public void setSamples(List<Sample> samples) {
		this.samples = samples;
	}

	public Integer setSize() {
		return samples.size();
	}
	
	public Sample getSample(int i) {
		if(i < setSize())
			return samples.get(i);
		return null;
	}
	
	public void addSample(Sample sample) {
		samples.add(sample);
	}
	
	public Integer setSampleDbIds(ArrayList <Long> orderedDbSampleIds) {
		if(orderedDbSampleIds.size() == this.samples.size()) {
			int i = 0;
			for(Sample sample : samples) {
				sample.setDbSampleId(orderedDbSampleIds.get(i));
				i++;
			}
			return samples.size();
		} else {
			return -1;
		}
	}

	public Long getDbProjectId() {
		return projectId;
	}

	public void setDbProjectId(Long dbProjectId) {
		this.projectId = dbProjectId;
	}

	public Long getDbExptId() {
		return exptId;
	}

	public void setDbExptId(Long dbExptId) {
		this.exptId = dbExptId;
	}

	public Long getDbInvestigatorId() {
		return investigatorId;
	}

	public void setDbInvestigatorId(Long dbInvestigatorId) {
		this.investigatorId = dbInvestigatorId;
	}

	public String getProjectCode() {
		return projectCode;
	}

	public void setProjectCode(String projectCode) {
		this.projectCode = projectCode;
	}

	public String getExptCode() {
		return exptCode;
	}

	public void setExptCode(String exptCode) {
		this.exptCode = exptCode;
	}

	public String getSetName() {
		return setName;
	}

	public void setSetName(String setName) {
		this.setName = setName;
	}

	public String getInvestigator() {
		return investigator;
	}

	public void setInvestigator(String investigator) {
		this.investigator = investigator;
	}

	public Project getParentProject() {
		return parentProject;
	}

	public void setParentProject(Project parentProject) {
		this.parentProject = parentProject;
	}

	public Person getParentInvestigator() {
		return parentInvestigator;
	}

	public void setParentInvestigator(Person parentInvestigator) {
		this.parentInvestigator = parentInvestigator;
	}

	public Experiment getParentExperiment() {
		return parentExperiment;
	}

	public void setParentExperiment(Experiment parentExperiment) {
		this.parentExperiment = parentExperiment;
	}

	public Long getDbSetId() {
		return sampleSetId;
	}

	public void setDbSetId(Long dbSetId) {
		this.sampleSetId = dbSetId;
	}

	public Long getSampleSetId() {
		return sampleSetId;
	}

	public void setSampleSetId(Long sampleSetId) {
		this.sampleSetId = sampleSetId;
	}

	public Long getInvestigatorId() {
		return investigatorId;
	}

	public void setInvestigatorId(Long investigatorId) {
		this.investigatorId = investigatorId;
	}

	public Integer getIsFullExperimentSet() {
		return isFullExperimentSet;
	}

	public void setIsFullExperimentSet(Integer isFullExperimentSet) {
		this.isFullExperimentSet = isFullExperimentSet;
	}

	public String getSampleSetCode() {
		return sampleSetCode;
	}

	public void setSampleSetCode(String sampleSetCode) {
		this.sampleSetCode = sampleSetCode;
	}
	
	public String getGenerationProcess() {
		return generationProcess;
	}
	
	public void setGenerationProcess(String generationProcess) {
		this.generationProcess = generationProcess;
	}

	public List<SampleToSampleSetLinker> getLinkages() {
		return linkages;
	}

	public void setLinkages(List<SampleToSampleSetLinker> linkage) {
		this.linkages = linkage;
	}
	
	public void addSampleLinkerElement(SampleToSampleSetLinker linker) {
		this.linkages.add(linker);
	}

	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}
	
	
}
