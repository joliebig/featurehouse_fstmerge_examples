
package genj.almanac;

import genj.gedcom.GedcomException;
import genj.gedcom.time.PointInTime;
import genj.util.WordBuffer;

import java.util.List;
import java.util.Set;


public class Event implements Comparable<Event> {
  
  private PointInTime pit;
  private String desc;
  private List<String> cats;
  private long julianDay;
  
  
  public Event(List<String> setCats, PointInTime setTime, String setText) throws GedcomException {
    pit = setTime;
    cats = setCats;
    desc = setText;
    
    julianDay = pit.getJulianDay();
  }
  
  
  protected long getJulian() {
    return julianDay;
  }
  
  
  protected boolean isCategory(Set<String> criteria) {
    for (int c=0; c<cats.size(); c++) {
      if (criteria.contains(cats.get(c)))
        return true;
    }
    return false;
  }
  
  
  public String toString() {
    WordBuffer result = new WordBuffer();
    result.append(pit.toString());
    result.append(desc);
    return result.toString();
  }
  
  
  public int compareTo(Event that) {
    return this.pit.compareTo(that.pit);
  }
  
  
  public PointInTime getTime() {
    return pit;
  }
  
  
  public List<String> getCategories() {
    return cats;
  }
  
  
  public String getDescription() {
    return desc;
  }
  
} 