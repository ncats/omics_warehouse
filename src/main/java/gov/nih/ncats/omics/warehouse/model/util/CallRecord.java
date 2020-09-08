package gov.nih.ncats.omics.warehouse.model.util;

public class CallRecord {

	public String urlTemplate;
	public String utilityNote;
	
	public CallRecord() {
		
	}
	
	public CallRecord(String url, String note) {
		this.urlTemplate = url;
		this.utilityNote = note;
	}

	public String getUrlTemplate() {
		return urlTemplate;
	}

	public void setUrlTemplate(String urlTemplate) {
		this.urlTemplate = urlTemplate;
	}

	public String getUtilityNote() {
		return utilityNote;
	}

	public void setUtilityNote(String utilityNote) {
		this.utilityNote = utilityNote;
	}
}
