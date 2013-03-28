
package net.sourceforge.squirrel_sql.client.update.xmlbeans;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;


public class UpdateXmlSerializer {

    
    public void write(ChannelXmlBean channelBean, String filename)
            throws Exception {
        XMLEncoder os = new XMLEncoder(new BufferedOutputStream(
                new FileOutputStream(filename)));
        os.writeObject(channelBean);
        os.close();
    }

    
    public ChannelXmlBean read(String filename) throws IOException {
        return read(new FileInputStream(filename));
    }

    
    public ChannelXmlBean read(InputStream is) throws IOException {
        XMLDecoder bis = null;
        Object result = null;
        try {
            bis = new XMLDecoder(new BufferedInputStream(is));
            result = bis.readObject();
        } finally {
            if (is != null) {
                is.close();
            }
        }
        return (ChannelXmlBean) result;
    }
    
}
