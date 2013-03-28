package net.sourceforge.squirrel_sql.client.mainframe.action;

import java.awt.event.ActionEvent;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.gui.db.IToogleableAliasesList;


public class DeleteAliasAction extends AliasAction
{
   private static final long serialVersionUID = 1L;

   private IToogleableAliasesList _aliasesList;

   public DeleteAliasAction(IApplication app, IToogleableAliasesList al)
   {
      super(app);
      _aliasesList = al;
   }

   public void actionPerformed(ActionEvent e)
   {
      _aliasesList.deleteSelected();
   }
}
