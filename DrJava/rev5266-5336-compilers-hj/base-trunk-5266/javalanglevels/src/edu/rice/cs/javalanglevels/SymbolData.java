
package edu.rice.cs.javalanglevels;

import edu.rice.cs.javalanglevels.tree.*;
import edu.rice.cs.javalanglevels.parser.JExprParser;
import java.util.*;

import junit.framework.TestCase;

import edu.rice.cs.plt.reflect.JavaVersion;


public class SymbolData extends TypeData {
  
  
  
  
  public static final SymbolData BOOLEAN_TYPE = new PrimitiveData("boolean") {
    
    
    public boolean isCastableTo(SymbolData castTo, JavaVersion version) {
      return isAssignableTo(castTo, version);
    }
    
    
    public boolean isAssignableTo(SymbolData toCheck, JavaVersion version) {
      if (toCheck == null) {return false;}
      if (LanguageLevelConverter.versionSupportsAutoboxing(version) && !toCheck.isPrimitiveType()) {
        SymbolData autoBoxMe = LanguageLevelConverter.symbolTable.get("java.lang.Boolean");
        return (autoBoxMe != null && autoBoxMe.isAssignableTo(toCheck, version));
      }
      return toCheck==BOOLEAN_TYPE;
    }
  };
  
  
  public static final SymbolData CHAR_TYPE = new PrimitiveData("char") {
    
    
    public boolean isCastableTo(SymbolData castTo, JavaVersion version) {
      return isAssignableTo(castTo, version) || castTo == SymbolData.SHORT_TYPE || castTo == SymbolData.BYTE_TYPE;
    }

    
    public boolean isAssignableTo(SymbolData assignTo, JavaVersion version) {
      if (assignTo == null) {return false;}
      if (LanguageLevelConverter.versionSupportsAutoboxing(version) && !assignTo.isPrimitiveType()) {
        SymbolData autoBoxMe = LanguageLevelConverter.symbolTable.get("java.lang.Character");
        return autoBoxMe != null && autoBoxMe.isAssignableTo(assignTo, version);
      }

      return assignTo==SymbolData.INT_TYPE || 
        assignTo==SymbolData.LONG_TYPE || 
        assignTo == SymbolData.FLOAT_TYPE || 
        assignTo == SymbolData.DOUBLE_TYPE || 
        assignTo == SymbolData.CHAR_TYPE;
    }
  };
  
  
  public static final SymbolData BYTE_TYPE = new PrimitiveData("byte"){
    
    
    public boolean isCastableTo(SymbolData castTo, JavaVersion version) {
      return isAssignableTo(castTo, version) || castTo == SymbolData.CHAR_TYPE;
    }

    
    public boolean isAssignableTo(SymbolData assignTo, JavaVersion version) {
      if (assignTo == null) return false;

      if (LanguageLevelConverter.versionSupportsAutoboxing(version) && !assignTo.isPrimitiveType()) {
        SymbolData autoBoxMe = LanguageLevelConverter.symbolTable.get("java.lang.Byte");
        return autoBoxMe != null && autoBoxMe.isAssignableTo(assignTo, version);
      }

      return assignTo == SymbolData.BYTE_TYPE || 
        assignTo == SymbolData.SHORT_TYPE || 
        assignTo == SymbolData.INT_TYPE || 
        assignTo == SymbolData.LONG_TYPE || 
        assignTo == SymbolData.FLOAT_TYPE || 
        assignTo == SymbolData.DOUBLE_TYPE;
    }
  };

  
  public static final SymbolData SHORT_TYPE = new PrimitiveData("short"){
    
    
    public boolean isCastableTo(SymbolData castTo, JavaVersion version) {
      return isAssignableTo(castTo, version) || castTo == SymbolData.CHAR_TYPE || castTo == SymbolData.BYTE_TYPE;
    }

    
    public boolean isAssignableTo(SymbolData assignTo, JavaVersion version) {
      if (assignTo == null) {return false;}

      if (LanguageLevelConverter.versionSupportsAutoboxing(version) && !assignTo.isPrimitiveType()) {
        SymbolData autoBoxMe = LanguageLevelConverter.symbolTable.get("java.lang.Short");
        return autoBoxMe != null && autoBoxMe.isAssignableTo(assignTo, version);
      }
      
      return assignTo==SymbolData.SHORT_TYPE || 
        assignTo == SymbolData.INT_TYPE || 
        assignTo == SymbolData.LONG_TYPE || 
        assignTo == SymbolData.FLOAT_TYPE || 
        assignTo == SymbolData.DOUBLE_TYPE; 
    }
  };
  
  
  public static final SymbolData INT_TYPE = new PrimitiveData("int"){
    
    
    public boolean isCastableTo(SymbolData castTo, JavaVersion version) {
      return isAssignableTo(castTo, version) || castTo == SymbolData.CHAR_TYPE || castTo == SymbolData.SHORT_TYPE || castTo == SymbolData.BYTE_TYPE;
    }

    
    public boolean isAssignableTo(SymbolData assignTo, JavaVersion version) {
      if (assignTo == null) {return false;}

      if (LanguageLevelConverter.versionSupportsAutoboxing(version) && !assignTo.isPrimitiveType()) {
        SymbolData autoBoxMe = LanguageLevelConverter.symbolTable.get("java.lang.Integer");
        return autoBoxMe != null && autoBoxMe.isAssignableTo(assignTo, version);
      }

      return assignTo == SymbolData.INT_TYPE || assignTo == SymbolData.LONG_TYPE || 
        assignTo == SymbolData.FLOAT_TYPE ||  assignTo == SymbolData.DOUBLE_TYPE;
    }
  };
  
  
  
  public static final SymbolData LONG_TYPE = new PrimitiveData("long"){
    
    
    public boolean isCastableTo(SymbolData castTo, JavaVersion version) {
      return isAssignableTo(castTo, version) || castTo == SymbolData.CHAR_TYPE || castTo == SymbolData.INT_TYPE || 
        castTo == SymbolData.SHORT_TYPE || castTo == SymbolData.BYTE_TYPE;
    }

    
    public boolean isAssignableTo(SymbolData assignTo, JavaVersion version) {
      if (assignTo == null) {return false;}

      if (LanguageLevelConverter.versionSupportsAutoboxing(version) && !assignTo.isPrimitiveType()) {
        SymbolData autoBoxMe = LanguageLevelConverter.symbolTable.get("java.lang.Long");
        return autoBoxMe != null && autoBoxMe.isAssignableTo(assignTo, version);
      }

      return assignTo == SymbolData.LONG_TYPE || 
        assignTo == SymbolData.FLOAT_TYPE || 
        assignTo == SymbolData.DOUBLE_TYPE;
        
    }
  };
  
  
  public static final SymbolData FLOAT_TYPE = new PrimitiveData("float"){
    
    
    public boolean isCastableTo(SymbolData castTo, JavaVersion version) {
      return isAssignableTo(castTo, version) || castTo == SymbolData.CHAR_TYPE || castTo == SymbolData.INT_TYPE || castTo == SymbolData.LONG_TYPE || castTo == SymbolData.SHORT_TYPE || castTo == SymbolData.BYTE_TYPE;
    }

    
    public boolean isAssignableTo(SymbolData assignTo, JavaVersion version) {
      if (assignTo == null) {return false;}

      if (LanguageLevelConverter.versionSupportsAutoboxing(version) && !assignTo.isPrimitiveType()) {
        SymbolData autoBoxMe = LanguageLevelConverter.symbolTable.get("java.lang.Float");
        return autoBoxMe != null && autoBoxMe.isAssignableTo(assignTo, version);
      }

      return assignTo == SymbolData.FLOAT_TYPE || assignTo == SymbolData.DOUBLE_TYPE;
    }
  };
  
  
  public static final SymbolData DOUBLE_TYPE = new PrimitiveData("double"){
    
    
    public boolean isCastableTo(SymbolData castTo, JavaVersion version) {
      return isAssignableTo(castTo, version) || castTo == SymbolData.CHAR_TYPE || castTo == SymbolData.INT_TYPE || castTo == SymbolData.LONG_TYPE || castTo == SymbolData.DOUBLE_TYPE || castTo == SymbolData.SHORT_TYPE || castTo == SymbolData.BYTE_TYPE;
    }

    
    public boolean isAssignableTo(SymbolData assignTo, JavaVersion version) {
      if (assignTo == null) { return false; }

      if (LanguageLevelConverter.versionSupportsAutoboxing(version) && ! assignTo.isPrimitiveType()) {
        SymbolData autoBoxMe = LanguageLevelConverter.symbolTable.get("java.lang.Double");
        return autoBoxMe != null && autoBoxMe.isAssignableTo(assignTo, version);
      }
      return assignTo == SymbolData.DOUBLE_TYPE; 
    }
  };
  
  
  public static final SymbolData VOID_TYPE = new PrimitiveData("void") {
  
    
    public boolean isCastableTo(SymbolData castTo, JavaVersion version) { return false; }
    
    
    public boolean isAssignableTo(SymbolData toCheck, JavaVersion version) { return this == toCheck; }
  };
  
  
  public static final SymbolData EXCEPTION = new SymbolData("exception") {
    
    public boolean isCastableTo(SymbolData castTo, JavaVersion version) { return false; }
    
    
    public boolean isAssignableTo(SymbolData toCheck, JavaVersion version) { return true; }
    
  };

  
  public static final SymbolData NOT_FOUND = new SymbolData("not found") {
  
    
    public boolean isCastableTo(SymbolData castTo, JavaVersion version) { return false; }
    
    
    public boolean isAssignableTo(SymbolData toCheck, JavaVersion version) { return false; }
  };
  
 
  public static final SymbolData NULL_TYPE = new SymbolData("null") {

    
    public boolean isCastableTo(SymbolData castTo, JavaVersion version) {
      return isAssignableTo(castTo, version);
    }

    
    public boolean isAssignableTo(SymbolData assignTo, JavaVersion version) {
      return (assignTo != null) && ! assignTo.isPrimitiveType();
    }
    
  };
  
  
  public static final SymbolData AMBIGUOUS_REFERENCE = new SymbolData("ambiguous reference") {
    
    public boolean isCastableTo(SymbolData castTo, JavaVersion version) { return false; }
    
    
    public boolean isAssignableTo(SymbolData toCheck, JavaVersion version) { return false; }
  };
  
  
  public static final SymbolData THIS_CONSTRUCTOR = new SymbolData("this constructor") {
    
    public boolean isCastableTo(SymbolData castTo, JavaVersion version) { return false; }
    
    
    public boolean isAssignableTo(SymbolData toCheck, JavaVersion version) { return false; }
  };
  
  
  public static final SymbolData SUPER_CONSTRUCTOR = new SymbolData("super constructor") {
    
    public boolean isCastableTo(SymbolData castTo, JavaVersion version) { return false; }
    
    
    public boolean isAssignableTo(SymbolData toCheck, JavaVersion version) { return false; }
  };

  
  static {
    ModifiersAndVisibility _publicMav = new ModifiersAndVisibility(SourceInfo.NO_INFO, new String[] {"public"});
    VOID_TYPE.setIsContinuation(false);    
    VOID_TYPE.setMav(_publicMav);
    NOT_FOUND.setIsContinuation(false);
    NOT_FOUND.setMav(_publicMav);
    EXCEPTION.setMav(_publicMav);
    EXCEPTION.setIsContinuation(false);
  }
  
  
  
  
  private boolean _isContinuation;
  
  
  private boolean _hasAutoGeneratedJunitImport;

  
  private TypeParameter[] _typeParameters;
  
  
  private LinkedList<MethodData> _methods;
  
  
  private SymbolData _superClass;
  
  
  private LinkedList<SymbolData> _interfaces;
  
  
  private LinkedList<SymbolData> _innerInterfaces;
  
  
  private boolean _isInterface;
  
  
  private String _package;
  
  
  private InstanceData _instanceData;
  
  
  private int _constructorNumber;
  
  
  private int _localClassNum;
  
  
  private int _anonymousInnerClassNum;
  
  
  public SymbolData(String name, ModifiersAndVisibility modifiersAndVisibility, TypeParameter[] typeParameters, 
                    SymbolData superClass, LinkedList<SymbolData> interfaces, Data outerData) {
    super(outerData);
    _name = name;
    _modifiersAndVisibility = modifiersAndVisibility;
    _typeParameters = typeParameters;
    _methods = new LinkedList<MethodData>();
    _superClass = superClass;
    _interfaces = interfaces;
    
    for (int i = 0; i < interfaces.size(); i++) { addEnclosingData(_interfaces.get(i)); }
    
    
    _enclosingData.addFirst(_superClass);
    _innerClasses = new LinkedList<SymbolData>();
    _innerInterfaces = new LinkedList<SymbolData>();
    _isContinuation = false;
    _hasAutoGeneratedJunitImport = false;
    _isInterface = false;
    _localClassNum = 0;
    _anonymousInnerClassNum = 0;
    _package = "";
    _constructorNumber = 0;
    _instanceData = new InstanceData(this);
  }

  
  public SymbolData(String name, ModifiersAndVisibility modifiersAndVisibility, TypeParameter[] typeParameters,
                    SymbolData superClass, LinkedList<SymbolData> interfaces, Data outerData, String pkg) {
    this(name, modifiersAndVisibility, typeParameters, superClass, interfaces, outerData);
    _package = pkg;
  }
  
  
  public SymbolData(String name, ModifiersAndVisibility modifiersAndVisibility, TypeParameter[] typeParameters,
                    LinkedList<SymbolData> interfaces, Data outerData) {
    this(name, modifiersAndVisibility, typeParameters, null, interfaces, outerData);
    _isInterface = true;
  }

  
  public SymbolData(String name) {
    super(null);
    _name = name;
    _modifiersAndVisibility = new ModifiersAndVisibility(SourceInfo.NO_INFO, new String[0]);
    _typeParameters = new TypeParameter[0];
    _methods = new LinkedList<MethodData>();
    _superClass = null;
    _interfaces = new LinkedList<SymbolData>();
    _innerClasses = new LinkedList<SymbolData>();
    _innerInterfaces = new LinkedList<SymbolData>();
    _isContinuation = true;
    _isInterface = false;
    _package = "";
    _instanceData = new InstanceData(this);
  }
  
  
  public boolean isPrimitiveType() { return false; }
  
