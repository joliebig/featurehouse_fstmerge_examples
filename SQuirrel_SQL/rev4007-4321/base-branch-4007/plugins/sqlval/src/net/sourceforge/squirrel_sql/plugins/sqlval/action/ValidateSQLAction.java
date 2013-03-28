package net.sourceforge.squirrel_sql.plugins.sqlval.action;

import java.awt.event.ActionEvent;

import javax.swing.JDialog;

import net.sourceforge.squirrel_sql.fw.util.*;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.plugins.sqlval.LogonDialog;
import net.sourceforge.squirrel_sql.plugins.sqlval.SQLValidatorPlugin;
import net.sourceforge.squirrel_sql.plugins.sqlval.WebServicePreferences;
import net.sourceforge.squirrel_sql.plugins.sqlval.WebServiceSessionProperties;
import net.sourceforge.squirrel_sql.plugins.sqlval.cmd.ValidateSQLCommand;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.action.ISessionAction;
import net.sourceforge.squirrel_sql.client.session.properties.SessionProperties;

public class ValidateSQLAction extends SquirrelAction implements ISessionAction
{
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(ValidateSQLAction.class);


	
	private final static ILogger s_log =
		LoggerController.createLogger(ValidateSQLAction.class);

	
	private final WebServicePreferences _prefs;

	
	private final SQLValidatorPlugin _plugin;

	
	private ISession _session;

	
	public ValidateSQLAction(IApplication app, Resources rsrc,
									WebServicePreferences prefs,
									SQLValidatorPlugin plugin)
	{
		super(app, rsrc);
		if (prefs == null)
		{
			throw new IllegalArgumentException("WebServicePreferences == null");
		}
		_prefs = prefs;
		_plugin = plugin;
	}

	public void actionPerformed(ActionEvent evt)
	{
		if (_session != null)
		{
			final WebServiceSessionProperties sessionProps = _plugin.getWebServiceSessionProperties(_session);
			if (!sessionProps.getWebServiceSession().isOpen())
			{
				JDialog dlog = new LogonDialog(_session, _prefs, sessionProps);
				dlog.setVisible(true);
			}

			if (sessionProps.getWebServiceSession().isOpen())
			{
				validateSQL();
			}
		}
	}

	public void setSession(ISession session)
	{
		_session = session;
	}

	private void validateSQL()
	{
		final ISQLPanelAPI api = _session.getSessionInternalFrame().getSQLPanelAPI();
		final String sql = api.getSQLScriptToBeExecuted();
		if (sql != null && sql.trim().length() > 0)
		{
			final WebServiceSessionProperties wssProps = _plugin.getWebServiceSessionProperties(_session);
			final String stmtSep= _session.getQueryTokenizer().getSQLStatementSeparator();
			final String solComment = _session.getQueryTokenizer().getLineCommentBegin();
			final ValidationProps valProps = new ValidationProps(_prefs, wssProps,
													sql, stmtSep, solComment, _session);
			new Executor(_session.getApplication(), valProps, _session.getProperties()).execute();
		}
		else
		{
			
			_session.showErrorMessage(s_stringMgr.getString("sqlval.noSql"));
		}
	}

	static final class ValidationProps
	{
		final WebServicePreferences _prefs;
		final WebServiceSessionProperties _sessionProps;
		final String _sql;
		final String _stmtSep;
		final String _solComment;
        final ISession _session;

		ValidationProps(WebServicePreferences prefs,
						WebServiceSessionProperties sessionProps,
                        String sql, String stmtSep,
						String solComment, ISession session)
		{
			super();
			_prefs = prefs;
			_sessionProps = sessionProps;
			_sql = sql;
			_stmtSep= stmtSep;
			_solComment = solComment;
            _session = session;
		}
	}

	static class Executor implements ICommand
	{
		private final IApplication _app;
		private final ValidationProps _valProps;
      private SessionProperties _sessionProperties;

      Executor(IApplication app, ValidationProps valProps, SessionProperties sessionProperties)
      {
         super();
         _app = app;
         _valProps = valProps;
         _sessionProperties = sessionProperties;
      }

		public void execute()
		{
			ValidateSQLCommand cmd = new ValidateSQLCommand(_valProps._prefs,
											_valProps._sessionProps,
											_valProps._sql, _valProps._stmtSep,
											_valProps._solComment,
											_sessionProperties,
                                            _valProps._session);
			try
			{
				cmd.execute();
                _valProps._session.showMessage(cmd.getResults());
			}
			catch (Throwable th)
			{
				final String msg = "Error occured when talking to the web service";
				s_log.error(msg, th);
				_app.showErrorDialog(msg, th);
			}
		}
	}
}

