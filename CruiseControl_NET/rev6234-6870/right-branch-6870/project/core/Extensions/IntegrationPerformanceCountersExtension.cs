using System.Collections.Generic;
using System.Diagnostics;
using ThoughtWorks.CruiseControl.Remote;
using ThoughtWorks.CruiseControl.Core.Util;
namespace ThoughtWorks.CruiseControl.Core.Extensions
{
    public class IntegrationPerformanceCountersExtension
        : ICruiseServerExtension
    {
        public void Initialise(ICruiseServer server, ExtensionConfiguration extensionConfig)
        {
            if (!PerformanceCounterCategory.Exists("CruiseControl.NET"))
            {
                Log.Info("Initialising new performance counters for integration requests");
                var collection = new CounterCreationDataCollection();
                var numberOfCompletedIntegrations = new CounterCreationData();
                numberOfCompletedIntegrations.CounterType = PerformanceCounterType.NumberOfItems32;
                numberOfCompletedIntegrations.CounterName = "Number of Completed Integrations";
                collection.Add(numberOfCompletedIntegrations);
                var numberOfFailedIntegrations = new CounterCreationData();
                numberOfFailedIntegrations.CounterType = PerformanceCounterType.NumberOfItems32;
                numberOfFailedIntegrations.CounterName = "Number of Failed Integrations";
                collection.Add(numberOfFailedIntegrations);
                var integrationElapsedTime = new CounterCreationData();
                integrationElapsedTime.CounterType = PerformanceCounterType.AverageTimer32;
                integrationElapsedTime.CounterName = "Integration Time";
                collection.Add(integrationElapsedTime);
                var averageIntegrations = new CounterCreationData();
                averageIntegrations.CounterType = PerformanceCounterType.AverageBase;
                averageIntegrations.CounterName = "Average number of integrations";
                collection.Add(averageIntegrations);
                PerformanceCounterCategory.Create("CruiseControl.NET",
                    "Performance counters for CruiseControl.NET",
                    collection);
            }
            Log.Debug("Initialising performance monitoring - integration requests");
            var numberOfCompletedIntegrationsCounter = new PerformanceCounter("CruiseControl.NET", "Number of Completed Integrations", false);
            var numberOfFailedIntegrationsCounter = new PerformanceCounter("CruiseControl.NET", "Number of Failed Integrations", false);
            var integrationElapsedTimeCounter = new PerformanceCounter("CruiseControl.NET", "Integration Time", false);
            var averageIntegrationsCounter = new PerformanceCounter("CruiseControl.NET", "Average number of integrations", false);
            var stopwatches = new Dictionary<string, Stopwatch>();
            server.IntegrationStarted += (o, e) =>
            {
                Log.Debug(string.Format("Starting stopwatch for '{0}'", e.ProjectName));
                if (stopwatches.ContainsKey(e.ProjectName))
                {
                    stopwatches[e.ProjectName].Reset();
                }
                else
                {
                    var stopwatch = new Stopwatch();
                    stopwatches.Add(e.ProjectName, stopwatch);
                    stopwatch.Start();
                }
            };
            server.IntegrationCompleted += (o, e) =>
            {
                Log.Debug(string.Format("Performance logging for '{0}'", e.ProjectName));
                if (stopwatches.ContainsKey(e.ProjectName))
                {
                    var stopwatch = stopwatches[e.ProjectName];
                    stopwatch.Stop();
                    stopwatches.Remove(e.ProjectName);
                    averageIntegrationsCounter.Increment();
                    integrationElapsedTimeCounter.IncrementBy(stopwatch.ElapsedTicks);
                }
                if (e.Status == IntegrationStatus.Success)
                {
                    numberOfCompletedIntegrationsCounter.Increment();
                }
                else
                {
                    numberOfFailedIntegrationsCounter.Increment();
                }
            };
        }
        public void Start()
        {
        }
        public void Stop()
        {
        }
        public void Abort()
        {
        }
    }
}
