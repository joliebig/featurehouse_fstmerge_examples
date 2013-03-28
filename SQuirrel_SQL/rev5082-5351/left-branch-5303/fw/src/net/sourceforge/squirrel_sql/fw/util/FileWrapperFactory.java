
package net.sourceforge.squirrel_sql.fw.util;

import java.io.File;
import java.net.URI;


public interface FileWrapperFactory
{

	
	FileWrapper create(FileWrapperImpl impl);

	
	FileWrapper create(File f);
	
	
	FileWrapper create(String pathname);

	
	FileWrapper create(String parent, String child);

	
	FileWrapper create(FileWrapper parent, String child);

	
	FileWrapper create(URI uri);

}