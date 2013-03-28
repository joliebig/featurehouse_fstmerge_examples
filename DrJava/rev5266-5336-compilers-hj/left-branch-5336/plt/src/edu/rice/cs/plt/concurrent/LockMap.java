

package edu.rice.cs.plt.concurrent;

import java.util.WeakHashMap;
import java.util.concurrent.locks.ReentrantLock;

import edu.rice.cs.plt.tuple.IdentityWrapper;


public class LockMap<T> {
  
  private final WeakHashMap<IdentityWrapper<T>, ReentrantLock> _map;
  
  public LockMap() { _map = new WeakHashMap<IdentityWrapper<T>, ReentrantLock>(); }

  public LockMap(int initialCapacity) {
    _map = new WeakHashMap<IdentityWrapper<T>, ReentrantLock>(initialCapacity);
  }
  
  
  public synchronized ReentrantLock get(T val) {
    IdentityWrapper<T> key = IdentityWrapper.make(val);
    if (!_map.containsKey(key)) { _map.put(key, new ReentrantLock()); }
    return _map.get(key);
  }
  
  
  public Runnable lock(T val) {
    final ReentrantLock l = get(val);
    Runnable result = new Unlocker(l);
    l.lock();
    return result;
  }
  
  
  private static class Unlocker implements Runnable {
    private final ReentrantLock _l;
    public Unlocker(ReentrantLock l) { _l = l; }
    public void run() { _l.unlock(); }
  }
  
}
