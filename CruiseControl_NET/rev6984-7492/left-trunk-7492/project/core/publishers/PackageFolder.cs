namespace ThoughtWorks.CruiseControl.Core.Publishers
{
    using System;
    using System.Collections.Generic;
    using System.IO;
    using Exortech.NetReflector;
    using ICSharpCode.SharpZipLib.Zip;
    [ReflectorType("packageFolder")]
    public class PackageFolder
        : IPackageItem
    {
        [ReflectorProperty("sourceFolder", Required = true)]
        public string SourceFolder
        {
            get;
            set;
        }
        [ReflectorProperty("fileFilter", Required = true)]
        public string FileFilter
        {
            get;
            set;
        }
        [ReflectorProperty("targetFolder", Required = false)]
        public string TargetFolder
        {
            get;
            set;
        }
        [ReflectorProperty("includeSubFolders", Required = false)]
        public bool IncludeSubFolders
        {
            get;
            set;
        }
        [ReflectorProperty("flatten", Required = false)]
        public bool Flatten
        {
            get;
            set;
        }
        public IEnumerable<string> Package(IIntegrationResult result, ZipOutputStream zipStream)
        {
            var filesAdded = new List<string>();
            var baseFolder = result.WorkingDirectory;
            var fullName = Path.IsPathRooted(this.SourceFolder) ? this.SourceFolder : result.BaseFromWorkingDirectory(this.SourceFolder);
            var folderInfo = new DirectoryInfo(fullName);
            if (folderInfo.Exists)
            {
                foreach (FileInfo file in folderInfo.GetFiles(string.IsNullOrEmpty(this.FileFilter) ? "*.*" : this.FileFilter, this.IncludeSubFolders ? SearchOption.AllDirectories : SearchOption.TopDirectoryOnly))
                {
                    string fileName = file.FullName;
                    if (this.Flatten)
                    {
                        fileName = file.Name;
                    }
                    else if (!string.IsNullOrEmpty(this.TargetFolder))
                    {
                        fileName = Path.Combine(this.TargetFolder, file.Name);
                    }
                    else
                    {
                        if (fileName.StartsWith(baseFolder, StringComparison.InvariantCultureIgnoreCase))
                            fileName = fileName.Substring(baseFolder.Length);
                    }
                    if (fileName.StartsWith(Path.DirectorySeparatorChar + string.Empty)) fileName = fileName.Substring(1);
                    var entry = new ZipEntry(ZipEntry.CleanName(fileName));
                    entry.Size = file.Length;
                    zipStream.PutNextEntry(entry);
                    var buffer = new byte[8182];
                    var inputStream = file.OpenRead();
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
                    filesAdded.Add(fileName);
                }
            }
            return filesAdded;
        }
    }
}
