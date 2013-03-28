package net.sourceforge.pmd;

import java.util.List;

import net.sourceforge.pmd.ast.CompilationUnit;


public interface RuleChainVisitor {
    
    void add(Rule rule);

    
    void visitAll(List<CompilationUnit> astCompilationUnits, RuleContext ctx);
}
