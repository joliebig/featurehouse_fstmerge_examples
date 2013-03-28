using System;
using System.IO;
using System.Runtime.InteropServices;
using Microsoft.Win32.SafeHandles;
using System.Collections.ObjectModel;
namespace NewsComponents.Utils
{
    internal class Kernel32
    {
        public const char STREAM_SEP = ':';
        public const int INVALID_HANDLE_VALUE = -1;
        public const int MAX_PATH = 256;
        [Flags]
        public enum FileFlags : uint
        {
            WriteThrough = 0x80000000,
            Overlapped = 0x40000000,
            NoBuffering = 0x20000000,
            RandomAccess = 0x10000000,
            SequentialScan = 0x8000000,
            DeleteOnClose = 0x4000000,
            BackupSemantics = 0x2000000,
            PosixSemantics = 0x1000000,
            OpenReparsePoint = 0x200000,
            OpenNoRecall = 0x100000
        }
        [Flags]
        public enum FileAccessAPI : uint
        {
            GENERIC_READ = 0x80000000,
            GENERIC_WRITE = 0x40000000
        }
        public static FileAccessAPI Access2API(FileAccess Access)
        {
            FileAccessAPI lRet = 0;
            if ((Access & FileAccess.Read) == FileAccess.Read) lRet |= FileAccessAPI.GENERIC_READ;
            if ((Access & FileAccess.Write) == FileAccess.Write) lRet |= FileAccessAPI.GENERIC_WRITE;
            return lRet;
        }
        [StructLayout(LayoutKind.Sequential)]
        public struct LARGE_INTEGER
        {
            public int Low;
            public int High;
            public long ToInt64()
            {
                return High*4294967296L + Low;
            }
        }
        [StructLayout(LayoutKind.Sequential)]
        public struct WIN32_STREAM_ID
        {
            public int dwStreamID;
            public int dwStreamAttributes;
            public LARGE_INTEGER Size;
            public int dwStreamNameSize;
        }
        [DllImport("kernel32")]
        public static extern SafeFileHandle CreateFile(string Name, FileAccessAPI Access, FileShare Share, int Security,
                                               FileMode Creation, FileFlags Flags, int Template);
        [DllImport("kernel32")]
        public static extern bool DeleteFile(string Name);
        [DllImport("kernel32")]
        public static extern bool BackupRead(SafeFileHandle hFile, IntPtr pBuffer, int lBytes, ref int lRead, bool bAbort,
                                             bool bSecurity, ref int Context);
        [DllImport("kernel32")]
        public static extern bool BackupRead(SafeFileHandle hFile, ref WIN32_STREAM_ID pBuffer, int lBytes, ref int lRead,
                                             bool bAbort, bool bSecurity, ref int Context);
        [DllImport("kernel32")]
        public static extern bool BackupSeek(SafeFileHandle hFile, int dwLowBytesToSeek, int dwHighBytesToSeek, ref int dwLow,
                                             ref int dwHigh, ref int Context);
    }
    public class StreamInfo : IEquatable<StreamInfo>
    {
        private readonly FileStreams _parent;
        private readonly string _name;
        private readonly long _size;
        internal StreamInfo(FileStreams Parent, string Name, long Size)
        {
            _parent = Parent;
            _name = Name;
            _size = Size;
        }
        public string Name
        {
            get
            {
                return _name;
            }
        }
        public long Size
        {
            get
            {
                return _size;
            }
        }
        public override string ToString()
        {
            return String.Format("{1}{0}{2}{0}$DATA", Kernel32.STREAM_SEP, _parent.FileName, _name);
        }
        public override bool Equals(Object o)
        {
            if (o is string)
            {
                return ((string)o).Equals(ToString());
            }
            else
                return Equals(o as StreamInfo);
        }
        public override int GetHashCode()
        {
            return ToString().GetHashCode();
        }
        public FileStream Open()
        {
            return Open(FileMode.OpenOrCreate, FileAccess.ReadWrite, FileShare.None);
        }
        public FileStream Open(FileMode Mode)
        {
            return Open(Mode, FileAccess.ReadWrite, FileShare.None);
        }
        public FileStream Open(FileMode Mode, FileAccess Access)
        {
            return Open(Mode, Access, FileShare.None);
        }
        public FileStream Open(FileMode Mode, FileAccess Access, FileShare Share)
        {
            try
            {
                SafeFileHandle hFile = Kernel32.CreateFile(ToString(), Kernel32.Access2API(Access), Share, 0, Mode, 0, 0);
                return new FileStream(hFile, Access);
            }
            catch
            {
                return null;
            }
        }
        public bool Delete()
        {
            return Kernel32.DeleteFile(ToString());
        }
        public bool Equals(StreamInfo other)
        {
            if(ReferenceEquals(other, null))
                return false;
            return (other._name.Equals(_name) && other._parent.Equals(_parent));
        }
    }
    public class FileStreams : Collection<StreamInfo>
    {
        private readonly FileInfo _file;
        public FileStreams(string File)
        {
            _file = new FileInfo(File);
            initStreams();
        }
        public FileStreams(FileInfo file)
        {
            _file = file;
            initStreams();
        }
        private void initStreams()
        {
            using (SafeFileHandle hFile =
                Kernel32.CreateFile(_file.FullName, Kernel32.FileAccessAPI.GENERIC_READ, FileShare.Read, 0,
                                    FileMode.Open, Kernel32.FileFlags.BackupSemantics, 0))
            {
                if (hFile.IsInvalid) return;
                Kernel32.WIN32_STREAM_ID sid = new Kernel32.WIN32_STREAM_ID();
                int dwStreamHeaderSize = Marshal.SizeOf(sid);
                int Context = 0;
                bool Continue = true;
                while (Continue)
                {
                    int lRead = 0;
                    Continue =
                        Kernel32.BackupRead(hFile, ref sid, dwStreamHeaderSize, ref lRead, false, false, ref Context);
                    if (Continue && lRead == dwStreamHeaderSize)
                    {
                        if (sid.dwStreamNameSize > 0)
                        {
                            lRead = 0;
                            IntPtr pName = Marshal.AllocHGlobal(sid.dwStreamNameSize);
                            try
                            {
                                Kernel32.BackupRead(hFile, pName, sid.dwStreamNameSize, ref lRead, false, false,
                                                    ref Context);
                                char[] bName = new char[sid.dwStreamNameSize];
                                Marshal.Copy(pName, bName, 0, sid.dwStreamNameSize);
                                string sName = new string(bName);
                                int i = sName.IndexOf(Kernel32.STREAM_SEP, 1);
                                if (i > -1) sName = sName.Substring(1, i - 1);
                                else
                                {
                                    i = sName.IndexOf('\0');
                                    if (i > -1) sName = sName.Substring(1, i - 1);
                                }
                                Items.Add(new StreamInfo(this, sName, sid.Size.ToInt64()));
                            }
                            finally
                            {
                                Marshal.FreeHGlobal(pName);
                            }
                        }
                        int l = 0;
                        int h = 0;
                        Continue =
                            Kernel32.BackupSeek(hFile, sid.Size.Low, sid.Size.High, ref l, ref h, ref Context);
                    }
                    else break;
                }
            }
        }
        public FileInfo FileInfo
        {
            get
            {
                return _file;
            }
        }
        public string FileName
        {
            get
            {
                return _file.FullName;
            }
        }
        public long FileSize
        {
            get
            {
                return _file.Length;
            }
        }
        public long Size
        {
            get
            {
                long size = this.FileSize;
                foreach (StreamInfo s in this) size += s.Size;
                return size;
            }
        }
        public FileStream Open()
        {
            return new FileStream(_file.FullName, FileMode.OpenOrCreate);
        }
        public FileStream Open(FileMode Mode)
        {
            return new FileStream(_file.FullName, Mode);
        }
        public FileStream Open(FileMode Mode, FileAccess Access)
        {
            return new FileStream(_file.FullName, Mode, Access);
        }
        public FileStream Open(FileMode Mode, FileAccess Access, FileShare Share)
        {
            return new FileStream(_file.FullName, Mode, Access, Share);
        }
        public void Delete()
        {
            for (int i = Items.Count; i > 0; i--)
            {
                Items.RemoveAt(i);
            }
            _file.Delete();
        }
        public void Add(string Name)
        {
            StreamInfo FSI = new StreamInfo(this, Name, 0);
            int i = Items.IndexOf(FSI);
            if (i == -1)
                Items.Add(FSI);
        }
        public void Remove(string Name)
        {
            StreamInfo FSI = new StreamInfo(this, Name, 0);
            int i = Items.IndexOf(FSI);
            if (i > -1)
                Items.RemoveAt(i);
        }
        public int IndexOf(string Name)
        {
            return Items.IndexOf(new StreamInfo(this, Name, 0));
        }
        public StreamInfo this[string Name]
        {
            get
            {
                int i = IndexOf(Name);
                if (i == -1) return null;
                else
                    return Items[i];
            }
        }
        protected override void RemoveItem(int index)
        {
            StreamInfo fsi = this[index];
            base.RemoveItem(index);
            if (fsi != null)
                fsi.Delete();
        }
    }
}
