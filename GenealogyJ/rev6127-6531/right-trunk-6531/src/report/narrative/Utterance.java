

package narrative;

import genj.gedcom.Indi;
import genj.gedcom.PropertySex;
import genj.gedcom.Entity;
import genj.util.Resources;
import genj.fo.Document;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.Locale;
import java.util.regex.Pattern;
import java.util.regex.Matcher;


public class Utterance {
  private final String template;
  private final Map props = new HashMap(); 
  private final ArrayList linkedEntities = new ArrayList();
  private Resources resources;
  private int gender = 0; 

  private static final String SUBJECT = "SUBJECT";

  private Utterance(Resources resources, String template) {
    this.resources = resources;
    this.template = template;
    if (template == null) {
      System.err.println("No resource found for " + template);
    }
    props.put("LBRACKET", "[");
    props.put("RBRACKET", "]");
    props.put("LBRACE", "{");
    props.put("RBRACE", "}");
  }

  public static Utterance forProperty(Resources resources, String property) {
    return forTemplate(resources, translateWithFallback(property, resources));
  }

  public static Utterance forProperty(Resources resources, String property, String[] params, Entity[] linkedEntities) {
    return forTemplate(resources, translateWithFallback(property, resources), params, linkedEntities);
  }

  public static Utterance forProperty(Resources resources, String property, String[] params) {
    return forTemplate(resources, translateWithFallback(property, resources), params);
  }

  public static Utterance forTemplate(Resources resources, String template) {
    return new Utterance(resources, template);
  }

  public static Utterance forTemplate(Resources resources, String template, String[] params, Entity[] linkedEntities) {
    Utterance result = forTemplate(resources, template, params);
    for (int i = 0; i < linkedEntities.length; i++) {
      Entity entity = linkedEntities[i];
      result.linkedEntities.add(entity);
    }
    return result;
  }

  public static Utterance forTemplate(Resources resources, String template, String[] params) {
    Utterance result = new Utterance(resources, template);
    for (int i=0; i < params.length; i++) {
      result.set(Integer.toString(i+1), params[i]);
    }
    return result;
  }

  
  private final static String lang = Locale.getDefault().getLanguage(); 

  
  private String translate(String key) {
    return translate(key, resources);
  }

  public static boolean isTranslatable(String key, Resources resources) {
      return translate(key, resources) != null;
  }

  public static String translateWithFallback(String key, Resources resources) {
    String translation = translate(key, resources);
    if (translation == null) {
      System.err.println("No translation found for " + key);
      translation = key;
    }
    return translation;
  }

  
  public static String translate(String key, Resources resources) {
    
    if (resources==null)
      return key;


    
    String result = null;
    if (lang!=null  && !lang.equals("en"))
      result = resources.getString(key+'.'+lang);
    if (result != null) return result;

    
    
    result = resources.getString(key);
    if (result.equals(key))
      return null; 

    
    if (result.startsWith("_")) {
      result = " " + result.substring(1);
    }
    if (result.endsWith("_")) {
      result = result.substring(0, result.length()-1) + " ";
    }


    return result;
  }

  private String getGenderKeySuffix() {
    if (gender == PropertySex.MALE) {
      return ".male";
    } else if (gender == PropertySex.FEMALE) {
      return ".female";
    } else {
      return ".genderUnknown";
    }
  }

  public void setSubject(Indi indi) {
    gender = indi.getSex();
    props.put(SUBJECT, translate("pronoun.nom" + getGenderKeySuffix()));
    props.put(SUBJECT + ".dat", translate("pronoun.dat" + getGenderKeySuffix()));
  }

  private static final Pattern argPattern = Pattern.compile("\\[[^\\[\\]]*\\]");

  public void addText(Document doc) {
    
    doc.addText(toString());
  }

  public String toString() {
    Matcher matcher = argPattern.matcher(template);
    String result = template;
    int start = 0;
    while (matcher.find(start)) {
      int where = matcher.start();
      String key = matcher.group();
      key = key.substring(1, key.length()-1);
      String value = (String) props.get(key);
      if (value == null) {
        if (key.startsWith("OPTIONAL_")) {
          value = "";
        } else {
          if (key.startsWith("ending.")) {
            value = translate(key + getGenderKeySuffix());
          } else if (key.startsWith("SUBJECT.")) {
            value = translate("pronoun." + key.substring(8) + getGenderKeySuffix());
          }
          if (value == null) {
            System.err.println("No value for key " + key + " in sentence template " + template);
            value = key;
          }
        }
      }
      if (where == 0) {
        if (value.length() == 0) {
          System.err.println("Empty string for key " + key);
        } else {
          value = Character.toUpperCase(value.charAt(0)) + value.substring(1);
        }
      }
      
      if (where > 0 && value.length() > 0 && !key.startsWith("ending.") &&
          (!Character.isSpaceChar(result.charAt(where-1)) &&
          Character.isLetterOrDigit(value.charAt(0))
          ||
          !Character.isSpaceChar(result.charAt(where-1)) &&
          punctuationRequiresPrecedingBlank(value.charAt(0))
          )
      ) {
          value = " " + value;
      }
      
      
      String before = result.substring(0, where);
      String after = result.substring(where + matcher.group().length());
      start = where + value.length();
      result = before + value + after; 
      
      
      if (start > result.length()) {
        System.err.println("OutOfBoundsException about to happen");
        result = result.substring(0, result.length() - key.length() -2);
        break;
      }
      matcher.reset(result);
    }
    return result;
  }

  
  private boolean punctuationRequiresPrecedingBlank(char c) {
    return c == '(' || c == '{' || c == '[';
  }

  public static void main(String[] args) {
    
    Utterance s = forTemplate(null, "[SUBJECT] wurde geboren[OPTIONAL_PP_PLACE][OPTIONAL_PP_DATE].");
    s.set("SUBJECT", "er");
    System.out.println(s);
    s.set("OPTIONAL_PP_PLACE", "in Frankfurt");
    System.out.println(s);
    s = forTemplate(null, "Geboren wurde [SUBJECT][OPTIONAL_PP_PLACE][OPTIONAL_PP_DATE].");
    s.set("SUBJECT", "sie");
    System.out.println(s);
    s.set("OPTIONAL_PP_PLACE", "in Duesseldorf");
    System.out.println(s);

    Utterance pp = forTemplate(null, "in [CITY]");
    pp.set("CITY", "Frankfurt");
    s.set("OPTIONAL_PP_PLACE", pp.toString());
    System.out.println(s);
  }

  public void set(String key, String value) {
    props.put(key, value);
  }

  public String get(String key) {
    return (String) props.get(key);
  }

  public boolean hasKey(String key) {
    return props.containsKey(key);
  }

}
