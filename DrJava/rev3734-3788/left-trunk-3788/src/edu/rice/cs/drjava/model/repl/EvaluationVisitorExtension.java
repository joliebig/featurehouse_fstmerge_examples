

package edu.rice.cs.drjava.model.repl;

import java.lang.reflect.*;
import koala.dynamicjava.interpreter.*;
import koala.dynamicjava.interpreter.context.*;
import koala.dynamicjava.interpreter.error.*;
import koala.dynamicjava.tree.*;



public class EvaluationVisitorExtension extends EvaluationVisitor {
  private Context _context;
  public EvaluationVisitorExtension(Context ctx) {
    super(ctx);
    _context = ctx;
  }

  private void _checkInterrupted(Node node) {
    
    
    
    if (Thread.currentThread().interrupted()) {
      throw new InterpreterInterruptedException(node.getBeginLine(), node.getBeginColumn(), node.getEndLine(),
                                                node.getEndColumn());
    }
  }

  
  
  public Object visit(WhileStatement node) {
    _checkInterrupted(node);
    super.visit(node);
    return Interpreter.NO_RESULT;
  }

  public Object visit(ForStatement node) {
    _checkInterrupted(node);
    super.visit(node);
    return Interpreter.NO_RESULT;
  }

  public Object visit(ForEachStatement node) {
    _checkInterrupted(node);
    super.visit(node);
    return Interpreter.NO_RESULT;
  }

  public Object visit(DoStatement node) {
    _checkInterrupted(node);
    super.visit(node);
    return Interpreter.NO_RESULT;
  }

  public Object visit(SwitchStatement node) {
    _checkInterrupted(node);
    super.visit(node);
    return Interpreter.NO_RESULT;
  }

  public Object visit(LabeledStatement node) {
    _checkInterrupted(node);
    super.visit(node);
    return Interpreter.NO_RESULT;
  }

  public Object visit(SynchronizedStatement node) {
    _checkInterrupted(node);
    super.visit(node);
    return Interpreter.NO_RESULT;
  }

  public Object visit(TryStatement node) {
    _checkInterrupted(node);
    super.visit(node);
    return Interpreter.NO_RESULT;
  }

  public Object visit(IfThenStatement node) {
    _checkInterrupted(node);
    super.visit(node);
    return Interpreter.NO_RESULT;
  }

  public Object visit(IfThenElseStatement node) {
    _checkInterrupted(node);
    super.visit(node);
    return Interpreter.NO_RESULT;
  }
  
  public Object visit(AssertStatement node) {
    _checkInterrupted(node);
    super.visit(node);
    return Interpreter.NO_RESULT;
  }

  public Object visit(BlockStatement node) {
    _checkInterrupted(node);
    super.visit(node);
    return Interpreter.NO_RESULT;
  }

  public Object visit(Literal node) {
    _checkInterrupted(node);
    return super.visit(node);
  }

  
  public Object visit(VariableDeclaration node) {
    _checkInterrupted(node);
    Class<?> c = (Class<?>) NodeProperties.getType(node.getType());

    if (node.getInitializer() != null) {
      Object o = performCast(c, node.getInitializer().acceptVisitor(this));

      
      String name = node.getName();

      if (!(c.isPrimitive() || o == null || c.isAssignableFrom(o.getClass()))) {
        Exception e = new ClassCastException(name);
        throw new CatchedExceptionError(e, node);
      }

      if (node.isFinal()) _context.setConstant(node.getName(), o);
      else _context.set(node.getName(), o);
    } 
    else if (node.isFinal()) _context.setConstant(node.getName(), UninitializedObject.INSTANCE);
    else {
      
      
      

      Object value = null;
      if (!c.isPrimitive()) value = null;
      else if (c == byte.class)  value = new Byte((byte) 0);
      else if (c == short.class) value = new Short((short) 0);
      else if (c == int.class) value = new Integer(0);
      else if (c == long.class)  value = new Long(0L);
      else if (c == float.class) value = new Float(0.0f);
      else if (c == double.class) value = new Double(0.0d);
      else if (c == char.class) value = new Character('\u');
      else if (c == boolean.class)  value = Boolean.valueOf(false);
      _context.set(node.getName(), value);
    }
    return Interpreter.NO_RESULT;
  }

