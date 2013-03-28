

package edu.rice.cs.plt.collect;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import java.io.Serializable;
import edu.rice.cs.plt.lambda.*;
import edu.rice.cs.plt.tuple.Option;
import edu.rice.cs.plt.tuple.Pair;
import edu.rice.cs.plt.iter.SizedIterable;
import edu.rice.cs.plt.iter.IterUtil;
import edu.rice.cs.plt.object.ObjectUtil;

public final class CollectUtil {
  
  
  private CollectUtil() {}
  
  
  public static final Predicate2<Set<?>, Object> SET_CONTENTS_PREDICATE = new SetContentsPredicate();
  
  private static final class SetContentsPredicate implements Predicate2<Set<?>, Object>, Serializable {
    private SetContentsPredicate() {}
    public boolean contains(Set<?> set, Object val) { return set.contains(val); }
  }
  
  
  @SuppressWarnings("unchecked") public static <T> Thunk<Set<T>> hashSetFactory() {
    return (Thunk<Set<T>>) (Thunk<?>) DefaultHashSetFactory.INSTANCE;
  }
  
  private static final class DefaultHashSetFactory<T> implements Thunk<Set<T>>, Serializable {
    public static final DefaultHashSetFactory<Object> INSTANCE = new DefaultHashSetFactory<Object>();
    private DefaultHashSetFactory() {}
    public Set<T> value() { return new HashSet<T>(); }
  }
  
  
  public static <T> Thunk<Set<T>> hashSetFactory(int initialCapacity) {
    return new CustomHashSetFactory<T>(initialCapacity);
  }
  
  private static final class CustomHashSetFactory<T> implements Thunk<Set<T>>, Serializable {
    private final int _initialCapacity;
    public CustomHashSetFactory(int initialCapacity) { _initialCapacity = initialCapacity; }
    public Set<T> value() { return new HashSet<T>(_initialCapacity); }
  }
  
  
  @SuppressWarnings("unchecked") public static <T> Thunk<Set<T>> linkedHashSetFactory() {
    return (Thunk<Set<T>>) (Thunk<?>) DefaultLinkedHashSetFactory.INSTANCE;
  }
  
  private static final class DefaultLinkedHashSetFactory<T> implements Thunk<Set<T>>, Serializable {
    public static final DefaultLinkedHashSetFactory<Object> INSTANCE = new DefaultLinkedHashSetFactory<Object>();
    private DefaultLinkedHashSetFactory() {}
    public Set<T> value() { return new LinkedHashSet<T>(); }
  }
  
  
  public static <T> Thunk<Set<T>> linkedHashSetFactory(int initialCapacity) {
    return new CustomLinkedHashSetFactory<T>(initialCapacity);
  }
  
  private static final class CustomLinkedHashSetFactory<T> implements Thunk<Set<T>>, Serializable {
    private final int _initialCapacity;
    public CustomLinkedHashSetFactory(int initialCapacity) { _initialCapacity = initialCapacity; }
    public Set<T> value() { return new LinkedHashSet<T>(_initialCapacity); }
  }
  
  
  @SuppressWarnings("unchecked")
  public static <T extends Comparable<? super T>> Thunk<Set<T>> treeSetFactory() {
    
    return (Thunk<Set<T>>) (Thunk<? extends Set<?>>) DefaultTreeSetFactory.INSTANCE;
  }
  
  private static final class DefaultTreeSetFactory<T extends Comparable<? super T>>
      implements Thunk<Set<T>>, Serializable {
    public static final DefaultTreeSetFactory<String> INSTANCE = new DefaultTreeSetFactory<String>();
    private DefaultTreeSetFactory() {}
    public Set<T> value() { return new TreeSet<T>(); }
  }
  
  
  public static <T> Thunk<Set<T>> treeSetFactory(Comparator<? super T> comparator) {
    return new CustomTreeSetFactory<T>(comparator);
  }
  
