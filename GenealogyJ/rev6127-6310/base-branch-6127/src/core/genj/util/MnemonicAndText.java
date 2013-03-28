
package genj.util;




public class MnemonicAndText {
  
  private char mnemonic = '\0';
  private String text;

  public MnemonicAndText(String label) {
    
    
    text = label!=null ? label : "";
    
    
    int i = text.indexOf('~');    
    if (i<0) {
      
      mnemonic = text.length()>0 ? text.charAt(0) : 0;
    } else if (i==text.length()-1) {
      
      mnemonic = text.length()>0 ? text.charAt(0) : 0;
      text = text.substring(0, text.length()-1);
    } else {
      
      mnemonic = text.charAt(i+1);
      text = text.substring(0,i)+text.substring(i+1);
    }
    
    
    
    
    
    mnemonic = Character.toUpperCase(mnemonic);
    
    
  }
  
  
  public char getMnemonic() {
    return mnemonic;
  }
  
  
  public String getText(String markup) {
    if (mnemonic==0)
      return getText();
    return text + " ["+markup+mnemonic+"]";
  }

  
  public String getText() {
    return text;
  }

  
  public String toString() {
    return mnemonic==0 ? text : text+"["+mnemonic+"]";
  }
  
}