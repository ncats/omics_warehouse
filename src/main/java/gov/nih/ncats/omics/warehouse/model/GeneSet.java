package gov.nih.ncats.omics.warehouse.model;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * This class encapsulates information about a collection of genes.
 * These are usually genes collected using analytical techniques such as statistical testing or clustering
 * or these may be marker genes of interest or represent a pathway or biological role.
 * 
 * @author braistedjc
 *
 */
@Entity
@Table(name = "omics_adm.gene_set")
public class GeneSet {
	
	@Id
	@Column(name = "gene_set_id")
	private Long geneSetId;
	
	@Column(name = "project_id")
	private Long projectId;
	
	@Column(name = "expt_id")
	private Long exptId;
	
	@Column(name = "sample_set_id")
	@JoinColumn
	private Long sampleSetId;
	
	@Column(name = "gene_set_name")
	private String geneSetName;
	
	@Column(name = "analysis_notes")
	private String analysisNotes;
	
	@Column(name = "biology_notes")
	private String biologyNotes;

	@OneToMany
	@JoinTable(name = "omics_adm.gene_set2gene",
			joinColumns = @JoinColumn(name = "gene_set_id"),
			inverseJoinColumns = @JoinColumn(name = "gene_id"))
	List <Gene> genes;
	
	@Transient
	List <ExpressionProfileLite> profiles;
	
	public Long getGeneSetId() {
		return geneSetId;
	}

	public void setGeneSetId(Long geneSetId) {
		this.geneSetId = geneSetId;
	}

	public Long getProjectId() {
		return projectId;
	}

	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}

	public Long getExptId() {
		return exptId;
	}

	public void setExptId(Long exptId) {
		this.exptId = exptId;
	}

	public Long getSampleSetId() {
		return sampleSetId;
	}

	public void setSampleSetId(Long sampleSetId) {
		this.sampleSetId = sampleSetId;
	}

	public String getDiscoveryName() {
		return geneSetName;
	}

	public void setDiscoveryName(String geneSetName) {
		this.geneSetName = geneSetName;
	}

	public String getAnalysisNotes() {
		return analysisNotes;
	}

	public void setAnalysisNotes(String analysisNotes) {
		this.analysisNotes = analysisNotes;
	}

	public String getBiologyNotes() {
		return biologyNotes;
	}

	public void setBiologyNotes(String biologyNotes) {
		this.biologyNotes = biologyNotes;
	}

	public List<Gene> getGenes() {
		return genes;
	}

	public void setGenes(List<Gene> genes) {
		this.genes = genes;
	}

	public List<ExpressionProfileLite> getProfiles() {
		return profiles;
	}

	public void setProfiles(List<ExpressionProfileLite> profiles) {
		this.profiles = profiles;
	}
	
	
}
