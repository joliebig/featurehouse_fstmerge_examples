package net.sourceforge.squirrel_sql.plugins.editextras;

import net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.codereformat.CodeReformator;
import net.sourceforge.squirrel_sql.fw.codereformat.CommentSpec;
import net.sourceforge.squirrel_sql.fw.util.BaseException;
import net.sourceforge.squirrel_sql.fw.util.ICommand;

class FormatSQLCommand implements ICommand
{
	private final ISession _session;
	private final EditExtrasPlugin _plugin;

	FormatSQLCommand(ISession session, EditExtrasPlugin plugin)
	{
		super();
		_session = session;
		_plugin = plugin;
	}

	public void execute() throws BaseException
	{
		ISQLPanelAPI api = FrameWorkAcessor.getSQLPanelAPI(_session, _plugin);
      
      int[] bounds = api.getSQLEntryPanel().getBoundsOfSQLToBeExecuted();

      if(bounds[0] == bounds[1])
      {
         return;
      }

      String textToReformat = api.getSQLEntryPanel().getSQLToBeExecuted();

		if (null == textToReformat)
		{
			return;
		}

		CommentSpec[] commentSpecs =
		  new CommentSpec[]
		  {
			  new CommentSpec("/*", "*/"),
			  new CommentSpec("--", "\n")
		  };

		String statementSep = _session.getQueryTokenizer().getSQLStatementSeparator();
		
		CodeReformator cr = new CodeReformator(statementSep, commentSpecs);

		String reformatedText = cr.reformat(textToReformat);

      api.getSQLEntryPanel().setSelectionStart(bounds[0]);
      api.getSQLEntryPanel().setSelectionEnd(bounds[1]);
      api.getSQLEntryPanel().replaceSelection(reformatedText);

	}
}
