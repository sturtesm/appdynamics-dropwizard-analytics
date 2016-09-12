# appdynamics-dropwizard-analytics
git repo that integrates metrics reporting using dropwizard with appdynamics analytics platform

# Usage

The <b>appdynamics-dropwizard-analytics</b> package provides a dropwizard reporter to publish performance metrics into the AppDynamics analytics platform for visualization, reporting, and analysis.

The reporter is built by instantiating an <b>EventServiceConfig</b> object which specifies the configuration needed to connect to, and publish custom events (metric data) into an Events Service Cluster.  The configuration needed is as follows:

   * Array of Strings containing the Custom Attributes we'll report with each Metric Record
   * Array of Strings containing the Custom Attribute values we'll report with each Metric Record
   * Events Service connection protocol, default should be https
   * Events Service URL endpoint, the ES_ENDPOINT_DEFAULT points to the AppDynamics SaaS cluster
   * The port information, if SaaS (port 80) then this should be null.  Available to be set to connect to an on-prem cluster if needed
   * The schema we'll create (if not already created) and publish events into
   * The global customer id with a valid analytics license, enabling the ability to publish events through the API
   * The API KEY that has been created in AppDynamics, enabling API access to publish events into the analytics cluster.
   

# Sample

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

# Known Limitations

<b> Inability to update existing schemas</b><p>
Prior to metrics being published into the cluster, we need to create a schema with all attributes defined.  We do this by identifying the custom attributes, and the default dropwizard metrics, we'll support.  If a change is made in a later execution to the custom attributes, we can't retroactively update the schema to add, remove or modify the existing schema / attributes.  In this case, the recommendation is to either delete, and subsequently re-create a new schema with the change in attributes, or identify a new schema to publish metrics into.