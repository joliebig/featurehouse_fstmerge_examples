
package net.sourceforge.squirrel_sql.fw.util;

import java.io.File;
import java.net.URI;


public class FileWrapperFactoryImpl implements FileWrapperFactory
{

	
	public FileWrapper create(FileWrapperImpl impl)
	{
		return new FileWrapperImpl(impl);
	}

	
	public FileWrapper create(String pathname)
	{
		return new FileWrapperImpl(pathname);
	}

	
	public FileWrapper create(String parent, String child)
	{
		return new FileWrapperImpl(parent, child);
	}

	
	public FileWrapper create(FileWrapper parent, String child)
	{
		return new FileWrapperImpl(parent, child);
	}

	
	public FileWrapper create(URI uri)
	{
		return new FileWrapperImpl(uri);
	}

	
	public FileWrapper create(File f)
	{
		return new FileWrapperImpl(f);
	}
	
	
}
