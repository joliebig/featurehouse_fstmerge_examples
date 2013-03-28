

package edu.rice.cs.plt.tuple;

import java.io.Serializable;
import java.io.ObjectInputStream;
import java.io.IOException;


public abstract class Tuple implements Serializable {

  private transient int _hashCode;
  private transient boolean _validHashCode;
  
  protected Tuple() {
    _validHashCode = false;
    
  }
  
  protected abstract int generateHashCode();
  
  public int hashCode() {
    if (!_validHashCode) { _hashCode = generateHashCode(); _validHashCode = true; }
    return _hashCode;
  }
  
  private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
    stream.defaultReadObject();
    _validHashCode = false;
    
  }

}
