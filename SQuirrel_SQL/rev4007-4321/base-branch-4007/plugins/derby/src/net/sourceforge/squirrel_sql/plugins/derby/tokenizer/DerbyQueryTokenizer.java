package net.sourceforge.squirrel_sql.plugins.derby.tokenizer;

import net.sourceforge.squirrel_sql.fw.sql.IQueryTokenizer;
import net.sourceforge.squirrel_sql.fw.sql.ITokenizerFactory;
import net.sourceforge.squirrel_sql.fw.sql.QueryTokenizer;


public class DerbyQueryTokenizer extends QueryTokenizer implements IQueryTokenizer
{
    private static final String DERBY_SCRIPT_INCLUDE_PREFIX = "run ";
    
	public DerbyQueryTokenizer(String sep, 
                               String linecomment, 
                               boolean removeMultiLineComment) 
	{
        super(sep, linecomment, removeMultiLineComment);
	}

    public void setScriptToTokenize(String script) {
        super.setScriptToTokenize(script);
        
        expandFileIncludes(DERBY_SCRIPT_INCLUDE_PREFIX);
        
        _queryIterator = _queries.iterator();
    }
    
        
	protected void setFactory() {
	    _tokenizerFactory = new ITokenizerFactory() {
	        public IQueryTokenizer getTokenizer() {
	            return new DerbyQueryTokenizer(
                                DerbyQueryTokenizer.this._querySep,
                                DerbyQueryTokenizer.this._lineCommentBegin,
                                DerbyQueryTokenizer.this._removeMultiLineComment);
            }
        };
    }
            
}
