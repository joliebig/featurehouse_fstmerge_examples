

package net.sourceforge.pmd.ast;

import net.sourceforge.pmd.typeresolution.ClassTypeResolver;


public class ASTCompilationUnit extends SimpleJavaTypeNode implements CompilationUnit {

    private ClassTypeResolver classTypeResolver;

    public ASTCompilationUnit(int id) {
        super(id);
    }

    public ASTCompilationUnit(JavaParser p, int id) {
        super(p, id);
    }


    
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    public boolean declarationsAreInDefaultPackage() {
        return getPackageDeclaration() == null;
    }

    public ASTPackageDeclaration getPackageDeclaration() {
        return getFirstChildOfType(ASTPackageDeclaration.class);
    }

    public ClassTypeResolver getClassTypeResolver() {
    	return classTypeResolver;
    }

    public void setClassTypeResolver(ClassTypeResolver classTypeResolver) {
    	this.classTypeResolver = classTypeResolver;
    }
}
