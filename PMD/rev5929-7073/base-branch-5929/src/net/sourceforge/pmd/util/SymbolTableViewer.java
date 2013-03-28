package net.sourceforge.pmd.util;

import net.sourceforge.pmd.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.ast.ASTCompilationUnit;
import net.sourceforge.pmd.ast.JavaParserVisitorAdapter;

public class SymbolTableViewer extends JavaParserVisitorAdapter {

    private int depth;

    public Object visit(ASTCompilationUnit node, Object data) {
        depth++;
        System.out.println(spaces() + node.getScope());
        super.visit(node, data);
        depth--;
        return data;
    }

    public Object visit(ASTClassOrInterfaceDeclaration node, Object data) {
        depth++;
        System.out.println(spaces() + node.getScope());
        super.visit(node, data);
        depth--;
        return data;
    }

    private String spaces() {
        StringBuffer sb = new StringBuffer(depth);
        for (int i=0; i<depth; i++) sb.append(' ');
        return sb.toString();
    }



}
