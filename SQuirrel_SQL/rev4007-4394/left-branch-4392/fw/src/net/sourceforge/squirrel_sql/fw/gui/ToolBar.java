package net.sourceforge.squirrel_sql.fw.gui;

import net.sourceforge.squirrel_sql.fw.gui.action.BaseAction;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import javax.swing.*;


public class ToolBar extends JToolBar
{
   
   private static ILogger s_log = LoggerController.createLogger(ToolBar.class);

   public ToolBar()
   {
      super();
   }

   public ToolBar(int orientation)
   {
      super(orientation);
   }

   public JButton add(Action action)
   {
      JButton btn = super.add(action);
      initialiseButton(action, btn);
      return btn;
   }

   public JToggleButton addToggleAction(IToggleAction action)
   {
      JToggleButton tglBtn = new JToggleButton();
      tglBtn.setAction(action);
      super.add(tglBtn);
      action.getToggleComponentHolder().addToggleableComponent(tglBtn);
      initialiseButton(action, tglBtn);
      return tglBtn;
   }


   public AbstractButton add(Action action, AbstractButton btn)
   {
      btn.setAction(action);
      super.add(btn);
      initialiseButton(action, btn);
      return btn;
   }



   public void setUseRolloverButtons(boolean value)
   {
      putClientProperty("JToolBar.isRollover", value ? Boolean.TRUE : Boolean.FALSE);
   }

   protected void initialiseButton(Action action, AbstractButton btn)
   {
      if (btn != null)
      {
         btn.setRequestFocusEnabled(false);
         btn.setText("");
         String tt = null;
         if (action != null)
         {
            tt = (String) action.getValue(Action.SHORT_DESCRIPTION);
         }
         btn.setToolTipText(tt != null ? tt : "");
         if (action != null)
         {
            Icon icon = getIconFromAction(action, BaseAction.IBaseActionPropertyNames.ROLLOVER_ICON);
            if (icon != null)
            {
               btn.setRolloverIcon(icon);
               btn.setRolloverSelectedIcon(icon);
            }
            icon = getIconFromAction(action, BaseAction.IBaseActionPropertyNames.DISABLED_ICON);
            if (icon != null)
            {
               btn.setDisabledIcon(icon);
            }
         }
      }
   }

   
   protected Icon getIconFromAction(Action action, String key)
   {
      
      Object obj = action.getValue(key);
      if (obj != null)
      {
         if (obj instanceof Icon)
         {
            return (Icon)obj;
         }
         StringBuffer msg = new StringBuffer();
         msg.append("Non icon object of type ").append(obj.getClass().getName())
            .append(" was stored in an Action of type ")
            .append(action.getClass().getName())
            .append(" using the key ").append(key).append(".");
         s_log.error(msg.toString());
      }
      return null;
   }
}
