package gov.nih.ncats.omics.warehouse.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import gov.nih.ncats.omics.warehouse.model.AnalysisConditionGroup;

public interface AnalysisGroupRepo extends CrudRepository <AnalysisConditionGroup, Long> {
	public AnalysisConditionGroup findAnalysisConditionGroupByAnalysisGroupId(Long analysisGroupId);
	public List <AnalysisConditionGroup> findAnalysisConditionGroupsByParentAnalysisId(Long parentAnalysisId);
}
