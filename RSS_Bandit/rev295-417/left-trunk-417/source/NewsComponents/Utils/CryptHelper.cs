using System;
using System.Diagnostics;
using System.Security.Cryptography;
using System.Text;
namespace NewsComponents.Utils
{
 internal class CryptHelper {
  private static readonly TripleDESCryptoServiceProvider _des = new TripleDESCryptoServiceProvider();
  private CryptHelper(){}
  static CryptHelper() {
   _des.Key = _calcHash();
   _des.Mode = CipherMode.ECB;
  }
  public static string Decrypt(string str) {
   byte[] base64;
      string ret;
   if (str == null)
    ret = null;
   else {
    if (str.Length == 0)
     ret = String.Empty;
    else {
     try {
      base64 = Convert.FromBase64String(str);
      byte[] bytes = _des.CreateDecryptor().TransformFinalBlock(base64, 0, base64.GetLength(0));
      ret = Encoding.Unicode.GetString(bytes);
     }
     catch (Exception e) {
      Trace.WriteLine("Exception in Decrypt: "+e, "CryptHelper");
      ret = String.Empty;
     }
    }
   }
   return ret;
  }
  public static string Decrypt(byte[] bytes) {
      string ret;
   if (bytes.GetLength(0) == 0)
    ret = String.Empty;
   else {
    try
    {
        byte[] tmp = _des.CreateDecryptor().TransformFinalBlock(bytes, 0, bytes.GetLength(0));
        ret = Encoding.Unicode.GetString(tmp);
    }
    catch (Exception e) {
     Trace.WriteLine("Exception in Decrypt: "+e, "CryptHelper");
     ret = String.Empty;
    }
   }
   return ret;
  }
  public static string Encrypt(string str) {
   byte[] inBytes;
      string ret;
   if (str == null)
    ret = null;
   else {
    if (str.Length == 0)
     ret = String.Empty;
    else {
     try {
      inBytes = Encoding.Unicode.GetBytes(str);
      byte[] bytes = _des.CreateEncryptor().TransformFinalBlock(inBytes, 0, inBytes.GetLength(0));
      ret = Convert.ToBase64String(bytes);
     }
     catch (Exception e) {
      Trace.WriteLine("Exception in Encrypt: "+e, "CryptHelper");
      ret = String.Empty;
     }
    }
   }
   return ret;
  }
  public static byte[] EncryptB(string str) {
   byte[] inBytes;
   byte[] ret;
   if (str == null)
    ret = null;
   else {
    if (str.Length == 0)
     ret = null;
    else {
     try {
      inBytes = Encoding.Unicode.GetBytes(str);
      ret = _des.CreateEncryptor().TransformFinalBlock(inBytes, 0, inBytes.GetLength(0));
     }
     catch (Exception e) {
      Trace.WriteLine("Exception in Encrypt: "+e, "CryptHelper");
      ret = null;
     }
    }
   }
   return ret;
  }
  private static byte[] _calcHash() {
   string salt = "NewsComponents.4711";
   byte[] b = Encoding.Unicode.GetBytes(salt);
   int bLen = b.GetLength(0);
   Random r = new Random(1500450271);
   byte[] res = new Byte[500];
   int i;
   for (i = 0; i < bLen && i < 500; i++)
    res[i] = (byte)(b[i] ^ r.Next(30, 127));
   while (i < 500) {
    res[i] = (byte)r.Next(30, 127);
    i++;
   }
   MD5CryptoServiceProvider csp = new MD5CryptoServiceProvider();
   return csp.ComputeHash(res);
  }
 }
}
