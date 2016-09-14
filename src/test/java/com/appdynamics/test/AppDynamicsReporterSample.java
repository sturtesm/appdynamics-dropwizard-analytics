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


		for (int i = 0; i < 10; i++) {
			requests.mark();

			responseTime.update(0, TimeUnit.MILLISECONDS);
			wait5Seconds();
			responseTime.update(5, TimeUnit.SECONDS);
			requests.mark(5);
			wait5Seconds();
		}
	}

	static void startReport() {

		/** our events service configuration */
		EventServiceConfig config = new EventServiceConfig(
				Arrays.asList(new String[] {"version", "userid"}),
				Arrays.asList(new String[] {"version_xyz", "steve sturtevant"}),
				"https", 
				EventServiceConfig.ES_ENDPOINT_DEFAULT, 
				null,
				"PayPalMetrics", 
				"customer1_dd34e97c-2906-4a86-a005-8c3efd1daa08", 

				//the below is the customer API key created for paypal
				"88bc27f6-5505-4465-b54e-78f9f1f34099");

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