package com.appdynamics.metrics;

import java.io.IOException;
import java.util.List;

import org.testng.log4testng.Logger;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

public class MetricEventsBuilder extends StdSerializer<MetricsRecord> 
{
	private enum TYPE {
		INT("integer"), FLOAT("float"), STRING("string");
		
		private String type = null;
		
		TYPE(String type) {
			this.type = type;
		}
		
		String getType() {
			return type;
		}
	}


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Logger logger = Logger.getLogger(getClass());
	
	private MetricsRecord template = null;

	private ObjectMapper mapper;
	
	private MetricEventsBuilder(Class<MetricsRecord> t) {
		super(t);
	}
	
	public MetricEventsBuilder(List<String> attributeKeys) {
		this(MetricsRecord.class);
		
		this.mapper = new ObjectMapper();
		SimpleModule module = new SimpleModule();

		/** 
		 * we'll use this class to custom serialize the metrics record based on 
		 * what the event service expects
		 **/
		module.addSerializer(MetricsRecord.class, this);
		
		/** 
		 * register the mapper to use this class as a custom serializer for MetricsRecord objects
		 */
		mapper.registerModule(module);
		
		
		template = new MetricsRecord("Metric Name", attributeKeys);
	}
	
    private void addFieldToEventsSchema(StringBuffer buffer, String key, TYPE type, boolean prefixComma)
	{
		String s = null;
		if (prefixComma) {
			s = String.format(",\n\t\"%s\":\"%s\"", key, type.getType());
		}
		else {
			s = String.format("\n\t\"%s\":\"%s\"", key, type.getType());

		}

		buffer.append(s);
	}
	
	/** 
	 * returns a JSON object defining the metrics schema
	 * 
	 * @return
	 */
	public String getEventsServiceSchema() {
		
		StringBuffer buffer = new StringBuffer();

		buffer.append("\n{\n\"schema\" : \n\t{\n");

		addFieldToEventsSchema(buffer, "metric_name", TYPE.STRING, false);
		addFieldToEventsSchema(buffer, "count", TYPE.INT, true);
		addFieldToEventsSchema(buffer, "max", TYPE.FLOAT, true);
		addFieldToEventsSchema(buffer, "mean", TYPE.FLOAT, true);
		addFieldToEventsSchema(buffer, "stdDev", TYPE.FLOAT, true);
		addFieldToEventsSchema(buffer, "median", TYPE.FLOAT, true);
		addFieldToEventsSchema(buffer, "seventyFifthPercentile", TYPE.FLOAT, true);
		addFieldToEventsSchema(buffer, "ninetyFifthPercentile", TYPE.FLOAT, true);
		addFieldToEventsSchema(buffer, "ninetyNinthPercentile", TYPE.FLOAT, true);
		addFieldToEventsSchema(buffer, "meanRate", TYPE.FLOAT, true);
		addFieldToEventsSchema(buffer, "oneMinuteRate", TYPE.FLOAT, true);
		addFieldToEventsSchema(buffer, "fiveMinuteRate", TYPE.FLOAT, true);
		addFieldToEventsSchema(buffer, "fifteenMinuteRate", TYPE.FLOAT, true);
		addFieldToEventsSchema(buffer, "rateUnit", TYPE.STRING, true);
		addFieldToEventsSchema(buffer, "durationUnit", TYPE.STRING, true);
		
		if (template.getAttributes() != null && !template.getAttributes().isEmpty()) {
			for (String s : template.getAttributes()) {
				addFieldToEventsSchema(buffer, s, TYPE.STRING, true);
			}
		}

		buffer.append("\n\t}\n}");

		return buffer.toString();
	}

	/**
	 * Get a json attribute that defines the metric record
	 * 
	 * @return
	 * @throws JsonProcessingException 
	 */
	public String getMetricsRecord(MetricsRecord record) throws JsonProcessingException {
		
		StringBuffer buffer = new StringBuffer();

		String json = mapper.writeValueAsString(record);

		buffer.append("[ " + json + "]");
		
		logger.debug("Mapped metrics record to: " + buffer.toString());
		
		return buffer.toString();
	}

	@Override
    public void serialize(MetricsRecord value, JsonGenerator jgen, SerializerProvider provider) 
      throws IOException, JsonProcessingException {
        jgen.writeStartObject();
        
        serializeIfNotNull(jgen, "metric_name", value.getName());
        serializeIfNotNull(jgen, "count", value.getCount());
        serializeIfNotNull(jgen, "max", value.getMax());
        serializeIfNotNull(jgen, "mean", value.getMean());
        serializeIfNotNull(jgen, "stdDev", value.getStdDev());
        serializeIfNotNull(jgen, "median", value.getMedian());
        serializeIfNotNull(jgen, "seventyFifthPercentile", value.getSeventyFifthPercentile());
        serializeIfNotNull(jgen, "ninetyFifthPercentile", value.getNinetyFifthPercentile());
        serializeIfNotNull(jgen, "ninetyNinthPercentile", value.getNinetyNinthPercentile());
        serializeIfNotNull(jgen, "meanRate", value.getMean());
        serializeIfNotNull(jgen, "oneMinuteRate", value.getOneMinuteRate());
        serializeIfNotNull(jgen, "fiveMinuteRate", value.getFiveMinuteRate());
        serializeIfNotNull(jgen, "fifteenMinuteRate", value.getFifteenMinuteRate());
        serializeIfNotNull(jgen, "rateUnit", value.getRateUnit());
        serializeIfNotNull(jgen, "durationUnit", value.getRateUnit());

        
        if (value.getAttributes() != null && !value.getAttributes().isEmpty()) {
        	
        	if (template.getAttributes() == null) {
        		logger.error("Error serializing metrics attributes, metric record contains " + value.getAttributes().size() + " attribute, but metrics template did not define any.");
        	}
        	else if (value.getAttributes().size() != template.getAttributes().size()) {
        		logger.error("Error serializing metrics attributes, metric record contains " + value.getAttributes().size() + 
        				" attribute, but metrics template was defined with " + template.getAttributes().size() + " attributes."); 
        	}
        	else {
        		for (int i = 0; i < value.getAttributes().size(); i++) {
        			String key = template.getAttributes().get(i);
        			String v = value.getAttributes().get(i);
        			
        			serializeIfNotNull(jgen,key, v);
        		}
        	}
        }
        
        jgen.writeEndObject();
    }
	
	/**
     * serializes the key / value field if the value is non-null
     * 
     * @param jgen
     * @param key
     * @param value
     * @throws IOException
     */
	private void serializeIfNotNull(JsonGenerator jgen, String key, String value) throws IOException {
		if (value != null) {
			jgen.writeStringField(key, value);
		}		
	}

	/**
     * serializes the key / value field if the value is non-null
     * 
     * @param jgen
     * @param key
     * @param value
     * @throws IOException
     */
    private void serializeIfNotNull(JsonGenerator jgen, String key, Double value) throws IOException {
    	if (value != null) {
			jgen.writeNumberField(key, value.doubleValue());
		}
	}

	
	
	/**
     * serializes the key / value field if the value is non-null
     * 
     * @param jgen
     * @param key
     * @param value
     * @throws IOException
     */
	private void serializeIfNotNull(JsonGenerator jgen, String key, Long value) throws IOException {
		if (value != null) {
			jgen.writeNumberField(key, value);
		}
	}
	
}
