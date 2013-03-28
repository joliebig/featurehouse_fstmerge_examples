
package net.sourceforge.squirrel_sql.client.update.xmlbeans;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import net.sourceforge.squirrel_sql.fw.util.FileWrapper;

public interface UpdateXmlSerializer {

	
	void write(ChannelXmlBean channelBean, String filename) throws FileNotFoundException;

	
	void write(ChangeListXmlBean changeBean, String filename) throws FileNotFoundException;

	
	void write(ChangeListXmlBean changeBean, FileWrapper file) throws FileNotFoundException;

	
	ChannelXmlBean readChannelBean(String filename) throws FileNotFoundException, IOException;

	ChannelXmlBean readChannelBean(FileWrapper fileWrapper) throws FileNotFoundException, IOException;

	
	ChannelXmlBean readChannelBean(InputStream is) throws IOException;

	ChangeListXmlBean readChangeListBean(FileWrapper file) throws FileNotFoundException;

}