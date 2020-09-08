package gov.nih.ncats.omics.warehouse.model;

import java.util.ArrayList;

import javax.persistence.Entity;

import org.springframework.data.repository.CrudRepository;

/** @deprecated Class is deprecated and will be removed. 
 * @author braistedjc
 *
 */
public class ExpressionDataSet {
		
	private Long sampleSetId;
	
	private SampleSet sampleSet;

	private ArrayList <ExpressionProfileLite> profiles;
	
	
}
