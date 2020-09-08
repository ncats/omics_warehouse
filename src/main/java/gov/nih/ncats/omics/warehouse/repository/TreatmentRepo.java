package gov.nih.ncats.omics.warehouse.repository;

import java.util.ArrayList;

import org.springframework.data.repository.CrudRepository;

import gov.nih.ncats.omics.warehouse.model.Treatment;

public interface TreatmentRepo extends CrudRepository <Treatment, Long> {
	public ArrayList<Treatment> findTreatmentsBySampleId(Long sampleId);
}
