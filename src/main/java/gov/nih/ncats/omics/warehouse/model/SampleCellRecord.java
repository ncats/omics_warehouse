package gov.nih.ncats.omics.warehouse.model;

/**
 * A simple mapping class to connect a biological Sample to a related single cell barcode.
 * This class is supporting single cell RNAseq and related single cell technologies.
 * 
 * @author braistedjc
 *
 */
public class SampleCellRecord {

	private Long sampleId;
	private String cellBarcode;
	
	public SampleCellRecord() {
		
	}
	
	public SampleCellRecord(Long sampleId, String cellBarcode) {
		this.sampleId = sampleId;
		this.cellBarcode = cellBarcode;
	}
	
	public Long getSampleId() {
		return sampleId;
	}
	public void setSampleId(Long sampleId) {
		this.sampleId = sampleId;
	}
	public String getCellBarcode() {
		return cellBarcode;
	}
	public void setCellBarcode(String cellBarcode) {
		this.cellBarcode = cellBarcode;
	}
	
}
