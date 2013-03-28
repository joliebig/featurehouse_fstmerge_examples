

package edu.rice.cs.javalanglevels;

import edu.rice.cs.javalanglevels.tree.*;
import edu.rice.cs.javalanglevels.parser.JExprParser;
import java.util.*;
import java.io.File;
import edu.rice.cs.plt.reflect.JavaVersion;
import edu.rice.cs.plt.iter.*;

import junit.framework.TestCase;



public class Bob extends TypeChecker {
  
  protected LinkedList<VariableData> _vars;
  
  
  protected LinkedList<VariableData> thingsThatHaveBeenAssigned;
  
  
  protected Data _data;
  
  
  protected LinkedList<Pair<SymbolData, JExpression>> _thrown;

  
  public Bob(Data data, File file, String packageName, LinkedList<String> importedFiles, 
             LinkedList<String> importedPackages, LinkedList<VariableData> vars, 
             LinkedList<Pair<SymbolData, JExpression>> thrown) {
    super(file, packageName, importedFiles, importedPackages);
    if (vars == null) throw new RuntimeException("Bob called with _vars = null!");
    _data = data;
    _vars = vars;

    thingsThatHaveBeenAssigned = new LinkedList<VariableData>();
    _thrown = thrown;
  }
  
  
  protected Data _getData() { return _data; }
  
  
  protected boolean inStaticMethod() {
    for (Data d = _data; d != null; d = d.getOuterData()) {
      if (d instanceof MethodData) { return d.hasModifier("static"); }

    }
    return false;
  }
  
  
  protected SymbolData findClassReference(TypeData lhs, String namePiece, JExpression jexpr) {
    SymbolData result;
    if (lhs == null) {
      
      result = getSymbolData(true, namePiece, _getData(), jexpr, false); 

    }
    
    else if (lhs instanceof PackageData) {
      
      result = getSymbolData(lhs.getName() + "." + namePiece, _getData(), jexpr, false); 
    }

    else {
      
      result = getSymbolData(true, namePiece, lhs.getSymbolData(), jexpr, false); 

      
    }
    
     return result;

  }


    
  protected void handleMethodInvocation(MethodData md, JExpression jexpr) {
    String[] thrown = md.getThrown();
    for (int i = 0; i<thrown.length; i++) {
      _thrown.addLast(new Pair<SymbolData, JExpression>(getSymbolData(thrown[i], _data, jexpr), jexpr));
    }
  }
  
  
  protected InstanceData[] getArgTypesForInvocation(ParenthesizedExpressionList pel) {
    Expression[] exprs = pel.getExpressions();
    InstanceData[] newArgs = new InstanceData[exprs.length];
    TypeData[] args = new TypeData[exprs.length];
    ExpressionTypeChecker etc = new ExpressionTypeChecker(_data, _file, _package, _importedFiles, _importedPackages,
                                                          _vars, _thrown);
    for (int i = 0; i < exprs.length; i++) {
      args[i] = exprs[i].visit(etc);
      if (args[i] == null || !assertFound(args[i], exprs[i])) {
        return null;
      }
      if (!args[i].isInstanceType()) {
        _addError("Cannot pass a class or interface name as a constructor argument.  " + 
                  "Perhaps you meant to create an instance or use " + args[i].getName() + ".class", exprs[i]);
      }
      newArgs[i] = args[i].getInstanceData(); 
    }
    thingsThatHaveBeenAssigned.addAll(etc.thingsThatHaveBeenAssigned);
    return newArgs;
  }
  

