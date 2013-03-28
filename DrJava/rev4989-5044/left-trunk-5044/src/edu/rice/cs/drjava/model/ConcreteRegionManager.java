
package edu.rice.cs.drjava.model;

import java.awt.EventQueue;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList; 
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import edu.rice.cs.plt.lambda.Lambda;
import edu.rice.cs.plt.tuple.Pair;
import edu.rice.cs.util.swing.Utilities;


public class ConcreteRegionManager<R extends OrderedDocumentRegion> extends EventNotifier<RegionManagerListener<R>> implements 
  RegionManager<R> {
  
  
  private volatile HashMap<OpenDefinitionsDocument, SortedSet<R>> _regions = 
    new HashMap<OpenDefinitionsDocument, SortedSet<R>>();
  
  
  private volatile Set<OpenDefinitionsDocument> _documents = new HashSet<OpenDefinitionsDocument>();
  
  
  
  
  public Set<OpenDefinitionsDocument> getDocuments() { return _documents; }
  
  private static final SortedSet<Object> EMPTY_SET = new TreeSet<Object>();
  
  
  @SuppressWarnings("unchecked")
  private <T> T emptySet() { return (T) EMPTY_SET; }
  
  
  @SuppressWarnings("unchecked")
  private <T> T newDocumentRegion(OpenDefinitionsDocument odd, int start, int end) { 
    return (T) new DocumentRegion(odd, start, end);
  }
  
  
  public SortedSet<R> getHeadSet(R r) {
    SortedSet<R> oddRegions = _regions.get(r.getDocument());
    if (oddRegions == null || oddRegions.isEmpty()) return emptySet();
    return oddRegions.headSet(r);
  }
  
  
  public SortedSet<R> getTailSet(R r) {
    SortedSet<R> oddRegions = _regions.get(r.getDocument());
    if (oddRegions == null || oddRegions.isEmpty()) return emptySet();
    return oddRegions.tailSet(r);
  }
  









  
  
  public R getRegionAt(OpenDefinitionsDocument odd, int offset) { 
 assert Utilities.TEST_MODE || EventQueue.isDispatchThread();
    
    
    @SuppressWarnings("unchecked")
    SortedSet<R> tail = getTailSet((R) newDocumentRegion(odd, 0, offset + 1));
    
    

    if (tail.size() == 0) return null;
    R r = tail.first();
    
    if (r.getStartOffset() <= offset) return r;
    else return null;
  }
  
  
  public Pair<R, R> getRegionInterval(OpenDefinitionsDocument odd, int offset) {
 assert Utilities.TEST_MODE || EventQueue.isDispatchThread();
    

    
    
    @SuppressWarnings("unchecked")
    SortedSet<R> tail = getTailSet((R) new DocumentRegion(odd, 0, offset - 119));
    
    
    
    if (tail.size() == 0) return null;
    
    
    Iterator<R> it = tail.iterator();
    R first = null;
    R last = null;
    
    
    while (it.hasNext()) {
      R r = it.next();

      
      int lineStart = r.getLineStartOffset();

      if (lineStart > offset) break;  
      int lineEnd = r.getLineEndOffset();

      if (lineStart - 1 <= offset && lineEnd >= offset) {  
        first = r;

        break;
      }
    }
    if (first == null) return null;
    
    
    last = first;
    while (it.hasNext()) {
      R r = it.next();
      int lineStart = r.getLineStartOffset();
      if (lineStart > offset) break;
      int lineEnd = r.getLineEndOffset();
      if (lineStart <= offset && lineEnd >= offset) {
        last = r;
      }
    }

    return new Pair<R, R>(first, last);
  }
    





















































  
  public Collection<R> getRegionsOverlapping(OpenDefinitionsDocument odd, int startOffset, int endOffset) {
    
 assert Utilities.TEST_MODE || EventQueue.isDispatchThread();
    LinkedList<R> result = new LinkedList<R>();
    if (startOffset == endOffset) return result;
    
    
    
    @SuppressWarnings("unchecked")
    SortedSet<R> tail = getTailSet((R) newDocumentRegion(odd, 0, startOffset + 1));
    
    

    for (R r: tail) {
      if (r.getStartOffset() >= endOffset) break;
      else result.add(r);
    }
    return result;
  }
  
  
  public void addRegion(final R region) {
    final OpenDefinitionsDocument odd = region.getDocument();
    SortedSet<R> docRegions = _regions.get(odd);
    if (docRegions == null) { 
      _documents.add(odd);
      docRegions = new TreeSet<R>(); 
      _regions.put(odd, docRegions);
    }
    
    
    final boolean alreadyPresent = docRegions.contains(region);
    if (! alreadyPresent) {    
      docRegions.add(region);  
    }
    
    assert _documents.contains(odd);
    
    
    if (! alreadyPresent) {
      
      _lock.startRead();
      try { for (RegionManagerListener<R> l: _listeners) { l.regionAdded(region); } } 
      finally { _lock.endRead(); }
    }
  }
  
  
  public void removeRegion(final R region) {

    OpenDefinitionsDocument doc = region.getDocument();
    SortedSet<R> docRegions = _regions.get(doc);

    if (docRegions == null) return;  
    final boolean wasRemoved = docRegions.remove(region);  
    if (docRegions.isEmpty()) {
      _documents.remove(doc);
      _regions.remove(doc);
    }

    
    if (wasRemoved) _notifyRegionRemoved(region);
  }
  
  
  public void removeRegions(Iterable<? extends R> regions) {
    for (R r: regions) removeRegion(r);
  }
  
  private void _notifyRegionRemoved(final R region) {
    _lock.startRead();
    try { for (RegionManagerListener<R> l: _listeners) { l.regionRemoved(region); } } 
    finally { _lock.endRead(); }
  }
    
  
  public void removeRegions(final OpenDefinitionsDocument doc) {
    assert doc != null;


    boolean found = _documents.remove(doc);

    if (found) {
      final SortedSet<R> regions = _regions.get(doc);

      
      while (! regions.isEmpty()) {
        R r = regions.first();
        regions.remove(r);  
        _notifyRegionRemoved(r);
      }

    }
  }
  
  
  public SortedSet<R> getRegions(OpenDefinitionsDocument odd) { return _regions.get(odd); }
  
  public ArrayList<R> getRegions() {
    ArrayList<R> regions = new ArrayList<R>();
    for (OpenDefinitionsDocument odd: _documents) regions.addAll(_regions.get(odd));
    return regions;
  }
  
  public ArrayList<FileRegion> getFileRegions() {
    ArrayList<FileRegion> regions = new ArrayList<FileRegion>();
    for (OpenDefinitionsDocument odd: _documents) {
      File f = odd.getRawFile();
      for (R r: _regions.get(odd)) regions.add(new DummyDocumentRegion(f, r.getStartOffset(), r.getEndOffset()));
    }
    return regions;
  }
      
  
  public boolean contains(R region) {
    for (OpenDefinitionsDocument doc: _documents) {
      if (_regions.get(doc).contains(region)) return true;
    }
    return false;
  }
  
  
  public void clearRegions() {
    for (R r: getRegions()) removeRegion(r);







  }
  



  
  
  public void changeRegion(final R region, Lambda<R,Object> cmd) {
    cmd.value(region);
    
    _lock.startRead();
    try { for (RegionManagerListener<R> l: _listeners) { l.regionChanged(region); } } 
    finally { _lock.endRead(); }            
  }
  
  
  public void updateLines(R firstRegion, R lastRegion) { 
 assert Utilities.TEST_MODE || EventQueue.isDispatchThread();
    
    
    SortedSet<R> tail = getTailSet(firstRegion);
    if (tail.size() == 0) return;

    List<R> toBeRemoved = new ArrayList<R>();  
    
    for (R region: tail) {
      if (region.compareTo(lastRegion) > 0) break;
      if (region.getStartOffset() == region.getEndOffset()) toBeRemoved.add(region); 
      else region.update();  

    }
    removeRegions(toBeRemoved);
  }
} 
