
package test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;

public class TestSQLite
{

	public static void main(String[] args) throws Exception
	{
		Class.forName("org.sqlite.JDBC");
		Connection conn = DriverManager.getConnection("jdbc:sqlite:/tmp/test.dbf");
		Statement stat = conn.createStatement();
		stat.executeUpdate("drop table if exists test");
		stat.executeUpdate("create table test (myid integer)");
		stat.close();
		
		
		
		System.out.println("\t *** Before insert (empty table) *** \n");
		
		getDatabaseMetaDataType(conn, "test", "myid");
		printColumnTypeAndName(conn);

		PreparedStatement prep = conn.prepareStatement("insert into test values (?)");
		prep.setInt(1, 1);
		prep.executeUpdate();

		System.out.println("\t *** After insert (one record table) *** \n");
		
		getDatabaseMetaDataType(conn, "test", "myid");
		printColumnTypeAndName(conn);

		conn.close();
	}

	private static void printColumnTypeAndName(Connection conn) throws Exception
	{
		Statement stat = conn.createStatement();
		ResultSet rs = stat.executeQuery("select * from test");
		ResultSetMetaData md = rs.getMetaData();
		System.out.println("Column type from ResultSetMetaData: " + md.getColumnType(1));
		System.out.println("Column type name from ResultSetMetaData: " + md.getColumnTypeName(1) + "\n");
		rs.close();
	}
	
	private static int getDatabaseMetaDataType(Connection conn, String tableName, String columnName) throws Exception {
		int result = -1;
		ResultSet rs = conn.getMetaData().getColumns(null, null, tableName, columnName);
		while (rs.next()) {
			int columnType = rs.getInt(5);
			String columnTypeName = rs.getString(6);
			System.out.println("Column type from DatabaseMetaData: "+columnType);
			System.out.println("Column type name from DatabaseMetaData: "+columnTypeName+"\n");
			
		}
		rs.close();
		return result;
	}
}
