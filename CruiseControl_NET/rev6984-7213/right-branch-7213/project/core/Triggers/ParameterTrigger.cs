using System;
using Exortech.NetReflector;
using ThoughtWorks.CruiseControl.Core.Util;
using ThoughtWorks.CruiseControl.Remote;
using System.Collections.Generic;
namespace ThoughtWorks.CruiseControl.Core.Triggers
{
    [ReflectorType("parameterTrigger")]
    public class ParameterTrigger : ITrigger
    {
        private ITrigger innerTrigger;
        private NameValuePair[] parameters = new NameValuePair[0];
        public ParameterTrigger()
        {
        }
        [ReflectorProperty("trigger", InstanceTypeKey = "type")]
        public ITrigger InnerTrigger
        {
            get { return innerTrigger; }
            set { innerTrigger = value; }
        }
        [ReflectorProperty("parameters")]
        public NameValuePair[] Parameters
        {
            get { return parameters; }
            set { parameters = value; }
        }
        public DateTime NextBuild
        {
            get { return innerTrigger.NextBuild; }
        }
        public void IntegrationCompleted()
        {
            innerTrigger.IntegrationCompleted();
        }
        public IntegrationRequest Fire()
        {
            IntegrationRequest request = innerTrigger.Fire();
            if (request != null)
            {
                List<NameValuePair> values = new List<NameValuePair>(parameters);
                request.BuildValues = NameValuePair.ToDictionary(values);
            }
            return request;
        }
    }
}
