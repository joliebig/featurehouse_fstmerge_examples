using System;
using System.Collections.Generic;
using System.Text;
using System.IO;
namespace ThoughtWorks.CruiseControl.Remote
{
    [Serializable]
    public class RemotingFileTransfer
        : IFileTransfer
    {
        private const int blockSize = 131072;
        private byte[] fileData = { };
        public RemotingFileTransfer(Stream source)
        {
            using (var memoryStream = new MemoryStream())
            {
                byte[] data = new byte[blockSize];
                var byteCount = source.Read(data, 0, blockSize);
                while (byteCount > 0)
                {
                    memoryStream.Write(data, 0, byteCount);
                    byteCount = source.Read(data, 0, blockSize);
                }
                fileData = memoryStream.GetBuffer();
            }
        }
        public void Download(Stream destination)
        {
            destination.Write(fileData, 0, fileData.Length);
        }
    }
}
