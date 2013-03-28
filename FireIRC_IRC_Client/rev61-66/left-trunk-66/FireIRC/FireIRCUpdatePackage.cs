using System;
using System.Collections.Generic;
using System.Text;
namespace FireIRC
{
    public class FireIRCUpdatePackage
    {
        public struct FilePackage
        {
            string fileName;
            byte[] fileData;
            public byte[] FileData
            {
                get { return fileData; }
                set { fileData = value; }
            }
            public string FileName
            {
                get { return fileName; }
                set { fileName = value; }
            }
            public FilePackage(string f, byte[] fd)
            {
                fileName = f;
                fileData = fd;
            }
        }
        List<FilePackage> files = new List<FilePackage>();
        public List<FilePackage> Files
        {
            get { return files; }
        }
    }
}
