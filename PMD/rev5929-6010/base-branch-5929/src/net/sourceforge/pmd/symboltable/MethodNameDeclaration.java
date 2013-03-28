
package net.sourceforge.pmd.symboltable;

import net.sourceforge.pmd.ast.ASTFormalParameter;
import net.sourceforge.pmd.ast.ASTFormalParameters;
import net.sourceforge.pmd.ast.ASTMethodDeclarator;
import net.sourceforge.pmd.ast.ASTPrimitiveType;
import net.sourceforge.pmd.ast.SimpleNode;

public class MethodNameDeclaration extends AbstractNameDeclaration {

    public MethodNameDeclaration(ASTMethodDeclarator node) {
        super(node);
    }

    public int getParameterCount() {
        return ((ASTMethodDeclarator) node).getParameterCount();
    }

    public boolean isVarargs() {
        ASTFormalParameters params = (ASTFormalParameters) node.jjtGetChild(0);
        for (int i = 0; i < ((ASTMethodDeclarator) node).getParameterCount(); i++) {
            ASTFormalParameter p = (ASTFormalParameter) params.jjtGetChild(i);
            if (p.isVarargs()) {
            	return true;
            }
        }
        return false;
    }

    public ASTMethodDeclarator getMethodNameDeclaratorNode() {
        return (ASTMethodDeclarator) node;
    }

    public String getParameterDisplaySignature() {
        StringBuffer sb = new StringBuffer("(");
        ASTFormalParameters params = (ASTFormalParameters) node.jjtGetChild(0);
        
        
        for (int i = 0; i < ((ASTMethodDeclarator) node).getParameterCount(); i++) {
            ASTFormalParameter p = (ASTFormalParameter) params.jjtGetChild(i);
            sb.append(p.getTypeNode().getTypeImage());
            if (p.isVarargs()) {
            	sb.append("...");
            }
            sb.append(',');
        }
        if (sb.charAt(sb.length() - 1) == ',') {
            sb.deleteCharAt(sb.length() - 1);
        }
        sb.append(')');
        return sb.toString();
    }

    public boolean equals(Object o) {
        MethodNameDeclaration other = (MethodNameDeclaration) o;

        
        if (!other.node.getImage().equals(node.getImage())) {
            return false;
        }

        
        if (((ASTMethodDeclarator) (other.node)).getParameterCount() != ((ASTMethodDeclarator) node).getParameterCount()) {
            return false;
        }

        
        ASTFormalParameters myParams = (ASTFormalParameters) node.jjtGetChild(0);
        ASTFormalParameters otherParams = (ASTFormalParameters) other.node.jjtGetChild(0);
        for (int i = 0; i < ((ASTMethodDeclarator) node).getParameterCount(); i++) {
            ASTFormalParameter myParam = (ASTFormalParameter) myParams.jjtGetChild(i);
            ASTFormalParameter otherParam = (ASTFormalParameter) otherParams.jjtGetChild(i);

            
            if (myParam.isVarargs() != otherParam.isVarargs()) {
            	return false;
            }

            SimpleNode myTypeNode = (SimpleNode) myParam.getTypeNode().jjtGetChild(0);
            SimpleNode otherTypeNode = (SimpleNode) otherParam.getTypeNode().jjtGetChild(0);

            
            if (myTypeNode.getClass() != otherTypeNode.getClass()) {
                return false;
            }

            
            
            
            
            String myTypeImg;
            String otherTypeImg;
            if (myTypeNode instanceof ASTPrimitiveType) {
                myTypeImg = myTypeNode.getImage();
                otherTypeImg = otherTypeNode.getImage();
            } else {
                myTypeImg = ((SimpleNode) (myTypeNode.jjtGetChild(0))).getImage();
                otherTypeImg = ((SimpleNode) (otherTypeNode.jjtGetChild(0))).getImage();
            }

            if (!myTypeImg.equals(otherTypeImg)) {
                return false;
            }

            
        }
        return true;
    }

    public int hashCode() {
        return node.getImage().hashCode() + ((ASTMethodDeclarator) node).getParameterCount();
    }

    public String toString() {
        return "Method " + node.getImage() + ", line " + node.getBeginLine() + ", params = " + ((ASTMethodDeclarator) node).getParameterCount();
    }
}
