package net.sourceforge.squirrel_sql.plugins.mssql.sql.constraint;



public class PrimaryKeyConstraint extends MssqlConstraint {
    
    
    private boolean _clustered;
    
    
    public PrimaryKeyConstraint() {
        super();
    }
    
    
    public boolean isClustered() {
        return this._clustered;
    }
    
    
    public void setClustered(boolean clustered) {
        this._clustered = clustered;
    }
    
}
