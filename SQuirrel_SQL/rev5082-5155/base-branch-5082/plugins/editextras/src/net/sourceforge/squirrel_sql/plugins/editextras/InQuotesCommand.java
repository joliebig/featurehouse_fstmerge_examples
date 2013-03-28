package net.sourceforge.squirrel_sql.plugins.editextras;

import net.sourceforge.squirrel_sql.fw.util.BaseException;
import net.sourceforge.squirrel_sql.fw.util.ICommand;

import net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI;
import net.sourceforge.squirrel_sql.client.session.ISQLEntryPanel;


class InQuotesCommand implements ICommand
{
	private final ISQLPanelAPI _api;

	InQuotesCommand(ISQLPanelAPI api)
	{
		super();
		_api = api;
	}

	public void execute() throws BaseException
	{
      ISQLEntryPanel entryPanel = _api.getSQLEntryPanel();

      quoteSQL(entryPanel, false);
	}

   public static void quoteSQL(ISQLEntryPanel entryPanel, boolean sbAppend)
   {
      int[] bounds = entryPanel.getBoundsOfSQLToBeExecuted();

      if(bounds[0] == bounds[1])
      {
         return;
      }

      String textToQuote = entryPanel.getSQLToBeExecuted();

      if (null == textToQuote)
      {
         return;
      }

      String quotedText = Utilities.quoteText(textToQuote, sbAppend);

      entryPanel.setSelectionStart(bounds[0]);
      entryPanel.setSelectionEnd(bounds[1]);
      entryPanel.replaceSelection(quotedText);
   }
}
