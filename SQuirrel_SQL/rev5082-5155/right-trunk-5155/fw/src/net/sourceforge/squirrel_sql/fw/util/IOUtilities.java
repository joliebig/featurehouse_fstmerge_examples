
package net.sourceforge.squirrel_sql.fw.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;

public interface IOUtilities
{

	String HTTP_PROTOCOL_PREFIX = "http";

	void closeInputStream(InputStream is);

	void closeOutputStream(OutputStream os);

	
	void closeReader(Reader reader);

	
	void closeWriter(Writer writer);

	
	void copyBytes(InputStream is, OutputStream os) throws IOException;

	
	void copyFile(FileWrapper from, FileWrapper to) throws IOException;

	
	long getCheckSum(File f) throws IOException;

	
	long getCheckSum(FileWrapper f) throws IOException;

	
	public int copyBytesToFile(InputStream is, FileWrapper outputFile) throws IOException;

	
	public int downloadHttpFile(final URL url, FileWrapper destFile, IProxySettings proxySettings)
		throws IOException;

	public URL constructHttpUrl(final String host, final int port, final String fileToGet)
		throws MalformedURLException;

}