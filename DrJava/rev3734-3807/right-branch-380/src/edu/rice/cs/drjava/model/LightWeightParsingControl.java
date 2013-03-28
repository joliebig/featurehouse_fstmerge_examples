

package edu.rice.cs.drjava.model;

import java.util.List;

import edu.rice.cs.drjava.model.definitions.*;


public interface LightWeightParsingControl {
  
  public void update(OpenDefinitionsDocument doc);
  
  
  public void delay();
  
  
  public void setAutomaticUpdates(boolean b);

  
  public void reset();
  
  
  public String getEnclosingClassName(OpenDefinitionsDocument doc);
  
  
  public void addListener(LightWeightParsingListener l);
  
  
  public void removeListener(LightWeightParsingListener l);
  
  
  public void removeAllListeners();
  
  
  public List<LightWeightParsingListener> getListeners();
}