  public TypeData forInitializedVariableDeclaratorOnly(InitializedVariableDeclarator that, TypeData type_result, 
                                                       TypeData name_result, TypeData initializer_result) {
    if (initializer_result != null && assertFound(initializer_result, that.getInitializer())) {
      if (!initializer_result.isInstanceType()) {
        _addError("Field or variable " + that.getName().getText() + 
                  " cannot be initialized with the class or interface name " + initializer_result.getName() + 
                  ".  Perhaps you meant to create an instance or use " + initializer_result.getName() + ".class", that);
      }
      
      else if (!_isAssignableFrom(type_result.getSymbolData(), initializer_result.getSymbolData())) {
        _addError("Type: \"" + type_result.getName() + "\" expected, instead found type: \"" + 
                  initializer_result.getName() + "\".", that);
      }
    }
    Word name = that.getName();
    String text = that.getName().getText();
    VariableData vd = _data.getVar(text);
    if (vd == null) {
      throw new RuntimeException("Internal Program Error: The field or variable " + text + 
                                 " was not found in this block.  Please report this bug.");
    }
    _vars.addLast(vd);
    return null;
  }
  
    
  public TypeData forInstanceInitializer(InstanceInitializer that) {
    throw new RuntimeException("Internal Program Error: Instance Initializers are not supported." + 
                               "  This should have been caught before the Type Checker Pass.  Please report this bug.");
  }

    
  public TypeData forStaticInitializer(StaticInitializer that) {
    throw new RuntimeException("Internal Program Error: Static Initializers are not supported." +
                               "  This should have been caught before the Type Checker Pass.  Please report this bug.");
  }
  
    
  public TypeData forLabeledStatement(LabeledStatement that) {
    throw new RuntimeException("Internal Program Error: Labeled Statements are not supported." + 
                               "  This should have been caught before the Type Checker Pass.  Please report this bug.");
  }
  
  
  public TypeData forExpressionStatement(ExpressionStatement that) {
    ExpressionTypeChecker etc = new ExpressionTypeChecker(_data, _file, _package, _importedFiles, _importedPackages,
                                                          _vars, _thrown);
    final TypeData expression_result = that.getExpression().visit(etc);

    
    thingsThatHaveBeenAssigned.addAll(etc.thingsThatHaveBeenAssigned);
    
    return expression_result;
  }
  
  
  public TypeData forThrowStatement(ThrowStatement that) {
    ExpressionTypeChecker etc = new ExpressionTypeChecker(_data, _file, _package, _importedFiles, _importedPackages,
                                                          _vars, _thrown);
    final TypeData thrown_result = that.getThrown().visit(etc);
    thingsThatHaveBeenAssigned.addAll(etc.thingsThatHaveBeenAssigned);

    forThrowStatementOnly(that, thrown_result);
    return SymbolData.EXCEPTION.getInstanceData();
  }


  
  public TypeData forThrowStatementOnly(ThrowStatement that, TypeData thrown_result) {
    if (thrown_result == null || !assertFound(thrown_result, that.getThrown())) return null;
  
    
    _thrown.addLast(new Pair<SymbolData, JExpression>(thrown_result.getSymbolData(), that));
      
    
    if (!thrown_result.isInstanceType()) {
      _addError("You cannot throw a class or interface name.  Perhaps you mean to instantiate the exception class " + 
                thrown_result.getSymbolData().getName() + " that you are throwing", that);
      thrown_result = thrown_result.getInstanceData();
    }
    


    
    if (!_isAssignableFrom(getSymbolData("java.lang.Throwable", that, false, true), thrown_result.getSymbolData())) {
      _addError("You are attempting to throw " + thrown_result.getSymbolData().getName() + 
                ", which does not implement the Throwable interface", that);
    }
    return thrown_result;
  }
  
    
  public TypeData forSynchronizedStatement(SynchronizedStatement that) {
    throw new RuntimeException("SynchronizedStatements are not supported.");
  }


  
  public TypeData forFormalParameter(FormalParameter that) {
    final TypeData declarator_result = that.getDeclarator().visit(this);
    return declarator_result;
  }

  
  public TypeData forVariableDeclaration(VariableDeclaration that) {
    final TypeData mav_result = that.getMav().visit(this);
    final TypeData[] declarators_result = makeArrayOfRetType(that.getDeclarators().length);
    for (int i = 0; i < that.getDeclarators().length; i++) {
      declarators_result[i] = that.getDeclarators()[i].visit(this);
    }
    return null;
  }

  
  public TypeData forUninitializedVariableDeclarator(UninitializedVariableDeclarator that) {
    final TypeData type_result = getSymbolData(that.getType().getName(), _data, that.getType());
    final TypeData name_result = that.getName().visit(this);
    return forUninitializedVariableDeclaratorOnly(that, type_result, name_result);
  }
  
  
  public TypeData forInitializedVariableDeclarator(InitializedVariableDeclarator that) {
    final SymbolData type_result = getSymbolData(that.getType().getName(), _data, that.getType());
    final TypeData name_result = that.getName().visit(this); 

    TypeData initializer_result;
    if (that.getInitializer() instanceof ArrayInitializer) {
      initializer_result = forArrayInitializerHelper((ArrayInitializer) that.getInitializer(), type_result);
    }
    else {
      ExpressionTypeChecker etc = new ExpressionTypeChecker(_data, _file, _package, _importedFiles, _importedPackages,
                                                            _vars, _thrown);
      initializer_result = that.getInitializer().visit(etc);
      thingsThatHaveBeenAssigned.addAll(etc.thingsThatHaveBeenAssigned); 
    }
    return forInitializedVariableDeclaratorOnly(that, type_result, name_result, initializer_result);
  }

  
  protected boolean canBeAssigned(VariableData vd) {
    return !vd.isFinal() || !vd.hasValue();
  }
  
  
  public TypeData forArrayInitializerHelper(ArrayInitializer that, SymbolData type) {
    if (type == null) {return null;}
    if (!(type instanceof ArrayData)) {_addError("You cannot initialize the non-array type " + type.getName() + 
                                                 " with an array initializer", that); return type.getInstanceData();}
    
    SymbolData elementType = ((ArrayData) type).getElementType();
    VariableInitializerI[] elements = that.getItems();
    TypeData[] result = makeArrayOfRetType(elements.length);
    
    for (int i = 0; i<elements.length; i++) {
      if (elements[i] instanceof ArrayInitializer) {
          result[i] = forArrayInitializerHelper((ArrayInitializer) elements[i], elementType);
      }
      else {
        ExpressionTypeChecker etc = new ExpressionTypeChecker(_data, _file, _package, _importedFiles, _importedPackages,
                                                              _vars, _thrown);
        result[i] = elements[i].visit(etc);
        
        
        thingsThatHaveBeenAssigned.addAll(etc.thingsThatHaveBeenAssigned); 

        if (result[i] != null) {
          if (assertFound(result[i], (JExpression) that.getItems()[i])) {
            if (!result[i].getSymbolData().isAssignableTo(elementType, LanguageLevelConverter.OPT.javaVersion())) {
              _addError("The elements of this initializer should have type " + elementType.getName() + " but element "
                          + i + " has type " + result[i].getSymbolData().getName(), (JExpression) that.getItems()[i]);
            }
            else {
              assertInstanceType(result[i], "The elements of this initializer should all be instances," + 
                                 " but you have specified the type name " + result[i].getName(), 
                                 (JExpression) that.getItems()[i]);
            }
          }
        }
      }
    }
    return type.getInstanceData();
  }
 
  
  public TypeData forInnerClassDef(InnerClassDef that) {
    String className = that.getName().getText();
    
    
    SymbolData sd = _data.getInnerClassOrInterface(className);
    
    if (sd == null) throw new RuntimeException("SymbolData is null for class name = " + className);

    
    if (checkForCyclicInheritance(sd, new LinkedList<SymbolData>(), that)) { return null; }
    final TypeData mav_result = that.getMav().visit(this);
    final TypeData name_result = that.getName().visit(this);
    final TypeData[] typeParameters_result = makeArrayOfRetType(that.getTypeParameters().length);
    for (int i = 0; i < that.getTypeParameters().length; i++) {
      typeParameters_result[i] = that.getTypeParameters()[i].visit(this);
    }
    final TypeData superclass_result = that.getSuperclass().visit(this);
    final TypeData[] interfaces_result = makeArrayOfRetType(that.getInterfaces().length);
    for (int i = 0; i < that.getInterfaces().length; i++) {
      interfaces_result[i] = that.getInterfaces()[i].visit(this);
    }
    final TypeData body_result = 
      that.getBody().visit(new ClassBodyTypeChecker(sd, _file, _package, _importedFiles, _importedPackages, _vars, 
                                                    _thrown));
    return null;
  }
  
  
  public TypeData forInnerInterfaceDef(InnerInterfaceDef that) {
    String className = that.getName().getText();
    SymbolData sd = _data.getInnerClassOrInterface(className); 


    
    if (checkForCyclicInheritance(sd, new LinkedList<SymbolData>(), that)) {
      return null;
    }
    final TypeData mav_result = that.getMav().visit(this);
    final TypeData name_result = that.getName().visit(this);
    final TypeData[] typeParameters_result = makeArrayOfRetType(that.getTypeParameters().length);
    for (int i = 0; i < that.getTypeParameters().length; i++) {
      typeParameters_result[i] = that.getTypeParameters()[i].visit(this);
    }
    final TypeData[] interfaces_result = makeArrayOfRetType(that.getInterfaces().length);
    for (int i = 0; i < that.getInterfaces().length; i++) {
      interfaces_result[i] = that.getInterfaces()[i].visit(this);
    }

    final TypeData body_result = 
      that.getBody().visit(new InterfaceBodyTypeChecker(sd, _file, _package, _importedFiles, _importedPackages, _vars, 
                                                        _thrown));
    return null;

  }
    
  
  
