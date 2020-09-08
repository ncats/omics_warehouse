package gov.nih.ncats.omics.warehouse.repository;

import org.springframework.data.repository.CrudRepository;

import gov.nih.ncats.omics.warehouse.model.Person;

public interface InvestigatorRepo extends CrudRepository <Person, Long> {
	public Person findInvestigatorById(Long id);
}
