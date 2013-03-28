

package edu.rice.cs.plt.collect;

import java.util.Map;
import java.util.HashMap;
import edu.rice.cs.plt.lambda.Lambda;
import edu.rice.cs.plt.lambda.LambdaUtil;


public class TotalMap<K, V> {
  
  private final Lambda<? super K, ? extends V> _lambda;
  private final Map<? super K, V> _overrides;
  private final boolean _cache;
  
  
  public TotalMap() {
    this(LambdaUtil.<V>nullLambda(), new HashMap<K, V>(), false);
  }
  
  
  public TotalMap(Lambda<? super K, ? extends V> lambda) { 
    this(lambda, new HashMap<K, V>(), false);
  }
  
  
  public TotalMap(Lambda<? super K, ? extends V> lambda, boolean cache) {
    this (lambda, new HashMap<K, V>(), cache);
  }
  
  
  public TotalMap(Map<? super K, V> overrides) {
    this(LambdaUtil.<V>nullLambda(), overrides, false);
  }
  
  
  public TotalMap(Lambda<? super K, ? extends V> lambda, Map<? super K, V> overrides) {
    this (lambda, overrides, false);
  }
  
  
  public TotalMap(Lambda<? super K, ? extends V> lambda, Map<? super K, V> overrides,
                  boolean cache) {
    _lambda = lambda;
    _overrides = overrides;
    _cache = cache;
  }

  
  public V get(K key) {
    if (_overrides.containsKey(key)) { return _overrides.get(key); }
    else { 
      V result = _lambda.value(key);
      if (_cache) { _overrides.put(key, result); }
      return result;
    }
  }
  
  
  public void override(K key, V value) { _overrides.put(key, value); }
  
  
  public boolean containsOverride(Object key) { return _overrides.containsKey(key); }
  
  
  public V revert(K key) { return _overrides.remove(key); }
  
  
  public void overrideAll(Map<? extends K, ? extends V> map) {
    _overrides.putAll(map);
  }
  
  
  public void revertAll() { _overrides.clear(); }
  
  
  public int cacheSize() { return _overrides.size(); }
    
  
  public static <K, V> TotalMap<K, V> make() { return new TotalMap<K, V>(); }
  
  
  public static <K, V> TotalMap<K, V> make(Lambda<? super K, ? extends V> lambda) {
    return new TotalMap<K, V>(lambda);
  }
  
  
  public static <K, V> TotalMap<K, V> make(Lambda<? super K, ? extends V> lambda, boolean cache) {
    return new TotalMap<K, V>(lambda, cache);
  }
  
  
  public static <K, V> TotalMap<K, V> make(Map<? super K, V> overrides) {
    return new TotalMap<K, V>(overrides);
  }
  
  
  public static <K, V> TotalMap<K, V> make(Lambda<? super K, ? extends V> lambda, 
                                    Map<? super K, V> overrides) {
    return new TotalMap<K, V>(lambda, overrides);
  }
  
  
  public static <K, V> TotalMap<K, V> make(Lambda<? super K, ? extends V> lambda, 
                                    Map<? super K, V> overrides, boolean cache) {
    return new TotalMap<K, V>(lambda, overrides, cache);
  }
  
}