  void reassignVariableDatas(LinkedList<VariableData> l1, LinkedList<VariableData> l2) {
    for (int i = 0; i<l1.size(); i++) { 
      if (l2.contains(l1.get(i))) {
        l1.get(i).gotValue();
        l1.get(i).gotValue();
      }
    }
  }
  
  
  void reassignLotsaVariableDatas(LinkedList<VariableData> tryBlock, LinkedList<LinkedList<VariableData>> catchBlocks) {
    for (int i = 0; i<tryBlock.size(); i++) {
      boolean seenIt = true;
      for (int j = 0; j<catchBlocks.size(); i++) {
        if (!catchBlocks.get(j).contains(tryBlock.get(i))) {seenIt = false;}
      }
      if (seenIt) {        
        tryBlock.get(i).gotValue();
      }
    }
  }
  

  
  public void handleUncheckedException(SymbolData sd, JExpression j) {
    if (j instanceof MethodInvocation) {
      _addError("The method " + ((MethodInvocation)j).getName().getText() + " is declared to throw the exception " + 
                sd.getName() + " which needs to be caught or declared to be thrown", j);
      }
      else if (j instanceof ThrowStatement) {
        _addError("This statement throws the exception " + sd.getName() + 
                  " which needs to be caught or declared to be thrown", j);
      }
      else if (j instanceof ClassInstantiation) {
        _addError("The constructor for the class " + ((ClassInstantiation)j).getType().getName() + 
                  " is declared to throw the exception " + sd.getName() +
                  " which needs to be caught or declared to be thrown.", j);
      }
      else if (j instanceof SuperConstructorInvocation) {
        _addError("The constructor of this class's super class could throw the exception " + sd.getName() + 
                  ", so the enclosing constructor needs to be declared to throw it", j);
      }
      else if (j instanceof ThisConstructorInvocation) {
        _addError("This constructor could throw the exception " + sd.getName() + 
                  ", so this enclosing constructor needs to be declared to throw it", j);
      }
      
      else if (j instanceof BracedBody) { 
        _addError("There is an implicit call to the superclass's constructor here.  " + 
                  "That constructor could throw the exception " + sd.getName() + 
                  ", so the enclosing constructor needs to be declared to throw it", j);
      }
      
      else {
        throw new RuntimeException("Internal Program Error: Something besides a method invocation or throw statement" + 
                                   " threw an exception.  Please report this bug.");
      }
  }
  
  
  public boolean isCheckedException(SymbolData sd, JExpression that) {
    return sd.isSubClassOf(getSymbolData("java.lang.Throwable", _data, that, false)) &&
      ! sd.isSubClassOf(getSymbolData("java.lang.RuntimeException", _data, that, false)) &&
      ! sd.isSubClassOf(getSymbolData("java.lang.Error", _data, that, false));
  }
  
  
  public boolean isUncaughtCheckedException(SymbolData sd, JExpression that) {
    return isCheckedException(sd, that);
  }
  
  
  public TypeData forBody(Body that) {
    final TypeData[] items_result = makeArrayOfRetType(that.getStatements().length);
    for (int i = 0; i < that.getStatements().length; i++) {
      items_result[i] = that.getStatements()[i].visit(this);
      
      for (int j = 0; j < this._thrown.size(); j++) {
        if (isUncaughtCheckedException(this._thrown.get(j).getFirst(), that)) {
          handleUncheckedException(this._thrown.get(j).getFirst(), this._thrown.get(j).getSecond());
        }
      }
    }

    return forBodyOnly(that, items_result);
  }

  
  public TypeData forBracedBody(BracedBody that) { return forBody(that); }
  
  
  public TypeData forUnbracedBody(UnbracedBody that) { return forBody(that); }
  
        
  public static class BobTest extends TestCase {
    
