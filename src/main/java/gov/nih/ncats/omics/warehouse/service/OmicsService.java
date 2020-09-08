package gov.nih.ncats.omics.warehouse.service;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.List;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;

import gov.nih.ncats.omics.warehouse.model.AnalysisConditionGroup;
import gov.nih.ncats.omics.warehouse.model.Cell;
import gov.nih.ncats.omics.warehouse.model.Experiment;
import gov.nih.ncats.omics.warehouse.model.ExpressionProfileLite;
import gov.nih.ncats.omics.warehouse.model.Gene;
import gov.nih.ncats.omics.warehouse.model.GeneSet;
import gov.nih.ncats.omics.warehouse.model.Person;
import gov.nih.ncats.omics.warehouse.model.Project;
import gov.nih.ncats.omics.warehouse.model.RNASeqAnalysis;
import gov.nih.ncats.omics.warehouse.model.RNASeqDERecord;
import gov.nih.ncats.omics.warehouse.model.RNASeqProfile;
import gov.nih.ncats.omics.warehouse.model.Sample;
import gov.nih.ncats.omics.warehouse.model.SampleSet;
import gov.nih.ncats.omics.warehouse.model.Treatment;
import gov.nih.ncats.omics.warehouse.model.util.SampleToSampleSetLinker;
import gov.nih.ncats.omics.warehouse.repository.AnalysisGroupRepo;
import gov.nih.ncats.omics.warehouse.repository.CellRepo;
import gov.nih.ncats.omics.warehouse.repository.ExperimentRepo;
import gov.nih.ncats.omics.warehouse.repository.GeneRepo;
import gov.nih.ncats.omics.warehouse.repository.GeneSetRepo;
import gov.nih.ncats.omics.warehouse.repository.InvestigatorRepo;
import gov.nih.ncats.omics.warehouse.repository.ProjectRepo;
import gov.nih.ncats.omics.warehouse.repository.RNASeqAnalysisRepo;
import gov.nih.ncats.omics.warehouse.repository.RNASeqDERecordRepo;
import gov.nih.ncats.omics.warehouse.repository.RNASeqProfileRepo;
import gov.nih.ncats.omics.warehouse.repository.SampleRepo;
import gov.nih.ncats.omics.warehouse.repository.SampleSetRepo;
import gov.nih.ncats.omics.warehouse.repository.TreatmentRepo;
import gov.nih.ncats.omics.warehouse.repository.util.SampleToSampleSeetLinkerRepo;
import gov.nih.ncats.omics.warehouse.util.RNASeqDEAnalysisLoader;

public class OmicsService {

	@Autowired
	ProjectRepo projectRepo;
	
	@Autowired
	TreatmentRepo treatmentRepo;
	
	@Autowired
	CellRepo cellRepo;
	
	@Autowired
	SampleRepo sampleRepo;
	
	@Autowired
	SampleSetRepo sampleSetRepo;
	
	@Autowired
	RNASeqProfileRepo rnaseqProfileRepo;
	
	@Autowired
	GeneSetRepo geneSetRepo;
	
	@Autowired
	GeneRepo geneRepo;
	
	@Autowired
	ExperimentRepo experimentRepo;
	
	@Autowired
	InvestigatorRepo investigatorRepo;
	
	@Autowired
	SampleToSampleSeetLinkerRepo s2ssRepo;

	@Autowired
	RNASeqDEAnalysisLoader analysisLoader;
	
	@Autowired
	RNASeqAnalysisRepo rnaseqAnalysisRepo;
	
	@Autowired
	AnalysisGroupRepo analysisGroupRepo;
	
	@Autowired
	RNASeqDERecordRepo rnaseqDeRecordRepo;
	
	public ArrayList<Project> getProjects() {
		ArrayList <Project> projects = (ArrayList<Project>)projectRepo.findAll();
		return projects;
	}
	
	public Project getProjectByProjectId(Long projectId) {
		Project project = projectRepo.findProjectByProjectId(projectId);
		return project;
	}
	
	public Project getProjectByProjectCode(String projectCode) {
		Project project = projectRepo.findProjectByProjectCode(projectCode);
		return project;
	}
	
