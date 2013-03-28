
package test;

import java.sql.Connection;
import java.sql.SQLException;

import org.h2.api.Trigger;

public class MyH2Trigger implements Trigger
{

	public void foo()
	{
		System.out.println("foo was called");
	}

	
	public void fire(Connection arg0, Object[] arg1, Object[] arg2) throws SQLException
	{
		System.out.println("fire was called");

	}

	
	public void init(Connection arg0, String arg1, String arg2, String arg3, boolean bool, int anInt)
		throws SQLException
	{
		System.out.println("init was called");

	}

}
