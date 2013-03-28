
package net.sourceforge.pmd.symboltable;

import net.sourceforge.pmd.ast.*;

import java.util.List;
import java.util.Stack;


public class ScopeAndDeclarationFinder extends JavaParserVisitorAdapter {

    
    private Stack<Scope> scopes = new Stack<Scope>();

    
    private void addScope(Scope newScope, SimpleNode node) {
        newScope.setParent(scopes.peek());
        scopes.push(newScope);
        node.setScope(newScope);
    }

    
    private void createLocalScope(SimpleNode node) {
        addScope(new LocalScope(), node);
    }

    
    private void createMethodScope(SimpleNode node) {
        addScope(new MethodScope(node), node);
    }

    
    private void createClassScope(SimpleNode node) {
        if (node instanceof ASTClassOrInterfaceBodyDeclaration) {
            addScope(new ClassScope(), node);
        } else {
            addScope(new ClassScope(node.getImage()), node);
        }
    }

    
    private void createSourceFileScope(SimpleNode node) {
        
        Scope scope;
        List packages = node.findChildrenOfType(ASTPackageDeclaration.class);
        if (!packages.isEmpty()) {
            Node n = (Node) packages.get(0);
            scope = new SourceFileScope(((SimpleNode) n.jjtGetChild(0)).getImage());
        } else {
            scope = new SourceFileScope();
        }
        scopes.push(scope);
        node.setScope(scope);
    }

    public Object visit(ASTCompilationUnit node, Object data) {
        createSourceFileScope(node);
        cont(node);
        return data;
    }

    public Object visit(ASTClassOrInterfaceDeclaration node, Object data) {
        createClassScope(node);
        Scope s = ((SimpleNode) node.jjtGetParent()).getScope();
        s.addDeclaration(new ClassNameDeclaration(node));
        cont(node);
        return data;
    }

    public Object visit(ASTEnumDeclaration node, Object data) {
        createClassScope(node);
        cont(node);
        return data;
    }

    public Object visit(ASTAnnotationTypeDeclaration node, Object data) {
        createClassScope(node);
        cont(node);
        return data;
    }

    public Object visit(ASTClassOrInterfaceBodyDeclaration node, Object data) {
        if (node.isAnonymousInnerClass() || node.isEnumChild()) {
            createClassScope(node);
            cont(node);
        } else {
            super.visit(node, data);
        }
        return data;
    }

    public Object visit(ASTBlock node, Object data) {
        createLocalScope(node);
        cont(node);
        return data;
    }

    public Object visit(ASTCatchStatement node, Object data) {
        createLocalScope(node);
        cont(node);
        return data;
    }

    public Object visit(ASTFinallyStatement node, Object data) {
        createLocalScope(node);
        cont(node);
        return data;
    }

    public Object visit(ASTConstructorDeclaration node, Object data) {
        
        createMethodScope(node);

        Scope methodScope = node.getScope();

        Node formalParameters = node.jjtGetChild(0);
        int i = 1;
        int n = node.jjtGetNumChildren();
        if (!(formalParameters instanceof ASTFormalParameters)) {
            visit((ASTTypeParameters) formalParameters, data);
            formalParameters = node.jjtGetChild(1);
            i++;
        }
        visit((ASTFormalParameters)formalParameters, data);

        Scope localScope = null;
        for (;i<n;i++) {
            SimpleJavaNode b = (SimpleJavaNode) node.jjtGetChild(i);
            if (b instanceof ASTBlockStatement) {
                if (localScope == null) {
                    createLocalScope(node);
                    localScope = node.getScope();
                }
                b.setScope(localScope);
                visit(b, data);
            } else {
                visit(b, data);
            }
        }
        if (localScope != null) {
            
            scopes.pop();

            
            node.setScope(methodScope);            
        }
        
        scopes.pop();

        return data;
    }

    public Object visit(ASTMethodDeclaration node, Object data) {
        createMethodScope(node);
        ASTMethodDeclarator md = node.getFirstChildOfType(ASTMethodDeclarator.class);
        node.getScope().getEnclosingClassScope().addDeclaration(new MethodNameDeclaration(md));
        cont(node);
        return data;
    }

    public Object visit(ASTTryStatement node, Object data) {
        createLocalScope(node);
        cont(node);
        return data;
    }

    
    public Object visit(ASTForStatement node, Object data) {
        createLocalScope(node);
        cont(node);
        return data;
    }

    public Object visit(ASTIfStatement node, Object data) {
        createLocalScope(node);
        cont(node);
        return data;
    }

    public Object visit(ASTVariableDeclaratorId node, Object data) {
        VariableNameDeclaration decl = new VariableNameDeclaration(node);
        node.getScope().addDeclaration(decl);
        node.setNameDeclaration(decl);
        return super.visit(node, data);
    }

    public Object visit(ASTSwitchStatement node, Object data) {
        createLocalScope(node);
        cont(node);
        return data;
    }

    private void cont(SimpleJavaNode node) {
        super.visit(node, null);
        scopes.pop();
    }
}
