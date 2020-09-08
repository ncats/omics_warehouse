package gov.nih.ncats.omics.warehouse.repository.util;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import gov.nih.ncats.omics.warehouse.model.util.SampleToSampleSetLinker;

public interface SampleToSampleSeetLinkerRepo extends CrudRepository <SampleToSampleSetLinker, Long> {
	public List <SampleToSampleSetLinker> findSampleToSampleSetLinkersBySampleId(Long sampleId);
}
