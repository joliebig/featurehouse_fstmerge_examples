
package net.sourceforge.squirrel_sql.plugins.dbcopy.event;


public class StatementEvent {
    
    
    public static final int CREATE_TABLE_TYPE = 0;

    
    public static final int CREATE_INDEX_TYPE = 1;
    
    public static final int CREATE_FOREIGN_KEY_TYPE = 2;
    
    public static final int INSERT_RECORD_TYPE = 3;
    
    
    private String statement = null;
    
    
    private String[] bindValues;
    
    
    private int statementType = -1;
    
    
    public StatementEvent(String aStatement, int type) {
        statement = aStatement;
        statementType = type;
    }

    
    public void setStatement(String statement) {
        this.statement = statement;
    }

    
    public String getStatement() {
        return statement;
    }

    
    public void setStatementType(int statementType) {
        this.statementType = statementType;
    }

    
    public int getStatementType() {
        return statementType;
    }

    
    public void setBindValues(String[] bindValues) {
        this.bindValues = bindValues;
    }

    
    public String[] getBindValues() {
        return bindValues;
    }
}
