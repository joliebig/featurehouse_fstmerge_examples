

package org.jmol.shape;

import java.util.BitSet;

import org.jmol.modelset.TickInfo;
import org.jmol.util.Escape;

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

  public String getShapeState() {
    String s = super.getShapeState();
    if (tickInfos == null)
      return s;
    StringBuffer sb = new StringBuffer(s);
    if (tickInfos[0] != null)
      appendTickInfo(sb, "", 0);
    if (tickInfos[1] != null)
      appendTickInfo(sb, " x", 1);
    if (tickInfos[2] != null)
      appendTickInfo(sb, " y", 2);
    if (tickInfos[3] != null)
      appendTickInfo(sb, " z", 3);
    if (s.indexOf(" off") >= 0)
      sb.append("  " + myType + " off;\n");
    return sb.toString();
  }
  
  private void appendTickInfo(StringBuffer sb, String type, int i) {
    sb.append("  ");
    sb.append(myType);
    sb.append(type);
    addTickInfo(sb, tickInfos[i], false);
    sb.append(";\n");
  }

  public static void addTickInfo(StringBuffer sb, TickInfo tickInfo, boolean addFirst) {
    sb.append(" ticks ").append(Escape.escape(tickInfo.ticks));
    if (tickInfo.tickLabelFormats != null)
      sb.append(" format ").append(Escape.escape(tickInfo.tickLabelFormats, false));
    if (tickInfo.scale != null)
      sb.append(" scale ").append(Escape.escape(tickInfo.scale));
    if (addFirst && !Float.isNaN(tickInfo.first) && tickInfo.first != 0)
      sb.append(" first ").append(tickInfo.first);
  }
}
