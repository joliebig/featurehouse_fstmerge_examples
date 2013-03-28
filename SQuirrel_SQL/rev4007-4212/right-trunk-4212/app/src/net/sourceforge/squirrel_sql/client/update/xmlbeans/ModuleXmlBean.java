
package net.sourceforge.squirrel_sql.client.update.xmlbeans;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;


public class ModuleXmlBean implements Serializable {

    private static final long serialVersionUID = -6047289718869161323L;



    private String name;
    
    private Set<ArtifactXmlBean> artifacts = new HashSet<ArtifactXmlBean>();
    














    
    public String getName() {
        return name;
    }

    
    public void setName(String name) {
        this.name = name;
    }

    
    public Set<ArtifactXmlBean> getArtifacts() {
        return artifacts;
    }

    
    public void setArtifacts(Set<ArtifactXmlBean> artifacts) {
        this.artifacts = artifacts;
    }
    
    
    public void addArtifact(ArtifactXmlBean artifact) {
        this.artifacts.add(artifact);
    }
    
    
}