  private static final class CustomTreeSetFactory<T> implements Thunk<Set<T>>, Serializable {
    private final Comparator<? super T> _comp;
    public CustomTreeSetFactory(Comparator<? super T> comp) { _comp = comp; }
    public Set<T> value() { return new TreeSet<T>(_comp); }
  }
  
  
  @SuppressWarnings("unchecked") public static <T> Thunk<Set<T>> copyOnWriteArraySetFactory() {
    return (Thunk<Set<T>>) (Thunk<?>) CopyOnWriteArraySetFactory.INSTANCE;
  }
  
  private static final class CopyOnWriteArraySetFactory<T> implements Thunk<Set<T>>, Serializable {
    public static final CopyOnWriteArraySetFactory<Object> INSTANCE = new CopyOnWriteArraySetFactory<Object>();
    private CopyOnWriteArraySetFactory() {}
    public Set<T> value() { return new CopyOnWriteArraySet<T>(); }
  }
  
  
  @SuppressWarnings("unchecked") public static <K, V> Thunk<Map<K, V>> hashMapFactory() {
    return (Thunk<Map<K, V>>) (Thunk<?>) DefaultHashMapFactory.INSTANCE;
  }
  
  private static final class DefaultHashMapFactory<K, V> implements Thunk<Map<K, V>>, Serializable {
    public static final DefaultHashMapFactory<Object, Object> INSTANCE =
      new DefaultHashMapFactory<Object, Object>();
    private DefaultHashMapFactory() {}
    public Map<K, V> value() { return new HashMap<K, V>(); }
  }
  
  
  public static <K, V> Thunk<Map<K, V>> hashMapFactory(int initialCapacity) {
    return new CustomHashMapFactory<K, V>(initialCapacity);
  }
  
  private static final class CustomHashMapFactory<K, V> implements Thunk<Map<K, V>>, Serializable {
    private final int _initialCapacity;
    public CustomHashMapFactory(int initialCapacity) { _initialCapacity = initialCapacity; }
    public Map<K, V> value() { return new HashMap<K, V>(_initialCapacity); }
  }
  
  
  @SuppressWarnings("unchecked") public static <K, V> Thunk<Map<K, V>> linkedHashMapFactory() {
    return (Thunk<Map<K, V>>) (Thunk<?>) DefaultLinkedHashMapFactory.INSTANCE;
  }
  
  private static final class DefaultLinkedHashMapFactory<K, V> implements Thunk<Map<K, V>>, Serializable {
    public static final DefaultLinkedHashMapFactory<Object, Object> INSTANCE =
      new DefaultLinkedHashMapFactory<Object, Object>();
    private DefaultLinkedHashMapFactory() {}
    public Map<K, V> value() { return new LinkedHashMap<K, V>(); }
  }
  
  
  public static <K, V> Thunk<Map<K, V>> linkedHashMapFactory(int initialCapacity) {
    return new CustomLinkedHashMapFactory<K, V>(initialCapacity);
  }
  
  private static final class CustomLinkedHashMapFactory<K, V> implements Thunk<Map<K, V>>, Serializable {
    private final int _initialCapacity;
    public CustomLinkedHashMapFactory(int initialCapacity) { _initialCapacity = initialCapacity; }
    public Map<K, V> value() { return new LinkedHashMap<K, V>(_initialCapacity); }
  }
  
  
  @SuppressWarnings("unchecked")
  public static <K extends Comparable<? super K>, V> Thunk<Map<K, V>> treeMapFactory() {
    
    return (Thunk<Map<K, V>>) (Thunk<? extends Map<?, ?>>) DefaultTreeMapFactory.INSTANCE;
  }
  
