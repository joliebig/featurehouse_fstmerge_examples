package test;




import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import net.sourceforge.squirrel_sql.client.ApplicationArguments;


public class InformixSwitchCatalogTest {
   
	private static void changeDatabase(Connection con) throws Exception {
		Statement stmt = con.createStatement();
		stmt.execute("DATABASE dbcopydest");
	}
	
   private static void test(Connection con) 	{
   	try {
   	DatabaseMetaData md = con.getMetaData();
      String cat = "dbcopydest";
      String schemaPattern = null;
      String typeNamePattern = "%";

      con.setCatalog(cat);
      
      ResultSet rs = md.getUDTs(cat, schemaPattern, typeNamePattern, null);
      while (rs.next()) {
         
         String catalog = rs.getString(1);
         String schema = rs.getString(2);
         String simpleName = rs.getString(3);
         System.out.println("catalog: "+catalog);
         System.out.println("schema: "+schema);
         System.out.println("simpleName: "+simpleName);
      }
   	} catch (SQLException e) {
   		e.printStackTrace();
   		System.out.println("code="+e.getErrorCode());
   	}
   }  
    
    
   public static void main(String[] args) throws Exception {
      ApplicationArguments.initialize(new String[] {});
      Class.forName("com.informix.jdbc.IfxDriver");
      String jdbcUrl = "jdbc:informix-sqli://192.168.1.135:9088:INFORMIXSERVER=sockets_srvr";
      Connection con = DriverManager.getConnection(jdbcUrl,
                                                   "informix",
                                                   "password");
      System.out.println("Running test before issuing DATABASE command:");
      test(con);
      System.out.println("Issuing DATABASE dbcopydest command:");
      changeDatabase(con);
      System.out.println("Running test after issuing DATABASE command:");
      test(con);
   }
}
