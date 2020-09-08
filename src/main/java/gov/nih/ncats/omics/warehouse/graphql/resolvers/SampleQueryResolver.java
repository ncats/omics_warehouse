package gov.nih.ncats.omics.warehouse.graphql.resolvers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.coxautodev.graphql.tools.GraphQLQueryResolver;

import gov.nih.ncats.omics.warehouse.model.Sample;
import gov.nih.ncats.omics.warehouse.service.OmicsService;

/**
 * Graphql resolver for Sample queries
 * @author braistedjc
 *
 */
@Component
public class SampleQueryResolver implements GraphQLQueryResolver {

	@Autowired
	OmicsService service;
	
	public Sample sample(Long sampleId) {
		return service.getSampleBySampleId(sampleId);
	}
}
