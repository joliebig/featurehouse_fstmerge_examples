
package genj.gedcom;

import genj.gedcom.time.Calendar;
import genj.gedcom.time.Delta;
import genj.gedcom.time.PointInTime;
import genj.util.DirectAccessTokenizer;
import genj.util.WordBuffer;


public class PropertyDate extends Property {

   public final static String TAG = "DATE";

  
  private PIT 
    start = new PIT(),
    end = new PIT();
  private boolean isAdjusting = false;
  private String valueAsString = null;

  
  private Format format = DATE;

  
  private String phrase = "";

  
  public final static Format
    DATE        = new Format("", ""),
    FROM_TO     = new Format("FROM", "TO"),
    FROM        = new Format("FROM", ""),
    TO          = new Format("TO"  , ""),
    BETWEEN_AND = new Format("BET" , "AND"),
    BEFORE      = new Format("BEF" , ""),
    AFTER       = new Format("AFT" , ""),
    ABOUT       = new Format("ABT" , ""),
    CALCULATED  = new Format("CAL" , ""),
    ESTIMATED   = new Format("EST" , ""),
    INTERPRETED = new Interpreted();
  
  public final static Format[] FORMATS = {
    DATE, FROM_TO, FROM, TO, BETWEEN_AND, BEFORE, AFTER, ABOUT, CALCULATED, ESTIMATED, INTERPRETED
  };
  
  
  public PropertyDate() {
  }

  
  public PropertyDate(int year) {
    getStart().set(PointInTime.UNKNOWN, PointInTime.UNKNOWN, year);
  }

  
  public int compareTo(Object o) {
    if (!(o instanceof PropertyDate)) return super.compareTo(o);
    return start.compareTo(((PropertyDate)o).start);
  }
  
  
  public String getPhrase() {
    return phrase;
  }

  
  public PointInTime getStart() {
    return start;
  }

  
  public PointInTime getEnd() {
    return end;
  }

  
  public Format getFormat() {
    return format;
  }

  
  public String getTag() {
    return TAG;
  }

  
  public String getValue() {
    return valueAsString!=null ? valueAsString : format.getValue(this);
  }

  
  public boolean isRange() {
    return format.isRange();
  }

  
  public boolean isValid() {
    return valueAsString==null && format.isValid(this);
  }
  
  
  public boolean isComparable() {
    
    return start.isValid();
  }
  
  
  public void setValue(Format newFormat, PointInTime newStart, PointInTime newEnd, String newPhrase) {

    String old = getValue();
    
    
    isAdjusting = true;
    try {
      
      if (newStart==null)
        start.reset();
      else
        start.set(newStart);
      if (newEnd==null)
        end.reset();
      else
        end.set(newEnd);
      phrase = newPhrase;
      valueAsString = null;
      
      format = (newFormat.needsValidStart() && !start.isValid()) || (newFormat.needsValidEnd() && !end.isValid()) ? DATE : newFormat ;
    } finally {
      isAdjusting = false;
    }
    
    
    propagatePropertyChanged(this, old);

    
  }

  
  public void setFormat(Format set) {

    String old = getValue();
    
    
    isAdjusting = true;
    try {
      
      if (!isRange()&&set.isRange()) 
        end.set(start);
      
      format = set;
    } finally {
      isAdjusting = false;
    }
    
    
    propagatePropertyChanged(this, old);

    
  }

  
  public void setValue(String newValue) {

    
    String old = getParent()==null ? null : getValue();

    
    isAdjusting = true;
    try {
      
      
      start.reset();
      end.reset();
      format = DATE;
      phrase= "";
      valueAsString = newValue.trim();
  
      
      if (valueAsString.length()>0) for (int f=0; f<FORMATS.length;f++) {
        if (FORMATS[f].setValue(newValue, this)) {
          format  = FORMATS[f];
          valueAsString = null;
          break;
        }
      } 
      
    } finally {
      isAdjusting = false;
    }

    
    if (old!=null) propagatePropertyChanged(this, old);

    
  }

  
   Property init(MetaProperty meta, String value) throws GedcomException {
    meta.assertTag(TAG);
    return super.init(meta, value);
  }

  
  public String getDisplayValue() {
    return getDisplayValue(null);
  }
  
  
  public String getDisplayValue(Calendar calendar) {
    if (valueAsString!=null)
      return valueAsString;
    return format.getDisplayValue(this, calendar);
  }
  
  
  public String getPropertyInfo() {
    WordBuffer result = new WordBuffer();
    result.append(super.getPropertyInfo());
    result.append("<br>");
    result.append(getDisplayValue());
    if (!(getStart().isGregorian()&&getEnd().isGregorian())) {
      result.append("<br>");
      result.append(getDisplayValue(PointInTime.GREGORIAN));
      result.append("("+PointInTime.GREGORIAN.getName()+")");
    }
    return result.toString();
  }
  
  
  public Delta getAnniversary() {
    return getAnniversary(PointInTime.getNow());
  }
  
  
  public Delta getAnniversary(PointInTime now) {
    
    
    if (!isValid())
      return null;
    
    
    PointInTime pit = isRange() ? getEnd() : getStart();

    
    if (now.compareTo(pit)<0)
      return null;
    
    
    return Delta.get(pit, now);
  }

  
  private final class PIT extends PointInTime {
    
    
    public void set(int d, int m, int y) {
      
      
      if (isAdjusting) {
        super.set(d,m,y);
      } else {
        
        String old = super.getValue();
        
        super.set(d,m,y);
        
        propagatePropertyChanged(PropertyDate.this, old);
      }
      
      
    }
    
  } 
  
  
  public static class Format {
    
