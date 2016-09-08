package com.appdynamics.metrics;

import java.util.ArrayList;
import java.util.List;

public class MetricsRecord {

	private String name = null;
	private Long count = null;
	private Double max = null;
	private Double mean = null;
	private Double min = null;
	private Double stdDev = null;
	private Double median = null;
	private Double seventyFifthPercentile = null;
	private Double ninetyFifthPercentile = null;
	private Double ninetyNinthPercentile = null;
	private Double meanRate = null;
	private Double oneMinuteRate = null;
	private Double fiveMinuteRate = null;
	private Double fifteenMinuteRate = null;
	private String rateUnit = null;
	private String durationUnit = null;

	private List<String> attributes = new ArrayList<String> ();
	

	public MetricsRecord(String metricName, List<String> attributes) {
		
		this.name = metricName;
		
		if (attributes != null) {
			this.attributes.addAll(attributes);
		}
	}

	public Long getCount() {
		return count;
	}
	public void setCount(Long count) {
		this.count = count;
	}
	public Double getMax() {
		return max;
	}
	public void setMax(Double max) {
		this.max = max;
	}
	public Double getMean() {
		return mean;
	}
	public void setMean(Double mean) {
		this.mean = mean;
	}
	public Double getMin() {
		return min;
	}
	public void setMin(Double min) {
		this.min = min;
	}
	public Double getStdDev() {
		return stdDev;
	}
	public void setStdDev(Double stdDev) {
		this.stdDev = stdDev;
	}
	public Double getMedian() {
		return median;
	}
	public void setMedian(Double median) {
		this.median = median;
	}
	public Double getSeventyFifthPercentile() {
		return seventyFifthPercentile;
	}
	public void setSeventyFifthPercentile(Double seventyFifthPercentile) {
		this.seventyFifthPercentile = seventyFifthPercentile;
	}
	public Double getNinetyFifthPercentile() {
		return ninetyFifthPercentile;
	}
	public void setNinetyFifthPercentile(Double ninetyFifthPercentile) {
		this.ninetyFifthPercentile = ninetyFifthPercentile;
	}
	public Double getNinetyNinthPercentile() {
		return ninetyNinthPercentile;
	}
	public void setNinetyNinthPercentile(Double ninetyNinthPercentile) {
		this.ninetyNinthPercentile = ninetyNinthPercentile;
	}
	public Double getMeanRate() {
		return meanRate;
	}
	public void setMeanRate(Double meanRate) {
		this.meanRate = meanRate;
	}
	public Double getOneMinuteRate() {
		return oneMinuteRate;
	}
	public void setOneMinuteRate(Double oneMinuteRate) {
		this.oneMinuteRate = oneMinuteRate;
	}
	public Double getFiveMinuteRate() {
		return fiveMinuteRate;
	}
	public void setFiveMinuteRate(Double fiveMinuteRate) {
		this.fiveMinuteRate = fiveMinuteRate;
	}
	public Double getFifteenMinuteRate() {
		return fifteenMinuteRate;
	}
	public void setFifteenMinuteRate(Double fifteenMinuteRate) {
		this.fifteenMinuteRate = fifteenMinuteRate;
	}
	public String getRateUnit() {
		return rateUnit;
	}
	public void setRateUnit(String rateUnit) {
		this.rateUnit = rateUnit;
	}
	public String getDurationUnit() {
		return durationUnit;
	}
	public void setDurationUnit(String durationUnit) {
		this.durationUnit = durationUnit;
	}

	public List<String> getAttributes() {
		return attributes;
	}

	public void setAttributes(List<String> attributes) {
		this.attributes = attributes;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}