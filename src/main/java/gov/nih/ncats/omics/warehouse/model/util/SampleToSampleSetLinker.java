package gov.nih.ncats.omics.warehouse.model.util;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "omics_adm.sample_set2sample")
public class SampleToSampleSetLinker {
	
	@Id
	@Column(name = "sample_set_id")
	private Long sampleSetId;
	
	@Column(name = "sample_id")
	Long sampleId;
	
	@Column(name = "sample_ordinal")
	Integer ordinal;

	public SampleToSampleSetLinker() {		
	
	}
	
	public SampleToSampleSetLinker(Long sampleSetId, Long sampleId, Integer ordinal) {		
		this.sampleSetId = sampleSetId;
		this.sampleId = sampleId;
		this.ordinal = ordinal;
	}
	
	public Long getSampleSetId() {
		return sampleSetId;
	}

	public void setSampleSetId(Long sampleSetId) {
		this.sampleSetId = sampleSetId;
	}

	public Long getSampleId() {
		return sampleId;
	}

	public void setSampleId(Long sampleId) {
		this.sampleId = sampleId;
	}

	public Integer getOrdinal() {
		return ordinal;
	}

	public void setOrdinal(Integer ordinal) {
		this.ordinal = ordinal;
	}
	
	
}
