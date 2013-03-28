

package edu.rice.cs.plt.text;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.LinkedList;
import edu.rice.cs.plt.iter.IterUtil;
import edu.rice.cs.plt.tuple.Pair;
import edu.rice.cs.plt.tuple.Triple;
import edu.rice.cs.plt.tuple.Quad;


public class ArgumentParser {
  
  private final Map<String, Integer> _supportedOptions;
  private final HashMap<String, Iterable<String>> _defaults; 
  private final Map<String, String> _aliases;
  private boolean _strictOrder;
  private int _requiredParams;
  
  
  public ArgumentParser() {
    _supportedOptions = new HashMap<String, Integer>();
    _defaults = new HashMap<String, Iterable<String>>();
    _aliases = new HashMap<String, String>();
    _strictOrder = false;
    _requiredParams = 0;
  }
  
  
  public void requireStrictOrder() { _strictOrder = true; }
  
  
  public void requireParams(int count) { _requiredParams = count; }
  
  
  public boolean supportsOption(String name) {
    return _supportedOptions.containsKey(name) || _aliases.containsKey(name);
  }
  
  
  public void supportOption(String name, int arity) {
    if (supportsOption(name)) { throw new IllegalArgumentException(name + " is already supported"); }
    _supportedOptions.put(name, arity);
  }
  
  
  public void supportOption(String name, String... defaultArguments) {
    supportOption(name, defaultArguments.length);
    if (defaultArguments.length > 0) {
      _defaults.put(name, IterUtil.asIterable(defaultArguments));
    }
  }
  
  
  public void supportVarargOption(String name) {
    if (supportsOption(name)) { throw new IllegalArgumentException(name + " is already supported"); }
    _supportedOptions.put(name, -1);
  }
  
  
  public void supportVarargOption(String name, String... defaultArguments) {
    supportVarargOption(name);
    _defaults.put(name, IterUtil.asIterable(defaultArguments));
  }
  
  
  public void supportAlias(String aliasName, String optionName) {
    if (supportsOption(aliasName)) { throw new IllegalArgumentException(aliasName + " is already supported"); }
    if (!supportsOption(optionName)) { throw new IllegalArgumentException(optionName + " is not supported"); }
    if (_aliases.containsKey(optionName)) { optionName = _aliases.get(optionName); }
    _aliases.put(aliasName, optionName);
  }
  
  
  
  public Result parse(String... args) throws IllegalArgumentException {
    @SuppressWarnings("unchecked")
    Map<String, Iterable<String>> options = (Map<String, Iterable<String>>) _defaults.clone();
    
    List<String> params = new LinkedList<String>();
    
    String currentOption = null;
    List<String> currentOptionArgs = null;
    int optionArgsCount = -1;
    boolean allowMoreOptions = true;
    
    for (String s : args) {
      if (currentOption != null && optionArgsCount == 0) {
        
        options.put(currentOption, IterUtil.asSizedIterable(currentOptionArgs));
        currentOption = null;
      }
      
      if (s.startsWith("-")) {
        String opt = s.substring(1);
        if (_aliases.containsKey(opt)) { opt = _aliases.get(opt); }
        if (optionArgsCount > 0) {
          throw new IllegalArgumentException("Expected " + optionArgsCount + " more argument(s) for option " + 
                                             currentOption);
        }
        else if (!allowMoreOptions) {
          throw new IllegalArgumentException("Unexpected option: " + opt);
        }
        else if (!_supportedOptions.containsKey(opt)) {
          throw new IllegalArgumentException("Unrecognized option: " + opt);
        }
        else {
          if (currentOption != null) {
            
            options.put(currentOption, IterUtil.asSizedIterable(currentOptionArgs));
          }
          currentOption = opt;
          currentOptionArgs = new LinkedList<String>();
          optionArgsCount = _supportedOptions.get(opt);
        }
      }
      
      else {
        if (currentOption == null) {
          if (_strictOrder) { allowMoreOptions = false; }
          params.add(s);
        }
        else {
          currentOptionArgs.add(s);
          if (optionArgsCount > 0) { optionArgsCount--; }
        }
      }
    }
    if (optionArgsCount > 0) {
      throw new IllegalArgumentException("Expected " + optionArgsCount + " more argument(s) for option " + 
                                         currentOption);
    }
    if (currentOption != null) {
      
      options.put(currentOption, IterUtil.asSizedIterable(currentOptionArgs));
    }
    
    if (params.size() < _requiredParams) {
      throw new IllegalArgumentException("Expected at least " + _requiredParams + " parameter(s)");
    }
    return new Result(options, params);
  }
      

  
  public static class Result {
    private final Map<String, Iterable<String>> _options;
    private final Iterable<String> _params;
    
    public Result(Map<String, Iterable<String>> options, Iterable<String> params) {
      _options = options;
      _params = params;
    }
    
    
    public Iterable<String> params() { return _params; }
    
    
    public boolean hasOption(String opt) { return _options.containsKey(opt); }
    
    
    public Iterable<String> getOption(String opt) { return _options.get(opt); }
    
    
    public boolean hasNullaryOption(String opt) {
      return _options.containsKey(opt) && IterUtil.isEmpty(_options.get(opt));
    }
    
    
    public String getUnaryOption(String opt) {
      Iterable<String> result = _options.get(opt);
      if (result == null || IterUtil.sizeOf(result) != 1) { return null; }
      else { return IterUtil.first(result); }
    }
    
    
    public Pair<String, String> getBinaryOption(String opt) {
      Iterable<String> result = _options.get(opt);
      if (result == null || IterUtil.sizeOf(result) != 2) { return null; }
      else { return IterUtil.makePair(result); }
    }
    
    
    public Triple<String, String, String> getTernaryOption(String opt) {
      Iterable<String> result = _options.get(opt);
      if (result == null || IterUtil.sizeOf(result) != 3) { return null; }
      else { return IterUtil.makeTriple(result); }
    }
    
    
    public Quad<String, String, String, String> getQuaternaryOption(String opt) {
      Iterable<String> result = _options.get(opt);
      if (result == null || IterUtil.sizeOf(result) != 4) { return null; }
      else { return IterUtil.makeQuad(result); }
    }
    
  }

}