  public String toString() {
    if (_isContinuation) return "? " + _name;
    return "!" + _name;
  }

  
   public boolean isAssignableTo(SymbolData assignTo, JavaVersion version) {
     if (assignTo != null) {
       if (assignTo.isPrimitiveType() && LanguageLevelConverter.versionSupportsAutoboxing(version)) { 
         SymbolData unboxedType = this.unbox();
         if (unboxedType == null) {return false;}
         else {return unboxedType.isAssignableTo(assignTo, version);}
       }
       
       else {
         return this.isSubClassOf(assignTo);
       }
      
     }
     return false;
   }
  
   
   private SymbolData unbox() {
     String name = getName();
     if (name.equals("java.lang.Integer")) {return SymbolData.INT_TYPE;}
     if (name.equals("java.lang.Character")) {return SymbolData.CHAR_TYPE;}
     if (name.equals("java.lang.Short")) {return SymbolData.SHORT_TYPE;}
     if (name.equals("java.lang.Byte")) {return SymbolData.BYTE_TYPE;}
     if (name.equals("java.lang.Float")) {return SymbolData.FLOAT_TYPE;}
     if (name.equals("java.lang.Double")) {return SymbolData.DOUBLE_TYPE;}
     if (name.equals("java.lang.Long")) {return SymbolData.LONG_TYPE;}
     if (name.equals("java.lang.Boolean")) {return SymbolData.BOOLEAN_TYPE;}
     return null;
   }

  
   public boolean isCastableTo(SymbolData castTo, JavaVersion version) {
     if (castTo != null) {
       if (castTo.isPrimitiveType()) { 
         if (LanguageLevelConverter.versionSupportsAutoboxing(version)) { 
           SymbolData unboxedType = this.unbox();
           if (unboxedType == null) {return false;}
           else {return unboxedType.isCastableTo(castTo, version);}
         }
         else {return false;} 
       }
       
       else if (!this.isInterface()) { 
         if (!castTo.isInterface()) {
           return this.isSubClassOf(castTo) || castTo.isSubClassOf(this);
         }
         
         else { 
           return (!castTo.hasModifier("final") || castTo.isSubClassOf(this));
         }
         
       }
       
       else { 
         
         if (!castTo.isInterface()) {
           return !castTo.hasModifier("final") || castTo.isSubClassOf(this);
         }
         
         else { 
           
           if (LanguageLevelConverter.versionSupportsAutoboxing(version)) {return true;} 
           for (MethodData md: this.getMethods()) {
             if (checkDifferentReturnTypes(md, castTo, false, version)) {
               
               return false;
             }
           }
           return true;
         }
       }
     }
     return false;
   }

   
   public boolean isSubClassOf(SymbolData superClass) {
    if (superClass == null) return false;
    if (this == superClass) return true;
    if (superClass.isInterface()) {
      Iterator<SymbolData> iter = _interfaces.iterator();
      while (iter.hasNext()) {
        SymbolData d = iter.next();
        if (d == null) {
          continue;
        }
        if (d == superClass) {
          return true;
        }
        if (d.isSubClassOf(superClass)) {
          return true;
        }
      }
    }

    if (_superClass != null) {
      return this._superClass.isSubClassOf(superClass);
    }
    return false;
  }
  
   
  public boolean isInnerClassOf(SymbolData outerClass, boolean stopAtStatic) {
    if (this == outerClass) {return true;}
    Data outerData = this.getOuterData();
    if (outerData == null) {return false;}
    if (stopAtStatic && this.hasModifier("static")) {return false;}
    return outerData.getSymbolData().isInnerClassOf(outerClass, stopAtStatic);
  }
  
  
  public boolean isInstanceType() { return false; }

 
  public SymbolData getSymbolData() { return this; }
  
  
  public InstanceData getInstanceData() { return _instanceData; }

  
  public void setInstanceData(InstanceData id) { _instanceData = id; }
  
  
  public String getPackage() { return _package; }
  
  
  public void setPackage(String pkg) { _package = pkg;  }
  
  
  public TypeParameter[] getTypeParameters() {
    return _typeParameters;
  }
  
  
  public void setTypeParameters(TypeParameter[] typeParameters) {
    _typeParameters = typeParameters;
  }
  
  
  public boolean isInterface() {
    return _isInterface;
  }
  
  public void setInterface(boolean ii) {
    _isInterface = ii;
  }
  
  
  public LinkedList<SymbolData> getInnerInterfaces() {
    return _innerInterfaces;
  }
  
  
  protected SymbolData getInnerClassOrInterfaceHelper(String nameToMatch, int firstIndexOfDot) {
    Iterator<SymbolData> iter = innerClassesAndInterfacesIterator();
    while (iter.hasNext()) {
      SymbolData sd = iter.next();
      String sdName = sd.getName();

      sdName = LanguageLevelVisitor.getUnqualifiedClassName(sdName);
      if (firstIndexOfDot == -1) {
        if (sdName.equals(nameToMatch))
          return sd;
      }
      else {
        if (sdName.equals(nameToMatch.substring(0, firstIndexOfDot))) {
          return sd.getInnerClassOrInterface(nameToMatch.substring(firstIndexOfDot + 1));
        }
      }
    }
    
    SymbolData result = null;
    SymbolData newResult = null;
    SymbolData privateResult = null;
    
    
    
    if (_superClass != null) {
      newResult = _superClass.getInnerClassOrInterfaceHelper(nameToMatch, firstIndexOfDot);
      if (newResult != null) {
        SymbolData outerPiece;
        if (firstIndexOfDot > 0) {
          outerPiece = _superClass.getInnerClassOrInterfaceHelper(nameToMatch.substring(0, firstIndexOfDot), -1);
        }
        else {
          outerPiece = newResult;
        }
        if (TypeChecker.checkAccessibility(outerPiece.getMav(), outerPiece, this)) {result = newResult;}
        else {privateResult = newResult;}
      }
    }
    
    
    
    for (SymbolData id: _interfaces) {
      newResult = id.getInnerClassOrInterfaceHelper(nameToMatch, firstIndexOfDot);
      if (newResult != null) {
        SymbolData outerPiece;
        if (firstIndexOfDot > 0) {
          outerPiece = _superClass.getInnerClassOrInterfaceHelper(nameToMatch.substring(0, firstIndexOfDot), -1);
        }
        else { outerPiece = newResult; }        
        if (TypeChecker.checkAccessibility(outerPiece.getMav(), outerPiece, this)) {
          if (result == null) {result = newResult;}
          else {return SymbolData.AMBIGUOUS_REFERENCE;}
        }
        else {privateResult = newResult;}
      }
    }
    if (result != null) {return result;}
    return privateResult;  
  }
  

  
  public Iterator<SymbolData> innerClassesAndInterfacesIterator() {
    return new Iterator<SymbolData>() {
      private Iterator<SymbolData> _first = _innerClasses.iterator();
      private Iterator<SymbolData> _second = _innerInterfaces.iterator();
      
      public boolean hasNext() { return _first.hasNext() || _second.hasNext(); }
      
      public SymbolData next() {
        if (_first.hasNext()) return _first.next();
        else return _second.next();
      }
      
      public void remove() { throw new UnsupportedOperationException(); }
    };
  }
  
  
  
