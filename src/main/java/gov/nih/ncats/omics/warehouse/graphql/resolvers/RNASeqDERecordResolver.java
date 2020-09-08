package gov.nih.ncats.omics.warehouse.graphql.resolvers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.coxautodev.graphql.tools.GraphQLQueryResolver;

import gov.nih.ncats.omics.warehouse.model.RNASeqDERecord;
import gov.nih.ncats.omics.warehouse.service.OmicsService;

/**
 * Graphql resolver to support RNASeqDERecord queries.
 * @author braistedjc
 *
 */
@Component
public class RNASeqDERecordResolver implements GraphQLQueryResolver {

	@Autowired
	private OmicsService service;
	
	public RNASeqDERecord rnaseqDeRecord(Long deRecordId) {
		return service.findRNASeqDERecordByDeRecordId(deRecordId);
	}
	                            
	public List<RNASeqDERecord> rnaseqDeRecordsByAnalysisId(Long analysisId) {
		return service.findRNASeqDERecordsByAnalysisId(analysisId);
	}
	
	public RNASeqDERecord rnaseqDeRecordByGeneIdAndAnalysisId(Long geneId, long analysisId) {
		return service.findRNASeqDERecordByDeGeneIdAndAnalysisId(geneId, analysisId);
	}
}
