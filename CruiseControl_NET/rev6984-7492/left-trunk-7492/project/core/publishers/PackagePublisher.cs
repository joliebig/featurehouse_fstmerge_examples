namespace ThoughtWorks.CruiseControl.Core.Publishers
{
    using System;
    using System.Collections.Generic;
    using System.IO;
    using System.Threading;
    using System.Xml;
    using Exortech.NetReflector;
    using ICSharpCode.SharpZipLib.Zip;
    using ThoughtWorks.CruiseControl.Core.Tasks;
    using ThoughtWorks.CruiseControl.Core.Util;
    using ThoughtWorks.CruiseControl.Remote;
    [ReflectorType("package")]
    public class PackagePublisher
        : TaskBase
    {
        private int compressionLevel = 5;
        [ReflectorProperty("name", Required = true)]
        public string PackageName { get; set; }
        [ReflectorProperty("compression", Required = false)]
        public int CompressionLevel
        {
            get
            {
                return compressionLevel;
            }
            set
            {
                if ((value < 0) || (value > 9))
                {
                    throw new ArgumentOutOfRangeException("CompressionLevel");
                }
                compressionLevel = value;
            }
        }
        [ReflectorProperty("always", Required = false)]
        public bool AlwaysPackage { get; set; }
        [ReflectorProperty("flatten", Required = false)]
        public bool Flatten { get; set; }
        [ReflectorProperty("manifest", Required = false, InstanceTypeKey = "type")]
        public IManifestGenerator ManifestGenerator { get; set; }
        [ReflectorProperty("packageList", Required = true)]
        public IPackageItem[] PackageList { get; set; }
        [ReflectorProperty("outputDir", Required = false)]
        public string OutputDirectory { get; set; }
        protected override bool Execute(IIntegrationResult result)
        {
            if (AlwaysPackage || (result.Status == IntegrationStatus.Success))
            {
                var logMessage = string.Format("Building package '{0}'", PackageName);
                result.BuildProgressInformation.SignalStartRunTask(logMessage);
                Log.Info(logMessage);
                string tempFile = Path.GetTempFileName();
                try
                {
                    var fileList = new List<string>();
                    var zipStream = new ZipOutputStream(File.Create(tempFile));
                    zipStream.IsStreamOwner = true;
                    zipStream.UseZip64 = UseZip64.Off;
                    try
                    {
                        var packagedFiles = new List<string>();
                        zipStream.SetLevel(CompressionLevel == 0 ? 5 : CompressionLevel);
                        foreach (IPackageItem packageEntry in PackageList)
                        {
                            var packagedFileList = packageEntry.Package(result, zipStream);
                            if (packagedFileList != null)
                            {
                                packagedFiles.AddRange(packagedFileList);
                            }
                        }
                        if (ManifestGenerator != null)
                        {
                            Log.Debug("Generating manifest");
                            AddManifest(result, packagedFiles, zipStream);
                        }
                    }
                    finally
                    {
                        zipStream.Finish();
                        zipStream.Close();
                    }
                    string actualFile = MoveFile(result, tempFile);
                    Log.Debug("Adding to package list(s)");
                    string listFile = Path.Combine(result.Label, result.ProjectName + "-packages.xml");
                    AddToPackageList(result, listFile, actualFile, fileList.Count);
                }
                finally
                {
                    if (File.Exists(tempFile))
                    {
                        try
                        {
                            File.Delete(tempFile);
                        }
                        catch (IOException)
                        {
                        }
                    }
                }
            }
            if (result.Status == IntegrationStatus.Unknown) result.Status = IntegrationStatus.Success;
            return true;
        }
        protected override XmlNode UpgradeConfiguration(Version configVersion, XmlNode node)
        {
            node = base.UpgradeConfiguration(configVersion, node);
            if (configVersion < new Version(1, 6))
            {
                var listNode = node.OwnerDocument.CreateElement("packageList");
                XmlNode filesNode = null;
                foreach (XmlNode childNode in node.ChildNodes)
                {
                    if (childNode.Name == "files")
                    {
                        filesNode = childNode;
                        break;
                    }
                }
                if (filesNode != null)
                {
                    filesNode.ParentNode.ReplaceChild(listNode, filesNode);
                    foreach (XmlElement childNode in filesNode.ChildNodes)
                    {
                        var fileNode = node.OwnerDocument.CreateElement("packageFile");
                        fileNode.SetAttribute("sourceFile", childNode.InnerText);
                        listNode.AppendChild(fileNode);
                    }
                }
            }
            return node;
        }
        private void AddToPackageList(IIntegrationResult result, string listFile, string fileName, int numberOfFiles)
        {
            XmlDocument listXml = new XmlDocument();
            listFile = result.BaseFromArtifactsDirectory(listFile);
            if (File.Exists(listFile))
            {
                listXml.Load(listFile);
            }
            else
            {
                XmlElement rootElement = listXml.CreateElement("packages");
                listXml.AppendChild(rootElement);
            }
            XmlElement packageElement = listXml.SelectSingleNode(
                string.Format("/packages/package[@name='{0}']", PackageName)) as XmlElement;
            if (packageElement == null)
            {
                packageElement = listXml.CreateElement("package");
                listXml.DocumentElement.AppendChild(packageElement);
                packageElement.SetAttribute("name", PackageName);
            }
            var packageFile = new FileInfo(fileName);
            packageElement.SetAttribute("file", fileName);
            packageElement.SetAttribute("label", result.Label);
            packageElement.SetAttribute("time", DateTime.Now.ToString("s"));
            packageElement.SetAttribute("files", numberOfFiles.ToString());
            packageElement.SetAttribute("size", packageFile.Length.ToString());
            var listDir = Path.GetDirectoryName(listFile);
            if (!Directory.Exists(listDir))
            {
                Directory.CreateDirectory(listDir);
            }
            listXml.Save(listFile);
        }
        private string MoveFile(IIntegrationResult result, string tempFile)
        {
            string actualFile = Path.Combine(result.Label, PackageName);
            if (!actualFile.EndsWith(".zip", StringComparison.InvariantCultureIgnoreCase)) actualFile += ".zip";
            actualFile = result.BaseFromArtifactsDirectory(actualFile);
            if (File.Exists(actualFile)) DeleteFileWithRetry(actualFile);
            string actualFolder = Path.GetDirectoryName(actualFile);
            if (!Directory.Exists(actualFolder)) Directory.CreateDirectory(actualFolder);
            File.Move(tempFile, actualFile);
            if (!string.IsNullOrEmpty(OutputDirectory))
            {
                var basePath = OutputDirectory;
                if (!Path.IsPathRooted(basePath)) basePath = Path.Combine(result.ArtifactDirectory, basePath);
                Log.Info(string.Format("Copying file to '{0}'", basePath));
                File.Copy(actualFile, Path.Combine(basePath, PackageName), true);
            }
            return actualFile;
        }
        private void DeleteFileWithRetry(string actualFile)
        {
            var retryLoop = 3;
            while (retryLoop > 0)
            {
                try
                {
                    File.Delete(actualFile);
                    retryLoop = 0;
                }
                catch (IOException)
                {
                    if (retryLoop-- > 0)
                    {
                        Log.Warning(
                            string.Format(
                                "Unable to delete file '{0}', delaying before retry",
                                actualFile));
                        Thread.Sleep(1000);
                    }
                    else
                    {
                        throw;
                    }
                }
            }
        }
        private void AddManifest(IIntegrationResult result, List<string> packagedFiles, ZipOutputStream zipStream)
        {
            XmlDocument manifest = ManifestGenerator.Generate(result, packagedFiles.ToArray());
            ZipEntry entry = new ZipEntry("manifest.xml");
            zipStream.PutNextEntry(entry);
            manifest.Save(zipStream);
            zipStream.CloseEntry();
        }
    }
}
