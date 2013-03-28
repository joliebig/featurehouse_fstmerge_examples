package net.sourceforge.squirrel_sql.client.update.gui;



import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

public class UpdateSummaryTableActionItem
{
   private static final StringManager s_stringMgr =
      StringManagerFactory.getStringManager(UpdateSummaryTableActionItem.class);

   private ArtifactAction _action;

   private UpdateSummaryTableActionItem(ArtifactAction action)
   {
      this._action = action;
   }

   public String toString()
   {
      return _action.name();
   }

   public void setValue(ArtifactAction action) {
      this._action = action;
   }


}
