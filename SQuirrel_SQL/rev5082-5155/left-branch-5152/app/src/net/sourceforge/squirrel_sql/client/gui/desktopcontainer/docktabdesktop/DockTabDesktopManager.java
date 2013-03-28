package net.sourceforge.squirrel_sql.client.gui.desktopcontainer.docktabdesktop;

import net.sourceforge.squirrel_sql.client.gui.mainframe.SquirrelDesktopManager;

public class DockTabDesktopManager implements TabHandleListener
{
   private SquirrelDesktopManager _squirrelDesktopManager;

   public void tabClosing(TabHandleEvent tabHandleEvent)
   {
      
   }

   public void tabClosed(TabHandleEvent tabHandleEvent)
   {
      
   }

   public void tabAdded(TabHandleEvent tabHandleEvent)
   {
      
   }

   public void tabSelected(TabHandleEvent tabHandleEvent)
   {
      _squirrelDesktopManager.activateWidget(tabHandleEvent.getTabHandle().getWidget());
   }

   public void tabDeselected(TabHandleEvent tabHandleEvent)
   {
      _squirrelDesktopManager.deactivateWidget(tabHandleEvent.getTabHandle().getWidget());
   }

   public void setSquirrelDesktopManager(SquirrelDesktopManager squirrelDesktopManager)
   {
      _squirrelDesktopManager = squirrelDesktopManager;
   }
}
