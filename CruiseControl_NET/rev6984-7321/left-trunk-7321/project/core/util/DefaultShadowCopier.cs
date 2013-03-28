using System;
using System.Collections.Generic;
using System.Text;
using System.IO;
namespace ThoughtWorks.CruiseControl.Core.Util
{
    public class DefaultShadowCopier
        : IShadowCopier
    {
        private static ShadowStore store = new ShadowStore();
        public virtual string RetrieveFilePath(string fileName)
        {
            return store.CopyFile(fileName);
        }
        private class ShadowStore
            : IDisposable
        {
            private readonly string tempPath = Path.Combine(Path.GetTempPath(), Guid.NewGuid().ToString());
            private List<string> copiedFiles = new List<string>();
            private readonly object lockObject = new object();
            public ShadowStore()
            {
                if (!Directory.Exists(tempPath))
                    Directory.CreateDirectory(tempPath);
            }
            public string CopyFile(string fileName)
            {
                var filePath = Path.Combine(tempPath, fileName);
                if (!File.Exists(filePath))
                {
                    var sourceFile = Path.Combine(AppDomain.CurrentDomain.BaseDirectory, fileName);
                    if (File.Exists(sourceFile))
                    {
                        lock (lockObject)
                        {
                            if (!File.Exists(filePath))
                            {
                                File.Copy(sourceFile, filePath);
                                copiedFiles.Add(filePath);
                            }
                        }
                    }
                    else
                    {
                        filePath = null;
                    }
                }
                return filePath;
            }
            public void Dispose()
            {
                lock (lockObject)
                {
                    foreach (var file in copiedFiles)
                    {
                        File.Delete(file);
                    }
                    copiedFiles.Clear();
                }
            }
        }
    }
}