  public void addInnerInterface(SymbolData innerInterface) { _innerInterfaces.addLast(innerInterface); }
  
  
  public int preincrementLocalClassNum() { return ++_localClassNum; }
  
  
  public void setAnonymousInnerClassNum(int i) { _anonymousInnerClassNum=i; }
  
  
  public int preincrementAnonymousInnerClassNum() { return ++_anonymousInnerClassNum; }
  
  
  public int getAnonymousInnerClassNum() { return _anonymousInnerClassNum; }
  
  
  public int postdecrementLocalClassNum() { return _localClassNum--; }
  
  
  public int postdecrementAnonymousInnerClassNum() { return _anonymousInnerClassNum--; }  
  
  
  public boolean addVar(VariableData var) {


    return super.addVar(var);
  }
  
  
  public boolean addVars(VariableData[] vars) {
   boolean success = true;
    for (int i = 0; i<vars.length; i++) {
      LinkedList<SymbolData> seen = new LinkedList<SymbolData>();
      if (!_repeatedName(vars[i], seen)) {
        if (!vars[i].isFinal()) {
        vars[i].gotValue();
        }
        _vars.addLast(vars[i]);
      }
      else {
        success = false;
      }
    }
    return success;
 }

  
  public boolean addFinalVars(VariableData[] vars) {
    boolean success = true;
    for (int i = 0; i<vars.length; i++) {
      LinkedList<SymbolData> seen = new LinkedList<SymbolData>();
      if (!_repeatedNameInHierarchy(vars[i], seen)) {
        vars[i].setFinal();
        _vars.addLast(vars[i]);
      }
      else {
        success = false;
      }
    }
    return success;
  }
  
  
  private boolean _repeatedName(VariableData vr, LinkedList<SymbolData> seen) {
    seen.addLast(this);
    Iterator<VariableData> iter = _vars.iterator();
    while (iter.hasNext()) {
      if (vr.getName().equals(iter.next().getName())) return true;
    }
    return false;
  }
  
  
  private boolean _repeatedNameInHierarchy (VariableData vr, LinkedList<SymbolData> seen) {
    seen.addLast(this);
    Iterator<VariableData> iter = _vars.iterator();
    while (iter.hasNext()) {
      if (vr.getName().equals(iter.next().getName())) return true;
    }
    
    
    if (_superClass != null && _superClass._repeatedNameInHierarchy(vr, seen)) return true;
    
    
    for (int i = 0; i<_interfaces.size(); i++) {
      if (_interfaces.get(i)._repeatedNameInHierarchy(vr, seen)) return true;
    }
    return false;
  }
  
  
  public LinkedList<MethodData> getMethods() { return _methods; }
  
  
  public boolean hasMethod(String name) {
    for (int i = 0; i < _methods.size(); i++) {
      MethodData currMd = _methods.get(i);
      if (currMd.getName().equals(name)) return true;
    }
    return false;
  }
  
  
  public MethodData getMethod(String name, TypeData[] paramTypes) {
    for (int i = 0; i < _methods.size(); i++) {
      MethodData currMd = _methods.get(i);
      if (currMd.getName().equals(name)) {
        if (paramTypes.length == currMd.getParams().length) {
          boolean match = true;
          for (int j = 0; j<paramTypes.length; j++) {
            if (paramTypes[j].getSymbolData() != 
                currMd.getParams()[j].getType().getSymbolData()) {match = false; break;}
          }
          if (match) {return currMd; }
        }
      }
    }
    return null;
  }
  
  
  public void setMethods(LinkedList<MethodData> methods) {
    _methods = methods;
  }
  
  
  public static MethodData repeatedSignature(LinkedList<MethodData> listOfMethods, MethodData method) {
    return repeatedSignature(listOfMethods, method, false);
  }
  
  
  
  public static MethodData repeatedSignature(LinkedList<MethodData> listOfMethods, MethodData method, boolean fromClassFile) {
    Iterator<MethodData> iter = listOfMethods.iterator();
    VariableData[] methodParams = method.getParams();
    while (iter.hasNext()) {
      boolean match = true;
      MethodData currMd = iter.next();
      
      if (currMd.getName().equals(method.getName()) && 
          (! fromClassFile || currMd.getReturnType() == method.getReturnType())) {
        VariableData[] currMdParams = currMd.getParams();
        if (currMdParams.length == methodParams.length) {
          for (int i = 0; i < currMdParams.length; i++) {
            if (currMdParams[i].getType() != methodParams[i].getType()) {
              match = false;
              break;
            }
          }
          if (match) return currMd;
        }
      }
    }
    return null;
  }
  
  

  
  boolean isBooleanType(JavaVersion version) {
    return this == BOOLEAN_TYPE || 
      (getName().equals("java.lang.Boolean") && LanguageLevelConverter.versionSupportsAutoboxing(version));
  }
  

  
  boolean isCharType(JavaVersion version) {
    return this == CHAR_TYPE || (getName().equals("java.lang.Character") && 
                                 LanguageLevelConverter.versionSupportsAutoboxing(version));
  }
    
  
  boolean isByteType(JavaVersion version) {
    return this == BYTE_TYPE || (getName().equals("java.lang.Byte") && 
                                 LanguageLevelConverter.versionSupportsAutoboxing(version));
  }
  
  
  boolean isShortType(JavaVersion version) {
    return this == SHORT_TYPE || (getName().equals("java.lang.Short") && 
                                  LanguageLevelConverter.versionSupportsAutoboxing(version));
  }
  
  
  boolean isIntType(JavaVersion version) {
    return this==INT_TYPE || (getName().equals("java.lang.Integer") && 
                              LanguageLevelConverter.versionSupportsAutoboxing(version));
  }
  
  
  boolean isLongType(JavaVersion version) {
    return this == LONG_TYPE || (this.getName().equals("java.lang.Long") && 
                                 LanguageLevelConverter.versionSupportsAutoboxing(version));
  }
  
  
  boolean isFloatType(JavaVersion version) {
    return this == FLOAT_TYPE || (getName().equals("java.lang.Float") && 
                                  LanguageLevelConverter.versionSupportsAutoboxing(version));
  }
  
  
  boolean isDoubleType(JavaVersion version) {
    return this == DOUBLE_TYPE || 
      (getName().equals("java.lang.Double") && LanguageLevelConverter.versionSupportsAutoboxing(version));
  }
  
  
  private static boolean _isCompatible(MethodData overwritten, MethodData overwriting) {
    if (overwritten.hasModifier("public")) { 
      return overwriting.hasModifier("public");
    }
    if (overwritten.hasModifier("protected")) { 
      return overwriting.hasModifier("protected") || overwriting.hasModifier("public");
    } 
    if (! overwritten.hasModifier("private")) { 
      return ! overwriting.hasModifier("private");
    }
    return true;
  }
  
  
  protected static boolean checkDifferentReturnTypes(MethodData md, SymbolData sd, JavaVersion version) {
      return checkDifferentReturnTypes(md, sd, true, version);
    }
  
  
  protected static boolean checkDifferentReturnTypes(MethodData md, SymbolData sd, boolean addError, JavaVersion version) {
    
    LinkedList<SymbolData> interfaces = sd.getInterfaces();    
    LinkedList<SymbolData> enclosingData = new LinkedList<SymbolData>();
    enclosingData.addAll(interfaces);
    SymbolData superClass = sd.getSuperClass();
    if (superClass != null) {
      enclosingData.add(superClass);
    }
    Iterator<SymbolData> iter = enclosingData.iterator();
    while (iter.hasNext()) {
      SymbolData currSd = iter.next();
      MethodData matchingMd = repeatedSignature(currSd.getMethods(), md);
      if (matchingMd != null) {
        if (matchingMd.hasModifier("private")) return false;
        boolean subclass = md.getReturnType().isSubClassOf(matchingMd.getReturnType());
        if (matchingMd.getReturnType() != md.getReturnType() && !(subclass && LanguageLevelConverter.versionIs15(version))) {
          StringBuffer methodSignature = new StringBuffer(md.getName() + "(");
          VariableData[] params = md.getParams();
          for (int i = 0; i < params.length; i++) {
            if (i > 0) {
              methodSignature.append(", ");
            }
            methodSignature.append(params[i].getType().getName());
          }
          methodSignature.append(")");
          String methodSigString = methodSignature.toString();
          
          if (addError) { 
            TypeChecker.errors.addLast(new Pair<String, JExpressionIF>(methodSigString + " in " + sd.getName() + 
                                                                       " cannot override " + methodSigString + " in " +
                                                                       currSd.getName() + 
                                                                       "; attempting to use different return types",
                                                                       md.getJExpression())); }
          return true;
        }
        
        if (! _isCompatible(matchingMd, md)) {  
          String access = "package";
          if (matchingMd.hasModifier("private")) access = "private";
          if (matchingMd.hasModifier("public")) access = "public";
          if (matchingMd.hasModifier("protected")) access = "protected";
          if (addError) {
            TypeChecker.errors.
              addLast(new Pair<String, JExpressionIF>(md.getName() + " in " + md.getSymbolData().getName() +
                                                      " cannot override " + matchingMd.getName() + " in " +
                                                      matchingMd.getSymbolData().getName() + 
                                                      ".  You are attempting to assign weaker access priviledges. In " +
                                                      matchingMd.getSymbolData().getName() + ", " + matchingMd.getName() +
                                                      " was " + access, md.getJExpression())); } 
          return true;
        }
      }
      else if (checkDifferentReturnTypes(md, currSd, version)) return true;
    }
    return false;
  }
  
  
  public String createUniqueMethodName(String methodName) {
    LinkedList<SymbolData> toCheck = new LinkedList<SymbolData>();
    toCheck.add(this);
    Set<String> names = new HashSet<String>();
    while(toCheck.size() > 0) {
      SymbolData sd = toCheck.removeFirst();
      LinkedList<MethodData> methods = sd.getMethods();
      for(MethodData md : methods) { names.add(md.getName()); }
      if (sd.getSuperClass() != null) { toCheck.add(sd.getSuperClass()); }
      toCheck.addAll(sd.getInterfaces());
      if (sd.getOuterData() != null) { toCheck.add(sd.getOuterData().getSymbolData()); }
    }
        
    String newName = methodName;
    int counter = 0;  
    while (names.contains(newName) && counter != -1) {
      newName = methodName + counter; counter++;
    }
    
    if (counter == -1) {throw new RuntimeException("Internal Program Error: Unable to rename method " + methodName + ".  All possible names were taken.  Please report this bug.");}

    return newName; 
  }
  
  
  
