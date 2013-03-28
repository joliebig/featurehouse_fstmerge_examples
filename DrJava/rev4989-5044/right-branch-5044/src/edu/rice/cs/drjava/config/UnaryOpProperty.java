

package edu.rice.cs.drjava.config;

import edu.rice.cs.plt.lambda.Lambda;


public class UnaryOpProperty<P,R> extends EagerProperty {
  
  protected Lambda<P,R> _op;
  
  protected String _op1Name;
  
  protected String _op1Default;
  
  protected Lambda<String,P> _parse;
  
  protected Lambda<R,String> _format;
  
  
  public UnaryOpProperty(String name,
                         String help,
                         Lambda<P,R> op,
                         String op1Name,
                         String op1Default,
                         Lambda<String,P> parse,
                         Lambda<R,String> format) {
    super(name, help);
    _op = op;
    _op1Name = op1Name;
    _op1Default = op1Default;
    _parse = parse;
    _format = format;
    resetAttributes();
  }

  
  public UnaryOpProperty(String name,
                         String help,
                         Lambda<P,R> op,
                         Lambda<String,P> parse,
                         Lambda<R,String> format) {
    this(name, help, op, "op", null, parse, format);
  }
  
  
  public void update(PropertyMaps pm) {
    P op;
    if (_attributes.get(_op1Name) == null) {
      _value = "("+_name+" Error...)";
      return;
    }
    else {
      try {
        op = _parse.value(_attributes.get(_op1Name));
      }
      catch(Exception e) {
        _value = "("+_name+" Error...)";
        return;
      }
    }
    _value = _format.value(_op.value(op));
  }
  
  public void resetAttributes() {
    _attributes.clear();
    _attributes.put(_op1Name, _op1Default);
  }
  
  
  public static final Lambda<String,Double> PARSE_DOUBLE =
    new Lambda<String,Double>() {
    public Double value(String s) { return new Double(s); }
  };
  
  
  public static final Lambda<String,Boolean> PARSE_BOOL =
    new Lambda<String,Boolean>() {
    public Boolean value(String s) { return new Boolean(s); }
  };

  
  public static final Lambda<String,String> PARSE_STRING =
    new Lambda<String,String>() {
    public String value(String s) { return s; }
  };
  
  
  public static final Lambda<Boolean,String> FORMAT_BOOL = 
    new Lambda<Boolean,String>() {
    public String value(Boolean b) { return b.toString().toLowerCase(); }
  };
  
  
  public static final Lambda<Double,String> FORMAT_DOUBLE = 
    new Lambda<Double,String>() {
    public String value(Double d) {
      String s = d.toString();
      while(s.endsWith("0")) { s = s.substring(0, s.length()-1); }
      if (s.endsWith(".")) { s = s.substring(0, s.length()-1); }
      return s;
    }
  };    

  
  public static final Lambda<String,String> FORMAT_STRING = 
    new Lambda<String,String>() {
    public String value(String s) { return s; }
  };
} 
