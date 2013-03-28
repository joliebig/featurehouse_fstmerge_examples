package net.sourceforge.squirrel_sql.client.update.gui;




public class UpdateSummaryTableActionItem
{
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
