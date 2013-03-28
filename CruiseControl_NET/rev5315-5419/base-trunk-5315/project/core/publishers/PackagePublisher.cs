using System;
using System.Collections.Generic;
using System.IO;
using System.Xml;
using Exortech.NetReflector;
using ICSharpCode.SharpZipLib.Zip;
using ThoughtWorks.CruiseControl.Core.Util;
using ThoughtWorks.CruiseControl.Remote;
namespace ThoughtWorks.CruiseControl.Core.Publishers
{
    [ReflectorType("package")]
    public class PackagePublisher
        : ITask
    {
        private int compressionLevel = 5;
        private bool singleInstance;
        private bool alwaysPackage;
        private bool flatten;
        private IManifestGenerator manifestGenerator;
        private string baseDirectory;
        private string[] files = {};
        private string name;
        [ReflectorProperty("name")]
        public string Name
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
        [ReflectorProperty("single", Required = false)]
        public bool SingleInstance
        {
            get { return singleInstance; }
            set { singleInstance = value; }
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
        [ReflectorArray("files")]
        public string[] Files
        {
            get { return files; }
            set { files = value; }
        }
        public void Run(IIntegrationResult result)
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
                    string listFile = result.ProjectName + "-packages.xml";
                    AddToPackageList(result, listFile, actualFile, fileList.Count);
                    if (!singleInstance)
                    {
                        listFile = Path.Combine(result.Label, listFile);
                        AddToPackageList(result, listFile, actualFile, fileList.Count);
                    }
                }
                finally
                {
                    if (File.Exists(tempFile)) File.Delete(tempFile);
                }
            }
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
            FileInfo packageFile = new FileInfo(fileName);
            packageElement.SetAttribute("file", fileName);
            packageElement.SetAttribute("label", result.Label);
            packageElement.SetAttribute("time", DateTime.Now.ToString("s"));
            packageElement.SetAttribute("files", numberOfFiles.ToString());
            packageElement.SetAttribute("size", packageFile.Length.ToString());
            string folderName = Path.GetDirectoryName(listFile);
            if (!Directory.Exists(folderName)) Directory.CreateDirectory(folderName);
            listXml.Save(listFile);
        }
        private string MoveFile(IIntegrationResult result, string tempFile)
        {
            string actualFile = name;
            if (!actualFile.EndsWith(".zip", StringComparison.InvariantCultureIgnoreCase)) actualFile += ".zip";
            if (!singleInstance) actualFile = Path.Combine(result.Label, actualFile);
            actualFile = result.BaseFromArtifactsDirectory(actualFile);
            if (File.Exists(actualFile)) File.Delete(actualFile);
            string actualFolder = Path.GetDirectoryName(actualFile);
            if (!Directory.Exists(actualFolder)) Directory.CreateDirectory(actualFolder);
            File.Move(tempFile, actualFile);
            return actualFile;
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
            string baseFolder = string.IsNullOrEmpty(baseDirectory) ? result.WorkingDirectory : baseDirectory;
            string fullName = Path.IsPathRooted(file) ? file : Path.Combine(baseFolder, file);
            FileInfo fileInfo = new FileInfo(fullName);
            if (fileInfo.Exists)
            {
                string fileName = file;
                if (flatten)
                {
                    fileName = fileInfo.Name;
                }
                else
                {
                    if (fileName.StartsWith(baseFolder, StringComparison.InvariantCultureIgnoreCase))
                    {
                        fileName = fileName.Substring(baseFolder.Length);
                    }
                    if (fileName.StartsWith(Path.DirectorySeparatorChar + string.Empty)) fileName = fileName.Substring(1);
                }
                ZipEntry entry = new ZipEntry(fileName);
                zipStream.PutNextEntry(entry);
                byte[] buffer = new byte[8182];
                FileStream inputStream = fileInfo.OpenRead();
                try
                {
                    int dataLength = 1;
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
