using System;
using System.Collections.Generic;
using System.IO;
using System.Reflection;
using System.Text;
using ThoughtWorks.CruiseControl.CCTrayLib.Configuration;
namespace ThoughtWorks.CruiseControl.CCTrayLib.Monitoring
{
    public static class ExtensionHelpers
    {
        public static string[] QueryAssembliesForTypes(string folderLocation, string interfaceType)
        {
            List<string> foundTypes = new List<string>();
            if (Directory.Exists(folderLocation))
            {
                foreach (string assemblyFile in Directory.GetFiles(folderLocation, "*.dll"))
                {
                    try
                    {
                        Assembly assemblyDetails = Assembly.LoadFrom(assemblyFile);
                        foreach (Type typeToCheck in assemblyDetails.GetExportedTypes())
                        {
                            if (typeToCheck.GetInterface(interfaceType) != null)
                            {
                                foundTypes.Add(typeToCheck.AssemblyQualifiedName);
                            }
                        }
                    }
                    catch (Exception) { }
                }
            }
            return foundTypes.ToArray();
        }
        public static ITransportExtension RetrieveExtension(string name)
        {
            object extensionInstance = RetrieveTypedObject(name);
            if (!(extensionInstance is ITransportExtension)) throw new CCTrayLibException("Extension '" + extensionInstance.GetType().Name + "'does not implement ITransportExtension");
            return extensionInstance as ITransportExtension;
        }
        public static IAuthenticationMode RetrieveAuthenticationMode(string name)
        {
            object extensionInstance = RetrieveTypedObject(name);
            if (!(extensionInstance is IAuthenticationMode)) throw new CCTrayLibException("Extension '" + extensionInstance.GetType().Name + "'does not implement IAuthenticationMode");
            return extensionInstance as IAuthenticationMode;
        }
        public static string CheckForDisplayName(string name)
        {
            Type extensionType = RetrieveType(name);
            ExtensionAttribute[] attributes = (ExtensionAttribute[])extensionType.GetCustomAttributes(typeof(ExtensionAttribute), true);
            string displayName = null;
            foreach (ExtensionAttribute attribute in attributes)
            {
                if (!string.IsNullOrEmpty(attribute.DisplayName))
                {
                    displayName = attribute.DisplayName;
                    break;
                }
            }
            if (displayName == null) displayName = name;
            return displayName;
        }
        private static Type RetrieveType(string name)
        {
            if (string.IsNullOrEmpty(name)) throw new ArgumentOutOfRangeException("name", "Extension name cannot be empty or null");
            Type extensionType = null;
            try
            {
                AppDomain.CurrentDomain.AssemblyResolve += new ResolveEventHandler(CurrentDomain_AssemblyResolve);
                extensionType = Type.GetType(name);
            }
            finally
            {
                AppDomain.CurrentDomain.AssemblyResolve -= new ResolveEventHandler(CurrentDomain_AssemblyResolve);
            }
            if (extensionType == null) throw new CCTrayLibException("Unable to find extension '" + name + "'");
            return extensionType;
        }
        private static object RetrieveTypedObject(string name)
        {
            Type extensionType = RetrieveType(name);
            object extensionInstance = Activator.CreateInstance(extensionType);
            if (extensionInstance == null) throw new CCTrayLibException("Unable to create extension '" + extensionType.Name + "'");
            return extensionInstance;
        }
        private static Assembly CurrentDomain_AssemblyResolve(object sender, ResolveEventArgs args)
        {
            int splitter = args.Name.IndexOf(',');
            string extensionPath = Path.Combine(Environment.CurrentDirectory, "Extensions");
            if (splitter >= 0)
            {
                extensionPath = Path.Combine(extensionPath, args.Name.Substring(0, splitter) + ".dll");
            }
            else
            {
                extensionPath = Path.Combine(extensionPath, args.Name + ".dll");
            }
            if (File.Exists(extensionPath))
            {
                return Assembly.LoadFrom(extensionPath);
            }
            else
            {
                return null;
            }
        }
    }
}
