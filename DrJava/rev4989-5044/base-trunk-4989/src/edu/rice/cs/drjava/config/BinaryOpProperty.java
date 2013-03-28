

package edu.rice.cs.drjava.config;

import edu.rice.cs.plt.lambda.Lambda;
import edu.rice.cs.plt.lambda.Lambda2;


public class BinaryOpProperty<P,Q,R> extends EagerProperty {
  
  protected Lambda2<P,Q,R> _op;
  
  protected String _op1Name;
  
  protected String _op1Default;
  
  protected String _op2Name;
  
  protected String _op2Default;
  
  protected Lambda<String, P> _parse1;
  
  protected Lambda<String, Q> _parse2;
  
  protected Lambda<R, String> _format;
  
  
  public BinaryOpProperty(String name,
                          String help,
                          Lambda2<P,Q,R> op,
                          String op1Name,
                          String op1Default,
                          Lambda<String,P> parse1,
                          String op2Name,
                          String op2Default,
                          Lambda<String,Q> parse2,
                          Lambda<R,String> format) {
    super(name, help);
    _op = op;
    _op1Name = op1Name;
    _op1Default = op1Default;
    _parse1 = parse1;
    _op2Name = op2Name;
    _op2Default = op2Default;
    _parse2 = parse2;
    _format = format;
    resetAttributes();
  }
  
  
  public BinaryOpProperty(String name,
                          String help,
                          Lambda2<P,Q,R> op,
                          Lambda<String,P> parse1,
                          Lambda<String,Q> parse2,
                          Lambda<R,String> format) {
    this(name,help,op,"op1",null,parse1,"op2",null,parse2,format);
  }
  
  
  public void update(PropertyMaps pm) {
    P op1;
    if (_attributes.get(_op1Name)==null) {
      _value = "("+_name+" Error...)";
      return;
    }
    else {
      try {
        op1 = _parse1.value(_attributes.get(_op1Name));
      }
      catch(Exception e) {
        _value = "("+_name+" Error...)";
        return;
      }
    }
    Q op2;
    if (_attributes.get(_op2Name)==null) {
      _value = "("+_name+" Error...)";
      return;
    }
    else {
      try {
        op2 = _parse2.value(_attributes.get(_op2Name));
      }
      catch(Exception ee) {
        _value = "("+_name+" Error...)";
        return;
      }
    }
    _value = _format.value(_op.value(op1,op2));
  }
  
  public void resetAttributes() {
    _attributes.clear();
    _attributes.put(_op1Name, _op1Default);
    _attributes.put(_op2Name, _op2Default);
  }
} 
