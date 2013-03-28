
package genj.renderer;

import java.awt.Graphics2D;
import java.awt.RenderingHints.Key;


public class EmptyHintKey extends Key {

  public final static Key KEY = new EmptyHintKey();

  private EmptyHintKey() {
    super(0);
  }

  @Override
  public boolean isCompatibleValue(Object val) {
    return val instanceof Boolean;
  }
  
  public static boolean isEmpty(Graphics2D g) {
    return Boolean.TRUE.equals(g.getRenderingHint(KEY));
  }
}