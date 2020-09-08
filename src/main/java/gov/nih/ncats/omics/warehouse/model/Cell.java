package gov.nih.ncats.omics.warehouse.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * The Cell class contains primary information about cell lines.
 * The description field may be used as a free text description of the cell line.
 * 
 * @author braistedjc
 *
 */
@Entity
@Table(name = "omics_adm.cell")
public class Cell {
	
	@Id
	@Column(name = "cell_type_id")
	private Long cellTypeId;
	
	@Column(name = "cell_line_abbr")
	private String cellLineAbbr;
	
	@Column(name = "cell_type")
	private String cellType;
	
	@Column(name = "organism")
	private String organism;
	
	@Column(name = "tissue")
	private String tissueOrigin;
	
	@Column(name = "atcc_id")
	private String atccId;
	
	@Column(name = "description")
	private String description;

	public Cell() {
	}

	public Long getCellTypeId() {
		return cellTypeId;
	}

	public void setCellTypeId(Long dbCellTypeId) {
		this.cellTypeId = dbCellTypeId;
	}

	public String getCellLineAbbr() {
		return cellLineAbbr;
	}

	public void setCellLineAbbr(String cellLineAbbr) {
		this.cellLineAbbr = cellLineAbbr;
	}

	public String getCellType() {
		return cellType;
	}

	public void setCellType(String cellType) {
		this.cellType = cellType;
	}

	public String getOrganism() {
		return organism;
	}

	public void setOrganism(String organsm) {
		this.organism = organsm;
	}

	public String getTissueOrigin() {
		return tissueOrigin;
	}

	public void setTissueOrigin(String tissueOrigin) {
		this.tissueOrigin = tissueOrigin;
	}

	public String getAtccId() {
		return atccId;
	}

	public void setAtccId(String atccId) {
		this.atccId = atccId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	
	
}
