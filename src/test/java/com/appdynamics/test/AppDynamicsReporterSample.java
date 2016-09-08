package com.appdynamics.test;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import com.appdynamics.metrics.AppdynamicsReporter;
import com.appdynamics.metrics.EventServiceConfig;
import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;

public class AppDynamicsReporterSample {
	static final MetricRegistry metrics = new MetricRegistry();

	public static void main(String args[]) {
		
		startReport();
		Timer responseTime = metrics.timer("response-time");
		Meter requests = metrics.meter("requests");
		requests.mark();

		responseTime.update(0, TimeUnit.MILLISECONDS);
		wait5Seconds();
		responseTime.update(5, TimeUnit.SECONDS);
		requests.mark(5);
		wait5Seconds();
	}

	static void startReport() {

		/** our events service configuration */
		EventServiceConfig config = new EventServiceConfig(
				Arrays.asList(new String[] {"version", "userid"}),
				Arrays.asList(new String[] {"version_xyz", "steve sturtevant"}),
				"https", 
				EventServiceConfig.ES_ENDPOINT_DEFAULT, 
				null,
				"MetricsSchema", 
				"customer1_dd34e97c-2906-4a86-a005-8c3efd1daa08", 
				"0d91579c-3266-4645-9348-4bf268f11a4f");

		AppdynamicsReporter reporter = AppdynamicsReporter.forRegistry(metrics)
				.convertRatesTo(TimeUnit.SECONDS)
				.convertDurationsTo(TimeUnit.MILLISECONDS)
				.build(config);

		reporter.start(1, TimeUnit.SECONDS);
	}

	static void wait5Seconds() {
		try {
			Thread.sleep(5*1000);
		}
		catch(InterruptedException e) {}
	}
}