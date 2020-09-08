package gov.nih.ncats.omics.warehouse.rest.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import gov.nih.ncats.omics.warehouse.model.Cell;
import gov.nih.ncats.omics.warehouse.model.Experiment;
import gov.nih.ncats.omics.warehouse.model.ExpressionProfileLite;
import gov.nih.ncats.omics.warehouse.model.Gene;
import gov.nih.ncats.omics.warehouse.model.GeneSet;
import gov.nih.ncats.omics.warehouse.model.Person;
import gov.nih.ncats.omics.warehouse.model.Project;
import gov.nih.ncats.omics.warehouse.model.Sample;
import gov.nih.ncats.omics.warehouse.model.SampleSet;
import gov.nih.ncats.omics.warehouse.model.Treatment;
import gov.nih.ncats.omics.warehouse.service.OmicsService;


@RestController
public class OmicsController {

	@Autowired
	OmicsService omicsService;
	
	@RequestMapping("/sctlomics")
	public String main(Model model) {
		return "welcome";
	}
	
	@PostMapping("/uploadAnalysisFile")
	public String uploadAnalysisFile(@RequestParam("file") MultipartFile file) {
        		
		omicsService.loadAnalyisFile(file);
		
		return "ok";
	}
	
    @RequestMapping("/")
    public String index() {
    	System.out.println("index()");
        return "Spring Boot Example";
    }
    
    @RequestMapping("/index")
    public ResponseEntity<String> index2() {
        return new ResponseEntity<String>("Omics Warehouse", HttpStatus.OK);
    }
    
    @RequestMapping(path = "/sctlomics/entity/count", produces="text/plain")
    public ResponseEntity<String> getEntityCountString() {
        return new ResponseEntity<String>(omicsService.getEntityCountsString(), HttpStatus.OK);
    } 
   
    /****************************
     ****************************
     * 
     *
     *  Resource: BULK RNASEQ
     * 
     * 
     ****************************
     ****************************/
    
    /*
     *  Project Endpoints
     */    
    
    @RequestMapping(path = "/sctlomics/projects", produces = "application/json")
    @ResponseBody
    public ArrayList<Project> getProjects() {
    	ArrayList <Project> projects = omicsService.getProjects();
    	
    	System.out.println("Project in controller:"+projects.size());
        return projects;
    }
    
    @RequestMapping(path = "/sctlomics/project/{pid}", produces = "application/json")
    @ResponseBody
    public Project getProjectByProjectId(@PathVariable Long pid) {
    	Project project = omicsService.getProjectByProjectId(pid);
    	return project;
    }
    
    @RequestMapping(path = "/sctlomics/project/code/{pcode}", produces = "application/json")
    @ResponseBody
    public Project getProjectByProjectCode(@PathVariable String pcode) {
    	Project project = omicsService.getProjectByProjectCode(pcode);
    	return project;
    }

    
    
    /*
     *  Experiment Endpoints
     */
    
    @RequestMapping(path = "/sctlomics/experiments", produces = "application/json")
    @ResponseBody 
    public List <Experiment> findAllExperiments() {	
    	List <Experiment> expts = omicsService.findAllExperiments();
    	return expts;             
    }
    
    @RequestMapping(path = "/sctlomics/experiment/{eid}", produces = "application/json")
    @ResponseBody 
    public Experiment findExptByExptId(@PathVariable Long eid) {	
    	Experiment expt = omicsService.findExperimentByExptId(eid);
    	return expt;             
    }
    
    
    /*
     * Sample Set Endpoints
     */
    
    @RequestMapping(path = "/sctlomics/sampleset/", produces = "application/json")
    @ResponseBody 
    public List <SampleSet> getSampleSets() {	
    	List <SampleSet> sampleSets = omicsService.getSampleSets();
    	//Sample sample = sampleSet.getSample(0);
    	//System.out.println(sample.getBaseCellId()+" <-- base and target ids --> " + sample.getTargetCellId());

    	return sampleSets;             
    }
    
