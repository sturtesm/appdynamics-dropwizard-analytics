package com.appdynamics.metrics;

import java.nio.charset.Charset;
import java.util.Map;
import java.util.SortedMap;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.Counter;
import com.codahale.metrics.Gauge;
import com.codahale.metrics.Histogram;
import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricFilter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.ScheduledReporter;
import com.codahale.metrics.Snapshot;
import com.codahale.metrics.Timer;
import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * A reporter which creates a comma-separated values file of the measurements for each metric.
 */
public class AppdynamicsReporter extends ScheduledReporter {
	/**
	 * Returns a new {@link Builder} for {@link AppdynamicsReporter}.
	 *
	 * @param registry the registry to report
	 * @return a {@link Builder} instance for a {@link AppdynamicsReporter}
	 */
	public static Builder forRegistry(MetricRegistry registry) {
		return new Builder(registry);
	}

	/**
	 * A builder for {@link AppdynamicsReporter} instances. Defaults to using the default locale, converting
	 * rates to events/second, converting durations to milliseconds, and not filtering metrics.
	 */
	public static class Builder {
		private final MetricRegistry registry;
		private TimeUnit rateUnit;
		private TimeUnit durationUnit;
		private MetricFilter filter;

		private Builder(MetricRegistry registry) {
			this.registry = registry;
			this.rateUnit = TimeUnit.SECONDS;
			this.durationUnit = TimeUnit.MILLISECONDS;
			this.filter = MetricFilter.ALL;
		}

		/**
		 * Convert rates to the given time unit.
		 *
		 * @param rateUnit a unit of time
		 * @return {@code this}
		 */
		public Builder convertRatesTo(TimeUnit rateUnit) {
			this.rateUnit = rateUnit;
			return this;
		}

		/**
		 * Convert durations to the given time unit.
		 *
		 * @param durationUnit a unit of time
		 * @return {@code this}
		 */
		public Builder convertDurationsTo(TimeUnit durationUnit) {
			this.durationUnit = durationUnit;
			return this;
		}

		/**
		 * Builds a {@link AppdynamicsReporter} with the given properties, writing {@code .csv} files to the
		 * given directory.
		 *
		 * @param directory the directory in which the {@code .csv} files will be created
		 * @return a {@link AppdynamicsReporter}
		 */
		public AppdynamicsReporter build(EventServiceConfig config) {

			return new AppdynamicsReporter(registry,
					rateUnit,
					durationUnit,
					filter,
					config);
		}
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(AppdynamicsReporter.class);
	private static final Charset UTF_8 = Charset.forName("UTF-8");

	/** the events service client */
	private AnalyticsClient esClient;
	
	/** have we created the schema yet? */
	private boolean schemaInit = false;

	private AppdynamicsReporter(
			MetricRegistry registry,
			TimeUnit rateUnit,
			TimeUnit durationUnit,
			MetricFilter filter, 
			EventServiceConfig config) 
	{
		super(registry, "appdynamics-metric-eventsreporter", filter, rateUnit, durationUnit);

		this.esClient = new AnalyticsClient(config);
	}

	@Override
	public void report(SortedMap<String, Gauge> gauges,
			SortedMap<String, Counter> counters,
			SortedMap<String, Histogram> histograms,
			SortedMap<String, Meter> meters,
			SortedMap<String, Timer> timers) {

		for (Map.Entry<String, Gauge> entry : gauges.entrySet()) {
			reportGauge(entry.getKey(), entry.getValue());
		}

		for (Map.Entry<String, Counter> entry : counters.entrySet()) {
			reportCounter(entry.getKey(), entry.getValue());
		}

		for (Map.Entry<String, Histogram> entry : histograms.entrySet()) {
			reportHistogram(entry.getKey(), entry.getValue());
		}

		for (Map.Entry<String, Meter> entry : meters.entrySet()) {
			reportMeter(entry.getKey(), entry.getValue());
		}

		for (Map.Entry<String, Timer> entry : timers.entrySet()) {
			reportTimer(entry.getKey(), entry.getValue());
		}
	}

