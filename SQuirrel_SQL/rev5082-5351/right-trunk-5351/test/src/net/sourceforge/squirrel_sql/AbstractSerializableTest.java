
package net.sourceforge.squirrel_sql;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import org.junit.After;
import org.junit.Test;



public abstract class AbstractSerializableTest extends BaseSQuirreLJUnit4TestCase
{

	protected Serializable serializableToTest = null;
	
	public AbstractSerializableTest()
	{
		super();
	}

	@Test
	public void serializationTest() throws Exception
	{
		String tmpDir = System.getProperty("java.io.tmpdir", "/tmp");
		String filename = tmpDir + File.separator  + "classUnderTest.ser";
		FileOutputStream fos = null;
		ObjectOutputStream out = null;
		fos = new FileOutputStream(filename);
		out = new ObjectOutputStream(fos);
		out.writeObject(serializableToTest);
		out.close();
	}

	@After
	public void tearDown() throws Exception
	{
		serializableToTest = null;
	}
	
}