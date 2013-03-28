
package genj.renderer;

import java.awt.RenderingHints.Key;


public class RenderSelectionHintKey extends Key {

  public final static Key KEY = new RenderSelectionHintKey();

  private RenderSelectionHintKey() {
    super(0);
  }

  @Override
  public boolean isCompatibleValue(Object val) {
    return val instanceof Boolean;
  }
}