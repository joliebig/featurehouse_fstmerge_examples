using System;
using System.Collections.Generic;
using System.IO;
using System.Xml;
using Exortech.NetReflector;
using ICSharpCode.SharpZipLib.Zip;
using ThoughtWorks.CruiseControl.Core.Util;
using ThoughtWorks.CruiseControl.Remote;
using ThoughtWorks.CruiseControl.Core.Tasks;
using System.Threading;
namespace ThoughtWorks.CruiseControl.Core.Publishers
{
    [ReflectorType("package")]
    public class PackagePublisher
        : TaskBase
    {
        private int compressionLevel = 5;
        private bool alwaysPackage;
        private bool flatten;
        private IManifestGenerator manifestGenerator;
        private string baseDirectory;
        private string[] files = {};
        private string name;
        [ReflectorProperty("name")]
        public string PackageName
        {
            get { return name; }
            set { name = value; }
        }
        [ReflectorProperty("compression", Required = false)]
        public int CompressionLevel
        {
            get { return compressionLevel; }
            set
            {
                if ((value < 0) || (value > 9)) throw new ArgumentOutOfRangeException("CompressionLevel");
                compressionLevel = value;
            }
        }
        [ReflectorProperty("always", Required = false)]
        public bool AlwaysPackage
        {
            get { return alwaysPackage; }
            set { alwaysPackage = value; }
        }
        [ReflectorProperty("flatten", Required = false)]
        public bool Flatten
        {
            get { return flatten; }
            set { flatten = value; }
        }
        [ReflectorProperty("baseDirectory", Required = false)]
        public string BaseDirectory
        {
            get { return baseDirectory; }
            set { baseDirectory = value; }
        }
        [ReflectorProperty("manifest", Required = false, InstanceTypeKey = "type")]
        public IManifestGenerator ManifestGenerator
        {
            get { return manifestGenerator; }
            set { manifestGenerator = value; }
        }
        [ReflectorProperty("files")]
        public string[] Files
        {
            get { return files; }
            set { files = value; }
        }
        [ReflectorProperty("outputDir", Required = false)]
        public string OutputDirectory { get; set; }
        protected override bool Execute(IIntegrationResult result)
        {
            if (alwaysPackage || (result.Status == IntegrationStatus.Success))
            {
                string logMessage = string.Format("Building package '{0}'", name);
                result.BuildProgressInformation.SignalStartRunTask(logMessage);
                Log.Info(logMessage);
                string tempFile = Path.GetTempFileName();
                try
                {
                    List<string> fileList = new List<string>();
                    ZipOutputStream zipStream = new ZipOutputStream(File.Create(tempFile));
                    zipStream.IsStreamOwner = true;
                    zipStream.UseZip64 = UseZip64.Off;
                    try
                    {
                        zipStream.SetLevel(compressionLevel);
                        List<string> packagedFiles = new List<string>();
                        fileList = GenerateFileList(result);
                        Log.Debug(string.Format("Compressing {0} file(s)", fileList.Count));
                        foreach (string file in fileList)
                        {
                            string fileInfo = PackageFile(result, file, zipStream);
                            if (fileInfo != null) packagedFiles.Add(fileInfo);
                        }
                        if (manifestGenerator != null)
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
                    if (File.Exists(tempFile)) File.Delete(tempFile);
                }
            }
            if (result.Status == IntegrationStatus.Unknown) result.Status = IntegrationStatus.Success;
            return true;
        }
        private List<string> GenerateFileList(IIntegrationResult result)
        {
            List<string> fileList = new List<string>();
            string allDirsWildcard = Path.DirectorySeparatorChar + "**" + Path.DirectorySeparatorChar;
            foreach (string fileName in files)
            {
                if (fileName.Contains("*") || fileName.Contains("?"))
                {
                    List<string> possibilities = new List<string>();
                    string actualPath = EnsureFileIsRooted(result, fileName);
                    if (actualPath.Contains(allDirsWildcard))
                    {
                        int position = actualPath.IndexOf(allDirsWildcard);
                        string path = actualPath.Substring(0, position);
                        string pattern = actualPath.Substring(position + 4);
                        possibilities.AddRange(Directory.GetFiles(path, pattern, SearchOption.AllDirectories));
                    }
                    else
                    {
                        int position = actualPath.IndexOfAny(new char[] { '*', '?' });
                        position = actualPath.LastIndexOf(Path.DirectorySeparatorChar, position);
                        string path = actualPath.Substring(0, position);
                        string pattern = actualPath.Substring(position + 1);
                        possibilities.AddRange(Directory.GetFiles(path, pattern, SearchOption.TopDirectoryOnly));
                    }
                    foreach (string possibility in possibilities)
                    {
                        if (!fileList.Contains(possibility))
                        {
                            if (PathUtils.MatchPath(actualPath, possibility, false))
                            {
                                fileList.Add(possibility);
                            }
                        }
                    }
                }
                else
                {
                    string actualPath = EnsureFileIsRooted(result, fileName);
                    if (!fileList.Contains(actualPath))
                    {
                        fileList.Add(actualPath);
                    }
                }
            }
            return fileList;
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
                string.Format("/packages/package[@name='{0}']", name)) as XmlElement;
            if (packageElement == null)
            {
                packageElement = listXml.CreateElement("package");
                listXml.DocumentElement.AppendChild(packageElement);
                packageElement.SetAttribute("name", name);
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
            string actualFile = Path.Combine(result.Label, name);
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
                File.Copy(actualFile, Path.Combine(basePath, Name), true);
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
            XmlDocument manifest = manifestGenerator.Generate(result, packagedFiles.ToArray());
            ZipEntry entry = new ZipEntry("manifest.xml");
            zipStream.PutNextEntry(entry);
            manifest.Save(zipStream);
            zipStream.CloseEntry();
        }
        private string PackageFile(IIntegrationResult result, string file, ZipOutputStream zipStream)
        {
            var baseFolder = string.IsNullOrEmpty(baseDirectory) ? result.WorkingDirectory : baseDirectory;
            if (!Path.IsPathRooted(baseFolder))
            {
                baseFolder = result.BaseFromWorkingDirectory(baseFolder);
            }
            var fullName = Path.IsPathRooted(file) ? file : result.BaseFromWorkingDirectory(file);
            var fileInfo = new FileInfo(fullName);
            if (fileInfo.Exists)
            {
                var fileName = file;
                if (flatten)
                {
                    fileName = fileInfo.Name;
                }
                else
                {
                    if (fullName.StartsWith(baseFolder, StringComparison.InvariantCultureIgnoreCase))
                    {
                        fileName = fullName.Substring(baseFolder.Length);
                    }
                    if (fileName.StartsWith(Path.DirectorySeparatorChar + string.Empty)) fileName = fileName.Substring(1);
                }
                var entry = new ZipEntry(ZipEntry.CleanName(fileName));
                entry.Size = fileInfo.Length;
                zipStream.PutNextEntry(entry);
                var buffer = new byte[8182];
                var inputStream = fileInfo.OpenRead();
                try
                {
                    var dataLength = 1;
                    while (dataLength > 0)
                    {
                        dataLength = inputStream.Read(buffer, 0, buffer.Length);
                        zipStream.Write(buffer, 0, dataLength);
                    }
                }
                finally
                {
                    inputStream.Close();
                }
                zipStream.CloseEntry();
                return fileName;
            }
            else
            {
                return null;
            }
        }
        private string EnsureFileIsRooted(IIntegrationResult result, string fileName)
        {
            string actualPath = fileName;
            string baseFolder = string.IsNullOrEmpty(baseDirectory) ? result.WorkingDirectory : baseDirectory;
            if (!Path.IsPathRooted(actualPath)) actualPath = Path.Combine(baseFolder, actualPath);
            return actualPath;
        }
    }
}
