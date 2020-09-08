package gov.nih.ncats.omics.warehouse.util;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.CharStreams;

import gov.nih.ncats.omics.warehouse.model.SCExpressionProfileLite;
import gov.nih.ncats.omics.warehouse.model.SCRNASeqProfile;
import gov.nih.ncats.omics.warehouse.model.SampleCellRecord;
import gov.nih.ncats.omics.warehouse.model.util.SingleCellLoadData;

public class SingleCellDataLoader {
	
	//metadata 
	private ArrayList <SingleCellLoadData> dataSets;
	private Long sampleId;
	private Long sampleSetId;
	
	private ArrayList<SCRNASeqProfile> profiles;
	
	public SingleCellDataLoader() {
		dataSets = new ArrayList <SingleCellLoadData>();
		profiles = new ArrayList<SCRNASeqProfile>();
	}
	
	public SingleCellDataLoader(ArrayList <SingleCellLoadData> dataSets) {
		this.dataSets = dataSets;
		profiles = new ArrayList<SCRNASeqProfile>();
	}
	
	public void loadSets() {
		for(SingleCellLoadData data : dataSets) {
			
		}
	}
	
	private void collectData(SingleCellLoadData data) {
		String dir = data.getFilePath();
		String fileName = data.getFileName();
		String sampleCode = data.getSampleCode();
		String sampleSetCode = data.getSampleSetCode();
		
		if(validateEntitiesAndGetCodes(sampleSetCode, sampleCode).contentEquals("OK")) {
			try {
				BufferedReader br = new BufferedReader(new FileReader(dir+fileName));
				String line;
				int dataCnt = 0;
				while((line = br.readLine()) != null) {
					if(dataCnt == 0) {
						captureSampleCellData(data, line);						
					} else {
						captureProfile(data, line);
					}
					
					if(dataCnt % 1000 == 0) {
						System.out.println("Data Count = "+ dataCnt);
					}
					
					dataCnt++;
				}

				br.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			// we have the main data
			// we want to pull gene information for the profiles that we save...
			collectGeneData(data);
			
			System.out.println("Number of cells:"+data.getSampleCellRecords().size());
			System.out.println("Profiles:"+data.getProfiles().size());
			
		}
	}
	
	public void captureSampleCellData(SingleCellLoadData data, String cellData) {
		String [] cells = cellData.split(",");
		for(int i = 1; i < cells.length; i++) {
			data.addCellRecord(this.sampleId, cells[i]);
		}
	}
	
	public void captureProfile(SingleCellLoadData data, String profileData) {
		String [] vals = profileData.split(",");
		SCExpressionProfileLite profile = new SCExpressionProfileLite();
		profile.setExtGeneName(vals[0]);
		for(int i = 1; i < vals.length; i++) {
			profile.addRawExpressionValue(Integer.parseInt(vals[i]));
		}
		data.addProfile(profile);
	}
	
	public String validateEntitiesAndGetCodes(String sampleSetCode, String sampleCode) {
		String status = "OK";
		Connection conn = UtilityConnector.getUtilConnection();

		String sampleSetSql = "select sample_set_id from omics_adm.dim_sample_set where sample_set_code = ?";
		String sampleSql = "select sample_id from omics_adm.dim_sample where sample_code = ?";

		
		try {
			
			PreparedStatement ps = conn.prepareStatement(sampleSql);
			
			ps.setString(1, sampleCode);
			ResultSet rs = ps.executeQuery();
			if(rs.next()) {
				// have the expt
				this.sampleId = rs.getLong("sample_id");
			} else {
				status = "Sample "+sampleCode + " has not been registered.\n";
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
	
	public void loadData(SingleCellLoadData data) {
		loadCells(data);
		loadProfiles(data);
	}
	
	public void loadCells(SingleCellLoadData data) {
		
		String cellSQL = "insert into omics_adm.sample_cell (sample_id, cell_barcode) values ("+this.sampleId+",?)";
		Long sampleId = this.sampleId;
		try {
			
			Connection conn = UtilityConnector.getUtilConnection();

			PreparedStatement ps = conn.prepareStatement(cellSQL);
			for(SampleCellRecord record : data.getSampleCellRecords()) {
				ps.setString(1, record.getCellBarcode());
				ps.addBatch();
			}
			ps.executeBatch();
			conn.commit();
			ps.close();
		
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	public void loadProfiles(SingleCellLoadData data) {
		String cellSQL = "insert into omics_adm.fact_sc_rnaseq_profile (gene_id, sample_id, profile_json_blob, ext_gene_name, gene_description, ensembl_id, data_type, profile_clob) "
				+ "values (?,?,?,?,?,?,?,?)";
		SCExpressionProfileLite p = null;
		
		try {
			
			Connection conn = UtilityConnector.getUtilConnection();
		//	conn.setAutoCommit(true);
			
			Clob clob = conn.createClob();
			
			ObjectMapper mapper = new ObjectMapper();
			PreparedStatement ps = conn.prepareStatement(cellSQL);
			int cnt = 1;
			
			for(SCExpressionProfileLite profile : data.getProfiles()) {
				p=profile;
				ps.setLong(2, this.sampleId);
				ps.setBytes(3, (profile.getTabDelimRawDataTableString().getBytes()));
				//ps.setNull(3, java.sql.Types.NULL);
				ps.setString(7, "sc_chromium_rnaseq");
				
				clob.setString(1, "");
				clob.setString(1, profile.getTabDelimRawDataTableString());
				ps.setString(8, profile.getTabDelimRawDataTableString());
				
				
				if(profile.getHaveGeneAnnot()) {
					ps.setLong(1, profile.getGeneId());
					ps.setString(4, profile.getExtGeneName());
					ps.setString(5,  profile.getGeneDescription());
					ps.setString(6, profile.getEnsemblId());					
				} else {
					ps.setLong(1, -1l);
					ps.setNull(4, java.sql.Types.NULL);
					ps.setNull(5, java.sql.Types.NULL);
					ps.setNull(6, java.sql.Types.NULL);					
				}
							
				ps.addBatch();
			
				if(cnt % 1000 == 0) {
					ps.executeBatch();
					ps.clearBatch();
					conn.commit();
				}
			
				cnt++;
			}
			ps.executeBatch();

			conn.commit();
			
			conn.close();
			ps.close();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println("gene_id"+p.getGeneId()+"  have annotation??? +"+p.getHaveGeneAnnot());
			
			e.printStackTrace();
		} 
//		catch (JsonProcessingException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}
	
	public void collectGeneData(SingleCellLoadData data) {
		
		
		String geneSQL = "select gene_id, description, ensembl_id, ensembl_vers_num from omics_adm.dim_gene where ext_gene_name = ? order by ensembl_vers_num desc";
		
		try {
			
			Connection conn = UtilityConnector.getUtilConnection();
			
			PreparedStatement stmt = conn.prepareStatement(geneSQL);
			ResultSet rs = null;
			
			int noAnnotGeneCnt = 0;
			
			for(SCExpressionProfileLite profile: data.getProfiles()) {
				stmt.setString(1, profile.getExtGeneName());
				rs = stmt.executeQuery();
				
				if(rs.next()) {
					profile.setGeneId(rs.getLong("gene_id"));
					profile.setGeneDescription(rs.getString("description"));
					profile.setEnsemblId(rs.getString("ensembl_id"));
					profile.setHaveGeneAnnot(true);
				} else {
					System.out.println("Hey... no gene info for this ext gene name = "+ profile.getExtGeneName());
					profile.setHaveGeneAnnot(false);
					noAnnotGeneCnt++;
				}
				
				rs.close();
			}			

			System.out.println("no annot gene count +"+noAnnotGeneCnt);
			conn.close();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void loadMatrix(String filePath) {
		

		StringBuffer buffer = new StringBuffer();
		
		try {
			BufferedReader br = new BufferedReader(new FileReader(filePath));
			String line;
			int dataCnt = 0;
			while((line = br.readLine()) != null) {
				buffer.append(line+"\n");
			}

			br.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String sql = "insert into omics_adm.fact_sc_rnaseq_profile (gene_id, sample_id, profile_json_blob, ext_gene_name, gene_description, "
				+ "ensembl_id, data_type) values ("
				+ "1398401," 
				+ "198,"
				+ "?,"
				+ "'HMOX1',"
				+ "'heme oxygenase 1',"
				+ "'ENSG00000100292',"
				+ "'sc_rnaseq')";
		
		
		try {

			Connection conn = UtilityConnector.getUtilConnection();
			PreparedStatement ps = conn.prepareStatement(sql);

			ps.setBytes(1, buffer.toString().getBytes());
			ByteArrayInputStream bais = new ByteArrayInputStream(buffer.toString().getBytes());
			ps.setBinaryStream(1, bais);
			
			ps.execute();
			
			conn.commit();
			ps.close();
			conn.close();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void testPull(ArrayList <Long> sampleIds) {
		
		String geneSQL = "select profile_clob from omics_adm.fact_sc_rnaseq_profile where sample_id = ?";
		ArrayList <Double> ets = new ArrayList <Double>();
		StringBuilder buffer;
		
		SCExpressionProfileLite profile;
		ObjectMapper mapper = new ObjectMapper();
		String row = "";
		try {
	
			
			long start;
			Connection conn = UtilityConnector.getBasicConnection();
			
			PreparedStatement stmt = conn.prepareStatement(geneSQL);

			stmt.setFetchSize(100);
			ResultSet rs;
			
			int cnt = 0;
			Clob clob;
			Boolean showString = true;
			ArrayList <Clob> blobs;
			for(Long sampleId : sampleIds) {

				blobs = new ArrayList<Clob>();
				
				buffer = new StringBuilder(1073741824);
				stmt.setLong(1, sampleId);

				System.out.println("Starting "+sampleId);
				start = System.currentTimeMillis();
				rs = stmt.executeQuery();

				while(rs.next()) {
					//buffer.append(rs.getBytes("profile_json_blob"));
				//	clob = rs.getClob("profile_clob");
				//	 buffer.append(clob.getSubString(1, (int)clob.length()));
					buffer.append(rs.getString("profile_clob"));
				//	 blobs.add(rs.getBlob("profile_json_blob"));
				//	buffer.append(new String(blob.getBytes(1l, (int)blob.length())));
				//	profile = mapper.readValue(rs.getBytes("profile_json_blob"), SCExpressionProfileLite.class);  
				//	buffer.append(rs.getString("ext_gene_name")+"\t"+profile.getTabDelimRawDataTableString()+"\n");
					cnt++;
					if(cnt % 10000 == 0) {
						System.out.println("Count ="+cnt);
						if(showString) {
							System.out.println(buffer.subSequence(20, 40).toString());
							showString = false;
						}
					}
				}

				System.out.println("retreival et: "+(System.currentTimeMillis()-start)/1000.0);				

//				for(Blob b : blobs) {
//					buffer.append(String.valueOf(b));
//				}
								
				ets.add((System.currentTimeMillis()-start)/1000.0);				

				System.out.println("Finished "+sampleId+"ET = "+ets.get(ets.size()-1));

			//	System.out.println(buffer.subSequence(20, 40).toString());
				
				rs.close();
				rs = null;
				buffer = null;
				cnt=0;
			}
			
			stmt.close();
			conn.close();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
//		catch (JsonParseException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (JsonMappingException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		int sampleIndex = 0;
		for(Long sampleId: sampleIds) {
			System.out.println(sampleId+"\t"+ets.get(sampleIndex));
			sampleIndex++;
		}
	}

	public void readAndLoadScDataSets(String fileName) {
		
		try {
			BufferedReader br = new BufferedReader(new FileReader(fileName));
			String line;
			String [] data;
			SingleCellLoadData dataSet;
			while((line = br.readLine()) != null) {
				if(line.startsWith("sc_file")) {
					dataSet = new SingleCellLoadData();
					data = line.split("\t");
					dataSet.setSampleSetCode(data[2]);
					dataSet.setSampleCode(data[3]);
					dataSet.setFilePath(data[4]);
					dataSet.setFileName(data[5]);
					
					//maybe we shouldn't accumulate data but rather do a direct load to dump memory overhead
					//add this data set
					//this.dataSets.add(dataSet);
					System.out.println("collect data for sample: "+dataSet.getSampleCode());
					collectData(dataSet);
					System.out.println("load data for sample: "+dataSet.getSampleCode());
					loadData(dataSet);		
					System.out.println("finished load for sample: "+dataSet.getSampleCode());
				}
			}
			br.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//now load..
//		for(SingleCellLoadData dataSet: dataSets) {
//
//		}
	}
	
	public void testPullEachSample() {
		
		Connection conn = UtilityConnector.getBasicConnection();
		
		String sampleIdSql = "select distinct sample_id from omics_adm.fact_sc_rnaseq_profile order by sample_id asc";
		PreparedStatement ps;
		
		try {
			
			ps = conn.prepareStatement(sampleIdSql);
		
			ResultSet rs = ps.executeQuery();
			
			ArrayList <Long> sampleIds = new ArrayList <Long>();
			
			while(rs.next()) {
				sampleIds.add(rs.getLong("sample_id"));
			}
			
			ps.close();
			rs.close();
			conn.close();
			
			testPull(sampleIds);
		
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	public void testPullMatrix() {
		
		String sql = "select profile_json_blob from omics_adm.fact_sc_rnaseq_profile where gene_id = 1398401 and sample_id = 198";

			try {
				PreparedStatement ps;

				Connection conn = UtilityConnector.getBasicConnection();

				long start = System.currentTimeMillis();
				
				ps = conn.prepareStatement(sql);
				ResultSet rs = ps.executeQuery();
				String matrix = "";
				if(rs.next()) {
					Blob blob = rs.getBlob("profile_json_blob");
					InputStream is = blob.getBinaryStream();

					InputStreamReader isr = new InputStreamReader(is);

					System.out.println("Have Blob, et (sec)="+((System.currentTimeMillis()-start)/(1000.0)));

					matrix = CharStreams.toString(isr);

					System.out.println("Have matrix String, et (sec)="+((System.currentTimeMillis()-start)/(1000.0)));
					is.close();

				}
				
				StringReader sr = new StringReader(matrix);
				BufferedReader br = new BufferedReader(sr);
				int cnt = 0;
				while(br.readLine() != null) {
					cnt++;
				}
				System.out.println("Row count = "+cnt);
				System.out.println("char count ="+matrix.toCharArray().length);
			    rs.close();
			    conn.close();
			    
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
			
			
		
	}
	
	public void testPullMatrixThroughBuffer() {
		
		String sql = "select profile_json_blob from omics_adm.fact_sc_rnaseq_profile where gene_id = 1398401 and sample_id = 198";

			try {
				PreparedStatement ps;

				Connection conn = UtilityConnector.getBasicConnection();

				long start = System.currentTimeMillis();
				
				ps = conn.prepareStatement(sql);
				ResultSet rs = ps.executeQuery();
				String matrix = "";
				StringBuilder buffer = new StringBuilder();
				byte [] byteBuffer = new byte[4096];
				int len = 0;
				
				
				if(rs.next()) {
					Blob blob = rs.getBlob("profile_json_blob");
					InputStream is = blob.getBinaryStream();

					InputStreamReader isr = new InputStreamReader(is);

					System.out.println("Have Blob, et (sec)="+((System.currentTimeMillis()-start)/(1000.0)));

					while((len = is.read(byteBuffer, 0, 4096)) != -1) {
						buffer.append(byteBuffer);
					}
														
					matrix = buffer.toString();
					
					//matrix = CharStreams.toString(isr);

					System.out.println("Have matrix String, et (sec)="+((System.currentTimeMillis()-start)/(1000.0)));
					is.close();
					
					String smallSampleString = matrix.substring(20, 100);
					System.out.println("substring= "+smallSampleString);

				}
				
				StringReader sr = new StringReader(matrix);
				BufferedReader br = new BufferedReader(sr);
				int cnt = 0;
				while(br.readLine() != null) {
					cnt++;
				}
				System.out.println("Row count = "+cnt);
				System.out.println("char count ="+matrix.toCharArray().length);
			    rs.close();
			    conn.close();
			    
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
	}
	
	
	
	public static void main(String [] args) {
//		SingleCellLoadData data = new SingleCellLoadData();
//		data.setSampleCode("IS019-1");
//		data.setSampleSetCode("IS019_Set_01");
//		data.setFileName("H9_Collagen2_Day60_gene_symbol.csv");
//		data.setFilePath("Z:\\NGS_related\\Chromium\\IS022\\Countfiles_gene_symbol\\");
					
		SingleCellDataLoader loader = new SingleCellDataLoader();
	//	loader.collectData(data);
////		
	//	loader.loadData(data);
//		loader.testPull();
		
	//	loader.readAndLoadScDataSets("C:\\Users\\braistedjc\\Desktop\\Analysis\\SCTL_Omics_Warehouse\\data\\to_load\\sc_data\\sc_metadata_example.txt");
		
	//	loader.testPullEachSample();
	//	loader.loadMatrix("T:\\JB\\Temp_IS022\\H9_Collagen8_Day60_gene_symbol.csv");
	loader.testPullMatrixThroughBuffer();
	
	}
	
}
