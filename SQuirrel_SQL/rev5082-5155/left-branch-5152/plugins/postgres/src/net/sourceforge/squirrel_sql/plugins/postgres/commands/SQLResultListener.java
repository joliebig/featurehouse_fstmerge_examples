package net.sourceforge.squirrel_sql.plugins.postgres.commands;



public interface SQLResultListener {

    
    void finished(String[] sql);

}
