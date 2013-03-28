

package edu.rice.cs.drjava.config;

import edu.rice.cs.plt.lambda.Lambda4;
import edu.rice.cs.plt.lambda.Lambda;


public class QuaternaryOpProperty<N,O,P,Q,R> extends EagerProperty {
  
  protected Lambda4<N,O,P,Q,R> _op;
  
  protected String _op1Name;
  
  protected String _op1Default;
  
  protected String _op2Name;
  
  protected String _op2Default;
  
  protected String _op3Name;
  
  protected String _op3Default;
  
  protected String _op4Name;
  
  protected String _op4Default;
  
  protected Lambda<String,N> _parse1;
  
  protected Lambda<String,O> _parse2;
  
  protected Lambda<String,P> _parse3;
  
  protected Lambda<String,Q> _parse4;
  
  protected Lambda<R,String> _format;
  
  
  public QuaternaryOpProperty(String name,
                              String help,
                              Lambda4<N,O,P,Q,R> op,
                              String op1Name,
                              String op1Default,
                              Lambda<String,N> parse1,
                              String op2Name,
                              String op2Default,
                              Lambda<String,O> parse2,
                              String op3Name,
                              String op3Default,
                              Lambda<String,P> parse3,
                              String op4Name,
                              String op4Default,
                              Lambda<String,Q> parse4,
                              Lambda<R,String> format) {
    super(name, help);
    _op = op;
    _op1Name = op1Name;
    _op1Default = op1Default;
    _parse1 = parse1;
    _op2Name = op2Name;
    _op2Default = op2Default;
    _parse2 = parse2;
    _op3Name = op3Name;
    _op3Default = op3Default;
    _parse3 = parse3;
    _op4Name = op4Name;
    _op4Default = op4Default;
    _parse4 = parse4;
    _format = format;
    resetAttributes();
  }

  
  public QuaternaryOpProperty(String name,
                              String help,
                              Lambda4<N,O,P,Q,R> op,
                              Lambda<String,N> parse1,
                              Lambda<String,O> parse2,
                              Lambda<String,P> parse3,
                              Lambda<String,Q> parse4,
                              Lambda<R,String> format) {
    this(name,help,op,"op1",null,parse1,"op2",null,parse2,"op3",null,parse3,"op4",null,parse4,format);
  }
  
  
  public void update(PropertyMaps pm) {
    N op1;
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
    O op2;
    if (_attributes.get(_op2Name)==null) {
      _value = "("+_name+" Error...)";
      return;
    }
    else {
      try {
        op2 = _parse2.value(_attributes.get(_op2Name));
      }
      catch(Exception e) {
        _value = "("+_name+" Error...)";
        return;
      }
    }
    P op3;
    if (_attributes.get(_op3Name)==null) {
      _value = "("+_name+" Error...)";
      return;
    }
    else {
      try {
        op3 = _parse3.value(_attributes.get(_op3Name));
      }
      catch(Exception e) {
        _value = "("+_name+" Error...)";
        return;
      }
    }
    Q op4;
    if (_attributes.get(_op4Name)==null) {
      _value = "("+_name+" Error...)";
      return;
    }
    else {
      try {
        op4 = _parse4.value(_attributes.get(_op4Name));
      }
      catch(Exception ee) {
        _value = "("+_name+" Error...)";
        return;
      }
    }
    _value = _format.value(_op.value(op1,op2,op3,op4));
  }
  
  public void resetAttributes() {
    _attributes.clear();
    _attributes.put(_op1Name, _op1Default);
    _attributes.put(_op2Name, _op2Default);
    _attributes.put(_op3Name, _op3Default);
    _attributes.put(_op4Name, _op4Default);
  }
} 
