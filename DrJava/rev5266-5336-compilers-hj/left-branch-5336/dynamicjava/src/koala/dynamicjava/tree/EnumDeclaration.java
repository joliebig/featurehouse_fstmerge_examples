

package koala.dynamicjava.tree;

import java.util.*;

import koala.dynamicjava.tree.tiger.GenericReferenceTypeName;
import koala.dynamicjava.tree.visitor.Visitor;

import static koala.dynamicjava.tree.ModifierSet.Modifier.*;



public class EnumDeclaration extends ClassDeclaration {
  
  
  public EnumDeclaration(ModifierSet mods, String name, List<? extends ReferenceTypeName> impl, EnumBody body) {
    this(mods, name, impl, body, SourceInfo.NONE);
  }


















 
  public EnumDeclaration(ModifierSet mods, String name, List<? extends ReferenceTypeName> impl, EnumBody body,
                          SourceInfo si) {
    
    
    
    
    
    
    
    
    
    super(mods, 
          name, makeTypeName(name), impl,
      AddValues(name,
        HandleConstructors(name,
          makeEnumBodyDeclarationsFromEnumConsts(name, body)),
        body.getConstants()),
      si);
  }
  
  private static ReferenceTypeName makeTypeName(String name) {
    List<Identifier> c = Arrays.asList(new Identifier("java"), new Identifier("lang"), new Identifier("Enum"));
    List<TypeName> emptyTargs = Collections.emptyList(); 
    List<ReferenceTypeName> targs = Arrays.asList(new ReferenceTypeName(name));
    List<List<? extends TypeName>> allTArgs = new LinkedList<List<? extends TypeName>>();
    allTArgs.add(emptyTargs);
    allTArgs.add(emptyTargs);
    allTArgs.add(targs);
    return new GenericReferenceTypeName(c, allTArgs);
  }

  static List<Node> AddValues(String enumTypeName, List<Node> body, List<EnumConstant> consts){
    List<Node> newbody = body;

    
    ReferenceTypeName enumType = new ReferenceTypeName(enumTypeName);
    List<Expression> cells = new LinkedList<Expression>();
    for(EnumConstant c : consts) {
      cells.add(new StaticFieldAccess(enumType, c.getName()));
    }
    
    Expression alloc = new ArrayAllocation(enumType,
                                           new ArrayAllocation.TypeDescriptor(Collections.<Expression>emptyList(), 1,
                                                                              new ArrayInitializer(cells),
                                                                              SourceInfo.NONE));
    Statement valuesBody = new ReturnStatement(alloc);
    newbody.add(new MethodDeclaration(ModifierSet.make(PUBLIC, STATIC),
                                      new ArrayTypeName(enumType, 1, false),
                                      "values",
                                      Collections.<FormalParameter>emptyList(),
                                      Collections.<ReferenceTypeName>emptyList(),
                                      new BlockStatement(Collections.<Node>singletonList(valuesBody))));
               
    
    
    
    
    
    
    FormalParameter nameParam = new FormalParameter(ModifierSet.make(),
                                                    new ReferenceTypeName("java", "lang", "String"),
                                                    "name");
    List<Node> valueOfBody = new LinkedList<Node>();
    for (EnumConstant c : consts) {
      String cn = c.getName();
      Expression cond = new ObjectMethodCall(new StringLiteral("\"" + cn + "\""), "equals",
                                             Collections.singletonList(new VariableAccess("name")));
      Statement ret = new ReturnStatement(new StaticFieldAccess(enumType, cn));
      valueOfBody.add(new IfThenStatement(cond, ret));
    }
    valueOfBody.add(new ThrowStatement(new SimpleAllocation(new ReferenceTypeName("IllegalArgumentException"),
                                                            Collections.<Expression>emptyList())));
    newbody.add(new MethodDeclaration(ModifierSet.make(PUBLIC, STATIC),
                                      enumType,
                                      "valueOf",
                                      Collections.singletonList(nameParam),
                                      Collections.<ReferenceTypeName>emptyList(),
                                      new BlockStatement(valueOfBody)));
    return newbody;
  }

