package net.sourceforge.pmd.lang.jsp.rule;

import java.util.List;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.jsp.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.jsp.ast.JspNode;
import net.sourceforge.pmd.lang.jsp.ast.JspParserVisitor;
import net.sourceforge.pmd.lang.jsp.ast.JspParserVisitorAdapter;
import net.sourceforge.pmd.lang.rule.AbstractRuleChainVisitor;
import net.sourceforge.pmd.lang.rule.XPathRule;

public class JspRuleChainVisitor extends AbstractRuleChainVisitor {

    protected void indexNodes(List<Node> nodes, RuleContext ctx) {
        JspParserVisitor jspParserVisitor = new JspParserVisitorAdapter() {
            
            
            public Object visit(JspNode node, Object data) {
                indexNode(node);
                return super.visit(node, data);
            }
        };

        for (int i = 0; i < nodes.size(); i++) {
            jspParserVisitor.visit((ASTCompilationUnit)nodes.get(i), ctx);
        }
    }

    protected void visit(Rule rule, Node node, RuleContext ctx) {
        
        if (rule instanceof JspParserVisitor) {
            ((JspNode) node).jjtAccept((JspParserVisitor) rule, ctx);
        } else {
            ((XPathRule) rule).evaluate(node, ctx);
        }
    }
}
