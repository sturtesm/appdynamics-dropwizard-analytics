  package com.appdynamics.test;
  import java.util.Random;
import java.util.concurrent.TimeUnit;

import com.codahale.metrics.ConsoleReporter;
import com.codahale.metrics.Histogram;
import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;

  public class ConsoleReporterSample {
    static final MetricRegistry metrics = new MetricRegistry();
    
    public static void main(String args[]) {
      startReport();
      Meter requests = metrics.meter("requests");
      Histogram histogram = metrics.histogram("histogram");
      
      for (int i = 1; i <= 10000; i++) {
    	  histogram.update(new Random().nextInt(i));
      }
      requests.mark();
      wait5Seconds();
    }

  static void startReport() {

	  ConsoleReporter reporter = ConsoleReporter.forRegistry(metrics)
          .convertRatesTo(TimeUnit.SECONDS)
          .convertDurationsTo(TimeUnit.MILLISECONDS)
          .build();

      reporter.start(1, TimeUnit.SECONDS);
  }

  static void wait5Seconds() {
      try {
          Thread.sleep(5*1000);
      }
      catch(InterruptedException e) {}
  }
}