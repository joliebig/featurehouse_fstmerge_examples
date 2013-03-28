package genj.view;

import genj.gedcom.Context;
import genj.util.swing.DialogHelper;
import genj.util.swing.DialogHelper.ComponentVisitor;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;

import javax.swing.RootPaneContainer;


public interface SelectionSink {

  
  public void fireSelection(Context context, boolean isActionPerformed);

  public class Dispatcher {
    public static void fireSelection(AWTEvent event, Context context) {
      boolean isActionPerformed = false;
      if (event instanceof ActionEvent)
        isActionPerformed |= (((ActionEvent)event).getModifiers()&ActionEvent.CTRL_MASK)!=0;
      if (event instanceof MouseEvent)
        isActionPerformed |= (((MouseEvent)event).getModifiers()&MouseEvent.CTRL_DOWN_MASK)!=0;
      fireSelection((Component)event.getSource(), context, isActionPerformed);
    }

    public static void fireSelection(Component source, Context context, boolean isActionPerformed) {

      SelectionSink sink = (SelectionSink)DialogHelper.visitOwners(source, new ComponentVisitor() {
        public Component visit(Component parent, Component child) {
          if (parent instanceof RootPaneContainer) {
            Container contentPane = ((RootPaneContainer)parent).getContentPane();
            if (contentPane.getComponentCount()>0 && contentPane.getComponent(0) instanceof SelectionSink)
              return contentPane.getComponent(0);
          }
          return parent instanceof SelectionSink ? parent : null;
        }
      });
      
      if (sink==null)
        throw new IllegalArgumentException("Can't find sink for "+source);

      sink.fireSelection(context, isActionPerformed);
    }
  }
}
