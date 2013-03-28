package net.sourceforge.squirrel_sql.plugins.mssql.sql.constraint;



public class CheckConstraint extends MssqlConstraint {
    
    
    private String _checkExpression;
    
    
    public CheckConstraint() {
        super();
    }
    
    
    public String getCheckExpression() {
        return this._checkExpression;
    }
    
    
    public void setCheckExpression(String checkExpression) {
        this._checkExpression = checkExpression;
    }
    
}
