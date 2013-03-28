package net.sourceforge.squirrel_sql.fw.sql;



public interface IQueryTokenizer {
    
    
    boolean hasQuery();
    
    
    String nextQuery();
    
    
    void setScriptToTokenize(String script);
    
    
    int getQueryCount();
    
    
    String getSQLStatementSeparator();
    
    
    String getLineCommentBegin();
    
    
    boolean isRemoveMultiLineComment();
}
