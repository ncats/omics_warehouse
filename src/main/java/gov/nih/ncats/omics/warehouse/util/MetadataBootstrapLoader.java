package gov.nih.ncats.omics.warehouse.util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.Locale;

import gov.nih.ncats.omics.warehouse.model.Cell;
import gov.nih.ncats.omics.warehouse.model.Experiment;
import gov.nih.ncats.omics.warehouse.model.Person;
import gov.nih.ncats.omics.warehouse.model.Project;
import gov.nih.ncats.omics.warehouse.model.Sample;
import gov.nih.ncats.omics.warehouse.model.SampleSet;
import gov.nih.ncats.omics.warehouse.model.Treatment;

public class MetadataBootstrapLoader {
	
	private ArrayList <Cell> cells;
	private ArrayList <Person> investigators;
	private ArrayList <Project> projects;
	private ArrayList <Experiment> experiments;
	private ArrayList <SampleSet> sampleSets;
	private ArrayList <Treatment> treatments;
	
	private Connection conn;
	
	public MetadataBootstrapLoader() {
		super();
		this.cells = new ArrayList <Cell>();
		this.investigators = new ArrayList <Person>();
		this.projects = new ArrayList <Project>();
		this.experiments = new ArrayList <Experiment>();
		this.sampleSets = new ArrayList <SampleSet>();
		this.treatments = new ArrayList <Treatment>();
	}

	public void dumpDataStatus() {
		System.out.println("Cell Line Count: " + cells.size());
		System.out.println("\nInvestigator Count: " + investigators.size());
		System.out.println("\nProject Count: " + projects.size());
		System.out.println("\nExperiment Count: " + experiments.size());
		System.out.println("\nSampleSet Count: "+sampleSets.size());
		System.out.println("\nTreatment Count: "+treatments.size());
		
		int setIndex = 1;
		for(SampleSet set : sampleSets) {
			System.out.println("SampleSet "+setIndex+" Sample Count:"+set.getSamples().size());
			setIndex++;
		}
	}
	
	public void loadAndcommitMetadata() {
		//get connection
		
	}

