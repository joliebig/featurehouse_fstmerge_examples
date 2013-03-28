package net.sourceforge.squirrel_sql.plugins.mssql.tokenizer;

import net.sourceforge.squirrel_sql.fw.preferences.IQueryTokenizerPreferenceBean;
import net.sourceforge.squirrel_sql.fw.sql.IQueryTokenizer;
import net.sourceforge.squirrel_sql.fw.sql.ITokenizerFactory;
import net.sourceforge.squirrel_sql.fw.sql.QueryTokenizer;
import net.sourceforge.squirrel_sql.fw.sql.TokenizerSessPropsInteractions;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

public class MSSQLQueryTokenizer extends QueryTokenizer implements IQueryTokenizer
{
    
    @SuppressWarnings("unused")
    private final static ILogger s_log =
        LoggerController.createLogger(MSSQLQueryTokenizer.class);
    
    
    private IQueryTokenizerPreferenceBean _prefs = null;
    
	public MSSQLQueryTokenizer(IQueryTokenizerPreferenceBean prefs)
	{
        super(prefs);
        _prefs = prefs;
	}

    public void setScriptToTokenize(String script) {
        super.setScriptToTokenize(script);
        
        _queryIterator = _queries.iterator();
    }
    
        
	protected void setFactory() {
	    _tokenizerFactory = new ITokenizerFactory() {
	        public IQueryTokenizer getTokenizer() {
	            return new MSSQLQueryTokenizer(_prefs);
            }
        };
    }

   @Override
   public TokenizerSessPropsInteractions getTokenizerSessPropsInteractions()
   {
      if(_prefs.isInstallCustomQueryTokenizer())
      {
         TokenizerSessPropsInteractions ret = new TokenizerSessPropsInteractions();
         ret.setTokenizerDefinesRemoveMultiLineComment(true);
         ret.setTokenizerDefinesStartOfLineComment(true);
         ret.setTokenizerDefinesStatementSeparator(true);

         return ret;
      }
      else
      {
         return super.getTokenizerSessPropsInteractions();
      }
   }

}