	public ArrayList<Treatment> getTreatmentBySampleId(Long sid) {
		ArrayList <Long> id = new ArrayList <Long>();
		id.add(sid);
		ArrayList <Treatment> treatments = (ArrayList<Treatment>) treatmentRepo.findTreatmentsBySampleId(sid);
		Collections.sort(treatments);
		return treatments;
	}

	public List <Cell> findAllCells() {
		return (List <Cell>) cellRepo.findAll();
	}
	
	public Cell getCellByCellTypeId(Long cid) {
		return cellRepo.findCellByCellTypeId(cid);
	}
	
	public Sample getSampleBySampleId(Long ssid) {
		Sample sample =  sampleRepo.getSampleBySampleId(ssid);
		return sample;
	}
	
	public Sample getSampleBySampleCode(String sampleCode) {
		Sample sample =  sampleRepo.getSampleBySampleCode(sampleCode);
		return sample;
	}
	
	public Sample getSampleBySampleName(String sampleName) {
		Sample sample =  sampleRepo.getSampleBySampleName(sampleName);
		return sample;
	}
	
	public List<SampleSet> getSampleSets() {
		return (List<SampleSet>)sampleSetRepo.findAll();
	}
	
	public List<SampleSet> getSampleSetsByProjectId(Long projectId) {
		return (List<SampleSet>)sampleSetRepo.getSampleSetsByProjectId(projectId);
	}
	
	public List<SampleSet> getSampleSetsByProjectCode(String projectCode) {
		Project project = projectRepo.findByProjectCode(projectCode);
		List <SampleSet> sets = getSampleSetsByProjectId(project.getProjectId());
		return sets;
	}
	
	public SampleSet getSampleSetBySampleSetId(Long ssid) {
		return sampleSetRepo.getSampleSetBySampleSetId(ssid);
	}
			
	public String getRNASeqExpressionTableBySampleSetId(Long ssid, Integer dataType) {
		String tab = "";
		
		SampleSet sampleSet = getSampleSetBySampleSetId(ssid);
		List<RNASeqProfile> profiles = rnaseqProfileRepo.getRNASeqProfileBySampleSetId(ssid);
		
		tab = formatExpressionTable(profiles, sampleSet.getSamples(), dataType);
		//System.out.println("Have Profiles "+profiles.size());
		return tab;
	}
	
	public String formatExpressionTable(List <RNASeqProfile> profiles, List <Sample> samples, Integer dataType) {

		String tab = "\t\t";
		for(Sample sample : samples) {
			tab += sample.getSampleCode() + "\t";
		}
		tab = tab.substring(0, tab.length()-1) +"\nGene_Name\tDescription\t"; 
		for(Sample sample : samples) {
			tab += sample.getSampleName() + "\t";
		}
		tab = tab.substring(0, tab.length()-1) + "\n";
		
		StringBuffer buffer = new StringBuffer(tab);
		ExpressionProfileLite profileLite;
		ObjectMapper mapper = new ObjectMapper();
		
		for(RNASeqProfile profile : profiles) {
			profileLite = profile.getExpressionProfileLite(mapper);
			if(profileLite != null) {
				if(dataType == 1)
					buffer.append(profileLite.getTabDelimNormalizedDataTableString());
				else if(dataType == 0)
					buffer.append(profileLite.getTabDelimNormalizedDataTableString());
				else
					buffer.append(profileLite.getTabDelimScaledDataTableString());
			}
		}
		
		tab = buffer.toString();
		//System.out.println(Runtime.getRuntime().freeMemory());
		
		return tab;
	}
	
	public List <GeneSet> findAllGeneSets() {
		return (List <GeneSet>) geneSetRepo.findAll();
	}
	
	public GeneSet findGeneSetByGeneSetId(Long did) {
		GeneSet set = geneSetRepo.findGeneSetByGeneSetId(did);
		if(set != null) {
			Long sampleSetId = set.getSampleSetId();
			ArrayList <ExpressionProfileLite> profiles = new ArrayList <ExpressionProfileLite>();
			RNASeqProfile profile;
			ObjectMapper mapper = new ObjectMapper();
			for(Gene gene : set.getGenes()) {
				profile = rnaseqProfileRepo.getRNASeqProfileByGeneIdAndSampleSetId(gene.getGeneId(), sampleSetId);
				profiles.add(profile.getExpressionProfileLite(mapper));
			}
			set.setProfiles(profiles);
		}
		return set;
	}
	
