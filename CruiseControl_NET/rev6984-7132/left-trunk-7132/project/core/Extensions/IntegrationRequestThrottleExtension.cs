using System;
using System.Collections.Generic;
using System.Xml;
using ThoughtWorks.CruiseControl.Core.Util;
using ThoughtWorks.CruiseControl.Remote;
using ThoughtWorks.CruiseControl.Remote.Events;
namespace ThoughtWorks.CruiseControl.Core.Extensions
{
    public class IntegrationRequestThrottleExtension
        : ICruiseServerExtension
    {
        private int numberOfRequestsAllowed = 5;
        private List<string> requests = new List<string>();
        private object updateLock = new object();
        public void Initialise(ICruiseServer server, ExtensionConfiguration extensionConfig)
        {
            foreach (XmlElement itemEl in extensionConfig.Items)
            {
                if (itemEl.Name == "limit") numberOfRequestsAllowed = Convert.ToInt32(itemEl.InnerText);
            }
            server.IntegrationStarted += new EventHandler<IntegrationStartedEventArgs>(server_IntegrationStarted);
            server.IntegrationCompleted += new EventHandler<IntegrationCompletedEventArgs>(server_IntegrationCompleted);
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
        private void server_IntegrationCompleted(object sender, IntegrationCompletedEventArgs e)
        {
            lock (updateLock)
            {
                if (requests.Contains(e.ProjectName)) requests.Remove(e.ProjectName);
            }
        }
        private void server_IntegrationStarted(object sender, IntegrationStartedEventArgs e)
        {
            Log.Debug(string.Format("Checking if '{0}' can integrate", e.ProjectName));
            int numberOfRequests = 0;
            string[] currentRequests = new string[0];
            lock (updateLock)
            {
                if (!requests.Contains(e.ProjectName)) requests.Add(e.ProjectName);
                numberOfRequests = requests.Count;
                currentRequests = requests.ToArray();
            }
            if (numberOfRequests <= numberOfRequestsAllowed)
            {
                Log.Debug(string.Format("'{0}' can integrate", e.ProjectName));
                e.Result = IntegrationStartedEventArgs.EventResult.Continue;
            }
            else
            {
                Log.Debug(string.Format("'{0}' is delayed - number of requests ({1}) has been exceeded ({2})",
                    e.ProjectName,
                    numberOfRequestsAllowed,
                    numberOfRequests));
                bool isAllowed = false;
                for (int loop = 0; loop < numberOfRequestsAllowed; loop++)
                {
                    if (currentRequests[loop] == e.ProjectName)
                    {
                        isAllowed = true;
                        break;
                    }
                }
                if (isAllowed)
                {
                    e.Result = IntegrationStartedEventArgs.EventResult.Continue;
                }
                else
                {
                    e.Result = IntegrationStartedEventArgs.EventResult.Delay;
                }
            }
        }
    }
}
