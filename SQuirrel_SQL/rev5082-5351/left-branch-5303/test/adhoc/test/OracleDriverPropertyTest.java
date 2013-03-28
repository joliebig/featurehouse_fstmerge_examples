
package test;

import java.sql.Driver;
import java.sql.DriverPropertyInfo;
import java.util.Properties;

public class OracleDriverPropertyTest
{

	
	public static void main(String[] args) throws Exception
	{
		Driver driver = (Driver) (Class.forName("oracle.jdbc.driver.OracleDriver").newInstance());
		DriverPropertyInfo[] infos =
			driver.getPropertyInfo("jdbc:oracle:thin:@localhost:1521:orcl", new Properties());
		for (DriverPropertyInfo info : infos) {
			System.out.println("info.name="+info.name);
		}
	}

}