    private Bob _b;
    
    private SymbolData _sd1;
    private SymbolData _sd2;
    private SymbolData _sd3;
    private SymbolData _sd4;
    private SymbolData _sd5;
    private SymbolData _sd6;
    private ModifiersAndVisibility _publicMav = 
      new ModifiersAndVisibility(SourceInfo.NO_INFO, new String[] {"public"});
    private ModifiersAndVisibility _protectedMav = 
      new ModifiersAndVisibility(SourceInfo.NO_INFO, new String[] {"protected"});
    private ModifiersAndVisibility _privateMav = 
      new ModifiersAndVisibility(SourceInfo.NO_INFO, new String[] {"private"});
    private ModifiersAndVisibility _packageMav = new ModifiersAndVisibility(SourceInfo.NO_INFO, new String[0]);
    private ModifiersAndVisibility _abstractMav =
      new ModifiersAndVisibility(SourceInfo.NO_INFO, new String[] {"abstract"});
    private ModifiersAndVisibility _finalMav =
      new ModifiersAndVisibility(SourceInfo.NO_INFO, new String[] {"final"});
    private ModifiersAndVisibility _finalPublicMav =
      new ModifiersAndVisibility(SourceInfo.NO_INFO, new String[] {"final", "public"});
    private ModifiersAndVisibility _publicAbstractMav =
      new ModifiersAndVisibility(SourceInfo.NO_INFO, new String[] {"public", "abstract"});
    private ModifiersAndVisibility _publicStaticMav =
      new ModifiersAndVisibility(SourceInfo.NO_INFO, new String[] {"public", "static"});
    
    
    public BobTest() { this(""); }
    public BobTest(String name) { super(name); }
    
