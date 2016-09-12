package com.appdynamics.metrics;

import java.util.List;

public class EventServiceConfig {

	/** the default ES host / endpoint */
	public final static String ES_ENDPOINT_DEFAULT = "analytics.api.appdynamics.com";

	/** the ES API Account Name Key */
	public final static String ES_API_ACCOUNT_NAME_HEADER_KEY = "X-Events-API-AccountName";

	/** the ES API Key Header */
	public final static String ES_API_KEY_ME_HEADER_KEY = "X-Events-API-Key";

	/** our content type */
	public final static String ES_CONTENT_TYPE_VALUE = "application/vnd.appd.events+json;v=1";

	private String esProtocol = null;

	private String esPort = null;

	private String esHostname = null;

	private String globalAccountName = null;

	private String esApiKey = null;

	private String esSchemaName = null;
	
	private MetricEventsBuilder metricsBuilder = null;

	/** the custom attributes we're reporting on*/
	private List<String> attributeKeys;

	/** the values of the custom attributes we're reporting on */
	private List<String> attributeValues;

	/**
	 * @param customAttributes - array of custom attributes we'll report with the metrics
	 * @param eventServiceProtocol - 'http' or 'https'
	 * @param eventServiceHostname - events service hostname, default SaaS is {@link #ES_ENDPOINT_DEFAULT}
	 * @param eventServicePort - events service port, should be null if it's port 80.  Default SaaS is 80, default on-prem is 9080
	 * @param esSchemaName - events service to work with, will be created if doesn't already exist
	 * @param globalAccountName - global account name
	 * @param esApiKey - events service API Key, must be created in AppDynamics Controller
	 */
	public EventServiceConfig(
			List<String> customAttributeKeys,
			List<String> customAttributeValues,
			String eventServiceProtocol,
			String eventServiceHostname,
			String eventServicePort,
			String esSchemaName,
			String globalAccountName,
			String esApiKey)
	{
		this.esSchemaName = esSchemaName;
		this.esHostname = eventServiceHostname;
		this.globalAccountName = globalAccountName;
		this.esApiKey = esApiKey;
		this.esPort = eventServicePort;
		this.esProtocol = eventServiceProtocol;
		
		this.attributeKeys = customAttributeKeys;
		this.attributeValues = customAttributeValues;
		
		this.metricsBuilder = new MetricEventsBuilder(attributeKeys);
	}

	public List<String> getAttributeValues() {
		return attributeValues;
	}

	public String getEsApiKey() {
		return esApiKey;
	}

	/**
	 * Get the base Events Service URL, example http://analytics.api.appdynamics.com:9080/events
	 * @return
	 */
	public String getEsBaseURL() {
		
		String port = (getEsPort() == null) ? "" : ":" + esPort;
		
		return String.format("%s://%s%s/events", 
				getEsProtocol(), getEsHostname(), port);
	}

	public String getEsHostname() {
		return esHostname;
	}

	public String getEsPort() {
		return esPort;
	}

	public String getEsProtocol() {
		return esProtocol;
	}

	public String getEsSchemaName() {
		return esSchemaName;
	}

	public String getEventServiceEndpoint() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getGlobalAccountName() {
		return globalAccountName;
	}

	public MetricEventsBuilder getMetricsBuilder() {
		return metricsBuilder;
	}

	public void setAttributeValues(List<String> attributeValues) {
		this.attributeValues = attributeValues;
	}

	/**
	 * validate we have config set to attempt a connection to the event service
	 *  
	 * @throws IllegalArgumentException
	 */
	protected void validateConfig() throws IllegalArgumentException 
	{
		if (metricsBuilder == null) {
			throw new IllegalArgumentException("Metrics Schema Cannot be NULL");
		}
		if (esHostname == null) {
			throw new IllegalArgumentException("Event Service Endpoint URL Cannot be NULL");
		}
		else if (globalAccountName == null) {
			throw new IllegalArgumentException("Global Account Name Cannot be NULL");
		}
		else if (esApiKey == null) {
			throw new IllegalArgumentException("Event Service API Key Cannot be NULL");
		}
		else if (esSchemaName == null) {
			throw new IllegalArgumentException("Event Service Schema Name Cannot be NULL");
		}
		else if (esProtocol == null) {
			throw new IllegalArgumentException("Event Service Protocol Cannot be NULL");
		}
	}

}
