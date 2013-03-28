using System;
using System.Collections.Generic;
using System.Text;
using System.IO;
using ThoughtWorks.CruiseControl.Core.Reporting.Dashboard.Navigation;
using System.Xml;
using ThoughtWorks.CruiseControl.WebDashboard.IO;
namespace ThoughtWorks.CruiseControl.WebDashboard.Plugins.Administration
{
    public class PackageManager
    {
        private const int blockSize = 16384;
        public PackageManager()
  {
        }
        public PackageManifest StorePackage(string fileName, Stream stream)
        {
            FileInfo packageDetails = new FileInfo(fileName);
            string packagePath = Path.Combine(ProgramDataFolder.MapPath("Packages"),
                packageDetails.Name);
            packageDetails = new FileInfo(packagePath);
            if (!packageDetails.Directory.Exists) packageDetails.Directory.Create();
            if (packageDetails.Exists) packageDetails.Delete();
            SaveToFile(stream, packageDetails.FullName);
            bool delete = false;
            PackageManifest manifest = null;
            using (Stream inputStream = File.OpenRead(packagePath))
            {
                using (Package newPackage = new Package(inputStream))
                {
                    if (!newPackage.IsValid)
                    {
                        delete = true;
                    }
                    else
                    {
                        manifest = newPackage.Manifest;
                        manifest.FileName = packageDetails.Name;
                    }
                }
            }
            if (manifest != null)
            {
                XmlDocument packageList = LoadPackageList();
                UpdatePackagesList(manifest, packageList, true);
                SavePackageList(packageList);
            }
            if (delete && packageDetails.Exists) packageDetails.Delete();
            return manifest;
        }
        public List<PackageImportEventArgs> InstallPackage(string fileName)
        {
            string packagePath = Path.Combine(ProgramDataFolder.MapPath("Packages"),
                fileName);
            FileInfo packageDetails = new FileInfo(packagePath);
            List<PackageImportEventArgs> events = null;
            if (packageDetails.Exists)
            {
                events = new List<PackageImportEventArgs>();
                using (Stream packageStream = File.OpenRead(packagePath))
                {
                    using (Package package = new Package(packageStream))
                    {
                        package.Message += delegate(object source, PackageImportEventArgs args)
                        {
                            events.Add(args);
                        };
                        package.Install();
                        XmlDocument packageList = LoadPackageList();
                        package.Manifest.IsInstalled = true;
                        UpdatePackagesList(package.Manifest, packageList, true);
                        SavePackageList(packageList);
                    }
                }
            }
            return events;
        }
        public string RemovePackage(string fileName)
        {
            FileInfo packageDetails = new FileInfo(fileName);
            string packagePath = Path.Combine(ProgramDataFolder.MapPath("Packages"),
                packageDetails.Name);
            packageDetails = new FileInfo(packagePath);
            string packageName = null;
            if (packageDetails.Directory.Exists)
            {
                PackageManifest manifest = null;
                using (Stream inputStream = File.OpenRead(packagePath))
                {
                    using (Package newPackage = new Package(inputStream))
                    {
                        if (newPackage.IsValid)
                        {
                            manifest = newPackage.Manifest;
                            manifest.FileName = packageDetails.Name;
                        }
                    }
                }
                if (manifest != null)
                {
                    XmlDocument packageList = LoadPackageList();
                    UpdatePackagesList(manifest, packageList, false);
                    SavePackageList(packageList);
                    packageName = manifest.Name;
                }
                packageDetails.Delete();
            }
            return packageName;
        }
        public List<PackageImportEventArgs> UninstallPackage(string fileName)
        {
            string packagePath = Path.Combine(ProgramDataFolder.MapPath("Packages"),
                fileName);
            FileInfo packageDetails = new FileInfo(packagePath);
            List<PackageImportEventArgs> events = null;
            if (packageDetails.Exists)
            {
                events = new List<PackageImportEventArgs>();
                using (Stream packageStream = File.OpenRead(packagePath))
                {
                    using (Package package = new Package(packageStream))
                    {
                        package.Message += delegate(object source, PackageImportEventArgs args)
                        {
                            events.Add(args);
                        };
                        package.Uninstall();
                        XmlDocument packageList = LoadPackageList();
                        package.Manifest.IsInstalled = false;
                        UpdatePackagesList(package.Manifest, packageList, true);
                        SavePackageList(packageList);
                    }
                }
            }
            return events;
        }
        public virtual List<PackageManifest> ListPackages()
        {
            List<PackageManifest> packages = new List<PackageManifest>();
            XmlDocument document = LoadPackageList();
            foreach (XmlElement packageElement in document.SelectNodes("/packages/package"))
            {
                PackageManifest manifest = new PackageManifest();
                manifest.Name = packageElement.GetAttribute("name");
                manifest.Description = packageElement.GetAttribute("description");
                manifest.Type = (PackageType)Enum.Parse(typeof(PackageType), packageElement.GetAttribute("type"));
                manifest.FileName = packageElement.GetAttribute("file");
                manifest.IsInstalled = (packageElement.GetAttribute("installed") == "yes");
                packages.Add(manifest);
            }
            return packages;
        }
        private void SaveToFile(Stream stream, string fileName)
        {
            using (FileStream outputWriter = File.Create(fileName))
            {
                byte[] data = new byte[blockSize];
                int dataLength = stream.Read(data, 0, blockSize);
                while (dataLength > 0)
                {
                    outputWriter.Write(data, 0, dataLength);
                    dataLength = stream.Read(data, 0, blockSize);
                }
                outputWriter.Close();
            }
        }
        private XmlDocument LoadPackageList()
        {
            string packagePath = Path.Combine(ProgramDataFolder.MapPath("Packages"),
                "packages.xml");
            FileInfo listDetails = new FileInfo(packagePath);
            XmlDocument document = new XmlDocument();
            if (listDetails.Exists)
            {
                document.Load(listDetails.FullName);
            }
            else
            {
                document.AppendChild(
                    document.CreateElement("packages"));
            }
            return document;
        }
        private void UpdatePackagesList(PackageManifest manifest, XmlDocument packageList, bool addPackage)
        {
            XmlElement packageElement = packageList.SelectSingleNode(
                string.Format("/packages/package[@name='{0}']",
                manifest.Name)) as XmlElement;
            if (packageElement == null)
            {
                if (addPackage)
                {
                    packageElement = packageList.CreateElement("package");
                    packageElement.SetAttribute("name", manifest.Name);
                    packageElement.SetAttribute("description", manifest.Description);
                    packageElement.SetAttribute("type", manifest.Type.ToString());
                    packageElement.SetAttribute("file", manifest.FileName);
                    packageList.DocumentElement.AppendChild(packageElement);
                }
            }
            else
            {
                if (!addPackage)
                {
                    packageElement.ParentNode.RemoveChild(packageElement);
                }
            }
            if (addPackage)
            {
                packageElement.SetAttribute("installed", manifest.IsInstalled ? "yes" : "no");
            }
        }
        private void SavePackageList(XmlDocument packageList)
        {
            string packagePath = Path.Combine(ProgramDataFolder.MapPath("Packages"),
                "packages.xml");
            XmlWriterSettings settings = new XmlWriterSettings();
            settings.Indent = true;
            settings.OmitXmlDeclaration = true;
            try
            {
                using (XmlWriter writer = XmlWriter.Create(packagePath, settings))
                {
                    packageList.Save(writer);
                    writer.Close();
                    writer.Close();
                }
            }
            catch (UnauthorizedAccessException)
            {
                throw new UnauthorizedAccessException("Unable to access path '" + packagePath + "' - make sure account '" + Environment.UserDomainName + "\\" + Environment.UserName + "' has write access to this path");
            }
        }
    }
}