    public void setUp() {
      errors = new LinkedList<Pair<String, JExpressionIF>>();
      LanguageLevelConverter.symbolTable.clear();
      _b = new Bob(null, new File(""), "", new LinkedList<String>(), new LinkedList<String>(), 
                   new LinkedList<VariableData>(), new LinkedList<Pair<SymbolData, JExpression>>());
      LanguageLevelConverter.OPT = new Options(JavaVersion.JAVA_5, EmptyIterable.<File>make());
        _b._importedPackages.addFirst("java.lang");
      _sd1 = new SymbolData("i.like.monkey");
      _sd2 = new SymbolData("i.like.giraffe");
      _sd3 = new SymbolData("zebra");
      _sd4 = new SymbolData("u.like.emu");
      _sd5 = new SymbolData("");
      _sd6 = new SymbolData("cebu");
      _b._data = _sd1;
    }
    
    public void testForInitializedVariableDeclarator() {
      LanguageLevelVisitor llv =
        new LanguageLevelVisitor(_b._file, 
                                 _b._package, 
                                 _b._importedFiles, 
                                 _b._importedPackages, new LinkedList<String>(), 
                                 new Hashtable<String, Pair<TypeDefBase, LanguageLevelVisitor>>(), 
                                 new Hashtable<String, Pair<SourceInfo, LanguageLevelVisitor>>());
      

      
      SourceInfo si = SourceInfo.NO_INFO;
      Expression e1 = new IntegerLiteral(si, 1);
      Expression e2 = new IntegerLiteral(si, 2);
      Expression e3 = new PlusExpression(si, new IntegerLiteral(si, 3), new CharLiteral(si, 'e'));
      Expression e4 = new CharLiteral(si, 'c');

      ArrayType intArrayType = 
        new ArrayType(SourceInfo.NO_INFO, "int[]", new PrimitiveType(SourceInfo.NO_INFO, "int"));

      
      ArrayData intArray = new ArrayData(SymbolData.INT_TYPE, llv, si);
      intArray.setIsContinuation(false);
      symbolTable.remove("int[]");
      symbolTable.put("int[]", intArray);
      
      _b._data.addVar(new VariableData("foozle", _publicMav, intArray, false, _b._data));
      InitializedVariableDeclarator ivd = 
        new InitializedVariableDeclarator(SourceInfo.NO_INFO, intArrayType,
                                          new Word(SourceInfo.NO_INFO, "foozle"),
                                          new ArrayInitializer(si, new VariableInitializerI[] {e1, e2, e3, e4}));

      assertEquals("Should return null", null, ivd.visit(_b));
      assertEquals("There should be no errors", 0, errors.size());
    }
    
