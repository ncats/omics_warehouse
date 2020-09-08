package gov.nih.ncats.omics.warehouse.model;

import java.io.IOException;
import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.data.domain.Pageable;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * The RNASeqProfile class is used to encapsulate a bulk RNASeq gene expression profile.
 * The profile data is a binary base64 encoding of JSON text representing an ExpressionProfileLite object.
 * A utility method returns the ExpressionProfileLite object.
 * 
 * @author braistedjc
 *
 */

@Entity
@Table(name = "omics_adm.fact_sc_rnaseq_profile")
public class SCRNASeqProfile implements Serializable {

		@Id
		@Column(name="gene_id")
		private Long geneId;
		
		@Column(name="sample_id")
		private Long sampleId;
				
		@Column(name="ext_gene_name")
		private String extGeneName;
		
		@Column(name="gene_description")
		private String geneDescription;

		@Column(name="profile_json_blob")
		private byte [] profileData;
		
		public SCExpressionProfileLite getExpressionProfileLite(ObjectMapper mapper) {
			SCExpressionProfileLite profile = null;
			
			try {
				profile = mapper.readValue(profileData, SCExpressionProfileLite.class);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return profile;
		}
		
		
		public Long getGeneId() {
			return geneId;
		}

		public void setGeneId(Long geneId) {
			this.geneId = geneId;
		}

		public Long getSampleId() {
			return sampleId;
		}

		public void setSampleId(Long sampleId) {
			this.sampleId = sampleId;
		}

		public String getExtGeneName() {
			return extGeneName;
		}

		public void setExtGeneName(String extGeneName) {
			this.extGeneName = extGeneName;
		}

		public String getGeneDescription() {
			return geneDescription;
		}

		public void setGeneDescription(String geneDescription) {
			this.geneDescription = geneDescription;
		}

		public byte[] getProfileData() {
			return profileData;
		}

		public void setProfileData(byte[] profileData) {
			this.profileData = profileData;
		}
		
		
}
