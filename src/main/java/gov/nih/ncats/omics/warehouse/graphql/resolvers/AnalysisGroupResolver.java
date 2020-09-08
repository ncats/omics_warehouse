package gov.nih.ncats.omics.warehouse.graphql.resolvers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.coxautodev.graphql.tools.GraphQLQueryResolver;

import gov.nih.ncats.omics.warehouse.model.AnalysisConditionGroup;
import gov.nih.ncats.omics.warehouse.model.RNASeqAnalysis;
import gov.nih.ncats.omics.warehouse.service.OmicsService;

/**
 * Graphql query resolver to support AnalysisConditionGroup queries. 
 * @author braistedjc
 *
 */
@Component
public class AnalysisGroupResolver implements GraphQLQueryResolver {

	@Autowired
	OmicsService service;
	
	public AnalysisConditionGroup analysisgroup(Long groupId) {
		return service.findAnalysisGroupById(groupId);
	}
	
	public List<AnalysisConditionGroup> analysisGroupsById(Long analysisId) {
		return service.findAnalysisGroupsByAnalysisId(analysisId);
	}
	
}
