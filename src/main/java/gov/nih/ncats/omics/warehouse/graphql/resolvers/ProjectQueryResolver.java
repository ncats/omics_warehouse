package gov.nih.ncats.omics.warehouse.graphql.resolvers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.coxautodev.graphql.tools.GraphQLQueryResolver;

import gov.nih.ncats.omics.warehouse.model.Experiment;
import gov.nih.ncats.omics.warehouse.model.Project;
import gov.nih.ncats.omics.warehouse.service.OmicsService;

/**
 * Graphql query resolver to support Project queries.
 * 
 * @author braistedjc
 *
 */
@Component
public class ProjectQueryResolver implements GraphQLQueryResolver {

	@Autowired
	private OmicsService service;

	public Project project(final Long projectId) {
		return service.getProjectByProjectId(projectId);
    }
	
	public List<Project> projects() {
        return service.getProjects();       
    }
	
}