  private String _createErrorMessage(MethodData md) {
    StringBuffer message = new StringBuffer("In the class \"" + md.getSymbolData().getName() + 
                                            "\", you cannot have two methods with the same name: \"" + md.getName() + "\"");
    VariableData[] params = md.getParams();
    if (params.length > 0) {
      message.append(" and parameter type");
      if (params.length > 1) {
        message.append("s");
      }
      message.append(":");
    }
    for (int i = 0; i < params.length; i++) {
      if (i > 0) {
        message.append(",");
      }
      message.append(" " + params[i].getType().getName());
    }
    return message.toString();
  }
  
  
  public void addMethod(MethodData method) {
    
    if (repeatedSignature(_methods, method) != null) {
      LanguageLevelVisitor.errors.addLast(new Pair<String, JExpressionIF>(_createErrorMessage(method), 
                                                                        method.getJExpression()));
    }
    else {
        _methods.addLast(method);
    }
  }
  
  
  public void addMethod(MethodData method, boolean isAugmentedCode) {
    
    MethodData md = repeatedSignature(_methods, method);
    if (md != null) {
      LanguageLevelVisitor.errors.addLast(new Pair<String, JExpressionIF>("This method's signature conflicts with an automatically generated method's signature", 
                                                                        md.getJExpression()));
    }
    else {
        _methods.addLast(method);
    }
  }
  
  
  public void addMethod(MethodData method, boolean isAugmentedCode, boolean fromClassFile) {
    
    MethodData md = repeatedSignature(_methods, method, fromClassFile);
    if (md != null) {
      LanguageLevelVisitor.errors.addLast(new Pair<String, JExpressionIF>(_createErrorMessage(method), md.getJExpression()));
    }
    else {
      _methods.addLast(method);
    }
  }

  
  public SymbolData getSuperClass() {
    return _superClass;
  }
  
  
  public void setSuperClass(SymbolData superClass) {
    _superClass = superClass;
    addEnclosingData(superClass);
  }
  
  
  public LinkedList<SymbolData> getInterfaces() {
    return _interfaces;
  }
  
  
  public void addInterface(SymbolData interphace) {
    _interfaces.addLast(interphace);
    addEnclosingData(interphace);
  }
  
  
  public void setInterfaces(LinkedList<SymbolData> interfaces) {
    _interfaces = interfaces;
    for (int i = 0; i<interfaces.size(); i++) {
      addEnclosingData(interfaces.get(i));
    }
  }
  

  
  public void incrementConstructorCount() {
    _constructorNumber ++;
  }
  
  
  public void decrementConstructorCount() {
    _constructorNumber --;
  }
  
  
  public int getConstructorCount() {
    return _constructorNumber;
  }
  
  
  public boolean isContinuation() {
    return _isContinuation;
  }
  
  
  public void setIsContinuation(boolean isContinuation) {
    _isContinuation = isContinuation;
  }
  
  
  public boolean hasAutoGeneratedJunitImport() {
    return _hasAutoGeneratedJunitImport;
  }
  
  
  public void setHasAutoGeneratedJunitImport(boolean hasAutoGeneratedJunitImport) {
    _hasAutoGeneratedJunitImport = hasAutoGeneratedJunitImport;
  }

  
  public boolean isNumberTypeWithoutAutoboxing() {
    return (this == SymbolData.INT_TYPE ||
        this == SymbolData.DOUBLE_TYPE ||
        this == SymbolData.LONG_TYPE ||
        this == SymbolData.CHAR_TYPE ||
        this == SymbolData.FLOAT_TYPE ||
        this == SymbolData.SHORT_TYPE ||
        this == SymbolData.BYTE_TYPE);
  }
  
  
  public boolean isNumberType(JavaVersion version) {
    if (!LanguageLevelConverter.versionSupportsAutoboxing(version)) {
      return isNumberTypeWithoutAutoboxing();
    }
    if (this.isDoubleType(version) ||
        this.isFloatType(version) ||
        this.isLongType(version) ||
        this.isIntType(version) ||        
        this.isCharType(version) ||
        this.isShortType(version) ||
        this.isByteType(version)) {
      return true;
    }
    return false;
  }


  
  public boolean isNonFloatOrBooleanType(JavaVersion version) {
    if (!LanguageLevelConverter.versionSupportsAutoboxing(version)) {
      return isNonFloatOrBooleanTypeWithoutAutoboxing();
    }

    return (this.isIntType(version) ||
        this.isLongType(version) ||
        this.isCharType(version) ||
        this.isShortType(version) ||
        this.isByteType(version) ||
        this.isBooleanType(version));
  }

  
  public boolean isNonFloatOrBooleanTypeWithoutAutoboxing() {
    return (this==SymbolData.INT_TYPE ||
        this==SymbolData.LONG_TYPE ||
        this==SymbolData.CHAR_TYPE ||
        this==SymbolData.SHORT_TYPE ||
        this==SymbolData.BYTE_TYPE ||
        this==SymbolData.BOOLEAN_TYPE);
    
  }
  
  
  
  public boolean equals(Object obj) {
    if (obj == null) return false;
    if ((obj.getClass() != this.getClass())) { 
      return false;
    }
    SymbolData sd = (SymbolData) obj;    

    
      
    return this.isContinuation() == sd.isContinuation() && 
      this.getMav().equals(sd.getMav()) &&
      LanguageLevelVisitor.arrayEquals(this.getTypeParameters(), sd.getTypeParameters()) &&
      this.getMethods().equals(sd.getMethods()) &&
      this.getSuperClass() == sd.getSuperClass() && 
      this.getInterfaces().equals(sd.getInterfaces()) &&
      this.getOuterData() == sd.getOuterData() && 
      this.getInnerClasses().equals(sd.getInnerClasses()) &&
      this.getName().equals(sd.getName()) &&
      this.getInnerInterfaces().equals(sd.getInnerInterfaces()) &&
      this.getPackage().equals(sd.getPackage()) &&
      this.isInterface() == sd.isInterface();
 

  }
  
  
  public int hashCode() {
    return getName().hashCode();
  }
  
  
  
  public boolean implementsRunnable() {
   return this.getName().equals("java.lang.Thread") || this.getName().equals("java.util.TimerTask") || 
     this.getName().equals("javax.swing.text.AsyncBoxView$ChildState") || this.getName().equals("java.awt.image.renderable.RenderableImageProducer")
     || this.getName().equals("java.util.concurrent.FutureTask");
  }
  

  
  public boolean hasInterface(SymbolData i) {
    if (i==null) return false;

    if (getInterfaces().contains(i)) { return true; }
    
    if ((this.getSuperClass() != null) && this.getSuperClass().hasInterface(i)) { return true; }
    
    for (int j = 0; j<getInterfaces().size(); j++) {
      if (getInterfaces().get(j).hasInterface(i)) {return true;}
    }
    return false;
  }
  
  
  public LinkedList<VariableData> getAllSuperVars() {
    LinkedList<VariableData> myVars = new LinkedList<VariableData>();
    if (this.getSuperClass() != null) {
      myVars.addAll(this.getSuperClass().getVars());
      myVars.addAll(this.getSuperClass().getAllSuperVars());
    }
    for (int i = 0; i<getInterfaces().size(); i++) {
      myVars.addAll(this.getInterfaces().get(i).getVars());
      myVars.addAll(this.getInterfaces().get(i).getAllSuperVars());
    }
    return myVars;
  }
  
  
   
  public static class SymbolDataTest extends TestCase {
    
    private SymbolData _sd;
    
    private ModifiersAndVisibility _publicMav = new ModifiersAndVisibility(SourceInfo.NO_INFO, new String[] {"public"});
    private ModifiersAndVisibility _protectedMav = new ModifiersAndVisibility(SourceInfo.NO_INFO, new String[] {"protected"});
    private ModifiersAndVisibility _privateMav = new ModifiersAndVisibility(SourceInfo.NO_INFO, new String[] {"private"});
    private ModifiersAndVisibility _packageMav = new ModifiersAndVisibility(SourceInfo.NO_INFO, new String[0]);
    private ModifiersAndVisibility _abstractMav = new ModifiersAndVisibility(SourceInfo.NO_INFO, new String[] {"abstract"});
    private ModifiersAndVisibility _finalMav = new ModifiersAndVisibility(SourceInfo.NO_INFO, new String[] {"final"});
    private ModifiersAndVisibility _publicFinalMav = new ModifiersAndVisibility(SourceInfo.NO_INFO, new String[]{"public", "final"});
    
    public SymbolDataTest() {
      this("");
    }
    public SymbolDataTest(String name) {
      super(name);
    }
    
    public void setUp() {
      _sd = new SymbolData("i.like.monkey");
      LanguageLevelVisitor.errors = new LinkedList<Pair<String, JExpressionIF>>();
    }
    
    public void testRepeatedSignatures() {
      MethodData md = new MethodData("methodName", _publicMav, new TypeParameter[0], SymbolData.INT_TYPE, 
                                     new VariableData[] { new VariableData(SymbolData.CHAR_TYPE),
        new VariableData(SymbolData.BOOLEAN_TYPE)}, 
                                     new String[0],
                                     _sd,
                                     null);
      md.getParams()[0].setEnclosingData(md);
      md.getParams()[1].setEnclosingData(md);
      LinkedList<MethodData> mds = new LinkedList<MethodData>();
      mds.addFirst(md);
      
      MethodData md2 = new MethodData("methodName", _publicMav, new TypeParameter[0], SymbolData.INT_TYPE, 
                                      new VariableData[] { new VariableData(SymbolData.CHAR_TYPE),
        new VariableData(SymbolData.BOOLEAN_TYPE) }, 
                                      new String[0],
                                      _sd,
                                      null);
      md2.getParams()[0].setEnclosingData(md2);
      md2.getParams()[1].setEnclosingData(md2);
                                
      assertTrue("repeatedSignatures should return md exactly.", md == _sd.repeatedSignature(mds, md2));
      
      MethodData md3 = new MethodData("methodName", _publicMav, new TypeParameter[0], SymbolData.INT_TYPE, 
                                      new VariableData[] { new VariableData(SymbolData.CHAR_TYPE),
        new VariableData(SymbolData.BOOLEAN_TYPE) }, 
                                      new String[0],
                                      _sd,
                                      null);
      md3.getParams()[0].setEnclosingData(md3);
      md3.getParams()[1].setEnclosingData(md3);
                                
      assertTrue("repeatedSignatures should return md exactly.", md == _sd.repeatedSignature(mds, md3, true));
      
      MethodData md4 = new MethodData("methodName", _publicMav, new TypeParameter[0], SymbolData.SHORT_TYPE, 
                                      new VariableData[] { new VariableData(SymbolData.CHAR_TYPE),
        new VariableData(SymbolData.BOOLEAN_TYPE) }, 
                                      new String[0],
                                      _sd,
                                      null);      
      md4.getParams()[0].setEnclosingData(md4);
      md4.getParams()[1].setEnclosingData(md4);

                                      
      assertEquals("repeatedSignatures should return null.", null, _sd.repeatedSignature(mds, md4, true));
      
      MethodData md5 = new MethodData("methodName", _publicMav, new TypeParameter[0], SymbolData.SHORT_TYPE, 
                                      new VariableData[] { new VariableData(SymbolData.BOOLEAN_TYPE) }, 
                                      new String[0],
                                      _sd,
                                      null);
      md5.getParams()[0].setEnclosingData(md5);
                                    
      assertEquals("repeatedSignatures should return null.", null, _sd.repeatedSignature(mds, md5));
      
      MethodData md6= new MethodData("methodName", _publicMav, new TypeParameter[0], SymbolData.SHORT_TYPE, 
                                      new VariableData[] { new VariableData(SymbolData.BOOLEAN_TYPE),
        new VariableData(SymbolData.CHAR_TYPE) }, 
                                      new String[0],
                                      _sd,
                                      null);
      md6.getParams()[0].setEnclosingData(md6);
      md6.getParams()[1].setEnclosingData(md6);
                                      
      assertEquals("repeatedSignatures should return null.", null, _sd.repeatedSignature(mds, md6));
    }
    