  private static final class DefaultTreeMapFactory<K extends Comparable<? super K>, V>
      implements Thunk<Map<K, V>>, Serializable {
    public static final DefaultTreeMapFactory<String, Object> INSTANCE =
      new DefaultTreeMapFactory<String, Object>();
    private DefaultTreeMapFactory() {}
    public Map<K, V> value() { return new TreeMap<K, V>(); }
  }
  
  
  public static <K, V> Thunk<Map<K, V>> treeMapFactory(Comparator<? super K> comparator) {
    return new CustomTreeMapFactory<K, V>(comparator);
  }
  
  private static final class CustomTreeMapFactory<K, V> implements Thunk<Map<K, V>>, Serializable {
    private final Comparator<? super K> _comp;
    public CustomTreeMapFactory(Comparator<? super K> comp) { _comp = comp; }
    public Map<K, V> value() { return new TreeMap<K, V>(_comp); }
  }
  
  
  @SuppressWarnings("unchecked") public static <T> Thunk<List<T>> arrayListFactory() {
    return (Thunk<List<T>>) (Thunk<?>) DefaultArrayListFactory.INSTANCE;
  }
  
  private static final class DefaultArrayListFactory<T> implements Thunk<List<T>>, Serializable {
    public static final DefaultArrayListFactory<Object> INSTANCE = new DefaultArrayListFactory<Object>();
    private DefaultArrayListFactory() {}
    public List<T> value() { return new ArrayList<T>(); }
  }
  
  
  public static <T> Thunk<List<T>> arrayListFactory(int initialCapacity) {
    return new CustomArrayListFactory<T>(initialCapacity);
  }
  
  private static final class CustomArrayListFactory<T> implements Thunk<List<T>>, Serializable {
    private final int _initialCapacity;
    public CustomArrayListFactory(int initialCapacity) { _initialCapacity = initialCapacity; }
    public List<T> value() { return new ArrayList<T>(_initialCapacity); }
  }
  
  
  @SuppressWarnings("unchecked") public static <T> Thunk<List<T>> linkedListFactory() {
    return (Thunk<List<T>>) (Thunk<?>) DefaultLinkedListFactory.INSTANCE;
  }
  
  private static final class DefaultLinkedListFactory<T> implements Thunk<List<T>>, Serializable {
    public static final DefaultLinkedListFactory<Object> INSTANCE = new DefaultLinkedListFactory<Object>();
    private DefaultLinkedListFactory() {}
    public List<T> value() { return new LinkedList<T>(); }
  }
  
  
  @SuppressWarnings("unchecked") public static <T> Thunk<List<T>> copyOnWriteArrayListFactory() {
    return (Thunk<List<T>>) (Thunk<?>) CopyOnWriteArrayListFactory.INSTANCE;
  }
  