	public List <GeneSet> findGeneSetsBySampleSetId(Long sampleSetId) {
		List <GeneSet> set = geneSetRepo.findGeneSetsBySampleSetId(sampleSetId);
		return set;
	}
	
	public List <GeneSet> findGeneSetsByExptId(Long exptId) {
		List <GeneSet> set = geneSetRepo.findGeneSetsByExptId(exptId);
		return set;
	}
	
	public List <GeneSet> findGeneSetsByProjectId(Long projectId) {
		List <GeneSet> set = geneSetRepo.findGeneSetsByProjectId(projectId);
		return set;
	}
	
	public Gene findGeneByGeneId(Long geneId) {
		return geneRepo.findGeneByGeneId(geneId);
	}

	public Gene findGeneByGeneExtGeneNameAndEnsemblVersNum(String extGeneName, Integer ensemblVersNum) {
		return geneRepo.findGeneByExtGeneNameAndEnsemblVersNum(extGeneName, ensemblVersNum);
	}

	public List <Experiment> findAllExperiments() {
		List <Experiment> expts = (List <Experiment>) experimentRepo.findAll();
		return expts;
	}
	
	public Experiment findExperimentByExptId(Long eid) {
		Experiment expt = experimentRepo.findExperimentByExptId(eid);
		return expt;
	}
	
	public Experiment findExperimentByExptCode(String exptCode) {
		Experiment expt = experimentRepo.findExperimentByExptCode(exptCode);
		return expt;
	}
	
	public ExpressionProfileLite findExpressionProfileByGeneIdAndSampleSetId(Long geneId, Long sampleSetId) {
		RNASeqProfile profile = rnaseqProfileRepo.getRNASeqProfileByGeneIdAndSampleSetId(geneId, sampleSetId);				
		ObjectMapper mapper = new ObjectMapper();
		ExpressionProfileLite liteProfile = profile.getExpressionProfileLite(mapper);
		return liteProfile;
	}
	
	public RNASeqProfile findProfileByGeneIdAndSampleSetId(Long geneId, Long sampleSetId) {
		RNASeqProfile profile = rnaseqProfileRepo.getRNASeqProfileByGeneIdAndSampleSetId(geneId, sampleSetId);				
		return profile;
	}
	
	public String getEntityCountsString() {
		String counts = "";
		
		counts += "project: " + projectRepo.count() + "\n";
				
		counts += "experiment: " + experimentRepo.count() + "\n";
		
		counts += "sampleset: " + sampleSetRepo.count() + "\n";

		counts += "sample: " + sampleRepo.count() + "\n";

		counts += "treatment (treatment descriptors)" + treatmentRepo.count() +"\n";

		counts += "gene (gene annotations): " + geneRepo.count() + "\n";
		
		counts += "profiles (bulk rnaseq profiles) " + rnaseqProfileRepo.count() + "\n";

		counts += "geneset (archived gene profile sets): " + geneSetRepo.count() + "\n";
		
		counts += "cell (registered cell types):" + cellRepo.count() + "\n";

		return counts;
	}
	
