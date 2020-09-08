package gov.nih.ncats.omics.warehouse.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;

/**
 * AnalysisConditionDefinition is primarily a container an utility class which refers to a parent RNASeqAnalysis object
 * and a collection of AnalysisConditionGroup objects.
 * 
 * @deprecated This class is under review for possible removal
 * 
 * @author braistedjc
 *
 */
public class AnalysisConditionDefinition {

	// Analysis id that refers to a parent analysis
	private Long analysisId;
	
	
	private List <AnalysisConditionGroup> analysisGroups;

	public AnalysisConditionDefinition() {
		analysisGroups = new ArrayList <AnalysisConditionGroup>();
	}
	
	public List<AnalysisConditionGroup> getAnalysisGroups() {
		return analysisGroups;
	}

	public void setAnalysisGroups(ArrayList<AnalysisConditionGroup> analysisGroups) {
		this.analysisGroups = analysisGroups;
	}
	
	public void addAnalysisGroup(AnalysisConditionGroup group) {
		this.analysisGroups.add(group);
	}
	
	public ArrayList <String> getGroupNames() {
		ArrayList <String> names = new ArrayList<String>();
		for(AnalysisConditionGroup group : analysisGroups) {
			names.add(group.getGroupName());
		}
		return names;
	}
	
	public ArrayList <String> getSampleNameList() {
		ArrayList <String> names = new ArrayList<String>();
		for(AnalysisConditionGroup group : analysisGroups) {
			for(Sample sample: group.getSampleSet().getSamples()) {
				names.add(sample.getSampleName());
			}
		}
		return names;
	}
	
	public ArrayList <String> getFullGroupNameList() {
		ArrayList <String> names = new ArrayList<String>();
		for(AnalysisConditionGroup group : analysisGroups) {
			for(Sample sample: group.getSampleSet().getSamples()) {
				//just repeat the group names for each sample
				names.add(group.getGroupName());
			}
		}
		return names;
	}

	public Long getAnalysisId() {
		return analysisId;
	}

	public void setAnalysisId(Long analysisId) {
		this.analysisId = analysisId;
	}
	

	
}
