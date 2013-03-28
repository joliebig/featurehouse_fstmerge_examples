
package net.sourceforge.pmd.util;

import net.sourceforge.pmd.RuleSetNotFoundException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;

public final class ResourceLoader {

    
    private ResourceLoader() {
    }

    
    public static InputStream loadResourceAsStream(String name) throws RuleSetNotFoundException {
        InputStream stream = ResourceLoader.loadResourceAsStream(name, ResourceLoader.class.getClassLoader());
        if (stream == null) {
            throw new RuleSetNotFoundException("Can't find resource " + name + ". Make sure the resource is a valid file or URL or is on the CLASSPATH");
        }
        return stream;
    }

    
    public static InputStream loadResourceAsStream(String name, ClassLoader loader) throws RuleSetNotFoundException {
        File file = new File(name);
        if (file.exists()) {
            try {
                return new FileInputStream(file);
            } catch (FileNotFoundException e) {
                
            }
        } else {
            try {
                return new URL(name).openConnection().getInputStream();
            } catch (Exception e) {
                return loader.getResourceAsStream(name);
            }
        }
        throw new RuleSetNotFoundException("Can't find resource " + name + ". Make sure the resource is a valid file or URL or is on the CLASSPATH");
    }
}