  public Object visit(ObjectFieldAccess node) {
    _checkInterrupted(node);    
    return super.visit(node);
  }

  public Object visit(ObjectMethodCall node) {
    _checkInterrupted(node);
    Method m = (Method) node.getProperty(NodeProperties.METHOD);

    Object ret = super.visit(node);
    
    
    
    if (m != null && m.getReturnType().equals(Void.TYPE)) return Interpreter.NO_RESULT;
    return ret;
  }

  public Object visit(StaticFieldAccess node) {
    _checkInterrupted(node);
    return super.visit(node);
  }

  public Object visit(SuperFieldAccess node) {
    _checkInterrupted(node);
    return super.visit(node);
  }

  public Object visit(SuperMethodCall node) {
    _checkInterrupted(node);
    return super.visit(node);
  }

  public Object visit(StaticMethodCall node) {
    _checkInterrupted(node);
    Method m = (Method) node.getProperty(NodeProperties.METHOD);

    
    if (! Modifier.isStatic(m.getModifiers())) {
      StringBuffer buf = new StringBuffer();
      buf.append(m.getDeclaringClass());
      buf.append(".");
      buf.append(m.getName());
      buf.append("(");

      boolean first = true;
      Class<?>[] params = m.getParameterTypes();
      for (int i = 0; i < params.length; i++) {
        if (first) first = false;
        else buf.append(", ");
        buf.append(params[i].getName());
      }

      buf.append(")");
      buf.append(" is not a static method.");

      throw new InteractionsException(buf.toString());
    }

    Object ret = super.visit(node);

    
    if (m.getReturnType().equals(Void.TYPE)) return Interpreter.NO_RESULT;
    else return ret;
  }

  public Object visit(SimpleAssignExpression node) {
    _checkInterrupted(node);
    return super.visit(node);
  }

  public Object visit(QualifiedName node) {
    _checkInterrupted(node);
    return super.visit(node);
  }

  public Object visit(TypeExpression node) {
    _checkInterrupted(node);
    return super.visit(node);
  }

  public Object visit(SimpleAllocation node) {
    _checkInterrupted(node);
    return super.visit(node);
  }

  public Object visit(ArrayAllocation node) {
    _checkInterrupted(node);
    return super.visit(node);
  }

  public Object visit(ArrayInitializer node) {
    _checkInterrupted(node);
    return super.visit(node);
  }

  public Object visit(ArrayAccess node) {
    _checkInterrupted(node);
    return super.visit(node);
  }

  public Object visit(InnerAllocation node) {
    _checkInterrupted(node);
    return super.visit(node);
  }

  public Object visit(ClassAllocation node) {
    _checkInterrupted(node);
    return super.visit(node);
  }

  public Object visit(NotExpression node) {
    _checkInterrupted(node);
    return super.visit(node);
  }

  public Object visit(ComplementExpression node) {
    _checkInterrupted(node);
    return super.visit(node);
  }

  public Object visit(PlusExpression node) {
    _checkInterrupted(node);
    return super.visit(node);
  }

  public Object visit(MinusExpression node) {
    _checkInterrupted(node);
    return super.visit(node);
  }

  public Object visit(AddExpression node) {
    _checkInterrupted(node);
    return super.visit(node);
  }

  public Object visit(AddAssignExpression node) {
    _checkInterrupted(node);
    return super.visit(node);
  }

  public Object visit(SubtractExpression node) {
    _checkInterrupted(node);
    return super.visit(node);
  }

  public Object visit(SubtractAssignExpression node) {
    _checkInterrupted(node);
    return super.visit(node);
  }

  public Object visit(MultiplyExpression node) {
    _checkInterrupted(node);
    return super.visit(node);
  }

  public Object visit(MultiplyAssignExpression node) {
    _checkInterrupted(node);
    return super.visit(node);
  }

  public Object visit(DivideExpression node) {
    _checkInterrupted(node);
    return super.visit(node);
  }

  public Object visit(DivideAssignExpression node) {
    _checkInterrupted(node);
    return super.visit(node);
  }

