

package edu.rice.cs.util;

import java.io.*;
import java.util.*;
import java.util.jar.*;
import java.security.*;


public class MD5ChecksumProperties extends Properties {
  public static final int BUFFER_SIZE = 10*1024;
  
  public MD5ChecksumProperties() { super(); }
  public MD5ChecksumProperties(Properties p) { super(p); }

  
  public static byte[] getMD5(InputStream is, OutputStream os) throws IOException {
    try {
      MessageDigest digest = MessageDigest.getInstance("MD5");
      DigestInputStream dis = new DigestInputStream(new BufferedInputStream(is), digest);
      BufferedOutputStream bos = null;
      if (os!=null) {
        bos = new BufferedOutputStream(os);
      }
      byte[] buf = new byte[BUFFER_SIZE];
      int bytesRead = 0;
      while((bytesRead=dis.read(buf, 0, BUFFER_SIZE))!=-1) {
        if (os!=null) { bos.write(buf, 0, bytesRead); }
      }
      if (os!=null) { bos.flush(); }
      dis.close();
      is.close();
      return digest.digest();
    }
    catch(NoSuchAlgorithmException nsae) {
      throw new UnexpectedException(nsae,"MD5 algorithm not available");
    }
  }
  
  
  public static byte[] getMD5(InputStream is) throws IOException {
    return getMD5(is, null);
  }
  
  
  public static String getMD5String(InputStream is, OutputStream os) throws IOException {
    byte[] messageDigest = getMD5(is,os);
    StringBuilder hexString = new StringBuilder();
    for (int i=0;i<messageDigest.length;i++) {
      String oneByte = "0"+Integer.toHexString(0xFF & messageDigest[i]);
      hexString.append(oneByte.substring(oneByte.length()-2,oneByte.length()));
    }
    return hexString.toString();
  }
  
  
  public static String getMD5String(InputStream is) throws IOException {
    return getMD5String(is, null);
  }
  
  public static byte[] getMD5(File f) throws IOException {
    return getMD5(new FileInputStream(f));
  }
  
  public static String getMD5String(File f) throws IOException {
    return getMD5String(new FileInputStream(f));
  }
  
  public static byte[] getMD5(byte[] b) throws IOException {
    return getMD5(new ByteArrayInputStream(b));
  }
  
  public static String getMD5String(byte[] b) throws IOException {
    return getMD5String(new ByteArrayInputStream(b));
  }
  
  
  public boolean addMD5(String key, InputStream is, OutputStream os) throws IOException {
    String md5 = getMD5String(is, os);
    Object prev = setProperty(key,md5);
    return ((prev==null) || (prev.equals(md5)));
  }

  
  public boolean addMD5(String key, InputStream is) throws IOException {
    return addMD5(key, is, null);
  }

  
  public boolean addMD5(String key, File f, OutputStream os) throws IOException {
    return addMD5(key, new FileInputStream(f), os);
  }
  
  
  public boolean addMD5(String key, File f) throws IOException {
    return addMD5(key, f, null);
  }
  
  
  public boolean addMD5(String key, byte[] b, OutputStream os) throws IOException {
    return addMD5(key, new ByteArrayInputStream(b), os);
  }

  
  public boolean addMD5(String key, byte[] b) throws IOException {
    return addMD5(key, b, null);
  }

  
  public boolean addMD5(File f, OutputStream os) throws IOException {
    return addMD5(f.getPath().replace('\\','/'), f, os);
  }

  
  public boolean addMD5(File f) throws IOException {
    return addMD5(f, null);
  }
  
  
  public static void main(String[] args) throws IOException {
    InputStream is = System.in;
    OutputStream os = System.out;
    Properties prevp = new Properties();
    
    if (args.length==2) {
      File outFile = new File(args[1]);
      if (outFile.exists()) {
        FileInputStream pis = new FileInputStream(outFile);
        prevp.load(pis);
        pis.close();
      }
      is = new FileInputStream(new File(args[0]));
      os = new FileOutputStream(outFile);
    }
    else if (args.length==1) {
      File outFile = new File(args[0]);
      if (outFile.exists()) {
        FileInputStream pis = new FileInputStream(outFile);
        prevp.load(pis);
        pis.close();
      }
      os = new FileOutputStream(outFile);
    }
    
    BufferedReader br = new BufferedReader(new InputStreamReader(is));
    MD5ChecksumProperties p = new MD5ChecksumProperties();
    p.putAll(prevp);
    String line;
    while((line = br.readLine())!=null) {
      if (line.equals("")) break;
      p.addMD5(new File(line));
    }
    p.store(os,"MD5 Checksums");
  }
}