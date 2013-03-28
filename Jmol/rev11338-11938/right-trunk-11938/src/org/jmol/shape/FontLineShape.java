

package org.jmol.shape;

import java.util.BitSet;

import org.jmol.modelset.TickInfo;

public abstract class FontLineShape extends FontShape {

  
  
  TickInfo[] tickInfos = new TickInfo[4];

  public void setProperty(String propertyName, Object value, BitSet bs) {

    if ("tickInfo" == propertyName) {
      TickInfo t = (TickInfo) value;
      if (t.ticks == null) {
        
        if (t.type.equals(" "))
          tickInfos[0] = tickInfos[1] = tickInfos[2] = tickInfos[3] = null;
        else
          tickInfos["xyz".indexOf(t.type) + 1] = null;
        return;
      }
      tickInfos["xyz".indexOf(t.type) + 1] = t;
      return;
    }

    super.setProperty(propertyName, value, bs);
  }



}
