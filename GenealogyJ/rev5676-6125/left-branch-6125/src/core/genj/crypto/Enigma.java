
package genj.crypto;

import java.io.IOException;


public abstract class Enigma {
  
  
  private static final String IMPL = "genj.crypto.EnigmaImpl";
  
  
  private final static String PREFIX = "[private]";
  
  
  private static boolean isAvailable = getInstance("") != null;
  
  
  public static Enigma getInstance(String password) {
    
    try {
      return ((Enigma)Class.forName(IMPL).newInstance()).init(password);
    } catch (Throwable t) {
      return null;
    }
    
  }
  
  
  public static boolean isAvailable() {
    return isAvailable;
  }
  
  
  public static boolean isEncrypted(String value) {
    return value.startsWith(PREFIX);
  }
  
    
  public String encrypt(String value) throws IOException {
    return PREFIX+encryptImpl(value);
  }

    
  public String decrypt(String value) throws IOException {
    if (!isEncrypted(value))
      throw new IOException("Not an encrypted value");
    return decryptImpl(value.substring(PREFIX.length()));
  }

  
  protected abstract Enigma init(String password);
  
  
  protected abstract String encryptImpl(String value) throws IOException;
  
  
  protected abstract String decryptImpl(String value) throws IOException;
  
} 