  private static final class CopyOnWriteArrayListFactory<T> implements Thunk<List<T>>, Serializable {
    public static final CopyOnWriteArrayListFactory<Object> INSTANCE = new CopyOnWriteArrayListFactory<Object>();
    private CopyOnWriteArrayListFactory() {}
    public List<T> value() { return new CopyOnWriteArrayList<T>(); }
  }
  
  
  public static <T> PredicateSet<T> makeSet(T... elements) {
    return makeSet(IterUtil.asIterable(elements));
  }
  
  
  public static <T> PredicateSet<T> makeSet(Iterable<? extends T> elements) {
    if (IterUtil.isEmpty(elements)) { 
      return EmptySet.make();
    }
    else if (IterUtil.sizeOf(elements, 2) == 1) {
      return new SingletonSet<T>(IterUtil.first(elements));
    }
    else {
      Set<T> result = new LinkedHashSet<T>(asCollection(elements));
      return new ImmutableSet<T>(result) {
        @Override public boolean hasFixedSize() { return true; }
        @Override public boolean isStatic() { return true; }
      };
    }
  }
  
  
  public static <T> PredicateSet<T> makeSet(Option<? extends T> opt) {
    if (opt.isSome()) { return new SingletonSet<T>(opt.unwrap()); }
    else { return EmptySet.make(); }
  }
  
  
  public static <T1, T2> Relation<T1, T2> makeRelation(Iterable<? extends Pair<? extends T1, ? extends T2>> pairs) {
    if (IterUtil.isEmpty(pairs)) { 
      return EmptyRelation.make();
    }
    else if (IterUtil.sizeOf(pairs, 2) == 1) {
      Pair<? extends T1, ? extends T2> elt = IterUtil.first(pairs);
      return new SingletonRelation<T1, T2>(elt.first(), elt.second());
    }
    else {
      Relation<T1, T2> result = IndexedRelation.makeLinkedHashBased();
      for (Pair<? extends T1, ? extends T2> elt : pairs) {
        result.add(elt.first(), elt.second());
      }
      return new ImmutableRelation<T1, T2>(result) {
        @Override public boolean hasFixedSize() { return true; }
        @Override public boolean isStatic() { return true; }
      };
    }
  }
  
  
  public static <T> List<T> makeList(Iterable<? extends T> iter) {
    return makeArrayList(iter);
  }

  
  public static <T> ArrayList<T> makeArrayList(Iterable<? extends T> iter) {
    if (iter instanceof Collection<?>) {
      @SuppressWarnings("unchecked") 
      Collection<? extends T> cast = (Collection<? extends T>) iter;
      return new ArrayList<T>(cast);
    }
    else if (iter instanceof SizedIterable<?>) {
      ArrayList<T> result = new ArrayList<T>(((SizedIterable<?>) iter).size());
      for (T e : iter) { result.add(e); }
      return result;
    }
    else {
      ArrayList<T> result = new ArrayList<T>();
      for (T e : iter) { result.add(e); }
      return result;
    }
  }
  
  
  public static <T> LinkedList<T> makeLinkedList(Iterable<? extends T> iter) {
    if (iter instanceof Collection<?>) {
      @SuppressWarnings("unchecked") 
      Collection<? extends T> cast = (Collection<? extends T>) iter;
      return new LinkedList<T>(cast);
    }
    else {
      LinkedList<T> result = new LinkedList<T>();
      for (T e : iter) { result.add(e); }
      return result;
    }
  }
  
  
  public static <T> ConsList<T> makeConsList(Iterable<? extends T> iter) {
    ConsList<T> result = ConsList.empty();
    for (T elt : IterUtil.reverse(iter)) { result = ConsList.cons(elt, result); }
    return result;
  }
  
  
  @SuppressWarnings("unchecked") public static <T> List<T> emptyList() {
    return (List<T>) Collections.EMPTY_LIST;
  }
  
  
  @SuppressWarnings("unchecked") public static <T> EmptySet<T> emptySet() {
    return (EmptySet<T>) EmptySet.INSTANCE;
  }
  
  
  @SuppressWarnings("unchecked") public static <K, V> EmptyMap<K, V> emptyMap() {
    return (EmptyMap<K, V>) EmptyMap.INSTANCE;
  }

  
  @SuppressWarnings("unchecked") public static <T1, T2> EmptyRelation<T1, T2> emptyRelation() {
    return (EmptyRelation<T1, T2>) EmptyRelation.INSTANCE;
  }
  
  
  public static <T> SingletonSet<T> singleton(T elt) {
    return new SingletonSet<T>(elt);
  }
  
  
  public static <T1, T2> SingletonRelation<T1, T2> singleton(T1 first, T2 second) {
    return new SingletonRelation<T1, T2>(first, second);
  }
  
  
  public static <K, V> SingletonMap<K, V> singletonMap(K key, V value) {
    return new SingletonMap<K, V>(key, value);
  }
  
  
  public static <T> Set<T> asSet(Iterable<T> iter) {
    if (iter instanceof Set<?>) { return (Set<T>) iter; }
    else { return new IterableSet<T>(iter); }
  }
  
  
  public static <T> PredicateSet<T> asPredicateSet(Iterable<T> iter) {
    if (iter instanceof PredicateSet<?>) { return (PredicateSet<T>) iter; }
    else if (iter instanceof Set<?>) { return new DelegatingSet<T>((Set<T>) iter); }
    else { return new IterableSet<T>(iter); }
  }
  
  
  public static <T> Collection<T> asCollection(Iterable<T> iter) {
    if (iter instanceof Collection<?>) { return (Collection<T>) iter; }
    else { return new IterableCollection<T>(iter); }
  }
  
  
  public static <K, V> LambdaMap<K, V> asLambdaMap(Map<K, V> m) {
    if (m instanceof LambdaMap<?, ?>) { return (LambdaMap<K, V>) m; }
    else { return new DelegatingMap<K, V>(m); }
  }
  
  
  public static <K, V> Map<K, V> asMap(Dictionary<K, V> d) {
    
    
    if (d instanceof Hashtable<?, ?>) { return (Hashtable<K, V>) d; }
    return new DictionaryMap<K, V>(d);
  }
  
  
  public static <T> PredicateSet<T> immutable(Set<? extends T> set) {
    return new ImmutableSet<T>(set);
  }
  
  
  public static <K, V> Map<K, V> immutable(Map<? extends K, ? extends V> map) {
    return new ImmutableMap<K, V>(map);
  }
  
  
  public static <T1, T2> ImmutableRelation<T1, T2> immutable(Relation<T1, T2> r) {
    return new ImmutableRelation<T1, T2>(r);
  }
  
  
  public static <T> PredicateSet<T> snapshot(Set<? extends T> set) {
    return makeSet(set);
  }
  
  
  public static <T> Iterable<T> conditionalSnapshot(Set<T> set, int threshold) {
    if (ObjectUtil.compositeSize(set) > threshold) { return makeSet(set); }
    else { return set; }
  }
  
  
  public static <T1, T2> Relation<T1, T2> snapshot(Relation<? extends T1, ? extends T2> relation) {
    return makeRelation(relation);
  }
  
  
  public static <T1, T2> Relation<T1, T2> conditionalSnapshot(Relation<T1, T2> rel, int threshold) {
    if (ObjectUtil.compositeSize(rel) > threshold) { return makeRelation(rel); }
    else { return rel; }
  }
  
  
  public static <K, V> LambdaMap<K, V> snapshot(Map<? extends K, ? extends V> map) {
    return new DelegatingMap<K, V>(new HashMap<K, V>(map));
  }
  
  
  public static <K, V> Map<K, V> conditionalSnapshot(Map<K, V> map, int threshold) {
    if (ObjectUtil.compositeSize(map) > threshold) { return snapshot(map); }
    else { return map; }
  }
  
  
  public static <T> List<T> snapshot(List<? extends T> list) {
    return makeArrayList(list);
  }

  
  public static <T> SnapshotSynchronizedSet<T> snapshotSynchronized(Set<T> s) {
    return new SnapshotSynchronizedSet<T>(s);
  }
  
  
  public static <T> SnapshotSynchronizedList<T> snapshotSynchronized(List<T> l) {
    return new SnapshotSynchronizedList<T>(l);
  }
  
  
  public static <T> PredicateSet<T> union(Set<? extends T> s1, Set<? extends T> s2) {
    return new UnionSet<T>(s1, s2);
  }
  
  
  public static <T> PredicateSet<T> union(Set<? extends T> set, T elt) {
    return new UnionSet<T>(set, new SingletonSet<T>(elt));
  }
  
  
  public static <T> PredicateSet<T> intersection(Set<?> s1, Set<? extends T> s2) {
    return new IntersectionSet<T>(s1, s2);
  }
  
  
  public static <T> PredicateSet<T> complement(Set<? extends T> domain, Set<?> excluded) {
    return new ComplementSet<T>(domain, excluded);
  }
  
  
  public static <T> PredicateSet<T> complement(Set<? extends T> domain, T excluded) {
    return new ComplementSet<T>(domain, new SingletonSet<T>(excluded));
  }
  
  
  public static <T> PredicateSet<T> filter(Set<? extends T> set, Predicate<? super T> predicate) {
    return new FilteredSet<T>(set, predicate);
  }
  
  
  public static <T1, T2> Relation<T1, T2> cross(Set<? extends T1> left, Set<? extends T2> right) {
    return new CartesianRelation<T1, T2>(left, right);
  }
  
  
  public static <T1, T2> Relation<T1, T2> union(Relation<T1, T2> r1, Relation<T1, T2> r2) {
    return new UnionRelation<T1, T2>(r1, r2);
  }
  
  
  public static <T1, T2> Relation<T1, T2> union(Relation<T1, T2> rel, T1 first, T2 second) {
    return new UnionRelation<T1, T2>(rel, new SingletonRelation<T1, T2>(first, second));
  }
  
  
  public static <T1, T2> Relation<T1, T2> intersection(Relation<T1, T2> r1, Relation<T1, T2> r2) {
    return new IntersectionRelation<T1, T2>(r1, r2);
  }
  
  
  public static <T1, T2> Relation<T1, T2> complement(Relation<T1, T2> domain,
                                                     Relation<? super T1, ? super T2> excluded) {
    return new ComplementRelation<T1, T2>(domain, excluded);
  }
  
  
  public static <T1, T2> Relation<T1, T2> complement(Relation<T1, T2> domain, T1 first, T2 second) {
    return new ComplementRelation<T1, T2>(domain, new SingletonRelation<T1, T2>(first, second));
  }
  
  
  public static <T1, T2, T3> Relation<T1, T3> compose(Relation<T1, T2> left, Relation<T2, T3> right) {
    return new ComposedRelation<T1, T2, T3>(left, right);
  }
  
  
  public static <T1, T2> Relation<T1, T2> filter(Relation<T1, T2> relation,
                                                 Predicate2<? super T1, ? super T2> pred) {
    return new FilteredRelation<T1, T2>(relation, pred);
  }
  
  
  public static <K, V> LambdaMap<K, V> union(Map<? extends K, ? extends V> parent,
                                             Map<? extends K, ? extends V> child) {
    return new UnionMap<K, V>(parent, child);
  }
  
  
  public static <K, X, V> LambdaMap<K, V> compose(Map<? extends K, ? extends X> left,
                                                  Map<? super X, ? extends V> right) {
    return new ComposedMap<K, X, V>(left, right);
  }
  
  
  @SuppressWarnings("unchecked")
  public static <T> Option<T> castIfContains(Collection<? extends T> c, Object obj) {
    if (c.contains(obj)) { return Option.some((T) obj); }
    else { return Option.none(); }
  }

  
  public static boolean containsAny(Collection<?> c, Iterable<?> candidates) {
    for (Object o : candidates) {
      if (c.contains(o)) { return true; }
    }
    return false;
  }

  
  public static boolean containsAll(Collection<?> c, Iterable<?> subset) {
    if (subset instanceof Collection<?>) {
      return c.containsAll((Collection<?>) subset);
    }
    else {
      for (Object o : subset) {
        if (!c.contains(o)) { return false; }
      }
      return true;
    }
  }

  
  public static <E> boolean addAll(Collection<E> c, Iterable<? extends E> elts) {
    if (elts instanceof Collection<?>) {
      @SuppressWarnings("unchecked")  
      Collection<? extends E> eltsColl = (Collection<? extends E>) elts;
      return c.addAll(eltsColl);
    }
    else {
      boolean result = false;
      for (E elt : elts) { result |= c.add(elt); }
      return result;
    }
  }

  
  public static boolean removeAll(Collection<?> c, Iterable<?> elts) {
    if (elts instanceof Collection<?>) {
      return c.removeAll((Collection<?>) elts);
    }
    else {
      boolean result = false;
      for (Object elt : elts) { result |= c.remove(elt); }
      return result;
    }
  }

  
  public static boolean retainAll(Collection<?> c, Iterable<?> elts) {
    if (elts instanceof Collection<?>) {
      return c.retainAll((Collection<?>) elts);
    }
    else { return c.retainAll(makeSet(elts)); }
  }

  
  public static <T> Set<T> functionClosure(T base, Lambda<? super T, ? extends T> function) {
    return functionClosure(Collections.singleton(base), function);
  }
  
  
  public static <T> Set<T> functionClosure(Set<? extends T> base, final Lambda<? super T, ? extends T> function) {
    Lambda<T, Set<T>> neighbors = new Lambda<T, Set<T>>() {
      public Set<T> value(T node) { return Collections.<T>singleton(function.value(node)); }
    };
    return graphClosure(base, neighbors);
  }
  
  
  public static <T> Set<T> partialFunctionClosure(T base, Lambda<? super T, ? extends Option<? extends T>> function) {
    return partialFunctionClosure(Collections.singleton(base), function);
  }
  
  
  public static <T> Set<T> partialFunctionClosure(Set<? extends T> base,
                                                  final Lambda<? super T, ? extends Option<? extends T>> function) {
    Lambda<T, Set<T>> neighbors = new Lambda<T, Set<T>>() {
      public Set<T> value(T node) { return makeSet(function.value(node)); }
    };
    return graphClosure(base, neighbors);
  }
  
  
  public static <T> Set<T> graphClosure(T base, Lambda<? super T, ? extends Iterable<? extends T>> neighbors) {
    return graphClosure(Collections.singleton(base), neighbors);
  }
  
  
  public static <T> Set<T> graphClosure(Set<? extends T> base,
                                        Lambda<? super T, ? extends Iterable<? extends T>> neighbors) {
    Set<T> result = new LinkedHashSet<T>(base);
    LinkedList<T> workList = new LinkedList<T>(base); 
    while (!workList.isEmpty())  {
      for (T newElt : neighbors.value(workList.removeFirst())) {
        if (!result.contains(newElt)) {
          result.add(newElt);
          workList.addLast(newElt);
        }
      }
    }
    return result;
  }
  
  
  public static <T> List<T> maxList(Iterable<? extends T> vals, Order<? super T> order) {
    switch (IterUtil.sizeOf(vals, 2)) {
      case 0: return Collections.emptyList();
      case 1: return Collections.singletonList(IterUtil.first(vals));
      default:
        LinkedList<? extends T> workList = makeLinkedList(vals);
        LinkedList<T> result = new LinkedList<T>();
        Iterable<T> remainingTs = IterUtil.compose(workList, result);
        while (!workList.isEmpty()) {
          
          T t = workList.removeLast();
          boolean discard = IterUtil.or(remainingTs, LambdaUtil.bindFirst(order, t));
          if (!discard) { result.addFirst(t); }
        }
        return result;
    }
  }
  
  
  public static <T> List<T> composeMaxLists(Iterable<? extends T> vals1, Iterable<? extends T> vals2,
                                            Order<? super T> order) {
    List<T> results2 = new LinkedList<T>();
    for (T t : vals2) {
      
      boolean discard = IterUtil.or(vals1, LambdaUtil.bindFirst(order, t));
      if (!discard) { results2.add(t); }
    }
    List<T> results1 = new LinkedList<T>();
    for (T t : vals1) {
      
      boolean discard = IterUtil.or(results2, LambdaUtil.bindFirst(order, t));
      if (!discard) { results1.add(t); }
    }
    results1.addAll(results2);
    return results1;
  }
  
  
  public static <T> List<T> minList(Iterable<? extends T> vals, Order<? super T> order) {
    return maxList(vals, inverse(order));
  }
  
  
  public static <T> List<T> composeMinLists(Iterable<? extends T> vals1, Iterable<? extends T> vals2,
                                            Order<? super T> order) {
    return composeMaxLists(vals1, vals2, inverse(order));
  }
  
  
  @SuppressWarnings("unchecked") public static <T extends Comparable<? super T>> TotalOrder<T> naturalOrder() {
    return (TotalOrder<T>) NaturalOrder.INSTANCE;
  }
    
