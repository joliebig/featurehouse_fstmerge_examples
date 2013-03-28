namespace ThoughtWorks.CruiseControl.Core.Publishers
{
    using System.Collections.Generic;
    using System.IO;
    using Exortech.NetReflector;
    using ICSharpCode.SharpZipLib.Zip;
    using ThoughtWorks.CruiseControl.Core.Util;
    [ReflectorType("packageFile")]
    public class PackageFile
        : IPackageItem
    {
        public PackageFile()
        {
        }
        public PackageFile(string fileName)
        {
            this.SourceFile = fileName;
        }
        [ReflectorProperty("sourceFile", Required = true)]
        public string SourceFile { get; set; }
        [ReflectorProperty("targetFileName", Required = false)]
        public string TargetFileName { get; set; }
        [ReflectorProperty("targetFolder", Required = false)]
        public string TargetFolder { get; set; }
        public IFileSystem FileSystem { get; set; }
        public IEnumerable<string> Package(IIntegrationResult result, ZipOutputStream zipStream)
        {
            var fileSystem = this.FileSystem ?? new SystemIoFileSystem();
            var baseFolder = result.WorkingDirectory;
            var files = this.GenerateFileList(result, fileSystem);
            var actualFiles = new List<string>();
            foreach (var fullName in files)
            {
                var fileInfo = new FileInfo(fullName);
                if (fileSystem.FileExists(fullName))
                {
                    var targetFileName = string.IsNullOrEmpty(this.TargetFileName) ? fileInfo.Name : this.TargetFileName;
                    var targetPath = this.TargetFolder;
                    if (string.IsNullOrEmpty(targetPath))
                    {
                        if (fileInfo.DirectoryName.StartsWith(baseFolder))
                        {
                            targetPath = fileInfo.DirectoryName.Substring(baseFolder.Length);
                        }
                        else
                        {
                            targetPath = fileInfo.DirectoryName;
                        }
                    }
                    if (targetPath.StartsWith(Path.DirectorySeparatorChar + string.Empty))
                    {
                        targetPath = targetPath.Substring(1);
                    }
                    var entry = new ZipEntry(ZipEntry.CleanName(Path.Combine(targetPath, targetFileName)));
                    entry.Size = fileSystem.GetFileLength(fullName);
                    zipStream.PutNextEntry(entry);
                    var buffer = new byte[8182];
                    var inputStream = fileSystem.OpenInputStream(fullName);
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
                    actualFiles.Add(fullName);
                }
            }
            return actualFiles;
        }
        private List<string> GenerateFileList(IIntegrationResult result, IFileSystem fileSystem)
        {
            var fileList = new List<string>();
            var allDirsWildcard = Path.DirectorySeparatorChar + "**" + Path.DirectorySeparatorChar;
            if (this.SourceFile.Contains("*") || this.SourceFile.Contains("?"))
            {
                var possibilities = new List<string>();
                var actualPath = Path.IsPathRooted(this.SourceFile) ? this.SourceFile : result.BaseFromWorkingDirectory(this.SourceFile);
                if (actualPath.Contains(allDirsWildcard))
                {
                    var position = actualPath.IndexOf(allDirsWildcard);
                    var path = actualPath.Substring(0, position);
                    var pattern = actualPath.Substring(position + 4);
                    possibilities.AddRange(fileSystem.GetFilesInDirectory(path, pattern, SearchOption.AllDirectories));
                }
                else
                {
                    var position = actualPath.IndexOfAny(new char[] { '*', '?' });
                    position = actualPath.LastIndexOf(Path.DirectorySeparatorChar, position);
                    var path = actualPath.Substring(0, position);
                    var pattern = actualPath.Substring(position + 1);
                    possibilities.AddRange(fileSystem.GetFilesInDirectory(path, pattern, SearchOption.TopDirectoryOnly));
                }
                foreach (var possibility in possibilities)
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
                string actualPath = Path.IsPathRooted(this.SourceFile) ? this.SourceFile : result.BaseFromWorkingDirectory(this.SourceFile);
                if (!fileList.Contains(actualPath))
                {
                    fileList.Add(actualPath);
                }
            }
            return fileList;
        }
    }
}
