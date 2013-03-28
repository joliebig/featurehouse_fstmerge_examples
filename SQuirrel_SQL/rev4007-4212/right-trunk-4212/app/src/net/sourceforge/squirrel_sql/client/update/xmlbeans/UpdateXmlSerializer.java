
package net.sourceforge.squirrel_sql.client.update.xmlbeans;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import net.sourceforge.squirrel_sql.client.update.gui.ArtifactAction;
import net.sourceforge.squirrel_sql.client.util.IOUtilities;


public class UpdateXmlSerializer {

   
   private EnumPersistenceDelegate enumDelegate = new EnumPersistenceDelegate();
   
   
   public void write(ChannelXmlBean channelBean, String filename)
         throws FileNotFoundException {
      XMLEncoder os = getXmlEncoder(filename);
      os.writeObject(channelBean);
      os.close();
   }

   
   public void write(ChangeListXmlBean changeBean, String filename)
         throws FileNotFoundException {
      XMLEncoder os = getXmlEncoder(filename);
      os.writeObject(changeBean);
      os.close();
   }

   
   public void write(ChangeListXmlBean changeBean, File file)
         throws FileNotFoundException {
      XMLEncoder os = getXmlEncoder(file.getAbsolutePath());
      os.writeObject(changeBean);
      os.close();
   }
   
   
   public ChannelXmlBean readChannelBean(String filename) throws FileNotFoundException,
         IOException {
      if (filename == null) {
         throw new IllegalArgumentException("filename cannot be null");
      }
      return readChannelBean(new FileInputStream(filename));
   }

   
   public ChannelXmlBean readChannelBean(InputStream is) throws IOException {
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

   public ChangeListXmlBean readChangeListBean(File file)
         throws FileNotFoundException {
      XMLDecoder bis = null;
      FileInputStream fis = null;
      Object result = null;
      try {
         fis = new FileInputStream(file);
         bis = new XMLDecoder(new BufferedInputStream(fis));
         result = bis.readObject();
      } finally {
         IOUtilities.closeInputStream(fis);
         if (bis != null) {
            bis.close();
         }
      }
      return (ChangeListXmlBean) result;      
   }
   
   private XMLEncoder getXmlEncoder(String filename)
         throws FileNotFoundException {
      XMLEncoder result = null;
      BufferedOutputStream os = 
         new BufferedOutputStream(new FileOutputStream(filename));
      result = new XMLEncoder(os);
      result.setPersistenceDelegate(ArtifactAction.class, enumDelegate);
      return result;
   }
}
