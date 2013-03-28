using Microsoft.Win32;
namespace WikiFunctions
{
    public static class RegistryUtils
    {
        private const string KeyPrefix = "Software\\AutoWikiBrowser\\";
        public static string GetValue(string keyNameSuffix, object defaultValue)
        {
            string wantedKey = keyNameSuffix.Substring(keyNameSuffix.LastIndexOf("\\"));
            RegistryKey regKey = Registry.CurrentUser.OpenSubKey(BuildKeyName(keyNameSuffix.Replace(wantedKey, "")));
            return (regKey != null) ? regKey.GetValue(wantedKey.Replace("\\", ""), defaultValue).ToString() : "";
        }
        public static void SetValue(string keyNameSuffix, string valueName, string value)
        {
            GetWritableKey(keyNameSuffix).SetValue(valueName, value);
        }
        public static RegistryKey GetWritableKey(string keyNameSuffix)
        {
            return Registry.CurrentUser.CreateSubKey(BuildKeyName(keyNameSuffix));
        }
        public static RegistryKey OpenSubKey(string keyNameSuffix)
        { return Registry.CurrentUser.OpenSubKey(BuildKeyName(keyNameSuffix)); }
        public static void DeleteSubKey(string keyNameSuffix, bool throwOnMissingSubKey)
        { Registry.CurrentUser.DeleteSubKey(BuildKeyName(keyNameSuffix), throwOnMissingSubKey); }
        public static void DeleteSubKey(string keyNameSuffix)
        {
            Registry.CurrentUser.DeleteSubKey(BuildKeyName(keyNameSuffix));
        }
        private static string BuildKeyName(string keyNameSuffix)
        {
            return KeyPrefix + keyNameSuffix;
        }
    }
    namespace Encryption
    {
        public class EncryptionUtils
        {
            private readonly string IV16Chars, PassPhrase, Salt;
            public EncryptionUtils(string initVector, string passPhrase, string salt)
            {
                IV16Chars = initVector;
                PassPhrase = passPhrase;
                Salt = salt;
            }
            public string Encrypt(string text)
            {
                try
                {
                    return (!string.IsNullOrEmpty(text))
                               ? RijndaelSimple.Encrypt(text, PassPhrase, Salt, "SHA1", 2, IV16Chars, 256)
                               : text;
                }
                catch
                {
                    return text;
                }
            }
            public string Decrypt(string text)
            {
                try
                {
                    return (!string.IsNullOrEmpty(text))
                               ? RijndaelSimple.Decrypt(text, PassPhrase, Salt, "SHA1", 2, IV16Chars, 256)
                               : text;
                }
                catch
                {
                    return text;
                }
            }
            public string RegistryGetValueAndDecrypt(string keyNameSuffix, object defaultValue)
            { return Decrypt(RegistryUtils.GetValue(keyNameSuffix, defaultValue)); }
        }
    }
}