    public void testCheckDifferentReturnTypes() {
      
      TypeChecker.errors = new LinkedList<Pair<String, JExpressionIF>>();
      
      
      SymbolData superSd = new SymbolData("superClass");
      MethodData md = new MethodData("methodName",
                                     _publicMav,
                                     new TypeParameter[0],
                                     SymbolData.INT_TYPE,
                                     new VariableData[0],
                                     new String[0],
                                     superSd,
                                     null);
      superSd.addMethod(md);
      _sd.setSuperClass(superSd);
      
      MethodData md2 = new MethodData("methodName",
                                      _publicMav,
                                      new TypeParameter[0],
                                      SymbolData.INT_TYPE,
                                      new VariableData[0],
                                      new String[0],
                                      _sd,
                                      null);
      assertFalse("There should not be a conflict.", checkDifferentReturnTypes(md2, _sd, JavaVersion.JAVA_5));
      assertEquals("There should not be an error.", 0, TypeChecker.errors.size());
      
      MethodData md3 = new MethodData("methodName",
                                      _publicMav,
                                      new TypeParameter[0],
                                      SymbolData.CHAR_TYPE,
                                      new VariableData[0],
                                      new String[0],
                                      _sd,
                                      null);
      assertTrue("There should be a conflict.", checkDifferentReturnTypes(md3, _sd, JavaVersion.JAVA_5));
      assertEquals("There should be one error.", 1, TypeChecker.errors.size());
      assertEquals("The error message should be correct.", 
                   "methodName() in i.like.monkey cannot override methodName() in superClass;" + 
                   " attempting to use different return types",
                   TypeChecker.errors.get(0).getFirst());
      
      SymbolData superSuperSd = new SymbolData("superSuperClass");
      MethodData md4 = new MethodData("superSuperMethodName",
                                      _publicMav,
                                      new TypeParameter[0],
                                      SymbolData.INT_TYPE,
                                      new VariableData[] { new VariableData(SymbolData.CHAR_TYPE) },
                                      new String[0],
                                      superSuperSd,
                                      null);
      md4.getParams()[0].setEnclosingData(md4);
      superSuperSd.addMethod(md4);
      superSd.setSuperClass(superSuperSd);
      
      MethodData md5 = new MethodData("superSuperMethodName",
                                      _publicMav,
                                      new TypeParameter[0],
                                      SymbolData.INT_TYPE,
                                      new VariableData[0],
                                      new String[0],
                                      null,
                                      null);
      assertFalse("There should not be a conflict.", checkDifferentReturnTypes(md5, _sd, JavaVersion.JAVA_5));
      assertEquals("There should still be one error.", 1, TypeChecker.errors.size());
      
      MethodData md6 = new MethodData("superSuperMethodName",
                                      _publicMav,
                                      new TypeParameter[0],
                                      SymbolData.BYTE_TYPE,
                                      new VariableData[] { new VariableData(SymbolData.CHAR_TYPE) },
                                      new String[0],
                                      null,
                                      null);
      md6.getParams()[0].setEnclosingData(md6);
      assertTrue("There should be a conflict.", checkDifferentReturnTypes(md6, _sd, JavaVersion.JAVA_5));
      assertEquals("There should be two errors.", 2, TypeChecker.errors.size());
      
      
      MethodData md7 = new MethodData("superSuperMethodName",
                                      _privateMav,
                                      new TypeParameter[0],
                                      SymbolData.INT_TYPE,
                                      new VariableData[] { new VariableData(SymbolData.CHAR_TYPE) },
                                      new String[0],
                                      new SymbolData("myData"),
                                      null);

      md7.getParams()[0].setEnclosingData(md7);
      assertTrue("There should be a conflict", checkDifferentReturnTypes(md7, _sd, JavaVersion.JAVA_5));
      assertEquals("There should be three errors", 3, TypeChecker.errors.size());
      assertEquals("The error message should be correct", 
                   "superSuperMethodName in myData cannot override superSuperMethodName in " + superSuperSd.getName() + 
                   ".  You are attempting to assign weaker access priviledges. In " + superSuperSd.getName() + 
                   ", superSuperMethodName was public", TypeChecker.errors.get(2).getFirst());
                                      
      
      
      SymbolData stringSd = new SymbolData("java.lang.String");
      stringSd.setIsContinuation(false);
      stringSd.setSuperClass(SymbolData.INT_TYPE);
      
      MethodData md8 = new MethodData("superSuperMethodName",
                                      _publicMav,
                                      new TypeParameter[0],
                                      stringSd,
                                      new VariableData[] {new VariableData(SymbolData.CHAR_TYPE)},
                                      new String[0],
                                      new SymbolData("myData"),
                                      null);
                                      
      assertFalse("There should be no conflict", checkDifferentReturnTypes(md8, _sd, JavaVersion.JAVA_5));
      assertEquals("There should still be 3 errors", 3, TypeChecker.errors.size());
      assertTrue("There should be a conflict in 1.4", checkDifferentReturnTypes(md8, _sd, JavaVersion.JAVA_1_4));
      assertEquals("There should now be 4 errors", 4, TypeChecker.errors.size());
      assertEquals("The error message should be correct", TypeChecker.errors.getLast().getFirst(), 
                   "superSuperMethodName(char) in superClass cannot override superSuperMethodName(char) in " + 
                   superSuperSd.getName() + "; attempting to use different return types");
    }
    
    public void test_createErrorMessage() {
      
      MethodData md = new MethodData("methodName",
                                     _publicMav,
                                     new TypeParameter[0],
                                     SymbolData.INT_TYPE,
                                     new VariableData[0],
                                     new String[0],
                                     _sd,
                                     null);
      assertEquals("The error message should be correct.", 
                   "In the class \"i.like.monkey\", you cannot have two methods with the same name: \"methodName\"", 
                   _sd._createErrorMessage(md));
      
      MethodData md2 = new MethodData("superSuperMethodName",
                                      _publicMav,
                                      new TypeParameter[0],
                                      SymbolData.INT_TYPE,
                                      new VariableData[] { new VariableData(SymbolData.CHAR_TYPE) },
                                      new String[0],
                                      _sd,
                                      null);
                                      assertEquals("The error message should be correct.", 
                                                   "In the class \"i.like.monkey\", you cannot have two methods with the same name: \"superSuperMethodName\" and parameter type: char", 
                                                   _sd._createErrorMessage(md2));
      md2.getParams()[0].setEnclosingData(md2);
      
      MethodData md3 = new MethodData("superSuperMethodName",
                                      _publicMav,
                                      new TypeParameter[0],
                                      SymbolData.INT_TYPE,
                                      new VariableData[] { new VariableData(SymbolData.CHAR_TYPE),
        new VariableData(SymbolData.CHAR_TYPE) },
                                      new String[0],
                                      _sd,
                                      null);
      md3.getParams()[0].setEnclosingData(md3);
      md3.getParams()[1].setEnclosingData(md3);
                                      
      assertEquals("The error message should be correct.", 
                   "In the class \"i.like.monkey\", you cannot have two methods with the same name: \"superSuperMethodName\" and parameter types: char, char", 
                   _sd._createErrorMessage(md3));
      
    }
    
    public void testAddMethod() {
      MethodData md = new MethodData("methodName", _publicMav, new TypeParameter[0], SymbolData.INT_TYPE, 
                                     new VariableData[] { new VariableData(SymbolData.CHAR_TYPE),
        new VariableData(SymbolData.BOOLEAN_TYPE)}, 
                                     new String[0],
                                     _sd,
                                     null);
                                     
      md.getParams()[0].setEnclosingData(md);
      md.getParams()[1].setEnclosingData(md);
                                     
      _sd.addMethod(md);
      assertEquals("There should be no errors.", 0, LanguageLevelVisitor.errors.size());
      
      MethodData md2 = new MethodData("methodName", _publicMav, new TypeParameter[0], SymbolData.INT_TYPE, 
                                      new VariableData[] { new VariableData(SymbolData.CHAR_TYPE),
        new VariableData(SymbolData.BOOLEAN_TYPE) }, 
                                      new String[0],
                                      _sd,
                                      null);
      md2.getParams()[0].setEnclosingData(md2);
      md2.getParams()[1].setEnclosingData(md2);
                                      
      _sd.addMethod(md2);
      assertEquals("There should be one error.", 1, LanguageLevelVisitor.errors.size());
      assertEquals("The error message should be correct.", "In the class \"" + _sd.getName() + 
                   "\", you cannot have two methods with the same name: \"" + md.getName() + "\" and parameter types: char, boolean",
                   LanguageLevelVisitor.errors.get(0).getFirst());
      MethodData md4 = new MethodData("methodName",
                                     _publicMav,
                                     new TypeParameter[0],
                                     SymbolData.INT_TYPE,
                                     new VariableData[0],
                                     new String[0],
                                     _sd,
                                     null);
      _sd.addMethod(md4);
      assertEquals("There should still be one error.", 1, LanguageLevelVisitor.errors.size());
      MethodData md5 = new MethodData("methodNamePlusStuff",
                                     _publicMav,
                                     new TypeParameter[0],
                                     SymbolData.INT_TYPE,
                                     new VariableData[0],
                                     new String[0],
                                     _sd,
                                     null);
      _sd.addMethod(md5);
      assertEquals("There should still be one error.", 1, LanguageLevelVisitor.errors.size());
      
      assertEquals("There are three methods in _sd.", 3, _sd.getMethods().size());
      assertEquals("The original method was added.", md, _sd.getMethods().get(0));
      assertEquals("md5 was added.", md5, _sd.getMethods().get(2));
      
      
      _sd.addMethod(md2, true);
      assertEquals("There should be two errors.", 2, LanguageLevelVisitor.errors.size());
      assertEquals("The error message should be correct.",
                   "This method's signature conflicts with an automatically generated method's signature",
                   LanguageLevelVisitor.errors.get(1).getFirst());
    }
    
