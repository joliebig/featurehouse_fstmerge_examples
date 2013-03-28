using System;
using System.Collections.Generic;
using System.IO;
using System.Reflection;
using System.Security;
using System.Security.Permissions;
using System.Text;
using log4net;
using NewsComponents.Utils;
using RssBandit;
using RssBandit.Common.Logging;
using RssBandit.UIServices;
using Syndication.Extensibility;
using Logger = RssBandit.Common.Logging;
namespace AppInteropServices
{
    [ReflectionPermission(SecurityAction.Demand, MemberAccess=true, Unrestricted=true)]
    [Serializable]
    public class ServiceManager : IAddInManager
    {
        private static readonly ILog _log = Log.GetLogger(typeof (ServiceManager));
        private static AppDomain myAppDomain;
        private static AppDomain LoaderDomain
        {
            get
            {
                if (myAppDomain == null)
                {
                    myAppDomain = AppDomain.CreateDomain("loaderDomain");
                }
                return myAppDomain;
            }
        }
        private IList<IAddIn> addInList = null;
        public static IList<IBlogExtension> SearchForIBlogExtensions(string path)
        {
            ServiceManager srvFinder =
                (ServiceManager)
                LoaderDomain.CreateInstanceAndUnwrap(Assembly.GetAssembly(typeof (ServiceManager)).FullName,
                                                     "AppInteropServices.ServiceManager");
            IEnumerable<Type> extensions = srvFinder.SearchForIBlogExtensionTypes(path);
            List<IBlogExtension> extensionInstances = new List<IBlogExtension>();
            foreach (Type foundType in extensions)
            {
                try
                {
                    IBlogExtension extension = (IBlogExtension) Activator.CreateInstance(foundType);
                    extensionInstances.Add(extension);
                }
                catch (Exception ex)
                {
                    _log.Error("Plugin of type '" + foundType.FullName + "' could not be activated.", ex);
                }
            }
            return extensionInstances;
        }
        public IEnumerable<Type> SearchForIBlogExtensionTypes(string path)
        {
            Type blogExtensionType = typeof (IBlogExtension);
            List<Type> foundTypes = new List<Type>();
            IPermission rp = new ReflectionPermission(ReflectionPermissionFlag.MemberAccess);
            try
            {
                rp.Demand();
            }
            catch (SecurityException se)
            {
                _log.Debug("ServiceManager.SearchForIBlogExtensionTypes()", se);
                return foundTypes;
            }
            if (path == null || ! Directory.Exists(path))
            {
                return foundTypes;
            }
            string[] fs1 = Directory.GetFiles(path, "*.exe");
            string[] fs2 = Directory.GetFiles(path, "*.dll");
            string[] files = new string[fs1.Length + fs2.Length];
            fs1.CopyTo(files, 0);
            fs2.CopyTo(files, fs1.Length);
            foreach (string f in files)
            {
                if (f == null || f.Length == 0) continue;
                try
                {
                    Assembly a = Assembly.LoadFrom(f);
                    foreach (Type t in a.GetTypes())
                    {
                        if (blogExtensionType.IsAssignableFrom(t))
                        {
                            foundTypes.Add(t);
                        }
                    }
                }
                catch (Exception e)
                {
                    _log.Debug("ServiceManager.SearchForIBlogExtensionTypes()", e);
                }
            }
            return foundTypes;
        }
        public static void UnloadLoaderAppDomain()
        {
            AppDomain.Unload(myAppDomain);
            myAppDomain = null;
        }
        public static IList<IAddIn> PopulateAndInitAddInPackages(IEnumerable<IAddIn> assemblies)
        {
            ServiceManager srvFinder =
                (ServiceManager)
                LoaderDomain.CreateInstanceAndUnwrap(Assembly.GetAssembly(typeof (ServiceManager)).FullName,
                                                     "AppInteropServices.ServiceManager");
            IDictionary<string, IEnumerable<Type> > addInTypes = srvFinder.SearchForIAddInPackagesTypes(assemblies);
            List<IAddIn> newList = new List<IAddIn>();
            foreach (IAddIn a in assemblies)
            {
                if (a.Location == null || ! addInTypes.ContainsKey(a.Location))
                    continue;
                List<IAddInPackage> instances = new List<IAddInPackage>();
                IEnumerable<Type> foundTypes = addInTypes[a.Location];
                foreach (Type foundType in foundTypes)
                {
                    try
                    {
                        IAddInPackage addInPackage = (IAddInPackage) Activator.CreateInstance(foundType);
                        if (addInPackage != null)
                            instances.Add(addInPackage);
                    }
                    catch (Exception ex)
                    {
                        _log.Error("AddIn of type '" + foundType.FullName + "' could not be activated.", ex);
                    }
                }
                if (instances.Count > 0)
                {
                    newList.Add(new AddIn(a.Location, Path.GetFileNameWithoutExtension(a.Location), instances));
                }
            }
            return newList;
        }
        public IDictionary<string, IEnumerable<Type> > SearchForIAddInPackagesTypes(IEnumerable<IAddIn> addIns)
        {
            Type packageType = typeof (IAddInPackage);
            Dictionary<string, IEnumerable<Type> > foundTypes = new Dictionary<string, IEnumerable<Type> >();
            IPermission rp = new ReflectionPermission(ReflectionPermissionFlag.MemberAccess);
            try
            {
                rp.Demand();
            }
            catch (SecurityException se)
            {
                _log.Debug("ServiceManager.SearchForIAddInPackagesTypes()", se);
                return foundTypes;
            }
            if (addInList == null || addInList.Count == 0)
            {
                return foundTypes;
            }
            foreach (IAddIn f in addIns)
            {
                if (f.Location == null || f.Location.Length == 0) continue;
                if (! File.Exists(f.Location)) continue;
                try
                {
                    Assembly a = Assembly.LoadFrom(f.Location);
                    List<Type> typeArray = new List<Type>();
                    foreach (Type t in a.GetTypes())
                    {
                        if (packageType.IsAssignableFrom(t))
                        {
                            typeArray.Add(t);
                        }
                    }
                    if (typeArray.Count > 0)
                        foundTypes.Add(f.Location, typeArray);
                }
                catch (Exception e)
                {
                    _log.Debug("ServiceManager.SearchForIAddInPackagesTypes()", e);
                }
            }
            return foundTypes;
        }
        public void Unload(IAddIn addIn)
        {
            if (addIn == null) return;
            addInList.Remove(addIn);
            SaveAddInsToConfiguration(addInList, AddInConfigurationFile);
        }
        public IAddIn Load(string fileName)
        {
            if (fileName == null || fileName.Length == 0)
                return null;
            if (File.Exists(fileName))
            {
                foreach (IAddIn a in AddIns)
                {
                    if (a.Location == fileName)
                        return null;
                }
                List<IAddIn> list = new List<IAddIn>();
                list.Add(new AddIn(fileName));
                IList<IAddIn> ret = PopulateAndInitAddInPackages(list);
                if (ret != null && ret.Count > 0)
                {
                    IAddIn addin = ret[0];
                    addInList.Add(addin);
                    SaveAddInsToConfiguration(addInList, AddInConfigurationFile);
                    return addin;
                }
            }
            return null;
        }
        public IEnumerable<IAddIn> AddIns
        {
            get
            {
                if (addInList == null)
                {
                    addInList = PopulateAndInitAddInPackages(LoadAddInFromConfiguration());
                }
                return addInList;
            }
        }
        private static string AddInConfigurationFile
        {
            get
            {
                return Path.Combine(RssBanditApplication.GetUserPath(), "addins.cfg");
            }
        }
        private static IList<IAddIn> LoadAddInFromConfiguration()
        {
            return LoadAddInPathsFromConfiguration(AddInConfigurationFile);
        }
        private static IList<IAddIn> LoadAddInPathsFromConfiguration(string cfgFile)
        {
            List<IAddIn> list = new List<IAddIn>();
            if (File.Exists(cfgFile))
            {
                using (Stream s = FileHelper.OpenForRead(cfgFile))
                {
                    using (StreamReader r = new StreamReader(s, Encoding.UTF8))
                    {
                        string addInPath = r.ReadLine();
                        if (File.Exists(addInPath))
                        {
                            list.Add(new AddIn(addInPath));
                        }
                        else
                        {
                            _log.Error("Unable to locate configured AddIn at '" + addInPath + "'");
                        }
                    }
                }
            }
            return list;
        }
        private static void SaveAddInsToConfiguration(ICollection<IAddIn> addIns, string cfgFile)
        {
            if (addIns == null || addIns.Count == 0)
            {
                if (File.Exists(cfgFile))
                    FileHelper.Delete(cfgFile);
                return;
            }
            using (Stream s = FileHelper.OpenForWrite(cfgFile))
            {
                using (StreamWriter w = new StreamWriter(s, Encoding.UTF8))
                {
                    foreach (IAddIn addIn in addIns)
                    {
                        w.WriteLine(addIn.Location);
                    }
                }
            }
        }
        [Serializable]
        private class AddIn : IAddIn
        {
            private readonly string _location;
            private readonly string _name;
            private readonly IList<IAddInPackage> _packages;
            public AddIn(string location, string name, IList<IAddInPackage> packages)
            {
                _location = location;
                _name = name;
                _packages = packages;
            }
            public AddIn(string location) : this(location, null, null)
            {
            }
            public IList<IAddInPackage> AddInPackages
            {
                get
                {
                    return _packages;
                }
            }
            public string Name
            {
                get
                {
                    return _name;
                }
            }
            public string Location
            {
                get
                {
                    return _location;
                }
            }
            public void Dispose()
            {
            }
        }
    }
}
