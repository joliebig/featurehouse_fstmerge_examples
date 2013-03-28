package edu.rice.cs.dynamicjava.interpreter;

import java.io.StringReader;
import edu.rice.cs.plt.tuple.Option;
import edu.rice.cs.plt.tuple.Pair;
import edu.rice.cs.plt.lambda.WrappedException;

import koala.dynamicjava.tree.Node;
import koala.dynamicjava.interpreter.error.ExecutionError;
import koala.dynamicjava.parser.wrapper.JavaCCParser;
import koala.dynamicjava.parser.wrapper.ParseError;
import edu.rice.cs.dynamicjava.Options;

import static edu.rice.cs.plt.debug.DebugUtil.debug;


public class Interpreter {

  private final Options _opt;
  private TypeContext _typeContext;
  private RuntimeBindings _bindings;
  
  public Interpreter(Options opt, TypeContext typeContext, RuntimeBindings bindings) {
    _opt = opt;
    _typeContext = typeContext;
    _bindings = bindings;
    
    _opt.typeSystem();
    new JavaCCParser(new StringReader(""), _opt).parseStream();
  }
  
  public Interpreter(Options opt) {
    this(opt, new ImportContext(Interpreter.class.getClassLoader(), opt), RuntimeBindings.EMPTY);
  }
  
  public Interpreter(Options opt, ClassLoader loader) {
    this(opt, new ImportContext(loader, opt), RuntimeBindings.EMPTY);
  }
  
  public Option<Object> interpret(String code) throws InterpreterException {
    Iterable<Node> tree = parse(code);
    debug.logValue("Parse result", tree);
    TypeContext tcResult = typeCheck(tree);
    debug.log("Static phase successful");
    Pair<RuntimeBindings, Option<Object>> evalResult = evaluate(tree);
    
    
    
    
    
    
    
    
    _typeContext = tcResult;
    _bindings = evalResult.first();
    return evalResult.second();
  }
  
  private Iterable<Node> parse(String code) throws InterpreterException {
    try {
      return new JavaCCParser(new StringReader(code), _opt).parseStream();
    }
    catch (ParseError e) {
      throw new ParserException(e);
    }
  }
  
  private TypeContext typeCheck(Iterable<Node> tree) throws InterpreterException {
    try { return new StatementChecker(_typeContext, _opt).checkList(tree); }
    catch (ExecutionError e) { throw new CheckerException(e); }
  }
  
  private Pair<RuntimeBindings, Option<Object>> evaluate(Iterable<Node> tree) throws InterpreterException {
    try {
      StatementEvaluator.Result r = new StatementEvaluator(_bindings, _opt).evaluateSequence(tree);
      return Pair.make(r.bindings(), r.value());
    }
    catch (WrappedException e) {
      if (e.getCause() instanceof InterpreterException) { throw (InterpreterException) e.getCause(); }
      else { throw e; }
    }
  }
  
}
