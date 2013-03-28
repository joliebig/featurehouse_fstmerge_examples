

package edu.rice.cs.drjava.model.repl;

import koala.dynamicjava.interpreter.error.*;
import koala.dynamicjava.interpreter.*;
import koala.dynamicjava.interpreter.context.*;
import koala.dynamicjava.tree.*;

import java.util.List;
import java.util.LinkedList;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.lang.reflect.*;

import edu.rice.cs.drjava.model.repl.newjvm.ClassPathManager;
import edu.rice.cs.util.UnexpectedException;


public class JavaDebugInterpreter extends DynamicJavaAdapter {

  
  protected final String _name;

  
  protected String _thisClassName;

  
  protected String _thisPackageName;

  
  protected IdentityVisitor _translationVisitor;

  
  public JavaDebugInterpreter(String name, String className) {
    super(new ClassPathManager());
    _name = name;
    setClassName(className);
    _translationVisitor = makeTranslationVisitor();
  }

  
  public Node processTree(Node node) { return node.acceptVisitor(_translationVisitor); }

  public GlobalContext makeGlobalContext(TreeInterpreter i) {
    return new GlobalContext(i) {
      public boolean exists(String name) {
        return (super.exists(name)) ||
          (_getObjectFieldAccessForField(name, this) != null) ||
          (_getStaticFieldAccessForField(name, this) != null) ||
          (_getReferenceTypeForField(name, this) != null);
      }
    };
  }

  
  private boolean hasAnonymous(String className) {
    StringTokenizer st = new StringTokenizer(className, "$");
    while (st.hasMoreElements()) {
      String currToken = st.nextToken();
      try {

          Integer.valueOf(currToken);
        return true;
      }
      catch(NumberFormatException nfe) {
        
      }
    }
    return false;
  }

  
  private String _getFullyQualifiedClassNameForThis() {
    String cName = _thisClassName;
    if (!_thisPackageName.equals("")) {
      cName = _thisPackageName + "." + cName;
    }
    return cName;
  }

