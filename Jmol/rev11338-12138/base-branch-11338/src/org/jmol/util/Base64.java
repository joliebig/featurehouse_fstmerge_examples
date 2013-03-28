













package org.jmol.util;

import java.io.IOException;
import java.io.OutputStream;

public class Base64 {

  
  
  private static String base64 = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";
  
  
  
  
  
  
  
  private static int[] decode64 = new int[] {
    0,0,0,0,     0,0,0,0,     0,0,0,0,     0,0,0,0,      
    0,0,0,0,     0,0,0,0,     0,0,0,0,     0,0,0,0,      
    0,0,0,0,     0,0,0,0,     0,0,0,62,    0,62,0,63,    
    52,53,54,55, 56,57,58,59, 60,61,0,0,   0,0,0,0,      
    0,0,1,2,     3,4,5,6,     7,8,9,10,    11,12,13,14,  
    15,16,17,18, 19,20,21,22, 23,24,25,0,  0,0,0,63,     
    0,26,27,28,  29,30,31,32, 33,34,35,36, 37,38,39,40,  
    41,42,43,44, 45,46,47,48, 49,50,51,0,  0,0,0,0,      
  };
    
  public static void write(byte[] bytes, OutputStream os) throws IOException {
    StringBuffer sb = getBase64(bytes);
    int len = sb.length();
    byte[] b = new byte[1];
    for (int i = 0; i < len; i++) {
      b[0] = (byte) sb.charAt(i);
      os.write(b);
    }
  }

  public static byte[] getBytes64(byte[] bytes) {
    return toBytes(getBase64(bytes));
  }

  public static StringBuffer getBase64(StringBuffer str) {
    return getBase64(toBytes(str));  
  }

  public static StringBuffer getBase64(byte[] bytes) {
    long nBytes = bytes.length;
    StringBuffer sout = new StringBuffer();
    if (nBytes == 0)
      return sout;
    for (int i = 0, nPad = 0; i < nBytes && nPad == 0;) {
      if (i % 75 == 0 && i != 0)
        sout.append("\r\n");
      nPad = (i + 2 == nBytes ? 1 : i + 1 == nBytes ? 2 : 0);
      int outbytes = (((int) (bytes[i++]) << 16) & 0xFF0000)
          | ((nPad == 2 ? 0 : (int) (bytes[i++]) << 8) & 0x00FF00)
          | ((nPad >= 1 ? 0 : (int) bytes[i++]) & 0x0000FF);
      
      sout.append(base64.charAt((outbytes >> 18) & 0x3F));
      sout.append(base64.charAt((outbytes >> 12) & 0x3F));
      sout.append(nPad == 2 ? '=' : base64.charAt((outbytes >> 6) & 0x3F));
      sout.append(nPad >= 1 ? '=' : base64.charAt(outbytes & 0x3F));
    }
    return sout;
  }

  
  
  
  
  
  
  public static byte[] decodeBase64(String strBase64) {
    int nBytes = 0;
    int ch;
    char[] chars64 = strBase64.toCharArray();
    int len64 = chars64.length;
    if (len64 == 0)
      return new byte[0];
    for (int i = len64; --i >= 0;)
      nBytes += ((ch = chars64[i] & 0x7F) == 'A' || decode64[ch] > 0 ? 3 : 0);
    nBytes = nBytes >> 2;
    byte[] bytes = new byte[nBytes];
    int offset = 18;
    for (int i = 0, pt = 0, b = 0; i < len64; i++) {
      if (decode64[ch = chars64[i] & 0x7F] > 0 || ch == 'A' || ch == '=') {
        b |= decode64[ch] << offset;
        
        offset -= 6;
        if (offset < 0) {
          bytes[pt++] = (byte) ((b & 0xFF0000) >> 16);
          if (pt < nBytes)
            bytes[pt++] = (byte) ((b & 0xFF00) >> 8);
          if (pt < nBytes)
            bytes[pt++] = (byte) (b & 0xFF);
          offset = 18;
          b =  0;
        }
      }
    }
    return bytes;
  }  
  
  private static byte[] toBytes(StringBuffer sb) {
    byte[] b = new byte[sb.length()];
    for (int i = sb.length(); --i >= 0;)
      b[i] = (byte) sb.charAt(i);
    return b;
  }

}