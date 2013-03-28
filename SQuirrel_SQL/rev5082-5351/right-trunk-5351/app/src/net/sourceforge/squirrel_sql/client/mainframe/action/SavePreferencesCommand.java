package net.sourceforge.squirrel_sql.client.mainframe.action;

import java.awt.Frame;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.fw.gui.IDialogUtils;
import net.sourceforge.squirrel_sql.fw.util.ICommand;


public class SavePreferencesCommand implements ICommand
{
   
   private final IApplication _app;

   
   private Frame _frame;

    
   private IDialogUtils dialogUtils = null;

   
   public SavePreferencesCommand(IApplication app, Frame frame)
   {
      super();
      if (app == null)
      {
         throw new IllegalArgumentException("app cannot be null");
      }
      if (frame == null)
      {
         throw new IllegalArgumentException("frame cannot be null");
      }
      _app = app;
      _frame = frame;
   }

   public void setDialogUtils(IDialogUtils utils) {
       dialogUtils = utils;
   }
   
   
   public void execute()
   {
       _app.saveApplicationState();
       dialogUtils.showOk(_frame, "All Preferences have been saved.");
   }
}
