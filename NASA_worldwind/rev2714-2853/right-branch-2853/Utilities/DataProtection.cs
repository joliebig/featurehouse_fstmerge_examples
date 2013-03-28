using System;
using System.Text;
using System.Runtime.InteropServices;
namespace Utility
{
 public class DataProtector
 {
  private const int CRYPTPROTECT_UI_FORBIDDEN = 0x1;
  private const int CRYPTPROTECT_LOCAL_MACHINE = 0x4;
  [StructLayout(LayoutKind.Sequential, CharSet=CharSet.Unicode)]
   internal struct DATA_BLOB
  {
   public int cbData;
   public IntPtr pbData;
  }
  [StructLayout(LayoutKind.Sequential, CharSet=CharSet.Unicode)]
   internal struct CRYPTPROTECT_PROMPTSTRUCT
  {
   public int cbSize;
   public int dwPromptFlags;
   public IntPtr hwndApp;
   public String szPrompt;
  }
  [DllImport("Crypt32.dll", SetLastError=true, CharSet=System.Runtime.InteropServices.CharSet.Auto)]
  private static extern bool CryptProtectData(
   ref DATA_BLOB pDataIn,
   String szDataDescr,
   ref DATA_BLOB pOptionalEntropy,
   IntPtr pvReserved,
   ref CRYPTPROTECT_PROMPTSTRUCT
   pPromptStruct,
   int dwFlags,
   ref DATA_BLOB pDataOut
   );
  [DllImport("Crypt32.dll", SetLastError=true, CharSet=System.Runtime.InteropServices.CharSet.Auto)]
  private static extern bool CryptUnprotectData(
   ref DATA_BLOB pDataIn,
   String szDataDescr,
   ref DATA_BLOB pOptionalEntropy,
   IntPtr pvReserved,
   ref CRYPTPROTECT_PROMPTSTRUCT
   pPromptStruct,
   int dwFlags,
   ref DATA_BLOB pDataOut
   );
  public enum Store {USE_MACHINE_STORE = 1, USE_USER_STORE};
  private Store store;
  public DataProtector(Store tempStore)
  {
   store = tempStore;
  }
  private static void InitPromptstruct(ref CRYPTPROTECT_PROMPTSTRUCT ps)
  {
   ps.cbSize = Marshal.SizeOf(typeof(CRYPTPROTECT_PROMPTSTRUCT));
   ps.dwPromptFlags = 0;
   ps.hwndApp = IntPtr.Zero;
   ps.szPrompt = null;
  }
  public byte[] Encrypt(byte[] plainText, byte[] optionalEntropy)
  {
   bool retVal = false;
   DATA_BLOB plainTextBlob = new DATA_BLOB();
   DATA_BLOB cipherTextBlob = new DATA_BLOB();
   DATA_BLOB entropyBlob = new DATA_BLOB();
   CRYPTPROTECT_PROMPTSTRUCT prompt = new CRYPTPROTECT_PROMPTSTRUCT();
   InitPromptstruct(ref prompt);
   int dwFlags;
   try
   {
    try
    {
     int bytesSize = plainText.Length;
     plainTextBlob.pbData = Marshal.AllocHGlobal(bytesSize);
     if(IntPtr.Zero == plainTextBlob.pbData)
     {
      throw new Exception("Unable to allocate plaintext buffer.");
     }
     plainTextBlob.cbData = bytesSize;
     Marshal.Copy(plainText, 0, plainTextBlob.pbData, bytesSize);
    }
    catch(Exception ex)
    {
     throw new Exception("Exception marshalling data. " + ex.Message);
    }
    if(Store.USE_MACHINE_STORE == store)
    {
     dwFlags = CRYPTPROTECT_LOCAL_MACHINE|CRYPTPROTECT_UI_FORBIDDEN;
     if(null == optionalEntropy)
     {
      optionalEntropy = new byte[0];
     }
     try
     {
      int bytesSize = optionalEntropy.Length;
      entropyBlob.pbData = Marshal.AllocHGlobal(optionalEntropy.Length);;
      if(IntPtr.Zero == entropyBlob.pbData)
      {
       throw new Exception("Unable to allocate entropy data buffer.");
      }
      Marshal.Copy(optionalEntropy, 0, entropyBlob.pbData, bytesSize);
      entropyBlob.cbData = bytesSize;
     }
     catch(Exception ex)
     {
      throw new Exception("Exception  marshalling entropy data. " +
       ex.Message);
     }
    }
    else
    {
     dwFlags = CRYPTPROTECT_UI_FORBIDDEN;
    }
    retVal = CryptProtectData(ref plainTextBlob, "", ref entropyBlob,
     IntPtr.Zero, ref prompt, dwFlags,
     ref cipherTextBlob);
    if(false == retVal)
    {
     throw new Exception("Encryption failed. " +
      Win32Message.GetMessage(Marshal.GetLastWin32Error()));
    }
    if(IntPtr.Zero != plainTextBlob.pbData)
    {
     Marshal.FreeHGlobal(plainTextBlob.pbData);
    }
    if(IntPtr.Zero != entropyBlob.pbData)
    {
     Marshal.FreeHGlobal(entropyBlob.pbData);
    }
   }
   catch(Exception ex)
   {
    throw new Exception("Exception encrypting. " + ex.Message);
   }
   byte[] cipherText = new byte[cipherTextBlob.cbData];
   Marshal.Copy(cipherTextBlob.pbData, cipherText, 0, cipherTextBlob.cbData);
   Marshal.FreeHGlobal(cipherTextBlob.pbData);
   return cipherText;
  }
  public byte[] Decrypt(byte[] cipherText, byte[] optionalEntropy)
  {
   bool retVal = false;
   DATA_BLOB plainTextBlob = new DATA_BLOB();
   DATA_BLOB cipherBlob = new DATA_BLOB();
   CRYPTPROTECT_PROMPTSTRUCT prompt = new CRYPTPROTECT_PROMPTSTRUCT();
   InitPromptstruct(ref prompt);
   try
   {
    try
    {
     int cipherTextSize = cipherText.Length;
     cipherBlob.pbData = Marshal.AllocHGlobal(cipherTextSize);
     if(IntPtr.Zero == cipherBlob.pbData)
     {
      throw new Exception("Unable to allocate cipherText buffer.");
     }
     cipherBlob.cbData = cipherTextSize;
     Marshal.Copy(cipherText, 0, cipherBlob.pbData,
      cipherBlob.cbData);
    }
    catch(Exception ex)
    {
     throw new Exception("Exception marshalling data. " +
      ex.Message);
    }
    DATA_BLOB entropyBlob = new DATA_BLOB();
    int dwFlags;
    if(Store.USE_MACHINE_STORE == store)
    {
     dwFlags =
      CRYPTPROTECT_LOCAL_MACHINE|CRYPTPROTECT_UI_FORBIDDEN;
     if(null == optionalEntropy)
     {
      optionalEntropy = new byte[0];
     }
     try
     {
      int bytesSize = optionalEntropy.Length;
      entropyBlob.pbData = Marshal.AllocHGlobal(bytesSize);
      if(IntPtr.Zero == entropyBlob.pbData)
      {
       throw new Exception("Unable to allocate entropy buffer.");
      }
      entropyBlob.cbData = bytesSize;
      Marshal.Copy(optionalEntropy, 0, entropyBlob.pbData,
       bytesSize);
     }
     catch(Exception ex)
     {
      throw new Exception("Exception marshalling entropy data. " +
       ex.Message);
     }
    }
    else
    {
     dwFlags = CRYPTPROTECT_UI_FORBIDDEN;
    }
    retVal = CryptUnprotectData(ref cipherBlob, null, ref
     entropyBlob,
     IntPtr.Zero, ref prompt, dwFlags,
     ref plainTextBlob);
    if(false == retVal)
    {
     throw new Exception("Decryption failed. " +
      Win32Message.GetMessage(Marshal.GetLastWin32Error()));
    }
    if(IntPtr.Zero != cipherBlob.pbData)
    {
     Marshal.FreeHGlobal(cipherBlob.pbData);
    }
    if(IntPtr.Zero != entropyBlob.pbData)
    {
     Marshal.FreeHGlobal(entropyBlob.pbData);
    }
   }
   catch(Exception ex)
   {
    throw new Exception("Exception decrypting. " + ex.Message);
   }
   byte[] plainText = new byte[plainTextBlob.cbData];
   Marshal.Copy(plainTextBlob.pbData, plainText, 0, plainTextBlob.cbData);
   Marshal.FreeHGlobal(plainTextBlob.pbData);
   return plainText;
  }
  System.Text.UnicodeEncoding unienc = new System.Text.UnicodeEncoding();
  public string EncryptStringToBase64(string plainText)
  {
   byte [] theBytes = unienc.GetBytes(plainText);
   string encryptedString = null;
   try
   {
    encryptedString = System.Convert.ToBase64String(Encrypt(theBytes, null));
   }
   catch(System.Exception caught)
   {
    Log.Write(caught);
   }
   return encryptedString;
  }
  public string DecryptBase64AsString(string cypherText)
  {
   byte [] theBytes = System.Convert.FromBase64String(cypherText);
   string decryptedString = null;
   try
   {
    decryptedString = unienc.GetString(Decrypt(theBytes, null));
   }
   catch(System.Exception caught)
   {
    Log.Write(caught);
   }
   return decryptedString;
  }
  public string TransparentDecrypt(string cypherText)
  {
   if(cypherText.StartsWith("crypt:"))
   {
    string decryptedString = DecryptBase64AsString(cypherText.Substring(6));
    if(decryptedString != null) return decryptedString;
   }
   return cypherText;
  }
  public string TransparentEncrypt(string plainText)
  {
   string encryptedString = EncryptStringToBase64(plainText);
   if(encryptedString != null) return "crypt:"+encryptedString;
   return plainText;
  }
 }
}
