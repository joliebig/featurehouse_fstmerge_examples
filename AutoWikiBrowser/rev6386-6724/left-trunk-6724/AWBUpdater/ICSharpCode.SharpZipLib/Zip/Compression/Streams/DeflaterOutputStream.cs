using System;
using System.IO;
using ICSharpCode.SharpZipLib.Checksums;
using ICSharpCode.SharpZipLib.Zip.Compression;
using System.Security.Cryptography;
using ICSharpCode.SharpZipLib.Encryption;
namespace ICSharpCode.SharpZipLib.Zip.Compression.Streams
{
 public class DeflaterOutputStream : Stream
 {
  public DeflaterOutputStream(Stream baseOutputStream)
   : this(baseOutputStream, new Deflater(), 512)
  {
  }
  public DeflaterOutputStream(Stream baseOutputStream, Deflater deflater)
   : this(baseOutputStream, deflater, 512)
  {
  }
  public DeflaterOutputStream(Stream baseOutputStream, Deflater deflater, int bufferSize)
  {
   if ( baseOutputStream == null ) {
    throw new ArgumentNullException("baseOutputStream");
   }
   if (baseOutputStream.CanWrite == false) {
    throw new ArgumentException("Must support writing", "baseOutputStream");
   }
   if (deflater == null) {
    throw new ArgumentNullException("deflater");
   }
   if (bufferSize <= 0) {
    throw new ArgumentOutOfRangeException("bufferSize");
   }
   baseOutputStream_ = baseOutputStream;
   buffer_ = new byte[bufferSize];
   deflater_ = deflater;
  }
  public virtual void Finish()
  {
   deflater_.Finish();
   while (!deflater_.IsFinished) {
    int len = deflater_.Deflate(buffer_, 0, buffer_.Length);
    if (len <= 0) {
     break;
    }
    if (cryptoTransform_ != null) {
     EncryptBlock(buffer_, 0, len);
    }
    baseOutputStream_.Write(buffer_, 0, len);
   }
   if (!deflater_.IsFinished) {
    throw new SharpZipBaseException("Can't deflate all input?");
   }
   baseOutputStream_.Flush();
   if (cryptoTransform_ != null) {
    cryptoTransform_.Dispose();
    cryptoTransform_ = null;
   }
  }
  public bool IsStreamOwner
  {
   get { return isStreamOwner_; }
   set { isStreamOwner_ = value; }
  }
  public bool CanPatchEntries {
   get {
    return baseOutputStream_.CanSeek;
   }
  }
  string password;
  ICryptoTransform cryptoTransform_;
  public string Password {
   get {
    return password;
   }
   set {
    if ( (value != null) && (value.Length == 0) ) {
     password = null;
    } else {
     password = value;
    }
   }
  }
  protected void EncryptBlock(byte[] buffer, int offset, int length)
  {
   cryptoTransform_.TransformBlock(buffer, 0, length, buffer, 0);
  }
  protected void InitializePassword(string password)
  {
   PkzipClassicManaged pkManaged = new PkzipClassicManaged();
   byte[] key = PkzipClassic.GenerateKeys(ZipConstants.ConvertToArray(password));
   cryptoTransform_ = pkManaged.CreateEncryptor(key, null);
  }
  protected void Deflate()
  {
   while (!deflater_.IsNeedingInput)
   {
    int deflateCount = deflater_.Deflate(buffer_, 0, buffer_.Length);
    if (deflateCount <= 0)
    {
     break;
    }
    if (cryptoTransform_ != null)
    {
     EncryptBlock(buffer_, 0, deflateCount);
    }
    baseOutputStream_.Write(buffer_, 0, deflateCount);
   }
   if (!deflater_.IsNeedingInput)
   {
    throw new SharpZipBaseException("DeflaterOutputStream can't deflate all input?");
   }
  }
  public override bool CanRead
  {
   get {
    return false;
   }
  }
  public override bool CanSeek {
   get {
    return false;
   }
  }
  public override bool CanWrite {
   get {
    return baseOutputStream_.CanWrite;
   }
  }
  public override long Length {
   get {
    return baseOutputStream_.Length;
   }
  }
  public override long Position {
   get {
    return baseOutputStream_.Position;
   }
   set {
    throw new NotSupportedException("Position property not supported");
   }
  }
  public override long Seek(long offset, SeekOrigin origin)
  {
   throw new NotSupportedException("DeflaterOutputStream Seek not supported");
  }
  public override void SetLength(long value)
  {
   throw new NotSupportedException("DeflaterOutputStream SetLength not supported");
  }
  public override int ReadByte()
  {
   throw new NotSupportedException("DeflaterOutputStream ReadByte not supported");
  }
  public override int Read(byte[] buffer, int offset, int count)
  {
   throw new NotSupportedException("DeflaterOutputStream Read not supported");
  }
  public override IAsyncResult BeginRead(byte[] buffer, int offset, int count, AsyncCallback callback, object state)
  {
   throw new NotSupportedException("DeflaterOutputStream BeginRead not currently supported");
  }
  public override IAsyncResult BeginWrite(byte[] buffer, int offset, int count, AsyncCallback callback, object state)
  {
   throw new NotSupportedException("BeginWrite is not supported");
  }
  public override void Flush()
  {
   deflater_.Flush();
   Deflate();
   baseOutputStream_.Flush();
  }
  public override void Close()
  {
   if ( !isClosed_ ) {
    isClosed_ = true;
    try
    {
     Finish();
     if ( cryptoTransform_ != null ) {
      cryptoTransform_.Dispose();
      cryptoTransform_ = null;
     }
    }
    finally
    {
     if( isStreamOwner_ )
     {
      baseOutputStream_.Close();
     }
    }
   }
  }
  public override void WriteByte(byte value)
  {
   byte[] b = new byte[1];
   b[0] = value;
   Write(b, 0, 1);
  }
  public override void Write(byte[] buffer, int offset, int count)
  {
   deflater_.SetInput(buffer, offset, count);
   Deflate();
  }
  byte[] buffer_;
  protected Deflater deflater_;
  protected Stream baseOutputStream_;
  bool isClosed_;
  bool isStreamOwner_ = true;
 }
}
