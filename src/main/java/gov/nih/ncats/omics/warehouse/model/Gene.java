package gov.nih.ncats.omics.warehouse.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * This is a gene annotation container class. the geneId field is a unique reference to a specific annotation, unique on (ensemblId, ensemblVers)
 * 
 * @author braistedjc
 *
 */
@Entity
@Table(name="omics_adm.dim_gene")
public class Gene implements Comparable <Gene> {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)	
	@Column(name = "gene_id")
	private Long geneId;
	
	@Column(name = "ensembl_id")
	private String ensemblId;
	
	@Column(name = "ensembl_stable_id")
	private String ensemblStableId;
	
	@Column(name = "ext_gene_name")
	private String extGeneName;

	@Column(name = "hgnc_gene_symbol")
	private String geneSymbol;
	
	@Column(name = "description")
	private String description;
	
	@Column(name = "gene_biotype")
	private String geneType;
	
	@Column(name = "ensembl_vers")
	private String ensemblVers;
	
	@Column(name = "ensembl_vers_num")
	private Integer ensemblVersNum;
	
	@Column(name = "chr_name")
	private String chrName;
	
	@Column(name = "start_pos")
	private Integer startPos;
	
	@Column(name = "end_pos")
	private Integer endPos;
	
	@Column(name = "strand")
	private Integer strand;
	
	public Gene() {
		
	}
	
	public Gene(String geneId, String geneStableId, String externalGeneName, String geneSymbol, String description,
			String geneType, String ensemblVers, Integer ensemblVersNum, String chrName, Integer startPos, Integer endPos, Integer strand) {
		super();
		this.ensemblId = geneId;
		this.ensemblStableId = geneStableId;
		this.extGeneName = externalGeneName;
		this.geneSymbol = geneSymbol;
		this.description = description;
		this.geneType = geneType;
		this.ensemblVers = ensemblVers;
		this.ensemblVersNum = ensemblVersNum;
		this.chrName = chrName;
		this.startPos = startPos;
		this.endPos = endPos;
		this.strand = strand;		
	}
	
	@Override
	public int compareTo(Gene otherAnn) {
		//System.out.println("compareto");
		return this.ensemblStableId.compareTo(otherAnn.getEnsemblStableId());
	}
	
	@Override
	public int hashCode() {
		return this.extGeneName.hashCode();
	}

	/**
	 * This implementation is intended to support a collections contains() method
	 * that will look for an external gene name. 
	 * The goal would be to make a unique list of the most recent annotations using 
	 * the SCTL's recorded gene name.
	 * 
	 *  Gene name in this case is either gene symbol or an alternative name from ensembl if the gene is less characterized.
	 */
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Gene) {
			Gene otherAnn = (Gene)obj;

			if(this.extGeneName != null && otherAnn.extGeneName != null) {
				return (this.extGeneName.contentEquals(otherAnn.extGeneName)
						&& this.ensemblStableId.contentEquals(otherAnn.ensemblStableId));
			} else { 
				return false;
			}
		}
		return false;
	}
	
	
	
	public Long getGeneId() {
		return geneId;
	}

	public void setGeneId(Long geneId) {
		this.geneId = geneId;
	}

	public String getEnsemblId() {
		return ensemblId;
	}

	public void setEnsemblId(String geneId) {
		this.ensemblId = geneId;
	}

	public String getEnsemblStableId() {
		return ensemblStableId;
	}

	public void setEnsemblStableId(String geneStableId) {
		this.ensemblStableId = geneStableId;
	}

	public String getGeneSymbol() {
		return geneSymbol;
	}

	public void setGeneSymbol(String geneSymbol) {
		this.geneSymbol = geneSymbol;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getGeneType() {
		return geneType;
	}

	public void setGeneType(String transcriptType) {
		this.geneType = transcriptType;
	}
	
	public String getEnsemblVers() {
		return ensemblVers;
	}

	public void setEnsemblVers(String ensemblVers) {
		this.ensemblVers = ensemblVers;
	}

	public String getExtGeneName() {
		return extGeneName;
	}

	public void setExtGeneName(String extGeneName) {
		this.extGeneName = extGeneName;
	}

	public Integer getEnsemblVersNum() {
		return ensemblVersNum;
	}

	public void setEnsemblVersNum(Integer ensemblVersNum) {
		this.ensemblVersNum = ensemblVersNum;
	}
	
	public String getChrName() {
		return chrName;
	}

	public void setChrName(String chrName) {
		this.chrName = chrName;
	}

	public Integer getStartPos() {
		return startPos;
	}

	public void setStartPos(Integer startPos) {
		this.startPos = startPos;
	}

	public Integer getEndPos() {
		return endPos;
	}

	public void setEndPos(Integer endPos) {
		this.endPos = endPos;
	}

	public Integer getStrand() {		
		return strand;
	}

	public void setStrand(Integer strand) {
		this.strand = strand;
	}

	public String toString() {
		String s = "Ensembl Gene ID: "+this.ensemblId+"\nEnsembl Stable ID: "+this.ensemblStableId+"\nExt. Gene Name: "+this.extGeneName+"\nGene Symbol: "+
				this.geneSymbol+"\nDescription: "+this.description+"\nTranscript Biotype:"+this.geneType+"\n";
		return s;
	}
	
	
		
}
