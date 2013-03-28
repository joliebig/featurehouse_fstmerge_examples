
package net.sourceforge.squirrel_sql.client.update.xmlbeans;

public class ChannelXmlBean {

    private String name;
    
    private ReleaseXmlBean currentRelease;

    
    public String getName() {
        return name;
    }

    
    public void setName(String name) {
        this.name = name;
    }

    
    public ReleaseXmlBean getCurrentRelease() {
        return currentRelease;
    }

    
    public void setCurrentRelease(ReleaseXmlBean currentRelease) {
        this.currentRelease = currentRelease;
    }

    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((currentRelease == null) ? 0 : currentRelease.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
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
        final ChannelXmlBean other = (ChannelXmlBean) obj;
        if (currentRelease == null) {
            if (other.currentRelease != null)
                return false;
        } else if (!currentRelease.equals(other.currentRelease))
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        return true;
    }
    
}
