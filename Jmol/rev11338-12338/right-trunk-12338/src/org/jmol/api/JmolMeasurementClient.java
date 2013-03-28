

package org.jmol.api;

import org.jmol.modelset.Measurement;

public interface JmolMeasurementClient {

  public abstract void processNextMeasure(Measurement m);
  
}
