package test;



import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;

import net.sourceforge.squirrel_sql.client.ApplicationArguments;


public class ProgressGetTablesTest {
   
   private static void test(Connection con) throws Exception {
      DatabaseMetaData md = con.getMetaData();
      
      String cat = "DBCOPYDEST";
      String schemaPattern = "MANNINGR";
      String tableNamePattern = "TEST";

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
      Class.forName("com.ddtek.jdbc.openedge.OpenEdgeDriver");
      String jdbcUrl = "jdbc:datadirect:openedge://192.168.1.136:20935;DATABASENAME=dbcopydest";
      Connection con = DriverManager.getConnection(jdbcUrl,
                                                   "manningr",
                                                   "");
      test(con);
   }

    
}