  public Object visit(RemainderExpression node) {
    _checkInterrupted(node);
    return super.visit(node);
  }

  public Object visit(RemainderAssignExpression node) {
    _checkInterrupted(node);
    return super.visit(node);
  }

  public Object visit(EqualExpression node) {
    _checkInterrupted(node);
    return super.visit(node);
  }

  public Object visit(NotEqualExpression node) {
    _checkInterrupted(node);
    return super.visit(node);
  }

  public Object visit(LessExpression node) {
    _checkInterrupted(node);
    return super.visit(node);
  }

  public Object visit(LessOrEqualExpression node) {
    _checkInterrupted(node);
    return super.visit(node);
  }

  public Object visit(GreaterExpression node) {
    _checkInterrupted(node);
    return super.visit(node);
  }

  public Object visit(GreaterOrEqualExpression node) {
    _checkInterrupted(node);
    return super.visit(node);
  }

  public Object visit(InstanceOfExpression node) {
    _checkInterrupted(node);
    return super.visit(node);
  }

  public Object visit(ConditionalExpression node) {
    _checkInterrupted(node);
    return super.visit(node);
  }

  public Object visit(PostIncrement node) {
    _checkInterrupted(node);
    return super.visit(node);
  }

  public Object visit(PreIncrement node) {
    _checkInterrupted(node);
    return super.visit(node);
  }

  public Object visit(PostDecrement node) {
    _checkInterrupted(node);
    return super.visit(node);
  }

  public Object visit(PreDecrement node) {
    _checkInterrupted(node);
    return super.visit(node);
  }

  public Object visit(CastExpression node) {
    _checkInterrupted(node);
    return super.visit(node);
  }

  public Object visit(BitAndExpression node) {
    _checkInterrupted(node);
    return super.visit(node);
  }

  public Object visit(BitAndAssignExpression node) {
    _checkInterrupted(node);
    return super.visit(node);
  }

  public Object visit(ExclusiveOrExpression node) {
    _checkInterrupted(node);
    return super.visit(node);
  }

  public Object visit(ExclusiveOrAssignExpression node) {
    _checkInterrupted(node);
    return super.visit(node);
  }

  public Object visit(BitOrExpression node) {
    _checkInterrupted(node);
    return super.visit(node);
  }

  public Object visit(BitOrAssignExpression node) {
    _checkInterrupted(node);
    return super.visit(node);
  }

  public Object visit(ShiftLeftExpression node) {
    _checkInterrupted(node);
    return super.visit(node);
  }

  public Object visit(ShiftLeftAssignExpression node) {
    _checkInterrupted(node);
    return super.visit(node);
  }

  public Object visit(ShiftRightExpression node) {
    _checkInterrupted(node);
    return super.visit(node);
  }

  public Object visit(ShiftRightAssignExpression node) {
    _checkInterrupted(node);
    return super.visit(node);
  }

  public Object visit(UnsignedShiftRightExpression node) {
    _checkInterrupted(node);
    return super.visit(node);
  }

  public Object visit(UnsignedShiftRightAssignExpression node) {
    _checkInterrupted(node);
    return super.visit(node);
  }

  public Object visit(AndExpression node) {
    _checkInterrupted(node);
    return super.visit(node);
  }

  public Object visit(OrExpression node) {
    _checkInterrupted(node);
    return super.visit(node);
  }

  public Object visit(FunctionCall node) {
    _checkInterrupted(node);

    Object ret = super.visit(node);

    
    if (Void.TYPE.equals(node.getProperty(NodeProperties.TYPE))) return Interpreter.NO_RESULT;
    else return ret;
  }

  public Object visit(PackageDeclaration node) { return Interpreter.NO_RESULT; }

  public Object visit(ImportDeclaration node) { return Interpreter.NO_RESULT; }

  public Object visit(EmptyStatement node) { return Interpreter.NO_RESULT; }

  public Object visit(ClassDeclaration node) { return Interpreter.NO_RESULT; }
  
  public Object visit(InterfaceDeclaration node) { return Interpreter.NO_RESULT; }
  
  public Object visit(MethodDeclaration node) { return Interpreter.NO_RESULT; }
}
