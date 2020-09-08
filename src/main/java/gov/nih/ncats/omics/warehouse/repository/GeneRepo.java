package gov.nih.ncats.omics.warehouse.repository;

import org.springframework.data.repository.CrudRepository;

import gov.nih.ncats.omics.warehouse.model.Gene;

public interface GeneRepo extends CrudRepository <Gene, Long>{
	public Gene findGeneByGeneId(Long geneId);
	public Gene findGeneByExtGeneNameAndEnsemblVersNum(String extGeneName, Integer ensemblVerNum);
}
