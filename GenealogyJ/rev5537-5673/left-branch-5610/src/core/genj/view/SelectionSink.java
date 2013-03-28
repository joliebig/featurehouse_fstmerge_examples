package genj.view;

import genj.gedcom.Context;
import genj.window.WindowManager;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;


public interface SelectionSink {

  
  public void fireSelection(Context context, boolean isActionPerformed);

  public class Dispatcher {
    public static void fireSelection(AWTEvent event, Context context) {
      boolean isActionPerformed = false;
      if (event instanceof ActionEvent)
        isActionPerformed |= (((ActionEvent)event).getModifiers()&ActionEvent.CTRL_MASK)!=0;
      if (event instanceof MouseEvent)
        isActionPerformed |= (((MouseEvent)event).getModifiers()&MouseEvent.CTRL_DOWN_MASK)!=0;
      fireSelection(WindowManager.getComponent(event), context, isActionPerformed);
    }

    public static void fireSelection(Component source, Context context, boolean isActionPerformed) {
      Component c = WindowManager.getComponent(source);
      while (c != null) {
        if (c instanceof SelectionSink) {
          ((SelectionSink) c).fireSelection(context, isActionPerformed);
          return;
        }
        c = c.getParent();
      }
      throw new IllegalArgumentException("No sink for source " + source);
    }
  }
}
