package gov.nih.ncats.omics.warehouse.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import gov.nih.ncats.omics.warehouse.model.Cell;

public interface CellRepo extends CrudRepository <Cell, Long> {
	public Cell findCellByCellTypeId(Long cellTypeId);
}
