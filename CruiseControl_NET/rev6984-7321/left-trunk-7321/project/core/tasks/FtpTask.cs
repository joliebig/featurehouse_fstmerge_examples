namespace ThoughtWorks.CruiseControl.Core.Tasks
{
    using System;
    using Exortech.NetReflector;
    using ThoughtWorks.CruiseControl.Core.Util;
    [ReflectorType("ftp")]
    public class FtpTask : TaskBase
    {
        public enum FtpAction
        {
            UploadFolder,
            DownloadFolder
        }
        public FtpTask()
        {
            this.ServerName = string.Empty;
            this.UserName = string.Empty;
            this.Password = string.Empty;
            this.UseActiveConnectionMode = true;
            this.Action = FtpAction.DownloadFolder;
            this.FtpFolderName = string.Empty;
            this.LocalFolderName = string.Empty;
            this.RecursiveCopy = true;
        }
        [ReflectorProperty("serverName", Required = true)]
        public string ServerName { get; set; }
        [ReflectorProperty("userName", Required = true)]
        public string UserName { get; set; }
        [ReflectorProperty("password", Required = true)]
        public string Password { get; set; }
        [ReflectorProperty("useActiveConnectionMode", Required = false)]
        public bool UseActiveConnectionMode { get; set; }
        [ReflectorProperty("action", Required = false)]
        public FtpAction Action { get; set; }
        [ReflectorProperty("ftpFolderName", Required = true)]
        public string FtpFolderName { get; set; }
        [ReflectorProperty("localFolderName", Required = true)]
        public string LocalFolderName { get; set; }
        [ReflectorProperty("recursiveCopy", Required = false)]
        public bool RecursiveCopy { get; set; }
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