    public void testForInitializedVariableDeclaratorOnly() {
      SymbolData sd1 = SymbolData.DOUBLE_TYPE;
      SymbolData sd2 = SymbolData.BOOLEAN_TYPE;
      SymbolData sd3 = SymbolData.INT_TYPE;
      _b._data.addVar(new VariableData("j", _publicMav, SymbolData.DOUBLE_TYPE, false, _b._data));
      
      InitializedVariableDeclarator ivd = 
        new InitializedVariableDeclarator(SourceInfo.NO_INFO,
                                          JExprParser.NO_TYPE,
                                          new Word(SourceInfo.NO_INFO, "j"),
                                          new DoubleLiteral(SourceInfo.NO_INFO, 1.0));
      

      assertEquals("Two assignable types should not throw an error; return null.", null, 
                   _b.forInitializedVariableDeclaratorOnly(ivd, sd1, sd1, sd3.getInstanceData()));
      assertEquals("Should be no errors", 0, errors.size());
      
      assertEquals("Two unassignable types should throw an error; return null.", null, 
                   _b.forInitializedVariableDeclaratorOnly(ivd, sd1, sd1, sd2.getInstanceData()));
      assertEquals("Should now be one error", 1, errors.size());
      assertEquals("Error message should be correct:", "Type: \"double\" expected, instead found type: \"boolean\".", 
                   errors.getLast().getFirst());

      SymbolData foo = new SymbolData("Foo");
      assertEquals("An initialization from a SymbolData should return null", null, 
                   _b.forInitializedVariableDeclaratorOnly(ivd, sd1, null, foo));
      assertEquals("There should be 2 errors", 2, errors.size());
      assertEquals("Error message should be correct:", 
                   "Field or variable j cannot be initialized with the class or interface name Foo.  " + 
                   "Perhaps you meant to create an instance or use Foo.class", errors.getLast().getFirst());
    }
        
