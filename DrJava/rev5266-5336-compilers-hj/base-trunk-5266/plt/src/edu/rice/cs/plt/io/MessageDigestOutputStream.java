

package edu.rice.cs.plt.io;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class MessageDigestOutputStream extends DirectOutputStream {
  private final MessageDigest _messageDigest;
  
  
  public MessageDigestOutputStream(MessageDigest messageDigest) { _messageDigest = messageDigest; }
  
  
  public byte[] digest() { return _messageDigest.digest(); }
  
  @Override public void close() {}
  @Override public void flush() {}
  @Override public void write(byte[] bbuf) { _messageDigest.update(bbuf); }
  @Override public void write(byte[] bbuf, int offset, int len) { _messageDigest.update(bbuf, offset, len); }
  @Override public void write(int b) { _messageDigest.update((byte) b); }
  
  
  public static MessageDigestOutputStream makeMD5() {
    try { return new MessageDigestOutputStream(MessageDigest.getInstance("MD5")); }
    catch (NoSuchAlgorithmException e) { throw new RuntimeException(e); }
  }
  
  
  public static MessageDigestOutputStream makeSHA1() {
    try { return new MessageDigestOutputStream(MessageDigest.getInstance("SHA-1")); }
    catch (NoSuchAlgorithmException e) { throw new RuntimeException(e); }
  }
  
  
  public static MessageDigestOutputStream makeSHA256() {
    try { return new MessageDigestOutputStream(MessageDigest.getInstance("SHA-256")); }
    catch (NoSuchAlgorithmException e) { throw new RuntimeException(e); }
  }
  
}