  private static final class NaturalOrder<T extends Comparable<? super T>>
      extends TotalOrder<T> implements Serializable {
    private static final NaturalOrder<Comparable<Object>> INSTANCE = new NaturalOrder<Comparable<Object>>();
    private NaturalOrder() {}
    public int compare(T arg1, T arg2) { return arg1.compareTo(arg2); }
  }
  
  
  public static <T> TotalOrder<T> asTotalOrder(Comparator<? super T> comp) {
    return new ComparatorTotalOrder<T>(comp);
  }
  
  private static final class ComparatorTotalOrder<T> extends TotalOrder<T> {
    private final Comparator<? super T> _comp;
    public ComparatorTotalOrder(Comparator<? super T> comp) { _comp = comp; }
    public int compare(T arg1, T arg2) { return _comp.compare(arg1, arg2); }
    public boolean equals(Object o) {
      if (this == o) { return true; }
      else if (!(o instanceof ComparatorTotalOrder<?>)) { return false; }
      else { return _comp.equals(((ComparatorTotalOrder<?>) o)._comp); }
    }
    public int hashCode() { return ObjectUtil.hash(ComparatorTotalOrder.class, _comp); }
  }
  
  
  public static <T> Order<T> inverse(Order<? super T> ord) {
    return new InverseOrder<T>(ord);
  }
  
