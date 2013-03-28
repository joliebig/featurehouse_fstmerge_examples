

package edu.rice.cs.drjava.model;


public interface LightWeightParsingListener {
  
  public void enclosingClassNameUpdated(OpenDefinitionsDocument doc, String old, String updated);
}