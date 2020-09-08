package gov.nih.ncats.omics.warehouse.graphql.resolvers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.coxautodev.graphql.tools.GraphQLQueryResolver;

import gov.nih.ncats.omics.warehouse.model.RNASeqAnalysis;
import gov.nih.ncats.omics.warehouse.service.OmicsService;

/**
 * Graphql resolver to support RNASeqAnalysis objects
 * 
 * @author braistedjc
 *
 */
@Component
public class RNASeqAnalysisResolver implements GraphQLQueryResolver {

	@Autowired
	OmicsService service;
	
	public List<RNASeqAnalysis> rnaseqanalyses() {
		return service.findAllRNASeqAnalyses();
	}
	
	public RNASeqAnalysis rnaseqanalysis(Long analysisId) {
		return service.findRNASeqAnalysisByAnalysisId(analysisId);
	}
}