    public void testForThrowStatementOnly() {
      ThrowStatement s = new ThrowStatement(SourceInfo.NO_INFO, new NullLiteral(SourceInfo.NO_INFO));
      SymbolData exception = _b.getSymbolData("java.lang.Throwable", s, false, true); 
      InstanceData exceptionInstance = exception.getInstanceData();
      
      SymbolData notAnException = new SymbolData("bob");
      InstanceData naeInstance = notAnException.getInstanceData();

      
      assertEquals("When a SymbolData is the thrown type, return its InstanceData", exceptionInstance, 
                   _b.forThrowStatementOnly(s, exception));
      assertEquals("There should be 1 error", 1, errors.size());
      
      assertEquals("Error message should be correct", 
                   "You cannot throw a class or interface name.  " + 
                   "Perhaps you mean to instantiate the exception class java.lang.Throwable that you are throwing", 
                   errors.get(0).getFirst());

      assertEquals("When a thrown type does not implement Throwable, return the type anyway", naeInstance, 
                   _b.forThrowStatementOnly(s, naeInstance));
      assertEquals("There should be 2 errors", 2, errors.size());
      assertEquals("Error message should be correct", 
                   "You are attempting to throw bob, which does not implement the Throwable interface", 
                   errors.getLast().getFirst());
    }
      
  
    public void testForArrayInitializerHelper() {
      LanguageLevelVisitor llv =
        new LanguageLevelVisitor(_b._file, _b._package, _b._importedFiles, 
                                 _b._importedPackages, new LinkedList<String>(), 
                                 new Hashtable<String, Pair<TypeDefBase, LanguageLevelVisitor>>(), 
                                 new Hashtable<String, Pair<SourceInfo, LanguageLevelVisitor>>());

      
      SourceInfo si = SourceInfo.NO_INFO;
      
      Expression e1 = new IntegerLiteral(si, 1);
      Expression e2 = new IntegerLiteral(si, 2);
      Expression e3 = new PlusExpression(si, new IntegerLiteral(si, 3), new CharLiteral(si, 'e'));
      Expression e4 = new CharLiteral(si, 'c');
      Expression e5 = new DoubleLiteral(si, 5.8);
      Expression e6 = new SimpleNameReference(SourceInfo.NO_INFO, new Word(SourceInfo.NO_INFO, "int"));

      ArrayInitializer a1 = new ArrayInitializer(si, new VariableInitializerI[] {e1, e3, e4});
      ArrayInitializer a2 = new ArrayInitializer(si, new VariableInitializerI[] {e2, e3, e1});
      
      Expression nl = new NullLiteral(si);

      
      ArrayData intArray = new ArrayData(SymbolData.INT_TYPE, llv, si);
      intArray.setIsContinuation(false);
      symbolTable.remove("int[]");
      symbolTable.put("int[]", intArray);
      
      ArrayInitializer ia = new ArrayInitializer(si, new VariableInitializerI[] {e1, e2, e3, e4});
      assertEquals("Should return instance of int[]", intArray.getInstanceData(), 
                   _b.forArrayInitializerHelper(ia, intArray));
      assertEquals("There should be no errors", 0, errors.size());
      
      
      ArrayData intArray2 = new ArrayData(intArray, llv, si);
      intArray2.setIsContinuation(false);
      symbolTable.put("int[][]", intArray2);
      
      ia = new ArrayInitializer(si, new VariableInitializerI[]{a1, a2});
      assertEquals("Should return instance of int[][]", intArray2.getInstanceData(), 
                   _b.forArrayInitializerHelper(ia, intArray2));
      assertEquals("There should be no errors", 0, errors.size());
      
      
      ia = new ArrayInitializer(si, new VariableInitializerI[] {nl, nl});
      assertEquals("Should return instance of int[][]", intArray2.getInstanceData(), 
                   _b.forArrayInitializerHelper(ia, intArray2));
      
      
      assertEquals("Should return double", SymbolData.DOUBLE_TYPE.getInstanceData(), 
                   _b.forArrayInitializerHelper(ia, SymbolData.DOUBLE_TYPE));
      assertEquals("There should be one error message", 1, errors.size());
      assertEquals("The error message should be correct", 
                   "You cannot initialize the non-array type double with an array initializer", 
                   errors.getLast().getFirst());

      
      ia = new ArrayInitializer(si, new VariableInitializerI[] {e1, e2, e5, e4});
      assertEquals("Should return instance of int[]", intArray.getInstanceData(), 
                   _b.forArrayInitializerHelper(ia, intArray));
      assertEquals("There should be two error messages", 2, errors.size());
      assertEquals("The error message should be correct", 
                   "The elements of this initializer should have type int but element 2 has type double", 
                   errors.getLast().getFirst());

      
      ia = new ArrayInitializer(si, new VariableInitializerI[] {nl, nl});
      assertEquals("Should return instance of int[]", intArray.getInstanceData(),
                   _b.forArrayInitializerHelper(ia, intArray));
      assertEquals("There should be four error messages", 4, errors.size());
      assertEquals("The error message should be correct", 
                   "The elements of this initializer should have type int but element 0 has type null", 
                   errors.get(2).getFirst());
      assertEquals("The error message should be correct", 
                   "The elements of this initializer should have type int but element 1 has type null", 
                   errors.get(3).getFirst());
      
      
      ia = new ArrayInitializer(si, new VariableInitializerI[] {e1, e2, e3, e4, e6});
      assertEquals("Should return instance of int[]", intArray.getInstanceData(), 
                   _b.forArrayInitializerHelper(ia, intArray));
      assertEquals("Should now be 5 error messages", 5, errors.size());
      assertEquals("Error message should be correct", 
                   "The elements of this initializer should all be instances, but you have specified the type name" + 
                   " int.  Perhaps you meant to create a new instance of int",
                   errors.getLast().getFirst());
    }

