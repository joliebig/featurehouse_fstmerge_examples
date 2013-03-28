using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Diagnostics;
using System.Linq;
using System.Text;
using System.Reflection;
using System.IO;
namespace FireIRC.Resources.GTK.PlugIn
{
    public partial class PlugInEngine
    {
        Dictionary<string, AppDomain> appDomains = new Dictionary<string, AppDomain>();
        Dictionary<string, Assembly> pluginAssembly = new Dictionary<string, Assembly>();
        List<string> loadedPlugins = new List<string>();
        public List<string> LoadedPlugins
        {
            get { return loadedPlugins; }
            set { loadedPlugins = value; }
        }
        public PlugInEngine()
        {
        }
        public object CreateObject(string pluginfriendlyname, string objectpath)
        {
            return appDomains[pluginfriendlyname].CreateInstanceAndUnwrap(pluginAssembly[pluginfriendlyname].FullName, objectpath);
        }
        public void LoadPlugin(string path, string pluginfriendlyname)
        {
            AppDomain root = AppDomain.CurrentDomain;
            AppDomain a = AppDomain.CreateDomain(pluginfriendlyname);
            pluginAssembly.Add(pluginfriendlyname, a.Load(File.ReadAllBytes(path), null));
            appDomains.Add(pluginfriendlyname, a);
            a.CreateInstanceAndUnwrap(pluginAssembly[pluginfriendlyname].FullName, "Plugin").GetType().GetMethod("OnLoad").Invoke(null, new object[] { root });
            loadedPlugins.Add(pluginfriendlyname);
        }
        public void UnloadPlugin(string pluginfriendlyname)
        {
            try
            {
                appDomains[pluginfriendlyname].CreateInstanceAndUnwrap(pluginAssembly[pluginfriendlyname].FullName, "Plugin").GetType().GetMethod("OnUnload").Invoke(null, new object[] { });
                AppDomain.Unload(appDomains[pluginfriendlyname]);
                appDomains.Remove(pluginfriendlyname);
                pluginAssembly.Remove(pluginfriendlyname);
                loadedPlugins.Remove(pluginfriendlyname);
            }
            catch (KeyNotFoundException) { }
        }
    }
}
