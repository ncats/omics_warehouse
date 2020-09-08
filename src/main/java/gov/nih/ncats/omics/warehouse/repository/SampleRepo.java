package gov.nih.ncats.omics.warehouse.repository;

import org.springframework.data.repository.CrudRepository;

import gov.nih.ncats.omics.warehouse.model.Sample;

public interface SampleRepo extends CrudRepository <Sample, Long>{
	public Sample getSampleBySampleId(Long sampleId);
	public Sample getSampleBySampleCode(String sampleCode);
	public Sample getSampleBySampleName(String sampleName);	
}
