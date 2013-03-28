

package edu.rice.cs.drjava.model;

import edu.rice.cs.drjava.model.definitions.*;


public interface LightWeightParsingListener {
  
  public void enclosingClassNameUpdated(OpenDefinitionsDocument doc, String old, String updated);
}