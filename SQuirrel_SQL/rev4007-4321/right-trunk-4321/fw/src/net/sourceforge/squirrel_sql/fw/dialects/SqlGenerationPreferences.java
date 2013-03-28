package net.sourceforge.squirrel_sql.fw.dialects;



public class SqlGenerationPreferences {
    private boolean qualifyTableNames = true;
    private boolean quoteIdentifiers = true;
    private String sqlStatementSeparator = ";";


    
    public void setQualifyTableNames(boolean qualifyTableNames) {
        this.qualifyTableNames = qualifyTableNames;
    }


    
    public boolean isQualifyTableNames() {
        return qualifyTableNames;
    }


    
    public void setQuoteIdentifiers(boolean quoteIdentifiers) {
        this.quoteIdentifiers = quoteIdentifiers;
    }


    
    public boolean isQuoteIdentifiers() {
        return quoteIdentifiers;
    }


    
    public void setSqlStatementSeparator(String sqlStatementSeparator) {
        this.sqlStatementSeparator = sqlStatementSeparator;
    }


    
    public String getSqlStatementSeparator() {
        return sqlStatementSeparator;
    }
}
