
package net.sourceforge.pmd.lang.java.symboltable;

import java.util.List;
import java.util.Map;


public interface Scope {

    
    Map<VariableNameDeclaration, List<NameOccurrence>> getVariableDeclarations();

    
    Map<ClassNameDeclaration, List<NameOccurrence>> getClassDeclarations();

    
    void addDeclaration(ClassNameDeclaration decl);

    
    void addDeclaration(VariableNameDeclaration decl);

    
    void addDeclaration(MethodNameDeclaration decl);

    
    boolean contains(NameOccurrence occ);

    
    NameDeclaration addVariableNameOccurrence(NameOccurrence occ);

    
    void setParent(Scope parent);

    
    Scope getParent();

    
    ClassScope getEnclosingClassScope();

    
    SourceFileScope getEnclosingSourceFileScope();

    
    MethodScope getEnclosingMethodScope();
}
