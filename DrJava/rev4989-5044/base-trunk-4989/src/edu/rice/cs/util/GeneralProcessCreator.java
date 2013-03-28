

package edu.rice.cs.util;

import edu.rice.cs.drjava.config.PropertyMaps;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;



public class GeneralProcessCreator extends ProcessCreator {
  protected List<List<List<String>>> _seqs;
    
  
  public GeneralProcessCreator(String cmdline, String workdir, PropertyMaps pm) {
    _cmdline = cmdline;
    _workdir = workdir;
    _props = pm;
  }

  
  public GeneralProcessCreator(List<List<List<String>>> seqs, String workdir, PropertyMaps pm) {
    _seqs = seqs;
    _workdir = workdir;
    _props = pm;
  }
  
  
  protected static String getProcessCmdLine(List<String> cmds) {
    StringBuilder sb = new StringBuilder();
    for (int i=0; i<cmds.size(); ++i) {
      sb.append(" ");
      sb.append(StringOps.unescapeFileName(cmds.get(i)));
    }
    String s = sb.toString();
    if (s.length()>0) {
      s = s.substring(1);
    }
    return s;
  }
  
  
  protected static String getProcessChainCmdLine(List<List<String>> pipe) {
    StringBuilder sb = new StringBuilder();
    final String sep = " "+ProcessChain.PIPE_SEPARATOR+" ";
    for (int i=0; i<pipe.size(); ++i) {
      sb.append(sep);
      sb.append(getProcessCmdLine(pipe.get(i)));
    }
    String s = sb.toString();
    if (s.length()>0) {
      s = s.substring(sep.length());
    }
    return s;
  }
  
  
  protected static String getProcessSequenceCmdLine(List<List<List<String>>> seqs) {
    StringBuilder sb = new StringBuilder();
    final String sep = " "+ProcessChain.PROCESS_SEPARATOR+" ";
    for (int i=0; i<seqs.size(); ++i) {
      sb.append(sep);
      sb.append(getProcessChainCmdLine(seqs.get(i)));
    }
    String s = sb.toString();
    if (s.length()>0) {
      s = s.substring(sep.length());
    }
    return s;
  }
  
  
  public String cmdline() {
    if (_cmdline==null) {
      if (_cachedCmdLine==null) {
        if (_seqs.size()==1) {
          
          List<List<String>> pipe = _seqs.get(0);
          if (pipe.size()==1) {
            
            List<String> cmds = pipe.get(0);
            _cachedCmdLine = getProcessCmdLine(cmds);
          }
          else {
            
            _cachedCmdLine = getProcessChainCmdLine(pipe);
          }
        }
        else  {
          
          _cachedCmdLine = getProcessSequenceCmdLine(_seqs);
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

  public static final edu.rice.cs.util.Log LOG = new edu.rice.cs.util.Log("process.txt",false);
  
  
  public Process start() throws IOException {
    
    _evaluatedWorkDir = StringOps.replaceVariables(_workdir, _props, PropertyMaps.GET_CURRENT);
    _evaluatedWorkDir = StringOps.unescapeFileName(_evaluatedWorkDir);
    File dir = null;
    if (!_evaluatedWorkDir.trim().equals("")) { dir = new File(_evaluatedWorkDir); }

    
    String[] env = null;
    if ((_env!=null) && (_env.size()>0)) {
      env = new String[_env.size()];
      int i = 0;
      for(String key: _env.keySet()) {
        String value = _env.get(key);
        env[i] = key+"="+value;
      }
    }

    
    if (_cmdline!=null) {
      _evaluatedCmdLine = StringOps.replaceVariables(_cmdline, _props, PropertyMaps.GET_CURRENT);
      _seqs = StringOps.commandLineToLists(_evaluatedCmdLine);
    }
    LOG.log("\t"+edu.rice.cs.plt.iter.IterUtil.toString(_seqs));
    if (_seqs.size()<1) { throw new IOException("No process to start."); }
    if (_seqs.size()==1) {
      
      List<List<String>> pipe = _seqs.get(0);
      if (pipe.size()<1) { throw new IOException("No process to start."); }
      if (pipe.size()==1) {
        
        List<String> cmds = pipe.get(0);
        if (cmds.size()<1) { throw new IOException("No process to start."); }
        String[] cmdarray = new String[cmds.size()];
        for (int i=0; i<cmds.size(); ++i) {
          cmdarray[i] = StringOps.unescapeFileName(cmds.get(i));
        }
        
        return Runtime.getRuntime().exec(cmdarray,env,dir);
      }
      
      ProcessCreator[] creators = new ProcessCreator[pipe.size()];
      for (int i=0; i<pipe.size(); ++i) {
        List<String> cmds = pipe.get(i);
        if (cmds.size()<1) { throw new IOException("No process to start."); }
        String[] cmdarray = new String[cmds.size()];
        for (int j=0; j<cmds.size(); ++j) {
          cmdarray[j] = StringOps.unescapeFileName(cmds.get(j));
        }
        creators[i] = new ProcessCreator(cmdarray, _workdir);
      }
      return new ProcessChain(creators);
    }
    
    ProcessCreator[] creators = new ProcessCreator[_seqs.size()];
    for (int i=0; i<_seqs.size(); ++i) {
      List<List<List<String>>> l = new ArrayList<List<List<String>>>();
      l.add(_seqs.get(i));
      creators[i] = new GeneralProcessCreator(l, _workdir, _props);
    }
    return new ProcessSequence(creators);
  }
}
