
package net.sourceforge.squirrel_sql.fw.util;

import java.io.FileFilter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;


public interface FileWrapper
{

	
	String getName();

	
	String getParent();

	
	FileWrapper getParentFile();

	
	String getPath();

	
	boolean isAbsolute();

	
	String getAbsolutePath();

	
	FileWrapper getAbsoluteFile();

	
	String getCanonicalPath() throws IOException;

	
	FileWrapper getCanonicalFile() throws IOException;

	
	URL toURL() throws MalformedURLException;

	
	URI toURI();

	
	boolean canRead();

	
	boolean canWrite();

	
	boolean exists();

	
	boolean isDirectory();

	
	boolean isFile();

	
	boolean isHidden();

	
	long lastModified();

	
	long length();

	
	boolean createNewFile() throws IOException;

	
	boolean delete();

	
	void deleteOnExit();

	
	String[] list();

	
	String[] list(FilenameFilter filter);

	
	FileWrapper[] listFiles();

	
	FileWrapper[] listFiles(FilenameFilter filter);

	
	FileWrapper[] listFiles(FileFilter filter);

	
	boolean mkdir();

	
	boolean mkdirs();

	
	boolean renameTo(FileWrapper dest);

	
	boolean setLastModified(long time);

	
	boolean setReadOnly();

	
	boolean equals(Object obj);

	
	int hashCode();

	
	String toString();

}