    public void testIsNumberType() {
      assertTrue(SymbolData.INT_TYPE.isNumberType(JavaVersion.JAVA_5));
      assertTrue(new SymbolData("java.lang.Integer").isNumberType(JavaVersion.JAVA_5));
      assertTrue(SymbolData.DOUBLE_TYPE.isNumberType(JavaVersion.JAVA_5));
      assertTrue(new SymbolData("java.lang.Double").isNumberType(JavaVersion.JAVA_5));
      assertTrue(SymbolData.LONG_TYPE.isNumberType(JavaVersion.JAVA_5));
      assertTrue(new SymbolData("java.lang.Long").isNumberType(JavaVersion.JAVA_5));
      assertTrue(SymbolData.CHAR_TYPE.isNumberType(JavaVersion.JAVA_5));
      assertTrue(new SymbolData("java.lang.Character").isNumberType(JavaVersion.JAVA_5));
      assertTrue(SymbolData.FLOAT_TYPE.isNumberType(JavaVersion.JAVA_5));
      assertTrue(new SymbolData("java.lang.Float").isNumberType(JavaVersion.JAVA_5));
      assertTrue(SymbolData.SHORT_TYPE.isNumberType(JavaVersion.JAVA_5));
      assertTrue(new SymbolData("java.lang.Short").isNumberType(JavaVersion.JAVA_5));
      assertTrue(SymbolData.BYTE_TYPE.isNumberType(JavaVersion.JAVA_5));
      assertTrue(new SymbolData("java.lang.Byte").isNumberType(JavaVersion.JAVA_5));     
      assertFalse(SymbolData.BOOLEAN_TYPE.isNumberType(JavaVersion.JAVA_5));
      assertFalse(new SymbolData("java.lang.Boolean").isNumberType(JavaVersion.JAVA_5));
    }

    public void testIsNumberTypeWithoutAutoboxing() {
      assertTrue(SymbolData.INT_TYPE.isNumberTypeWithoutAutoboxing());
      assertFalse((new SymbolData("java.lang.Integer")).isNumberTypeWithoutAutoboxing());
      assertTrue(SymbolData.DOUBLE_TYPE.isNumberTypeWithoutAutoboxing());
      assertFalse((new SymbolData("java.lang.Double")).isNumberTypeWithoutAutoboxing());
      assertTrue(SymbolData.LONG_TYPE.isNumberTypeWithoutAutoboxing());
      assertFalse((new SymbolData("java.lang.Long")).isNumberTypeWithoutAutoboxing());
      assertTrue(SymbolData.CHAR_TYPE.isNumberTypeWithoutAutoboxing());
      assertFalse((new SymbolData("java.lang.Character")).isNumberTypeWithoutAutoboxing());
      assertTrue(SymbolData.FLOAT_TYPE.isNumberTypeWithoutAutoboxing());
      assertFalse((new SymbolData("java.lang.Float")).isNumberTypeWithoutAutoboxing());
      assertTrue(SymbolData.SHORT_TYPE.isNumberTypeWithoutAutoboxing());
      assertFalse((new SymbolData("java.lang.Short")).isNumberTypeWithoutAutoboxing());
      assertTrue(SymbolData.BYTE_TYPE.isNumberTypeWithoutAutoboxing());
      assertFalse((new SymbolData("java.lang.Byte")).isNumberTypeWithoutAutoboxing());     
      assertFalse(SymbolData.BOOLEAN_TYPE.isNumberTypeWithoutAutoboxing());
      assertFalse((new SymbolData("java.lang.Boolean")).isNumberTypeWithoutAutoboxing());
    }

    
    public void testIsIntType() {
      assertTrue(SymbolData.INT_TYPE.isIntType(JavaVersion.JAVA_5));
      assertTrue((new SymbolData("java.lang.Integer")).isIntType(JavaVersion.JAVA_5));
      assertFalse(SymbolData.DOUBLE_TYPE.isIntType(JavaVersion.JAVA_5));
      assertFalse((new SymbolData("java.lang.Double")).isIntType(JavaVersion.JAVA_5));
      assertFalse(SymbolData.LONG_TYPE.isIntType(JavaVersion.JAVA_5));
      assertFalse((new SymbolData("java.lang.Long")).isIntType(JavaVersion.JAVA_5));
      assertFalse(SymbolData.CHAR_TYPE.isIntType(JavaVersion.JAVA_5));
      assertFalse((new SymbolData("java.lang.Character")).isIntType(JavaVersion.JAVA_5));
      assertFalse(SymbolData.FLOAT_TYPE.isIntType(JavaVersion.JAVA_5));
      assertFalse((new SymbolData("java.lang.Float")).isIntType(JavaVersion.JAVA_5));
      assertFalse(SymbolData.SHORT_TYPE.isIntType(JavaVersion.JAVA_5));
      assertFalse((new SymbolData("java.lang.Short")).isIntType(JavaVersion.JAVA_5));
      assertFalse(SymbolData.BYTE_TYPE.isIntType(JavaVersion.JAVA_5));
      assertFalse((new SymbolData("java.lang.Byte")).isIntType(JavaVersion.JAVA_5));
      assertFalse(SymbolData.BOOLEAN_TYPE.isIntType(JavaVersion.JAVA_5));
      assertFalse((new SymbolData("java.lang.Boolean")).isIntType(JavaVersion.JAVA_5));
    }
    

    public void testIsDoubleType() {
      assertFalse(SymbolData.INT_TYPE.isDoubleType(JavaVersion.JAVA_5));
      assertFalse((new SymbolData("java.lang.Integer")).isDoubleType(JavaVersion.JAVA_5));
      assertTrue(SymbolData.DOUBLE_TYPE.isDoubleType(JavaVersion.JAVA_5));
      assertTrue((new SymbolData("java.lang.Double")).isDoubleType(JavaVersion.JAVA_5));
      assertFalse(SymbolData.CHAR_TYPE.isDoubleType(JavaVersion.JAVA_5));
      assertFalse((new SymbolData("java.lang.Character")).isDoubleType(JavaVersion.JAVA_5));
      assertFalse(SymbolData.FLOAT_TYPE.isDoubleType(JavaVersion.JAVA_5));
      assertFalse((new SymbolData("java.lang.Float")).isDoubleType(JavaVersion.JAVA_5));
      assertFalse(SymbolData.SHORT_TYPE.isDoubleType(JavaVersion.JAVA_5));
      assertFalse((new SymbolData("java.lang.Short")).isDoubleType(JavaVersion.JAVA_5));
      assertFalse(SymbolData.BYTE_TYPE.isDoubleType(JavaVersion.JAVA_5));
      assertFalse((new SymbolData("java.lang.Byte")).isDoubleType(JavaVersion.JAVA_5));
      assertFalse(SymbolData.BOOLEAN_TYPE.isDoubleType(JavaVersion.JAVA_5));
      assertFalse((new SymbolData("java.lang.Boolean")).isDoubleType(JavaVersion.JAVA_5));
    }
    
    public void testIsCharType() {
      assertFalse(SymbolData.INT_TYPE.isCharType(JavaVersion.JAVA_5));
      assertFalse((new SymbolData("java.lang.Integer")).isCharType(JavaVersion.JAVA_5));
      assertFalse(SymbolData.DOUBLE_TYPE.isCharType(JavaVersion.JAVA_5));
      assertFalse((new SymbolData("java.lang.Double")).isCharType(JavaVersion.JAVA_5));
      assertTrue(SymbolData.CHAR_TYPE.isCharType(JavaVersion.JAVA_5));
      assertTrue((new SymbolData("java.lang.Character")).isCharType(JavaVersion.JAVA_5));
      assertFalse(SymbolData.FLOAT_TYPE.isCharType(JavaVersion.JAVA_5));
      assertFalse((new SymbolData("java.lang.Float")).isCharType(JavaVersion.JAVA_5));
      assertFalse(SymbolData.SHORT_TYPE.isCharType(JavaVersion.JAVA_5));
      assertFalse((new SymbolData("java.lang.Short")).isCharType(JavaVersion.JAVA_5));
      assertFalse(SymbolData.BYTE_TYPE.isCharType(JavaVersion.JAVA_5));
      assertFalse((new SymbolData("java.lang.Byte")).isCharType(JavaVersion.JAVA_5));
      assertFalse(SymbolData.BOOLEAN_TYPE.isCharType(JavaVersion.JAVA_5));
      assertFalse((new SymbolData("java.lang.Boolean")).isCharType(JavaVersion.JAVA_5));
    }
    
    public void testIsFloatType() {
      assertFalse(SymbolData.INT_TYPE.isFloatType(JavaVersion.JAVA_5));
      assertFalse((new SymbolData("java.lang.Integer")).isFloatType(JavaVersion.JAVA_5));
      assertFalse(SymbolData.DOUBLE_TYPE.isFloatType(JavaVersion.JAVA_5));
      assertFalse((new SymbolData("java.lang.Double")).isFloatType(JavaVersion.JAVA_5));
      assertFalse(SymbolData.CHAR_TYPE.isFloatType(JavaVersion.JAVA_5));
      assertFalse((new SymbolData("java.lang.Character")).isFloatType(JavaVersion.JAVA_5));
      assertTrue(SymbolData.FLOAT_TYPE.isFloatType(JavaVersion.JAVA_5));
      assertTrue((new SymbolData("java.lang.Float")).isFloatType(JavaVersion.JAVA_5));
      assertFalse(SymbolData.SHORT_TYPE.isFloatType(JavaVersion.JAVA_5));
      assertFalse((new SymbolData("java.lang.Short")).isFloatType(JavaVersion.JAVA_5));
      assertFalse(SymbolData.BYTE_TYPE.isFloatType(JavaVersion.JAVA_5));
      assertFalse((new SymbolData("java.lang.Byte")).isFloatType(JavaVersion.JAVA_5));
      assertFalse(SymbolData.BOOLEAN_TYPE.isFloatType(JavaVersion.JAVA_5));
      assertFalse((new SymbolData("java.lang.Boolean")).isFloatType(JavaVersion.JAVA_5));
    }
    
    
    public void testIsByteType() {
      assertFalse(SymbolData.INT_TYPE.isByteType(JavaVersion.JAVA_5));
      assertFalse((new SymbolData("java.lang.Integer")).isByteType(JavaVersion.JAVA_5));
      assertFalse(SymbolData.DOUBLE_TYPE.isByteType(JavaVersion.JAVA_5));
      assertFalse((new SymbolData("java.lang.Double")).isByteType(JavaVersion.JAVA_5));
      assertFalse(SymbolData.CHAR_TYPE.isByteType(JavaVersion.JAVA_5));
      assertFalse((new SymbolData("java.lang.Character")).isByteType(JavaVersion.JAVA_5));
      assertFalse(SymbolData.FLOAT_TYPE.isByteType(JavaVersion.JAVA_5));
      assertFalse((new SymbolData("java.lang.Float")).isByteType(JavaVersion.JAVA_5));
      assertFalse(SymbolData.SHORT_TYPE.isByteType(JavaVersion.JAVA_5));
      assertFalse((new SymbolData("java.lang.Short")).isByteType(JavaVersion.JAVA_5));
      assertTrue(SymbolData.BYTE_TYPE.isByteType(JavaVersion.JAVA_5));
      assertTrue((new SymbolData("java.lang.Byte")).isByteType(JavaVersion.JAVA_5));
      assertFalse(SymbolData.BOOLEAN_TYPE.isByteType(JavaVersion.JAVA_5));
      assertFalse((new SymbolData("java.lang.Boolean")).isByteType(JavaVersion.JAVA_5));
    }
    
