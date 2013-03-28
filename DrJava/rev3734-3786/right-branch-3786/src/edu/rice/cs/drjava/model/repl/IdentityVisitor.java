

package edu.rice.cs.drjava.model.repl;

import java.util.*;

import koala.dynamicjava.tree.*;
import koala.dynamicjava.tree.visitor.*;



public class IdentityVisitor implements Visitor<Node> {
  

  public Node visit(PackageDeclaration node) { return node; }

  

  public Node visit(ImportDeclaration node) { return node; }

  

  public Node visit(EmptyStatement node) { return node; }

  
  public Node visit(WhileStatement node) {
    node.setCondition((Expression) node.getCondition().acceptVisitor(this));
    node.setBody(node.getBody().acceptVisitor(this));
    return node;
  }

  
  public Node visit(ForStatement node) {
    LinkedList<Node> init = null; 
    if (node.getInitialization() != null) {
      init = new LinkedList<Node>(); 
      Iterator<Node> it = node.getInitialization().iterator();
      while (it.hasNext()) {
        init.add(it.next().acceptVisitor(this));
      }
    }
    node.setInitialization(init);
    Expression cond = null;
    if (node.getCondition() != null) {
      cond = (Expression)node.getCondition().acceptVisitor(this);
    }
    node.setCondition(cond);
    LinkedList<Node> updt = null; 
    if (node.getUpdate() != null) {
      updt = new LinkedList<Node>(); 
      Iterator<Node> it = node.getUpdate().iterator();
      while (it.hasNext()) {
        updt.add(it.next().acceptVisitor(this));
      }
    }
    node.setUpdate(updt);
    node.setBody(node.getBody().acceptVisitor(this));
    return node;
  }

  
  public Node visit(ForEachStatement node) {
    FormalParameter param = node.getParameter();
    Expression collection = node.getCollection();
    Node stmt = node.getBody();
    
    node.setParameter((FormalParameter)param.acceptVisitor(this));
    node.setCollection((Expression)collection.acceptVisitor(this));
    node.setBody(stmt.acceptVisitor(this));
    
    return node;
  }

  
  public Node visit(DoStatement node) {
    Expression cond = (Expression) node.getCondition().acceptVisitor(this);
    node.setCondition(cond);
    Node body = node.getBody().acceptVisitor(this);
    node.setBody(body);
    return node;
  }

  
  public Node visit(SwitchStatement node) {
    Expression sel = (Expression) node.getSelector().acceptVisitor(this);
    node.setSelector(sel);
    LinkedList<SwitchBlock> cases = new LinkedList<SwitchBlock>(); 
    Iterator<SwitchBlock> it = node.getBindings().iterator();
    while (it.hasNext()) {
      cases.add((SwitchBlock)it.next().acceptVisitor(this));
    }
    node.setBindings(cases);
    return node;
  }

  
  public Node visit(SwitchBlock node) {
    Expression e = null;
    if (node.getExpression() != null) {
      e = (Expression) node.getExpression().acceptVisitor(this);
    }
    node.setExpression(e);
    LinkedList<Node> statements = null; 
    if (node.getStatements() != null) {
      statements = new LinkedList<Node>(); 
      Iterator<Node> it = node.getStatements().iterator();
      while (it.hasNext()) {
        statements.add(it.next().acceptVisitor(this));
      }
    }
    node.setStatements(statements);
    return node;
  }

  
  public Node visit(LabeledStatement node) {
    node.setStatement(node.getStatement().acceptVisitor(this));
    return node;
  }

  
  public Node visit(BreakStatement node) { return node; }

  
  public Node visit(TryStatement node) {
    Node tryBlock = node.getTryBlock().acceptVisitor(this);
    LinkedList<CatchStatement> catchStatements = new LinkedList<CatchStatement>();
    Iterator<CatchStatement> it = node.getCatchStatements().iterator();
    while (it.hasNext()) {
      catchStatements.add((CatchStatement)it.next().acceptVisitor(this));
    }
    Node finallyBlock = null;
    if (node.getFinallyBlock() != null) {
      finallyBlock = node.getFinallyBlock().acceptVisitor(this);
    }
    node = new TryStatement(tryBlock, catchStatements, finallyBlock, null, 0, 0, 0, 0);
    return node;
  }

  
  public Node visit(CatchStatement node) {
    Node exp = node.getException().acceptVisitor(this);
    Node block = node.getBlock().acceptVisitor(this);
    node = new CatchStatement((FormalParameter)exp, block, null, 0, 0, 0, 0);
    return node;
  }

  
  public Node visit(ThrowStatement node) {
    node.setExpression((Expression)node.getExpression().acceptVisitor(this));
    return node;
  }

  
  public Node visit(ReturnStatement node) {
    node.setExpression((Expression)node.getExpression().acceptVisitor(this));
    return node;
  }

  
  public Node visit(SynchronizedStatement node) {
    node.setLock((Expression)node.getLock().acceptVisitor(this));
    node.setBody(node.getBody().acceptVisitor(this));
    return node;
  }

  
  public Node visit(ContinueStatement node) {
    return node;
  }

  
  public Node visit(IfThenStatement node) {
    node.setCondition((Expression)node.getCondition().acceptVisitor(this));
    node.setThenStatement(node.getThenStatement().acceptVisitor(this));
    return node;
  }

  
  public Node visit(IfThenElseStatement node) {
    node.setCondition((Expression)node.getCondition().acceptVisitor(this));
    node.setThenStatement(node.getThenStatement().acceptVisitor(this));
    node.setElseStatement(node.getElseStatement().acceptVisitor(this));
    return node;
  }
  
  
  public Node visit(AssertStatement node) {
    node.setCondition((Expression)node.getCondition().acceptVisitor(this));
    if (node.getFailString() != null)
      node.setFailString((Expression)node.getFailString().acceptVisitor(this));
    return node;
  }

  
  public Node visit(Literal node) { return node; }

  
  public Node visit(ThisExpression node) { return node; }

  
  public Node visit(QualifiedName node) {
    return node;
  }

  
  public Node visit(ObjectFieldAccess node) {
    node.setExpression((Expression)node.getExpression().acceptVisitor(this));
    return node;
  }

  
  public Node visit(StaticFieldAccess node) {
    node.setFieldType((ReferenceType)node.getFieldType().acceptVisitor(this));
    return node;
  }

  
  public Node visit(ArrayAccess node) {
    node.setExpression((Expression)node.getExpression().acceptVisitor(this));
    node.setCellNumber((Expression)node.getCellNumber().acceptVisitor(this));
    return node;
  }

  
  public Node visit(SuperFieldAccess node) {
    return node;
  }

  
  public Node visit(ObjectMethodCall node) {
    if (node.getExpression() != null) {
      node.setExpression((Expression)node.getExpression().acceptVisitor(this));
    }
    LinkedList<Expression> arguments = null; 
    if (node.getArguments() != null) {
      arguments = new LinkedList<Expression>(); 
      Iterator<Expression> it = node.getArguments().iterator();
      while (it.hasNext()) {
        arguments.add((Expression) it.next().acceptVisitor(this));
      }
    }
    node.setArguments(arguments);
    return node;
  }

  
  public Node visit(FunctionCall node) {
    LinkedList<Expression> arguments = null; 
    if (node.getArguments() != null) {
      arguments = new LinkedList<Expression>(); 
      Iterator<Expression> it = node.getArguments().iterator();
      while (it.hasNext()) {
        arguments.add((Expression) it.next().acceptVisitor(this));
      }
    }
    node.setArguments(arguments);
    return node;
  }

  
  public Node visit(StaticMethodCall node) {
    node.setMethodType((ReferenceType)node.getMethodType().acceptVisitor(this));
    LinkedList<Expression> arguments = null; 
    if (node.getArguments() != null) {
      arguments = new LinkedList<Expression>(); 
      Iterator<Expression> it = node.getArguments().iterator();
      while (it.hasNext()) {
        arguments.add((Expression)it.next().acceptVisitor(this));
      }
    }
    node.setArguments(arguments);
    return node;
  }

    
    public Node visit(ConstructorInvocation node) {
      if (node.getExpression() != null) {
        node.setExpression((Expression)node.getExpression().acceptVisitor(this));
      }
      LinkedList<Expression> arguments = null; 
      if (node.getArguments() != null) {
        arguments = new LinkedList<Expression>(); 
        Iterator<Expression> it = node.getArguments().iterator();
        while (it.hasNext()) {
          arguments.add((Expression)it.next().acceptVisitor(this));
        }
      }
      node.setArguments(arguments);
      return node;
    }

    
    public Node visit(SuperMethodCall node) {
      LinkedList<Expression> arguments = null; 
      if (node.getArguments() != null) {
        arguments = new LinkedList<Expression>(); 
        Iterator<Expression> it = node.getArguments().iterator();
        while (it.hasNext()) {
          arguments.add((Expression)it.next().acceptVisitor(this));
        }
      }
      node.setArguments(arguments);
      return node;
    }

    
    public Node visit(PrimitiveType node) { return node; }

    
    public Node visit(ReferenceType node) { return node; }

    
    public Node visit(ArrayType node) {
      if (node.getElementType() != null) {
        node.setElementType((Type)node.getElementType().acceptVisitor(this));
      }
      return node;
    }

    
    public Node visit(TypeExpression node) {
      
      
      
      
      node = new TypeExpression((Type)node.getType().acceptVisitor(this));
      return node;
    }

    
    public Node visit(PostIncrement node) { return _visitUnary(node); }

    
    public Node visit(PostDecrement node) { return _visitUnary(node); }

    
    public Node visit(PreIncrement node) { return _visitUnary(node); }

    
    public Node visit(PreDecrement node) { return _visitUnary(node); }

    
    public Node visit(ArrayInitializer node) {
      LinkedList<Expression> cells = new LinkedList<Expression>(); 
      Iterator<Expression> it = node.getCells().iterator();
      while (it.hasNext()) {
        cells.add((Expression)it.next().acceptVisitor(this));
      }
      node.setCells(cells);
      node.setElementType((Type)node.getElementType().acceptVisitor(this));
      return node;
    }

    
    public Node visit(ArrayAllocation node) {
      int dim = node.getDimension();
      Type creationType = (Type)node.getCreationType().acceptVisitor(this);
      LinkedList<Expression> sizes = new LinkedList<Expression>(); 
      Iterator<Expression> it = node.getSizes().iterator();
      while (it.hasNext()) {
        sizes.add((Expression)it.next().acceptVisitor(this));
      }
      ArrayInitializer ai = null;
      if (node.getInitialization() != null) {
        ai = (ArrayInitializer)node.getInitialization().acceptVisitor(this);
      }
      node = new ArrayAllocation(creationType,
                                 new ArrayAllocation.TypeDescriptor(sizes, dim, ai, 0, 0));
      return node;
    }

    
    public Node visit(SimpleAllocation node) {
      node.setCreationType((Type)node.getCreationType().acceptVisitor(this));
      LinkedList<Expression> arguments = null; 
      if (node.getArguments() != null) {
        arguments = new LinkedList<Expression>(); 
        Iterator<Expression> it = node.getArguments().iterator();
        while (it.hasNext()) {
          arguments.add((Expression)it.next().acceptVisitor(this));
        }
      }
      node.setArguments(arguments);
      return node;
    }

    
    public Node visit(ClassAllocation node) {
      node.setCreationType((Type)node.getCreationType().acceptVisitor(this));
      LinkedList<Expression> arguments = null; 
      if (node.getArguments() != null) {
        arguments = new LinkedList<Expression>(); 
        Iterator<Expression> it = node.getArguments().iterator();
        while (it.hasNext()) {
          arguments.add((Expression)it.next().acceptVisitor(this));
        }
      }
      node.setArguments(arguments);
      LinkedList<Node> members = new LinkedList<Node>(); 
      Iterator<Node> it = node.getMembers().iterator();
      while (it.hasNext()) {
        members.add(it.next().acceptVisitor(this));
      }
      node.setMembers(members);
      return node;
    }

    
    public Node visit(InnerAllocation node) {
      node.setExpression((Expression)node.getExpression().acceptVisitor(this));
      node.setCreationType((Type)node.getCreationType().acceptVisitor(this));
      LinkedList<Expression> arguments = null; 
      if (node.getArguments() != null) {
        arguments = new LinkedList<Expression>(); 
        Iterator<Expression> it = node.getArguments().iterator();
        while (it.hasNext()) {
          arguments.add((Expression)it.next().acceptVisitor(this));
        }
      }
      node.setArguments(arguments);
      return node;
    }

    
    public Node visit(InnerClassAllocation node) {
      node.setExpression((Expression)node.getExpression().acceptVisitor(this));
      node.setCreationType((Type)node.getCreationType().acceptVisitor(this));
      LinkedList<Expression> arguments = null; 
      if (node.getArguments() != null) {
        arguments = new LinkedList<Expression>(); 
        Iterator<Expression> it = node.getArguments().iterator();
        while (it.hasNext()) {
          arguments.add((Expression)it.next().acceptVisitor(this));
        }
      }
      node.setArguments(arguments);
      LinkedList<Node> members = new LinkedList<Node>(); 
      Iterator<Node> it = node.getMembers().iterator();
      while (it.hasNext()) {
        members.add(it.next().acceptVisitor(this));
      }
      node.setMembers(members);
      return node;
    }

    
    public Node visit(CastExpression node) {
      node.setTargetType((Type)node.getTargetType().acceptVisitor(this));
      node.setExpression((Expression)node.getExpression().acceptVisitor(this));
      return node;
    }

    
    public Node visit(NotExpression node) {
      return _visitUnary(node);
    }

    
    public Node visit(ComplementExpression node) {
      return _visitUnary(node);
    }

    
    public Node visit(PlusExpression node) {
      return _visitUnary(node);
    }

    
    public Node visit(MinusExpression node) {
      return _visitUnary(node);
    }

    
    public Node visit(MultiplyExpression node) {
      return _visitBinary(node);
    }

    
    public Node visit(DivideExpression node) {
      return _visitBinary(node);
    }

    
    public Node visit(RemainderExpression node) {
      return _visitBinary(node);
    }

    
    public Node visit(AddExpression node) {
      return _visitBinary(node);
    }

    
    public Node visit(SubtractExpression node) {
      return _visitBinary(node);
    }

    
    public Node visit(ShiftLeftExpression node) {
      return _visitBinary(node);
    }

    
    public Node visit(ShiftRightExpression node) {
      return _visitBinary(node);
    }

    
    public Node visit(UnsignedShiftRightExpression node) {
      return _visitBinary(node);
    }

    
    public Node visit(LessExpression node) {
      return _visitBinary(node);
    }

    
    public Node visit(GreaterExpression node) {
      return _visitBinary(node);
    }

    
    public Node visit(LessOrEqualExpression node) {
      return _visitBinary(node);
    }

    
    public Node visit(GreaterOrEqualExpression node) {
      return _visitBinary(node);
    }

    
    public Node visit(InstanceOfExpression node) {
      node.setExpression((Expression)node.getExpression().acceptVisitor(this));
      node.setReferenceType((Type)node.getReferenceType().acceptVisitor(this));
      return node;
    }

    
    public Node visit(EqualExpression node) {
      return _visitBinary(node);
    }

    
    public Node visit(NotEqualExpression node) {
      return _visitBinary(node);
    }

    
    public Node visit(BitAndExpression node) {
      return _visitBinary(node);
    }

    
    public Node visit(ExclusiveOrExpression node) {
      return _visitBinary(node);
    }

    
    public Node visit(BitOrExpression node) {
      return _visitBinary(node);
    }

    
    public Node visit(AndExpression node) {
      return _visitBinary(node);
    }

    
    public Node visit(OrExpression node) {
      return _visitBinary(node);
    }

    
    public Node visit(ConditionalExpression node) {
      node.setConditionExpression((Expression)node.getConditionExpression().acceptVisitor(this));
      node.setIfTrueExpression((Expression)node.getIfTrueExpression().acceptVisitor(this));
      node.setIfFalseExpression((Expression)node.getIfFalseExpression().acceptVisitor(this));
      return node;
    }

    
    public Node visit(SimpleAssignExpression node) {
      return _visitBinary(node);
    }

    
    public Node visit(MultiplyAssignExpression node) {
      return _visitBinary(node);
    }

    
    public Node visit(DivideAssignExpression node) {
      return _visitBinary(node);
    }

    
    public Node visit(RemainderAssignExpression node) {
      return _visitBinary(node);
    }

    
    public Node visit(AddAssignExpression node) {
      return _visitBinary(node);
    }

    
    public Node visit(SubtractAssignExpression node) {
      return _visitBinary(node);
    }

    
    public Node visit(ShiftLeftAssignExpression node) {
      return _visitBinary(node);
    }

    
    public Node visit(ShiftRightAssignExpression node) {
      return _visitBinary(node);
    }

    
    public Node visit(UnsignedShiftRightAssignExpression node) {
      return _visitBinary(node);
    }

    
    public Node visit(BitAndAssignExpression node) {
      return _visitBinary(node);
    }

    
    public Node visit(ExclusiveOrAssignExpression node) {
      return _visitBinary(node);
    }

    
    public Node visit(BitOrAssignExpression node) {
      return _visitBinary(node);
    }

    
    public Node visit(BlockStatement node) {
      LinkedList<Node> statements = new LinkedList<Node>(); 
      Iterator<Node> it = node.getStatements().iterator();
      while (it.hasNext()) {
        statements.add(it.next().acceptVisitor(this));
      }
      node.setStatements(statements);
      return node;
    }

    
    public Node visit(ClassDeclaration node) {
      LinkedList<Node> members = new LinkedList<Node>(); 
      Iterator<Node> it = node.getMembers().iterator();
      while (it.hasNext()) {
        members.add(it.next().acceptVisitor(this));
      }
      node.setMembers(members);
      return node;
    }

    
    public Node visit(InterfaceDeclaration node) {
      LinkedList<Node> members = new LinkedList<Node>(); 
      Iterator<Node> it = node.getMembers().iterator();
      while (it.hasNext()) {
        members.add(it.next().acceptVisitor(this));
      }
      node.setMembers(members);
      return node;
    }

    
    public Node visit(ConstructorDeclaration node) {
      LinkedList<FormalParameter> parameters = new LinkedList<FormalParameter>(); 
      Iterator<FormalParameter> it1 = node.getParameters().iterator();
      while (it1.hasNext()) {
        parameters.add((FormalParameter)it1.next().acceptVisitor(this));
      }
      node.setParameters(parameters);
      if (node.getConstructorInvocation() != null) {
        node.setConstructorInvocation((ConstructorInvocation)node.getConstructorInvocation().acceptVisitor(this));
      }
      LinkedList<Node> statements = new LinkedList<Node>(); 
      Iterator<Node> it2 = node.getStatements().iterator();
      while (it2.hasNext()) {
        statements.add(it2.next().acceptVisitor(this));
      }
      node.setStatements(statements);
      return node;
    }

    
    public Node visit(MethodDeclaration node) {
      node.setReturnType((Type)node.getReturnType().acceptVisitor(this));
      LinkedList<FormalParameter> parameters = new LinkedList<FormalParameter>(); 
      Iterator<FormalParameter> it = node.getParameters().iterator();
      while (it.hasNext()) {
        parameters.add((FormalParameter)it.next().acceptVisitor(this));
      }
      node.setParameters(parameters);
      if (node.getBody() != null) {
        node.setBody((BlockStatement)node.getBody().acceptVisitor(this));
      }
      return node;
    }

    
    public Node visit(FormalParameter node) {
      node.setType((Type)node.getType().acceptVisitor(this));
      return node;
    }

    
    public Node visit(FieldDeclaration node) {
      node.setType((Type)node.getType().acceptVisitor(this));
      if (node.getInitializer() != null) {
        node.setInitializer((Expression)node.getInitializer().acceptVisitor(this));
      }
      return node;
    }

    
    public Node visit(VariableDeclaration node) {
      node.setType((Type)node.getType().acceptVisitor(this));
      if (node.getInitializer() != null) {
        node.setInitializer((Expression)node.getInitializer().acceptVisitor(this));
      }
      return node;
    }

    
    public Node visit(ClassInitializer node) {
      node.setBlock((BlockStatement)node.getBlock().acceptVisitor(this));
      return node;
    }

    
    public Node visit(InstanceInitializer node) {
      node.setBlock((BlockStatement)node.getBlock().acceptVisitor(this));
      return node;
    }

    private Node _visitUnary(UnaryExpression node) {
      node.setExpression((Expression)node.getExpression().acceptVisitor(this));
      return node;
    }

    private Node _visitBinary(BinaryExpression node) {
      node.setLeftExpression((Expression)node.getLeftExpression().acceptVisitor(this));
      node.setRightExpression((Expression)node.getRightExpression().acceptVisitor(this));
      return node;
    }
}
