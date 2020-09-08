package gov.nih.ncats.omics.warehouse.model;

import java.util.ArrayList;

/**
 * @deprecated This class been replaced with RNASeqProfile and ExpressionProfileLite
 * 
 * Container class for an expression profile holding gene annotation, a sample set, raw counts and normalized counts.
 * This utility class is used to deliver the results for a gene over a collection of samples. 
 * 
 * @author braistedjc
 *
 */
public class ExpressionProfile {

	private Gene geneAnn;
	private SampleSet sampleSet;
	private Long geneId;
	private Long sampleSetId;
	private String extGeneName;
	private String geneDescription;
	private ArrayList <Integer> rawCounts;
	private ArrayList <Double> normCounts;
	
	public ExpressionProfile() {
		//these may be overwritten by setter methods but construction
		//will insure that we can accumulate count records sequentially if deemed a useful way to populate
		rawCounts = new ArrayList<Integer>();
		normCounts = new ArrayList<Double>();
	}
	
	public void addNormalizedExpressionValue(Double val) {
		normCounts.add(val);
	}
	
	public Boolean verifyProfileLength() {
		return (sampleSet.setSize() > 0 && 
				sampleSet.setSize() == rawCounts.size() && 
				sampleSet.setSize() == normCounts.size());
	}
	
	public Integer getLength() {
		return sampleSet.setSize();
	}
	
	public Integer getRawCounts(int i) {
		return rawCounts.get(i);
	}
	
	public Double getNormCounts(int i) {
		return normCounts.get(i);
	}
	
	public Sample getSample(int i) {
		return sampleSet.getSample(i);
	}

	public Gene getGeneAnn() {
		return geneAnn;
	}

	public void setGeneAnn(Gene geneAnn) {
		this.geneAnn = geneAnn;
	}

	public SampleSet getSampleSet() {
		return sampleSet;
	}

	public void setSampleSet(SampleSet sampleSet) {
		this.sampleSet = sampleSet;
	}

	public ArrayList<Integer> getRawCountsList() {
		return rawCounts;
	}

	public void setRawCountsList(ArrayList<Integer> rawCountsList) {
		this.rawCounts = rawCountsList;
	}

	public ArrayList<Double> getNormCountsList() {
		return normCounts;
	}

	public void setNormCountsList(ArrayList<Double> normCountsList) {
		this.normCounts = normCountsList;
	}
	
	public Integer getSampleCount() {
		return this.sampleSet.setSize();
	}
	
	//method will permit appending raw counts rather than a setter method for the collection of values.
	public void appendRawCountEntry(Integer rawCounts) {
		this.rawCounts.add(rawCounts);
	}
	
	//method permits appending normalized counts to the list
	public void appendNormalizedCountEntry(Double normCounts) {
		this.normCounts.add(normCounts);
	}
	
	
}
