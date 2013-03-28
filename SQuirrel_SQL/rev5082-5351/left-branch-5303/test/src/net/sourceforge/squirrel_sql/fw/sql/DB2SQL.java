package net.sourceforge.squirrel_sql.fw.sql;

public interface DB2SQL {

    String insertSubSelectSQL = 
        "insert into MY_TEST_TABLE ( SELECT_COL_1 , SELECT_COL_2 ) " +
        "values " +
        "( ( select 1 from sysibm.sysdummy1 ) , " +
        "( select 1 from sysibm.sysdummy1 ) )";
    
}
