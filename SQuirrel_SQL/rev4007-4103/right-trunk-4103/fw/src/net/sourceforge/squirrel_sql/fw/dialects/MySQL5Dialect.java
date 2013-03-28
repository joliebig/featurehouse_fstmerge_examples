
package net.sourceforge.squirrel_sql.fw.dialects;

public class MySQL5Dialect extends MySQLDialect {

    
    @Override
    public boolean supportsProduct(String databaseProductName, String databaseProductVersion) {
        if (databaseProductName == null || databaseProductVersion == null) {
            return false;
        }
        if (!databaseProductName.trim().toLowerCase().startsWith("mysql")) {
            return false;
        }
        return databaseProductVersion.startsWith("5");
    }

    
    public DialectType getDialectType() {
       return DialectType.MYSQL5;
    }
    
}
