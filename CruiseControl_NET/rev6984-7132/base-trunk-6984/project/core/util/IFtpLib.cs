using System;
namespace ThoughtWorks.CruiseControl.Core.Util
{
    interface IFtpLib
    {
        void LogIn(string serverName, string userName, string password, bool activeConnectionMode);
        void DisConnect();
        bool IsConnected();
        string CurrentWorkingFolder();
        void DownloadFolder(string localFolder, string remoteFolder, bool recursive);
        void UploadFolder(string remoteFolder, string localFolder, bool recursive);
        Modification[] ListNewOrUpdatedFilesAtFtpSite(string localFolder, string remoteFolder, bool recursive);
    }
}
