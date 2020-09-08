package gov.nih.ncats.omics.warehouse.graphql.resolvers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.coxautodev.graphql.tools.GraphQLQueryResolver;

import gov.nih.ncats.omics.warehouse.model.Experiment;
import gov.nih.ncats.omics.warehouse.model.Gene;
import gov.nih.ncats.omics.warehouse.service.OmicsService;

/**
 * Graphql resolver to support Gene queries.
 * 
 * @author braistedjc
 *
 */
@Component
public class GeneQueryResolver implements GraphQLQueryResolver {
	
	@Autowired
	private OmicsService service;
	
	public Gene gene(final Long geneId) {
		return service.findGeneByGeneId(geneId);
    }
	
	public Gene geneByExtGeneNameAndVers(final String extGeneName, final Integer vers) {
		return service.findGeneByGeneExtGeneNameAndEnsemblVersNum(extGeneName, vers);
    }
	
}
