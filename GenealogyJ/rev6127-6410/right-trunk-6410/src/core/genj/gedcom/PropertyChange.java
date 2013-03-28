
package genj.gedcom;

import genj.gedcom.time.PointInTime;

import java.text.DecimalFormat;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;


public class PropertyChange extends Property implements MultiLineProperty {

  private final static DecimalFormat decimal = new DecimalFormat("00");

  public final static String
   CHAN = "CHAN",
   TIME = "TIME",
   DATE = "DATE";

  private long time = -1;
  
  public PropertyChange() {
    super(CHAN);
    setTime(System.currentTimeMillis());
  }

  
  public PropertyChange(String tag) {
    super(tag);
    assertTag(CHAN);
  }

  
  public boolean isReadOnly() {
    return true;
  }

  
  public String getDisplayValue() {
    return time<0 ? "" : getDateDisplayValue() +", "+getTimeDisplayValue();
  }

  
  public String getDateDisplayValue() {
    return time<0 ? "" : PointInTime.getPointInTime(toLocal(time)).toString();
  }

  
  public String getTimeDisplayValue() {
    return time<=0 ? "" : toString(toLocal(time));
  }

  private long toLocal(long utc) {
    java.util.Calendar c = java.util.Calendar.getInstance();
    return utc + c.get(java.util.Calendar.ZONE_OFFSET) + c.get(java.util.Calendar.DST_OFFSET);
  }

  private String toString(long time) {

    long
      sec = (time/1000)%60,
      min = (time/1000/60)%60,
      hr  = (time/1000/60/60)%24;

    StringBuffer buffer = new StringBuffer();
    buffer.append(decimal.format(hr));
    buffer.append(':');
    buffer.append(decimal.format(min));
    buffer.append(':');
    buffer.append(decimal.format(sec));

    return buffer.toString();
  }

  
  public Collector getLineCollector() {
    return new DateTimeCollector();
  }

  
  public Iterator getLineIterator() {
    return new DateTimeIterator();
  }

  
  public long getTime() {
    return time;
  }

  
  public void setTime(long set) {

    
    if (time==set)
      return;

    String old = getValue();

    
    time = set;

    
    propagatePropertyChanged(this, old);

    
  }

  
  public void setValue(String value) {

    String old = getValue();

    
    int i = value.indexOf(',');
    if (i<0)
      return;

    try {
      time = 0;

      
      StringTokenizer tokens = new StringTokenizer(value.substring(i+1), ":");
      if (tokens.hasMoreTokens())
        time += Integer.parseInt(tokens.nextToken()) * 60 * 60 * 1000;
      if (tokens.hasMoreTokens())
        time += Integer.parseInt(tokens.nextToken()) * 60 * 1000;
      if (tokens.hasMoreTokens())
        time += Integer.parseInt(tokens.nextToken()) * 1000;

      
      time += PointInTime.getPointInTime(value.substring(0,i)).getTimeMillis();

      
      getGedcom().updateLastChange(this);

    } catch (Throwable t) {

      time = -1;
    }

    
    propagatePropertyChanged(this, old);

    
  }

  
  public String getValue() {
    return time<0 ? "" : PointInTime.getPointInTime(time).getValue() +','+toString(time);
  }

  
  public int compareTo(Property other) {
    
    if (time<((PropertyChange)other).time)
      return -1;
    if (time>((PropertyChange)other).time)
      return 1;
    return 0;
  }

  
  public void setPrivate(boolean set, boolean recursively) {
    
  }

  
  private class DateTimeCollector implements MultiLineProperty.Collector {

    private String dateCollected, timeCollected;

    
    public boolean append(int indent, String tag, String value) {

      
      if (indent==1&&DATE.equals(tag)) {
        dateCollected = value;
        return true;
      }

      
      if (indent==2&&TIME.equals(tag)) {
        timeCollected = value;
        return true;
      }

      
      return false;
    }

    
    public String getValue() {
      return dateCollected+','+timeCollected;
    }

  } 

  
  private class DateTimeIterator implements MultiLineProperty.Iterator {

    
    int i = 0;

    
    private String[]
      tags = { CHAN, DATE, TIME  },
      values = { "", PointInTime.getPointInTime(time).getValue(), PropertyChange.this.toString(time) };

    
    public void setValue(String value) {
      
    }

    
    public int getIndent() {
      return i;
    }

    
    public String getTag() {
      return tags[i];
    }

    
    public String getValue() {
      return values[i];
    }
    
    public boolean next() {
      return time>=0 && ++i!=tags.length;
    }

  } 

  
   static class Monitor extends GedcomListenerAdapter {

    private Set<Entity> updated = new HashSet<Entity>();

    
    private void update(Property where) {

      Entity entity = where.getEntity();
      if (updated.contains(entity))
        return;

      
      while (where!=null) {
        if (where instanceof PropertyChange)
          return;
        where = where.getParent();
      }

      
      Gedcom.LOG.finer("updating CHAN for "+entity.getId());

      
      MetaProperty meta = entity.getMetaProperty();
      if (!meta.allows(PropertyChange.CHAN))
        return;

      
      PropertyChange prop = (PropertyChange)entity.getProperty(PropertyChange.CHAN);
      if (prop==null)
        prop = (PropertyChange)entity.addProperty(new PropertyChange());
      else
        prop.setTime(System.currentTimeMillis());

      
      updated.add(entity);
      entity.getGedcom().updateLastChange(prop);
    }

    public void gedcomEntityAdded(Gedcom gedcom, Entity entity) {
      update(entity);
    }

    public void gedcomEntityDeleted(Gedcom gedcom, Entity entity) {
      updated.remove(entity);
    }

    public void gedcomPropertyAdded(Gedcom gedcom, Property property, int pos, Property added) {
      update(added);
    }

    public void gedcomPropertyChanged(Gedcom gedcom, Property property) {
      update(property);
    }

    public void gedcomPropertyDeleted(Gedcom gedcom, Property property, int pos, Property deleted) {
      if (!(deleted instanceof PropertyChange))
        update(property);
    }

  } 

} 