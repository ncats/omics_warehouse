package gov.nih.ncats.omics.warehouse.graphql.resolvers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.coxautodev.graphql.tools.GraphQLQueryResolver;

import gov.nih.ncats.omics.warehouse.model.Cell;
import gov.nih.ncats.omics.warehouse.service.OmicsService;

/**
 * GraphQL resolver to support Cell queries.
 * @author braistedjc
 *
 */
@Component
public class CellQueryResolver implements GraphQLQueryResolver {

	@Autowired
	OmicsService service;
	
	public List<Cell> cells() {
		return service.findAllCells();
	}
	
	public Cell cell(Long cellTypeId) {
		return service.getCellByCellTypeId(cellTypeId);
	}
}
