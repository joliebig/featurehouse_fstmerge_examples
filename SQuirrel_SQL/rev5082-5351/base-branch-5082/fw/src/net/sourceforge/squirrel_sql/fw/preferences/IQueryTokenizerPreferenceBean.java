
package net.sourceforge.squirrel_sql.fw.preferences;

public interface IQueryTokenizerPreferenceBean {

    
    void setStatementSeparator(String statementSeparator);

    
    String getStatementSeparator();

    
    void setProcedureSeparator(String procedureSeparator);

    
    String getProcedureSeparator();

    
    void setLineComment(String lineComment);

    
    String getLineComment();

    
    void setRemoveMultiLineComments(boolean removeMultiLineComments);

    
    boolean isRemoveMultiLineComments();

    
    void setInstallCustomQueryTokenizer(boolean installCustomQueryTokenizer);

    
    boolean isInstallCustomQueryTokenizer();

    
    String getClientName();
    
    
    void setClientName(String value);

    
    String getClientVersion();
    
    
    void setClientVersion(String value);
    
    
}