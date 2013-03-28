
package genj;

import genj.util.Resources;


public class Version {

  
  private String version;

  
  private String build;

  
  public static final Version singleton = new Version();

  
  private Version() {

    Resources r = Resources.get(this);
    
    version = r.getString("version", false);
    if (version==null) 
      version = "?";
    
    build = r.getString("build", false);
    if (build==null)
      build = "?";
    
  }

  
  public String toString() {
    return getVersionString();
  }
  
  
  public String getVersionString() {
    return version;
  }

  
  public String getBuildString() {
    return build;
  }

  
  public static Version getInstance() {
    return singleton;
  }

}
