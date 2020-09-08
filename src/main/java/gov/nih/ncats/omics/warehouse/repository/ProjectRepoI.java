package gov.nih.ncats.omics.warehouse.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import gov.nih.ncats.omics.warehouse.model.Project;


public interface ProjectRepoI extends CrudRepository <Project, Long> {
	
	public Project findByProjectCode(String code);
	
}
