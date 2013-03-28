
package net.sourceforge.squirrel_sql.plugins.sqlbookmark;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.plugin.PluginResources;
import net.sourceforge.squirrel_sql.client.session.ISQLEntryPanel;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.completion.CompletionInfo;
import net.sourceforge.squirrel_sql.fw.completion.Completor;
import net.sourceforge.squirrel_sql.fw.completion.CompletorListener;

import javax.swing.text.JTextComponent;
import java.awt.event.ActionEvent;
import java.awt.*;

public class CompleteBookmarkAction extends SquirrelAction
{
   private ISQLEntryPanel _sqlEntryPanel;
   private Completor _cc;
   private SQLBookmarkPlugin _plugin;


   public CompleteBookmarkAction(IApplication app, PluginResources rsrc, ISQLEntryPanel sqlEntryPanel, SQLBookmarkPlugin plugin)
   {
      super(app, rsrc);
      _sqlEntryPanel = sqlEntryPanel;
      _plugin = plugin;

      _cc = new Completor(_sqlEntryPanel.getTextComponent(), plugin.getBookmarkManager(), new Color(204,255,255), true);

      _cc.addCodeCompletorListener
      (
         new CompletorListener()
         {
            public void completionSelected(CompletionInfo completion, int replaceBegin, int keyCode, int modifiers)
            {performCompletionSelected(completion);}
         }
      );
   }


   public void actionPerformed(ActionEvent evt)
   {
      _cc.show();
   }



   private void performCompletionSelected(CompletionInfo completion)
   {
      Bookmark bm = ((BookmarkCompletionInfo)completion).getBookmark();
      new RunBookmarkCommand(getApplication().getMainFrame(), _sqlEntryPanel.getSession(), bm, _plugin, _sqlEntryPanel).execute();
	}
}