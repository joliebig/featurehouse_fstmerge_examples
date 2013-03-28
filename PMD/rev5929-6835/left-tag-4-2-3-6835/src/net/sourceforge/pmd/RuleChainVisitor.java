package net.sourceforge.pmd;

import java.util.List;

import net.sourceforge.pmd.ast.CompilationUnit;


public interface RuleChainVisitor {
    
    void add(RuleSet ruleSet, Rule rule);

    
    void visitAll(List<CompilationUnit> astCompilationUnits, RuleContext ctx);
}
