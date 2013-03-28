
package genj.crypto;

import genj.util.Base64;

import java.io.IOException;
import java.security.spec.KeySpec;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;


 class EnigmaImpl extends Enigma {

  private final static Logger LOG = Logger.getLogger("genj.crypto");
  
  
  private SecretKey key;
  
  
  private Cipher cipher;

  
  private final static String SALT_PADDING = "GENEALOGYJ"; 
  
  
  private final static String ALGORITHM = "DES";

  
  protected Enigma init(String password) {
    
    try {
    
      
      byte[] salt = (password+SALT_PADDING).getBytes("UTF-8");

      
      KeySpec keyspec = new DESKeySpec(salt);

      
      cipher = Cipher.getInstance(ALGORITHM);
  
      
      key = SecretKeyFactory.getInstance(ALGORITHM).generateSecret(keyspec);
      
    } catch (Throwable t) {
      
      LOG.log(Level.WARNING, "Couldn't initialize Enigma", t); 
      return null;
    }
  
    return this;
  }

  
  protected String encryptImpl(String value) throws IOException {
    
    try {
    
      
      byte[] utf8bytes = value.getBytes("UTF-8");
      
      
      cipher.init(Cipher.ENCRYPT_MODE, key);
      byte[] desbytes = cipher.doFinal(utf8bytes); 
      
      
      String base64 = Base64.encode(desbytes).toString();
      
      
      return base64;
      
    } catch (Throwable t) {
      
      throw new IOException("Encrypt failed : "+t+'/'+t.getMessage());
    }
    
  }

  
  protected String decryptImpl(String value) throws IOException {
  
    try {
    
      
      byte[] desbytes = Base64.decode(value);
      
      
      cipher.init(Cipher.DECRYPT_MODE, key);
      byte[] utf8bytes = cipher.doFinal(desbytes);
      
      
      String javaString = new String(utf8bytes, "UTF-8");
      
      
      return javaString;

    } catch (Throwable t) {
      throw new IOException("Decrypt failed : "+t.getMessage());
    }
  }

} 
