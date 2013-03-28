package net.sourceforge.squirrel_sql.plugins.mssql.sql.constraint;



public class DefaultConstraint extends MssqlConstraint {
    
    
    private String _defaultExpression;
    
    
    public DefaultConstraint() {
        super();
    }
    
    
    public String getDefaultExpression() {
        return this._defaultExpression;
    }
    
    
    public void setDefaultExpression(String defaultExpression) {
        this._defaultExpression = defaultExpression;
    }
    
}
