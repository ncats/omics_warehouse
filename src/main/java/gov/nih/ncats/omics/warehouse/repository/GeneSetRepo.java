package gov.nih.ncats.omics.warehouse.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import gov.nih.ncats.omics.warehouse.model.GeneSet;

public interface GeneSetRepo extends CrudRepository <GeneSet, Long>{
	public GeneSet findGeneSetByGeneSetId(Long did);
	public List <GeneSet> findGeneSetsBySampleSetId(Long sampleSetId);
	public List <GeneSet> findGeneSetsByExptId(Long exptId);
	public List <GeneSet> findGeneSetsByProjectId(Long projectId);
}
