package net.sourceforge.squirrel_sql.plugins.mssql.sql.constraint;



import java.util.ArrayList;
import java.util.List;

public class TableConstraints {
    
    private ArrayList<MssqlConstraint> _constraints;
    
    
    public TableConstraints() {
        _constraints = new ArrayList<MssqlConstraint>();
    }
    
    public MssqlConstraint[] getConstraints() {
        
        return _constraints.toArray(new MssqlConstraint[_constraints.size()]);
    }
    
    public void addConstraint(MssqlConstraint constraint) {
        _constraints.add(constraint);
    }
    
    public List<DefaultConstraint> getDefaultsForColumn(String columnName) {
        ArrayList<DefaultConstraint> results = new ArrayList<DefaultConstraint>();
        for (int i = 0; i < _constraints.size(); i++) {
            MssqlConstraint constraint = _constraints.get(i);
            if (constraint instanceof DefaultConstraint) {
                DefaultConstraint def = (DefaultConstraint) constraint;
                if (def.constrainsColumn(columnName))
                    results.add(def);
            }
        }
        return results;
    }
    
    public List<CheckConstraint> getCheckConstraints() {
        ArrayList<CheckConstraint> results = new ArrayList<CheckConstraint>();
        for (int i = 0; i < _constraints.size(); i++) {
            MssqlConstraint constraint = _constraints.get(i);
            if (constraint instanceof CheckConstraint) {
                results.add((CheckConstraint)constraint);
            }
        }
        return results;
    }
    
    public List<ForeignKeyConstraint> getForeignKeyConstraints() {
        ArrayList<ForeignKeyConstraint> results = 
            new ArrayList<ForeignKeyConstraint>();
        for (int i = 0; i < _constraints.size(); i++) {
            MssqlConstraint constraint = _constraints.get(i);
            if (constraint instanceof ForeignKeyConstraint) {
                results.add((ForeignKeyConstraint)constraint);
            }
        }
        return results;
    }
    
    public List<PrimaryKeyConstraint> getPrimaryKeyConstraints() {
        ArrayList<PrimaryKeyConstraint> results = 
            new ArrayList<PrimaryKeyConstraint>();
        for (int i = 0; i < _constraints.size(); i++) {
            MssqlConstraint constraint = _constraints.get(i);
            if (constraint instanceof PrimaryKeyConstraint) {
                results.add((PrimaryKeyConstraint)constraint);
            }
        }
        return results;
    }
    
}
