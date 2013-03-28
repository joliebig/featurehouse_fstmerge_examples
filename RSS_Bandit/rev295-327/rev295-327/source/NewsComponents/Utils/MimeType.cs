using System; 
using System.Globalization; 
using System.IO; 
using System.Runtime.InteropServices; 
using System.Security.Permissions; 
using Microsoft.Win32; namespace  NewsComponents.Utils {
	
    [Serializable] 
    public class  MimeType  : IEquatable<MimeType> {
		
        private  string msType;
 
        private  string msSubType;
 
        public  MimeType()
        {
            msType = msSubType = String.Empty;
        }
 
        public  MimeType(string contentType)
        {
            SplitTypeAndSubType(contentType, out msType, out msSubType);
        }
 
        public  MimeType(string type, string subType)
        {
            msType = (type ?? String.Empty);
            msSubType = (subType ?? String.Empty);
        }
 
        public static readonly  MimeType Empty = new MimeType();
 
        public static  MimeType CreateFrom(string dataFileName)
        {
            return CreateFrom(dataFileName, Empty);
        }
 
        public static  MimeType CreateFrom(string dataFileName, string mimeProposed)
        {
            return CreateFrom(dataFileName, new MimeType(mimeProposed));
        }
 
        public static  MimeType CreateFrom(string dataFileName, MimeType mimeProposed)
        {
            if (string.IsNullOrEmpty(dataFileName))
                throw new ArgumentNullException("dataFileName");
            MimeType mimeRet;
            if (mimeProposed != null)
            {
                mimeRet = mimeProposed;
            }
            else
            {
                mimeRet = CreateFromFileExt(dataFileName);
            }
            if (File.Exists(dataFileName))
            {
                try
                {
                    using (Stream f = FileHelper.OpenForRead(dataFileName))
                    {
                        return CreateFrom(f, mimeRet);
                    }
                }
                catch
                {
                }
            }
            string fileUri = dataFileName;
            try
            {
                if (fileUri.IndexOf(@"://") < 0)
                    fileUri = new Uri(dataFileName).ToString();
            }
            catch (UriFormatException)
            {
            }
            int ret = -1;
            IntPtr suggestPtr = IntPtr.Zero;
            IntPtr outPtr = IntPtr.Zero;
            try
            {
                IntPtr filePtr = Marshal.StringToCoTaskMemUni(fileUri);
                ret = FindMimeFromData(IntPtr.Zero, filePtr, null, 0, suggestPtr, 0, out outPtr, 0);
            }
            catch
            {
            }
            if (ret == 0 && outPtr != IntPtr.Zero)
            {
                return new MimeType(Marshal.PtrToStringUni(outPtr));
            }
            return mimeRet;
        }
 
        public static  MimeType CreateFrom(Stream dataStream)
        {
            return CreateFrom(dataStream, Empty);
        }
 
        public static  MimeType CreateFrom(Stream dataStream, string mimeProposed)
        {
            return CreateFrom(dataStream, new MimeType(mimeProposed));
        }
 
        public static  MimeType CreateFrom(Stream dataStream, MimeType mimeProposed)
        {
            if (dataStream == null)
                throw new ArgumentNullException("dataStream");
            MimeType mimeRet = (mimeProposed ?? Empty);
            long oldPosition = dataStream.Position;
            dataStream.Seek(0, SeekOrigin.Begin);
            byte[] bValues = new byte[256];
            int bytesRead;
            bytesRead = dataStream.Read(bValues, 0, bValues.Length);
            dataStream.Seek(oldPosition, SeekOrigin.Begin);
            if (bytesRead > 0)
                return CreateFrom(bValues, mimeRet);
            else
                throw new InvalidOperationException("Cannot analyze stream content (length Zero)");
        }
 
        public static  MimeType CreateFrom(byte[] dataBytes)
        {
            return CreateFrom(dataBytes, Empty);
        }
 
        public static  MimeType CreateFrom(byte[] dataBytes, string mimeProposed)
        {
            return CreateFrom(dataBytes, new MimeType(mimeProposed));
        }
 
        public static  MimeType CreateFrom(byte[] dataBytes, MimeType mimeProposed)
        {
            if (dataBytes == null || dataBytes.Length == 0)
                throw new ArgumentNullException("dataBytes");
            MimeType mimeRet = (mimeProposed ?? Empty);
            string sMimeType = null;
            MimeType mimeFnd;
            IntPtr suggestPtr = IntPtr.Zero, outPtr;
            int ret =
                FindMimeFromData(IntPtr.Zero, IntPtr.Zero, dataBytes, dataBytes.Length, suggestPtr, 0, out outPtr, 0);
            if (ret == 0 && outPtr != IntPtr.Zero)
            {
                sMimeType = Marshal.PtrToStringUni(outPtr);
            }
            if (IsAmbigious(sMimeType))
            {
                if (BytesEqual(_magic_write_sign, dataBytes, 0, _magic_write_sign.Length))
                    sMimeType = "application/richtext";
                if (BytesEqual(_magic_rtf_sign, dataBytes, 0, _magic_rtf_sign.Length))
                    sMimeType = "text/richtext";
                if (BytesEqual(_magic_old_word_sign, dataBytes, 0, _magic_old_word_sign.Length))
                    sMimeType = "application/msword";
                if (BytesEqual(_magic_ole_sign, dataBytes, 0, _magic_ole_sign.Length))
                {
                    if (IsOfficeMimeType(mimeRet.ContentType))
                        return mimeRet;
                }
                if (BytesEqual(_magic_ms_jetdb, dataBytes, 0, _magic_ms_jetdb.Length))
                    sMimeType = "application/msaccess";
                if (BytesEqual(_magic_ms_onenote, dataBytes, 0, _magic_ms_onenote.Length))
                    sMimeType = "application/msonenote";
            }
            mimeFnd = new MimeType(sMimeType);
            if (mimeFnd != Empty && !mimeFnd.Equals(mimeRet))
                return mimeFnd;
            return mimeRet;
        }
 
        public static  MimeType CreateFromFileExt(string fileNameExt)
        {
            if (string.IsNullOrEmpty(fileNameExt))
                return Empty;
            string ext = Path.GetExtension(fileNameExt);
            if (string.IsNullOrEmpty(ext))
                return Empty;
            MimeType m = CreateFromRegisteredApps(ext);
            if (m != Empty)
                return m;
            switch (ext)
            {
                case ".rtf":
                    {
                        return new MimeType("text", "richtext");
                    }
                case ".htm":
                case ".html":
                    {
                        return new MimeType("text", "html");
                    }
                case ".aif":
                case ".aiff":
                    {
                        return new MimeType("audio", "x-aiff");
                    }
                case ".bas":
                case ".basic":
                    {
                        return new MimeType("audio", "basic");
                    }
                case ".wav":
                case ".wave":
                    {
                        return new MimeType("audio", "wav");
                    }
                case ".gif":
                    {
                        return new MimeType("image", "gif");
                    }
                case ".jpeg":
                    {
                        return new MimeType("image", "jpeg");
                    }
                case ".pjpeg":
                    {
                        return new MimeType("image", "pjpeg");
                    }
                case ".tiff":
                    {
                        return new MimeType("image", "tiff");
                    }
                case ".x-png":
                    {
                        return new MimeType("image", "x-png");
                    }
                case ".x-xbitmap":
                    {
                        return new MimeType("image", "x-xbitmap");
                    }
                case ".bmp":
                    {
                        return new MimeType("image", "bmp");
                    }
                case ".jg":
                    {
                        return new MimeType("image", "x-jg");
                    }
                case ".emf":
                    {
                        return new MimeType("image", "x-emf");
                    }
                case ".wmf":
                    {
                        return new MimeType("image", "x-wmf");
                    }
                case ".avi":
                    {
                        return new MimeType("video", "avi");
                    }
                case ".mpeg":
                    {
                        return new MimeType("video", "mpeg");
                    }
                case ".ps":
                case ".postscript":
                    {
                        return new MimeType("application", "postscript");
                    }
                case ".base64":
                    {
                        return new MimeType("application", "base64");
                    }
                case ".macbinhex40":
                    {
                        return new MimeType("application", "macbinhex40");
                    }
                case ".pdf":
                    {
                        return new MimeType("application", "pdf");
                    }
                case ".x-compressed":
                    {
                        return new MimeType("application", "x-compressed");
                    }
                case ".zip":
                    {
                        return new MimeType("application", "x-zip-compressed");
                    }
                case ".gzip":
                    {
                        return new MimeType("application", "x-gzip-compressed");
                    }
                case ".java":
                    {
                        return new MimeType("application", "java");
                    }
                case ".msdownload ":
                    {
                        return new MimeType("application", "x-msdownload");
                    }
                case ".cdf ":
                    {
                        return new MimeType("application", "x-cdf");
                    }
            }
            return Empty;
        }
 
        public static  MimeType CreateFromRegisteredApps(string fileExtension)
        {
            if (fileExtension == null)
                return Empty;
            string dotExt = (fileExtension.IndexOf(".") >= 0 ? fileExtension : "." + fileExtension);
            string mType = WindowsRegistry.GetMimeTypeString(dotExt);
            if (mType == null)
                return Empty;
            return new MimeType(mType);
        }
 
        public  string Type
        {
            set
            {
                msType = value;
            }
            get
            {
                return msType;
            }
        }
 
        public  string SubType
        {
            set
            {
                msSubType = value;
            }
            get
            {
                return msSubType;
            }
        }
 
        public  string ContentType
        {
            set
            {
                SplitTypeAndSubType(value, out msType, out msSubType);
            }
            get
            {
                return msType + "/" + msSubType;
            }
        }
 
        public  string GetCLSID()
        {
            if (msType == null || msType.Length == 0)
                return null;
            return WindowsRegistry.GetMimeTypeOption(ContentType, "CLSID");
        }
 
        public  string GetFileExtension()
        {
            if (msType == null || msType.Length == 0)
                return null;
            return WindowsRegistry.GetMimeTypeOption(ContentType, "Extension");
        }
 
        public  bool MatchContentOf(string fileName)
        {
            if (string.IsNullOrEmpty(fileName))
                throw new ArgumentNullException("fileName");
            return (CreateFrom(fileName, ContentType).Equals(this));
        }
 
        public  bool MatchContentOf(Stream stream)
        {
            if (stream == null)
                throw new ArgumentNullException("stream");
            return (CreateFrom(stream, ContentType).Equals(this));
        }
 
        public  bool MatchContentOf(byte[] bytes)
        {
            if (bytes == null || bytes.Length == 0)
                throw new ArgumentNullException("bytes");
            return (CreateFrom(bytes, ContentType).Equals(this));
        }
 
        public override  string ToString()
        {
            return ContentType;
        }
 
        public  bool Equals(MimeType other)
        {
            if (ReferenceEquals(this, other))
            {
                return true;
            }
            if (ReferenceEquals(other,null))
            {
                return false;
            }
            if (ContentType.Equals(other.ContentType))
            {
                return true;
            }
            return false;
        }
 
        public override  bool Equals(object obj)
        {
            return Equals(obj as MimeType);
        }
 
        public override  int GetHashCode()
        {
            if (ContentType != null)
                return ContentType.GetHashCode();
            return base.GetHashCode();
        }
 
        private static  void SplitTypeAndSubType(string contentType, out string sType, out string sSubType)
        {
            string[] sArray = String.Concat(contentType, "/").Split(new char[] {'/'});
            sType = sArray[0].Trim();
            sSubType = sArray[1].Trim();
        }
 
        private static readonly  string _binaryMime = "application/octet-stream";
 
        private static readonly  string _textMime = "text/plain";
 
        private static readonly  byte[] _magic_ole_sign = new byte[] {0xD0, 0xCF, 0x11, 0xE0, 0xA1, 0xB1, 0x1A, 0xE1};
 
        private static readonly  byte[] _magic_rtf_sign = new byte[] {(byte) '{', (byte) '\\', (byte) 'r', (byte) 't', (byte) 'f'};
 
        private static readonly  byte[] _magic_old_word_sign = new byte[] {0xdb, 0xa5};
 
        private static readonly  byte[] _magic_write_sign = new byte[] {0x31, 0xBE};
 
        private static readonly  byte[] _magic_ms_jetdb =
            new byte[]
                {
                    0x0, 0x01, 0x0, 0x0, (byte) 'S', (byte) 't', (byte) 'a', (byte) 'n', (byte) 'd', (byte) 'a', (byte) 'r'
                    , (byte) 'd', (byte) ' ', (byte) 'J', (byte) 'e', (byte) 't', (byte) ' ', (byte) 'D', (byte) 'B'
                };
 
        private static readonly  byte[] _magic_ms_onenote =
            new byte[] {0xE4, 0x52, 0x5C, 0x7B, 0x8C, 0xD8, 0xA7, 0x4D, 0xAE, 0xB1, 0x53, 0x78, 0xD0, 0x29, 0x96, 0xD3};
 
        private static  bool IsAmbigious(string mimeType)
        {
            if (string.IsNullOrEmpty(mimeType))
                return true;
            if (_binaryMime.Equals(mimeType) || _textMime.Equals(mimeType))
                return true;
            return false;
        }
 
        private static  bool IsOfficeMimeType(string mimeType)
        {
            if (string.IsNullOrEmpty(mimeType))
                return false;
            if (mimeType.IndexOf("msword") > 0)
                return true;
            if (mimeType.IndexOf("msexcel") > 0)
                return true;
            if (mimeType.IndexOf("mspowerpoint") > 0)
                return true;
            if (mimeType.IndexOf("ms-publisher") > 0)
                return true;
            if (mimeType.IndexOf("ms-powerpoint") > 0)
                return true;
            if (mimeType.IndexOf("ms-excel") > 0)
                return true;
            return false;
        }
 
        private static  bool BytesEqual(byte[] a, byte[] b, int offset, int length)
        {
            int lena = a.Length, lenb = b.Length;
            for (int i = offset; i < offset + length && i < lena & i < lenb; i++)
            {
                if (a[i] != b[i])
                    return false;
            }
            return true;
        }
 
        [DllImport("urlmon.dll", CharSet=CharSet.Auto)] 
        private static extern  int FindMimeFromData(
            IntPtr pBC,
            IntPtr pwzUrl,
            byte[] pBuffer,
            int cbSize,
            IntPtr pwzMimeProposed,
            int dwMimeFlags,
            out IntPtr ppwzMimeOut,
            int dwReserved);
 
        private class  WindowsRegistry {
			
            public static  string GetMimeTypeString(string fileExtension)
            {
                if (fileExtension == null)
                    return null;
                if (null == AquireReadAccess(@"HKEY_CLASSES_ROOT\MIME"))
                    return null;
                RegistryKey typeKey = Registry.ClassesRoot.OpenSubKey(@"MIME\Database\Content Type", false);
                if (typeKey == null)
                    return null;
                CultureInfo c = CultureInfo.InvariantCulture;
                foreach (string keyname in typeKey.GetSubKeyNames())
                {
                    RegistryKey curKey = typeKey.OpenSubKey(keyname);
                    string val = curKey.GetValue("Extension") as string;
                    curKey.Close();
                    if (String.Compare(fileExtension, val, true, c) == 0)
                    {
                        typeKey.Close();
                        return keyname;
                    }
                }
                typeKey.Close();
                return null;
            }
 
            public static  string GetMimeTypeOption(string mimeType, string option)
            {
                if (option == null || mimeType == null)
                    return null;
                if (null == AquireReadAccess(@"HKEY_CLASSES_ROOT\MIME"))
                    return null;
                RegistryKey typeKey = Registry.ClassesRoot.OpenSubKey(@"MIME\Database\Content Type\" + mimeType, false);
                if (typeKey == null)
                    return null;
                string val = typeKey.GetValue(option) as string;
                typeKey.Close();
                return val;
            }
 
            private static  RegistryPermission AquireReadAccess(string hive)
            {
                RegistryPermission regPerm = new RegistryPermission(RegistryPermissionAccess.Read, hive);
                regPerm.Demand();
                return regPerm;
            }

		}

	}

}
