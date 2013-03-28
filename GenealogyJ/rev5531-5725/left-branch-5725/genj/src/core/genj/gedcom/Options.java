
package genj.gedcom;

import genj.option.Option;
import genj.option.OptionProvider;
import genj.option.PropertyOption;
import genj.util.Resources;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class Options extends OptionProvider {
  
  private final static Resources RESOURCES = Resources.get(Options.class);
  
  
  private final static Options instance = new Options();
  
  
  public boolean isUseSpacedPlaces = true;
  
  
  public boolean isFillGapsInIDs = false;
  
  
  public boolean isUpperCaseNames = false;
  
  
  public boolean setWifeLastname = true;
  
  
  public boolean isAddGivenSurname = false;
  
  
  public int nameFormat = 0;
  public final static String[] nameFormats = {
    RESOURCES.getString("option.nameFormat.first"),
    RESOURCES.getString("option.nameFormat.last")
};

    
  private int maxImageFileSizeKB = 128;
  
  
  private int valueLineBreak = 255;
  
  
  protected String txtMarriageSymbol = "+";

  
  protected int numberOfUndos = 10;
  
  
  protected Set<String> placeHierarchyCityKeys = new HashSet<String>(Arrays.asList(new String[]{ "city", "commune", "ville", "stadt"}));
  
  
  public String maskPrivate = "...";
    
  
  protected int defaultEncoding = Gedcom.ENCODINGS.length-1;
  
  
  public int dateFormat = 1;
  
  public final static String[] dateFormats = {
      RESOURCES.getString("option.dateFormat.gedcom"),
      RESOURCES.getString("option.dateFormat.short"),
      RESOURCES.getString("option.dateFormat.long"),
      RESOURCES.getString("option.dateFormat.numeric")
  };

  
  public static Options getInstance() {
    return instance;
  }

  
  public void setMaxImageFileSizeKB(int max) {
    maxImageFileSizeKB = Math.max(4,max);
  }
  
  
  public int getMaxImageFileSizeKB() {
    return maxImageFileSizeKB;
  }
  
  
  public int getValueLineBreak() {
    return valueLineBreak;
  }

  
  public void setValueLineBreak(int set) {
    valueLineBreak = Math.max(40,set);
  }

  
  public String getTxtMarriageSymbol() {
    return txtMarriageSymbol;
  }

  
  public void setTxtMarriageSymbol(String set) {
    if (set!=null&&set.trim().length()>0)
      txtMarriageSymbol = ' '+set.trim()+' ';
    else
      txtMarriageSymbol = " + ";
  }

  
  public int getNumberOfUndos() {
    return numberOfUndos;
  }

  
  public void setNumberOfUndos(int i) {
    numberOfUndos = Math.max(10,i);
  }

  
  public List<? extends Option> getOptions() {
    return PropertyOption.introspect(instance);
  }

  
  public int getDefaultEncoding() {
    return defaultEncoding;
  }

  
  public void setDefaultEncoding(int setEncoding) {
    if (setEncoding>=0&&setEncoding<Gedcom.ENCODINGS.length)
      defaultEncoding = setEncoding;
  }
  
  public static String[] getDefaultEncodings() {
    return Gedcom.ENCODINGS;
  }

} 
