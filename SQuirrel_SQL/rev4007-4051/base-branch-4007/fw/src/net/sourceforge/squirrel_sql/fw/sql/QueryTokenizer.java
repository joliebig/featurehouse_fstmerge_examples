package net.sourceforge.squirrel_sql.fw.sql;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.squirrel_sql.fw.preferences.IQueryTokenizerPreferenceBean;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

public class QueryTokenizer implements IQueryTokenizer
{
	protected ArrayList<String> _queries = new ArrayList<String>();
    
	protected Iterator<String> _queryIterator;

    protected String _querySep = null;
    
    protected String _lineCommentBegin = null;
    
    protected boolean _removeMultiLineComment = true;

    protected ITokenizerFactory _tokenizerFactory = null;
    
    
    private final static ILogger s_log =
        LoggerController.createLogger(QueryTokenizer.class); 
    
    public QueryTokenizer() {}
    
	public QueryTokenizer(String querySep, 
                          String lineCommentBegin, 
                          boolean removeMultiLineComment)
	{
        _querySep = querySep;
        _lineCommentBegin = lineCommentBegin;
        _removeMultiLineComment = removeMultiLineComment;
        setFactory();
	}

    public QueryTokenizer(IQueryTokenizerPreferenceBean prefs) {
        this(prefs.getStatementSeparator(), 
             prefs.getLineComment(),
             prefs.isRemoveMultiLineComments()); 
    }
    
    
    protected void setFactory() {
        _tokenizerFactory = new ITokenizerFactory() {
            public IQueryTokenizer getTokenizer() {
                return new QueryTokenizer();
            }
        };
    }
    

	private int getLenOfQuerySepIfAtLastCharOfQuerySep(String sql, int i, String querySep, boolean inLiteral)
	{
		if(inLiteral)
		{
			return -1;
		}

		char c = sql.charAt(i);

		if(1 == querySep.length() && c == querySep.charAt(0))
		{
			return 1;
		}
		else
		{
			int fromIndex = i - querySep.length();
			if(0 > fromIndex)
			{
				return -1;
			}

			int querySepIndex = sql.indexOf(querySep, fromIndex);

			if(0 > querySepIndex)
			{
				return -1;
			}

			if(Character.isWhitespace(c))
			{
				if(querySepIndex + querySep.length() == i)
				{
					if(0 == querySepIndex)
					{
						return querySep.length() + 1;
					}
					else if(Character.isWhitespace(sql.charAt(querySepIndex - 1)))
					{
						return querySep.length() + 2;
					}
				}
			}
			else if(sql.length() -1 == i)
			{
				if(querySepIndex + querySep.length() - 1 == i)
				{
					if(0 == querySepIndex)
					{
						return querySep.length();
					}
					else if(Character.isWhitespace(sql.charAt(querySepIndex - 1)))
					{
						return querySep.length() + 1;
					}
				}
			}

			return -1;
		}
	}
    
	public boolean hasQuery()
	{
		return _queryIterator.hasNext();
	}

	public String nextQuery()
	{
		return _queryIterator.next();
	}

    public void setScriptToTokenize(String script) {
        _queries.clear();
        
        String MULTI_LINE_COMMENT_END = "*/";
        String MULTI_LINE_COMMENT_BEGIN = "/*";

        script = script.replace('\r', ' ');

        StringBuffer curQuery = new StringBuffer();

        boolean isInLiteral = false;
        boolean isInMultiLineComment = false;
        boolean isInLineComment = false;
        int literalSepCount = 0;


        for (int i = 0; i < script.length(); ++i)
        {
            char c = script.charAt(i);

            if(false == isInLiteral)
            {
                
                

                
                if(isInLineComment && script.startsWith("\n", i - "\n".length()))
                {
                    isInLineComment = false;
                }

                
                if(isInMultiLineComment && script.startsWith(MULTI_LINE_COMMENT_END, i - MULTI_LINE_COMMENT_END.length()))
                {
                    isInMultiLineComment = false;
                }


                if(false == isInLineComment && false == isInMultiLineComment)
                {
                    
                    isInMultiLineComment = script.startsWith(MULTI_LINE_COMMENT_BEGIN, i);
                    isInLineComment = script.startsWith(_lineCommentBegin, i);

                    if(isInMultiLineComment && _removeMultiLineComment)
                    {
                        
                        i+=MULTI_LINE_COMMENT_BEGIN.length()+1;
                    }
                }

                if((isInMultiLineComment && _removeMultiLineComment) || isInLineComment)
                {
                    
                    continue;
                }
                
                
            }

            curQuery.append(c);

            if ('\'' == c)
            {
                if(false == isInLiteral)
                {
                    isInLiteral = true;
                }
                else
                {
                    ++literalSepCount;
                }
            }
            else
            {
                if(0 != literalSepCount % 2)
                {
                    isInLiteral = false;
                }
                literalSepCount = 0;
            }


            int querySepLen = 
                getLenOfQuerySepIfAtLastCharOfQuerySep(script, i, _querySep, isInLiteral);

            if(-1 < querySepLen)
            {
                int newLength = curQuery.length() - querySepLen;
                if(-1 < newLength && curQuery.length() > newLength)
                {
                    curQuery.setLength(newLength);

                    String newQuery = curQuery.toString().trim();
                    if(0 < newQuery.length())
                    {
                        _queries.add(curQuery.toString().trim());
                    }
                }
                curQuery.setLength(0);
            }
        }

        String lastQuery = curQuery.toString().trim();
        if(0 < lastQuery.length())
        {
            _queries.add(lastQuery.toString().trim());
        }

        _queryIterator = _queries.iterator();
    }
    
    
    public int getQueryCount() {
        if (_queries == null) {
            return 0;
        }
        return _queries.size();
    }
    
    
    