  private Class<?> _loadClassForThis(Context context) {
    try {
      return context.lookupClass(_getFullyQualifiedClassNameForThis());
    }
    catch(ClassNotFoundException e) {
      throw new UnexpectedException(e);
    }
  }

  
  protected ObjectFieldAccess _getObjectFieldAccessForField(String field, Context context) {
    AbstractTypeChecker tc = makeTypeChecker(context);
    int numDollars = _getNumDollars(_thisClassName);

    
    if (hasAnonymous(_thisClassName)) {
      
      Class<?> c = _loadClassForThis(context);
      Field[] fields = c.getDeclaredFields();

      
      for (int i = 0; i < fields.length; i++) {
        if (fields[i].getName().startsWith("this$")) {
          String fieldName = fields[i].getName();
          int lastIndex = fieldName.lastIndexOf("$");
          numDollars = Integer.valueOf(fieldName.substring(lastIndex+1, fieldName.length())).intValue() + 1;
          break;
        }
      }
    }
    for (int i = 0; i <= numDollars; i++) {
      Expression expr = _buildObjectFieldAccess(i, numDollars);
      Expression newExpr = new ObjectFieldAccess(expr, field);
      try {
        
        tc.visit((ObjectFieldAccess) newExpr);
        return (ObjectFieldAccess) newExpr;
      }
      catch (ExecutionError e) {
        
        newExpr = new ObjectFieldAccess(expr, "val$" + field);
        try {
          
          tc.visit((ObjectFieldAccess)newExpr);
          return (ObjectFieldAccess)newExpr;
        }
        catch (ExecutionError e2) {
          
        }
      }
    }

    return null;
  }

  
  protected ObjectMethodCall _getObjectMethodCallForFunction(MethodCall method, Context context) {
    AbstractTypeChecker tc = makeTypeChecker(context);
    int numDollars = _getNumDollars(_thisClassName);
    String methodName = method.getMethodName();
    List<Expression> args = method.getArguments();

    
    if (hasAnonymous(_thisClassName)) {
      
      Class<?> c = _loadClassForThis(context);
      Field[] fields = c.getDeclaredFields();

      
      for (int i = 0; i < fields.length; i++) {
        if (fields[i].getName().startsWith("this$")) {
          String fieldName = fields[i].getName();
          int lastIndex = fieldName.lastIndexOf("$");
          numDollars = Integer.valueOf(fieldName.substring(lastIndex+1, fieldName.length())).intValue() + 1;
          break;
        }
      }
    }
    for (int i = 0; i <= numDollars; i++) {
      Expression expr = _buildObjectFieldAccess(i, numDollars);
      expr = new ObjectMethodCall(expr, methodName, args, null, 0, 0, 0, 0);
      try {
        
        tc.visit((ObjectMethodCall)expr);
        return (ObjectMethodCall)expr;
      }
      catch (ExecutionError e2) {
        
      }
    }
    return null;
  }

  
  protected StaticFieldAccess _getStaticFieldAccessForField(String field, Context context) {
    AbstractTypeChecker tc = makeTypeChecker(context);
    int numDollars = _getNumDollars(_thisClassName);
    String currClass = _getFullyQualifiedClassNameForThis();
    int index = currClass.length();
    
    for (int i = 0; i <= numDollars; i++) {
      currClass = currClass.substring(0, index);
      ReferenceType rt = new ReferenceType(currClass);
      StaticFieldAccess expr = new StaticFieldAccess(rt, field);
      try {
        
        tc.visit(expr);
        return expr;
      }
      catch (ExecutionError e2) {
        
        index = currClass.lastIndexOf("$");
      }
    }
    return null;
  }

  
  protected StaticMethodCall _getStaticMethodCallForFunction(MethodCall method, Context context) {
    AbstractTypeChecker tc = makeTypeChecker(context);
    int numDollars = _getNumDollars(_thisClassName);
    String methodName = method.getMethodName();
    List<Expression> args = method.getArguments();
    String currClass = _getFullyQualifiedClassNameForThis();
    int index = currClass.length();
    
    for (int i = 0; i <= numDollars; i++) {
      currClass = currClass.substring(0, index);
      ReferenceType rt = new ReferenceType(currClass);
      StaticMethodCall expr = new StaticMethodCall(rt, methodName, args);
      try {
        
        tc.visit(expr);
        return expr;
      }
      catch (ExecutionError e2) {
        
        index = currClass.lastIndexOf("$");
      }
    }
    return null;
  }

  
  protected ReferenceType _getReferenceTypeForField(String field, Context context) {
    AbstractTypeChecker tc = makeTypeChecker(context);
    int index = _indexOfWithinBoundaries(_getFullyQualifiedClassNameForThis(), field);
    if (index != -1) {
      
      
      
      int lastDollar = field.lastIndexOf("$");
      int lastDot = field.lastIndexOf(".");
      if (lastDollar != -1) {
        field = field.substring(lastDollar + 1, field.length());
      }
      else {
        if (lastDot != -1) {
          field = field.substring(lastDot + 1, field.length());
        }
      }
      LinkedList<IdentifierToken> list = new LinkedList<IdentifierToken>();
      StringTokenizer st = new StringTokenizer(_getFullyQualifiedClassNameForThis(), "$.");
      String currString = st.nextToken();
      while (!currString.equals(field)) {
        list.add(new Identifier(currString));
        currString = st.nextToken();
      }
      list.add(new Identifier(field));
      ReferenceType rt = new ReferenceType(list);
      try {
        
        tc.visit(rt);
        return rt;
      }
      catch (ExecutionError e) {
        return null;
      }
    }
    else {
      return null;
    }
  }


  
  protected void setClassName(String className) {
    int indexLastDot = className.lastIndexOf(".");
    if (indexLastDot == -1) {
      _thisPackageName = "";
    }
    else {
      _thisPackageName = className.substring(0,indexLastDot);
    }
    _thisClassName = className.substring(indexLastDot + 1, className.length());
  }

  
  protected QualifiedName _convertThisToName(ThisExpression node) {
    
    List<IdentifierToken> ids = new LinkedList<IdentifierToken>(); 
    ids.add(new Identifier("this", node.getBeginLine(), node.getBeginColumn(),
                           node.getEndLine(), node.getEndColumn()));
    return new QualifiedName(ids, node.getFilename(),
                             node.getBeginLine(), node.getBeginColumn(),
                             node.getEndLine(), node.getEndColumn());
  }

  
  protected Expression _convertThisToObjectFieldAccess(ThisExpression node) {
    String className = node.getClassName();
    int numToWalk = verifyClassName(className);
    int numDollars = _getNumDollars(_thisClassName);
    
    if (numToWalk == -1) {
      throw new ExecutionError("malformed.expression", node);
    }
    else {
      return _buildObjectFieldAccess(numToWalk, numDollars);
    }
  }

  
  protected ThisExpression buildUnqualifiedThis() {
    LinkedList<IdentifierToken> ids = new LinkedList<IdentifierToken>();
    return new ThisExpression(ids, "", 0, 0, 0, 0);
  }

  
  private Expression _buildObjectFieldAccess(int numToWalk, int numDollars) {
    if (numToWalk == 0) {
      return _convertThisToName(buildUnqualifiedThis());
    }
    else {
      return new ObjectFieldAccess(_buildObjectFieldAccess(numToWalk - 1, numDollars), "this$" + (numDollars - numToWalk));
    }
  }

  
  private int _indexOfWithinBoundaries(String string, String subString) {
    int index = string.indexOf(subString);
    if (index == -1) {
      return index;
    }
    
    else {
      
      if (((string.length() == subString.length() + index) ||
           (string.charAt(subString.length() + index) == '$'))
            &&
          
          ((index == 0) ||
           (string.charAt(index-1) == '$') ||
           (string.charAt(index-1) == '.'))) {
        return index;
      }
      else {
        return -1;
      }
    }
  }

  
  private int _getNumDollars(String className) {
    int numDollars = 0;
    int index = className.indexOf("$");
    while (index != -1) {
      numDollars++;
      index = className.indexOf("$", index + 1);
    }
    return numDollars;
  }

  
  protected int verifyClassName(String className) {
    boolean hasPackage = false;
    if (!_thisPackageName.equals("")) {
      int index = className.indexOf(_thisPackageName);
      if (index == 0) {
        hasPackage = true;
        
        index = _thisPackageName.length() + 1;
        if (index >= className.length()) {
          return -1;
        }
        
        className = className.substring(index, className.length());
      }
    }

    className = className.replace('.', '$');
    int indexWithBoundaries = _indexOfWithinBoundaries(_thisClassName, className);
    if ((hasPackage && indexWithBoundaries != 0) ||
        (indexWithBoundaries == -1)) {
      return -1;
    }
    else {
      return _getNumDollars(_thisClassName.substring(indexWithBoundaries + className.length()));
    }
  }

  
  protected Expression visitThis(ThisExpression node) {
    if (node.getClassName().equals("")) {
      return _convertThisToName(node);
    }
    else {
      return _convertThisToObjectFieldAccess(node);
    }
  }

  
  public IdentityVisitor makeTranslationVisitor() {
    return new IdentityVisitor() {
      public Node visit(ThisExpression node) {
        Expression e = visitThis(node);
        if (e instanceof QualifiedName) {
          return visit((QualifiedName)e);
        }
        else if (e instanceof ObjectFieldAccess) {
          return visit((ObjectFieldAccess)e);
        }
        else {
          throw new UnexpectedException(new IllegalArgumentException("Illegal type of Expression"));
        }
      }
    };
  }




























  
  public NameVisitor makeNameVisitor(final Context nameContext) {
    return new NameVisitor(nameContext) {
      
      
      
      
      
      
      
      
      
      
      
      
      
      
      
      
      
      
      
      
      
      
      
      
      public Node visit(QualifiedName node) {
        try {
          return super.visit(node);
        }
        catch(ExecutionError e) {
          
          
          List<IdentifierToken> ids = node.getIdentifiers();
          Iterator<IdentifierToken> iter = ids.iterator();
          StringBuffer fieldBuf = new StringBuffer(iter.next().image());
          while (iter.hasNext()) {
            IdentifierToken t = iter.next();
            fieldBuf.append('$').append(t.image());
          }
          String field = fieldBuf.toString();
          if (nameContext.isDefined("this")) {
            
            
            ObjectFieldAccess ofa = _getObjectFieldAccessForField(field, nameContext);
            if (ofa != null) return ofa;
          }
          else {
            
            StaticFieldAccess sfa = _getStaticFieldAccessForField(field, nameContext);
            if (sfa != null) return sfa;
            else {
              
              
              
              
              ReferenceType rt = _getReferenceTypeForField(field, nameContext);
              if (rt != null)  return rt;
            }
          }
          
          throw e;
        }
      }
      public Node visit(ObjectMethodCall node) {
        MethodCall method = (MethodCall) super.visit(node);
        
        
        if (method != null) {
          if (method instanceof StaticMethodCall) {
            return method;
          }
          
          else if (nameContext.isDefined("this")) {
            ObjectMethodCall omc = _getObjectMethodCallForFunction(method, nameContext);
            if (omc != null) {
              return omc;
            }
            else {
              return method;
            }
          }
          
          else {
            StaticMethodCall smc = _getStaticMethodCallForFunction(method, nameContext);
            if (smc != null) {
              return smc;
            }
            else {
              return method;
            }
          }
        }
        else {
          return null;
        }
      }
    };
  }

  
  public AbstractTypeChecker makeTypeChecker(final Context context) {
    if (Float.valueOf(System.getProperty("java.specification.version")) < 1.5) { 
      return new TypeChecker14(context) {
      
      public Class<?> visit(QualifiedName node) {
        String var = node.getRepresentation();
        if ("this".equals(var)) {
          
          
          
          
          
          Class<?> c = _loadClassForThis(context);
          node.setProperty(NodeProperties.TYPE, c);
          node.setProperty(NodeProperties.MODIFIER, context.getModifier(node));
          return c;
        }
        else return super.visit(node);
      }

      };
    }
    else {
      return new TypeChecker15(context) {
        
      public Class<?> visit(QualifiedName node) {
        String var = node.getRepresentation();
        if ("this".equals(var)) {
          
          
          
          
          
          Class<?> c = _loadClassForThis(context);
          node.setProperty(NodeProperties.TYPE, c);
          node.setProperty(NodeProperties.MODIFIER, context.getModifier(node));
          return c;
        }
        else return super.visit(node);
      }

      };
    }
  }
      
}