    @RequestMapping(path = "/sctlomics/sampleset/{ssid}", produces = "application/json")
    @ResponseBody 
    public SampleSet getSampleSetBySampleSetId(@PathVariable Long ssid) {	
    	SampleSet sampleSet = omicsService.getSampleSetBySampleSetId(ssid);
    	//Sample sample = sampleSet.getSample(0);
    	//System.out.println(sample.getBaseCellId()+" <-- base and target ids --> " + sample.getTargetCellId());

    	return sampleSet;             
    }
    
    @RequestMapping(path = "/sctlomics/project/{pid}/samplesets", produces = "application/json")
    @ResponseBody 
    public List <SampleSet> getSampleSetsByProjectId(@PathVariable Long pid) {	
    	List <SampleSet> sampleSets = omicsService.getSampleSetsByProjectId(pid);
    	return sampleSets;             
    }
    
    @RequestMapping(path = "/sctlomics/project/code/{pcode}/samplesets", produces = "application/json")
    @ResponseBody 
    public List <SampleSet> getSampleSetsByProjectId(@PathVariable String pcode) {	
    	List <SampleSet> sampleSets = omicsService.getSampleSetsByProjectCode(pcode);
    	return sampleSets;             
    }
    
    
    
    /*
     * Sample Endpoints
     */
    
    
    @RequestMapping(path = "/sctlomics/sample/{sid}", produces = "application/json")
    @ResponseBody 
    public Sample getSampleBySampleId(@PathVariable Long sid) {	
    	Sample sample = omicsService.getSampleBySampleId(sid);
    	return sample;             
    }
    
    
    
    /*
     * Profile Endpoints 
     */
    
    @RequestMapping(path = "/sctlomics/sampleset/{ssid}/gene/{gid}/profile", produces = "application/json")
    @ResponseBody 
    public ExpressionProfileLite findProfileByGeneIdAndSampleSetId(@PathVariable Long gid, @PathVariable Long ssid) {	
    	ExpressionProfileLite profile = omicsService.findExpressionProfileByGeneIdAndSampleSetId(gid, ssid);
    	return profile;             
    }
    
    @RequestMapping(path = "/sctlomics/sampleset/{ssid}/profiles/norm", produces = "application/x-download")
    @ResponseBody 
    public ResponseEntity<String> getRNASeqProfilesBySampleSetId(@PathVariable Long ssid) {	
    	String tab = omicsService.getRNASeqExpressionTableBySampleSetId(ssid, 1);
    	return ResponseEntity.ok().contentType(MediaType.parseMediaType("application/x-download")).header("Content-disposition","attachment; filename=DataSet_"+ssid+"_Count_Matrix_for_Selected_Samples.txt").body(tab);             
    }
    
    @RequestMapping(path = "/sctlomics/sampleset/{ssid}/profiles/raw", produces = "application/x-download")
    @ResponseBody 
    public ResponseEntity<String> getRawRNASeqProfilesBySampleSetId(@PathVariable Long ssid) {	
    	String tab = omicsService.getRNASeqExpressionTableBySampleSetId(ssid, 0);
    	return ResponseEntity.ok().contentType(MediaType.parseMediaType("application/x-download")).header("Content-disposition","attachment; filename=DataSet_"+ssid+"_Count_Matrix_for_Selected_Samples.txt").body(tab);             
    }
    
    @RequestMapping(path = "/sctlomics/sampleset/{ssid}/profiles/scaled", produces = "application/x-download")
    @ResponseBody 
    public ResponseEntity<String> getScaledRNASeqProfilesBySampleSetId(@PathVariable Long ssid) {	
    	String tab = omicsService.getRNASeqExpressionTableBySampleSetId(ssid, 2);
    	return ResponseEntity.ok().contentType(MediaType.parseMediaType("application/x-download")).header("Content-disposition","attachment; filename=DataSet_"+ssid+"_Count_Matrix_for_Selected_Samples.txt").body(tab);             
    }
    
