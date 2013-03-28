

package org.jfree.chart.ui;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.jfree.chart.util.ObjectUtilities;


public class BasicProjectInfo extends Library {

    
    private static class OptionalLibraryHolder {
        private String libraryClass;
        private transient Library library;

        
        public OptionalLibraryHolder(String libraryClass) {
            if (libraryClass == null) {
                throw new NullPointerException(
                        "LibraryClass must not be null.");
            }
            this.libraryClass = libraryClass;
        }

        
        public OptionalLibraryHolder(Library library) {
          if (library == null) {
              throw new NullPointerException("Library must not be null.");
          }
          this.library = library;
          this.libraryClass = library.getClass().getName();
        }

        
        public String getLibraryClass() {
            return this.libraryClass;
        }

        
        public Library getLibrary() {
            if (this.library == null) {
                this.library = loadLibrary(this.libraryClass);
            }
            return this.library;
        }

        protected Library loadLibrary(String classname) {
            if (classname == null) {
                return null;
            }
            try {
                Class c = ObjectUtilities.getClassLoader(
                        getClass()).loadClass(classname);
                try {
                    Method m = c.getMethod("getInstance", (Class[]) null);
                    return (Library) m.invoke(null, (Object[]) null);
                }
                catch(Exception e) {
                    
                }
                return (Library) c.newInstance();
            }
            catch (Exception e) {
                
                
                return null;
            }
        }

    }

    
    private String copyright;

    
    private List libraries;

    private List optionalLibraries;

    
    public BasicProjectInfo() {
        this.libraries = new ArrayList();
        this.optionalLibraries = new ArrayList();
    }

    
    public BasicProjectInfo(String name, String version,
                            String licence, String info) {
        this();
        setName(name);
        setVersion(version);
        setLicenceName(licence);
        setInfo(info);
    }

    
    public BasicProjectInfo(String name, String version,
                            String info, String copyright,
                            String licenceName) {
        this(name, version, licenceName, info);
        setCopyright(copyright);
    }

    
    public String getCopyright() {
        return this.copyright;
    }

    
    public void setCopyright(String copyright) {
        this.copyright = copyright;
    }

    
    public void setInfo(String info) {
        super.setInfo(info);
    }

    
    public void setLicenceName(String licence) {
        super.setLicenceName(licence);
    }

    
    public void setName(String name) {
        super.setName(name);
    }

    
    public void setVersion(String version) {
        super.setVersion(version);
    }

    
    public Library[] getLibraries() {
        return (Library[]) this.libraries.toArray
                (new Library[this.libraries.size()]);
    }

    
    public void addLibrary(Library library) {
        if (library == null) {
            throw new NullPointerException();
        }
        this.libraries.add(library);
    }

    
    public Library[] getOptionalLibraries() {
        ArrayList libraries = new ArrayList();
        for (int i = 0; i < this.optionalLibraries.size(); i++) {
          OptionalLibraryHolder holder =
                  (OptionalLibraryHolder) this.optionalLibraries.get(i);
          Library l = holder.getLibrary();
          if (l != null) {
              libraries.add(l);
          }
        }
        return (Library[]) libraries.toArray(new Library[libraries.size()]);
    }

    
    public void addOptionalLibrary(String libraryClass) {
        if (libraryClass == null) {
            throw new NullPointerException("Library classname must be given.");
        }
        this.optionalLibraries.add
                (new OptionalLibraryHolder(libraryClass));
    }


    
    public void addOptionalLibrary(Library library) {
      if (library == null) {
          throw new NullPointerException("Library must be given.");
      }
      this.optionalLibraries.add(new OptionalLibraryHolder(library));
  }
}