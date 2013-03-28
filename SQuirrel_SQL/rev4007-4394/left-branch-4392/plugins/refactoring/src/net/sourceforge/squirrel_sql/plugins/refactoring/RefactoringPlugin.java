package net.sourceforge.squirrel_sql.plugins.refactoring;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.ActionCollection;
import net.sourceforge.squirrel_sql.client.gui.session.ObjectTreeInternalFrame;
import net.sourceforge.squirrel_sql.client.gui.session.SQLInternalFrame;
import net.sourceforge.squirrel_sql.client.plugin.DefaultSessionPlugin;
import net.sourceforge.squirrel_sql.client.plugin.PluginException;
import net.sourceforge.squirrel_sql.client.plugin.PluginResources;
import net.sourceforge.squirrel_sql.client.plugin.PluginSessionCallback;
import net.sourceforge.squirrel_sql.client.session.IObjectTreeAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;
import net.sourceforge.squirrel_sql.plugins.refactoring.actions.AddColumnAction;
import net.sourceforge.squirrel_sql.plugins.refactoring.actions.AddPrimaryKeyAction;
import net.sourceforge.squirrel_sql.plugins.refactoring.actions.DropPrimaryKeyAction;
import net.sourceforge.squirrel_sql.plugins.refactoring.actions.DropSelectedTablesAction;
import net.sourceforge.squirrel_sql.plugins.refactoring.actions.ModifyColumnAction;
import net.sourceforge.squirrel_sql.plugins.refactoring.actions.RemoveColumnAction;


public class RefactoringPlugin extends DefaultSessionPlugin {
   private interface IMenuResourceKeys {
      String REFACTORING = "refactoring";
   }

   private PluginResources _resources;

   
   public String getInternalName() {
      return "refactoring";
   }

   
   public String getDescriptiveName() {
      return "Refactoring Plugin";
   }

   
   public String getVersion() {
      return "0.12";
   }

   
   public String getAuthor() {
      return "Rob Manning";
   }



   
   public String getChangeLogFileName()
   {
      return "changes.txt";
   }

   
   public String getHelpFileName()
   {
      return "readme.html";
   }

   
   public String getLicenceFileName()
   {
      return "licence.txt";
   }

   
   public String getContributors()
   {
      return "";
   }

   
   
   
   
   public synchronized void initialize() throws PluginException
   {
      super.initialize();
      IApplication app = getApplication();

      _resources =
         new SQLPluginResources(
            "net.sourceforge.squirrel_sql.plugins.refactoring.refactoring",
            this);

      ActionCollection coll = app.getActionCollection();
      coll.add(new AddColumnAction(app, _resources));
      coll.add(new ModifyColumnAction(app, _resources));
      coll.add(new RemoveColumnAction(app, _resources));
      coll.add(new AddPrimaryKeyAction(app, _resources));
      coll.add(new DropPrimaryKeyAction(app, _resources));
      coll.add(new DropSelectedTablesAction(app, _resources));
   }

   public boolean allowsSessionStartedInBackground()
   {
      return true;
   }

   
   public PluginSessionCallback sessionStarted(final ISession session)
   {
       
        GUIUtils.processOnSwingEventThread(new Runnable() {
           public void run() {
               addActionsToPopup(session);
           }
        });
        
       PluginSessionCallback ret = new PluginSessionCallback()
       {
           public void sqlInternalFrameOpened(SQLInternalFrame sqlInternalFrame, ISession sess)
           {
               
               
               

               
               
           }

           public void objectTreeInternalFrameOpened(ObjectTreeInternalFrame objectTreeInternalFrame, ISession sess)
           {
               
               
               
               
               
               
           }
       };

       return ret;
   }

    private void addActionsToPopup(ISession session) {
        ActionCollection coll = getApplication().getActionCollection();

        IObjectTreeAPI api = session.getObjectTreeAPIOfActiveSessionWindow();

        
        JMenu tableObjectMenu = _resources.createMenu(IMenuResourceKeys.REFACTORING);
        JMenu columnMenu = new JMenu("Column"); 
        JMenuItem addColItem = new JMenuItem("Add Column");
        addColItem.setAction(coll.get(AddColumnAction.class));
        JMenuItem removeColItem = new JMenuItem("Drop Column");
        removeColItem.setAction(coll.get(RemoveColumnAction.class));
        JMenuItem modifyMenuItem = new JMenuItem("Modify Column");
        modifyMenuItem.setAction(coll.get(ModifyColumnAction.class));
        
        columnMenu.add(addColItem);
        columnMenu.add(modifyMenuItem);
        columnMenu.add(removeColItem);
        
        JMenuItem dropTableItem = new JMenuItem("Drop Table");
        dropTableItem.setAction(coll.get(DropSelectedTablesAction.class));
        
        
        
        JMenuItem addPrimaryKeyItem = new JMenuItem("Add Primary Key");
        addPrimaryKeyItem.setAction(coll.get(AddPrimaryKeyAction.class));
        JMenuItem dropPrimaryKeyItem = new JMenuItem("Drop Primary Key");
        dropPrimaryKeyItem.setAction(coll.get(DropPrimaryKeyAction.class));
        
        
        
        
        
        
        
        JMenu tableMenu = new JMenu("Table");
        
        tableMenu.add(dropTableItem);
        tableMenu.add(addPrimaryKeyItem);
        tableMenu.add(dropPrimaryKeyItem);
        
        
        
        
        
        
        
        
        tableObjectMenu.add(tableMenu);
        tableObjectMenu.add(columnMenu);
        
        api.addToPopup(DatabaseObjectType.TABLE, tableObjectMenu);
    }

}
