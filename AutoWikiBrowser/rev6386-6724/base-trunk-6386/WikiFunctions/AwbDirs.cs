using System;
using System.IO;
using System.Windows.Forms;
namespace WikiFunctions
{
    public static class AwbDirs
    {
        private static string mAppData;
        public static string AppData
        {
            get
            {
                if (mAppData != null) return mAppData;
                mAppData = Path.Combine(Environment.GetFolderPath(Environment.SpecialFolder.CommonApplicationData),
                    "AutoWikiBrowser");
                Directory.CreateDirectory(mAppData);
                return mAppData;
            }
        }
        private static string mUserData;
        public static string UserData
        {
            get
            {
                if (mUserData != null) return mUserData;
                mUserData = Path.Combine(Environment.GetFolderPath(Environment.SpecialFolder.LocalApplicationData),
                    "AutoWikiBrowser");
                Directory.CreateDirectory(mUserData);
                return mUserData;
            }
        }
        private static string mDefaultSettings;
        public static string DefaultSettings
        {
            get
            {
                if (mDefaultSettings != null) return mDefaultSettings;
                mDefaultSettings = Path.Combine(UserData, "Default.xml");
                return mDefaultSettings;
            }
        }
        private static bool? mSpecialMedia;
        public static bool RunningFromNetworkOrRemovableDrive
        {
            get
            {
                if (mSpecialMedia != null) return (bool)mSpecialMedia;
                string drive = Path.GetPathRoot(Application.ExecutablePath);
                if (drive.StartsWith("\\")) return true;
                DriveInfo di = new DriveInfo(drive.Substring(0, 1));
                mSpecialMedia = (di.DriveType == DriveType.Removable || di.DriveType == DriveType.Network);
                return (bool)mSpecialMedia;
            }
        }
        public static void MigrateDefaultSettings()
        {
            string exeDir = Path.GetDirectoryName(Application.ExecutablePath);
            string defaultXml = Path.Combine(exeDir, "Default.xml");
            if (File.Exists(defaultXml) && !File.Exists(DefaultSettings) && !RunningFromNetworkOrRemovableDrive)
            {
                try
                {
                    File.Copy(defaultXml, DefaultSettings);
                    File.Delete(defaultXml);
                }
                catch
                { }
            }
        }
    }
}
