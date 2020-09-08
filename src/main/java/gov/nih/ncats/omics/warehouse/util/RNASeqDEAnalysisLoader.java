package gov.nih.ncats.omics.warehouse.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;

import gov.nih.ncats.omics.warehouse.model.AnalysisConditionGroup;
import gov.nih.ncats.omics.warehouse.model.Experiment;
import gov.nih.ncats.omics.warehouse.model.Project;
import gov.nih.ncats.omics.warehouse.model.RNASeqAnalysis;
import gov.nih.ncats.omics.warehouse.model.Sample;
import gov.nih.ncats.omics.warehouse.model.SampleSet;
import gov.nih.ncats.omics.warehouse.model.util.SampleToSampleSetLinker;
import gov.nih.ncats.omics.warehouse.service.OmicsService;

@Component
public class RNASeqDEAnalysisLoader {

	// input should include project code and experiment code, verify existence
	// input sample names should correspond to existing sample codes, verify
	
	// we need group names for the sample sets
	// the group names need to be associated with an analysis channel
	
	// pull the list of sample ids to build a SampleSet

	//will omics service work for queries?

	@Autowired
	OmicsService service;
	
	//data fields to populate
	private RNASeqAnalysis analysis;
//	private AnalysisConditionDefinition condDef;
	
	private String analysisName = null;
	private String description = null;
	
	private String projectCode = null;
	private String exptCode = null;
	private String analysisDate = null;
			
	private ArrayList <String> groupNames;
	private ArrayList <String> analysisChannel;
	private ArrayList <String> sampleNames;
	private ArrayList <Sample> sampl;
	
	private Hashtable <String, ArrayList<Sample>> groupNameToSampleHash;
	private Hashtable <String, String> groupNameToChannelNameHash;
	
	private Long projectId;
	private Long exptId;
	private ArrayList <Long> sampleIds;
	
	public RNASeqDEAnalysisLoader() { }
	

