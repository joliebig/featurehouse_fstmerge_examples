

package net.sourceforge.squirrel_sql.fw.util;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;


public class FileWrapperImpl implements Serializable, Comparable<FileWrapperImpl>, FileWrapper
{

	
	private File _wrappedFile = null;

	

	
	public FileWrapperImpl(FileWrapperImpl impl)
	{
		this._wrappedFile = impl._wrappedFile;
	}

	
	public FileWrapperImpl(File wrappedFile)
	{
		this._wrappedFile = wrappedFile;
	}

	
	public FileWrapperImpl(String pathname)
	{
		_wrappedFile = new File(pathname);
	}

	
	public FileWrapperImpl(String parent, String child)
	{
		_wrappedFile = new File(parent, child);
	}

	
	public FileWrapperImpl(FileWrapper parent, String child)
	{
		FileWrapperImpl parentImpl = (FileWrapperImpl) parent;
		_wrappedFile = new File(parentImpl._wrappedFile, child);
	}

	
	public FileWrapperImpl(URI uri)
	{
		_wrappedFile = new File(uri);
	}

	

	
	public String getName()
	{
		return _wrappedFile.getName();
	}

	
	public String getParent()
	{
		return _wrappedFile.getParent();
	}

	
	public FileWrapper getParentFile()
	{
		return new FileWrapperImpl(_wrappedFile.getParentFile());
	}

	
	public String getPath()
	{
		return _wrappedFile.getPath();
	}

	

	
	public boolean isAbsolute()
	{
		return _wrappedFile.isAbsolute();
	}

	
	public String getAbsolutePath()
	{
		return _wrappedFile.getAbsolutePath();
	}

	
	public FileWrapper getAbsoluteFile()
	{
		return new FileWrapperImpl(_wrappedFile.getAbsoluteFile());
	}

	
	public String getCanonicalPath() throws IOException
	{
		return _wrappedFile.getCanonicalPath();
	}

	
	public FileWrapper getCanonicalFile() throws IOException
	{
		return new FileWrapperImpl(_wrappedFile.getCanonicalFile());
	}

	
	public URL toURL() throws MalformedURLException
	{
		return _wrappedFile.toURL();
	}

	
	public URI toURI()
	{
		return _wrappedFile.toURI();
	}

	

	
	public boolean canRead()
	{
		return _wrappedFile.canRead();
	}

	
	public boolean canWrite()
	{
		return _wrappedFile.canWrite();
	}

	
	public boolean exists()
	{
		return _wrappedFile.exists();
	}

	
	public boolean isDirectory()
	{
		return _wrappedFile.isDirectory();
	}

	
	public boolean isFile()
	{
		return _wrappedFile.isFile();
	}

	
	public boolean isHidden()
	{
		return _wrappedFile.isHidden();
	}

	
	public long lastModified()
	{
		return _wrappedFile.lastModified();
	}

	
	public long length()
	{
		return _wrappedFile.length();
	}

	

	
	public boolean createNewFile() throws IOException
	{
		return _wrappedFile.createNewFile();
	}

	
	public boolean delete()
	{
		return _wrappedFile.delete();
	}

	
	public void deleteOnExit()
	{
		_wrappedFile.deleteOnExit();
	}

	
	public String[] list()
	{
		return _wrappedFile.list();
	}

	
	public String[] list(FilenameFilter filter)
	{
		return _wrappedFile.list(filter);
	}

	
	public FileWrapper[] listFiles()
	{
		return wrapFiles(_wrappedFile.listFiles());
	}

	
	public FileWrapper[] listFiles(FilenameFilter filter)
	{
		return wrapFiles(_wrappedFile.listFiles(filter));
	}

	
	public FileWrapper[] listFiles(FileFilter filter)
	{
		return wrapFiles(_wrappedFile.listFiles(filter));
	}

	
	public boolean mkdir()
	{
		return _wrappedFile.mkdir();
	}

	
	public boolean mkdirs()
	{
		return _wrappedFile.mkdirs();
	}

	
	public boolean renameTo(FileWrapper dest)
	{
		return _wrappedFile.renameTo(((FileWrapperImpl) dest)._wrappedFile);
	}

	
	public boolean setLastModified(long time)
	{
		return _wrappedFile.setLastModified(time);
	}

	
	public boolean setReadOnly()
	{
		return _wrappedFile.setReadOnly();
	}

	

	
	public static FileWrapper[] listRoots()
	{
		return wrapFiles(File.listRoots());
	}

	
	public static FileWrapper createTempFile(String prefix, String suffix, FileWrapper directory)
		throws IOException
	{
		return new FileWrapperImpl(File.createTempFile(prefix, suffix,
			((FileWrapperImpl) directory)._wrappedFile));
	}

	
	public static FileWrapper createTempFile(String prefix, String suffix) throws IOException
	{
		return createTempFile(prefix, suffix, null);
	}

	

	
	public int compareTo(FileWrapperImpl pathname)
	{
		return _wrappedFile.compareTo(pathname._wrappedFile);
	}

	
	public boolean equals(Object obj)
	{
		return _wrappedFile.equals(obj);
	}

	
	public int hashCode()
	{
		return _wrappedFile.hashCode();
	}

	
	public String toString()
	{
		return _wrappedFile.toString();
	}

	
	private synchronized void writeObject(java.io.ObjectOutputStream s) throws IOException
	{
	}

	
	private synchronized void readObject(java.io.ObjectInputStream s) throws IOException,
		ClassNotFoundException
	{

	}

	
	private static final long serialVersionUID = 301077366599181567L;

	private static FileWrapper[] wrapFiles(File[] resultFiles)
	{
		FileWrapper[] wrappedFiles = new FileWrapper[resultFiles.length];
		for (int i = 0; i < resultFiles.length; i++)
		{
			wrappedFiles[i] = new FileWrapperImpl(resultFiles[i]);
		}
		return wrappedFiles;
	}

}
