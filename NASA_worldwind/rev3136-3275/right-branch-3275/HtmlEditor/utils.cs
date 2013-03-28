using System;
using System.Diagnostics;
using System.IO;
using System.Runtime.InteropServices;
using System.Runtime.InteropServices.ComTypes;
using System.Text;
using System.Drawing;
namespace onlyconnect
{
    public class utils
    {
        public utils()
        {
        }
        public static string DecodeHRESULT(int hr)
        {
            switch (hr)
            {
                case HRESULT.S_OK: return "S_OK";
                case HRESULT.S_FALSE: return "S_FALSE";
                case HRESULT.E_UNEXPECTED: return "E_UNEXPECTED";
                case HRESULT.E_NOTIMPL: return "E_NOTIMPL";
                case HRESULT.E_NOINTERFACE: return "E_NOINTERFACE";
                case HRESULT.E_INVALIDARG: return "E_INVALIDARG";
                case HRESULT.E_FAIL: return "E_FAIL";
                default: return "Unknown";
             }
        }
        public static void LoadUrl(ref HTMLDocument doc, String url)
        {
            LoadUrl(ref doc, url, true);
        }
        public static void LoadUrl(ref HTMLDocument doc, String url, bool CreateSite)
        {
            if (doc == null)
            {
                throw new HtmlEditorException("Null document passed to LoadDocument");
            }
            if (CreateSite)
            {
                DownloadOnlySite ds = new DownloadOnlySite();
                IOleObject ob = (IOleObject)doc;
                ob.SetClientSite(ds);
            }
            IPersistMoniker persistMoniker = (IPersistMoniker)doc;
            IMoniker moniker = null;
            int iResult = win32.CreateURLMoniker(null, url, out moniker);
            IBindCtx bindContext = null;
            iResult = win32.CreateBindCtx(0, out bindContext);
            iResult = persistMoniker.Load(0, moniker, bindContext, constants.STGM_READ);
            persistMoniker = null;
            bindContext = null;
            moniker = null;
        }
        public static void LoadDocument(ref HTMLDocument doc, String documentVal)
        {
            LoadDocument(ref doc, documentVal, false, true);
        }
        public static void LoadDocument(ref HTMLDocument doc, String documentVal, bool LoadAsAnsi, bool CreateSite)
        {
            LoadDocument(ref doc, documentVal, LoadAsAnsi, CreateSite, Encoding.UTF8);
        }
        public static void LoadDocument(ref HTMLDocument doc, String documentVal, bool LoadAsAnsi, bool CreateSite,Encoding EncodingToAddPreamble)
        {
            if (doc == null)
            {
                throw new HtmlEditorException("Null document passed to LoadDocument");
            }
            IHTMLDocument2 htmldoc = (IHTMLDocument2)doc;
            bool isWin98 = (System.Environment.OSVersion.Platform == PlatformID.Win32Windows);
            if (documentVal == string.Empty )
            {
                documentVal = "<html></html>";
            }
            if (CreateSite)
            {
                DownloadOnlySite ds = new DownloadOnlySite();
                IOleObject ob = (IOleObject)doc;
                ob.SetClientSite(ds);
            }
            IStream stream = null;
            if (isWin98 | LoadAsAnsi)
            {
                win32.CreateStreamOnHGlobal(Marshal.StringToHGlobalAnsi(documentVal), 1, out
     stream);
            }
            else
            {
                if (!isBOMPresent(documentVal))
                {
                    if (EncodingToAddPreamble != null)
                    {
                        byte[] preamble = EncodingToAddPreamble.GetPreamble();
                        String byteOrderMark = EncodingToAddPreamble.GetString(preamble, 0, preamble.Length);
                        documentVal = byteOrderMark + documentVal;
                    }
                }
                win32.CreateStreamOnHGlobal(Marshal.StringToHGlobalUni(documentVal), 1, out stream);
            }
            if (stream == null)
            {
                throw new HtmlEditorException("Could not allocate document stream");
            }
            if (isWin98)
            {
                ulong thesize = 0;
                IntPtr ptr = IntPtr.Zero;
                int iSizeOfInt64 = Marshal.SizeOf(typeof(Int64));
                ptr = Marshal.AllocHGlobal(iSizeOfInt64);
                if (ptr == IntPtr.Zero)
                {
                    throw new HtmlEditorException("Could not load document");
                }
                stream.Seek(0, 2, ptr);
                thesize = (ulong)Marshal.ReadInt64(ptr);
                Marshal.FreeHGlobal(ptr);
                stream.SetSize((long)thesize);
                if (thesize != (ulong)documentVal.Length + 1)
                {
                    Debug.Assert(true, "Size of stream is unexpected", "The size of the stream is not equal to the length of the string passed to it.");
                    stream.SetSize(documentVal.Length + 1);
                }
            }
            stream.Seek(0, 0, IntPtr.Zero);
            IPersistStreamInit persistentStreamInit = (IPersistStreamInit)
                doc;
            if (persistentStreamInit != null)
            {
                int iRetVal = 0;
                iRetVal = persistentStreamInit.InitNew();
                if (iRetVal == HRESULT.S_OK)
                {
                    iRetVal = persistentStreamInit.Load(stream);
                    if (iRetVal != HRESULT.S_OK)
                    {
                        throw new HtmlEditorException("Could not load document");
                    }
                }
                else
                {
                    throw new HtmlEditorException("Could not load document");
                }
                persistentStreamInit = null;
            }
            else
            {
                throw new HtmlEditorException("Could not load document");
            }
            stream = null;
        }
        public static String GetDocumentSource(ref HTMLDocument doc)
        {
            return GetDocumentSource(ref doc, EncodingType.Auto);
        }
        public static String GetDocumentSource(ref HTMLDocument doc, Encoding enc)
        {
            if (doc == null) return null;
            bool IsUnicodeDetermined = false;
            Encoding theEncoding = enc;
            if (theEncoding == null)
            {
                theEncoding = Encoding.GetEncoding(0);
            }
            if (theEncoding != Encoding.GetEncoding(0))
            {
                IsUnicodeDetermined = true;
            }
            MemoryStream memstream = new MemoryStream();
            ComStream cstream = new ComStream(memstream);
            IPersistStreamInit pStreamInit = (IPersistStreamInit)doc;
            pStreamInit.Save(cstream, false);
            StringBuilder Result = new StringBuilder();
            memstream.Seek(0, SeekOrigin.Begin);
            int iSize = 2048;
            byte[] bytedata = new byte[2048];
            int iBOMLength = 0;
            while (true)
            {
                iSize = memstream.Read(bytedata, 0, bytedata.Length);
                if (iSize > 0)
                {
                    if (!IsUnicodeDetermined)
                    {
                        bool IsUTF16LE = false;
                        bool IsUTF16BE = false;
                        bool IsUTF8 = false;
                        bool IsBOMPresent = false;
                        if ((bytedata[0] == 0xFF) & (bytedata[1] == 0xFE))
                        {
                            IsUTF16LE = true;
                            IsBOMPresent = true;
                        }
                        if ((bytedata[0] == 0xFE) & (bytedata[1] == 0xFF))
                        {
                            IsUTF16BE = true;
                            IsBOMPresent = true;
                        }
                        if ((bytedata[0] == 0xEF) & (bytedata[1] == 0xBB) & (bytedata[2] == 0xBF))
                        {
                            IsUTF8 = true;
                            IsBOMPresent = true;
                        }
                        if (!IsUTF16LE & !IsUTF16BE & !IsUTF8)
                        {
                            if ((bytedata[1] == 0) & (bytedata[3] == 0) & (bytedata[5] == 0) & (bytedata[7] == 0))
                            {
                                IsUTF16LE = true;
                            }
                        }
                        if (IsUTF16LE)
                        {
                            theEncoding = Encoding.Unicode;
                        }
                        else if (IsUTF16BE)
                        {
                            theEncoding = Encoding.BigEndianUnicode;
                        }
                        else if (IsUTF8)
                        {
                            theEncoding = Encoding.UTF8;
                        }
                        if (IsBOMPresent)
                        {
                            iBOMLength = theEncoding.GetPreamble().Length;
                        }
                        IsUnicodeDetermined = true;
                    }
                    Result.Append(theEncoding.GetString(bytedata, iBOMLength, iSize));
                }
                else
                {
                    break;
                }
            }
            memstream.Close();
            return Result.ToString();
        }
        public static String GetDocumentSource(ref HTMLDocument doc, EncodingType theDocumentEncoding)
        {
            Encoding theEncoding;
            switch (theDocumentEncoding)
            {
                case EncodingType.Auto:
                    IHTMLDocument2 htmldoc = (IHTMLDocument2)doc;
                    String theCharSet = htmldoc.GetCharset();
                    theEncoding = Encoding.GetEncoding(theCharSet);
                    Debug.Assert(theEncoding != null,"Auto Encoding is null");
                    break;
                case EncodingType.ASCII:
                    theEncoding = new ASCIIEncoding();
                    break;
                case EncodingType.Unicode:
                    theEncoding = new UnicodeEncoding();
                    break;
                case EncodingType.UTF7:
                    theEncoding = new UTF7Encoding();
                    break;
                case EncodingType.UTF8:
                    theEncoding = new UTF8Encoding();
                    break;
                case EncodingType.WindowsCurrent:
                    theEncoding = Encoding.GetEncoding(0);
                    break;
                default:
                    theEncoding = Encoding.GetEncoding(0);
                    break;
            }
            return GetDocumentSource(ref doc, theEncoding);
        }
        internal static uint GetCsColor(Color col)
        {
            return (uint)(col.R + (col.G * 256) + (col.B * 65536));
        }
        private static bool isBOMPresent(string sVal)
        {
            if (sVal.Length < 4)
            {
                return false;
            }
            byte[] preamblebytes;
            preamblebytes = Encoding.UTF8.GetBytes(sVal.ToCharArray(0, 4));
            if (isStartOfArrayEqual(preamblebytes,Encoding.UTF8.GetPreamble()))
            {
                return true;
            }
            preamblebytes = Encoding.Unicode.GetBytes(sVal.ToCharArray(0, 4));
            if (isStartOfArrayEqual(preamblebytes,Encoding.Unicode.GetPreamble()))
            {
                return true;
            }
            preamblebytes = Encoding.BigEndianUnicode.GetBytes(sVal.ToCharArray(0, 4));
            if (isStartOfArrayEqual(preamblebytes, Encoding.BigEndianUnicode.GetPreamble()))
            {
                return true;
            }
            return false;
        }
        private static bool isStartOfArrayEqual(byte[] arr1, byte[] arr2)
        {
            if (arr1.Length < arr2.Length)
                return false;
            for (int i = 0; i < arr2.Length; i++)
                if (arr1[i] != arr2[i])
                    return false;
            return true;
        }
    }
}
