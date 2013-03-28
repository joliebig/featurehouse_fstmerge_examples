package net.sourceforge.squirrel_sql.plugins.oracle.tokenizer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.Pattern;

import net.sourceforge.squirrel_sql.fw.preferences.IQueryTokenizerPreferenceBean;
import net.sourceforge.squirrel_sql.fw.sql.IQueryTokenizer;
import net.sourceforge.squirrel_sql.fw.sql.ITokenizerFactory;
import net.sourceforge.squirrel_sql.fw.sql.QueryTokenizer;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;


public class OracleQueryTokenizer extends QueryTokenizer implements IQueryTokenizer
{
    
    private final static ILogger s_log =
        LoggerController.createLogger(OracleQueryTokenizer.class);
    
    private static final String PROCEDURE_PATTERN = 
        "^\\s*CREATE\\s+PROCEDURE.*|^\\s*CREATE\\s+OR\\s+REPLACE\\s+PROCEDURE\\s+.*";

    private static final String FUNCTION_PATTERN = 
        "^\\s*CREATE\\s+FUNCTION.*|^\\s*CREATE\\s+OR\\s+REPLACE\\s+FUNCTION\\s+.*";    

    private static final String TRIGGER_PATTERN = 
        "^\\s*CREATE\\s+TRIGGER.*|^\\s*CREATE\\s+OR\\s+REPLACE\\s+TRIGGER\\s+.*";    
    
    private static final String DECLARE_PATTERN = "^\\s*DECLARE\\s*.*";
    
    private static final String BEGIN_PATTERN = "^\\s*BEGIN\\s*.*";    
    
    
    private static final String SLASH_PATTERN = ".*\\n/\\n.*";

    
    private static final String SLASH_SPLIT_PATTERN = "\\n/\\n";
    
    private Pattern procPattern = Pattern.compile(PROCEDURE_PATTERN, Pattern.DOTALL);
    
    private Pattern funcPattern = Pattern.compile(FUNCTION_PATTERN, Pattern.DOTALL);
    
    private Pattern triggerPattern = Pattern.compile(TRIGGER_PATTERN, Pattern.DOTALL);
    
    private Pattern declPattern = Pattern.compile(DECLARE_PATTERN, Pattern.DOTALL);
    
    private Pattern beginPattern = Pattern.compile(BEGIN_PATTERN, Pattern.DOTALL);
    
    private Pattern slashPattern = Pattern.compile(SLASH_PATTERN, Pattern.DOTALL);
    
    private static final String ORACLE_SCRIPT_INCLUDE_PREFIX = "@";
    
    private IQueryTokenizerPreferenceBean _prefs = null;
    
	public OracleQueryTokenizer(IQueryTokenizerPreferenceBean prefs)
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
        joinFragments(declPattern, false);
        joinFragments(beginPattern, true);
        
        expandFileIncludes(ORACLE_SCRIPT_INCLUDE_PREFIX);
        
        removeRemainingSlashes();
        
        _queryIterator = _queries.iterator();
    }
    
        
	protected void setFactory() {
	    _tokenizerFactory = new ITokenizerFactory() {
	        public IQueryTokenizer getTokenizer() {
	            return new OracleQueryTokenizer(_prefs);
            }
        };
    }
        
    
    private void removeRemainingSlashes() {
        
        ArrayList<String> tmp = new ArrayList<String>();
        boolean foundEOLSlash = false;
        for (Iterator<String> iter = _queries.iterator(); iter.hasNext();) {
            String next = iter.next();
            if (slashPattern.matcher(next).matches()) {
                foundEOLSlash = true;
                String[] parts = next.split(SLASH_SPLIT_PATTERN);
                for (int i = 0; i < parts.length; i++) {
                    String part = parts[i];
                    if (slashPattern.matcher(part).matches()) {
                        int lastIndex = part.lastIndexOf("/");
                        tmp.add(part.substring(0, lastIndex));
                    } else {
                        if (part.endsWith("/")) {
                            part = part.substring(0, part.lastIndexOf("/"));
                        } 
                        tmp.add(part);
                    }
                }
            } else if (next.endsWith("/")) {
                foundEOLSlash = true;
                int lastIndex = next.lastIndexOf("/");
                tmp.add(next.substring(0, lastIndex));
            } else {
                tmp.add(next);
            }
        }
        if (foundEOLSlash == true) {
            _queries = tmp;
        }
        
    }
    
    
    private void breakApartNewLines() {
        ArrayList<String> tmp = new ArrayList<String>();
        String sep = _prefs.getProcedureSeparator();
        for (Iterator<String> iter = _queries.iterator(); iter.hasNext();) {
            String next = iter.next();
            if (next.startsWith(sep)) {
                tmp.add(sep);
                String[] parts = next.split(sep+"\\n+");
                for (int i = 0; i < parts.length; i++) {
                    if (!"".equals(parts[i]) && !sep.equals(parts[i])) {
                        tmp.add(parts[i]);
                    }
                }
            } else {
                tmp.add(next);
            }
        }
        _queries = tmp;
    }
    
    
    private void joinFragments(Pattern pattern, boolean skipStraySlash) {
        
        boolean inMultiSQLStatement = false;
        StringBuffer collector = null;
        ArrayList<String> tmp = new ArrayList<String>();
        String sep = _prefs.getProcedureSeparator();
        for (Iterator<String> iter = _queries.iterator(); iter.hasNext();) {
            String next = iter.next();
            if (pattern.matcher(next.toUpperCase()).matches()) {
                inMultiSQLStatement = true;
                collector = new StringBuffer(next);
                collector.append(";");
                continue;
            } 
            if (next.startsWith(sep)) {
                inMultiSQLStatement = false;
                if (collector != null) {
                    tmp.add(collector.toString());
                    collector = null;
                } else {
                    if (skipStraySlash) {
                        
                        if (s_log.isDebugEnabled()) {
                            s_log.debug(
                                "Detected stray proc separator("+sep+"). Skipping");
                        }
                    } else {
                        tmp.add(next);
                    }
                }
                continue;
            }
            if (inMultiSQLStatement) {
                collector.append(next);
                collector.append(";");
                continue;
            } 
            tmp.add(next);
        }
        _queries = tmp;
    }    
}