	private void reportTimer(String name, Timer timer) {
		final Snapshot snapshot = timer.getSnapshot();

		MetricsRecord record = new MetricsRecord(name, esClient.getConfig().getAttributeValues());

		record.setCount(timer.getCount());
		record.setMax(convertDuration(snapshot.getMax()));
		record.setMean(convertDuration(snapshot.getMean()));
		record.setMin(convertDuration(snapshot.getMin()));
		record.setStdDev(convertDuration(snapshot.getStdDev()));
		record.setMedian(convertDuration(snapshot.getMedian()));
		record.setSeventyFifthPercentile(convertDuration(snapshot.get75thPercentile()));
		record.setNinetyFifthPercentile(convertDuration(snapshot.get95thPercentile()));
		record.setNinetyNinthPercentile(convertDuration(snapshot.get99thPercentile()));
		record.setMeanRate(convertRate(timer.getMeanRate()));
		record.setOneMinuteRate(convertRate(timer.getOneMinuteRate()));
		record.setFiveMinuteRate(convertRate(timer.getFiveMinuteRate()));
		record.setFifteenMinuteRate(convertRate(timer.getFifteenMinuteRate()));
		record.setRateUnit(getRateUnit());
		record.setDurationUnit(getDurationUnit());

		report(record);
	}

	private void reportMeter(String name, Meter meter) {

		MetricsRecord record = new MetricsRecord(name, 
				esClient.getConfig().getAttributeValues());

		record.setCount(meter.getCount());
		record.setMeanRate(convertRate(meter.getMeanRate()));
		record.setOneMinuteRate(convertRate(meter.getOneMinuteRate()));
		record.setFiveMinuteRate(convertRate(meter.getFiveMinuteRate()));
		record.setFifteenMinuteRate(convertRate(meter.getFifteenMinuteRate()));
		record.setRateUnit(getRateUnit());

		report(record);
	}

	private void reportHistogram(String name, Histogram histogram) {
		final Snapshot snapshot = histogram.getSnapshot();

		MetricsRecord record = new MetricsRecord(name, 
				esClient.getConfig().getAttributeValues());

		record.setCount(histogram.getCount());
		record.setMax(convertDuration(snapshot.getMax()));
		record.setMean(convertDuration(snapshot.getMean()));
		record.setMin(convertDuration(snapshot.getMin()));
		record.setStdDev(convertDuration(snapshot.getStdDev()));
		record.setMedian(convertDuration(snapshot.getMedian()));
		record.setSeventyFifthPercentile(convertDuration(snapshot.get75thPercentile()));
		record.setNinetyFifthPercentile(convertDuration(snapshot.get95thPercentile()));
		record.setNinetyNinthPercentile(convertDuration(snapshot.get99thPercentile()));
	}

	private void reportCounter(String name, Counter counter) {
		MetricsRecord record = new MetricsRecord(name, 
				esClient.getConfig().getAttributeValues());

		record.setCount(counter.getCount());
	}

	private void reportGauge(String name, Gauge gauge) {
		MetricsRecord record = new MetricsRecord(name, 
				esClient.getConfig().getAttributeValues());

		Object o = gauge.getValue();
		Double v = new Double(0);
		
		if (o instanceof Integer) {
			v = new Double((Integer) o);
		}
		else if (o instanceof Double) {
			v = new Double((Double) o);
		}
		else if (o instanceof Float) {
			v = new Double((Float) o);
		}
		else if (o instanceof String) {
			LOGGER.error("Gauges as Strings are not currently support...");
			
			return;
		}
		else {
			LOGGER.error("Gauge is an unsupported type of " + o.getClass().getName());
			
			return;
		}
		
		record.setMean(v);
	}

	private void report(MetricsRecord record) {
		try {
			
			if (!schemaInit) {
				schemaInit = esClient.createEventsSchema();
			}
			
			esClient.publishMetricEvent(record);
			
		} catch (JsonProcessingException e) {
			LOGGER.error("Error reporting metric " + record.getName() + ": " + e.getMessage());
			e.printStackTrace();
		}
	}

	protected String sanitize(String name) {
		return name;
	}
}
