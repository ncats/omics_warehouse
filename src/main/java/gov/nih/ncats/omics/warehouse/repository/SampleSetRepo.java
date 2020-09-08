package gov.nih.ncats.omics.warehouse.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import gov.nih.ncats.omics.warehouse.model.Sample;
import gov.nih.ncats.omics.warehouse.model.SampleSet;

public interface SampleSetRepo extends CrudRepository <SampleSet, Long> {
	public SampleSet getSampleSetBySampleSetId(Long sampleSetId);
	public List<SampleSet> getSampleSetsByProjectId(Long projectId);
}
