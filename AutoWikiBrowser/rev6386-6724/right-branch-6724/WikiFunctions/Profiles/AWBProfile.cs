using System.Collections.Generic;
using System.Windows.Forms;
using WikiFunctions.Encryption;
using Microsoft.Win32;
namespace WikiFunctions.Profiles
{
    public class AWBProfile
    {
        public AWBProfile(int pID, string pUsername, string pPassword, string pDefaultSettings, string pNotes,
            bool pUseForUpload)
        {
            ID = pID;
            Username = pUsername;
            Password = pPassword;
            DefaultSettings = pDefaultSettings;
            Notes = pNotes;
            UseForUpload = pUseForUpload;
        }
        public AWBProfile() { }
        public int ID = -1;
        public string DefaultSettings, Notes;
        public string Username, Password;
        public bool UseForUpload;
    }
    public static class AWBProfiles
    {
        private static readonly EncryptionUtils EncryptionUtils = new EncryptionUtils("tnf47bgfdwlp9,.q",
            "oi frjweopi 4r390%^($%%^$HJKJNMHJGY 2`';'[#", "SH1ew yuhn gxe$ï¿½$%^y HNKLHWEQ JEW`b");
        private const string ProfileRegistryString = "Profiles\\";
        static AWBProfiles()
        {
            ResetTempPassword();
        }
        public static List<AWBProfile> GetProfiles()
        {
            List<AWBProfile> profiles = new List<AWBProfile>();
            foreach (int id in GetProfileIDs())
            {
                profiles.Add(GetProfile(id));
            }
            return profiles;
        }
        public static AWBProfile GetProfile(int id)
        {
            AWBProfile prof = new AWBProfile() {ID = id};
            try { prof.Username = RegistryGetAndDecryptValue(id + "\\User", ""); }
            catch
            {
                if (MessageBox.Show("Profile corrupt. Would you like to delete this profile?", "Delete corrupt profile?", MessageBoxButtons.YesNo) == DialogResult.Yes)
                    DeleteProfile(id);
            }
            if (string.IsNullOrEmpty(prof.Username)) return null;
            try { prof.Password = RegistryGetAndDecryptValue(id + "\\Pass", ""); }
            catch { prof.Password = ""; }
            finally
            {
                prof.DefaultSettings = RegistryGetValue(id + "\\Settings", "");
                try { prof.UseForUpload = bool.Parse(RegistryGetValue(id + "\\UseForUpload", "")); }
                catch { prof.UseForUpload = false; }
                prof.Notes = RegistryGetValue(id + "\\Notes", "");
            }
            return prof;
        }
        public static AWBProfile GetProfile(string userName)
        {
            foreach (AWBProfile prof in GetProfiles())
            {
                if (prof.Username == userName) return prof;
            }
            return null;
        }
        public static AWBProfile GetProfileForLogUploading(IWin32Window owner)
        {
            int idOfUploadAccount = GetIDOfUploadAccount();
            AWBProfile retval;
            if (idOfUploadAccount == -1)
            {
                if (MessageBox.Show("Please select or add a Profile to use for log uploading",
                    "Log uploading", MessageBoxButtons.OKCancel, MessageBoxIcon.Information)
                    == DialogResult.OK)
                {
                    AWBLogUploadProfilesForm profiles = new AWBLogUploadProfilesForm();
                    profiles.ShowDialog(owner);
                    retval = GetProfileForLogUploading(owner);
                }
                else
                    throw new System.Configuration.ConfigurationErrorsException("Log upload profile: User cancelled");
            }
            else
                retval = GetProfile(idOfUploadAccount);
            if (string.IsNullOrEmpty(retval.Password) && string.IsNullOrEmpty(TempPassword))
            {
                UserPassword password = new UserPassword() {Username = retval.Username};
                if (password.ShowDialog() == DialogResult.OK)
                {
                    retval.Password = TempPassword = password.GetPassword;
                }
            }
            else if (!string.IsNullOrEmpty(TempPassword))
                retval.Password = TempPassword;
            return retval;
        }
        public static int GetIDOfUploadAccount()
        {
            foreach (AWBProfile prof in GetProfiles())
                if (prof.UseForUpload)
                    return prof.ID;
            return -1;
        }
        internal static void SetOtherAccountsAsNotForUpload()
        {
            try
            {
                ResetTempPassword();
                foreach (int id in GetProfileIDs())
                { RegistrySetValue(id, "UseForUpload", false.ToString()); }
            }
            catch { }
        }
        public static string GetPassword(int id)
        { return RegistryGetAndDecryptValue(id + "\\Pass", ""); }
        public static string GetUsername(int id)
        { return RegistryGetAndDecryptValue(id + "\\User", ""); }
        public static void SetPassword(int id, string password)
        { SetProfilePassword(id, EncryptionUtils.Encrypt(password)); }
        private static void SetProfilePassword(int id, string password)
        {
            try { RegistrySetValue(id, "Pass", password); }
            catch { }
        }
        internal static void AddEditProfile(AWBProfile profile)
        {
            if (profile.ID == -1)
                profile.ID = GetFirstFreeID();
            RegistryKey key = RegistryGetWritableKey(profile.ID);
            try
            {
                key.SetValue("User", EncryptionUtils.Encrypt(profile.Username));
                key.SetValue("Pass", EncryptionUtils.Encrypt(profile.Password));
                key.SetValue("Settings", profile.DefaultSettings);
                key.SetValue("UseForUpload", profile.UseForUpload);
                key.SetValue("Notes", profile.Notes);
            }
            catch { }
        }
        internal static string LastUsedAccount
        {
            get
            {
                try
                {
                    return RegistryUtils.GetValue(ProfileRegistryString + "LastUsedAccount", "");
                }
                catch
                {
                    return "";
                }
            }
            set
            {
                try
                {
                    RegistryUtils.SetValue(ProfileRegistryString, "LastUsedAccount", value);
                }
                catch { }
            }
        }
        private static string TempPassword
        {
            get
            {
                try { return RegistryGetAndDecryptValue("TempPassword", ""); }
                catch { return ""; }
            }
            set
            {
                try
                {
                    RegistryKey key = RegistryUtils.GetWritableKey(ProfileRegistryString);
                    if (key != null) key.SetValue("TempPassword", EncryptionUtils.Encrypt(value));
                }
                catch { }
            }
        }
        public static void ResetTempPassword()
        {
            TempPassword = "";
        }
        public static void DeleteProfile(int id)
        {
            try { RegistryUtils.DeleteSubKey(ProfileRegistryString + id); }
            catch { }
        }
        private static List<int> GetProfileIDs()
        {
            List<int> profileIds = new List<int>();
            try
            {
                foreach (string id in RegistryUtils.OpenSubKey(ProfileRegistryString).GetSubKeyNames())
                { profileIds.Add(int.Parse(id)); }
                return profileIds;
            }
            catch
            { return profileIds; }
        }
        private static int GetFirstFreeID()
        {
            bool freeIdFound = false;
            List<int> ids = GetProfileIDs();
            int i = 1;
            do
            {
                if (!ids.Contains(i))
                    freeIdFound = true;
                else
                    i++;
            } while (!freeIdFound);
            return i;
        }
        private static string RegistryGetValue(string suffix, object defaultValue)
        { return RegistryUtils.GetValue(ProfileRegistryString + suffix, defaultValue); }
        private static string RegistryGetAndDecryptValue(string suffix, object defaultValue)
        { return EncryptionUtils.RegistryGetValueAndDecrypt(ProfileRegistryString + suffix, defaultValue); }
        private static void RegistrySetValue(int keyNameSuffix, string valueName, string value)
        { RegistryUtils.SetValue(ProfileRegistryString + keyNameSuffix, valueName, value); }
        private static RegistryKey RegistryGetWritableKey(int keyNameSuffix)
        { return RegistryUtils.GetWritableKey(ProfileRegistryString + keyNameSuffix); }
    }
}
