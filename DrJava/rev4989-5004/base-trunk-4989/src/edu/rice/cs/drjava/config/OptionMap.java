

package edu.rice.cs.drjava.config;

public interface OptionMap {
  
  public <T> T getOption(OptionParser<T> o);
  
  public <T> T setOption(Option<T> o, T val);
  
  public <T> String getString(OptionParser<T> o);
  
  public <T> void setString(OptionParser<T> o, String s);
  
  public <T> T removeOption(OptionParser<T> o);
  
  public Iterable<OptionParser<?>> keys();
}



