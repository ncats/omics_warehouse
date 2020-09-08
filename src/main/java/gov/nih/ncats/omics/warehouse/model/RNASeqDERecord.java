package gov.nih.ncats.omics.warehouse.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 * RNASeqDERecord is a container that holds information from differential expression analysis from DESeq2
 * 
 * @author braistedjc
 *
 */
@Entity
@Table(name="ft_bulk_rna_de")
public class RNASeqDERecord {

	@Id
	@Column(name="data_id")
	private Long deRecordId;
	
	@Column(name="analysis_id")
	private Long analysisId;
	
	@Column(name="gene_id")
	private Long geneId;
	
	@Column(name="lfc")
	private Float LFC;
	
	@Column(name="p_value")
	private Double pValue;
	
	@Column(name="fdr")
	private Double FDR;
	
	@Column(name="base_mean_a")
	private Long baseMeanA;
	
	@Column(name="base_mean_b")
	private Long baseMeanB;
	
	@OneToOne
	private Gene gene;
	
	public Long getDeRecordId() {
		return deRecordId;
	}
	public void setDeRecordId(Long deRecordId) {
		this.deRecordId = deRecordId;
	}
	public Long getAnalysisId() {
		return analysisId;
	}
	public void setAnalysisId(Long analysisId) {
		this.analysisId = analysisId;
	}
	public Long getGeneId() {
		return geneId;
	}
	public void setGeneId(Long geneId) {
		this.geneId = geneId;
	}
	public Float getLFC() {
		return LFC;
	}
	public void setLFC(Float lFC) {
		LFC = lFC;
	}
	public Double getpValue() {
		return pValue;
	}
	public void setpValue(Double pValue) {
		this.pValue = pValue;
	}
	public Double getFDR() {
		return FDR;
	}
	public void setFDR(Double fDR) {
		FDR = fDR;
	}
	public Long getBaseMeanA() {
		return baseMeanA;
	}
	public void setBaseMeanA(Long baseMeanA) {
		this.baseMeanA = baseMeanA;
	}
	public Long getBaseMeanB() {
		return baseMeanB;
	}
	public void setBaseMeanB(Long baseMeanB) {
		this.baseMeanB = baseMeanB;
	}
	public Gene getGene() {
		return gene;
	}
	public void setGene(Gene gene) {
		this.gene = gene;
	}
	
	
}
