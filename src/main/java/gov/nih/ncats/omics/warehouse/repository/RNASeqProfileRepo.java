package gov.nih.ncats.omics.warehouse.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import gov.nih.ncats.omics.warehouse.model.RNASeqProfile;

public interface RNASeqProfileRepo extends CrudRepository <RNASeqProfile, Long> {
	public List<RNASeqProfile> getRNASeqProfileBySampleSetId(Long ssid);
	public RNASeqProfile getRNASeqProfileByGeneIdAndSampleSetId(Long geneId, Long sampleSetId);
	public List<RNASeqProfile> findAllBySampleSetId(Long ssid, Pageable pageRequest);	
}