    public void testIsBooleanType() {
      assertFalse(SymbolData.INT_TYPE.isBooleanType(JavaVersion.JAVA_5));
      assertFalse((new SymbolData("java.lang.Integer")).isBooleanType(JavaVersion.JAVA_5));
      assertFalse(SymbolData.DOUBLE_TYPE.isBooleanType(JavaVersion.JAVA_5));
      assertFalse((new SymbolData("java.lang.Double")).isBooleanType(JavaVersion.JAVA_5));
      assertFalse(SymbolData.CHAR_TYPE.isBooleanType(JavaVersion.JAVA_5));
      assertFalse((new SymbolData("java.lang.Character")).isBooleanType(JavaVersion.JAVA_5));
      assertFalse(SymbolData.FLOAT_TYPE.isBooleanType(JavaVersion.JAVA_5));
      assertFalse((new SymbolData("java.lang.Float")).isBooleanType(JavaVersion.JAVA_5));
      assertFalse(SymbolData.SHORT_TYPE.isBooleanType(JavaVersion.JAVA_5));
      assertFalse((new SymbolData("java.lang.Short")).isBooleanType(JavaVersion.JAVA_5));
      assertFalse(SymbolData.BYTE_TYPE.isBooleanType(JavaVersion.JAVA_5));
      assertFalse((new SymbolData("java.lang.Byte")).isBooleanType(JavaVersion.JAVA_5));
      assertTrue(SymbolData.BOOLEAN_TYPE.isBooleanType(JavaVersion.JAVA_5));
      assertTrue((new SymbolData("java.lang.Boolean")).isBooleanType(JavaVersion.JAVA_5));
    }
    
    public void testIsNonFloatOrBooleanType() {
      assertTrue(SymbolData.INT_TYPE.isNonFloatOrBooleanType(JavaVersion.JAVA_5));
      assertTrue(new SymbolData("java.lang.Integer").isNonFloatOrBooleanType(JavaVersion.JAVA_5));
      assertFalse(SymbolData.DOUBLE_TYPE.isNonFloatOrBooleanType(JavaVersion.JAVA_5));
      assertFalse(new SymbolData("java.lang.Double").isNonFloatOrBooleanType(JavaVersion.JAVA_5));
      assertTrue(SymbolData.CHAR_TYPE.isNonFloatOrBooleanType(JavaVersion.JAVA_5));
      assertTrue(new SymbolData("java.lang.Character").isNonFloatOrBooleanType(JavaVersion.JAVA_5));
      assertFalse(SymbolData.FLOAT_TYPE.isNonFloatOrBooleanType(JavaVersion.JAVA_5));
      assertFalse(new SymbolData("java.lang.Float").isNonFloatOrBooleanType(JavaVersion.JAVA_5));
      assertTrue(SymbolData.SHORT_TYPE.isNonFloatOrBooleanType(JavaVersion.JAVA_5));
      assertTrue(new SymbolData("java.lang.Short").isNonFloatOrBooleanType(JavaVersion.JAVA_5));
      assertTrue(SymbolData.BYTE_TYPE.isNonFloatOrBooleanType(JavaVersion.JAVA_5));
      assertTrue(new SymbolData("java.lang.Byte").isNonFloatOrBooleanType(JavaVersion.JAVA_5));
      assertTrue(SymbolData.LONG_TYPE.isNonFloatOrBooleanType(JavaVersion.JAVA_5));
      assertTrue(new SymbolData("java.lang.Long").isNonFloatOrBooleanType(JavaVersion.JAVA_5));
      assertTrue(SymbolData.BOOLEAN_TYPE.isNonFloatOrBooleanType(JavaVersion.JAVA_5));
      assertTrue(new SymbolData("java.lang.Boolean").isNonFloatOrBooleanType(JavaVersion.JAVA_5));
      
    }
    
    public void testIsNonFloatOrBooleanTypeWithoutAutoboxing() {
      assertTrue(SymbolData.INT_TYPE.isNonFloatOrBooleanType(JavaVersion.JAVA_5));
      assertFalse(SymbolData.DOUBLE_TYPE.isNonFloatOrBooleanType(JavaVersion.JAVA_5));
      assertTrue(SymbolData.CHAR_TYPE.isNonFloatOrBooleanType(JavaVersion.JAVA_5));
      assertFalse(SymbolData.FLOAT_TYPE.isNonFloatOrBooleanType(JavaVersion.JAVA_5));
      assertTrue(SymbolData.SHORT_TYPE.isNonFloatOrBooleanType(JavaVersion.JAVA_5));
      assertTrue(SymbolData.BYTE_TYPE.isNonFloatOrBooleanType(JavaVersion.JAVA_5));
      assertTrue(SymbolData.LONG_TYPE.isNonFloatOrBooleanType(JavaVersion.JAVA_5));
      assertTrue(SymbolData.BOOLEAN_TYPE.isNonFloatOrBooleanType(JavaVersion.JAVA_5));
    }
    
    
    public void testEquals() {
      SymbolData superSd = new SymbolData("superClass");
      _sd = new SymbolData("i.like.monkey", _publicMav, new TypeParameter[0], superSd, new LinkedList<SymbolData>(), null);
      
      
      SymbolData _sd2 = new SymbolData("i.like.monkey", _publicMav, new TypeParameter[0], superSd, new LinkedList<SymbolData>(), null);
      assertTrue("Equals should return true if two SymbolDatas are equal", _sd.equals(_sd2));
      assertTrue("Equals should return true in opposite direction as well", _sd2.equals(_sd));

      
      assertFalse("Equals should return false if SymbolData is compared to null",_sd.equals(null));   
    
      
      _sd2 = new SymbolData("q", _publicMav, new TypeParameter[0], superSd, new LinkedList<SymbolData>(), null);
      assertFalse("Equals should return false if class names are different", _sd.equals(_sd2));
      
      
      _sd2 = new SymbolData("i.like.monkey", _protectedMav, new TypeParameter[0], superSd, new LinkedList<SymbolData>(), null);
      assertFalse("Equals should return false if class modifiers are different", _sd.equals(_sd2));
      
      
      _sd2 = new SymbolData("i.like.monkey", 
                            _publicMav, 
                            new TypeParameter[] { new TypeParameter(SourceInfo.NO_INFO, new TypeVariable(SourceInfo.NO_INFO,"tv"), 
                                                                    new TypeVariable(SourceInfo.NO_INFO,"i")) }, 
                            superSd, 
                            new LinkedList<SymbolData>(), null);
      assertFalse("Equals should return false if class type parameters are different", _sd.equals(_sd2));
      
      
      _sd2 = new SymbolData("i.like.monkey", _publicMav, new TypeParameter[0], null, new LinkedList<SymbolData>(), null);
      assertFalse("Equals should return false if super classes are different", _sd.equals(_sd2));
      
      
      LinkedList<SymbolData> interfaces = new LinkedList<SymbolData>();
      interfaces.addLast(SymbolData.INT_TYPE);
      _sd2 = new SymbolData("i.like.monkey", _publicMav, new TypeParameter[0], superSd, interfaces, null);
      assertFalse("Equals should return false if the interfaces are different", _sd.equals(_sd2));
    }
     
    public void testImplementsRunnable() {
      SymbolData sd1 = new SymbolData("java.lang.Thread");
      SymbolData sd2 = new SymbolData("java.util.TimerTask");
      SymbolData sd3 = new SymbolData("javax.swing.text.AsyncBoxView$ChildState");
      SymbolData sd4 = new SymbolData("java.awt.image.renderable.RenderableImageProducer");
      SymbolData sd5 = new SymbolData("java.util.concurrent.FutureTask");
      SymbolData sd6 = new SymbolData("java.util.Vector");
      assertTrue("Should implement Runnable", sd1.implementsRunnable());
      assertTrue("Should implement Runnable", sd2.implementsRunnable());
      assertTrue("Should implement Runnable", sd3.implementsRunnable());
      assertTrue("Should implement Runnable", sd4.implementsRunnable());
      assertTrue("Should implement Runnable", sd5.implementsRunnable());
      assertFalse("Should not implement Runnable", sd6.implementsRunnable());
    }
  
    public void testHasInterface() {
      
      
      SymbolData runnable = new SymbolData("java.lang.Runnable");
      runnable.setInterface(true);
      SymbolData subRunnable = new SymbolData("edgar");
      subRunnable.setInterface(true);
      subRunnable.addInterface(runnable);
      assertTrue(subRunnable.hasInterface(runnable));
      SymbolData subRunnable2 = new SymbolData("jones");
      subRunnable2.setInterface(true);
      subRunnable2.addInterface(runnable);
      assertTrue(subRunnable2.hasInterface(runnable));
      SymbolData someSd = new SymbolData("someSd");
      someSd.addInterface(subRunnable);      
      assertTrue(someSd.hasInterface(runnable));
      SymbolData someOtherSd = new SymbolData("someOtherSd");
      someOtherSd.addInterface(subRunnable2);
      assertTrue(someOtherSd.hasInterface(runnable));
      SymbolData notRunnable = new SymbolData("aksdf");
      assertFalse(notRunnable.hasInterface(runnable));
      someOtherSd.addInterface(notRunnable);
      assertTrue(someOtherSd.hasInterface(runnable));
      
      
      SymbolData myClass = new SymbolData("myClass");
      assertFalse(myClass.hasInterface(runnable));
      myClass.setSuperClass(someSd);
      assertTrue(myClass.hasInterface(runnable));
      SymbolData myClass2 = new SymbolData("myClass2");
      assertFalse(myClass2.hasInterface(runnable));
      myClass2.addInterface(someOtherSd);
      assertTrue(myClass2.hasInterface(runnable));
    }
    