	public String makeExpressionProfileImage(Integer w, Integer h, List<Double> values) {
		String base64Image = "";
		
		BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = bi.createGraphics();
		int xMargin = 5;
		int yMargin = 5;
		int boxW = w-2*xMargin;
		int boxH = h-2*yMargin;		

		g2d.drawRect(xMargin, yMargin, boxW, boxH);
		Color c;

		Graphics2D g2dGradient = (Graphics2D)bi.getGraphics();

		LinearGradientPaint lgp = new LinearGradientPaint(0,0,100,100, new float[]{0.0f, 1.0f}, new Color[] {Color.white, Color.blue});

		BufferedImage biGradient = new BufferedImage(100, 5, BufferedImage.TYPE_INT_RGB);
		GradientPaint gp = new GradientPaint(0,0, Color.white, 100, 5, Color.blue);
		Graphics2D biG2d = (Graphics2D)biGradient.getGraphics();
		biG2d.setPaint(gp);
		biG2d.fillRect(0, 0, 100, 5);
		
		int column = 0;
		int colWidth = (int)((double)boxW/(double)values.size());
		
		for(Double value : values) {
			c = new Color(biGradient.getRGB((int)(value*100), 1));
			System.out.println("Color:"+c.getRGB());
			g2d.setColor(c);
			g2d.fillRect(column*colWidth, 0, colWidth, yMargin + boxH);
			column++;
		}
		
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		try {
			ImageIO.write(bi, "png", os);
			base64Image = Base64.getEncoder().encodeToString(os.toByteArray());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return base64Image;
	}
			
	public String makeExpressionHeatmapImageForSampleSet(Long sampleSetId) {
		List <RNASeqProfile> profiles = rnaseqProfileRepo.getRNASeqProfileBySampleSetId(sampleSetId);
		ObjectMapper mapper = new ObjectMapper();
		ArrayList <ExpressionProfileLite> profileList = new ArrayList<ExpressionProfileLite>();
		for(RNASeqProfile profile : profiles) {
			profileList.add(profile.getExpressionProfileLite(mapper));
		}
		return makeExpressionHeatMapImage(500, 50000, profileList);
	}
	
	
	public String makeExpressionHeatMapImage(int w, int h, List<ExpressionProfileLite> values) {		
		
		String base64Image = "";
		
		BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = bi.createGraphics();
		int xMargin = 5;
		int yMargin = 5;
		int boxW = w-2*xMargin;
		int boxH = h-2*yMargin;		

		g2d.drawRect(xMargin, yMargin, boxW, boxH);
		Color c;

		Graphics2D g2dGradient = (Graphics2D)bi.getGraphics();

		BufferedImage biGradient = new BufferedImage(101, 5, BufferedImage.TYPE_INT_RGB);
		GradientPaint gp = new GradientPaint(0,0, Color.white, 101, 5, Color.blue);
		Graphics2D biG2d = (Graphics2D)biGradient.getGraphics();
		biG2d.setPaint(gp);
		biG2d.fillRect(0, 0, 101, 5);
		
		int column = 0;
		int row = 0;
		int colWidth = (int)((double)boxW/(double)values.get(1).getScaledNormCounts().size());
		int rowHeight = (int)((double)boxH/(double)values.size());
		
		System.out.println("row and col size "+rowHeight + "  "+colWidth);
		for(ExpressionProfileLite profile : values) {
			column = 0;
			for(Double value : profile.getScaledNormCounts()) {
				c = new Color(biGradient.getRGB((int)(value*100), 1));
			//	System.out.println("Color:"+c.getRGB());
				g2d.setColor(c);
				g2d.fillRect(column*colWidth, row*rowHeight, colWidth, rowHeight);
				column++;
			}
			row++;
		}
		
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		try {
			ImageIO.write(bi, "png", os);
			base64Image = Base64.getEncoder().encodeToString(os.toByteArray());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return base64Image;
	}

	public List<ExpressionProfileLite> getExpressionProfileLiteBySSIDPaginate(Long ssid, Integer page, Integer count) {
		
		return null;
	}

	public ArrayList<ExpressionProfileLite> getExpressionProfileLiteBySSIDPaginate(Long ssid, PageRequest pageRequest) {
			List<RNASeqProfile> profiles = rnaseqProfileRepo.findAllBySampleSetId(ssid, pageRequest);
			ObjectMapper mapper = new ObjectMapper();
			ArrayList <ExpressionProfileLite> profileList = new ArrayList<ExpressionProfileLite>();
			for(RNASeqProfile profile : profiles) {
				profileList.add(profile.getExpressionProfileLite(mapper));
			}
		return profileList;
	}	
	
	public List <Person> findAllPeople() {
		List <Person> people = (List <Person>) investigatorRepo.findAll();
		Collections.sort(people);
		return people;
	}
	
	public Long getPersonIdFromSample(Sample sample) {
		Long personId = null;
		ArrayList <SampleToSampleSetLinker> linkerList = (ArrayList <SampleToSampleSetLinker>) this.s2ssRepo.findSampleToSampleSetLinkersBySampleId(sample.getSampleId());

		if(linkerList.size() > 0) {
			SampleSet set = this.getSampleSetBySampleSetId(linkerList.get(0).getSampleSetId());
			personId = set.getInvestigatorId();
		}
		
		return personId;
	}
	
	public List<RNASeqAnalysis> findAllRNASeqAnalyses() {
		List <RNASeqAnalysis> analysisList = (List <RNASeqAnalysis>) rnaseqAnalysisRepo.findAll();
		return analysisList;
	}
	
	public RNASeqAnalysis findRNASeqAnalysisByAnalysisId(Long analysisId) {
		RNASeqAnalysis analysis = rnaseqAnalysisRepo.findRNASeqAnalysisByAnalysisId(analysisId);
		if(analysis != null) {
//			AnalysisConditionDefinition condDef = analysis.getConditionDef();
//			if(condDef != null)
//				System.out.println("HEYYYYYYYYYY  have cond def group count="+condDef.getAnalysisGroups().size());
//			else
//				System.out.println("Condition def is null????");
//			byte [] condDefBytes = analysis.getConditionDefJson();
//			if(condDefBytes == null) {
//				System.out.println("Hey Noooooo empty condDefBytes");
//			} else {
//				ObjectMapper mapper = new ObjectMapper();
//				try {
//					analysis.setConditionDef(mapper.readValue(condDefBytes, AnalysisConditionDefinition.class));
//				} catch (JsonParseException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				} catch (JsonMappingException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				System.out.println("OK.... made the cond def object");
//			System.out.println("Hey.... I've got a non null analysis");
//			}
//			if(analysis.getConditionGroups() == null)
//				System.out.println("Whoaaa we have a null conditionGroups");
//			else {
//				System.out.println("Have analysis without conditionGroups list.......");
//				//analysis.setConditionGroups(this.analysisGroupRepo.findAnalysisConditionGroupsByParentAnalysisId(analysis.getAnalysisId()));
//			}
		
		}
		return analysis;
	}	
	
	public AnalysisConditionGroup findAnalysisGroupById(Long analysisGroupId) {
		return analysisGroupRepo.findAnalysisConditionGroupByAnalysisGroupId(analysisGroupId);
	}
	
	public List<AnalysisConditionGroup> findAnalysisGroupsByAnalysisId(Long analysisId) {
		System.out.println("Heyyyyyyyyyy getting analysis group using analysis id? Is this called???????");
		return analysisGroupRepo.findAnalysisConditionGroupsByParentAnalysisId(analysisId);
	}
	
	public RNASeqDERecord findRNASeqDERecordByDeRecordId(Long deRecordId) {
		RNASeqDERecord deRecord = rnaseqDeRecordRepo.findRecordByDeRecordId(deRecordId);
		return deRecord;
	}
	
	public RNASeqDERecord findRNASeqDERecordByDeGeneIdAndAnalysisId(Long geneId, Long analysisId) {
		RNASeqDERecord deRecord = rnaseqDeRecordRepo.findRecordByGeneIdAndAnalysisId(geneId, analysisId);
		return deRecord;
	}
	
	public List<RNASeqDERecord> findRNASeqDERecordsByAnalysisId(Long analysisId) {
		List<RNASeqDERecord> deRecords = rnaseqDeRecordRepo.findRecordsByAnalysisId(analysisId);
		return deRecords;
	}
		
	public void saveRNASeqAnalysis(RNASeqAnalysis analysis) {
		rnaseqAnalysisRepo.save(analysis);
	}
	
	public void saveSampleSet(SampleSet sampleSet) {
		List <SampleToSampleSetLinker> linkages = sampleSet.getLinkages();
			
		sampleSetRepo.save(sampleSet);
	
		//do we actually now have linkages to samples?
		System.out.println("Persisted sample set, linkages?:");
		if(sampleSet.getLinkages() != null) {
			System.out.println("Yes, we have some linkages:"+sampleSet.getLinkages().size());
		} else {
			System.out.println("Nope... don't have automatic linkages");
		}
		
		for(SampleToSampleSetLinker linker: sampleSet.getLinkages()) {
			linker.setSampleSetId(sampleSet.getSampleSetId());
			
		}
		// need to persist the sampleset2_sample linkages

	}
	
	public void loadAnalyisFile(MultipartFile file) {
		analysisLoader.captureAnalysisHeader(file);
	}
}
