using System;
using System.Collections.Generic;
using System.Text;
using System.Resources;
using System.Globalization;
using System.Threading;
using OVT.Melissa.PluginSupport;
namespace FireIRC.Resources.Localization
{
    public class ResourceLocalizer
    {
        ResourceManager res = new ResourceManager("FireIRC.Resources.Localization.StringResources", typeof(ResourceLocalizer).Assembly);
        public string GetString(string key)
        {
            return res.GetString(key, new System.Globalization.CultureInfo(PropertyService.Get<string>("language", "en-US")));
        }
        public string GetString(string key, CultureInfo ci)
        {
            return res.GetString(key,ci);
        }
    }
}