    public void test_isAssignable() {
      MethodData md = new MethodData("Overwritten", _publicMav, new TypeParameter[0], _sd, new VariableData[0], new String[0], _sd, new NullLiteral(SourceInfo.NO_INFO));
      MethodData md2 = new MethodData("Overwriting", _publicMav, new TypeParameter[0], _sd, new VariableData[0], new String[0], _sd, new NullLiteral(SourceInfo.NO_INFO));

      
      assertTrue("Should be assignable", _isCompatible(md, md2));
      md.setMav(_protectedMav);
      assertTrue("Should be assignable", _isCompatible(md, md2));
      md.setMav(_privateMav);
      assertTrue("Should be assignable", _isCompatible(md, md2));
      md.setMav(_packageMav);
      assertTrue("Should be assignable", _isCompatible(md, md2));
      md2.setMav(_protectedMav);
      assertTrue("Should be assignable", _isCompatible(md, md2));
      md2.setMav(_privateMav);
      assertFalse("Should not be assignable", _isCompatible(md, md2));
      md2.setMav(_packageMav);
      assertTrue("Should be assignable", _isCompatible(md, md2));
      
    }
    
    
    public void testRepeatedNameInHierarchy() {
      SymbolData _d = new SymbolData("myname");
      
      VariableData vd = new VariableData("v1", _publicMav, SymbolData.INT_TYPE, false, _d);

      
      assertFalse("No variables to repeat name", _d._repeatedNameInHierarchy(vd, new LinkedList<SymbolData>()));
      
      
      _d.addVar(new VariableData("v2", _protectedMav, SymbolData.BOOLEAN_TYPE, true, _d));
      assertFalse("No repeated name", _d._repeatedNameInHierarchy(vd, new LinkedList<SymbolData>()));
      
      
      _d.addVar(vd);
      assertTrue("Should be repeated name", _d._repeatedNameInHierarchy(vd, new LinkedList<SymbolData>()));
      
      
      _d.setVars(new LinkedList<VariableData>());
      SymbolData superC = new SymbolData("I am a super class");
      superC.addVar(new VariableData(vd.getName(), _protectedMav, SymbolData.DOUBLE_TYPE, false, superC));
      _d.setSuperClass(superC);
      assertTrue("Should be repeated name", _d._repeatedNameInHierarchy(vd, new LinkedList<SymbolData>()));
      
      
      _d.setSuperClass(null);
      _d.addInterface(superC);
      assertTrue("Should also be repeated name", _d._repeatedNameInHierarchy(vd, new LinkedList<SymbolData>()));
    }
    
    public void testAddFinalVars() {
      SymbolData _d = new SymbolData("genius");
      VariableData vd = new VariableData("v1", _publicMav, SymbolData.INT_TYPE, true, _d);
      VariableData vd2 = new VariableData("v2", _publicMav, SymbolData.CHAR_TYPE, true, _d);
      VariableData[] toAdd = new VariableData[] {vd, vd2};
      LinkedList<VariableData> myVds = new LinkedList<VariableData>();
      
      
      myVds.addLast(vd);
      myVds.addLast(vd2);
      assertTrue("Should be able to add new vars array", _d.addFinalVars(toAdd));
      assertEquals("Variable list should have 2 variables", myVds, _d.getVars());
      
      
      assertFalse("Should not be able to add same variables again", _d.addFinalVars(toAdd));
      assertEquals("Variable list should not have changed", myVds, _d.getVars());
      assertTrue("vd should now be final", _d.getVars().get(0).hasModifier("final"));
      assertTrue("vd2 should now be final", _d.getVars().get(1).hasModifier("final"));

      
      
      
      VariableData vd3 = new VariableData("v3", _publicFinalMav, SymbolData.INT_TYPE, true, _d);
      VariableData[] toAdd2 = new VariableData[] {vd3};
      myVds.addLast(vd3);
      
      assertTrue("Should be able to add new variable array", _d.addFinalVars(toAdd2));
      assertEquals("Variable list should now have 3 variables", myVds, _d.getVars());
      assertTrue("vd3 should now be final", _d.getVars().get(2).hasModifier("final"));

      
      
      assertTrue("Should be able to add an empty array", _d.addFinalVars(new VariableData[0]));
      assertEquals("Variable list should not have changed by adding empty array", myVds, _d.getVars());
    }
    
    public void testGetAllVars() {
      SymbolData sd1 = new SymbolData("SubSub");
      SymbolData sd2 = new SymbolData("Sub");
      SymbolData sd3 = new SymbolData("Super");
      SymbolData sd4 = new SymbolData("SuperInterface");
      sd4.setInterface(true);
      SymbolData sd5 = new SymbolData("NotRelated");
      
      sd1.setSuperClass(sd2);
      sd2.setSuperClass(sd3);
      sd2.addInterface(sd4);

      VariableData vd1 = new VariableData("vd1", _finalMav, SymbolData.INT_TYPE, false, sd1);
      VariableData vd2 = new VariableData("vd2", _finalMav, SymbolData.INT_TYPE, false, sd2);
      VariableData vd3 = new VariableData("vd3", _finalMav, SymbolData.INT_TYPE, false, sd3);
      VariableData vd4 = new VariableData("vd4", _finalMav, SymbolData.INT_TYPE, false, sd4);
      VariableData vd5 = new VariableData("vd5", _finalMav, SymbolData.INT_TYPE, false, sd5);

      sd1.addVar(vd1);
      sd2.addVar(vd2);
      sd3.addVar(vd3);
      sd4.addVar(vd4);
      sd5.addVar(vd5);
      
      LinkedList<VariableData> shouldContain = new LinkedList<VariableData>();
      shouldContain.addLast(vd2);
      shouldContain.addLast(vd3);
      shouldContain.addLast(vd4);

      LinkedList<VariableData> vds = sd1.getAllSuperVars();
      assertEquals("the list should have 3 elements", 3, vds.size());
      assertEquals("The list should be correct", shouldContain, vds);
    }

    public void testIsSubclassOf() {
      _sd = new SymbolData("subClass");
      SymbolData superC = new SymbolData("superC");
      _sd._superClass = superC;
      
      
      assertFalse("subClass, int, and boolean are not related", _sd.isSubClassOf(SymbolData.BOOLEAN_TYPE));
      
      
      assertTrue("subClass is a subclass of itself", _sd.isSubClassOf(_sd));
      
      
      assertTrue("subClass is a subclass of its super class", _sd.isSubClassOf(superC));
      
      
      superC._superClass = SymbolData.CHAR_TYPE;
      assertTrue("subClass is a subclass of its super class's super class", _sd.isSubClassOf(SymbolData.CHAR_TYPE));
      
      
      SymbolData myData = new SymbolData("yes");
      SymbolData yourData = new SymbolData("interface");
      yourData.setInterface(true);
      myData.setIsContinuation(false);
      myData.addInterface(yourData);
      yourData.setIsContinuation(false);
      assertTrue("Should be assignable", myData.isSubClassOf(yourData));
    }

    public void testIsInnerClassOf() {
      _sd = new SymbolData("innerClass");
      SymbolData outer1 = new SymbolData("outer1");
      outer1.addInnerClass(_sd);
      _sd.setOuterData(outer1);
      SymbolData outer2 = new SymbolData("outer2");
      outer2.addInnerClass(outer1);
      outer1.setOuterData(outer2);
      outer2.setMav(new ModifiersAndVisibility(SourceInfo.NO_INFO, new String[] {"static"}));
      SymbolData outer3 = new SymbolData("outer3");
      outer3.addInnerClass(outer2);
      outer2.setOuterData(outer3);
      
      
      SymbolData notInChain = new SymbolData("no");
      
      assertTrue("_sd is an inner class of itself", _sd.isInnerClassOf(_sd, true));
      assertTrue("outer1 is the outer class of _sd", _sd.isInnerClassOf(outer1, true));
      assertTrue("outer2 is an outer class of _sd", _sd.isInnerClassOf(outer2, true));
      assertTrue("outer2 is an outer class of itself", outer2.isInnerClassOf(outer2, true));
      assertFalse("_sd is not related to notInChain", _sd.isInnerClassOf(notInChain, true));
      assertFalse("_sd is not an outer class of outer1", outer1.isInnerClassOf(_sd, true));
      assertFalse("outer3 cannot be seen from _sd if the static flag is true, because outer2 is static", _sd.isInnerClassOf(outer3, true));
      assertTrue("But, outer3 is an outer class of _sd", _sd.isInnerClassOf(outer3, false));
    }
    
    public void testCreateUniqueMethodName() {
      SymbolData george = new SymbolData("George");
      VariableData[] noVars = new VariableData[0];
      george.addMethod(new MethodData("getFriends", noVars));
      george.addMethod(new MethodData("sayHello", noVars));
      
      SymbolData iAteGeorge = new SymbolData("IAteGeorge");
      iAteGeorge.addMethod(new MethodData("sayHello", noVars));
      iAteGeorge.addMethod(new MethodData("sayHello0", noVars));
      iAteGeorge.addInnerClass(george);
      george.setOuterData(iAteGeorge);
      
      SymbolData mrsGeorge = new SymbolData("MrsGeorge");
      mrsGeorge.addMethod(new MethodData("sayHello1", noVars));
      george.setSuperClass(mrsGeorge);
      
      SymbolData grandmaGeorge = new SymbolData("GrandmaGeorge");
      grandmaGeorge.addMethod(new MethodData("sayHello2", noVars));
      grandmaGeorge.addMethod(new MethodData("sayHello5", noVars));
      george.addInterface(grandmaGeorge);
      
      SymbolData papaOfIAteGeorge = new SymbolData("PapaOfIAteGeorge");
      papaOfIAteGeorge.addMethod(new MethodData("sayHello3", noVars));
      iAteGeorge.setSuperClass(papaOfIAteGeorge);
      
      assertEquals("The generated name is correct when SymbolData has that method name",
                   "getFriends0", george.createUniqueMethodName("getFriends"));
      assertEquals("The generated name is correct when SymbolData doesn't have a method of that name",
                   "eatDinner", george.createUniqueMethodName("eatDinner"));
      assertEquals("The generated name is correct when a super class uses the name",
                   "sayHello10", george.createUniqueMethodName("sayHello1"));
      assertEquals("The generated name is correct when an outer class uses the name",
                   "sayHello00", george.createUniqueMethodName("sayHello0"));
      assertEquals("The generated name is correct when an interface uses the name",
                   "sayHello20", george.createUniqueMethodName("sayHello2"));
      assertEquals("The generated name is correct when a super class of an outer class uses the name",
                   "sayHello30", george.createUniqueMethodName("sayHello3"));
      assertEquals("The generated name is correct when a super class of an outer class uses the name",
                   "sayHello30", george.createUniqueMethodName("sayHello3"));
      assertEquals("The generated name is correct when a lot of things use the name",
                   "sayHello4", george.createUniqueMethodName("sayHello"));
      
    }
  }
}