    public static void main(String[] args)
    {
        
        
        
        String sql = "@c:\\tools\\sql\\file.sql";
        
        
        QueryTokenizer qt = new QueryTokenizer("GO", "--", true);

        qt.setScriptToTokenize(sql);
        
        while(qt.hasQuery())
        {
            System.out.println(">" + qt.nextQuery() + "<");
        }
    }

    
    public String getQuerySep() {
        return _querySep;
    }

    
    public void setQuerySep(String sep) {
        _querySep = sep;
    }

    
    public String getLineCommentBegin() {
        return _lineCommentBegin;
    }

    
    public void setLineCommentBegin(String commentBegin) {
        _lineCommentBegin = commentBegin;
    }

    
    public boolean isRemoveMultiLineComment() {
        return _removeMultiLineComment;
    }

    
    public void setRemoveMultiLineComment(boolean multiLineComment) {
        _removeMultiLineComment = multiLineComment;
    }
    
    
    protected void expandFileIncludes(String scriptIncludePrefix) {
        if (scriptIncludePrefix == null) {
            s_log.error("scriptIncludePrefix cannot be null ");
            return;
        }
        ArrayList<String> tmp = new ArrayList<String>();
        for (Iterator<String> iter = _queries.iterator(); iter.hasNext();) {
            String sql = iter.next();
            if (sql.startsWith(scriptIncludePrefix)) {
                try {
                    String filename = 
                        sql.substring(scriptIncludePrefix.length());
                    List<String> fileSQL = getStatementsFromIncludeFile(filename);
                    tmp.addAll(fileSQL);
                } catch (Exception e) {
                    s_log.error(
                       "Unexpected error while attempting to include file " +
                       "from "+sql, e);
                }
                
            } else {
                tmp.add(sql);
            }
        }
        _queries = tmp;
    }
    
    protected List<String> getStatementsFromIncludeFile(String filename) 
        throws Exception 
    {
        if (filename.startsWith("'")) {
            filename = filename.substring(1);
        }
        if (filename.endsWith("'")) {
            filename = StringUtilities.chop(filename);
        }
        ArrayList<String> result = new ArrayList<String>();
        if (s_log.isDebugEnabled()) {
            s_log.debug("Attemping to open file '"+filename+"'");
        }
        File f = new File(filename);
        
            StringBuffer fileLines = new StringBuffer();
            try {
                BufferedReader reader = new BufferedReader(new FileReader(f));
                String next = reader.readLine();
                while (next != null) {
                    fileLines.append(next);
                    fileLines.append("\n");
                    next = reader.readLine();
                }
            } catch (Exception e) {
                s_log.error(
                    "Unexpected exception while reading lines from file " +
                    "("+filename+")", e);
            }
            if (fileLines.toString().length() > 0) {
                IQueryTokenizer qt = null;
                if (_tokenizerFactory != null) {
                    qt = _tokenizerFactory.getTokenizer();
                } else {
                    qt = new QueryTokenizer(_querySep, 
                                            _lineCommentBegin, 
                                            _removeMultiLineComment);
                }
                qt.setScriptToTokenize(fileLines.toString());
                while (qt.hasQuery()) {
                    String sql = qt.nextQuery();
                    result.add(sql);
                }
            }
            
        return result;
    }

    
    public String getSQLStatementSeparator() {
        return _querySep;
    }    
    
}
