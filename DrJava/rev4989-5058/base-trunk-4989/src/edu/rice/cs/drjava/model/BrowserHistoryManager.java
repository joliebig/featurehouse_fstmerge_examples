

package edu.rice.cs.drjava.model;

import java.awt.EventQueue;

import java.util.*;

import edu.rice.cs.plt.lambda.Lambda;
import edu.rice.cs.util.swing.Utilities;


public class BrowserHistoryManager extends EventNotifier<RegionManagerListener<BrowserDocumentRegion>> {
  
  public static final int DIFF_THRESHOLD = 5;

  
  private volatile Stack<BrowserDocumentRegion> _pastRegions = new Stack<BrowserDocumentRegion>();
  private volatile Stack<BrowserDocumentRegion> _futureRegions = new Stack<BrowserDocumentRegion>();
  
  private volatile int _maxSize;
  
  
  public BrowserHistoryManager(int size) { _maxSize = size; }
  
  
  public BrowserHistoryManager() { this(0); }
  
  
  public void addBrowserRegion(final BrowserDocumentRegion r, final GlobalEventNotifier notifier) { 
     assert Utilities.TEST_MODE || EventQueue.isDispatchThread();

    final BrowserDocumentRegion current = getCurrentRegion();
    if ((current!=null) && (similarRegions(current, r))) {
      
      

      current.update(r);
    }
    else {
      _pastRegions.push(r);
      r.getDocument().addBrowserRegion(r);
      
      
      Utilities.invokeLater(new Runnable() { 
        public void run() {
          _lock.startRead();
          try { for (RegionManagerListener<BrowserDocumentRegion> l: _listeners) { l.regionAdded(r); } } 
          finally { _lock.endRead(); }
        } 
      });
      
      
      shrinkManager();
    }
    notifier.browserChanged();
  }
  
  
  public void addBrowserRegionBefore(final BrowserDocumentRegion r, final GlobalEventNotifier notifier) { 
     assert Utilities.TEST_MODE || EventQueue.isDispatchThread();

    final BrowserDocumentRegion current = getCurrentRegion();
    if ((current!=null) && (similarRegions(current, r))) {
      
      

      current.update(r);
    }
    else {
      if (_pastRegions.size()==0) {
        _pastRegions.push(r);
      }
      else {
        _futureRegions.push(_pastRegions.pop());
        _pastRegions.push(r);
      }
      r.getDocument().addBrowserRegion(r);
      
      
      Utilities.invokeLater(new Runnable() { 
        public void run() {
          _lock.startRead();
          try { for (RegionManagerListener<BrowserDocumentRegion> l: _listeners) { l.regionAdded(r); } } 
          finally { _lock.endRead(); }
        } 
      });
      
      
      shrinkManager();
    }
    notifier.browserChanged();
  }
  
  
  private void shrinkManager() {
    if (_maxSize > 0) {
      int size = _pastRegions.size() + _futureRegions.size();
      int diff = size - _maxSize;
      for (int i = 0; i < diff; ++i) {
        
        remove(((_pastRegions.size()>_futureRegions.size())?_pastRegions:_futureRegions).get(0));
      }
    }
  }
  
  
  public  void remove(final BrowserDocumentRegion r) {
    OpenDefinitionsDocument doc = r.getDocument();
    if (!_pastRegions.remove(r)) _futureRegions.remove(r);
    doc.removeBrowserRegion(r);
    
    Utilities.invokeLater(new Runnable() { 
      public void run() {
        _lock.startRead();
        try { for (RegionManagerListener<BrowserDocumentRegion> l: _listeners) { l.regionRemoved(r); } } 
        finally { _lock.endRead(); }
      } 
    });
  }
  
  
  public SortedSet<BrowserDocumentRegion> getRegions() {
    TreeSet<BrowserDocumentRegion> ts = new TreeSet<BrowserDocumentRegion>(_pastRegions);
    ts.addAll(_futureRegions);
    return ts;
  }
  
  
  public  void clearBrowserRegions() {
    while(_pastRegions.size()+_futureRegions.size()>0) {
      remove(((_pastRegions.size()>_futureRegions.size())?_pastRegions:_futureRegions).get(0));
    }
  }
  
  
  public BrowserDocumentRegion getCurrentRegion() {
    if (_pastRegions.isEmpty()) return null;
    return _pastRegions.peek();
  }
  
  
  public  boolean isCurrentRegionFirst() { return (_pastRegions.size()<2); }
  
  
  public  boolean isCurrentRegionLast() { return (_futureRegions.size()<1); }
  
  
  public  BrowserDocumentRegion nextCurrentRegion(final GlobalEventNotifier notifier) {
    if (isCurrentRegionLast()) return null;
    _pastRegions.push(_futureRegions.pop());
    notifier.browserChanged();
    return _pastRegions.peek();
  }
  
  
  public  BrowserDocumentRegion prevCurrentRegion(final GlobalEventNotifier notifier) {
    if (isCurrentRegionFirst()) return null;
    _futureRegions.push(_pastRegions.pop());
    notifier.browserChanged();
    return _pastRegions.peek();
  }  
  
  
  public  void setMaximumSize(int size) {
    _maxSize = size;
    
    
    shrinkManager();
  }
  
  
  public int getMaximumSize() { return _maxSize; }
  
  
  public void changeRegion(final BrowserDocumentRegion region, Lambda<BrowserDocumentRegion,Object> cmd) {
    cmd.value(region);
    Utilities.invokeLater(new Runnable() { public void run() {
      
      _lock.startRead();
      try {
        for (RegionManagerListener<BrowserDocumentRegion> l: _listeners) { l.regionChanged(region); }
      } finally { _lock.endRead(); }            
    } });
  }
  
  
  public static boolean similarRegions(BrowserDocumentRegion r1, BrowserDocumentRegion r2) {
    OpenDefinitionsDocument d = r1.getDocument();
    if (d!=r2.getDocument()) return false;
    int l1 = d.getLineOfOffset(r1.getStartOffset());
    int l2 = d.getLineOfOffset(r2.getStartOffset());
    return (Math.abs(l1-l2) <= DIFF_THRESHOLD);
  }
  
  public String toString() {
    ArrayList<BrowserDocumentRegion> future = new ArrayList<BrowserDocumentRegion>(_futureRegions);
    Collections.reverse(future);
    return "Past: "+_pastRegions.toString()+", Future: "+future.toString();
  }
}