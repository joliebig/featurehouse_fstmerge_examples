package net.sourceforge.squirrel_sql.plugins.editextras;

import net.sourceforge.squirrel_sql.fw.util.BaseException;
import net.sourceforge.squirrel_sql.fw.util.ICommand;

import net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI;
import net.sourceforge.squirrel_sql.client.session.ISQLEntryPanel;


class RemoveQuotesCommand implements ICommand
{
	private final ISQLPanelAPI _api;

	RemoveQuotesCommand(ISQLPanelAPI api)
	{
		super();
		_api = api;
	}

	public void execute() throws BaseException
	{
      ISQLEntryPanel entryPanel = _api.getSQLEntryPanel();

      unquoteSQL(entryPanel);
	}

   static void unquoteSQL(ISQLEntryPanel entryPanel)
   {
      int[] bounds = entryPanel.getBoundsOfSQLToBeExecuted();

      if(bounds[0] == bounds[1])
      {
         return;
      }

      String textToUnquote = entryPanel.getSQLToBeExecuted();

      if (null == textToUnquote)
      {
         return;
      }

      String unquotedText = Utilities.unquoteText(textToUnquote);

      entryPanel.setSelectionStart(bounds[0]);
      entryPanel.setSelectionEnd(bounds[1]);
      entryPanel.replaceSelection(unquotedText);
   }

}