	public void captureMetadataFromFile(String localPath) {
		String buffer = "";

		try {
			
			//just get the data in and buffered
			BufferedReader br = new BufferedReader(new FileReader(localPath));
			String line;
			
			while((line = br.readLine()) != null) {
				buffer += line + "\n";
			}
			
			br.close();
			
			System.out.println(buffer);
			
			String command;
			String currentBlock = "";
			BufferedReader dataReader = new BufferedReader(new StringReader(buffer));
			while((command = dataReader.readLine()) != null) {
				command = command.trim();				
				System.out.println("Command = ***"+ command.substring(0, 8)+"***");
				if(command.startsWith("[")) {
					System.out.println("Command Starts with [");
					if(command.startsWith("[Investigator")) {
						currentBlock = "I";
						System.out.println("In "+currentBlock);
					} else if(command.startsWith("[Cell")) {
						currentBlock = "C";
						System.out.println("In "+currentBlock);
					} else if(command.startsWith("[Project")) {
						currentBlock = "P";
						System.out.println("In "+currentBlock);
					} else if(command.startsWith("[Experiment")) {
						currentBlock = "E";
						System.out.println("In "+currentBlock);
					} else if(command.startsWith("[Sample_Set")) {
						currentBlock = "SS";
						System.out.println("In "+currentBlock);
					} else if(command.startsWith("[Samples")) {
						currentBlock = "S";
						System.out.println("In "+currentBlock);
					} else if(command.startsWith("[Treatment")) {
						currentBlock = "T";
					}
				} else {
					//building an entity
					buildEntity(currentBlock, command);
				}				
			}
			
			//associate projects with experiments
			if((projects != null && projects.size() > 0) &&
					(experiments != null && experiments.size() > 0)) {
				for(Experiment expt : experiments) {
					for(Project project : projects) {
						expt.getParentProjects().add(project);
					}
				}
				
			}
			
			dataReader.close();
			
			//check data status
			dumpDataStatus();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private void buildEntity(String type, String data) {
		
		System.out.println("In buildEntity, type= "+type);
		
		String [] dataArr = data.split("\t");
		if(type.contentEquals("I")) {	
			Person inv = new Person();
			inv.setName(dataArr[1].trim());
			inv.setFirstName(dataArr[2].trim());
			inv.setLastName(dataArr[3].trim());
			this.investigators.add(inv);
		} else if(type.contentEquals("C")) {	
			Cell cell = new Cell();
			cell.setCellLineAbbr(dataArr[1]);
			cell.setOrganism(dataArr[2]);
			cell.setAtccId(dataArr[3]);
			cell.setTissueOrigin(dataArr[4]);			
			cell.setCellType(dataArr[5]);
			cell.setDescription(dataArr[6]);
			this.cells.add(cell);
		} else if(type.contentEquals("P")) {
			Project project = new Project();
			project.setProjectCode(dataArr[1]);
			project.setProjectName(dataArr[2]);
			project.setDescription(dataArr[3]);
			this.projects.add(project);
		} else if(type.contentEquals("E")) {
			Experiment expt = new Experiment();
			expt.setExptCode(dataArr[1]);
			expt.setExptName(dataArr[2]);
			expt.setExptDescription(dataArr[3]);
			try {
				Date date = new SimpleDateFormat("mm/dd/yyyy", Locale.ENGLISH).parse(dataArr[4].trim());
				expt.setRunDate(date);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			expt.setTechPlatform(dataArr[6]);
			expt.setTechnology(dataArr[5]);
			expt.setDataType(dataArr[7]);
			this.experiments.add(expt);
		} else if(type.contentEquals("SS")) {
			SampleSet set = new SampleSet();
			set.setProjectCode(dataArr[1]);
			set.setExptCode(dataArr[2]);
			set.setSetName(dataArr[3]);
			set.setSampleSetCode(dataArr[4]);
			set.setInvestigator(dataArr[5]);
			set.setGenerationProcess("primary-data-load");
			this.sampleSets.add(set);
		} else if(type.contentEquals("S")) {
			if(!dataArr[0].trim().contentEquals("sample")) {
				//jump out if we're in an unhandles section
				return;
			}
			
			Sample sample = new Sample();
			sample.setSampleCode(dataArr[2]);
			sample.setSampleName(dataArr[3]);
			sample.setDescription(dataArr[4]);
			sample.setBaseCellLine(dataArr[5]);
			sample.setTargetCellLine(dataArr[6]);
			
			//this sample should be added to the last sample set in the set
			sampleSets.get(sampleSets.size()-1).addSample(sample);			
		} else if(type.contentEquals("T")) {
			if(!dataArr[0].trim().contentEquals("treatment")) {
				//jump out if we're in an unhandles section
				return;
			}
			
			Treatment treatment = new Treatment();
			treatment.setSampleCode(dataArr[1]);
			treatment.setVarName(dataArr[3]);
			treatment.setVarType(dataArr[4]);
			treatment.setVarData(dataArr[5]);
			treatment.setVarUnit(dataArr[6]);
			
			try {
				treatment.setStartTime(Float.parseFloat(dataArr[7]));
			} catch (NumberFormatException nfe) {
				treatment.setStartTime(-1.0f);
			}
				
			try {
				treatment.setEndTime(Float.parseFloat(dataArr[8]));
			} catch (NumberFormatException nfe) {
				treatment.setEndTime(-1.0f);
			}
			
			treatment.setTimeUnit(dataArr[9]);
			
			//add the treatment info
			treatments.add(treatment);
		}
	}
	
	public void loadCapturedMetadata(String conf) {
		
		try {
			
			conn = UtilityConnector.getUtilConnection();
			
			//review data types and load sequentially		
			if(!this.investigators.isEmpty()) {
				loadInvestigators();
			}
			
			if(!this.cells.isEmpty()) {
				this.loadCells();
			}
			
			if(!this.projects.isEmpty()) {
				loadProjects();
			}
			
			if(!this.experiments.isEmpty()) {
				loadExperiments();
			} 
			
			if(!this.projects.isEmpty() && !this.experiments.isEmpty()) {
				//load experiment to project mappings
				loadProjectExperimentMapping();
			}
			
			if(!this.sampleSets.isEmpty()) {
				//this will load sample sets, samples and sample_set2sample			
				this.loadSampleSets();
			}
			
			// this loads treatments 
			if(!this.treatments.isEmpty()) {
				if(!this.sampleSets.isEmpty()) {
					connectTreatmentsToSamples();
					loadSampleTreatments();
				}				
			}
			
			conn.commit();
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void connectTreatmentsToSamples() {
		Hashtable<String, ArrayList <Treatment>> treatmentHash = new Hashtable<String, ArrayList <Treatment>>();
		ArrayList <Treatment> tList;
		for(Treatment treatment: treatments) {
			tList = treatmentHash.get(treatment.getSampleCode());
			if(tList == null) {
				tList = new ArrayList <Treatment>();
				treatmentHash.put(treatment.getSampleCode(), tList);
			}
			tList.add(treatment);			
		}

		
		for(SampleSet sampleSet : sampleSets) {
			for(Sample sample : sampleSet.getSamples()) {
				tList = treatmentHash.get(sample.getSampleCode());
				if(tList != null) {
					sample.setTreatments(tList);
				} else {
					//no treatments for sample
					System.out.println("No treatments for sample: "+sample.getSampleCode());
				}
			}
		}
	}
	
	public void loadInvestigators() throws SQLException {
		String sql = "insert into omics_adm.investigator (name, first_name, last_name) "+
		"select ?,?,? from dual "+
		"where not exists( select 1 from omics_adm.investigator where name = ?)";
		
		PreparedStatement ps = conn.prepareStatement(sql);
		
		for(Person investigator : investigators) {
			
			ps.setString(1, investigator.getName());
			
			if(investigator.getFirstName() != null)
				ps.setString(2, investigator.getFirstName());
			else
				ps.setNull(2, java.sql.Types.VARCHAR);

			if(investigator.getLastName() != null)			
				ps.setString(3, investigator.getLastName());
			else
				ps.setString(3, investigator.getFirstName());

			ps.setString(4, investigator.getName());
			
			ps.execute();
		}
		ps.close();	
	}
	
	
	public void collect(BufferedReader dataReader) {
		
	}
	
	
	public void loadInvestigator() {
		
	}
	
	public void loadCells() throws SQLException {
		String sql = "insert into omics_adm.cell (cell_line_abbr, cell_type, organism, tissue, atcc_id, description) "+
		"select ?,?,?,?,?,? from dual "+
		"where not exists( select 1 from omics_adm.cell where cell_line_abbr = ?)";
		
		PreparedStatement ps = conn.prepareStatement(sql);
		
		for(Cell cell : cells) {
			ps.setString(1, cell.getCellLineAbbr());
			ps.setString(2, cell.getCellType());
			ps.setString(3, cell.getOrganism());
			ps.setString(4,  cell.getTissueOrigin());
			ps.setString(5, cell.getAtccId());
			ps.setString(6, cell.getDescription());
			ps.setString(7,  cell.getCellLineAbbr());
			ps.execute();
			System.out.println("add "+cell.getCellLineAbbr());
		}
		ps.close();	
	}
	
	public void loadProjects() throws SQLException {	
		String sql = "insert into omics_adm.dim_project (user_project_code, name, description, update_date) "+
				"values (?,?,?,CURRENT_TIMESTAMP)";
		
		PreparedStatement checkStmt = conn.prepareStatement("select project_id from omics_adm.dim_project where user_project_code = ?");
		
		PreparedStatement insertStmt = conn.prepareStatement(sql, new String[] {"project_id"});
		
		ResultSet rs;
		
		for(Project project : projects) {
			checkStmt.setString(1, project.getProjectCode());
			rs = checkStmt.executeQuery();
			
			if(!rs.next()) { // new user project code
				
				insertStmt.setString(1, project.getProjectCode());
				insertStmt.setString(2, project.getProjectName());
				insertStmt.setString(3, project.getDescription());

				insertStmt.executeUpdate();
				
				rs.close();
				
				//set generated key
				rs = insertStmt.getGeneratedKeys();
				if(rs.next()) {
					// have the generated db key
					project.setProjectId(rs.getLong(1));
					
					System.out.println("Insert project dbPID:"+project.getProjectId());
				}			
			} else {
				//project exists
				project.setProjectId(rs.getLong("project_id"));
				System.out.println("Project "+project.getProjectCode()+" already exists.");
				rs.close();
			}
		}
		conn.commit();
		checkStmt.close();
		insertStmt.close();
	}
	
	public void loadExperiments() throws SQLException {
		
		String sql = "insert into omics_adm.dim_experiment (user_expt_code, name, description, expt_run_date,"
				+ " technology, technology_platform, data_type, update_date) values (?,?,?,?,?,?,?,CURRENT_TIMESTAMP)";
		
		PreparedStatement checkStmt = conn.prepareStatement("select expt_id from omics_adm.dim_experiment where user_expt_code = ?");		
		PreparedStatement insertStmt = conn.prepareStatement(sql, new String[] {"expt_id"});
		
		ResultSet rs;
		
		for(Experiment expt : experiments) {
			
			checkStmt.setString(1, expt.getExptCode());
			
			rs = checkStmt.executeQuery();
			
			if(!rs.next()) {
				insertStmt.setString(1, expt.getExptCode());
				insertStmt.setString(2, expt.getExptName());
				insertStmt.setString(3, expt.getExptDescription());
				insertStmt.setDate(4,  new java.sql.Date(expt.getRunDate().getTime()));
				insertStmt.setString(5,  expt.getTechnology());
				insertStmt.setString(6, expt.getTechPlatform());
				insertStmt.setString(7, expt.getDataType());
				insertStmt.executeUpdate();

				rs.close();

				rs = insertStmt.getGeneratedKeys();
				if(rs.next()) {
					Long exptId = rs.getLong(1);
					
					expt.setExptId(exptId);
				} else {
					System.out.println("No expt id");
				}
				System.out.println("add "+expt.getExptCode() + ": id "+expt.getExptId());
				rs.close();
				
			} else {
				//we have an experiment with an experiment id for this expt code
				System.out.println("Already have experiment: "+expt.getExptCode());
				expt.setExptId(rs.getLong("expt_id"));
				rs.close();
			}
		}
		conn.commit();
		checkStmt.close();
		insertStmt.close();
	}
	
	public void loadProjectExperimentMapping() throws SQLException {
		PreparedStatement checkStmt = conn.prepareStatement("select 1 from omics_adm.project_experiment where project_id = ? and expt_id = ?");		
		String sql = "insert into omics_adm.project_experiment (project_id, expt_id) values (?, ?)";
		PreparedStatement insertStmt = conn.prepareStatement(sql);
		
		ResultSet rs;
		for(Experiment expt : experiments) {
			for(Project project : expt.getParentProjects()) {
				checkStmt.setLong(1, project.getProjectId());
				checkStmt.setLong(2, expt.getExptId());
				rs = checkStmt.executeQuery();
				
				if(!rs.next()) {
					rs.close();
					
					// we don't have this mapping
					insertStmt.setLong(1, project.getProjectId());
					insertStmt.setLong(2, expt.getExptId());				
					insertStmt.execute();
				} else {
					// we have the mapping
					rs.close();
				}				
			}
		}
		insertStmt.close();
		checkStmt.close();		
	}
	
	public void loadSampleSets() throws SQLException {
		
		PreparedStatement checkStmt = conn.prepareStatement("select sample_set_id from omics_adm.dim_sample_set where set_name = ?");

		String sql = "insert into omics_adm.dim_sample_set (project_id, expt_id, invest_id, set_name, sample_set_code, generation_process, update_date) values (?, ?, ?, ?, ?, ?,  CURRENT_TIMESTAMP)";
		PreparedStatement insertStmt = conn.prepareStatement(sql, new String[] {"sample_set_id"});
		
		ResultSet rs;
		
		for(SampleSet set : sampleSets) {
			
			checkStmt.setString(1, set.getSetName());
			rs = checkStmt.executeQuery();
			
			if(!rs.next()) {
				set.setDbProjectId(this.retrieveProjectIdByProjectCode(set.getProjectCode()));
				set.setExptId(this.retrieveExptIdByExptCode(set.getExptCode()));
				set.setDbInvestigatorId(this.retrieveInvIdByInvName(set.getInvestigator()));
				
				insertStmt.setLong(1,  set.getProjectId());
				insertStmt.setLong(2, set.getExptId());
				insertStmt.setLong(3, set.getDbInvestigatorId());
				insertStmt.setString(4, set.getSetName());
				insertStmt.setString(5, set.getSampleSetCode());
				insertStmt.setString(6, set.getGenerationProcess());				
				insertStmt.executeQuery();
				rs = insertStmt.getGeneratedKeys();
				
				if(rs.next()) {
					set.setDbSetId(rs.getLong(1));
				}
				
				//now work on samples
				this.loadSamples(set);
			
			} else {
				//sample set already exists
				set.setSampleSetId(rs.getLong("sample_set_id"));
				rs.close();
				
				//set id, consider if this should trigger a sample entity update?
			} 			
		}
	}

	public void loadSamples(SampleSet set) throws SQLException {
		String sql = "insert into omics_adm.dim_sample (sample_code, name, description, base_cell_id, target_cell_id, update_date) values (?,?,?,?,?,CURRENT_TIMESTAMP)";
		PreparedStatement insertStmt = conn.prepareStatement(sql, new String[] {"sample_id"});
		ResultSet rs;
		
		//abort load if no sample set is present
		if(set.getDbSetId() == null) {
			return;
		}
		
		// need to query base_cell_id and target_cell id for base and target cell names
		Long baseCellId, targetCellId;

		for(Sample sample : set.getSamples()) {
			
			baseCellId = getIdForEntityByCode("Cell", sample.getBaseCellLine());
			if(baseCellId == null) {
				//if we don't have a cell type, we need to abort to populate cell type
				System.out.println("No cell id for type = "+sample.getBaseCellLine()+" for sample_code " + sample.getSampleCode());
				return;
			} 
			
			targetCellId = getIdForEntityByCode("Cell", sample.getTargetCellLine());
			
			if(targetCellId == null) {
				//if we don't have a cell type, we need to abort to populate cell type
				System.out.println("No cell id for type = "+sample.getTargetCellLine()+" for sample_code " + sample.getSampleCode());
				return;
			}
			
			insertStmt.setString(1, sample.getSampleCode());
			insertStmt.setString(2, sample.getSampleName());
			insertStmt.setString(3, sample.getDescription());
			insertStmt.setLong(4,baseCellId);
			insertStmt.setLong(5, targetCellId);
			
			insertStmt.execute();
			
			rs = insertStmt.getGeneratedKeys();
			
			//set the sample id
			if(rs.next()) {
				sample.setDbSampleId(rs.getLong(1));
			}
						
			rs.close();			
		}
		
		insertStmt.close();
		
		//add the sample set and samples to the mapping table
		loadSampleSet2Sample(set);
	}
	
	public void loadSampleSet2Sample(SampleSet set) throws SQLException {
		String sql = "insert into omics_adm.sample_set2sample (sample_set_id, sample_id, sample_ordinal) values (?,?,?)";
		PreparedStatement ps = conn.prepareStatement(sql);
		Integer order = 0;
		for(Sample sample : set.getSamples()) {
			ps.setLong(1, set.getDbSetId());
			ps.setLong(2, sample.getDbSampleId());
			ps.setInt(3, order);
			order++;
			ps.execute();
		}
		ps.close();
	}

	
	public Long getIdForEntityByCode(String entityType, String entityCode) throws SQLException {
		String sql = "select XXX from YYY where ZZZ = '"+entityCode+"'";
		Long id = null;
		
		if(entityType.contentEquals("Project")) {
			sql.replace("[X]", "project_id");
			sql.replace("[Y]",  "omics_adm.dim_project");
			sql.replace("[Z]", "user_project_code");
		} else if(entityType.contentEquals("Experiment")) {
			sql.replace("[X]", "expt_id");
			sql.replace("[Y]",  "omics_adm.dim_experiment");
			sql.replace("[Z]", "user_expt_code");
		} else if(entityType.contentEquals("Investigator")) {
			sql.replace("[X]", "investigator_id");
			sql.replace("[Y]",  "omics_adm.investigator");
			sql.replace("[Z]", "name");
		} else if(entityType.contentEquals("Cell")) {
			sql = sql.replace("XXX", "cell_type_id");
			sql = sql.replace("YYY",  "omics_adm.cell");
			sql = sql.replace("ZZZ", "cell_line_abbr");

			System.out.println("Cell sql:"+sql);
		} 
		
		PreparedStatement ps = conn.prepareStatement(sql);
		ResultSet rs = ps.executeQuery();
		
		if(rs.next()) {
			id = rs.getLong(1);
			rs.close();
			ps.close();
			return id;
		} else {
			rs.close();
			ps.close();
			return null;
		}
		
	}
	
	public void loadSampleTreatments() throws SQLException {
		//traverse sample sets, load all sample treatments
		
		String sql = "insert into omics_adm.treatment (sample_id, var_name, var_type, var_data, var_unit, start_time, end_time, time_unit) "
				+ "values (?,?,?,?,?,?,?,?)";
		PreparedStatement insertStmt = conn.prepareStatement(sql);
		PreparedStatement checkStmt = conn.prepareStatement("select 1 from omics_adm.treatment where sample_id = ? and "
				+ "var_name = ? and var_type = ? and var_data = ? and start_time = ?");
		
		Long sampleId;
		ResultSet rs;
		
		for(SampleSet sampleSet : sampleSets) {
			for(Sample sample : sampleSet.getSamples()) {
				sampleId = sample.getSampleId();
				
				for(Treatment treatment : sample.getTreatments()) {
					checkStmt.setLong(1, sampleId);
					checkStmt.setString(2, treatment.getVarName());
					checkStmt.setString(3, treatment.getVarType());
					checkStmt.setString(4, treatment.getVarData());
					checkStmt.setDouble(5,  treatment.getStartTime());
				
					rs = checkStmt.executeQuery();
					if(!rs.next()) {
						//new treatment
						insertStmt.setLong(1, sampleId);
						insertStmt.setString(2, treatment.getVarName());
						insertStmt.setString(3, treatment.getVarType());
						insertStmt.setString(4, treatment.getVarData());
						insertStmt.setString(5, treatment.getVarUnit());
						insertStmt.setDouble(6,  treatment.getStartTime());
						insertStmt.setDouble(7,  treatment.getEndTime());
						insertStmt.setString(8, treatment.getTimeUnit());
						
						insertStmt.execute();
					} else {
						//treatment exists
						System.out.println("Treatment Exists:"+treatment.toString());
					}
					
					
				}				
			}
		}
	}
	
	public Long retrieveCellIdByCellName(String name) throws SQLException {
		String sql = "select cell_type_id from omics_adm.dim_cell where cell_line_abbr = ?";
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setString(1, name);
		ResultSet rs = ps.executeQuery();
		Long cellId = null;
		while(rs.next()) {
			cellId = rs.getLong("cell_type_id");
		}
		return cellId;
	}
	
	public Long retrieveProjectIdByProjectCode(String projectCode) throws SQLException {
		String sql = "select project_id from omics_adm.dim_project where user_project_code = ?";
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setString(1, projectCode);
		ResultSet rs = ps.executeQuery();
		Long projectId = null;
		System.out.println("Getting projectId based on code="+projectCode+"****");
		
		if (rs.next()) {
			projectId = rs.getLong("project_id");
			System.out.println("have project id to set id:"+projectId);			
		} else {
			System.out.println("No project id for this p code");
		}
		 
		return projectId;
	}
	
	
	public Long retrieveExptIdByExptCode(String exptCode) throws SQLException {
		String sql = "select expt_id from omics_adm.dim_experiment where user_expt_code = ?";
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setString(1, exptCode);
		ResultSet rs = ps.executeQuery();
		Long exptId = null;
		while(rs.next()) {
			exptId = rs.getLong("expt_id");
		}
		return exptId;
	}
	
	public Long retrieveInvIdByInvName(String name) throws SQLException {
		String sql = "select invest_id from omics_adm.investigator where name = ?";
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setString(1, name);
		ResultSet rs = ps.executeQuery();
		Long exptId = null;
		while(rs.next()) {
			exptId = rs.getLong("invest_id");
		}
		return exptId;
	}
	
	public void makeDummyInvestigators() {
		Person inv = new Person();
		inv.setName("Pei-Hsuan Chu");
		investigators.add(inv);
		Person inv2 = new Person();
		inv2.setName("Tao Deng");
		investigators.add(inv2);
		Person inv3 = new Person();
		inv3.setName("Carlos Tristan");
		investigators.add(inv3);
	}

	public void loadSingleCellProfiles() {
		
	}
	
	
	public static void main(String [] args) {
		
		MetadataBootstrapLoader m = new MetadataBootstrapLoader();
		
		//m.makeDummyInvestigators();
		
		m.captureMetadataFromFile("C:\\Users\\braistedjc\\Desktop\\Analysis\\SCTL_Omics_Warehouse\\data\\to_load\\sc_data\\sc_metadata_example.txt");

		
		m.loadCapturedMetadata("C:\\Users\\braistedjc\\eclipse-workspace\\Omics_Warehouse_Play\\conf\\application.conf");

		//m.dumpDataStatus();
	}
}
