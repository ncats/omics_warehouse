package gov.nih.ncats.omics.warehouse.graphql.resolvers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.coxautodev.graphql.tools.GraphQLQueryResolver;

import gov.nih.ncats.omics.warehouse.model.GeneSet;
import gov.nih.ncats.omics.warehouse.service.OmicsService;

/**
 * Graphql resolver to support GeneSet queries.
 * @author braistedjc
 *
 */
@Component
public class GeneSetQueryResolver implements GraphQLQueryResolver {
	
	@Autowired
	private OmicsService service;
	
	public List<GeneSet> genesets() {
        return service.findAllGeneSets();       
    }

	public GeneSet geneset(final Long geneSetId) {
		return service.findGeneSetByGeneSetId(geneSetId);
    }
		
	public List<GeneSet> genesetsBySampleSetId(Long sampleSetId) {
		return service.findGeneSetsBySampleSetId(sampleSetId);
	}

	public List<GeneSet> genesetsByExptId(Long exptId) {
		return service.findGeneSetsByExptId(exptId);
	}

	public List<GeneSet> genesetsByProjectId(Long projectId) {
		return service.findGeneSetsByProjectId(projectId);
	}
	
}
