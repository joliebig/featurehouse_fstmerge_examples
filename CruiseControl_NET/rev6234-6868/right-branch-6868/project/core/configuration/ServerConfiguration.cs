using System;
using System.Collections.Generic;
using System.Threading;
using ThoughtWorks.CruiseControl.Remote;
namespace ThoughtWorks.CruiseControl.Core.Config
{
    public class ServerConfiguration
    {
        private List<ExtensionConfiguration> extensions = new List<ExtensionConfiguration>();
        public List<ExtensionConfiguration> Extensions
        {
            get { return extensions; }
        }
    }
}