    protected String start, end;
    
    private Format(String s, String e) {
      start  = s; 
      end    = e;
    }
    
    public String toString() {
      return start+end;
    }
    
    public boolean usesPhrase() {
      return false;
    }
    
    public boolean isRange() {
      return end.length()>0;
    }
    
    protected boolean needsValidStart() {
      return true;
    }

    protected boolean needsValidEnd() {
      return isRange();
    }

    public String getName() {
      String key = (start+end).toLowerCase();
      if (key.length()==0)
        key = "date";
      return resources.getString("prop.date."+key);
    }
    
    public String getPrefix1Name() {
      return resources.getString("prop.date.mod."+start, false);
    }
    
    public String getPrefix2Name() {
      return resources.getString("prop.date.mod."+end, false);
    }
    
    protected boolean isValid(PropertyDate date) {
      
      return date.start.isValid() && (!isRange()||date.end.isValid());
    }
    
    protected String getValue(PropertyDate date) {

      
      WordBuffer result = new WordBuffer();
      result.append(start);  
      date.start.getValue(result);
      if (isRange())  {
        result.append(end);
        date.end.getValue(result);
      }

      
      return result.toString();
    }
    
    protected String getDisplayValue(PropertyDate date, Calendar calendar) {
      
      
      try {
        WordBuffer result = new WordBuffer();
        
        
        if (start.length()>0)
          result.append(Gedcom.getResources().getString("prop.date.mod."+start));
        if (calendar==null||date.start.getCalendar()==calendar) 
          date.start.toString(result);
        else 
          date.start.getPointInTime(calendar).toString(result);
    
        
        if (isRange()) {
          result.append(Gedcom.getResources().getString("prop.date.mod."+end));
          if (calendar==null||date.end.getCalendar()==calendar) 
            date.end.toString(result);
          else 
            date.end.getPointInTime(calendar).toString(result);
        }
    
        
        return result.toString();
        
      } catch (GedcomException e) {
        
        return "";
      }      
    }
    
    protected boolean setValue(String text, PropertyDate date) {
      
      DirectAccessTokenizer tokens = new DirectAccessTokenizer(text, " ", true);
      int afterFirst = 0;
      
      
      if (start.length()>0) {
        String first = tokens.get(0);
        afterFirst = tokens.getEnd();
        if (!first.equalsIgnoreCase(start))
          return false;
      }

      
      if ( !isRange()) 
        return date.start.set(text.substring(afterFirst));

      
      for (int pos=1; ;pos++) {
        String token = tokens.get(pos);
        if (token==null) break;
        if ( token.equalsIgnoreCase(end) ) 
          return date.start.set(text.substring(afterFirst, tokens.getStart())) && date.end.set(text.substring(tokens.getEnd()));
      }

      
      return false;
    }
    
  } 
  
  
  private static class Interpreted extends Format {
    
    private Interpreted() {
      super("INT" , "");
    }
    
    public boolean usesPhrase() {
      return true;
    }
    
    public boolean isRange() {
      return false;
    }
    
    protected boolean needsValidStart() {
      return false;
    }

    protected boolean needsValidEnd() {
      return false;
    }

    protected boolean isValid(PropertyDate date) {
      
      return true;
    }

    protected boolean setValue(String text, PropertyDate date) {
      
      
      if (text.length()>start.length() && text.substring(0,start.length()).equalsIgnoreCase(start)) {
        
        
        int bracket = text.indexOf('(');
        if (bracket>0 && date.start.set(text.substring(start.length(), bracket))) {
          date.phrase = text.substring(bracket+1, text.endsWith(")") ? text.length()-1 : text.length());
          return true;
        }
        
        if (date.start.set(text.substring(start.length()))) {
          date.phrase = "";
          return true;
        }
      }
      
      
      if (!text.startsWith("(")||!text.endsWith(")"))
        return false;
      
      date.phrase = text.substring(1, text.length()-1).trim();
      
      
      return true;
    }
    
    protected String getDisplayValue(PropertyDate date, Calendar calendar) {
      
      try {
        WordBuffer result = new WordBuffer();
        
        
        if (date.start.isValid()) {
          if (calendar==null||date.start.getCalendar()==calendar) 
            date.start.toString(result);
          else 
            date.start.getPointInTime(calendar).toString(result);
        }
        
        
        result.append("("+date.phrase+")");
    
        
        return result.toString();
        
      } catch (GedcomException e) {
        
        return "";
      }      
    }
    
    protected String getValue(PropertyDate date) {
      
      
      WordBuffer result = new WordBuffer();
      
      if (date.start.isValid()) {
        result.append(start);  
        date.start.getValue(result);
      }
      
      result.append("("+date.phrase+")");
      
      
      return result.toString();
    }
    
  }
  
} 
