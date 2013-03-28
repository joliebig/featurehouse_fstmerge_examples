

package edu.rice.cs.drjava.model;

import java.lang.ClassLoader;
import java.net.URL;

import edu.rice.cs.drjava.model.repl.WrapperClassLoader;


public class BrainClassLoader extends ClassLoader {
  
  ClassLoader projectCL;
  ClassLoader buildCL;
  ClassLoader projectFilesCL;
  ClassLoader externalFilesCL;
  ClassLoader extraCL;
  ClassLoader systemCL;
  
  public BrainClassLoader(ClassLoader p, ClassLoader b, ClassLoader pf, ClassLoader ef, ClassLoader e) {
    projectCL = p;
    buildCL = b;
    projectFilesCL = pf;
    externalFilesCL = ef;
    extraCL = e;
    systemCL = new WrapperClassLoader(this.getClass().getClassLoader().getSystemClassLoader());
  }
  
  
  public URL getResource(String name) {
    URL resource = projectCL.getResource(name);
    if (resource != null) return resource;
    
    resource = buildCL.getResource(name);
    if (resource != null) return resource;
    
    resource = projectFilesCL.getResource(name);
    if (resource != null) return resource;
    
    resource = externalFilesCL.getResource(name);
    if (resource != null) return resource;
    
    resource = extraCL.getResource(name);
    if (resource != null) return resource;

    resource = systemCL.getResource(name);
    if (resource != null) return resource;

    return resource;
  }
}




