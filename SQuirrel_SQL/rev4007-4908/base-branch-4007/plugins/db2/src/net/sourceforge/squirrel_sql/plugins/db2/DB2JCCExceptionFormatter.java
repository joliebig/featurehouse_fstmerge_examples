package net.sourceforge.squirrel_sql.plugins.db2;

import java.lang.reflect.Method;

import net.sourceforge.squirrel_sql.fw.util.ExceptionFormatter;


public class DB2JCCExceptionFormatter implements ExceptionFormatter {

    

    
    private static final String JCC_EXCEPTION_PREFIX = "com.ibm.db2.jcc";

    
    private static final String JCC_EXCEPTION_CLASS = "SqlException";
    
    
    private static final String METHOD_GET_SQLCA = "getSqlca";

    private static final String METHOD_GET_SQL_STATE = "getSqlState";

    private static final String METHOD_GET_SQL_CODE = "getSqlCode";

    private static final String METHOD_GET_MESSAGE = "getMessage";

    
    public boolean formatsException(Throwable t) {
        if (t == null) {
            return false;
        } else {
            String className = t.getClass().getName();
            return className.startsWith(JCC_EXCEPTION_PREFIX)
                    && className.endsWith(JCC_EXCEPTION_CLASS);
        }
    }

    
    public String format(Throwable t) throws Exception {
        StringBuilder builder = new StringBuilder();
        
        Method getSqlca = t.getClass().getMethod(METHOD_GET_SQLCA,
                (Class[]) null);
        Object sqlca = getSqlca.invoke(t, (Object[]) null);

        
        Method getMessage = sqlca.getClass().getMethod(METHOD_GET_MESSAGE,
                (Class[]) null);
        String msg = getMessage.invoke(sqlca, (Object[]) null).toString();

        
        Method getSqlCode = sqlca.getClass().getMethod(METHOD_GET_SQL_CODE,
                (Class[]) null);
        int sqlCode = (Integer) getSqlCode.invoke(sqlca, (Object[]) null);

        
        Method getSqlState = sqlca.getClass().getMethod(
                METHOD_GET_SQL_STATE, (Class[]) null);
        String sqlState = getSqlState.invoke(sqlca, (Object[]) null)
                .toString();

        builder.append(msg).append(" SQL Code: ").append(sqlCode).append(
                ", SQL State: ").append(sqlState);
        return builder.toString();
    }
}