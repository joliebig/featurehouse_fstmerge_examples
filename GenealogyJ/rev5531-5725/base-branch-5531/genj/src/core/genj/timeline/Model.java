
package genj.timeline;

import genj.gedcom.Context;
import genj.gedcom.Entity;
import genj.gedcom.Gedcom;
import genj.gedcom.GedcomException;
import genj.gedcom.GedcomListener;
import genj.gedcom.Property;
import genj.gedcom.PropertyDate;
import genj.gedcom.PropertyEvent;
import genj.gedcom.PropertyName;
import genj.gedcom.TagPath;
import genj.gedcom.time.Calendar;
import genj.gedcom.time.PointInTime;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import spin.Spin;


 class Model implements GedcomListener {

  
   Gedcom gedcom;
  
  
   double 
    max = Double.NaN,
    min = Double.NaN;

  
  private Set paths = new HashSet(), tags = new HashSet();
  
  
  private final static String[] DEFAULT_PATHS = new String[]{ 
    "INDI:BIRT", "FAM:MARR", "INDI:RESI", "INDI:EMIG" 
  };
    
  
  List layers;
  
  
   double 
    timeBeforeEvent = 0.5D,
    timeAfterEvent  = 2.0D;
  
  
  private List listeners = new ArrayList(1);
  
  
   Model(Gedcom ged, String[] paths) {
    
    
    if (paths==null) 
      paths = DEFAULT_PATHS;
    setPathsInternally(Arrays.asList(paths));
    
    
    gedcom = ged;
    createEvents();
    
    
  }
  
  
   void addListener(Listener listener) {
    listeners.add(listener);
    
    
    if (listeners.size()==1)
      gedcom.addGedcomListener((GedcomListener)Spin.over(this));
  }
  
  
   void removeListener(Listener listener) {
    listeners.remove(listener);
    
    
    if (listeners.isEmpty())
      gedcom.removeGedcomListener((GedcomListener)Spin.over(this));
  }
  
  
   void setTimePerEvent(double before, double after) {
    
    if (timeBeforeEvent==before&&timeAfterEvent==after) return;
    
    timeBeforeEvent = before;
    timeAfterEvent = after;
    
    if (layers!=null) layoutEvents();
    
  }
  
  
   static double toDouble(PointInTime pit, boolean roundUp) throws GedcomException {
    
    
    Calendar calendar = PointInTime.GREGORIAN;
    
    if (pit.getCalendar()!=calendar) { 
      pit = pit.getPointInTime(calendar);
    }
    
    
    int year = pit.getYear();
    double result = year; 

    
    int month = pit.getMonth();
    if (month==PointInTime.UNKNOWN)
      return roundUp ? result+1 : result;

    double months = calendar.getMonths(); 
    result += month / months;

    
    int day = pit.getDay();
    if (day==PointInTime.UNKNOWN) 
      return roundUp ? result+1/months : result;

    double days = calendar.getDays(month, year);
    result += day/months/days;

    
    return result;
  }
  
  
   static PointInTime toPointInTime(double year) {

    Calendar calendar = PointInTime.GREGORIAN;
    
    int months = calendar.getMonths();
    
    int y = (int)Math.floor(year);
    
    year = year%1;
    if (year<0) year = 1+year;
    
    int m = (int)Math.floor(year * months);

    int days = calendar.getDays(m, y);
    
    int d = (int)Math.floor((year*months)%1 * days);
    
    return new PointInTime(d, m, y);
  }
  
  
  protected Event getEvent(double year, int layer) {
    
    Iterator events = ((List)layers.get(layer)).iterator();
    while (events.hasNext()) {
      Event event = (Event)events.next();
      if (event.from-timeBeforeEvent<year&&year<event.to+timeAfterEvent)
        return event;
    }
    
    return null;
  }
  
  
  protected Set getEvents(Context context) {
    
    Set propertyHits = new HashSet();
    Set entityHits = new HashSet();
    
    Property[] props = context.getProperties();
    Entity[] ents = context.getEntities();
    
    for (int l=0; l<layers.size(); l++) {
      Iterator events = ((List)layers.get(l)).iterator();
      while (events.hasNext()) {
        Event event = (Event)events.next();
        for (int j = 0; j < ents.length; j++) {
          if (ents[j]==event.getEntity())
            entityHits.add(event);
        }
        for (int i = 0; i < props.length; i++) {
          if (event.getProperty()==props[i]||event.getProperty().contains(props[i]))
            propertyHits.add(event);
        }
      }
    }

    return propertyHits.isEmpty() ? entityHits : propertyHits;
  } 
  
  
  public Set getPaths() {
    return Collections.unmodifiableSet(paths);
  }
  
  
  public void setPaths(Collection set) {
    
    



      
    
    
    
    setPathsInternally(set);
    
    
    createEvents();
    
    
    
  }
  
  private void setPathsInternally(Collection set) {
    
    
    paths.clear();
    tags.clear();
    
    
    for (Iterator it = set.iterator();it.hasNext();) {
      Object next = it.next();
      try {
        if (!(next instanceof TagPath)) 
          next = new TagPath(next.toString());
      } catch (IllegalArgumentException e) {
        continue; 
      }
      paths.add(next);
      tags.add(((TagPath)next).getLast());
    }
    
    
  }
  
  
  private void fireStructureChanged() {
    for (int l=listeners.size()-1; l>=0; l--) {
      ((Listener)listeners.get(l)).structureChanged();
    }
  }

  
  private void fireDataChanged() {
    for (int l=listeners.size()-1; l>=0; l--) {
      ((Listener)listeners.get(l)).dataChanged();
    }
  }
  
  
  private final void contentEvents(Entity entity) {
    
    for (int l=0; l<layers.size(); l++) {
      List layer = (List)layers.get(l);
      Iterator events = layer.iterator();
      while (events.hasNext()) {
        Event event = (Event)events.next();
        if (event.pe.getEntity()==entity) event.content();
      }
    }
    
  }

  
  private final void layoutEvents() {
    
    min = Double.MAX_VALUE;
    max = -Double.MAX_VALUE;
    
    List old = layers;
    layers = new ArrayList(10);
    
    for (int l=0; l<old.size(); l++) {
      List layer = (List)old.get(l);
      Iterator events = layer.iterator();
      while (events.hasNext()) {
        Event event = (Event)events.next();
        insertEvent(event);
      }
    }
    
    max += timeAfterEvent;
    min -= timeBeforeEvent;
    
    fireStructureChanged();
    
  }
  
  
  private final void createEvents() {
    
    min = Double.MAX_VALUE;
    max = -Double.MAX_VALUE;
    
    layers = new ArrayList(10);
    
    if (gedcom!=null) {
      createEventsFrom(gedcom.getEntities(Gedcom.INDI).iterator());
      createEventsFrom(gedcom.getEntities(Gedcom.FAM ).iterator());
    }
    
    max += timeAfterEvent;
    min -= timeBeforeEvent;
    
    fireStructureChanged();
    
  }
  
  
  private final void createEventsFrom(Iterator es) {
    
    while (es.hasNext()) {
      Entity e = (Entity)es.next();
      List ps = e.getProperties(PropertyEvent.class);
      for (int j=0; j<ps.size(); j++) {
        PropertyEvent pe = (PropertyEvent)ps.get(j);
        if (tags.contains(pe.getTag())) createEventFrom(pe);
      }
    }
    
  }
  
  
  private final void createEventFrom(PropertyEvent pe) {
    
    PropertyDate pd = pe.getDate();
    if (pd==null||!pd.isValid()||!pd.isComparable())
      return;
    
    
    try { 
      insertEvent(new Event(pe, pd));
    } catch (GedcomException e) {
    }
    
  }
  
  
  private final void insertEvent(Event e) {
    
    
    min = Math.min(Math.floor(e.from), min);
    max = Math.max(Math.ceil (e.to  ), max);
    
    
    for (int l=0;l<layers.size();l++) {
      
      List layer = (List)layers.get(l);
      if (insertEvent(e, layer)) return;
      
    }
    
    
    List layer = new LinkedList();
    layers.add(layer);
    layer.add(e);
    
    
  }
  
  
  private final boolean insertEvent(Event candidate, List layer) {
    
    ListIterator events = layer.listIterator();
    do {
      Event event = (Event)events.next();
      
      if (candidate.to+timeAfterEvent<event.from-timeBeforeEvent) {
        events.previous();
        events.add(candidate);
        return true;
      }
      
      if (candidate.from-timeBeforeEvent<event.to+timeAfterEvent) 
        return false;
      
    } while (events.hasNext());
    
    events.add(candidate);
    return true;
  }
  
  
   class Event {
    
     double from, to;
     PropertyEvent pe;
     PropertyDate pd;
     String content;
    
    Event(PropertyEvent propEvent, PropertyDate propDate) throws GedcomException {
      
      pe = propEvent;
      pd = propDate;
      
      from = toDouble(propDate.getStart(), propDate.getFormat()==PropertyDate.AFTER);
      to  = propDate.isRange() ? toDouble(propDate.getEnd(), false) : from;
      
      if (from>to)
        throw new GedcomException("");
      
      content();
      
    }
    
    
    private final void content() {
      Entity e = pe.getEntity();
      content = e.toString();
    }
    
    public String toString() {
      return content;
    }
    
     Entity getEntity() {
      return pe.getEntity();
    }
    
     PropertyEvent getProperty() {
      return pe;
    }
  } 
  
  
   interface Listener {
    
    public void dataChanged();
    
    public void structureChanged();
  } 

  public void gedcomEntityAdded(Gedcom gedcom, Entity entity) {
    createEvents();
  }

  public void gedcomEntityDeleted(Gedcom gedcom, Entity entity) {
    createEvents();
  }

  public void gedcomPropertyAdded(Gedcom gedcom, Property property, int pos, Property added) {
    gedcomPropertyDeleted(gedcom, added, -1, added);
  }

  public void gedcomPropertyChanged(Gedcom gedcom, Property property) {
    gedcomPropertyDeleted(gedcom, property, -1, property);
  }

  public void gedcomPropertyDeleted(Gedcom gedcom, Property property, int pos, Property deleted) {
    if (deleted instanceof PropertyDate) {
      createEvents();
    } else if (deleted instanceof PropertyName) {
      contentEvents(property.getEntity());
      fireDataChanged();
    }
  }
  
} 
