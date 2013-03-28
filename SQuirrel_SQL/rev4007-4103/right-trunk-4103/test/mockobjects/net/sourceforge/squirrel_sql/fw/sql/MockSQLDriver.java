
package net.sourceforge.squirrel_sql.fw.sql;

import java.beans.PropertyChangeListener;

import net.sourceforge.squirrel_sql.fw.id.IIdentifier;
import net.sourceforge.squirrel_sql.fw.id.UidIdentifierFactory;
import net.sourceforge.squirrel_sql.fw.persist.ValidationException;
import net.sourceforge.squirrel_sql.fw.util.beanwrapper.StringWrapper;

public class MockSQLDriver implements ISQLDriver {

    private String driverClassName = null;
    
    private String url = null;
    
    private String name = "mockSQLDriver";
    
    IIdentifier id = new UidIdentifierFactory().createIdentifier();
    
    public MockSQLDriver(String aClassName, String url) {
        driverClassName = aClassName;
        this.url = url;
    }
    
    public void assignFrom(ISQLDriver rhs) throws ValidationException {
        System.err.println("MockSQLDriver.assignFrom: stub not yet implemented");
    }

    public int compareTo(ISQLDriver rhs) {
        System.err.println("MockSQLDriver.compareTo: stub not yet implemented");
        return 0;
    }

    public IIdentifier getIdentifier() {
        System.err.println("MockSQLDriver.getIdentifier: stub not yet implemented");
        return null;
    }

    public String getDriverClassName() {
        return driverClassName;
    }

    public void setDriverClassName(String aClassName) 
        throws ValidationException 
    {
        driverClassName = aClassName;
    }

    @SuppressWarnings("deprecation")
    public String getJarFileName() {
        System.err.println("MockSQLDriver.getJarFileName: stub not yet implemented");
        return null;
    }

    public void setJarFileName(String value) throws ValidationException {
        System.err.println("MockSQLDriver.setJarFileName: stub not yet implemented");
    }

    public StringWrapper[] getJarFileNameWrappers() {
        System.err.println("MockSQLDriver.getJarFileNameWrappers: stub not yet implemented");
        return null;
    }

    public StringWrapper getJarFileNameWrapper(int idx)
            throws ArrayIndexOutOfBoundsException {
        System.err.println("MockSQLDriver.getJarFileNameWrapper: stub not yet implemented");
        return null;
    }

    public void setJarFileNameWrappers(StringWrapper[] value) {
        System.err.println("MockSQLDriver.setJarFileNameWrappers: stub not yet implemented");
    }

    public void setJarFileNameWrapper(int idx, StringWrapper value)
            throws ArrayIndexOutOfBoundsException {
        System.err.println("MockSQLDriver.setJarFileNameWrapper: stub not yet implemented");
    }

    public String[] getJarFileNames() {
        System.err.println("MockSQLDriver.getJarFileNames: stub not yet implemented");
        return null;
    }

    public void setJarFileNames(String[] values) {
        System.err.println("MockSQLDriver.setJarFileNames: stub not yet implemented");
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) throws ValidationException {
        this.url = url;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) throws ValidationException {
        this.name = name;
    }

    public boolean isJDBCDriverClassLoaded() {
        System.err.println("MockSQLDriver.isJDBCDriverClassLoaded: stub not yet implemented");
        return false;
    }

    public void setJDBCDriverClassLoaded(boolean cl) {
        System.err.println("MockSQLDriver.setJDBCDriverClassLoaded: stub not yet implemented");
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        System.err.println("MockSQLDriver.addPropertyChangeListener: stub not yet implemented");
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        System.err.println("MockSQLDriver.removePropertyChangeListener: stub not yet implemented");
    }

    
    public String getWebSiteUrl() {
        
        return null;
    }

    
    public void setWebSiteUrl(String url) throws ValidationException {
        
        
    }

    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final MockSQLDriver other = (MockSQLDriver) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

}
