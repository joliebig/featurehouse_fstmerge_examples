
package test;

import java.sql.Driver;
import java.sql.DriverPropertyInfo;
import java.util.Properties;

public class OracleDriverPropertyTest
{

	
	public static void main(String[] args) throws Exception
	{
		Driver driver = (Driver) (Class.forName("oracle.jdbc.driver.OracleDriver").newInstance());
		
		String nullUrl = null;
		String emptyUrl = "";
		String oracleUrl = "jdbc:oracle:thin:@localhost:1521:orcl";
		
		Properties nullProps = null;
		Properties emptyProps = new Properties();
		
		checkForUrl(driver, nullUrl, nullProps);
		checkForUrl(driver, emptyUrl, nullProps);
		checkForUrl(driver, oracleUrl, nullProps);
		
		checkForUrl(driver, nullUrl, emptyProps);
		checkForUrl(driver, emptyUrl, emptyProps);
		checkForUrl(driver, oracleUrl, emptyProps);
		
	}
	
	private static void checkForUrl(Driver driver, String url, Properties props) throws Exception {
		System.out.println("Checking driver for properties for URL: "+url+" with props="+props);
		DriverPropertyInfo[] infos = driver.getPropertyInfo(url, props);
		for (DriverPropertyInfo info : infos) {
			System.out.println("info.name="+info.name);
		}		
	}

}