  static List<Node> HandleConstructors(String name, List<Node> body){
    Iterator<Node> it = body.listIterator();

    List<FormalParameter> addToConsDeclaration = new LinkedList<FormalParameter>();
    addToConsDeclaration.add(new FormalParameter(ModifierSet.make(), new ReferenceTypeName("String"), "$1"));
    addToConsDeclaration.add(new FormalParameter(ModifierSet.make(), new IntTypeName(),               "$2"));

    List<Expression> args = new LinkedList<Expression>();
    args.add(new AmbiguousName("$1"));
    args.add(new AmbiguousName("$2"));

    List<FormalParameter> consParams;
    boolean noConstructor = true;

    while(it.hasNext()) {
      Node current = it.next();
      if (current instanceof ConstructorDeclaration) {
        noConstructor = false;

        consParams = ((ConstructorDeclaration)current).getParameters();
        List<FormalParameter> newConsParam = new LinkedList<FormalParameter>();

        newConsParam.addAll(addToConsDeclaration);
        newConsParam.addAll(consParams);

        ((ConstructorDeclaration)current).setParameters(newConsParam);

        ((ConstructorDeclaration)current).setConstructorCall(new ConstructorCall(null, args, true));
      }
    }

    if (noConstructor) {
      body.add(new ConstructorDeclaration(ModifierSet.make(PRIVATE), name, addToConsDeclaration,
                                          new LinkedList<ReferenceTypeName>(),
                                          new ConstructorCall(null, args, true),
                                          new LinkedList<Node>()));
    }
    return body;
  }

  public static class EnumConstant extends Declaration {
    private String name;
    private List<Expression> args;
    private List<Node> classBody;

    public EnumConstant(ModifierSet mods, String _name, List<? extends Expression> _args, List<Node> _classBody,
                        SourceInfo si) {
      super(mods, si);
      name = _name;
      args = (_args == null) ? null : new ArrayList<Expression>(_args);
      classBody = _classBody;
    }

    public String           getName() {return name;}
    public List<Expression> getArguments() {return args;}
    public List<Node>        getClassBody() {return classBody;}
    
    public <T> T acceptVisitor(Visitor<T> visitor) {
      return visitor.visit(this);
    }
  }

  public static class EnumBody {
    private List<EnumConstant> consts;
    private List<Node> decls;

    public EnumBody(List<EnumConstant> c, List<Node> d){
      consts = c;
      decls = d;
    }

    public List<EnumConstant> getConstants() {
      return consts;
    }

    public List<Node> getDeclarations(){
      return decls;
    }
  }

  static List<Node> makeEnumBodyDeclarationsFromEnumConsts(String enumTypeName, EnumBody body) {
    List<EnumConstant> consts = body.getConstants();
    List<Node> decls = body.getDeclarations();

    ReferenceTypeName enumType = new ReferenceTypeName(enumTypeName);

    SimpleAllocation allocExpr = null;

    Iterator<EnumConstant> it = consts.listIterator();
    int i = 0;
    while(it.hasNext()){
      List<Expression> args = new LinkedList<Expression>();
      EnumConstant ec = it.next();

      args.add(new StringLiteral("\""+ec.getName()+"\""));
      args.add(new IntegerLiteral(String.valueOf(i++)));

      if(ec.getArguments() != null){
        args.addAll(ec.getArguments());
      }

      if (ec.getClassBody() != null){
        allocExpr = new AnonymousAllocation(enumType, args, ec.getClassBody());
      }
      else {
        allocExpr = new SimpleAllocation(enumType, args);
      }

      decls.add(new FieldDeclaration(ModifierSet.make(PUBLIC, STATIC, FINAL, ENUM), enumType, ec.getName(), allocExpr));
    }
    return decls;
  }
}
