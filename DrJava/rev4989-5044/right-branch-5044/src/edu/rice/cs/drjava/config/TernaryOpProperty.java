

package edu.rice.cs.drjava.config;

import edu.rice.cs.plt.lambda.Lambda3;
import edu.rice.cs.plt.lambda.Lambda;


public class TernaryOpProperty<O,P,Q,R> extends EagerProperty {
  
  protected Lambda3<O,P,Q,R> _op;
  
  protected String _op1Name;
  
  protected String _op1Default;
  
  protected String _op2Name;
  
  protected String _op2Default;
  
  protected String _op3Name;
  
  protected String _op3Default;
  
  protected Lambda<String,O> _parse1;
  
  protected Lambda<String,P> _parse2;
  
  protected Lambda<String,Q> _parse3;
  
  protected Lambda<R,String> _format;
  
  
  public TernaryOpProperty(String name,
                           String help,
                           Lambda3<O,P,Q,R> op,
                           String op1Name,
                           String op1Default,
                           Lambda<String,O> parse1,
                           String op2Name,
                           String op2Default,
                           Lambda<String,P> parse2,
                           String op3Name,
                           String op3Default,
                           Lambda<String,Q> parse3,
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
    _format = format;
    resetAttributes();
  }

  
  public TernaryOpProperty(String name,
                           String help,
                           Lambda3<O,P,Q,R> op,
                           Lambda<String,O> parse1,
                           Lambda<String,P> parse2,
                           Lambda<String,Q> parse3,
                           Lambda<R,String> format) {
    this(name,help,op,"op1",null,parse1,"op2",null,parse2,"op3",null,parse3,format);
  }
  
  
  public void update(PropertyMaps pm) {
    O op1;
    if (_attributes.get(_op1Name) == null) {
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
    P op2;
    if (_attributes.get(_op2Name) == null) {
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
    Q op3;
    if (_attributes.get(_op3Name) == null) {
      _value = "("+_name+" Error...)";
      return;
    }
    else {
      try {
        op3 = _parse3.value(_attributes.get(_op3Name));
      }
      catch(Exception ee) {
        _value = "("+_name+" Error...)";
        return;
      }
    }
    _value = _format.value(_op.value(op1,op2,op3));
  }
  
  public void resetAttributes() {
    _attributes.clear();
    _attributes.put(_op1Name, _op1Default);
    _attributes.put(_op2Name, _op2Default);
    _attributes.put(_op3Name, _op3Default);
  }
} 
