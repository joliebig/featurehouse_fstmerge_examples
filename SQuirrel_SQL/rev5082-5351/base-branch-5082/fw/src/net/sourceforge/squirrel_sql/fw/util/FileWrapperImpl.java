

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
		
		if (parent != null) {
			FileWrapperImpl parentImpl = (FileWrapperImpl) parent;
			_wrappedFile = new File(parentImpl._wrappedFile, child);
		} else {
			_wrappedFile = new File((File)null, child);
		}
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
		if (_wrappedFile.getParentFile() == null) {
			return null;
		}
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
		return _wrappedFile.toURI().toURL();
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
		if (directory == null)
		{
			return new FileWrapperImpl(File.createTempFile(prefix, suffix, null));
		}
		else
		{
			return new FileWrapperImpl(File.createTempFile(prefix, suffix,
				((FileWrapperImpl) directory)._wrappedFile));
		}
	}

	
	public static FileWrapper createTempFile(String prefix, String suffix) throws IOException
	{
		return createTempFile(prefix, suffix, null);
	}

	

	
	public int compareTo(FileWrapperImpl pathname)
	{
		return _wrappedFile.compareTo(pathname._wrappedFile);
	}

	
	@Override
	public String toString()
	{
		return _wrappedFile.toString();
	}

	
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((_wrappedFile == null) ? 0 : _wrappedFile.hashCode());
		return result;
	}

	
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj) { return true; }
		if (obj == null) { return false; }
		if (getClass() != obj.getClass()) { return false; }
		FileWrapperImpl other = (FileWrapperImpl) obj;
		if (_wrappedFile == null)
		{
			if (other._wrappedFile != null) { return false; }
		}
		else if (!_wrappedFile.equals(other._wrappedFile)) { return false; }
		return true;
	}

	
	private synchronized void writeObject(java.io.ObjectOutputStream s) throws IOException
	{
	}

	
	private void readObject(java.io.ObjectInputStream s) throws IOException, ClassNotFoundException
	{

	}

	
	private static final long serialVersionUID = 301077366599181567L;

	private static FileWrapper[] wrapFiles(File[] resultFiles)
	{
		if (resultFiles == null) {
			return null;
		}
		FileWrapper[] wrappedFiles = new FileWrapper[resultFiles.length];
		for (int i = 0; i < resultFiles.length; i++)
		{
			wrappedFiles[i] = new FileWrapperImpl(resultFiles[i]);
		}
		return wrappedFiles;
	}

	public static FileWrapperImpl createTempFile(String prefix, String suffix, FileWrapperImpl directory)
		throws IOException
	{
		if (directory != null) {
			return new FileWrapperImpl(File.createTempFile(prefix, suffix, directory._wrappedFile));
		} else {
			return new FileWrapperImpl(File.createTempFile(prefix, suffix, null));
		}
	}

}
