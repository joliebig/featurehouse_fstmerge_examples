using System;
using System.Collections.Generic;
using System.Text;
using System.IO;
namespace ThoughtWorks.CruiseControl.Remote
{
    [Serializable]
    public class RemotingFileTransfer
        : IFileTransfer, IDisposable
    {
        private const int blockSize = 131072;
        private readonly RemotingStreamHolder streamHolder;
        public RemotingFileTransfer(Stream source)
        {
            streamHolder = new RemotingStreamHolder(source);
        }
        public void Download(Stream destination)
        {
            streamHolder.Reset();
            int count = blockSize;
            while (count > 0)
            {
                TransferPackage package = streamHolder.TransferData(blockSize);
                count = package.Length;
                destination.Write(package.Data, 0, count);
            }
        }
        public void Dispose()
        {
            streamHolder.Dispose();
        }
        private class RemotingStreamHolder
            : MarshalByRefObject, IDisposable
        {
            private readonly Stream stream;
            public RemotingStreamHolder(Stream source)
            {
                stream = source;
            }
            public TransferPackage TransferData(int length)
            {
                byte[] data = new byte[length];
                int transferedLength = stream.Read(data, 0, length);
                return new TransferPackage(data, transferedLength);
            }
            public void Reset()
            {
                stream.Seek(0, SeekOrigin.Begin);
            }
            public void Dispose()
            {
                stream.Dispose();
            }
        }
        [Serializable]
        private class TransferPackage
        {
            private readonly byte[] data;
            private readonly int length;
            public TransferPackage(byte[] data, int length)
            {
                this.data = data;
                this.length = length;
            }
            public byte[] Data
            {
                get { return data; }
            }
            public int Length
            {
                get { return length; }
            }
        }
    }
}