    public void testFindClassReference() {
      SymbolData string = new SymbolData("java.lang.String");
      string.setIsContinuation(false);
      string.setPackage("java.lang");
      symbolTable.remove("java.lang.String");
      symbolTable.put("java.lang.String", string);
      
      
      assertEquals("Should return string", string,
                   _b.findClassReference(null, "java.lang.String", new NullLiteral(SourceInfo.NO_INFO)));
      assertEquals("Should not be an error", 0, errors.size());
      
      
      assertEquals("Should return null", null, 
                   _b.findClassReference(null, "non-existant", new NullLiteral(SourceInfo.NO_INFO)));
      assertEquals("Should be no errors", 0, errors.size());
      
      
      assertEquals("Should return string", string,
                   _b.findClassReference(new PackageData("java.lang"), "String", new NullLiteral(SourceInfo.NO_INFO)));
      assertEquals("Should not be an error", 0, errors.size());
      
      
      assertEquals("Should return null", null, 
                   _b.findClassReference(new PackageData("nonsense"), "non-existant", 
                                         new NullLiteral(SourceInfo.NO_INFO)));
      assertEquals("Should be no errors", 0, errors.size());
      
      
      SymbolData inner = new SymbolData("java.lang.String$Inner");
      inner.setIsContinuation(false);
      inner.setPackage("java.lang");
      inner.setOuterData(string);
      string.addInnerClass(inner);
      assertEquals("Should return inner", inner,
                   _b.findClassReference(string, "Inner", new NullLiteral(SourceInfo.NO_INFO)));
      assertEquals("Should be no errors", 0, errors.size());
      
      
      assertEquals("Should return null", null, 
                   _b.findClassReference(string, "non-existant", new NullLiteral(SourceInfo.NO_INFO)));
      assertEquals("Should be no errors", 0, errors.size());
    }
  }
}
