
package genj.search;

import genj.gedcom.Entity;
import genj.gedcom.Gedcom;
import genj.gedcom.Property;
import genj.gedcom.TagPath;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;


 class Worker {
  
  
  private final static int MAX_HITS = 255;
  
  
  private WorkerListener listener;
  
  
  private Gedcom gedcom;
  private TagPath tagPath;
  private Matcher matcher;
  private Set<Entity> entities = new HashSet<Entity>();
  private List<Hit> hits = new ArrayList<Hit>(MAX_HITS);
  private int hitCount = 0;
  
  
  private Thread thread;
  private AtomicBoolean lock = new AtomicBoolean(false);
  private long lastFlush;
  
   Worker(WorkerListener listener) {
    this.listener = listener;
  }
  
  
   void stop() {

    synchronized (lock) {
      try {
        lock.set(false);
        thread.interrupt();
      } catch (Throwable t) {
      }
    }
    
    
  }

  
   void start(Gedcom gedcom, TagPath path, String value, boolean regexp) {
    
    
    synchronized (lock) {
      
      
      if (thread!=null)
        throw new IllegalStateException("can't start while running");

      
      
      this.gedcom = gedcom;
      this.matcher = getMatcher(value, regexp);
      this.tagPath = null;
      this.hits.clear();
      this.entities.clear();
      this.hitCount = 0;
      
      lock.set(true);

      thread = new Thread(new Runnable() {
        public void run() {
          try {
            Worker.this.listener.started();
            search(Worker.this.gedcom);
            flush();
          } catch (Throwable t) {
            Logger.getLogger("genj.search").log(Level.FINE, "worker bailed", t);
          } finally {
            synchronized (lock) {
              thread = null;
              lock.set(false);
              lock.notifyAll();
            }
            Worker.this.listener.stopped();
          }
        }
      });
      thread.setDaemon(true);
      thread.start();
    }
    
    
  }
  
  
  private void search(Gedcom gedcom) {
    for (int t=0; t<Gedcom.ENTITIES.length && hitCount<MAX_HITS; t++) {
      for (Entity entity : gedcom.getEntities(Gedcom.ENTITIES[t])) {
        
        
        search(entity, entity, 0);

        
        if (!lock.get())
          return;
      }
    }
  }

  private void flush() {
    
    if (!hits.isEmpty()) {
      listener.more(Collections.unmodifiableList(hits));
      hits.clear();
    }
  }
  
  
  private void search(Entity entity, Property prop, int pathIndex) {
    
    boolean searchThis = true;
    if (tagPath!=null) {
      
      if (pathIndex<tagPath.length()&&!tagPath.get(pathIndex).equals(prop.getTag())) 
        return;
      
      searchThis = pathIndex>=tagPath.length()-1;
    }
    
    if (searchThis&&!prop.isTransient()) {
      
      if (entity==prop)
        search(entity, entity, entity.getId(), true);
      
      search(entity, prop, prop.getDisplayValue(), false);
    }
    
    int n = prop.getNoOfProperties();
    for (int i=0;i<n;i++) {
      search(entity, prop.getProperty(i), pathIndex+1);
    }
    
  }

  
  private void search(Entity entity, Property prop, String value, boolean isID) {
    
    Matcher.Match[] matches = matcher.match(value);
    if (matches.length==0)
      return;
    
    if (hitCount>=MAX_HITS)
      return;
    
    entities.add(entity);
    
    Hit hit = new Hit(prop, value, matches, entities.size(), isID);
    
    hits.add(hit);
    hitCount++;
    
    long now = System.currentTimeMillis();
    if (now-lastFlush>500) 
      flush();
    lastFlush = now;
    
  }

  
  private Matcher getMatcher(String pattern, boolean regex) {

    Matcher result = regex ? (Matcher)new RegExMatcher() : (Matcher)new SimpleMatcher();
    
    
    result.init(pattern);
    
    
    return result;
  }

}
