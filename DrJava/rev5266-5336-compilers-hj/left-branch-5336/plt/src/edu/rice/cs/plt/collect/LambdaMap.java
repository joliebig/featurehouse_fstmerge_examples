

package edu.rice.cs.plt.collect;

import java.util.Map;
import edu.rice.cs.plt.lambda.Lambda;


public interface LambdaMap<K, V> extends Map<K, V>, Lambda<K, V> {
  public PredicateSet<K> keySet();
}
