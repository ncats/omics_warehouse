package gov.nih.ncats.omics.warehouse.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import gov.nih.ncats.omics.warehouse.model.RNASeqDERecord;

public interface RNASeqDERecordRepo extends CrudRepository <RNASeqDERecord, Long>{
	public RNASeqDERecord findRecordByDeRecordId(Long deRecordId);
	public RNASeqDERecord findRecordByGeneIdAndAnalysisId(Long geneId, Long analysisId);
	public List<RNASeqDERecord> findRecordsByAnalysisId(Long analysisId);
}
