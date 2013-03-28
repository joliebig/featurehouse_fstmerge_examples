

package edu.rice.cs.util;

import edu.rice.cs.drjava.config.PropertyMaps;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;



public class ProcessCreator {
  protected String _cmdline = null;
  protected String _evaluatedCmdLine = null;
  protected String _workdir;
  protected String _evaluatedWorkDir = null;
  protected String[] _cmdarray; 
  protected Map<String,String> _env;
  protected PropertyMaps _props = PropertyMaps.TEMPLATE;
  
  
  protected ProcessCreator() { }
  
  
  public ProcessCreator(String cmdline, String workdir, PropertyMaps pm) {
    _cmdline = cmdline;
    _workdir = workdir;
    _props = pm;
  }

  
  public ProcessCreator(String[] cmdarray, String workdir) {
    _cmdarray = cmdarray;
    _workdir = workdir;
  }
  
  
  protected String _cachedCmdLine = null;
  
  
  public String cmdline() {
    if (_cmdline == null) {
      if (_cachedCmdLine == null) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < _cmdarray.length; ++i) {
          sb.append(" ");
          sb.append(StringOps.unescapeFileName(_cmdarray[i]));
        }
        _cachedCmdLine = sb.toString();
        if (_cachedCmdLine.length() > 0) {
          _cachedCmdLine = _cachedCmdLine.substring(1);
        }
      }
      return _cachedCmdLine;
    }
    else {
      return _cmdline;
    }
  }
  
  
  public Map<String,String> environment() {
    return _env;
  }
  
  
  public String workDir() {
    return _workdir;
  }
  
  
  public String evaluatedCommandLine() {
    return _evaluatedCmdLine;
  }
  
  
  public String evaluatedWorkDir() {
    return _evaluatedWorkDir;
  }
  
  
  public PropertyMaps getPropertyMaps() { return _props; }
  
  
  public Process start() throws IOException {
    
    _evaluatedWorkDir = StringOps.replaceVariables(_workdir, _props, PropertyMaps.GET_CURRENT);
    _evaluatedWorkDir = StringOps.unescapeFileName(_evaluatedWorkDir);
    File dir = null;
    if (!_evaluatedWorkDir.trim().equals("")) { dir = new File(_evaluatedWorkDir); }
    
    
    String[] env = null;
    if ((_env != null) && (_env.size() > 0)) {
      env = new String[_env.size()];
      int i = 0;
      for(String key: _env.keySet()) {
        String value = _env.get(key);
        env[i] = key + "=" + value;
      }
    }
    
    
    if (_cmdline != null) {
      _evaluatedCmdLine = StringOps.replaceVariables(_cmdline, _props, PropertyMaps.GET_CURRENT);
      List<List<List<String>>> seqs = StringOps.commandLineToLists(_evaluatedCmdLine);
      if (seqs.size() != 1) { throw new IllegalArgumentException("ProcessCreator needs a command line with just one process."); }
      List<List<String>> pipe = seqs.get(0);
      if (pipe.size()<1) { throw new IllegalArgumentException("ProcessCreator needs a command line with just one process."); }
      List<String> cmds = pipe.get(0);
      _cmdarray = new String[cmds.size()];
      for (int i = 0; i < cmds.size(); ++i) {
        _cmdarray[i] = StringOps.unescapeFileName(cmds.get(i));
      }
    }
    
    return Runtime.getRuntime().exec(_cmdarray,env,dir);
  }
}
