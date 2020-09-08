package gov.nih.ncats.omics.warehouse.graphql.resolvers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.coxautodev.graphql.tools.GraphQLQueryResolver;

import gov.nih.ncats.omics.warehouse.model.Project;
import gov.nih.ncats.omics.warehouse.model.SampleSet;
import gov.nih.ncats.omics.warehouse.service.OmicsService;

/**
 * Graphql query resolver for SampleSet objects.
 * 
 * @author braistedjc
 *
 */
@Component
public class SampleSetQueryResolver implements GraphQLQueryResolver {

	@Autowired
	private OmicsService service;

	public SampleSet sampleset(final Long sampleSetId) {
		return service.getSampleSetBySampleSetId(sampleSetId);
    }
	
	public List<SampleSet> sampleSetsByProjectId(final Long projectId) {
        return service.getSampleSetsByProjectId(projectId);       
    }

	public List<SampleSet> sampleSetsByProjectCode(final String projectCode) {
        return service.getSampleSetsByProjectCode(projectCode);       
    }

	
	
}
