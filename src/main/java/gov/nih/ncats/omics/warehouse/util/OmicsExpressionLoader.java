package gov.nih.ncats.omics.warehouse.util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Hashtable;

import javax.xml.bind.DatatypeConverter;

import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import gov.nih.ncats.omics.warehouse.model.ExpressionProfileLite;
import gov.nih.ncats.omics.warehouse.model.Project;
import gov.nih.ncats.omics.warehouse.model.Sample;
import gov.nih.ncats.omics.warehouse.repository.ProjectRepo;

public class OmicsExpressionLoader {

	private ArrayList <ExpressionProfileLite> profiles;
	private Hashtable <Long, ExpressionProfileLite> geneIdProfileHash;
	private Hashtable <String, Long> geneToDBIDHash;
	private Hashtable <Long, ArrayList <String>> geneIdToEnsemblIdHash;
	private Hashtable <Long, String> geneIdToExtGeneNameHash;	
	private Hashtable <String, String> geneToDescriptionHash;
	
	String dependencyMessage;
	
	private Long projectId;
	private Long exptId;
	private Long sampleSetId;
	
	boolean isProfileUpdate;
	
	public OmicsExpressionLoader() {
		dependencyMessage = "OK";
		isProfileUpdate = false;
	}
	
	public void constructEntities(String dataFile, String conf, boolean isNormalized) {

		//if we have profiles, just update the profiles refer to hash of gene id to profiles.
		if(profiles != null && profiles.size() > 0) {
			isProfileUpdate = true;
		} else {
			//
			isProfileUpdate = false;
			profiles = new ArrayList <ExpressionProfileLite>();
			geneIdProfileHash = new Hashtable<Long, ExpressionProfileLite>();
		}
		
		try {
			String line;
			BufferedReader br = new BufferedReader(new FileReader(dataFile));
			String projectCode = null;
			String exptCode = null;
			String sampleSetCode = null;
			Integer ensemblVers;

			int lineNum = 0;
			ExpressionProfileLite profile;
			String [] data;
			Long geneId;
			String description;
			while((line = br.readLine()) !=null) {
				if(lineNum < 5) {
					if(lineNum == 0) {
						System.out.println("line"+line);
						projectCode = line.split("\t")[1].trim();
					
					}
					if(lineNum == 1)
						exptCode = line.split("\t")[1].trim();
					if(lineNum == 2)
						sampleSetCode = line.split("\t")[1].trim();
					if(lineNum == 3) {
						ensemblVers = Integer.parseInt(line.split("\t")[1].trim());

						//have version capture the gene name to id hash for version
						//also capture geneId to description and to ensemblId list
						extractGeneNameToIdHash(ensemblVers, conf);
						System.out.println("GeneHash size="+this.geneToDBIDHash.values().size());
						System.out.println("Gene description size="+this.geneToDescriptionHash.size());
						
						//check cardinality of mapping from gene name to ensembl id;
						for(long key : this.geneIdToEnsemblIdHash.keySet()) {
							if(this.geneIdToEnsemblIdHash.get(key).size() > 1) {
								for(String ensemblId : this.geneIdToEnsemblIdHash.get(key)) {
									System.out.print("gene name repeat ");
									System.out.println(ensemblId + " " + key + " " + this.geneIdToExtGeneNameHash.get(key));
								}
							}
						}
						
						//now validate that the project code, experiment code and sample set name are already registered.
						//if not, halt load gracefully and log issue.
						// this will also collect entity ids based on supplied names or codes.
						dependencyMessage = validateEntitiesAndGetCodes(projectCode, exptCode, sampleSetCode);
						
						if(!dependencyMessage.contentEquals("OK")) {
							// dependency message holds the fault
							// log dependency violation and return.
							System.out.println(dependencyMessage);
							return;
						}
						
					}
					
					lineNum++;

					
				} else {

					data = line.split("\t");
					
					if(!isProfileUpdate) {
						profile = new ExpressionProfileLite();
					} else {
						// this is using gene name to gene id, then gene id to profile
						// do we really want to key by gene name?
						profile = this.geneIdProfileHash.get(this.geneToDBIDHash.get(data[0]));
						if(profile == null) {
							System.out.println("Bad profile? "+data[0]+" "+this.geneToDBIDHash.get(data[0]));
							System.out.println("geneIdProfileHash.size() = "+geneIdProfileHash.size());
						}
					}					
	
					//
					//
					//
					//   If this is an update, we need to be sure that the metadata references are exactly the same
					//   Genes may be ordered differently, so we need to key on a consistent key like our db gene id mapped from ext gene name?
					//   
					//  If the metadata entities are teh same, then setting things like sample set id, gene name, gene id, description, 
					
					
					if(!isProfileUpdate) {
						profile.setSampleSetId(sampleSetId);

						profile.setExtGeneName(data[0]);

						geneId = geneToDBIDHash.get(profile.getExtGeneName());
						profile.setGeneId(geneId);
						profile.setGeneDescription(this.geneToDescriptionHash.get(profile.getExtGeneName()));
						profile.setEnsemblId(this.geneIdToEnsemblIdHash.get(geneId).get(0));

						geneIdProfileHash.put(geneId, profile);

						//only add protein if not an update.
						profiles.add(profile);					

					}

					// if this is an update, should we assume that it's the other data type.
					// if it's marked as normalized and a normalized vector extists, then we should
					// drop out. Don't add extra expression values that would extend the vectors.
					//
					// if i = 1 (just starting) and normalized, but normalized vector is not empty, then drop out.
					for(int i = 1; i < data.length; i++) {
						if(isNormalized) {
							if(i == 1 && !profile.getNormCounts().isEmpty()) {
								// we're adding a values to normalized values we alraedy have.
							} else {
								profile.addNormalizedExpressionValue(Double.parseDouble(data[i]));
							}
						} else
							profile.addRawExpressionValue(Integer.parseInt(data[i]));
					}

					//finished adding normalized data, now set the scaled values for normalized values
					//scaled values support heatmap display. Important to also deliver min and max normalized counts for context.
					if(isNormalized) {
						profile.createProfileScaledValues();
					}
					
				}
			}
			
			System.out.println("Profile List size="+this.profiles.size());

			br.close();
					
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public String validateEntitiesAndGetCodes(String projectCode, String exptCode, String sampleSetCode) {
		String status = "OK";
		Connection conn = UtilityConnector.getConnection();

		String projectSql = "select project_id from omics_adm.dim_project where user_project_code = ?";
		String exptSql = "select expt_id from omics_adm.dim_experiment where user_expt_code = ?";
		String sampleSetSql = "select sample_set_id from omics_adm.dim_sample_set where sample_set_code = ?";

		try {
			PreparedStatement ps = conn.prepareStatement(projectSql);
			ps.setString(1, projectCode);
			ResultSet rs = ps.executeQuery();
			if(rs.next()) {
				// have the project
				this.projectId = rs.getLong("project_id");
			} else {
				status = "Project "+projectCode + " has not been registered.\n";
			}
			rs.close();
			ps.close();

			ps = conn.prepareStatement(exptSql);
			ps.setString(1, exptCode);
			rs = ps.executeQuery();
			if(rs.next()) {
				// have the expt
				this.exptId = rs.getLong("expt_id");
			} else {
				status = "Experiment "+exptCode + " has not been registered.\n";
			}
			rs.close();
			ps.close();

			ps = conn.prepareStatement(sampleSetSql);
			ps.setString(1, sampleSetCode);
			rs = ps.executeQuery();
			if(rs.next()) {
				// have the sample set
				this.sampleSetId = rs.getLong("sample_set_id");
			} else {
				status = "Sample Set "+ sampleSetCode + " has not been registered.\n";
			}
			rs.close();
			ps.close();

			if(!status.contentEquals("OK")) {
				status += "Abort Expression Load. Resolve above metadata entity dependencies.\n";
			}

			conn.close();

		} catch (SQLException sqle) {
			sqle.printStackTrace();
		}

		return status;
	}

	private void extractGeneNameToIdHash(Integer ensemblVers, String conf) {
		this.geneToDBIDHash = new Hashtable<String, Long>();
		this.geneToDescriptionHash = new Hashtable<String, String>();
		this.geneIdToEnsemblIdHash = new Hashtable<Long, ArrayList<String>>();
		this.geneIdToExtGeneNameHash = new Hashtable<Long, String>();
		UtilityConnector uc = new UtilityConnector(conf);
			
		try {
			Connection conn = UtilityConnector.getConnection();
			PreparedStatement ps = conn.prepareStatement("select gene_id, ext_gene_name, description, ensembl_id from omics_adm.dim_gene where ensembl_vers_num = ?");
			ps.setInt(1,ensemblVers);
			ResultSet rs = ps.executeQuery();
			String extGeneName;
			Long geneId;
			String description;
			String ensemblId;
			ArrayList <String> ensemblIdList;
			while(rs.next()) {
				extGeneName = null;
				geneId = null;
				description = null;
				
				extGeneName = rs.getString("ext_gene_name");
				geneId = rs.getLong("gene_id");
				description = rs.getString("description");
				ensemblId = rs.getString("ensembl_id");
				
				if(extGeneName != null && geneId != null) {
					this.geneToDBIDHash.put(extGeneName, geneId);
					this.geneIdToExtGeneNameHash.put(geneId, extGeneName);
					ensemblIdList = this.geneIdToEnsemblIdHash.get(geneId);
					
					if(ensemblIdList == null) {
						ensemblIdList = new ArrayList <String>();
						this.geneIdToEnsemblIdHash.put(geneId, ensemblIdList);						
					}
					ensemblIdList.add(ensemblId);
				}
				if(extGeneName != null && description != null) {
					this.geneToDescriptionHash.put(rs.getString("ext_gene_name"), rs.getString("description"));
				}				
			}
			rs.close();
			ps.close();
			conn.close();
			
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}

	
	public void loadProfilesTable(String conf, int dataType) {

		Connection conn = null;
		String insertSql = "insert into omics_adm.fact_bulk_rnaseq_profile (gene_id, sample_set_id, ext_gene_name, gene_description, profile_json_blob, ensembl_id, data_type ) "+
				"values (?,?,?,?,?,?,?)";
		try {
			conn = UtilityConnector.getConnection();
			PreparedStatement ps = conn.prepareStatement(insertSql);
			int noIdCount = 0;
			
			int count = 1;
			
			ObjectMapper objectMapper = new ObjectMapper();
			
			for(ExpressionProfileLite profile: profiles) {
				if(profile.getGeneId() != null) {
					ps.setLong(1, profile.getGeneId());
					ps.setLong(2, profile.getSampleSetId());
					ps.setString(3, profile.getExtGeneName());
					ps.setString(4, profile.getGeneDescription());
					ps.setBytes(5, objectMapper.writeValueAsBytes(profile));
					ps.setString(6, profile.getEnsemblId());
					ps.setInt(7, dataType);

					ps.addBatch();					
					if(count % 100 == 0) {
						ps.executeBatch();
					}
					count++;
				} else {
					System.out.println("No gene id for: "+ profile.getExtGeneName());
					noIdCount++;
				}
			}
			
			ps.executeBatch();
			ps.close();
			conn.commit();
			conn.close();
			
		} catch (SQLException e) {
			
			try {
				if(conn != null) {
					conn.rollback();
					conn.close();
				}
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	
		
	}
	
	public void testQuery(String extGeneName, String conf) {
		UtilityConnector uc = new UtilityConnector(conf);
		Connection conn = uc.connect();
		
		try {
			String sql = "select * from omics_adm.fact_bulk_rnaseq_profile where ext_gene_name =?";
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, extGeneName);
			ResultSet rs = ps.executeQuery();
			if(rs.next()) {
				System.out.println("Gene: "+rs.getString("gene_id"));
				System.out.println("SampleSetId: "+rs.getString("sample_set_id"));
				System.out.println("ExtGeneName: "+rs.getString("ext_gene_name"));
				System.out.println("Description: "+rs.getString("gene_description"));
				byte [] arr = rs.getBytes("profile_json_blob");
				String base64Encoded = DatatypeConverter.printBase64Binary(arr);
				System.out.println("\nA Life Encoded...Base64...\n"+base64Encoded);
				
				ObjectMapper mapper = new ObjectMapper();
				ExpressionProfileLite profile = mapper.readValue(arr, ExpressionProfileLite.class);
				
				System.out.println("Have profile object, norm arraylist size: "+profile.getNormCounts().size());
				
				String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(profile);
				System.out.println("\nProfile JSON\n"+json);
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void testQueryAllProfiles(Long sampleSetId, String conf) {
		UtilityConnector uc = new UtilityConnector(conf);
		ArrayList<ExpressionProfileLite> profiles = new ArrayList<ExpressionProfileLite>();
		ObjectMapper mapper = new ObjectMapper();
		byte [] arr;
		
 		try {
 			
			Long start = System.currentTimeMillis();

 			Connection conn = uc.connect();
 			
			String sql = "select profile_json_blob from omics_adm.fact_bulk_rnaseq_profile where sample_set_id =?";
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setLong(1, sampleSetId);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				//System.out.println("Gene: "+rs.getString("gene_id"));
				//System.out.println("SampleSetId: "+rs.getString("sample_set_id"));
				//System.out.println("ExtGeneName: "+rs.getString("ext_gene_name"));
				//System.out.println("Description: "+rs.getString("gene_description"));
				arr = rs.getBytes("profile_json_blob");
				//String base64Encoded = DatatypeConverter.printBase64Binary(arr);
				//System.out.println("\nA Life Encoded...Base64...\n"+base64Encoded);

				ExpressionProfileLite profile = mapper.readValue(arr, ExpressionProfileLite.class);
				profiles.add(profile);
				//System.out.println("Have profile object, norm arraylist size: "+profile.getNormCounts().size());
				
				//String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(profile);
				//System.out.println("\nProfile JSON\n"+json);
			}
			
			rs.close();
			ps.close();
			
			sql = "select a.sample_code, a.name from omics_adm.dim_sample a, omics_adm.sample_set2sample b " +
					"where b.sample_set_id = ? and a.sample_id = b.sample_id " +
					"order by b.sample_ordinal asc";
			
			ps = conn.prepareStatement(sql);
			ps.setLong(1, sampleSetId);
			rs = ps.executeQuery();
			ArrayList <Sample> samples = new ArrayList<Sample>();
			
			while (rs.next()) {
				samples.add(new Sample(
				rs.getString("sample_code"),
				rs.getString("name")));
			}

			System.out.println("Samples Size="+samples.size());
			
			rs.close();
			ps.close();
			conn.close();
			
			Long et = System.currentTimeMillis() - start;
			
			String tab = this.constructTableString(profiles, samples);
			
			PrintWriter pw = new PrintWriter(new FileWriter("C:\\Users\\braistedjc\\Desktop\\Analysis\\SCTL_Omics_Warehouse\\data\\to_load\\ISB003_Test_TabDelim_Output.txt"));
			pw.print(tab);
			pw.flush();
			pw.close();
			System.out.println("Profile set size:"+profiles.size());
			System.out.println("ET = "+ et/1000.0);
			
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String constructTableString(ArrayList <ExpressionProfileLite> profiles, ArrayList <Sample> samples) {

		
		//System.out.println(Runtime.getRuntime().freeMemory());

		String tab = "\t\t";
		for(Sample sample : samples) {
			tab += sample.getSampleCode() + "\t";
		}
		tab = tab.substring(0, tab.length()-2) +"\nGene_Name\tDescription\t"; 
		for(Sample sample : samples) {
			tab += sample.getSampleName() + "\t";
		}
		tab = tab.substring(0, tab.length()-2) + "\n";
		StringBuffer buffer = new StringBuffer(tab);
		for(ExpressionProfileLite profile : profiles) {
			buffer.append(profile.getTabDelimNormalizedDataTableString());
		}
		
		tab = buffer.toString();
		//System.out.println(Runtime.getRuntime().freeMemory());
		
		return tab;
		
	}
	
	public static void main(String [] args) {
		OmicsExpressionLoader loader = new OmicsExpressionLoader();
//		loader.validateEntitiesAndGetCodes("P0001", "", "");
		
		loader.constructEntities(
				"C:\\Users\\braistedjc\\Desktop\\Analysis\\SCTL_Omics_Warehouse\\data\\to_load\\ISB003_82-93.raw.counts.txt", 
				"C:\\Users\\braistedjc\\eclipse-workspace\\Omics_Warehouse_Play\\conf\\application.conf", false);
		
		loader.constructEntities(
				"C:\\Users\\braistedjc\\Desktop\\Analysis\\SCTL_Omics_Warehouse\\data\\to_load\\ISB003_82-93.normalized.counts.txt", 
				"C:\\Users\\braistedjc\\eclipse-workspace\\Omics_Warehouse_Play\\conf\\application.conf", true);
		

		
		
		loader.loadProfilesTable("C:\\Users\\braistedjc\\eclipse-workspace\\Omics_Warehouse_Play\\conf\\application.conf", 2);
	
		//loader.testQuery("NODAL", "C:\\Users\\braistedjc\\eclipse-workspace\\Omics_Warehouse_Play\\conf\\application.conf");
		
		//loader.testQueryAllProfiles(21L, "C:\\Users\\braistedjc\\eclipse-workspace\\Omics_Warehouse_Play\\conf\\application.conf");
	}
}
