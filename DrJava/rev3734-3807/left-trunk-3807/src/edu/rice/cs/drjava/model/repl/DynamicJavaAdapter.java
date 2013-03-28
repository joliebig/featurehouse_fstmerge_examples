

package edu.rice.cs.drjava.model.repl;

import java.util.List;
import java.io.StringReader;
import java.io.Reader;
import java.net.URL;
import edu.rice.cs.drjava.model.repl.newjvm.ClassPathManager;
import koala.dynamicjava.interpreter.*;
import koala.dynamicjava.interpreter.context.*;
import koala.dynamicjava.interpreter.error.*;
import koala.dynamicjava.interpreter.throwable.*;
import koala.dynamicjava.parser.wrapper.*;
import koala.dynamicjava.tree.*;

import edu.rice.cs.util.classloader.StickyClassLoader;
import edu.rice.cs.util.*;






public class DynamicJavaAdapter implements JavaInterpreter {
  private InterpreterExtension _djInterpreter;

  ClassPathManager cpm;
  
   
  public DynamicJavaAdapter(ClassPathManager c) {
    cpm = c;
    _djInterpreter = new InterpreterExtension(c);
  }

  
  public Object interpret(String s) throws ExceptionReturnedException {
    boolean print = false;
    
    
    s = s.trim();
    if (!s.endsWith(";")) {
      
      print = true;
    }

    StringReader reader = new StringReader(s);
    try {
      Object result = _djInterpreter.interpret(reader, "DrJava");
      if (print) return result;
      else return JavaInterpreter.NO_RESULT;
    }
    catch (InterpreterException ie) {
      Throwable cause = ie.getException();
      if (cause instanceof ThrownException) cause = ((ThrownException) cause).getException();
      else if (cause instanceof CatchedExceptionError) cause = ((CatchedExceptionError) cause).getException();

      throw new ExceptionReturnedException(cause);
    }
    catch (CatchedExceptionError cee) {
      throw new ExceptionReturnedException(cee.getException());
    }
    catch (InterpreterInterruptedException iie) {
      return JavaInterpreter.NO_RESULT;
    }
    catch (ExitingNotAllowedException enae) {
      return JavaInterpreter.NO_RESULT;
    }









  }

  public List<Node> parse(String input) { return _djInterpreter.parse(input); }

  





  public void addProjectClassPath(URL path) { cpm.addProjectCP(path); }

  public void addBuildDirectoryClassPath(URL path) { cpm.addBuildDirectoryCP(path); }

  public void addProjectFilesClassPath(URL path) { cpm.addProjectFilesCP(path); }

  public void addExternalFilesClassPath(URL path) { cpm.addExternalFilesCP(path); }
  
  public void addExtraClassPath(URL path) { cpm.addExtraCP(path); }
  
  
  public void setPackageScope(String packageName) {
    StringReader reader = new StringReader("package " + packageName + ";");
    _djInterpreter.interpret(reader, "DrJava");
  }

  
  public Object getVariable(String name) { return _djInterpreter.getVariable(name); }

  
  public Class<?> getVariableClass(String name) { return _djInterpreter.getVariableClass(name); }

  
  public void defineVariable(String name, Object value, Class<?> type) {
    if (type == null) type = java.lang.Object.class;
    ((TreeInterpreter)_djInterpreter).defineVariable(name, value, type);
  }

  
  public void defineVariable(String name, Object value) {
    ((TreeInterpreter)_djInterpreter).defineVariable(name, value);
  }

  
  public void defineVariable(String name, boolean value) {
    ((TreeInterpreter)_djInterpreter).defineVariable(name, value);
  }

  
  public void defineVariable(String name, byte value) {
    ((TreeInterpreter)_djInterpreter).defineVariable(name, value);
  }

  
  public void defineVariable(String name, char value) {
    ((TreeInterpreter)_djInterpreter).defineVariable(name, value);
  }

  
  public void defineVariable(String name, double value) {
    ((TreeInterpreter)_djInterpreter).defineVariable(name, value);
  }

  
  public void defineVariable(String name, float value) {
    ((TreeInterpreter)_djInterpreter).defineVariable(name, value);
  }


  
  public void defineVariable(String name, int value) {
    ((TreeInterpreter)_djInterpreter).defineVariable(name, value);
  }

  
  public void defineVariable(String name, long value) {
    ((TreeInterpreter)_djInterpreter).defineVariable(name, value);
  }

  
  public void defineVariable(String name, short value) {
    ((TreeInterpreter)_djInterpreter).defineVariable(name, value);
  }

  
  public void defineConstant(String name, Object value) {
    _djInterpreter.defineConstant(name, value);
  }

  
  public void defineConstant(String name, boolean value) {
    _djInterpreter.defineConstant(name, value);
  }

  
  public void defineConstant(String name, byte value) {
    _djInterpreter.defineConstant(name, value);
  }

  
  public void defineConstant(String name, char value) {
    _djInterpreter.defineConstant(name, value);
  }

  
  public void defineConstant(String name, double value) {
    _djInterpreter.defineConstant(name, value);
  }

  
  public void defineConstant(String name, float value) {
    _djInterpreter.defineConstant(name, value);
  }

  
  public void defineConstant(String name, int value) {
    _djInterpreter.defineConstant(name, value);
  }

  
  public void defineConstant(String name, long value) {
    _djInterpreter.defineConstant(name, value);
  }
  
