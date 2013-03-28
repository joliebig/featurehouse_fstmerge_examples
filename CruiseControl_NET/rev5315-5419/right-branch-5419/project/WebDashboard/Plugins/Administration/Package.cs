using System;
using System.Collections.Generic;
using System.Text;
using System.IO;
using ICSharpCode.SharpZipLib.Zip;
using System.Xml.Serialization;
using System.Xml;
using System.Diagnostics;
using ThoughtWorks.CruiseControl.WebDashboard.Configuration;
using ThoughtWorks.CruiseControl.Core.Reporting.Dashboard.Navigation;
namespace ThoughtWorks.CruiseControl.WebDashboard.Plugins.Administration
{
    public class Package
        : IDisposable
    {
        private const int blockSize = 16384;
        private PackageManifest manifest;
        private List<string> files = new List<string>();
        private string tempFolder;
        public Package(Stream packageStream)
        {
            ExtractAllFiles(packageStream);
            string manifestFile = Path.Combine(tempFolder, "Manifest.xml");
            if (File.Exists(manifestFile))
            {
                XmlSerializer serialiser = new XmlSerializer(typeof(PackageManifest));
                using (Stream inputStream = File.OpenRead(manifestFile))
                {
                    manifest = serialiser.Deserialize(inputStream) as PackageManifest;
                    inputStream.Close();
                }
            }
        }
        public PackageManifest Manifest
        {
            get { return manifest; }
            set { manifest = value; }
        }
        public bool IsValid
        {
            get { return (manifest != null); }
        }
        public void Install(IPhysicalApplicationPathProvider physicalApplicationPathProvider)
        {
            if (!IsValid) throw new ApplicationException("This is not a valid package");
            FireMessage(TraceLevel.Info, "Starting installation");
            Stopwatch stopwatch = new Stopwatch();
            stopwatch.Start();
            try
            {
                FireMessage(TraceLevel.Verbose, "Installing files");
                foreach (FileLocation folder in manifest.FileLocations)
                {
                    CopyFiles(physicalApplicationPathProvider, folder);
                }
                FireMessage(TraceLevel.Verbose, "Updating configuration");
                UpdateConfigurationFile(physicalApplicationPathProvider, true);
            }
            catch (Exception error)
            {
                FireMessage(TraceLevel.Error,
                    "An unexpected error occurred during the installation: '{0}'",
                    error.Message);
            }
            finally
            {
                stopwatch.Stop();
                double time = stopwatch.ElapsedMilliseconds;
                FireMessage(TraceLevel.Info,
                    "Installation has completed ({0:0.00}s)",
                    time / 1000);
            }
        }
        public void Uninstall(IPhysicalApplicationPathProvider physicalApplicationPathProvider)
        {
            if (!IsValid) throw new ApplicationException("This is not a valid package");
            FireMessage(TraceLevel.Info, "Starting uninstallation");
            Stopwatch stopwatch = new Stopwatch();
            stopwatch.Start();
            try
            {
                FireMessage(TraceLevel.Verbose, "Removing files");
                foreach (FileLocation folder in manifest.FileLocations)
                {
                    RemoveFiles(physicalApplicationPathProvider, folder);
                }
                FireMessage(TraceLevel.Verbose, "Updating configuration");
                UpdateConfigurationFile(physicalApplicationPathProvider, false);
            }
            catch (Exception error)
            {
                FireMessage(TraceLevel.Error,
                    "An unexpected error occurred during the uninstallation: '{0}'",
                    error.Message);
            }
            finally
            {
                stopwatch.Stop();
                double time = stopwatch.ElapsedMilliseconds;
                FireMessage(TraceLevel.Info,
                    "Uninstallation has completed ({0:0.00}s)",
                    time / 1000);
            }
        }
        public void Dispose()
        {
            foreach (string fileName in files)
            {
                if (File.Exists(fileName)) File.Delete(fileName);
            }
        }
        public event EventHandler<PackageImportEventArgs> Message;
        private void FireMessage(TraceLevel level, string message, params object[] values)
        {
            if (message != null)
            {
                string fullMessage = string.Format(message, values);
                PackageImportEventArgs args = new PackageImportEventArgs(fullMessage, level);
                Message(this, args);
            }
        }
        private void ExtractAllFiles(Stream packageStream)
        {
            tempFolder = Path.GetTempPath();
            ZipInputStream zipIn = new ZipInputStream(packageStream);
            ZipEntry entry = zipIn.GetNextEntry();
            while (entry != null)
            {
                string fileName = Path.Combine(tempFolder, entry.Name);
                using (FileStream outputWriter = File.Create(fileName))
                {
                    byte[] data = new byte[blockSize];
                    int dataLength = zipIn.Read(data, 0, blockSize);
                    while (dataLength > 0)
                    {
                        outputWriter.Write(data, 0, dataLength);
                        dataLength = zipIn.Read(data, 0, blockSize);
                    }
                    outputWriter.Close();
                }
                files.Add(fileName);
                entry = zipIn.GetNextEntry();
            }
        }
        private void CopyFiles(IPhysicalApplicationPathProvider physicalApplicationPathProvider,
            FileLocation folder)
        {
            FireMessage(TraceLevel.Verbose, "Copying files to target '{0}'", folder.Location);
            string target = physicalApplicationPathProvider.GetFullPathFor(folder.Location);
            if (!Directory.Exists(target))
            {
                FireMessage(TraceLevel.Info, "Adding target folder '{0}'", folder.Location);
                Directory.CreateDirectory(target);
            }
            foreach (string file in folder.Files)
            {
                string source = Path.Combine(tempFolder, file);
                if (File.Exists(source))
                {
                    FireMessage(TraceLevel.Info, "Deploying file '{0}' to target '{1}'", file, folder.Location);
                    File.Copy(source, Path.Combine(target, file), true);
                }
                else
                {
                    FireMessage(TraceLevel.Warning, "Source file '{0}' does not exist in package", file);
                }
            }
        }
        private void RemoveFiles(IPhysicalApplicationPathProvider physicalApplicationPathProvider,
            FileLocation folder)
        {
            FireMessage(TraceLevel.Verbose, "Removing files from_ target '{0}'", folder.Location);
            string target = physicalApplicationPathProvider.GetFullPathFor(folder.Location);
            if (Directory.Exists(target))
            {
                foreach (string file in folder.Files)
                {
                    string targetFile = Path.Combine(target, file);
                    if (File.Exists(targetFile))
                    {
                        FireMessage(TraceLevel.Info, "Removing file '{0}' from_ target '{1}'", file, folder.Location);
                        File.Delete(targetFile);
                    }
                }
            }
        }
        private void UpdateConfigurationSetting(XmlDocument configXml, ConfigurationSetting setting, bool addSettings)
        {
            XmlElement element = configXml.SelectSingleNode(setting.Path) as XmlElement;
            if (element != null)
            {
                FireMessage(TraceLevel.Verbose, "Element found ('{0}'), updating", setting.Path);
                string xpath = string.IsNullOrEmpty(setting.Filter) ?
                    setting.Name :
                    string.Format("{0}[{1}]", setting.Name, setting.Filter);
                if (addSettings)
                {
                    XmlNode oldElement = element.SelectSingleNode(xpath);
                    if (oldElement != null)
                    {
                        FireMessage(TraceLevel.Info,
                            "Setting element '{0}' on element '{1}' to value '{2}'",
                            setting.Name,
                            setting.Path,
                            setting.Value);
                        oldElement.InnerText = setting.Value;
                        element = oldElement as XmlElement;
                    }
                    else
                    {
                        FireMessage(TraceLevel.Info,
                            "Adding new element '{0}' to element '{1}' with value '{2}'",
                            setting.Name,
                            setting.Path,
                            setting.Value);
                        XmlElement newElement = configXml.CreateElement(setting.Name);
                        newElement.InnerText = setting.Value;
                        element.AppendChild(newElement);
                        element = newElement;
                    }
                    foreach (ConfigurationAttribute attribute in setting.Attributes ?? new ConfigurationAttribute[0])
                    {
                        FireMessage(TraceLevel.Info,
                            "Setting attribute '{0}' on element '{1}' to value '{2}'",
                            attribute.Name,
                            setting.Name,
                            attribute.Value);
                        element.SetAttribute(attribute.Name, attribute.Value);
                    }
                }
                else
                {
                    XmlNode oldElement = element.SelectSingleNode(xpath);
                    if (oldElement != null)
                    {
                        FireMessage(TraceLevel.Info,
                            "Removing element '{0}' on element '{1}'",
                            setting.Name,
                            setting.Path);
                        oldElement.ParentNode.RemoveChild(oldElement);
                    }
                }
            }
            else
            {
                if (addSettings) FireMessage(TraceLevel.Warning, "Unable to find element '{0}'", setting.Path);
            }
        }
        private void UpdateConfigurationFile(IPhysicalApplicationPathProvider physicalApplicationPathProvider,
            bool addSettings)
        {
            string configFile = DashboardConfigurationLoader.CalculateDashboardConfigPath(physicalApplicationPathProvider);
            XmlDocument configXml = new XmlDocument();
            if (File.Exists(configFile))
            {
                FireMessage(TraceLevel.Verbose, "Loading configuration file");
                configXml.Load(configFile);
                bool configChanged = false;
                foreach (ConfigurationSetting setting in manifest.ConfigurationSettings)
                {
                    UpdateConfigurationSetting(configXml, setting, addSettings);
                    configChanged = true;
                }
                if (configChanged)
                {
                    FireMessage(TraceLevel.Verbose, "Saving configuration file");
                    XmlWriterSettings settings = new XmlWriterSettings();
                    settings.CloseOutput = true;
                    settings.ConformanceLevel = ConformanceLevel.Document;
                    settings.Indent = true;
                    using (XmlWriter writer = XmlWriter.Create(configFile, settings))
                    {
                        configXml.Save(writer);
                        writer.Close();
                    }
                }
            }
            else
            {
                FireMessage(TraceLevel.Warning, "Configuration file does not exist");
            }
        }
    }
}
