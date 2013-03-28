package net.sourceforge.squirrel_sql.plugins.sqlval.cmd;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.properties.SessionProperties;
import net.sourceforge.squirrel_sql.fw.sql.IQueryTokenizer;
import net.sourceforge.squirrel_sql.fw.util.BaseException;
import net.sourceforge.squirrel_sql.fw.util.ICommand;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.plugins.sqlval.WebServicePreferences;
import net.sourceforge.squirrel_sql.plugins.sqlval.WebServiceSession;
import net.sourceforge.squirrel_sql.plugins.sqlval.WebServiceSessionProperties;
import net.sourceforge.squirrel_sql.plugins.sqlval.WebServiceValidator;

import com.mimer.ws.validateSQL.ValidatorResult;


public class ValidateSQLCommand implements ICommand
{
   private final WebServicePreferences _prefs;
   private final WebServiceSessionProperties _wsSessionProps;
   private final String _sql;
   private final String _stmtSep;
   private final String _solComment;
   private SessionProperties _sessionProperties;
   private String _results;
   private final ISession _session;
   
   
   private final static ILogger s_log =
      LoggerController.createLogger(ValidateSQLCommand.class);

   public ValidateSQLCommand(WebServicePreferences prefs,
                             WebServiceSessionProperties wsSessionProps, String sql,
                             String stmtSep, String solComment, 
                             SessionProperties sessionProperties,
                             ISession session)
   {
      super();
      _prefs = prefs;
      _wsSessionProps = wsSessionProps;
      _sql = sql;
      _stmtSep= stmtSep;
      _solComment = solComment;
      _sessionProperties = sessionProperties;
      _session = session;
   }

   public void openSession(WebServiceSession info)
   {
      if (info == null)
      {
         throw new IllegalArgumentException("ValidationInfo == null");
      }
   }

   public String getResults()
   {
      return _results;
   }

   public void execute() throws BaseException
   {
      try
      {
         
         WebServiceSession wss = new WebServiceSession(_prefs,_wsSessionProps);
         wss.open();

         final WebServiceValidator val = new WebServiceValidator(wss, _wsSessionProps);
         final IQueryTokenizer qt = _session.getQueryTokenizer();

         qt.setScriptToTokenize(_sql);
         final StringBuffer results = new StringBuffer(1024);
         while (qt.hasQuery())
         {
            
            
            ValidatorResult rc = val.validate(qt.nextQuery());
            results.append(rc.getData());
         }
         _results = results.toString().trim();

      }
      catch (Throwable th)
      {
         throw new BaseException(th);
      }
   }
}

