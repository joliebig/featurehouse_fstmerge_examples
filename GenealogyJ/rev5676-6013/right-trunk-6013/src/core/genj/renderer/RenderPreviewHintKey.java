
package genj.renderer;

import java.awt.RenderingHints.Key;


public class RenderPreviewHintKey extends Key {

  public final static Key KEY = new RenderPreviewHintKey();

  private RenderPreviewHintKey() {
    super(0);
  }

  @Override
  public boolean isCompatibleValue(Object val) {
    return val instanceof Boolean;
  }
}