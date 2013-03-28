package edu.rice.cs.dynamicjava.symbol;

import edu.rice.cs.dynamicjava.symbol.type.Type;
import edu.rice.cs.plt.lambda.DelayedThunk;


public class BoundedSymbol {
  
  private final Object _id;
  private final boolean _generated;
  private final String _name;
  private final DelayedThunk<Type> _upperBound;
  private final DelayedThunk<Type> _lowerBound;

  public BoundedSymbol(Object id) {
    _id = id;
    _generated = true;
    _name = null;
    _upperBound = new DelayedThunk<Type>();
    _lowerBound = new DelayedThunk<Type>();
  }
  
  public BoundedSymbol(Object id, String name) {
    _id = id;
    _generated = false;
    _name = name; 
    _upperBound = new DelayedThunk<Type>();
    _lowerBound = new DelayedThunk<Type>();
  }
  
  public BoundedSymbol(Object id, Type upperBound, Type lowerBound) { 
    this(id);
    _upperBound.set(upperBound);
    _lowerBound.set(lowerBound);
  }
  
  public BoundedSymbol(Object id, String name, Type upperBound, Type lowerBound) {
    this(id, name);
    _upperBound.set(upperBound);
    _lowerBound.set(lowerBound);
  }
  
  
  
  public boolean generated() { return _generated; }
  
  public String name() { 
    if (_generated) { throw new IllegalArgumentException("Symbol is unnamed"); }
    else { return _name; }
  }
  
  public void initializeUpperBound(Type t) { _upperBound.set(t); }
  
  public void initializeLowerBound(Type t) { _lowerBound.set(t); }
  
  public Type upperBound() { return _upperBound.value(); }
  
  public Type lowerBound() { return _lowerBound.value(); }
  
  public String toString() { return "symbol " + _id; }
  
  public boolean equals(Object o) {
    if (this == o) { return true; }
    else if (!(o instanceof BoundedSymbol)) { return false; }
    else { return ((BoundedSymbol) o)._id.equals(_id); }
  }
  
  public int hashCode() {
    return getClass().hashCode() ^ _id.hashCode();
  }

}
