using System;
using System.Collections.Generic;
using System.Text;
using Exortech.NetReflector;
using ThoughtWorks.CruiseControl.Core.Util;
namespace ThoughtWorks.CruiseControl.Core.Tasks
{
    [ReflectorType("ftp")]
    public class FtpTask : TaskBase
    {
        public enum FtpAction
        {
            UploadFolder,
            DownloadFolder
        }
        [ReflectorProperty("serverName", Required = true)]
        public string ServerName = string.Empty;
        [ReflectorProperty("userName", Required = true)]
        public string UserName = string.Empty;
        [ReflectorProperty("password", Required = true)]
        public string Password = string.Empty;
        [ReflectorProperty("useActiveConnectionMode", Required = false)]
        public bool UseActiveConnectionMode = true;
        [ReflectorProperty("action", Required = false)]
        public FtpAction Action = FtpAction.DownloadFolder;
        [ReflectorProperty("ftpFolderName", Required = true)]
        public string FtpFolderName = string.Empty;
        [ReflectorProperty("localFolderName", Required = true)]
        public string LocalFolderName = string.Empty;
        [ReflectorProperty("recursiveCopy", Required = false)]
        public bool RecursiveCopy = true;
        protected override bool Execute(IIntegrationResult result)
        {
            result.BuildProgressInformation.SignalStartRunTask(!string.IsNullOrEmpty(Description) ? Description : GetDescription());
            string remoteFolder = FtpFolderName;
            FtpLib ftp = new FtpLib(this,result.BuildProgressInformation);
            try
            {
                ftp.LogIn(ServerName, UserName, Password, UseActiveConnectionMode);
                if (!FtpFolderName.StartsWith("/"))
                {
                    remoteFolder = System.IO.Path.Combine(ftp.CurrentWorkingFolder(),FtpFolderName);
                }
                if (Action == FtpAction.UploadFolder)
                {
                    Log.Debug("Uploading {0} to {1}, recursive : {2}", LocalFolderName, remoteFolder, RecursiveCopy);
                    ftp.UploadFolder(remoteFolder, LocalFolderName, RecursiveCopy);
                }
                if (Action == FtpAction.DownloadFolder)
                {
                    Log.Debug("Downloading {0} to {1}, recursive : {2}", remoteFolder, LocalFolderName, RecursiveCopy);
                    ftp.DownloadFolder(LocalFolderName, remoteFolder, RecursiveCopy);
                }
            }
            catch (Exception ex)
            {
                Log.Error(ex);
                if (ftp != null)
                {
                    try
                    {
                        if (ftp.IsConnected()) ftp.DisConnect();
                    }
                    catch { }
                }
                Log.Info("throwing");
                throw;
            }
            return true;
        }
        private string GetDescription()
        {
            if (Action == FtpAction.DownloadFolder)
            {
                return string.Concat("Downloading ", FtpFolderName, " to ", LocalFolderName);
            }
            return string.Concat("Uploading ", LocalFolderName, " to ", FtpFolderName);
        }
    }
}
