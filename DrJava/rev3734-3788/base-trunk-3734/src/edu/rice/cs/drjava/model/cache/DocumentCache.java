

package edu.rice.cs.drjava.model.cache;

import javax.swing.text.BadLocationException;
import java.util.*;
import java.io.IOException;

import edu.rice.cs.drjava.model.definitions.DefinitionsDocument;
import edu.rice.cs.drjava.model.OpenDefinitionsDocument;
import edu.rice.cs.drjava.model.FileMovedException;
import edu.rice.cs.util.UnexpectedException;
import edu.rice.cs.util.swing.Utilities;
import edu.rice.cs.util.OrderedHashSet;



public class DocumentCache {
  
  private static final int INIT_CACHE_SIZE = 24;
  
  
  private int CACHE_SIZE;
  
  private OrderedHashSet<DocManager> _residentQueue;
  
  private Object _cacheLock = new Object();
    
  public DocumentCache(int size) {

    CACHE_SIZE = size;
    _residentQueue = new OrderedHashSet<DocManager>();
  }
  
  public DocumentCache() { this(INIT_CACHE_SIZE); }

  
  public DCacheAdapter register(OpenDefinitionsDocument odd, DDReconstructor rec) {
    DocManager mgr = new DocManager(rec, odd.toString(), odd.isUntitled());
    notifyRegistrationListeners(odd, mgr);

    return mgr;
  }
  
  
  public void setCacheSize(int size) {
    if (size <= 0) throw new IllegalArgumentException("Cannot set the cache size to zero or less.");
    int dist;
    DocManager[] removed = null;  
    synchronized(_cacheLock) {    
      CACHE_SIZE = size;
      dist = _residentQueue.size() - CACHE_SIZE;
      if (dist > 0) { 
        removed = new DocManager[dist];
        for (int i = 0; i < dist; i++) removed[i] = _residentQueue.remove(0);
      }
      if (dist > 0) kickOut(removed);
    }
  }
  
  
  private void kickOut(DocManager[] removed) {
    for (int i = 0; i < removed.length; i++) {
      DocManager dm = removed[i];
      dm.kickOut();
    }
  }
    
  public int getCacheSize() { return CACHE_SIZE; }
  public int getNumInCache() { return _residentQueue.size(); }
    
  
  
  
  
  private static final int IN_QUEUE = 0;     
  private static final int UNTITLED = 1;     
  private static final int NOT_IN_QUEUE = 2; 
  private static final int UNMANAGED = 3;    
  
  
  
  private class DocManager implements DCacheAdapter {
    
    private int _stat; 
    private DDReconstructor _rec;
    private DefinitionsDocument _doc;
    private String _fileName;
    
    
    public DocManager(DDReconstructor rec, String fn, boolean isUntitled) {

      if (isUntitled) _stat = UNTITLED; 
      else _stat = NOT_IN_QUEUE;
      _rec = rec;
      _doc = null;
     _fileName = fn;
    }
    
    public DDReconstructor getReconstructor() { return _rec; }
    
    
    public DefinitionsDocument getDocument() throws IOException, FileMovedException {

      




      synchronized(_cacheLock) { 
        if (_doc != null) return _doc;  
        try { 
          _doc = _rec.make();
          assert _doc != null;
        }
        catch(BadLocationException e) { throw new UnexpectedException(e); }

        if (_stat == NOT_IN_QUEUE) add();       
        return _doc;
      }
    }
    
    
    public boolean isReady() { synchronized (_cacheLock) { return _doc != null; } }
  
    
    public void close() {

      synchronized(_cacheLock) {
        _residentQueue.remove(this);
        closingKickOut();
      }
    }
    
    public void documentModified() {
      synchronized(_cacheLock) { 
        _residentQueue.remove(this); 
        _stat = UNMANAGED;
      }
    }
    
     public void documentReset() {
      synchronized(_cacheLock) { 
        if (_stat == UNMANAGED) add(); 
      }
    }
     
    
    public void documentSaved(String fileName) {

      synchronized(_cacheLock) {  
        if (isUnmanagedOrUntitled()) {
          _fileName = fileName;
          add();  
        }
      }
    }
    
    
    private void add() {

      if (! _residentQueue.contains(this)) {
        _residentQueue.add(this);
        _stat = IN_QUEUE;
      }
      if (_residentQueue.size() > CACHE_SIZE) _residentQueue.get(0).remove();
    }
    
    
    private void remove() { 
      boolean removed = _residentQueue.remove(this);
      kickOut();
    }
    
    
    private boolean isUnmanagedOrUntitled() { return (_stat & 0x1) != 0; }  
     
    
    void kickOut() { kickOut(false); }
    
    void closingKickOut() { kickOut(true); }
   
    private void kickOut(boolean isClosing) {

      if (! isClosing) {
        

        _rec.saveDocInfo(_doc);
      }
      if (_doc != null) {
        _doc.close(); 
        _doc = null;
      }
      _stat = NOT_IN_QUEUE;
    }
    
    public String toString() { return _fileName; } 
  }
  
  
  
  
  public interface RegistrationListener {
    public void registered(OpenDefinitionsDocument odd, DCacheAdapter man);
  }
  
  private LinkedList<RegistrationListener> _regListeners =   new LinkedList<RegistrationListener>();
  
  public void addRegistrationListener(RegistrationListener list) { _regListeners.add(list); }
  public void removeRegistrationListener(RegistrationListener list) { _regListeners.remove(list); }
  public void clearRegistrationListeners() { _regListeners.clear(); }
  private void notifyRegistrationListeners(final OpenDefinitionsDocument odd, final DocManager man) {
    Utilities.invokeAndWait(new Runnable() {
      public void run() { for (RegistrationListener list : _regListeners) { list.registered(odd, man); } }
    });
  }
}
