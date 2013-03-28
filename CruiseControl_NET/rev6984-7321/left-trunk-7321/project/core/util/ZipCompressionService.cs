namespace ThoughtWorks.CruiseControl.Core.Util
{
    using System;
    using System.IO;
    using System.Text;
    using ICSharpCode.SharpZipLib.Core;
    using ICSharpCode.SharpZipLib.Zip.Compression.Streams;
    public class ZipCompressionService
        : ICompressionService
    {
        public string CompressString(string value)
        {
            if (value == null)
            {
                throw new ArgumentNullException("value");
            }
            var outputData = string.Empty;
            var inputData = UTF8Encoding.UTF8.GetBytes(value);
            using (var inputStream = new MemoryStream(inputData))
            {
                using (var outputStream = new MemoryStream())
                {
                    using (var zipStream = new DeflaterOutputStream(outputStream))
                    {
                        zipStream.IsStreamOwner = false;
                        StreamUtils.Copy(inputStream, zipStream, new byte[4096]);
                    }
                    outputData = Convert.ToBase64String(outputStream.GetBuffer(), 0, Convert.ToInt32(outputStream.Length));
                }
            }
            return outputData;
        }
        public string ExpandString(string value)
        {
            if (value == null)
            {
                throw new ArgumentNullException("value");
            }
            var outputData = string.Empty;
            var inputData = Convert.FromBase64String(value);
            using (var inputStream = new MemoryStream(inputData))
            {
                using (var outputStream = new MemoryStream())
                {
                    using (var zipStream = new InflaterInputStream(inputStream))
                    {
                        zipStream.IsStreamOwner = false;
                        StreamUtils.Copy(zipStream, outputStream, new byte[4096]);
                    }
                    outputData = UTF8Encoding.UTF8.GetString(outputStream.GetBuffer(), 0, Convert.ToInt32(outputStream.Length));
                }
            }
            return outputData;
        }
    }
}
