
import genj.gedcom.Indi;
import genj.gedcom.Property;
import genj.gedcom.PropertySex;
import genj.gedcom.TagPath;
import genj.report.Report;
import genj.view.ViewContext;
import genj.view.ViewContext.ContextList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;


public class ReportRelatives extends Report {

  private final static int
    UNKNOWN = PropertySex.UNKNOWN,
    MALE = PropertySex.MALE,
    FEMALE = PropertySex.FEMALE;

  
  static class Relative {

    
    String key;
    String expression;
    int sex;

    
    Relative(String key, String expression) {
      this(key, expression, UNKNOWN);
    }

    
    Relative(String key, String expression, int sex) {
      this.key = key;
      this.expression = expression.trim();
      this.sex = sex;
    }

  } 

  private final static Relative[] RELATIVES = {
    new Relative("farfar"     , "father+father"),
    new Relative("farmor"     , "father+mother"),
    new Relative("morfar"     , "mother+father"),
    new Relative("mormor"     , "mother+mother"),
    new Relative("father"     , "INDI:FAMC:*:..:HUSB:*"   ),
    new Relative("mother"     , "INDI:FAMC:*:..:WIFE:*"   ),
    new Relative("husband"    , "INDI:FAMS:*:..:HUSB:*"   ),
    new Relative("wife"       , "INDI:FAMS:*:..:WIFE:*"   ),
    new Relative("daughter"   , "INDI:FAMS:*:..:CHIL:*"   , FEMALE),
    new Relative("son"        , "INDI:FAMS:*:..:CHIL:*"   , MALE),
    new Relative("brother"    , "INDI:FAMC:*:..:CHIL:*"   , MALE),
    new Relative("sister"     , "INDI:FAMC:*:..:CHIL:*"   , FEMALE),

    new Relative("grandson"     , "son+son|daughter+son"          , MALE),
    new Relative("granddaughter", "son+daughter|daughter+daughter", FEMALE),

    new Relative("uncle.paternal", "father+brother|father+sister +husband"),
    new Relative("uncle.maternal", "mother+brother|mother+sister +husband"),
    new Relative( "aunt.paternal", "father+sister |father+brother+wife"   ),
    new Relative( "aunt.maternal", "mother+sister |mother+brother+wife"   ),

    new Relative("nephew.fraternal", "brother+son"),
    new Relative("niece.fraternal" , "brother+daughter"),
    new Relative("nephew.sororal"  , "sister+son"),
    new Relative("niece.sororal"   , "sister+daughter"),

    new Relative("cousin.paternal" , "uncle.paternal+son"),
    new Relative("cousin.maternal" , "uncle.maternal+son"),
    new Relative("cousine.paternal", "uncle.paternal+daughter"),
    new Relative("cousine.maternal", "uncle.maternal+daughter")
  };

  
  public ContextList start(Indi indi) {

    
    Map key2relative = new HashMap();
    for (int i=0; i<RELATIVES.length;i++) {
      Relative relative = RELATIVES[i];
      key2relative.put(relative.key, relative);
    }

    
    ContextList result = new ContextList(indi.getGedcom(), translate("title", indi));
    result.add(new ViewContext(indi));
    
    for (int i=0; i<RELATIVES.length; i++) {
      Relative relative = RELATIVES[i];
      for (Indi found : find(indi, relative.expression, relative.sex, key2relative)) {
        result.add(new ViewContext(found).setText(translate(relative.key) + ": " + found));
      }
    }
    return result;

    
  }

  
  private List<Indi>  find(List roots, String expression, int sex, Map key2relative) {

    List<Indi>  result = new ArrayList<Indi>();
    for (int i=0;i<roots.size();i++) {
      result.addAll(find((Property)roots.get(i), expression, sex, key2relative));
    }

    return result;

  }

  
  private List<Indi> find(Property root, String expression, int sex, Map key2relative) {

    
    int or = expression.indexOf('|');
    if (or>0) {
      List<Indi> result = new ArrayList<Indi>();
      StringTokenizer ors = new StringTokenizer(expression, "|");
      while (ors.hasMoreTokens())
        result.addAll(find(root, ors.nextToken().trim(), sex, key2relative));
      return result;
    }

    
    int dot = expression.indexOf('+');
    if (dot>0) {
      List<Indi> roots = new ArrayList<Indi>();
      roots.add((Indi)root.getEntity());
      StringTokenizer cont = new StringTokenizer(expression, "+");
      while (cont.hasMoreTokens()) {
        roots = find(roots, cont.nextToken(), sex, key2relative);
      }
      return roots;
    }

    
    int colon = expression.indexOf(':');
    if (colon<0) {
      Relative relative = (Relative)key2relative.get(expression.trim());
      return find(root, relative.expression, relative.sex, key2relative);
    }

    
    List<Indi> result = new ArrayList<Indi>();
    Property[] found = root.getProperties(new TagPath(expression));
    for (int i = 0; i < found.length; i++) {
      Indi indi = (Indi)found[i].getEntity();
      if (indi!=root) {
        if (sex==UNKNOWN||indi.getSex()==sex)
          result.add(indi);
      }
    }

    
    return result;
  }

} 
