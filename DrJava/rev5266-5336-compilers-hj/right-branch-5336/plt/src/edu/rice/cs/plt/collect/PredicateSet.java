

package edu.rice.cs.plt.collect;

import java.util.Set;
import edu.rice.cs.plt.lambda.Predicate;
import edu.rice.cs.plt.iter.SizedIterable;


public interface PredicateSet<T> extends Set<T>, Predicate<Object>, SizedIterable<T> {
}
