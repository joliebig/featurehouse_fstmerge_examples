
package genj.util;


public class Base64 {

  
  public static byte decode(char c) throws IllegalArgumentException {
    if (c >= 'A' && c <= 'Z') {
      return (byte) (c - 'A');
    }
    if (c >= 'a' && c <= 'z') {
      return (byte) (c - 'a' + 26);
    }
    if (c >= '0' && c <= '9') {
      return (byte) (c - '0' +52);
    }
    if (c == '+') {
      return (byte) 62;
    }
    if (c == '/') {
      return (byte) 63;
    }
    if (c == '=') {
      return (byte) 0;
    }
    throw new IllegalArgumentException("Illegal Base64 byte ("+c+")");
  }

  
  public static byte[] decode(String in) throws IllegalArgumentException {

    if ( ( (in.length() % 4) != 0) || (in.length()==0) ) {
      throw new IllegalArgumentException("Illegal Base64 String");
    }

    
    int pad = 0;
    for (int i=in.length()-1; in.charAt(i)=='='; i--)
      pad++;
    int len = in.length() * 3 / 4 - pad;

    
    byte[] raw = new byte[len];
    int rawIndex = 0;

    for (int i=0; i<in.length(); i+=4) {

      
      int block =
      (Base64.decode(in.charAt(i + 0)) << 18) +
      (Base64.decode(in.charAt(i + 1)) << 12) +
      (Base64.decode(in.charAt(i + 2)) <<  6) +
      (Base64.decode(in.charAt(i + 3))      ) ;

      
      for (int j=0; j<3 && rawIndex+j < raw.length; j++)
      raw[ rawIndex+j ] = (byte) ((block >> (8 * (2-j))) & 0xff);

      rawIndex +=3;
    }

    
    return raw;
  }

  
  public static String encode(byte[] raw) {

    StringBuffer encoded = new StringBuffer( (raw.length+1)*4/3 );
    for (int i=0; i<raw.length; i+=3) {
      encoded.append(encodeBlock(raw,i));
    }

    return encoded.toString();
  }

  
  public static char encode(int i) {
    if (i >= 0 && i <= 25) {
      return (char)( 'A' + i );
    }
    if (i >= 26 && i <= 51) {
      return (char)( 'a' + (i - 26) );
    }
    if (i >= 52 && i <=61 ) {
      return (char)( '0' + (i - 52) );
    }
    if (i == 62) {
      return (char)( '+' );
    }
    if (i == 63) {
      return (char)( '/' );
    }
    return (char)( '?' );
  }

  
  private static char[] encodeBlock(byte[] raw, int offset) {

    
    int block = 0;
    int left  = raw.length - offset -1;
    int bsize = (left>=2) ? 2 : left;

    
    for (int i=0; i<=bsize; i++) {
      byte b = raw[offset+i];
      block += (b<0?b+256:b) << (8* (2-i));
    }

    
    char[] base64 = new char[4];
    for (int i=0; i<4; i++) {
      int sixbit = (block >>> (6 * (3-i))) & 0x3f ;
      base64[i] = encode(sixbit);
    }

    if (left<1) {
      base64[2] = '=';
    }
    if (left<2) {
      base64[3] = '=';
    }

    return base64;
  }

}
