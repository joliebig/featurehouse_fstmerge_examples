using System;
using System.IO;
namespace ThoughtWorks.CruiseControl.Core.Util
{
    public class IoService : IFileDirectoryDeleter
    {
        private readonly IExecutionEnvironment executionEnvironment = new ExecutionEnvironment();
        public void DeleteIncludingReadOnlyObjects(string path)
        {
            try
            {
                if (File.Exists(path))
                {
                    File.SetAttributes(path, FileAttributes.Normal);
                    File.Delete(path);
                }
                else if (Directory.Exists(path))
                {
                    var dirInfo = new DirectoryInfo(path);
                    SetReadOnlyRecursive(dirInfo);
                    dirInfo.Delete(true);
                }
                else
                {
                    Log.Warning("[IoService] File or directory not found: '{0}'", path);
                }
            }
            catch (PathTooLongException pathTooLongEx)
            {
                Log.Error("[IoService] Unable to delete path '{0}'.{1}{2}", path, Environment.NewLine, pathTooLongEx);
                if (executionEnvironment.IsRunningOnWindows)
                {
                    DeleteDirectoryWithLongPath(path);
                }
                else
                {
                    throw;
                }
            }
            catch (Exception ex)
            {
                Log.Error(ex);
                throw;
            }
        }
        static void DeleteDirectoryWithLongPath(string path)
        {
            Log.Info("[IoService] Try running 'cmd.exe /C RD /S /Q' to delete '{0}'", path);
            var executor = new ProcessExecutor();
            var processInfo = new ProcessInfo("cmd.exe",
                string.Concat("/C RD /S /Q ", StringUtil.AutoDoubleQuoteString(path)));
            var pr = executor.Execute(processInfo);
            if (pr.Failed)
                throw new CruiseControlException(string.Format("Unable to delete path '{0}'.", path));
        }
        static void SetReadOnlyRecursive(DirectoryInfo path)
        {
            foreach (var dirInfo in path.GetDirectories())
            {
                try
                {
                    dirInfo.Attributes = FileAttributes.Normal;
                }
                catch (Exception ex)
                {
                    Log.Error("[IoService] Unable to remove read-only attribute from_ '{0}'.", dirInfo.FullName);
                    Log.Error(ex);
                    throw;
                }
                SetReadOnlyRecursive(dirInfo);
            }
            foreach (var fileInfo in path.GetFiles())
            {
                try
                {
                    fileInfo.Attributes = FileAttributes.Normal;
                }
                catch (Exception ex)
                {
                    Log.Error("[IoService] Unable to remove read-only attribute from_ '{0}'.", fileInfo.FullName);
                    Log.Error(ex);
                    throw;
                }
            }
        }
    }
}