  public void defineConstant(String name, short value) {
    _djInterpreter.defineConstant(name, value);
  }

  
  public void setPrivateAccessible(boolean accessible) {
    _djInterpreter.setAccessible(accessible);
  }

  
  public NameVisitor makeNameVisitor(Context nameContext) { return new NameVisitor(nameContext); }

  






  
  public EvaluationVisitor makeEvaluationVisitor(Context context) {
    return new EvaluationVisitorExtension(context);
  }

  
  public Node processTree(Node node) { return node; }

  public GlobalContext makeGlobalContext(TreeInterpreter i) { return new GlobalContext(i); }

  
  public class InterpreterExtension extends TreeInterpreter {

    
    public InterpreterExtension(ClassPathManager cpm) {
      super(new JavaCCParserFactory());

      classLoader = new ClassLoaderExtension(this, cpm);
      
      
      nameVisitorContext = makeGlobalContext(this);
      ClassLoaderContainer clc = new ClassLoaderContainer() {
        public ClassLoader getClassLoader() { return classLoader; }
      };
      nameVisitorContext.setAdditionalClassLoaderContainer(clc);
      checkVisitorContext = makeGlobalContext(this);
      checkVisitorContext.setAdditionalClassLoaderContainer(clc);
      evalVisitorContext = makeGlobalContext(this);
      evalVisitorContext.setAdditionalClassLoaderContainer(clc);
      

    }

    
    public Object interpret(Reader r, String fname) throws InterpreterException {
      List<Node> statements;
      try {
        SourceCodeParser p = parserFactory.createParser(r, fname);
        statements = p.parseStream();

      } 
      catch (ParseError e) {
        
        
        throw new InterpreterException(e);
      }
      
      Object result = JavaInterpreter.NO_RESULT;
      
      nameVisitorContext.setRevertPoint();
      checkVisitorContext.setRevertPoint();
      evalVisitorContext.setRevertPoint();
      
      try {
        for (Node n : statements) {
          n = processTree(n);
          
          NameVisitor nv = makeNameVisitor(nameVisitorContext);
          Node o = n.acceptVisitor(nv);
          if (o != null) n = o;
          
          AbstractTypeChecker tc = AbstractTypeChecker.makeTypeChecker(checkVisitorContext);
          
          n.acceptVisitor(tc);
          
          evalVisitorContext.defineVariables(checkVisitorContext.getCurrentScopeVariables());
          
          EvaluationVisitor ev = makeEvaluationVisitor(evalVisitorContext);

          result = n.acceptVisitor(ev);

        }
      }
      catch (ExecutionError e) {
        
        
        nameVisitorContext.revert();
        checkVisitorContext.revert();
        evalVisitorContext.revert();
        
        
        throw new InterpreterException(e);
      }
      
      if (result instanceof String) return  "\"" + result + "\"";
      if (result instanceof Character) return "'" + result + "'";
      return result;
    }
    
    
    public void defineConstant(String name, Object value) {
      Class<?> c = (value == null) ? null : value.getClass();
      nameVisitorContext.defineConstant(name, c);
      checkVisitorContext.defineConstant(name, c);
      evalVisitorContext.defineConstant(name, value);
    }

    
    public void defineConstant(String name, boolean value) {
      Class<?> c = boolean.class;
      nameVisitorContext.defineConstant(name, c);
      checkVisitorContext.defineConstant(name, c);
      evalVisitorContext.defineConstant(name, Boolean.valueOf(value));
    }

    
    public void defineConstant(String name, byte value) {
      Class<?> c = byte.class;
      nameVisitorContext.defineConstant(name, c);
      checkVisitorContext.defineConstant(name, c);
      evalVisitorContext.defineConstant(name, new Byte(value));
    }

    
    public void defineConstant(String name, char value) {
      Class<?> c = char.class;
      nameVisitorContext.defineConstant(name, c);
      checkVisitorContext.defineConstant(name, c);
      evalVisitorContext.defineConstant(name, new Character(value));
    }

    
    public void defineConstant(String name, double value) {
      Class<?> c = double.class;
      nameVisitorContext.defineConstant(name, c);
      checkVisitorContext.defineConstant(name, c);
      evalVisitorContext.defineConstant(name, new Double(value));
    }

    
    public void defineConstant(String name, float value) {
      Class<?> c = float.class;
      nameVisitorContext.defineConstant(name, c);
      checkVisitorContext.defineConstant(name, c);
      evalVisitorContext.defineConstant(name, new Float(value));
    }

    
    public void defineConstant(String name, int value) {
      Class<?> c = int.class;
      nameVisitorContext.defineConstant(name, c);
      checkVisitorContext.defineConstant(name, c);
      evalVisitorContext.defineConstant(name, new Integer(value));
    }

    
    public void defineConstant(String name, long value) {
      Class<?> c = long.class;
      nameVisitorContext.defineConstant(name, c);
      checkVisitorContext.defineConstant(name, c);
      evalVisitorContext.defineConstant(name, new Long(value));
    }
    
    
    public void defineConstant(String name, short value) {
      Class<?> c = short.class;
      nameVisitorContext.defineConstant(name, c);
      checkVisitorContext.defineConstant(name, c);
      evalVisitorContext.defineConstant(name, new Short(value));
    }
  }

  
  public static class ClassLoaderExtension extends TreeClassLoader {
    
    
    
    
  private static boolean classLoaderCreated = false;
  
