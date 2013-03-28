using System;
using System.Collections.Generic;
using System.Configuration;
using System.Xml;
using ThoughtWorks.CruiseControl.Remote;
namespace ThoughtWorks.CruiseControl.Core.Config
{
    public sealed class ServerConfigurationHandler
        : IConfigurationSectionHandler
    {
        public object Create(object parent, object configContext, XmlNode section)
        {
            ServerConfiguration configuration = new ServerConfiguration();
            foreach (XmlNode node in section.SelectNodes("extension"))
            {
                ExtensionConfiguration config = new ExtensionConfiguration();
                config.Type = node.Attributes["type"].Value;
                List<XmlElement> items = new List<XmlElement>();
                foreach (XmlElement itemEl in node.SelectNodes("*"))
                {
                    items.Add(itemEl);
                }
                config.Items = items.ToArray();
                configuration.Extensions.Add(config);
            }
            return configuration;
        }
    }
}