	public void captureAnalysisHeader(MultipartFile file) {

	
		groupNames = new ArrayList <String>();
		analysisChannel = new ArrayList <String>();
		sampleNames = new ArrayList <String>();
		
		String [] data;
		
		try {
			
			InputStream in = file.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
			String line;
			while ((line = br.readLine()) != null) {
				if(line.startsWith("analysis_name")) {
					analysisName = line.split("\t")[1].trim();
				} else if(line.startsWith("description")) {
					description = line.split("\t")[1].trim();
				}  else if(line.startsWith("project_code")) {
					projectCode = line.split("\t")[1].trim();
				} else if (line.startsWith("expt_code")) {
					exptCode = line.split("\t")[1].trim();
				} else if (line.startsWith("analysis_date")) {
					analysisDate = line.split("\t")[1].trim();
				} else if (line.startsWith("group_names")) {
					data = line.split("\t");
					for(int i = 1; i < data.length; i++) {
						groupNames.add(data[i]);
					}
				} else if (line.startsWith("analysis_channel")) {
					data = line.split("\t");
					for(int i = 1; i < data.length; i++) {
						analysisChannel.add(data[i]);
					}
				}  else if (line.startsWith("sample_codes")) {
					data = line.split("\t");
					for(int i = 1; i < data.length; i++) {
						sampleNames.add(data[i]);
					}
				} else if(line.startsWith("[data]")) {
					break;
				}				
			}
			br.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//make sure we have all fields
		if(analysisName == null || description == null || projectCode == null 
				|| exptCode == null || analysisDate == null) {
			System.out.println("Missing Analysis Header Field: Analysis Load Abort");			
		} else if(!(groupNames.size() > 0 
				&& groupNames.size() == analysisChannel.size() &&
				analysisChannel.size() == sampleNames.size())) {
			System.out.println("Check group_names, analysis_channel and sample_codes size parity : Analysis Load Abort");			
		} else if(verifyReferencedEntities(projectCode, exptCode, sampleNames, groupNames)) {
			//collected sampleId list projectId, exptId and other metadata.
			buildAnalysisAndSave();			
		} else {
			
		}
	}
	
	public void buildAnalysisAndSave() {

		analysis = new RNASeqAnalysis();
		analysis.setAnalysisName(analysisName);
		analysis.setDescription(description);
		analysis.setExptId(exptId);
		analysis.setProjectId(projectId);
		
		Date date;
		try {
			date = new SimpleDateFormat("yyyyMMdd").parse(analysisDate);
			analysis.setAnalysisDate(date);
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			System.out.println("Can't parse date from:" + analysisDate +". Format as yyyyMMdd");
			e1.printStackTrace();
		}
		
		// build condition def
		ArrayList <AnalysisConditionGroup> condGroups = this.buildConditionGroupList();

		ObjectMapper mapper = new ObjectMapper();
				
		SampleSet currSampleSet;
		
		//persist the new sample sets
		for(AnalysisConditionGroup group : condGroups) {			
			currSampleSet = group.getSampleSet();			
		
			if(group.getSampleSet() == null) {
				System.out.println("Hey null sample set");				
			} else {
				System.out.println("Hey NON!!!!! null sample set "+currSampleSet.getSamples().size());
				
			}
				
			System.out.println("Hey about to save a sample set..."+currSampleSet.getSampleSetCode());
			
			// save this as part of the analysis?
			service.saveSampleSet(currSampleSet);
			
			System.out.println("have a sample set saved... do we have a sample set id? sample set id: "+currSampleSet.getSampleSetId());
			System.out.println("Saved Sample Set, how about now? Do we have linkages???: "+currSampleSet.getLinkages().size());					
			
		//////////////////////////////////////////////////////////////////////////////////////
			
			//here we set the sample set id into the linkage objects and just below into the analysis group object.
			for(SampleToSampleSetLinker linkage: currSampleSet.getLinkages()) {
				linkage.setSampleSetId(currSampleSet.getSampleSetId());
				System.out.println("Hey working on the linkages: ordinal: " + linkage.getOrdinal() + " samplesetid:"+linkage.getSampleSetId() + "sampleId:"+linkage.getSampleId());
			}
			//set our sample set id
			group.setSampleSetId(currSampleSet.getSampleSetId());

		}
						
		//add the condition groups.
		analysis.setConditionGroups(condGroups);
			
		
		System.out.println("Hey!!!!! have "+analysis.getConditionGroups().size()+" condition groups!!!!!!");
		//persist the analysis object

		service.saveRNASeqAnalysis(analysis);
	/////////////////////////////////////////////////////////////////////////////////////////////
	
	
	}
	
	public ArrayList<AnalysisConditionGroup> buildConditionGroupList() {
		
		ArrayList <AnalysisConditionGroup> condGroups = new ArrayList <AnalysisConditionGroup>();
		//group names
		ArrayList<String> uniqueGroupNames = getDistinctGroupNames();
		ArrayList<String> uniqueChannels = this.getDistinctChannels();
		
		AnalysisConditionGroup analysisGroup;
		
		SampleSet currentSet;

		String groupName;
		SampleToSampleSetLinker linker;
		
		// samples already exist, so we want to build SampleSet objects but don't add the samples
		// this will stop the cascade attempted insert of existing samples.
		for(int i = 0; i < uniqueGroupNames.size(); i++) {
			
			groupName = uniqueGroupNames.get(i);
			
			analysisGroup = new AnalysisConditionGroup();			
			analysisGroup.setGroupName(groupName);
			
			currentSet = new SampleSet();			
			
			int sampleCount = 0;
			// need to iterate over a collection of samples for this group name.
			for(Sample sample : groupNameToSampleHash.get(groupName)) {

				//add the sample to the current set
				currentSet.addSample(sample);
				
				linker = new SampleToSampleSetLinker(currentSet.getSampleSetId(), sample.getSampleId(), sampleCount);
				currentSet.addSampleLinkerElement(linker);
								
				//use one sample to determine the 'owner' of the sample set
				if(sampleCount == 0) {
					Long setOwner = getSampleOwner(sample);
					if(setOwner != null)
						currentSet.setInvestigatorId(setOwner);
				}
				sampleCount++;
			}
			
			currentSet.setProjectId(projectId);
			currentSet.setExptId(exptId);
			currentSet.setIsFullExperimentSet(0);
			currentSet.setGenerationProcess("analysis-load");				
			currentSet.setSetName("Sample_Set_"+uniqueChannels.get(i)+" (GroupName: "+ uniqueGroupNames.get(i)+")");
			currentSet.setSampleSetCode(exptCode + "_Set_" + uniqueChannels.get(i) + "_"+System.currentTimeMillis());

			// set the sample set
			analysisGroup.setSampleSet(currentSet);

			//set the channel name
			analysisGroup.setAnalysisChannel(groupNameToChannelNameHash.get(groupName));
			
			condGroups.add(analysisGroup);
		}	
		return condGroups;
	}
	
	
	public Long getSampleOwner(Sample sample) {
		return service.getPersonIdFromSample(sample);
	}
	
	public ArrayList<String> getDistinctGroupNames() {
		ArrayList <String> uniqueOrderedNames = new ArrayList <String>();
		
		for(String name: groupNames) {
			if(!uniqueOrderedNames.contains(name)) {
				uniqueOrderedNames.add(name);
			}
		}		
		return uniqueOrderedNames;
	}
	
	public ArrayList<String> getDistinctChannels() {
		ArrayList <String> uniqueOrderedChannels = new ArrayList <String>();
		
		for(String channel: this.analysisChannel) {
			if(!uniqueOrderedChannels.contains(channel)) {
				uniqueOrderedChannels.add(channel);
			}
		}		
		return uniqueOrderedChannels;
	}
	
	public Boolean verifyReferencedEntities(String projectCode, String exptCode, ArrayList <String> sampleNames, ArrayList <String> groupNames) {
		Boolean verified = true;
		
		Project project = service.getProjectByProjectCode(projectCode);
		if(project != null) {
			System.out.println("Hey have a project "+project.getProjectId());
			projectId = project.getProjectId();
		} else {
			System.out.println("missing specified project code = "+projectCode);
			verified = false;
		}
		
		Experiment experiment = service.findExperimentByExptCode(exptCode);
		if(experiment != null) {
			System.out.println("Hey have an expt "+experiment.getExptId());
			exptId = experiment.getExptId();
		} else {
			System.out.println("missing specified experiment code = "+exptCode);
			verified = false;
		}
		
		Sample sample = null;
		sampleIds = new ArrayList <Long>();

	    groupNameToSampleHash = new Hashtable <String, ArrayList<Sample>>();
		String groupName;
		int cnt =  0;
		ArrayList <Sample> samples;
		
		for(String sampleName : sampleNames) {
		
			groupName = groupNames.get(cnt); 
			
			sample = service.getSampleBySampleName(sampleName);
			
			if(sample == null) {				
				System.out.println("missing specified sample code = "+sampleName);
				verified = false;
			} else {
				System.out.println("Hey have a sample "+sample.getSampleId());
				sampleIds.add(sample.getSampleId());

				//we have a sample add it to the hash
				samples = groupNameToSampleHash.get(groupName);
				if(samples == null) {
					samples = new ArrayList<Sample>();
					groupNameToSampleHash.put(groupName, samples);
				}
				samples.add(sample);
			}
			
			sample = null;
			cnt++;
		}
		
		cnt = 0;
		//build a hash from group name to channel
		this.groupNameToChannelNameHash = new Hashtable<String, String>();

		for(String name : groupNames) {
			groupNameToChannelNameHash.put(name, this.analysisChannel.get(cnt));
			cnt++;
		}
		
		return verified;
	}

	
//	public static void main(String [] args) {
//	    SpringApplication.run(RNASeqDEAnalysisLoader.class, args);
//	    
//		RNASeqDEAnalysisLoader loader = new RNASeqDEAnalysisLoader("C:\\Users\\braistedjc\\eclipse-workspace\\Omics_Warehouse_Play\\conf\\application.conf");
//		loader.captureAnalysisHeader("C:\\Users\\braistedjc\\Desktop\\Analysis\\SCTL_Omics_Warehouse\\data\\to_load\\stats\\DE.iPSC_LA_day0_vs_day5.txt");
//
//	}
	
	
	
}