  private static StickyClassLoader _stickyLoader;
  
  
  ClassPathManager cpm;
  
  
  public ClassLoaderExtension(koala.dynamicjava.interpreter.Interpreter i, ClassPathManager c) {
    super(i);
    cpm = c;
    
    
    
    
    
    classLoader = new WrapperClassLoader(getClass().getClassLoader()); 
    
    
    
    
    
    String[] excludes = {
      "edu.rice.cs.drjava.model.repl.DynamicJavaAdapter$InterpreterExtension",
      "edu.rice.cs.drjava.model.repl.DynamicJavaAdapter$ClassLoaderExtension"
    };
    
    if (!classLoaderCreated) {
      _stickyLoader = new StickyClassLoader(this, 
                                            classLoader, 
                                            excludes);
      classLoaderCreated = true;
    }
    
    
  }
  
  
  public URL getResource(String name) {
    
    return cpm.getClassLoader().getResource(name);
    
  }
  
  protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException{
    Class<?> clazz;
    
    
    if (classes.containsKey(name)) clazz = (Class<?>) classes.get(name);
    else {
      try {
        clazz = _stickyLoader.loadClass(name);
      }
      catch (ClassNotFoundException e) {
        
        
        
        clazz = interpreter.loadClass(name);
      }
    }
    
    if (resolve) resolveClass(clazz);
    
    return clazz;
  }
  
  





























  















  }
}
