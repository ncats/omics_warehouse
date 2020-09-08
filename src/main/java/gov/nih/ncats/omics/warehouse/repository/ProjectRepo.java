package gov.nih.ncats.omics.warehouse.repository;


import java.util.List;

import org.springframework.stereotype.Repository;

import gov.nih.ncats.omics.warehouse.model.Project;

@Repository
public interface ProjectRepo extends ProjectRepoI {
	public Project findProjectByProjectCode(String projectCode);
	public Project findProjectByProjectId(Long projectId);
}
