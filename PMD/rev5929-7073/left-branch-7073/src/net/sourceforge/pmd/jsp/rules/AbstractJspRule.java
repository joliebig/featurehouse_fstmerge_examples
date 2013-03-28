
package net.sourceforge.pmd.jsp.rules;

import java.text.MessageFormat;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.pmd.CommonAbstractRule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.ast.Node;
import net.sourceforge.pmd.jsp.ast.ASTAttribute;
import net.sourceforge.pmd.jsp.ast.ASTAttributeValue;
import net.sourceforge.pmd.jsp.ast.ASTCData;
import net.sourceforge.pmd.jsp.ast.ASTCommentTag;
import net.sourceforge.pmd.jsp.ast.ASTCompilationUnit;
import net.sourceforge.pmd.jsp.ast.ASTContent;
import net.sourceforge.pmd.jsp.ast.ASTDeclaration;
import net.sourceforge.pmd.jsp.ast.ASTDoctypeDeclaration;
import net.sourceforge.pmd.jsp.ast.ASTDoctypeExternalId;
import net.sourceforge.pmd.jsp.ast.ASTElExpression;
import net.sourceforge.pmd.jsp.ast.ASTElement;
import net.sourceforge.pmd.jsp.ast.ASTHtmlScript;
import net.sourceforge.pmd.jsp.ast.ASTJspComment;
import net.sourceforge.pmd.jsp.ast.ASTJspDeclaration;
import net.sourceforge.pmd.jsp.ast.ASTJspDirective;
import net.sourceforge.pmd.jsp.ast.ASTJspDirectiveAttribute;
import net.sourceforge.pmd.jsp.ast.ASTJspExpression;
import net.sourceforge.pmd.jsp.ast.ASTJspExpressionInAttribute;
import net.sourceforge.pmd.jsp.ast.ASTJspScriptlet;
import net.sourceforge.pmd.jsp.ast.ASTText;
import net.sourceforge.pmd.jsp.ast.ASTUnparsedText;
import net.sourceforge.pmd.jsp.ast.ASTValueBinding;
import net.sourceforge.pmd.jsp.ast.JspParserVisitor;
import net.sourceforge.pmd.jsp.ast.SimpleNode;

public abstract class AbstractJspRule extends CommonAbstractRule implements
		JspParserVisitor {

	@Override
	public void setUsesTypeResolution() {
		
	}

	
	protected final void addViolation(Object data, SimpleNode node) {
		RuleContext ctx = (RuleContext)data;
		ctx.getReport().addRuleViolation(new RuleViolation(this, ctx, node));
	}

	
	protected final void addViolationWithMessage(Object data, SimpleNode node,
			String msg) {
		RuleContext ctx = (RuleContext)data;
		ctx.getReport().addRuleViolation(
				new RuleViolation(this, ctx, node, msg));
	}

	
	protected final void addViolation(Object data, SimpleNode node, String embed) {
		RuleContext ctx = (RuleContext)data;
		ctx.getReport().addRuleViolation(
				new RuleViolation(this, ctx, node, MessageFormat.format(
						getMessage(), embed)));
	}

	
	protected final void addViolation(Object data, Node node, Object[] args) {
		RuleContext ctx = (RuleContext)data;
		ctx.getReport().addRuleViolation(
				new RuleViolation(this, ctx, (SimpleNode)node, MessageFormat
						.format(getMessage(), args)));
	}

	public void apply(List acus, RuleContext ctx) {
		visitAll(acus, ctx);
	}

	protected void visitAll(List acus, RuleContext ctx) {
		for (Iterator i = acus.iterator(); i.hasNext();) {
			SimpleNode node = (SimpleNode)i.next();
			visit(node, ctx);
		}
	}

	
	
	
	
	

	public Object visit(SimpleNode node, Object data) {
		node.childrenAccept(this, data);
		return null;
	}

	public Object visit(ASTCompilationUnit node, Object data) {
		return visit((SimpleNode)node, data);
	}

	public Object visit(ASTContent node, Object data) {
		return visit((SimpleNode)node, data);
	}

	public Object visit(ASTJspDirective node, Object data) {
		return visit((SimpleNode)node, data);
	}

	public Object visit(ASTJspDirectiveAttribute node, Object data) {
		return visit((SimpleNode)node, data);
	}

	public Object visit(ASTJspScriptlet node, Object data) {
		return visit((SimpleNode)node, data);
	}

	public Object visit(ASTJspExpression node, Object data) {
		return visit((SimpleNode)node, data);
	}

	public Object visit(ASTJspDeclaration node, Object data) {
		return visit((SimpleNode)node, data);
	}

	public Object visit(ASTJspComment node, Object data) {
		return visit((SimpleNode)node, data);
	}

	public Object visit(ASTText node, Object data) {
		return visit((SimpleNode)node, data);
	}

	public Object visit(ASTUnparsedText node, Object data) {
		return visit((SimpleNode)node, data);
	}

	public Object visit(ASTElExpression node, Object data) {
		return visit((SimpleNode)node, data);
	}

	public Object visit(ASTValueBinding node, Object data) {
		return visit((SimpleNode)node, data);
	}

	public Object visit(ASTCData node, Object data) {
		return visit((SimpleNode)node, data);
	}

	public Object visit(ASTElement node, Object data) {
		return visit((SimpleNode)node, data);
	}

	public Object visit(ASTAttribute node, Object data) {
		return visit((SimpleNode)node, data);
	}

	public Object visit(ASTAttributeValue node, Object data) {
		return visit((SimpleNode)node, data);
	}

	public Object visit(ASTJspExpressionInAttribute node, Object data) {
		return visit((SimpleNode)node, data);
	}

	public Object visit(ASTCommentTag node, Object data) {
		return visit((SimpleNode)node, data);
	}

	public Object visit(ASTDeclaration node, Object data) {
		return visit((SimpleNode)node, data);
	}

	public Object visit(ASTDoctypeDeclaration node, Object data) {
		return visit((SimpleNode)node, data);
	}

	public Object visit(ASTDoctypeExternalId node, Object data) {
		return visit((SimpleNode)node, data);
	}

	public Object visit(ASTHtmlScript node, Object data) {
		return visit((SimpleNode)node, data);
	}
}
