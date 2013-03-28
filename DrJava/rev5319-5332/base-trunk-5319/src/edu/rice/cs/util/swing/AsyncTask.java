

package edu.rice.cs.util.swing;


public abstract class AsyncTask<ParamType, ResType> {

 private String _name;

 
 public AsyncTask() { this("Untitled"); }

 
 public AsyncTask(String name) { _name = name; }

 
 public abstract ResType runAsync(ParamType param, IAsyncProgress monitor) throws Exception;

 
 public abstract void complete(AsyncCompletionArgs<ResType> args);

 
 public abstract String getDiscriptionMessage();

 
 public String getName() { return _name; }

 
 public int getMinProgress() { return 0; }

 
 public int getMaxProgress() { return 100; }

 public String toString() {
  return getClass().getName() + ": " + getName() + " (@" + System.identityHashCode(this) + ")";
 }
}