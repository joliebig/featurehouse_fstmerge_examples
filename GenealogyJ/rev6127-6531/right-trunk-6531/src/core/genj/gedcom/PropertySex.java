
package genj.gedcom;

import genj.util.swing.ImageIcon;


public class PropertySex extends Property {
  
  
  private final static ImageIcon
    IMG_UNKNOWN= Grammar.V55.getMeta(new TagPath("INDI:SEX")).getImage(),
    IMG_MALE   = Grammar.V55.getMeta(new TagPath("INDI:SEX")).getImage("male"),
    IMG_FEMALE = Grammar.V55.getMeta(new TagPath("INDI:SEX")).getImage("female");

  
  public final static String
    TXT_SEX     = Gedcom.getResources().getString("prop.sex"),
    TXT_MALE    = Gedcom.getResources().getString("prop.sex.male"),
    TXT_FEMALE  = Gedcom.getResources().getString("prop.sex.female"),
    TXT_UNKNOWN = Gedcom.getResources().getString("prop.sex.unknown");
    
  
  public static final int UNKNOWN = 0;
  public static final int MALE    = 1;
  public static final int FEMALE  = 2;

  
  private int sex = UNKNOWN;

  
  private String sexAsString;

  
   PropertySex(String tag) {
    super(tag);
    assertTag("SEX");
  }
  
   PropertySex() {
    super("SEX");
  }
  
  
  public static ImageIcon getImage(int sex) {
    switch (sex) {
      case MALE: return IMG_MALE;
      case FEMALE: return IMG_FEMALE;
      default:
        return IMG_UNKNOWN;
    }
  }

  
  public ImageIcon getImage(boolean checkValid) {
    
    if (checkValid&&(!isValid()))
      return super.getImage(true);
    
    switch (sex) {
      case MALE: return IMG_MALE;
      case FEMALE: return IMG_FEMALE;
      default:
        return super.getImage(checkValid);
    }
  }

  
  public boolean isValid() {
    return (sexAsString==null);
  }


  
  static public String getLabelForSex(int which) {
    switch (which) {
      case MALE:
        return Gedcom.getResources().getString("prop.sex.male");
      case FEMALE:
        return Gedcom.getResources().getString("prop.sex.female");
      default:
        return Gedcom.getResources().getString("prop.sex.unknown");
    }
  }

  
  public int getSex() {
    return sex;
  }

  
  public String getValue() {
    if (sexAsString != null)
      return sexAsString;
    if (sex == MALE)
      return "M";
    if (sex == FEMALE)
      return "F";
    return "";
  }
  
  
  public String getDisplayValue() {
    return getLabelForSex(sex);
  }

  
  public void setSex(int newSex) {
    String old = getValue();
    sexAsString = null;
    sex = newSex;
    propagatePropertyChanged(this, old);
    
  }

  
  public void setValue(String newValue) {

    String old = getValue();

    
    if (newValue.trim().length()>1) {
      sexAsString=newValue;
    } else {
	    
	    if (newValue.length()==0) {
	      sexAsString = null;
	      sex = UNKNOWN;
	    } else {
		    
		    switch (newValue.charAt(0)) {
		      case 'f' :
		      case 'F' :
		        sex = FEMALE;
		        sexAsString=null;
		        break;
		      case 'm' :
		      case 'M' : 
		        sex = MALE;
		        sexAsString=null;
		        break;
		      default:
		        sexAsString = newValue;
		        break;
		    }
	    }
    }
    
    propagatePropertyChanged(this, old);
    
  }

  
  public static boolean isSex(int tst) {
    return tst==MALE||tst==FEMALE;
  }

  
  public static int calcOppositeSex(int from, int fallback) {
    if (from==MALE)
      return FEMALE;
    if (from==FEMALE)
      return MALE;
    return fallback;
  }

  
  public static int calcOppositeSex(Indi from, int fallback) {

    
    if (from==null) {
      return fallback;
    }

    
    return calcOppositeSex(from.getSex(), fallback);

  }
}
