

package net.sf.freecol.common.io;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URLConnection;
import java.util.Enumeration;
import java.util.Properties;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sf.freecol.common.resources.ResourceFactory;
import net.sf.freecol.common.resources.ResourceMapping;



public class FreeColDataFile {
    private static final Logger logger = Logger.getLogger(FreeColDataFile.class.getName());
    
    private static final String RESOURCES_PROPERTIES_FILE = "resources.properties";

    
    private final File file;

    
    private final String jarDirectory;
    
    
    
    public FreeColDataFile(File file) {
        if (!file.exists()) {
            for (String ending : getFileEndings()) {
                final File tempFile = new File(file.getAbsolutePath() + ending);
                if (tempFile.exists()) {
                    file = tempFile;
                    break;
                }
            }
        }
        
        this.file = file;
        
        if (file.isDirectory()) {
            this.jarDirectory = null;
        } else {
            this.jarDirectory = findJarDirectory(file.getName().substring(0, file.getName().lastIndexOf('.')), file);
        }
    }
    
    
    private static String findJarDirectory(final String expectedName, File file) {
        JarFile jf = null;
        try {
            jf = new JarFile(file);
            final JarEntry entry = jf.entries().nextElement();
            final String en = entry.getName();
            final int index = en.lastIndexOf('/');
            String name = "";
            if (index > 0) {
                name = en.substring(0, index + 1);
            }
            return name;
        } catch (Exception e) {
            logger.log(Level.WARNING, "Exception while reading data file.", e);
            return expectedName;
        } finally {
            try {
                jf.close();
            } catch (Exception e) {}
        }
    }

    
    protected InputStream getInputStream(String filename) throws IOException {
        final URLConnection connection = getURI(filename).toURL().openConnection();
        connection.setDefaultUseCaches(false);
        return new BufferedInputStream(connection.getInputStream());
    }
    
    protected URI getURI(String filename) {
        try {
            if (filename.startsWith("urn:")) {
                return new URI(filename);
            } else if (file.isDirectory()) {
                return new File(file, filename).toURI();
            } else {
                return new URI("jar:file:" + file.toURI().getSchemeSpecificPart()
                               + "!/" + jarDirectory + filename);
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, "Exception while reading ResourceMapping from: " + file, e);
            return null;
        }
    }
    
    
    public ResourceMapping getResourceMapping() {
        try {
            final Properties properties = new Properties();
            final InputStream is = getInputStream(RESOURCES_PROPERTIES_FILE);
            try {
                properties.load(is);
            } finally {
                try {
                    is.close();
                } catch (Exception e) {}
            }
            ResourceMapping rc = new ResourceMapping();
            Enumeration<?> pn = properties.propertyNames();
            while (pn.hasMoreElements()) {
                final String key = (String) pn.nextElement();
                final URI resourceLocator = getURI(properties.getProperty(key));
                rc.add(key, ResourceFactory.createResource(resourceLocator));
            }  
            return rc;
        } catch (FileNotFoundException e) {
            return null;
        } catch (IOException e) {
            logger.log(Level.WARNING, "Exception while reading ResourceMapping from: " + file, e);
            return null;
        }
    }
    
    
    public FileFilter getFileFilter() {
        return new FileFilter() {
            public boolean accept(File f) {
                final String name = f.getName();
                for (String ending : getFileEndings()) {
                    if (name.endsWith(ending)) {
                        return true;
                    }
                }
                return false;
            }
        };
    }
    
    
    protected String[] getFileEndings() {
        return new String[] {".zip"};   
    }
}