  private static final class InverseOrder<T> implements Order<T> {
    private final Order<? super T> _ord;
    public InverseOrder(Order<? super T> ord) { _ord = ord; }
    public boolean contains(T arg1, T arg2) { return _ord.contains(arg2, arg1); }
    public boolean equals(Object o) {
      if (this == o) { return true; }
      else if (!(o instanceof InverseOrder<?>)) { return false; }
      else { return _ord.equals(((InverseOrder<?>) o)._ord); }
    }
    public int hashCode() { return ObjectUtil.hash(InverseOrder.class, _ord); }
  }

  
  public static <T> TotalOrder<T> inverse(Comparator<? super T> ord) {
    return new InverseTotalOrder<T>(ord);
  }
  
  private static final class InverseTotalOrder<T> extends TotalOrder<T> {
    private final Comparator<? super T> _ord;
    public InverseTotalOrder(Comparator<? super T> ord) { _ord = ord; }
    public int compare(T arg1, T arg2) { return _ord.compare(arg2, arg1); }
    public boolean equals(Object o) {
      if (this == o) { return true; }
      else if (!(o instanceof InverseTotalOrder<?>)) { return false; }
      else { return _ord.equals(((InverseTotalOrder<?>) o)._ord); }
    }
    public int hashCode() { return ObjectUtil.hash(InverseTotalOrder.class, _ord); }
  }
  
  
  public static final Order<Iterable<?>> SUBSET_ORDER = new SubsetOrder();
  
  private static final class SubsetOrder implements Order<Iterable<?>>, Serializable {
    private SubsetOrder() {}
    public boolean contains(Iterable<?> sub, Iterable<?> sup) { return IterUtil.containsAll(sup, sub); }
  }
  
  
  public static final Order<String> SUBSTRING_ORDER = new SubstringOrder();
  
  private static final class SubstringOrder implements Order<String>, Serializable {
    private SubstringOrder() {}
    public boolean contains(String sub, String sup) { return sup.contains(sub); }
  }
  
  
  public static final Order<String> STRING_PREFIX_ORDER = new StringPrefixOrder();
  
  private static final class StringPrefixOrder implements Order<String>, Serializable {
    private StringPrefixOrder() {}
    public boolean contains(String pre, String s) { return s.startsWith(pre); }
  }

}
