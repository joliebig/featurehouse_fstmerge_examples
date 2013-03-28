
package genj.report;

import genj.gedcom.Gedcom;
import genj.gedcom.PrivacyPolicy;
import genj.option.Option;
import genj.option.OptionProvider;
import genj.option.PropertyOption;

import java.util.List;


public class Options extends OptionProvider {
    
    
    private static Options instance = new Options();
    
    
    private int positions = 2;
    
    
    private int indentPerLevel = 5;
    
    
    private String birthSymbol = "*";
    
    
    private String baptismSymbol =  "~";
    
    
    private String engagingSymbol = "o";
    
    
    private String marriageSymbol = "oo";
    
    
    private String divorceSymbol = "o|o";
    
    
    private String deathSymbol = "+";
    
    
    private String burialSymbol = "[]";
    
    
    private String  occuSymbol = "=";

    
    private String  resiSymbol = "^";
    
    
    private String childOfSymbol = "/";
    
    
    public  String privateTag = "_PRIV";
    
    
    public boolean deceasedIsPublic = true;
    
    
    public int yearsEventsArePrivate = 0; 
    
    private String trim(String symbol, String fallback) {
      if (symbol==null)
        return fallback;
      symbol = symbol.trim();
      int len = symbol.length();
      if (symbol.length()==0)
        return fallback;
      if (!Character.isLetter(symbol.charAt(len-1)))
        return symbol;
      return symbol + ' ';
    }
    
    public int getIndentPerLevel() {
        return indentPerLevel;
    }
    
    public void setIndentPerLevel(int set) {
        indentPerLevel = Math.max(2,set);
    }
    
    public int getPositions() {
        return positions;
    }
    
    public void setPositions(int set) {
        positions = Math.max(0,set);
    }
    
    public String getBirthSymbol() {
        return birthSymbol;
    }
    
    public void setBirthSymbol(String set) {
      birthSymbol = trim(set, "*");
    }
    
    public String getBaptismSymbol() {
        return baptismSymbol;
    }
    
    public void setBaptismSymbol(String set) {
        baptismSymbol = trim(set, "~");
    }
    
    public String getEngagingSymbol() {
        return engagingSymbol;
    }
    
    public void setEngagingSymbol(String set) {
        engagingSymbol = trim(set, "o");
    }
    
    public String getMarriageSymbol() {
        return marriageSymbol;
    }
    
    public void setMarriageSymbol(String set) {
        marriageSymbol  = trim(set, "oo");
    }
    
    public String getDivorceSymbol() {
        return divorceSymbol;
    }
    
    public void setDivorceSymbol(String set) {
        divorceSymbol = trim(set, "o|o");
    }
    
    public String getDeathSymbol() {
        return deathSymbol;
    }
    
    public void setDeathSymbol(String set) {
        deathSymbol = trim(set, "+");
    }
    
    public String getBurialSymbol() {
        return burialSymbol;
    }
    
    public void setBurialSymbol(String set) {
        burialSymbol = trim(set, "[]");
    }
    
    public String getOccuSymbol() {
		return occuSymbol;
	}

	public void setOccuSymbol(String set) {
	  occuSymbol  = trim(set, "=");
	}

	public String getResiSymbol() {
		return resiSymbol;
	}

	public void setResiSymbol(String set) {
	   	resiSymbol  = trim(set, "^");
	}

	public String getChildOfSymbol() {
        return childOfSymbol;
    }
    
    public void setChildOfSymbol(String set) {
       childOfSymbol  = trim(set, "/");
    }
    
    public String getSymbol(String tag) {
      if ("BIRT".equals(tag))
        return getBirthSymbol();
      if ("BAPM".equals(tag))
        return getBaptismSymbol();
      if ("ENGA".equals(tag))
        return getEngagingSymbol();
      if ("MARR".equals(tag))
        return getMarriageSymbol();
      if ("DIV".equals(tag))
        return getDivorceSymbol();
      if ("DEAT".equals(tag))
        return getDeathSymbol();
      if ("BURI".equals(tag))
        return getBurialSymbol();
      if ("OCCU".equals(tag))
        return getOccuSymbol();
      if ("RESI".equals(tag))
        return getResiSymbol();
      if ("FAMC".equals(tag))
        return getChildOfSymbol();
      
      return Gedcom.getName(tag);
    }
    
    
    public PrivacyPolicy getPrivacyPolicy() {
      return new PrivacyPolicy(deceasedIsPublic, yearsEventsArePrivate, privateTag);    
    }
    
    
    public List<? extends Option> getOptions() {
      
      new Thread(new Runnable() {
        public void run() {
          ReportLoader.getInstance();
        }
      }).start();
      
      
      return PropertyOption.introspect(getInstance());
    }
    
    
    public static Options getInstance() {
        return instance;
    }
    
} 
