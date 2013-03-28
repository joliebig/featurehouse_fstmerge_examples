package test;



import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;

import net.sourceforge.squirrel_sql.client.ApplicationArguments;


public class SybaseNonDboTableTest {
   
   private static void test(Connection con) throws Exception {
      DatabaseMetaData md = con.getMetaData();
      
      String cat = "dbcopydest";
      String schemaPattern = null;
      String tableNamePattern = null;

      ResultSet rs = md.getTables(cat,
                                  schemaPattern,
                                  tableNamePattern,
                                  new String[] { "TABLE" });
      while (rs.next()) {
         
         String catalog = rs.getString(1);
         String schema = rs.getString(2);
         String simpleName = rs.getString(3);
         String tableType = rs.getString(4);
         String remarks = rs.getString(5);
         System.out.println("catalog: "+catalog);
         System.out.println("schema: "+schema);
         System.out.println("simpleName: "+simpleName);
         System.out.println("tableType: "+tableType);
         System.out.println("remarks: "+remarks);
      }
   }  
    
    
    
   public static void main(String[] args) throws Exception {
      ApplicationArguments.initialize(new String[] {});
      Class.forName("com.sybase.jdbc3.jdbc.SybDriver");
      String jdbcUrl = "jdbc:sybase:Tds:dbserver:4115/dbcopydest";
      Connection con = DriverManager.getConnection(jdbcUrl,
                                                   "dbcopydest",
                                                   "password");
      test(con);
   }

    
}
