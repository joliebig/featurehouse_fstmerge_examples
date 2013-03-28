

package edu.rice.cs.drjava.model.repl;

import java.net.URL;


public interface JavaInterpreter extends Interpreter {
  
  
  
  public void addProjectClassPath(URL path);
  public void addBuildDirectoryClassPath(URL path);
  public void addProjectFilesClassPath(URL path);
  public void addExternalFilesClassPath(URL path);
  public void addExtraClassPath(URL path);
  
  
  public void setPackageScope(String packageName);
  
  
  public Object getVariable(String name);
  
  
  public Class getVariableClass(String name);
  
  
  public void defineVariable(String name, Object value);
  
  
  public void defineVariable(String name, boolean value);
  
  
  public void defineVariable(String name, byte value);
  
  
  public void defineVariable(String name, char value);
  
  
  public void defineVariable(String name, double value);
  
  
  public void defineVariable(String name, float value);
  
  
  
  public void defineVariable(String name, int value);
  
  
  public void defineVariable(String name, long value);
  
  
  public void defineVariable(String name, short value);
  
  
  public void defineConstant(String name, Object value);
  
  
  public void defineConstant(String name, boolean value);
  
  
  public void defineConstant(String name, byte value);
  
  
  public void defineConstant(String name, char value);
  
  
  public void defineConstant(String name, double value);
  
  
  public void defineConstant(String name, float value);
  
  
  public void defineConstant(String name, int value);
  
  
  public void defineConstant(String name, long value);
  
  
  public void defineConstant(String name, short value);
  
  
  public void setPrivateAccessible(boolean accessible);
}
