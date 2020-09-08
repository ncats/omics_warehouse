package gov.nih.ncats.omics.warehouse.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * The Sample class holds high level information about a biological sample entering an omics Experiment.
 * @param sample_id is a unique id within the omics platform.
 * @param sampleCode is a user assigned code for the sample. This is usually a identifier.
 * @param sampleName is a user assigned short descriptive name, for example, 'H9_zika_48h'.
 * @param baseCell and baseCellId and baseCellLine refer to a cell 'starting' cell line.
 * @param targetCell and targetCellId and targetCellLine handle cases where the sample represents a differentiation protocol.
 * @param treatments holds a list of Treatment objects that relate to the sample. (0 or more treatments per sample).
 * 
 * @author braistedjc
 *
 */
@Entity
@Table(name = "omics_adm.dim_sample")
public class Sample {
	
	@Id
	@Column(name = "sample_id")
	private Long sampleId;

	@Column(name = "sample_code")
	private String sampleCode;
	
	@Column(name = "name")
	private String sampleName;
	
	@Column(name = "description")
	private String description;
	  
	@Column(name = "base_cell_id", insertable = false, updatable = false)
	private Long baseCellId;

	@Column(name = "target_cell_id", insertable = false, updatable = false)
	private Long targetCellId;
	
	@JsonIgnore
	@Transient
	private String baseCellLine;
	
	@JsonIgnore
	@Transient
	private String targetCellLine;
	
	@OneToOne
	@JoinColumn(name = "base_cell_id")
	private Cell baseCell;
	
	@OneToOne
	@JoinColumn(name = "target_cell_id")
	private Cell targetCell;
	
	@OneToMany
	@JoinColumn(name = "sample_id")
	private List <Treatment> treatments;
	
	public Sample() {
		treatments = new ArrayList<Treatment>();
	}
	
	public Sample(String sampleCode, String sampleName) {
		this.sampleCode = sampleCode;
		this.sampleName = sampleName;
	}
	
	public String getSampleName() {
		return sampleName;
	}
	public void setSampleName(String sampleName) {
		this.sampleName = sampleName;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getDbCellId() {
		return baseCellLine;
	}
	public void setDbCellId(String cellTypeId) {
		this.baseCellLine = cellTypeId;
	}
	public Long getDbSampleId() {
		return sampleId;
	}
	public void setDbSampleId(Long dbSampleId) {
		this.sampleId = dbSampleId;
	}

	public String getSampleCode() {
		return sampleCode;
	}

	public void setSampleCode(String sampleCode) {
		this.sampleCode = sampleCode;
	}

	public String getBaseCellLine() {
		return baseCellLine;
	}

	public void setBaseCellLine(String baseCellLine) {
		this.baseCellLine = baseCellLine;
	}

	public String getTargetCellLine() {
		return targetCellLine;
	}

	public void setTargetCellLine(String targetCellLine) {
		this.targetCellLine = targetCellLine;
	}

	public Long getBaseCellId() {
		return baseCellId;
	}

	public void setBaseCellId(Long baseCellId) {
		this.baseCellId = baseCellId;
	}

	public Long getTargetCellId() {
		return targetCellId;
	}

	public void setTargetCellId(Long targetCellId) {
		this.targetCellId = targetCellId;
	}

	public Cell getBaseCell() {
		return baseCell;
	}

	public void setBaseCell(Cell baseCell) {
		this.baseCell = baseCell;
	}

	public Cell getTargetCell() {
		return targetCell;
	}

	public void setTargetCell(Cell targetCell) {
		this.targetCell = targetCell;
	}

	public Long getSampleId() {
		return sampleId;
	}

	public void setSampleId(Long sampleId) {
		this.sampleId = sampleId;
	}

	public List<Treatment> getTreatments() {
		return treatments;
	}

	public void setTreatments(List<Treatment> treatments) {
		this.treatments = treatments;
	}
	
	
}
