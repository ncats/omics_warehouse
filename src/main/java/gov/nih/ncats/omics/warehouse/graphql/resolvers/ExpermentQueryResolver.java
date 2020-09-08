package gov.nih.ncats.omics.warehouse.graphql.resolvers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.coxautodev.graphql.tools.GraphQLQueryResolver;

import gov.nih.ncats.omics.warehouse.model.Experiment;
import gov.nih.ncats.omics.warehouse.service.OmicsService;

/**
 * Graphql resolver to support Experiment queries. 
 * 
 * @author braistedjc
 *
 */
@Component
public class ExpermentQueryResolver implements GraphQLQueryResolver {
	
	@Autowired
	private OmicsService service;
	
	public Experiment experiment(final Long exptId) {
		return service.findExperimentByExptId(exptId);
    }

	public Experiment experimentByExptCode(final String exptCode) {
		return service.findExperimentByExptCode(exptCode);
    }

	public List<Experiment> experiments() {
        return service.findAllExperiments();       
    }
	
}
