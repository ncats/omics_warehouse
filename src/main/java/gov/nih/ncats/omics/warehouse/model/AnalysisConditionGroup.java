package gov.nih.ncats.omics.warehouse.model;

import java.util.ArrayList;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 * AnalysisConditionGroup is a container that holds a collection of samples as a SampleSet object and related metadata.
 * Note: Child Sample objects within the SampleSet may have related Treatment objects
 * 
 * @author braistedjc
 *
 */
@Entity
@Table (name = "omics_adm.analysis_group")
public class AnalysisConditionGroup {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "group_id")
	private Long analysisGroupId;

	@Column(name = "analysis_id")
	private Long parentAnalysisId;
	
	@Column(name = "sample_set_id")
	private Long sampleSetId;

	@OneToOne(cascade = {CascadeType.ALL})
	@JoinColumn(name="sample_set_id", insertable=false, updatable=false)
	private SampleSet sampleSet;
	
	@Column(name = "group_name")
	private String groupName;
	
	@Column(name = "group_channel_name")
	private String analysisChannel;
	
	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getAnalysisChannel() {
		return analysisChannel;
	}

	public void setAnalysisChannel(String analysisChannel) {
		this.analysisChannel = analysisChannel;
	}

	public ArrayList<String> getOrderedSampleNames() {
		ArrayList <String> orderedSampleNames = new ArrayList<String>();
		if(sampleSet != null) {
			for(Sample sample : sampleSet.getSamples()) {
				orderedSampleNames.add(sample.getSampleName());
			}
		}
		return orderedSampleNames;
	}

	public SampleSet getSampleSet() {
		return sampleSet;
	}

	public void setSampleSet(SampleSet sampleSet) {
		this.sampleSet = sampleSet;
	}

	public Long getParentAnalysisId() {
		return parentAnalysisId;
	}

	public void setParentAnalysisId(Long parentAnalysisId) {
		this.parentAnalysisId = parentAnalysisId;
	}

	public Long getSampleSetId() {
		return sampleSetId;
	}

	public void setSampleSetId(Long sampleSetId) {
		this.sampleSetId = sampleSetId;
	}

	public Long getAnalysisGroupId() {
		return analysisGroupId;
	}

	public void setAnalysisGroupId(Long analysisGroupId) {
		this.analysisGroupId = analysisGroupId;
	}
	
	
	
}
