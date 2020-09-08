package gov.nih.ncats.omics.warehouse.graphql.resolvers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.coxautodev.graphql.tools.GraphQLQueryResolver;

import gov.nih.ncats.omics.warehouse.model.Treatment;
import gov.nih.ncats.omics.warehouse.service.OmicsService;

/**
 * GraphQL resolver for Treatment queries.
 * 
 * @author braistedjc
 *
 */
@Component
public class TreatmentQueryResolver implements GraphQLQueryResolver {

	@Autowired
	OmicsService service;
	
	public List<Treatment> treatmentsBySampleId(Long sampleId) {
		return service.getTreatmentBySampleId(sampleId);
	}
}
