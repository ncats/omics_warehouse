package gov.nih.ncats.omics.warehouse.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import gov.nih.ncats.omics.warehouse.model.RNASeqAnalysis;

public interface RNASeqAnalysisRepo extends CrudRepository <RNASeqAnalysis, Long> {
	public RNASeqAnalysis findRNASeqAnalysisByAnalysisId(Long analysisId);	
}
