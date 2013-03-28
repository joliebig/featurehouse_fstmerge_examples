
package net.sourceforge.pmd.lang.java.typeresolution;

import net.sourceforge.pmd.lang.java.typeresolution.visitors.PMDASMVisitor;

import org.objectweb.asm.ClassReader;

import java.io.IOException;
import java.util.LinkedList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class PMDASMClassLoader extends ClassLoader {

    public PMDASMClassLoader(ClassLoader parent) {
    	super(parent);
    }

    private Set<String> dontBother = new HashSet<String>();

    public synchronized Map<String, String> getImportedClasses(String name) throws ClassNotFoundException {

        if (dontBother.contains(name)) {
            throw new ClassNotFoundException(name);
        }
        try {
            ClassReader reader = new ClassReader(getResourceAsStream(name.replace('.', '/') + ".class"));
            PMDASMVisitor asmVisitor = new PMDASMVisitor();
            reader.accept(asmVisitor, 0);

            List<String> inner = asmVisitor.getInnerClasses();
            if (inner != null && !inner.isEmpty()) {
                inner = new LinkedList<String>(inner); 
                for (String str: inner) {
                    reader = new ClassReader(getResourceAsStream(str.replace('.', '/') + ".class"));
                    reader.accept(asmVisitor, 0);
                }
            }
            return asmVisitor.getPackages();
        } catch (IOException e) {
            dontBother.add(name);
            throw new ClassNotFoundException(name);
        }
    }
}