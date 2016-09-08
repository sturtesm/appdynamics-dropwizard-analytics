package com.appdynamics.metrics;




import org.apache.log4j.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.WebResource.Builder;

public class AnalyticsClient {

	private static final Logger logger = Logger.getLogger(AnalyticsClient.class);

	private EventServiceConfig config = null;
	private Client client = null;
	private WebResource resource = null;
	
	/** have we initialized the schema yet? */
	private boolean schemaInit = false;

	public AnalyticsClient(EventServiceConfig config) {
		this.config = config;
	}

	/**
	 * Attempt to create the analytics schema, returns TRUE if the schema already
	 * exists, or was successfully created.
	 * 
	 * @return TRUE or FALSE
	 */
	public boolean createEventsSchema() {

		initWebResource();

		String baseURL = config.getEsBaseURL();

		String resourceURL = String.format("%s/schema/%s", 
				baseURL, config.getEsSchemaName());

		WebResource resource = client.resource(resourceURL);

		String accountName = config.getGlobalAccountName();
		String apiKey = config.getEsApiKey();

		logger.debug("Adding Account Name: " + accountName + " and API Key: " + apiKey);

		Builder requestBuilder = resource.getRequestBuilder();

		requestBuilder = requestBuilder.header(
				EventServiceConfig.ES_API_ACCOUNT_NAME_HEADER_KEY, accountName);
		requestBuilder.header(
				EventServiceConfig.ES_API_KEY_ME_HEADER_KEY, apiKey);
		requestBuilder.header("Content-Type", EventServiceConfig.ES_CONTENT_TYPE_VALUE);

		requestBuilder.accept(EventServiceConfig.ES_CONTENT_TYPE_VALUE);


		logger.debug("Creating Schema: " + resourceURL);

		/**gets the json representation of the metrics schema */
		String schema = config.getMetricsBuilder().getEventsServiceSchema();

		logger.debug("Build analytics schema: " + schema);

		ClientResponse response = requestBuilder.post(ClientResponse.class, schema);

		logger.debug("Create schema response: " + response);

		/** if the status is 201 then we're OK */
		if (response.getStatus() == 201) {

			logger.info("Successfully created schema: " + config.getEsSchemaName());


			return true;
		}
		else if (response.getStatus() == 409) {
			logger.info("Schema " + config.getEsSchemaName() + " already exists, will reuse existing schema");

			return true;
		}
		else {
			logger.error("Error creating new schema " + config.getEsSchemaName() + ": " + response.getStatus());

			return false;
		}
	}

	public boolean deleteEventsSchema() {
		initWebResource();

		String baseURL = config.getEsBaseURL();

		String resourceURL = String.format("%s/schema/%s", 
				baseURL, config.getEsSchemaName());

		WebResource resource = client.resource(resourceURL);

		String accountName = config.getGlobalAccountName();
		String apiKey = config.getEsApiKey();

		logger.trace("Adding Account Name: " + accountName + " and API Key: " + apiKey);

		Builder requestBuilder = resource.getRequestBuilder();

		requestBuilder = requestBuilder.header(
				EventServiceConfig.ES_API_ACCOUNT_NAME_HEADER_KEY, accountName);
		requestBuilder.header(
				EventServiceConfig.ES_API_KEY_ME_HEADER_KEY, apiKey);
		requestBuilder.header("Content-Type", EventServiceConfig.ES_CONTENT_TYPE_VALUE);


		logger.debug("Deleting Schema: " + resourceURL);

		String schema = config.getMetricsBuilder().getEventsServiceSchema();

		logger.debug("Deleting events service schema: " + schema);

		ClientResponse response = requestBuilder.delete(ClientResponse.class);

		logger.debug("Deleted schema response: " + response);

		/** if the status is 201 then we're OK */
		if (response.getStatus() == 200) {

			logger.info("Successfully deleted schema: " + config.getEsSchemaName());


			return true;
		}
		else {
			logger.error("Error deleting schema " + config.getEsSchemaName() + " [" + response.getStatus() + "]");
			
			return false;
		}
	}
	
	public EventServiceConfig getConfig() {
		return config;
	}


	private void initWebResource() {

		if (resource == null) {
			client = Client.create();

			config.validateConfig();
		}
	}

	public boolean publishMetricEvent(MetricsRecord record) throws JsonProcessingException {
		initWebResource();


		/** get the metrics schema */
		MetricEventsBuilder schema = config.getMetricsBuilder();

		String baseURL = config.getEsBaseURL();

		/** the analytics publish endpoint we're posting to */
		String resourceURL = String.format("%s/publish/%s", 
				baseURL, config.getEsSchemaName());

		WebResource resource = client.resource(resourceURL);

		String accountName = config.getGlobalAccountName();
		String apiKey = config.getEsApiKey();

		logger.debug("Adding Account Name: " + accountName + " and API Key: " + apiKey);

		Builder requestBuilder = resource.getRequestBuilder();

		requestBuilder = requestBuilder.header(
				EventServiceConfig.ES_API_ACCOUNT_NAME_HEADER_KEY, accountName);
		requestBuilder.header(
				EventServiceConfig.ES_API_KEY_ME_HEADER_KEY, apiKey);
		requestBuilder.header("Content-Type", EventServiceConfig.ES_CONTENT_TYPE_VALUE);

		requestBuilder.accept(EventServiceConfig.ES_CONTENT_TYPE_VALUE);

		/** the json record we're posting */
		String jsonRecord = schema.getMetricsRecord(record);

		logger.debug("Posting metrics record to: " + config.getEsSchemaName());

		ClientResponse response = requestBuilder.post(ClientResponse.class, jsonRecord);

		logger.debug("Publish metric reponse: " + response);

		/** if the status is 201 then we're OK */
		if (response.getStatus() != 400 ) {

			logger.info("Successfully published metrics record to " + config.getEsSchemaName());


			return true;
		}
		else if (response.getStatus() == 400) {
			logger.info("Error publishing metrics record to " + config.getEsSchemaName() + " [400]");

			return false;
		}
		else {
			logger.info("Unknown error publishing metrics record to " + config.getEsSchemaName() + " [" + response.getStatus() + "]");
			
			return false;
		}
	}

	public void setConfig(EventServiceConfig config) {
		this.config = config;
	}

}