    @RequestMapping(path = "/sctlomics/sampleset/{ssid}/profiles/scaled/page/{page}/{count}", produces = "application/json")
    @ResponseBody 
    public List<ExpressionProfileLite> getScaledRNASeqProfilesBySampleSetId(@PathVariable Long ssid, @PathVariable Integer page, @PathVariable Integer count) {	
    	Sort s;
    	List<ExpressionProfileLite> profiles = omicsService.getExpressionProfileLiteBySSIDPaginate(ssid, PageRequest.of((int)page, (int)count, Sort.by(Sort.Direction.ASC, "extGeneName")));
    	return profiles;
    }
    
    
    
    
    @RequestMapping(path = "/sctlomics/sample/{sid}/treatments", produces = "application/json")
    @ResponseBody
    public ArrayList<Treatment> getTreatmentsBySampleId(@PathVariable Long sid) {
    	ArrayList <Treatment> treatments = omicsService.getTreatmentBySampleId(sid);
    	
    	System.out.println("Treatment in controller:"+treatments.size());
        return treatments;
    }
    

    @RequestMapping(path = "/sctlomics/cells", produces = "application/json")
    @ResponseBody 
    public List <Cell> findAllCells() {
    	List <Cell> cells = omicsService.findAllCells();    	
    	return cells;             
    }
    
    @RequestMapping(path = "/sctlomics/cell/{cid}", produces = "application/json")
    @ResponseBody 
    public Cell getCellByCellTypeId(@PathVariable Long cid) {
    	Cell cell = omicsService.getCellByCellTypeId(cid);    	
    	return cell;             
    }
    
    @RequestMapping(path = "/sctlomics/profile/image", produces = "image/base64")
    @ResponseBody 
    public ResponseEntity<String> getProfileImage() {
    	ArrayList <Double> values = new ArrayList<Double>();
    	values.add(0.1);
    	values.add(0.12);
    	values.add(0.20);
    	values.add(0.35);
    	values.add(0.78);
    	values.add(0.88);
    	values.add(0.96);
    	
    	String base64Str = omicsService.makeExpressionProfileImage(new Integer(400), new Integer(25), values);    	
    	
    	return ResponseEntity.ok().contentType(MediaType.parseMediaType("image/base64")).body(base64Str);         
    }

    @RequestMapping(path = "/sctlomics/sampleset/{sid}/heatmap/image", produces = "image/base64")
    @ResponseBody 
    public ResponseEntity<String> getHeatMapImage(@PathVariable Long sid) {
    	
    	String base64Str = omicsService.makeExpressionHeatmapImageForSampleSet(sid);	
    	
    	return ResponseEntity.ok().contentType(MediaType.parseMediaType("image/base64")).body(base64Str);         
    }
    
    
    @RequestMapping(path = "/sctlomics/geneset/{gsid}", produces = "application/json")
    @ResponseBody 
    public GeneSet findDiscoveryGeneSetByDiscoveryId(@PathVariable Long gsid) {	
    	GeneSet set = omicsService.findGeneSetByGeneSetId(gsid);
    	return set;             
    }

    @RequestMapping(path = "/sctlomics/gene/{gid}", produces = "application/json")
    @ResponseBody 
    public Gene findGeneByGeneId(@PathVariable Long gid) {	
    	Gene gene = omicsService.findGeneByGeneId(gid);
    	return gene;             
    }
    
    @RequestMapping(path = "/sctlomics/gene/name/{egn}/version/{vers}", produces = "application/json")
    @ResponseBody 
    public Gene findGeneByExtGeneName(@PathVariable String egn, @PathVariable Integer vers) {	
    	Gene gene = omicsService.findGeneByGeneExtGeneNameAndEnsemblVersNum(egn, vers);
    	return gene;             
    }
    

    @RequestMapping(path = "/sctlomics/people", produces = "application/json")
    @ResponseBody 
    public List <Person> findAllPeople() {	
    	List <Person> investigator = omicsService.findAllPeople();
    	return investigator;             
    }
    
}
