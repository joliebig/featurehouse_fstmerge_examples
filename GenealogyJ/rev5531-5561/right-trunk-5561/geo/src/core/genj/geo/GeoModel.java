
package genj.geo;

import genj.gedcom.Entity;
import genj.gedcom.Gedcom;
import genj.gedcom.GedcomListener;
import genj.gedcom.Property;
import genj.util.swing.Action2;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import spin.Spin;

import com.vividsolutions.jts.geom.Coordinate;


 class GeoModel implements GedcomListener {
  
  public final static int 
    ALL_MATCHED = 0,
    SOME_MATCHED = 1,
    ERROR = 2;
  
  
  private List listeners = new ArrayList();
  private Gedcom gedcom;
  private Map locations = new HashMap();
  private LinkedList resolvers = new LinkedList();
  
  
  public GeoModel() {
  }
  
  
  public Gedcom getGedcom() {
    return gedcom;
  }
  
  
  public void setGedcom(Gedcom set) {
    
    
    if (gedcom!=null) {
      
      Collection removed = new ArrayList(locations.keySet());
      for (Iterator it = removed.iterator(); it.hasNext();)  {
        GeoLocation loc = (GeoLocation) it.next();
        locations.remove(loc);
        fireLocationRemoved(loc);
      }
      
      gedcom.removeGedcomListener((GedcomListener)Spin.over(this));
    }
    
    
    gedcom = set;

    
    if (gedcom!=null) {
      
      for (Iterator it = GeoLocation.parseEntities(gedcom.getEntities()).iterator(); it.hasNext();) {
        GeoLocation loc = (GeoLocation) it.next();
        locations.put(loc, loc);
        fireLocationAdded(loc);
      }
      
      resolve(locations.keySet(), false);
      
      gedcom.addGedcomListener((GedcomListener)Spin.over(this));
    }
    
    
  }
  
  
  public void setCoordinates(GeoLocation loc, Coordinate coord) {
    loc = (GeoLocation)locations.get(loc);
    if (loc!=null) {
      loc.setCoordinate(coord);
      GeoService.getInstance().remember(gedcom, loc);
      fireLocationUpdated(loc);
    }
  }
  
  
  public synchronized int getNumLocations() {
    return locations.size();
  }
  
  
  public synchronized Collection getLocations() {
    return locations.keySet();
  }
  
  
  private void fireLocationAdded(GeoLocation location) {
    GeoModelListener[] ls = (GeoModelListener[])listeners.toArray(new GeoModelListener[listeners.size()]);
    for (int i = 0; i < ls.length; i++) {
      ls[i].locationAdded(location);
    }
  }
  
  
  private void fireLocationUpdated(GeoLocation location) {
    GeoModelListener[] ls = (GeoModelListener[])listeners.toArray(new GeoModelListener[listeners.size()]);
    for (int i = 0; i < ls.length; i++) {
      ls[i].locationUpdated(location);
    }
  }

  
  private void fireLocationRemoved(GeoLocation location) {
    GeoModelListener[] ls = (GeoModelListener[])listeners.toArray(new GeoModelListener[listeners.size()]);
    for (int i = 0; i < ls.length; i++) {
      ls[i].locationRemoved(location);
    }
  }
  
  
  private void fireAsyncResolveStart() {
    GeoModelListener[] ls = (GeoModelListener[])listeners.toArray(new GeoModelListener[listeners.size()]);
    for (int i = 0; i < ls.length; i++) {
      ls[i].asyncResolveStart();
    }
  }
  
  
  private void fireAsyncResolveEnd(int status, String msg) {
    GeoModelListener[] ls = (GeoModelListener[])listeners.toArray(new GeoModelListener[listeners.size()]);
    for (int i = 0; i < ls.length; i++) {
      ls[i].asyncResolveEnd(status, msg);
    }
  }
  
  
  public void resolveAll() {
    resolve(locations.keySet(), true);
  }
  
  
  private void resolve(Collection todo, boolean matchAll) {
    synchronized (resolvers) {
      Resolver resolver = new Resolver(todo, matchAll);
      if (resolvers.isEmpty())
        resolver.trigger();
      else
        resolvers.add(resolver);
    }
  }

  
  private class Resolver extends Action2 {
    
    private ArrayList todo;
    private boolean matchAll;
    private Throwable err = null;

    
    private Resolver(Collection todo, boolean matchAll) {
      setAsync(Action2.ASYNC_SAME_INSTANCE);
      getThread().setDaemon(true);
      
      this.todo = new ArrayList(todo);
      this.matchAll = matchAll;
    }
    
    
    protected boolean preExecute() {
      fireAsyncResolveStart();
      return true;
    }

    
    protected void execute() {
      try { 
        GeoService.getInstance().match(gedcom, todo, matchAll);
      } catch (Throwable t) {
        err = t;
      }
    }

    
    protected void postExecute(boolean preExecuteResult) {
      
      int misses = 0;
      for (Iterator it=todo.iterator(); it.hasNext(); ) {
        GeoLocation loc = (GeoLocation)it.next();
        if (!loc.isValid()) misses++;
        GeoLocation old = (GeoLocation)locations.get(loc);
        if (old!=null) fireLocationUpdated(old);
      }
      
      if (err!=null) 
        fireAsyncResolveEnd( ERROR, GeoView.RESOURCES.getString("resolve.error", err.getMessage() ));
      else
        fireAsyncResolveEnd( misses>0 ? SOME_MATCHED : ALL_MATCHED, GeoView.RESOURCES.getString("resolve.matches", new Integer[]{ new Integer(todo.size()-misses), new Integer(todo.size())}));
      
      synchronized (resolvers) {
        if (!resolvers.isEmpty())
          ((Resolver)resolvers.removeFirst()).trigger();
      }
    }
    
  } 
  
  
  public void addGeoModelListener(GeoModelListener l) {
    
    listeners.add(l);
    
  }
  
  
  public void removeGeoModelListener(GeoModelListener l) {
    
    listeners.remove(l);
  }

  public void gedcomEntityAdded(Gedcom gedcom, Entity entity) {
    
    
    Set added = GeoLocation.parseEntities(Collections.singletonList(entity));
    
    for (Iterator locs = added.iterator(); locs.hasNext(); ) {
      GeoLocation loc = (GeoLocation)locs.next();
      GeoLocation old = (GeoLocation)locations.get(loc);
      if (old!=null) {
        old.add(loc);
        fireLocationUpdated(old);
      } else {
        locations.put(loc, loc);
        fireLocationAdded(loc);
      }
    }
    
    
    resolve(added, true);
  }

  public void gedcomEntityDeleted(Gedcom gedcom, Entity entity) {
    
    List current = new ArrayList(locations.keySet());
    for (Iterator locs = current.iterator(); locs.hasNext(); ) {
      GeoLocation loc = (GeoLocation)locs.next();
      loc.removeEntity(entity);
      if (loc.getNumProperties()==0) {
        locations.remove(loc);
        fireLocationRemoved(loc);
      } else {
        fireLocationUpdated(loc);
      }
    }
    
  }

  public void gedcomPropertyAdded(Gedcom gedcom, Property property, int pos, Property added) {
    gedcomPropertyChanged(gedcom, property);
  }

  public void gedcomPropertyChanged(Gedcom gedcom, Property property) {
    Entity entity = property.getEntity();
    gedcomEntityDeleted(gedcom, entity);
    gedcomEntityAdded(gedcom, entity);
  }

  public void gedcomPropertyDeleted(Gedcom gedcom, Property property, int pos, Property deleted) {
    gedcomPropertyChanged(gedcom, property);
  }
  
}
