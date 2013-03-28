

package koala.dynamicjava.tree.visitor;

import koala.dynamicjava.tree.*;
import koala.dynamicjava.tree.tiger.GenericReferenceTypeName;
import koala.dynamicjava.tree.tiger.HookTypeName;



public abstract class AbstractVisitor<T> implements Visitor<T> {
  
  public T defaultCase(Node node) {
    throw new IllegalArgumentException("Visitor is undefined for " + node.getClass().getName());
  }
  
  
  public T visit(CompilationUnit node) {
    return defaultCase(node);
  }
  
  
  public T visit(PackageDeclaration node) {
    return defaultCase(node);
  }
  
  
  public T visit(ImportDeclaration node) {
    return defaultCase(node);
  }
  
  
  public T visit(EmptyStatement node) {
    return defaultCase(node);
  }
  
  
  public T visit(ExpressionStatement node) {
    return defaultCase(node);
  }
  
  
  public T visit(WhileStatement node) {
    return defaultCase(node);
  }
  
  
  public T visit(ForStatement node) {
    return defaultCase(node);
  }
  
  
  public T visit(ForEachStatement node) {
    return defaultCase(node);
  }

  
  public T visit(DoStatement node) {
    return defaultCase(node);
  }
  
  
  public T visit(SwitchStatement node) {
    return defaultCase(node);
  }
  
  
  public T visit(SwitchBlock node) {
    return defaultCase(node);
  }
  
  
  public T visit(LabeledStatement node) {
    return defaultCase(node);
  }
  
  
  public T visit(BreakStatement node) {
    return defaultCase(node);
  }
  
  
  public T visit(TryStatement node) {
    return defaultCase(node);
  }
  
  
  public T visit(CatchStatement node) {
    return defaultCase(node);
  }
  
  
  public T visit(ThrowStatement node) {
    return defaultCase(node);
  }
  
  
  public T visit(ReturnStatement node) {
    return defaultCase(node);
  }
  
  
  public T visit(SynchronizedStatement node) {
    return defaultCase(node);
  }
  
  
  public T visit(ContinueStatement node) {
    return defaultCase(node);
  }
  
  
  public T visit(IfThenStatement node) {
    return defaultCase(node);
  }
  
  
  public T visit(IfThenElseStatement node) {
    return defaultCase(node);
  }
  
  
  public T visit(AssertStatement node) {
    return defaultCase(node);
  }
  
  
  public T visit(Literal node) {
    return defaultCase(node);
  }
  
  
  public T visit(ThisExpression node) {
    return defaultCase(node);
  }
  
  
  public T visit(AmbiguousName node) {
    return defaultCase(node);
  }
  
  
  public T visit(VariableAccess node) {
    return defaultCase(node);
  }
  
  
  public T visit(SimpleFieldAccess node) {
    return defaultCase(node);
  }
  
  
  public T visit(ObjectFieldAccess node) {
    return defaultCase(node);
  }
  
  
  public T visit(StaticFieldAccess node) {
    return defaultCase(node);
  }
  
  
  public T visit(SuperFieldAccess node) {
    return defaultCase(node);
  }
  
  
  public T visit(ArrayAccess node) {
    return defaultCase(node);
  }
  
  
  public T visit(ObjectMethodCall node) {
    return defaultCase(node);
  }
  
  
  public T visit(SimpleMethodCall node) {
    return defaultCase(node);
  }
  
  
  public T visit(StaticMethodCall node) {
    return defaultCase(node);
  }
  
  
  public T visit(ConstructorCall node) {
    return defaultCase(node);
  }
  
  
  public T visit(SuperMethodCall node) {
    return defaultCase(node);
  }
  
  
  public T visit(BooleanTypeName node) {
    return defaultCase(node);
  }
  
  
  public T visit(ByteTypeName node) {
    return defaultCase(node);
  }
  
  
  public T visit(ShortTypeName node) {
    return defaultCase(node);
  }
  
  
  public T visit(CharTypeName node) {
    return defaultCase(node);
  }
  
  
  public T visit(IntTypeName node) {
    return defaultCase(node);
  }
  
  
  public T visit(LongTypeName node) {
    return defaultCase(node);
  }
  
  
  public T visit(FloatTypeName node) {
    return defaultCase(node);
  }
  
  
  public T visit(DoubleTypeName node) {
    return defaultCase(node);
  }
  
  
  public T visit(VoidTypeName node) {
    return defaultCase(node);
  }
  
  
  public T visit(ReferenceTypeName node) {
    return defaultCase(node);
  }
  
  
  public T visit(GenericReferenceTypeName node) {
    return defaultCase(node);
  }
  
  
  public T visit(ArrayTypeName node) {
    return defaultCase(node);
  }
  
  
  public T visit(HookTypeName node) {
    return defaultCase(node);
  }
  
  
  public T visit(TypeExpression node) {
    return defaultCase(node);
  }
  
  
  public T visit(PostIncrement node) {
    return defaultCase(node);
  }
  
  
  public T visit(PostDecrement node) {
    return defaultCase(node);
  }
  
  
  public T visit(PreIncrement node) {
    return defaultCase(node);
  }
  
  
  public T visit(PreDecrement node) {
    return defaultCase(node);
  }
  
  
  public T visit(ArrayInitializer node) {
    return defaultCase(node);
  }
  
  
  public T visit(ArrayAllocation node) {
    return defaultCase(node);
  }
  
  
  public T visit(SimpleAllocation node) {
    return defaultCase(node);
  }
  
  
  public T visit(AnonymousAllocation node) {
    return defaultCase(node);
  }
  
  
  public T visit(InnerAllocation node) {
    return defaultCase(node);
  }
  
  
  public T visit(AnonymousInnerAllocation node) {
    return defaultCase(node);
  }
  
  
  public T visit(CastExpression node) {
    return defaultCase(node);
  }
  
  
  public T visit(NotExpression node) {
    return defaultCase(node);
  }
  
  
  public T visit(ComplementExpression node) {
    return defaultCase(node);
  }
  
  
  public T visit(PlusExpression node) {
    return defaultCase(node);
  }
  
  
  public T visit(MinusExpression node) {
    return defaultCase(node);
  }
  
  
  public T visit(MultiplyExpression node) {
    return defaultCase(node);
  }
  
  
  public T visit(DivideExpression node) {
    return defaultCase(node);
  }
  
  
  public T visit(RemainderExpression node) {
    return defaultCase(node);
  }
  
  
  public T visit(AddExpression node) {
    return defaultCase(node);
  }
  
  
  public T visit(SubtractExpression node) {
    return defaultCase(node);
  }
  
  
  public T visit(ShiftLeftExpression node) {
    return defaultCase(node);
  }
  
  
  public T visit(ShiftRightExpression node) {
    return defaultCase(node);
  }
  
  
  public T visit(UnsignedShiftRightExpression node) {
    return defaultCase(node);
  }
  
  
  public T visit(LessExpression node) {
    return defaultCase(node);
  }
  
  
  public T visit(GreaterExpression node) {
    return defaultCase(node);
  }
  
  
  public T visit(LessOrEqualExpression node) {
    return defaultCase(node);
  }
  
  
  public T visit(GreaterOrEqualExpression node) {
    return defaultCase(node);
  }
  
  
  public T visit(InstanceOfExpression node) {
    return defaultCase(node);
  }
  
  
  public T visit(EqualExpression node) {
    return defaultCase(node);
  }
  
  
  public T visit(NotEqualExpression node) {
    return defaultCase(node);
  }
  
  
  public T visit(BitAndExpression node) {
    return defaultCase(node);
  }
  
  
  public T visit(ExclusiveOrExpression node) {
    return defaultCase(node);
  }
  
  
  public T visit(BitOrExpression node) {
    return defaultCase(node);
  }
  
  
  public T visit(AndExpression node) {
    return defaultCase(node);
  }
  
  
  public T visit(OrExpression node) {
    return defaultCase(node);
  }
  
  
  public T visit(ConditionalExpression node) {
    return defaultCase(node);
  }
  
  
  public T visit(SimpleAssignExpression node) {
    return defaultCase(node);
  }
  
  
  public T visit(MultiplyAssignExpression node) {
    return defaultCase(node);
  }
  
  
  public T visit(DivideAssignExpression node) {
    return defaultCase(node);
  }
  
  
  public T visit(RemainderAssignExpression node) {
    return defaultCase(node);
  }
  
  
  public T visit(AddAssignExpression node) {
    return defaultCase(node);
  }
  
  
  public T visit(SubtractAssignExpression node) {
    return defaultCase(node);
  }
  
  
  public T visit(ShiftLeftAssignExpression node) {
    return defaultCase(node);
  }
  
  
  public T visit(ShiftRightAssignExpression node) {
    return defaultCase(node);
  }
  
  
  public T visit(UnsignedShiftRightAssignExpression node) {
    return defaultCase(node);
  }
  
  
  public T visit(BitAndAssignExpression node) {
    return defaultCase(node);
  }
  
  
  public T visit(ExclusiveOrAssignExpression node) {
    return defaultCase(node);
  }
  
  
  public T visit(BitOrAssignExpression node) {
    return defaultCase(node);
  }
  
  
  public T visit(BlockStatement node) {
    return defaultCase(node);
  }
  
  
  public T visit(ClassDeclaration node) {
    return defaultCase(node);
  }
  
  
  public T visit(InterfaceDeclaration node) {
    return defaultCase(node);
  }
  
  
  public T visit(ConstructorDeclaration node) {
    return defaultCase(node);
  }
  
  
  public T visit(MethodDeclaration node) {
    return defaultCase(node);
  }
  
  
  public T visit(FormalParameter node) {
    return defaultCase(node);
  }
  
  
  public T visit(FieldDeclaration node) {
    return defaultCase(node);
  }
  
  
  public T visit(VariableDeclaration node) {
    return defaultCase(node);
  }
  
  public T visit(EnumDeclaration.EnumConstant node) {
    return defaultCase(node);
  }
  
  
  public T visit(ClassInitializer node) {
    return defaultCase(node);
  }
  
  
  public T visit(InstanceInitializer node) {
    return defaultCase(node);
  }

  
  public T visit(ModifierSet node) {
    return defaultCase(node);
  }
  
  public T visit(Annotation node) {
    return defaultCase(node);
  }
  
}
