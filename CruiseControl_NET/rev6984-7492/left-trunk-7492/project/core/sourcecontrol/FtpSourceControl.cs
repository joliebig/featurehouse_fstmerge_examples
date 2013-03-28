using System;
using System.Collections.Generic;
using System.IO;
using Exortech.NetReflector;
using ThoughtWorks.CruiseControl.Core.Util;
namespace ThoughtWorks.CruiseControl.Core.Sourcecontrol
{
    [ReflectorType("ftpSourceControl")]
    public class FtpSourceControl
        : SourceControlBase
    {
        private FtpLib ftp;
        public FtpSourceControl()
        {
            this.ServerName = string.Empty;
            this.UserName = string.Empty;
            this.Password = string.Empty;
            this.UseActiveConnectionMode = true;
            this.FtpFolderName = string.Empty;
            this.LocalFolderName = string.Empty;
            this.RecursiveCopy = true;
        }
        [ReflectorProperty("serverName", Required = true)]
        public string ServerName { get; set; }
        [ReflectorProperty("userName", Required = true)]
        public string UserName { get; set; }
        [ReflectorProperty("password", typeof(PrivateStringSerialiserFactory), Required = true)]
        public PrivateString Password { get; set; }
        [ReflectorProperty("useActiveConnectionMode", Required = false)]
        public bool UseActiveConnectionMode { get; set; }
        [ReflectorProperty("ftpFolderName", Required = true)]
        public string FtpFolderName { get; set; }
        [ReflectorProperty("localFolderName", Required = true)]
        public string LocalFolderName { get; set; }
        [ReflectorProperty("recursiveCopy", Required = true)]
        public bool RecursiveCopy { get; set; }
        public override Modification[] GetModifications(IIntegrationResult from_, IIntegrationResult to)
        {
            ftp = new FtpLib(to.BuildProgressInformation);
            string remoteFolder = FtpFolderName;
            ftp.LogIn(ServerName,UserName,Password.PrivateValue,UseActiveConnectionMode);
            if (!FtpFolderName.StartsWith("/"))
            {
                remoteFolder = System.IO.Path.Combine(ftp.CurrentWorkingFolder(), FtpFolderName);
            }
            Modification[] mods = ftp.ListNewOrUpdatedFilesAtFtpSite(LocalFolderName, remoteFolder, RecursiveCopy);
            ftp.DisConnect();
            return mods;
        }
        public override void LabelSourceControl(IIntegrationResult result)
        {
        }
        public override void GetSource(IIntegrationResult result)
        {
            Util.Log.Info(result.HasModifications().ToString());
            ftp = new FtpLib(result.BuildProgressInformation);
            string remoteFolder = FtpFolderName;
            ftp.LogIn(ServerName, UserName, Password.PrivateValue, UseActiveConnectionMode);
            if (!FtpFolderName.StartsWith("/"))
            {
                remoteFolder = System.IO.Path.Combine(ftp.CurrentWorkingFolder(), FtpFolderName);
            }
            ftp.DownloadFolder( LocalFolderName, remoteFolder, RecursiveCopy);
            ftp.DisConnect();
        }
        public override void Initialize(IProject project)
        {
        }
        public override void Purge(IProject project)
        {
        }
    }
}
