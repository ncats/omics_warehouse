package gov.nih.ncats.omics.warehouse.model;

import java.util.ArrayList;
import java.util.Collections;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * This class holds basic gene annotations, a reference gene id and arrays for raw, normalized and scaled counts.
 * The sampleSetId is a reference to a collection of samples, including sample order that corresponds to the values in the data lists.
 * 
 * @author braistedjc
 *
 */
@Entity
@Table(name = "omics_adm.fact_sc_rnaseq_profile")
public class SCExpressionProfileLite {

	@Id
	@Column(name="gene_id")
	private Long geneId;
	
	@Column(name="sample_id")
	private Long sampleId;
	
	@Column(name="ensembl_id")
	private String ensemblId;
	
	@Column(name="ext_gene_name")
	private String extGeneName;
	
	@Column(name="gene_description")
	private String geneDescription;
	
	private ArrayList <Integer> rawCounts;
	private ArrayList <Double> normCounts;
	private ArrayList <Double> scaledNormCounts;

	private Boolean haveGeneAnnot;
	
	public SCExpressionProfileLite() {
		this.rawCounts = new ArrayList<Integer>();
		this.normCounts = new ArrayList<Double>();
		this.scaledNormCounts = new ArrayList<Double>();
		haveGeneAnnot = false;	
	}
	
	public void addNormalizedExpressionValue(Double val) {
		normCounts.add(val);
	}
	
	public void addRawExpressionValue(Integer val) {
		rawCounts.add(val);
	}
	
	public Long getGeneId() {
		return geneId;
	}
	
	public void setGeneId(Long geneId) {
		this.geneId = geneId;
	}
	public Long getSampleSetId() {
		return sampleId;
	}
	public void setSampleSetId(Long sampleSetId) {
		this.sampleId = sampleSetId;
	}
	public ArrayList<Integer> getRawCounts() {
		return rawCounts;
	}
	public void setRawCounts(ArrayList<Integer> rawCountsList) {
		this.rawCounts = rawCountsList;
	}
	public ArrayList<Double> getNormCounts() {
		return normCounts;
	}
	public void setNormCounts(ArrayList<Double> normCountsList) {
		this.normCounts = normCountsList;
	}

	public String getExtGeneName() {
		return extGeneName;
	}

	public void setExtGeneName(String extGeneName) {
		this.extGeneName = extGeneName;
	}

	public String getGeneDescription() {
		return geneDescription;
	}

	public void setGeneDescription(String geneDescription) {
		this.geneDescription = geneDescription;
	}

	public Integer getRawCounts(int sampleIndex) {
		return rawCounts.get(sampleIndex);
	}
	
	public Double getNormCounts(int sampleIndex) {
		return normCounts.get(sampleIndex);
	}
	
	public String getEnsemblId() {
		return ensemblId;
	}

	public void setEnsemblId(String ensemblId) {
		this.ensemblId = ensemblId;
	}

	@JsonIgnore
	public String getTabDelimNormalizedDataTableString() {
		String line = this.extGeneName+"\t"+this.geneDescription+"\t";
		for(Double val : normCounts) {
			line += val +"\t";
		}
		return line.trim() + "\n";
	}
	
	@JsonIgnore
	public String getTabDelimRawDataTableString() {
		String line = this.extGeneName+"\t"+this.geneDescription+"\t";
		for(Integer val : rawCounts) {
			line += val +"\t";
		}
		return line.trim() + "\n";
	}
	
	@JsonIgnore
	public String getTabDelimScaledDataTableString() {
		String line = this.extGeneName+"\t"+this.geneDescription+"\t";
		for(Double val : scaledNormCounts) {
			line += val +"\t";
		}
		return line.trim() + "\n";
	}
	
	public void createProfileScaledValues() {
		Double min = Collections.min(normCounts);
		Double max = Collections.max(normCounts);
		for(Double counts : normCounts) {
			if(max > min)
				this.scaledNormCounts.add((counts-min)/(max-min));
			else
				this.scaledNormCounts.add(0.0);
		}
	}

	public ArrayList<Double> getScaledNormCounts() {
		return scaledNormCounts;
	}

	public void setScaledNormCounts(ArrayList<Double> scaledNormCounts) {
		this.scaledNormCounts = scaledNormCounts;
	}

	public Long getSampleId() {
		return sampleId;
	}

	public void setSampleId(Long sampleId) {
		this.sampleId = sampleId;
	}

	public Boolean getHaveGeneAnnot() {
		return haveGeneAnnot;
	}

	public void setHaveGeneAnnot(Boolean haveGeneAnnot) {
		this.haveGeneAnnot = haveGeneAnnot;
	}	
	
}
