package com.appdynamics.test;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.appdynamics.metrics.AnalyticsClient;
import com.appdynamics.metrics.EventServiceConfig;
import com.appdynamics.metrics.MetricsRecord;
import com.fasterxml.jackson.core.JsonProcessingException;


public class TestAnalyticsEvents {

	Logger logger = Logger.getLogger(getClass());
	
	private AnalyticsClient analyticsClient = null;


	@BeforeClass
	public void setup() throws JsonProcessingException {

		/** our events service configuration */
		EventServiceConfig config = new EventServiceConfig(
				Arrays.asList(new String[] {"version", "userid"}),
				Arrays.asList(new String[] {"TestAnalyticsEvents", "steve sturtevant"}),
				"https", 
				EventServiceConfig.ES_ENDPOINT_DEFAULT, 
				null,
				"MetricsSchema", 
				"customer1_dd34e97c-2906-4a86-a005-8c3efd1daa08", 
				"0d91579c-3266-4645-9348-4bf268f11a4f");

		analyticsClient = new AnalyticsClient(config);
	}
	
	@Test
	public void testSchemaDelete() {
		boolean success = analyticsClient.deleteEventsSchema();
		
		logger.info("Status of schema delete: " + success);
	}
	
	@Test (dependsOnMethods = { "testSchemaDelete" } )
	public void testSchemaCreate() {

		boolean status = analyticsClient.createEventsSchema();

		assertThat(status);
	}


	@Test (dependsOnMethods = { "testSchemaCreate" } )
	public void testMetricsRecordCreate() throws JsonProcessingException {
		List<String> attributes = Arrays.asList(new String[] {"1.0", "steve s"});
	
		MetricsRecord record = new MetricsRecord("response-time", attributes);
		
		record.setCount((long) 100);
		record.setMean((double) 50);
		record.setMax((double) 1000);
		record.setMeanRate((double) 35);
		
		record.setAttributes(attributes);
		
		EventServiceConfig config = analyticsClient.getConfig();
		
		String s = config.getMetricsBuilder().getMetricsRecord(record);
		
		logger.debug("Metrics schema [json] for the metrics record == " + s);
		
		AnalyticsClient client = new AnalyticsClient(config);
		
		boolean status = client.publishMetricEvent(record);
		
		assertThat(status);
	}
	

}
