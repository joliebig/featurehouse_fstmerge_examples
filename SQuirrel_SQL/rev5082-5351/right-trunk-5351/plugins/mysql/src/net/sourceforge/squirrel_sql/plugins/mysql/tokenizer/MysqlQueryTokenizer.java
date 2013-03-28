package net.sourceforge.squirrel_sql.plugins.mysql.tokenizer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.Pattern;

import net.sourceforge.squirrel_sql.fw.preferences.IQueryTokenizerPreferenceBean;
import net.sourceforge.squirrel_sql.fw.sql.IQueryTokenizer;
import net.sourceforge.squirrel_sql.fw.sql.ITokenizerFactory;
import net.sourceforge.squirrel_sql.fw.sql.QueryTokenizer;
import net.sourceforge.squirrel_sql.fw.sql.TokenizerSessPropsInteractions;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;


public class MysqlQueryTokenizer extends QueryTokenizer implements IQueryTokenizer
{
    
    private final static ILogger s_log =
        LoggerController.createLogger(MysqlQueryTokenizer.class);
    
    private static final String PROCEDURE_PATTERN = 
        "^\\s*CREATE\\s+PROCEDURE.*";

    private static final String FUNCTION_PATTERN = 
        "^\\s*CREATE\\s+FUNCTION.*";    

    private static final String TRIGGER_PATTERN = 
        "^\\s*CREATE\\s+TRIGGER.*";    
    
    private Pattern procPattern = Pattern.compile(PROCEDURE_PATTERN, Pattern.DOTALL);
    
    private Pattern funcPattern = Pattern.compile(FUNCTION_PATTERN, Pattern.DOTALL);
    
    private Pattern triggerPattern = Pattern.compile(TRIGGER_PATTERN, Pattern.DOTALL);
    
    
    private IQueryTokenizerPreferenceBean _prefs = null;
    
	public MysqlQueryTokenizer(IQueryTokenizerPreferenceBean prefs)
	{
        super(prefs.getStatementSeparator(),
              prefs.getLineComment(), 
              prefs.isRemoveMultiLineComments());
        _prefs = prefs;
	}

    public void setScriptToTokenize(String script) {
        super.setScriptToTokenize(script);
        
        
        
        
        
        
        
        
        breakApartNewLines();
        
        
        
        
        
        
        
        joinFragments(procPattern, false);
        joinFragments(funcPattern, false);
        joinFragments(triggerPattern, false);
        
        _queryIterator = _queries.iterator();
    }
    
        
	protected void setFactory() {
	    _tokenizerFactory = new ITokenizerFactory() {
	        public IQueryTokenizer getTokenizer() {
	            return new MysqlQueryTokenizer(_prefs);
            }
        };
    }
        
    
    
    private void breakApartNewLines() {
        ArrayList<String> tmp = new ArrayList<String>();
        String procSep = _prefs.getProcedureSeparator();
        for (Iterator<String> iter = _queries.iterator(); iter.hasNext();) {
            String next = iter.next();
            if (next.startsWith(procSep)) {
                tmp.add(procSep);
                String[] parts = next.split(procSep+"\\n+");
                for (int i = 0; i < parts.length; i++) {
                    if (!"".equals(parts[i]) && !procSep.equals(parts[i])) {
                        tmp.add(parts[i]);
                    }
                }
            } else if (next.endsWith(procSep)) { 
                String chopped = StringUtilities.chop(next);
                tmp.add(chopped);
                tmp.add(procSep);
            } else if (next.indexOf(procSep) != -1 ) {
                String[] parts = next.split("\\"+procSep);
                for (int i = 0; i < parts.length; i++) {
                    tmp.add(parts[i]);
                    if (i < parts.length - 1) {
                        tmp.add(procSep);
                    }
                }
            } else {
                tmp.add(next);
            }
        }
        _queries = tmp;
    }
    
    
    private void joinFragments(Pattern pattern, boolean skipStraySep) {
        
        boolean inMultiSQLStatement = false;
        StringBuilder collector = null;
        ArrayList<String> tmp = new ArrayList<String>();
        String procSep = _prefs.getProcedureSeparator();
        String stmtSep = _prefs.getStatementSeparator();
        for (Iterator<String> iter = _queries.iterator(); iter.hasNext();) {
            String next = iter.next();
            
            
            
            if (next.startsWith("DELIMITER")) {
                String[] parts = StringUtilities.split(next, ' ', true);
                if (parts.length == 2) {
                    procSep = parts[1];
                } else {
                    s_log.error(
                        "Found DELIMITER keyword, followed by "+
                        (parts.length-1)+" elements; expected only one: "+next+
                        "\nSkipping DELIMITER directive.");
                }
            }
            
            if (pattern.matcher(next.toUpperCase()).matches()) {
                inMultiSQLStatement = true;
                collector = new StringBuilder(next);
                collector.append(stmtSep);
                continue;
            } 
            if (next.startsWith(procSep)) {
                inMultiSQLStatement = false;
                if (collector != null) {
                    tmp.add(collector.toString());
                    collector = null;
                } else {
                    if (skipStraySep) {
                        
                        if (s_log.isDebugEnabled()) {
                            s_log.debug(
                                "Detected stray proc separator("+procSep+"). Skipping");
                        }
                    } else {
                        tmp.add(next);
                    }
                }
                continue;
            }
            if (inMultiSQLStatement) {
                collector.append(next);
                collector.append(stmtSep);
                continue;
            } 
            tmp.add(next);
        }
        
        
        if (collector != null && inMultiSQLStatement) {
            tmp.add(collector.toString());
        }
        _queries = tmp;
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
