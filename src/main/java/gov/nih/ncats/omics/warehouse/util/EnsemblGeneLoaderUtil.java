package gov.nih.ncats.omics.warehouse.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Hashtable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import gov.nih.ncats.omics.warehouse.model.Gene;

public class EnsemblGeneLoaderUtil {

	private ArrayList <Gene> mainAnnList;
	private String ensemblVers;
	private Hashtable <String, Gene> annHash;
	
	/**
	 * 
	 */
	public EnsemblGeneLoaderUtil() {
		mainAnnList = new ArrayList <Gene> ();
		annHash = new Hashtable <String, Gene>();
	}

	public void collectEnsemblGeneAnnotationSet(String dir) {
		File d = new File(dir);
		ArrayList <Gene> annList = new ArrayList <Gene>();
		
		long time = System.currentTimeMillis();
		
		if(d.isDirectory()) {
			File [] files = d.listFiles();
			Arrays.sort(files, Collections.reverseOrder());
			
			for(File file : files) {
				try {
					annList = collectEnsemblGeneAnnotations(file.getCanonicalPath());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				for(Gene ann : annList) {
					if(!this.mainAnnList.contains(ann))
						this.mainAnnList.add(ann);
				}
				System.out.println("File: "+ file.getAbsolutePath());
				System.out.println("Current ann list size: "+mainAnnList.size());
			}
		}
		
		float delta = (System.currentTimeMillis() - time)/6000;
		System.out.println("\nDelta Time (min): " + delta);
		System.out.println("Final ann list size: "+mainAnnList.size());
		
	}
	
	public void collectEnsemblGeneAnnotationSetUsingHash(String dir) {
		File d = new File(dir);
		ArrayList <Gene> annList = new ArrayList <Gene>();
		
		long time = System.currentTimeMillis();
		
		if(d.isDirectory()) {
			File [] files = d.listFiles();
			Arrays.sort(files, Collections.reverseOrder());
			
			for(File file : files) {
				try {
					annList = collectEnsemblGeneAnnotations(file.getCanonicalPath());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
//				for(EnsemblAnnotation ann : annList) {
//					annHash.put((ann.getExternalGeneName()+ann.getGeneStableId()+ann.getDescription()), ann);
//				}
				
				mainAnnList.addAll(annList);
				System.out.println("File: "+ file.getAbsolutePath());
				System.out.println("Current ann list size: "+mainAnnList.size());
			}
		}
		
		this.mainAnnList.addAll(annHash.values());
		
		float delta = (System.currentTimeMillis() - time)/1000/60;
		System.out.println("\nDelta Time (min): " + delta);
		System.out.println("Final ann list size: "+mainAnnList.size());
		
	}
	
	
	
	/**
	 * Loads ensemble annotation csv in the format from R bioMart csv dump with the following columns
	 * row number, ensembl gene id, ensembl gene stable id, hgnc gene symbol, description, transcript type
	 * @param filePath
	 */
	public ArrayList <Gene> collectEnsemblGeneAnnotations(String filePath) {
		
		ArrayList <Gene> annList = new ArrayList <Gene>();
		
		try {
			
			BufferedReader br = new BufferedReader(new FileReader(filePath));
			String line;
			String [] toks;
			int row = 0;
			int shortLineCnt = 0;
			Integer versNum;
			while((line = br.readLine()) != null) {
				if(row > 0) {
					toks = line.split("\t");				
					if(toks.length == 13) {
						versNum = Integer.parseInt(toks[8]);
						if(versNum > 89) { 
							annList.add(new Gene(toks[1], toks[2], toks[3], toks[4], stripSource(toks[5]), toks[6], toks[7],versNum, toks[9], Integer.parseInt(toks[10]), Integer.parseInt(toks[11]), Integer.parseInt(toks[12])));
						} else {
							//for versions 89 and earlier, we don't have a versioned id, so the basic id is the stable id
							annList.add(new Gene(toks[1], toks[1], toks[3], toks[4], stripSource(toks[5]), toks[6], toks[7],versNum, toks[9], Integer.parseInt(toks[10]), Integer.parseInt(toks[11]), Integer.parseInt(toks[12])));
						}	
					} else {
						shortLineCnt++;
						//System.out.println(toks.length);
					}
				}
				row++;
			}
			
			//System.out.println("Short line count = "+shortLineCnt);
			//System.out.println("ann list length = "+annList.size());
			//Collections.sort(annList);
			//annList.sort(Comparator.comparing(EnsemblAnnotation::getGeneStableId));
			//EnsemblAnnotation first = mainAnnList.get(0);
			//System.out.println(first.toString());
			
			//testPrintIDs();
			
			br.close();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return annList;
	}
	
	public void loadGeneTable() {
		
		Connection conn = UtilityConnector.getConnection();
		String sql = "insert into omics_adm.dim_gene (ensembl_id, ensembl_stable_id, ext_gene_name,"
				+ "hgnc_gene_symbol, description, gene_biotype, ensembl_vers, ensembl_vers_num, chr_name, start_pos, end_pos, strand, update_date) values "
				+ "(?,?,?,?,?,?,?,?,?,?,?,?,sysdate)";
		
		int counter = 1;
		try {
			//conn.setAutoCommit(true);
			PreparedStatement ps = conn.prepareStatement(sql);
			for(Gene ann : mainAnnList) {
				ps.setString(1, ann.getEnsemblId());
				ps.setString(2,  ann.getEnsemblStableId());
				ps.setString(3,  ann.getExtGeneName());
				ps.setString(4,  ann.getGeneSymbol());
				ps.setString(5,  ann.getDescription());
				ps.setString(6, ann.getGeneType());
				ps.setString(7, ann.getEnsemblVers());
				ps.setInt(8,  ann.getEnsemblVersNum());
				ps.setString(9,  ann.getChrName());
				ps.setInt(10,  ann.getStartPos());
				ps.setInt(11,ann.getEndPos());
				ps.setInt(12, ann.getStrand());

				ps.addBatch();
				
				if(counter%10000 == 0) {
					ps.executeBatch();
					if(counter%100000 == 0)
						System.out.println(counter);
				}
				counter++;
			}
			ps.executeBatch();
			ps.close();
			conn.commit();
			
		} catch (SQLException e) {
			try {
				//try to roll back on exception
				conn.rollback();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

				
	}
	
	private ArrayList <Gene> makeUniqueGeneList() {
		ArrayList <Gene> newAnnList = new ArrayList <Gene>();
		
		Hashtable <String, Gene> listHash = new  Hashtable <String, Gene>();
		
		for(Gene ann : mainAnnList) {
			listHash.put(ann.getEnsemblStableId(), ann);
		}
		
		newAnnList.addAll(listHash.values());
		
		return newAnnList;
	}
	
	public void loadTranscriptType() {
		CompletionStage stage;
		CompletableFuture fut;
		
	}
		
	private String stripSource(String description) {
		int loc = description.indexOf("[Source"); 
		if(loc > 0) {
			return description.substring(0, loc-1);
		}
		else return description;
	}
	
	public void testPrintIDs() {
		int index = 0;
		int dupCnt = 0;
		for(int i = 0; i < mainAnnList.size(); i++) {
			if(index > 0) {
				if(mainAnnList.get(index).getGeneId().equals(mainAnnList.get(index-1).getGeneId())) {
					System.out.println("duplicate id " + mainAnnList.get(index).getGeneId());
					dupCnt++;
				}
			}
			index++;
		}
		System.out.println("dup count "+dupCnt);
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		EnsemblGeneLoaderUtil u = new EnsemblGeneLoaderUtil();
		u.collectEnsemblGeneAnnotationSetUsingHash("C:\\Users\\braistedjc\\Desktop\\Analysis\\SCTL_Omics_Warehouse\\data\\ensembl");
		u.loadGeneTable();
		//u.loadTranscriptType();
		//u.loadGeneTable();
	}

}
