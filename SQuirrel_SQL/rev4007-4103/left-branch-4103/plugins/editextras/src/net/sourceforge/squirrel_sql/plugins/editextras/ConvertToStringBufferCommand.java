package net.sourceforge.squirrel_sql.plugins.editextras;

import net.sourceforge.squirrel_sql.fw.util.BaseException;
import net.sourceforge.squirrel_sql.fw.util.ICommand;

import net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI;

class ConvertToStringBufferCommand implements ICommand
{
	private final ISQLPanelAPI _api;

	ConvertToStringBufferCommand(ISQLPanelAPI api)
	{
		super();
		_api = api;
	}

	public void execute() throws BaseException
	{
      int[] bounds = _api.getSQLEntryPanel().getBoundsOfSQLToBeExecuted();

      if(bounds[0] == bounds[1])
      {
         return;
      }

      String textToQuote = _api.getSQLEntryPanel().getSQLToBeExecuted();

		if (null == textToQuote)
		{
			return;
		}

		String quotedText = Utilities.quoteText(textToQuote, true);

      _api.getSQLEntryPanel().setSelectionStart(bounds[0]);
      _api.getSQLEntryPanel().setSelectionEnd(bounds[1]);
      _api.getSQLEntryPanel().replaceSelection(quotedText);
	}
}
