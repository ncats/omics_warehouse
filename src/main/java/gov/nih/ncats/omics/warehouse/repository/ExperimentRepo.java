package gov.nih.ncats.omics.warehouse.repository;

import org.springframework.data.repository.CrudRepository;

import gov.nih.ncats.omics.warehouse.model.Experiment;

public interface ExperimentRepo extends CrudRepository <Experiment, Long>{
	public Experiment findExperimentByExptId(Long eid);
	public Experiment findExperimentByExptCode(String exptCode);
}
