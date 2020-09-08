package gov.nih.ncats.omics.warehouse.model.util;

import java.util.ArrayList;

import gov.nih.ncats.omics.warehouse.model.SCExpressionProfileLite;
import gov.nih.ncats.omics.warehouse.model.SCRNASeqProfile;
import gov.nih.ncats.omics.warehouse.model.SampleCellRecord;

public class SingleCellLoadData {

	private String filePath;
	private String fileName;
	private String sampleCode;
	private String sampleSetCode;
	private ArrayList <SampleCellRecord> sampleCellRecords;
	private ArrayList <SCExpressionProfileLite> profiles;
	
	public SingleCellLoadData() {
		sampleCellRecords = new ArrayList <SampleCellRecord>();
		profiles = new ArrayList <SCExpressionProfileLite>();
	}
	
	public void addCellRecord(Long sampleId, String cellBarcode) {
		sampleCellRecords.add(new SampleCellRecord(sampleId, cellBarcode));
	}
	
	public void addProfile(SCExpressionProfileLite profile) {
		profiles.add(profile);
	}
	
	public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getSampleCode() {
		return sampleCode;
	}
	public void setSampleCode(String sampleCode) {
		this.sampleCode = sampleCode;
	}
	
	public ArrayList<SampleCellRecord> getSampleCellRecords() {
		return sampleCellRecords;
	}
	public void setSampleCellRecords(ArrayList<SampleCellRecord> sampleCellRecords) {
		this.sampleCellRecords = sampleCellRecords;
	}
	public String getSampleSetCode() {
		return sampleSetCode;
	}
	public void setSampleSetCode(String sampleSetCode) {
		this.sampleSetCode = sampleSetCode;
	}

	public ArrayList<SCExpressionProfileLite> getProfiles() {
		return profiles;
	}

	public void setProfiles(ArrayList<SCExpressionProfileLite> profiles) {
		this.profiles = profiles;
	}	
	
}
