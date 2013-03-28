package net.sourceforge.pmd.lang.jsp.ast;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.pmd.lang.ast.Node;

public class DumpFacade extends JspParserVisitorAdapter {

    private PrintWriter writer;
    private boolean recurse;

    public void initializeWith(Writer writer, String prefix, boolean recurse, JspNode node) {
	this.writer = (writer instanceof PrintWriter) ? (PrintWriter) writer : new PrintWriter(writer);
	this.recurse = recurse;
	this.visit(node, prefix);
	try {
	    writer.flush();
	} catch (IOException e) {
	    throw new RuntimeException("Problem flushing PrintWriter.", e);
	}
    }

    @Override
    public Object visit(JspNode node, Object data) {
	dump(node, (String) data);
	if (recurse) {
	    return super.visit(node, data + " ");
	} else {
	    return data;
	}
    }

    private void dump(Node node, String prefix) {
	
	
	

	
	writer.print(prefix);

	
	writer.print(node.toString());

	
	
	
	
	
	

	
	String image = node.getImage();

	
	List<String> extras = new ArrayList<String>();

	
	if (node instanceof ASTAttribute) {
	    extras.add("name=[" + ((ASTAttribute) node).getName() + "]");
	} else if (node instanceof ASTDeclaration) {
	    extras.add("name=[" + ((ASTDeclaration) node).getName() + "]");
	} else if (node instanceof ASTDoctypeDeclaration) {
	    extras.add("name=[" + ((ASTDoctypeDeclaration) node).getName() + "]");
	} else if (node instanceof ASTDoctypeExternalId) {
	    extras.add("uri=[" + ((ASTDoctypeExternalId) node).getUri() + "]");
	    if (((ASTDoctypeExternalId) node).getPublicId().length() > 0) {
		extras.add("publicId=[" + ((ASTDoctypeExternalId) node).getPublicId() + "]");
	    }
	} else if (node instanceof ASTElement) {
	    extras.add("name=[" + ((ASTElement) node).getName() + "]");
	    if (((ASTElement) node).isEmpty()) {
		extras.add("empty");
	    }
	} else if (node instanceof ASTJspDirective) {
	    extras.add("name=[" + ((ASTJspDirective) node).getName() + "]");
	} else if (node instanceof ASTJspDirectiveAttribute) {
	    extras.add("name=[" + ((ASTJspDirectiveAttribute) node).getName() + "]");
	    extras.add("value=[" + ((ASTJspDirectiveAttribute) node).getValue() + "]");
	}

	
	if (image != null || !extras.isEmpty()) {
	    writer.print(":");
	    if (image != null) {
		writer.print(image);
	    }
	    for (String extra : extras) {
		writer.print("(");
		writer.print(extra);
		writer.print(")");
	    }
	}

	writer.println();
    }
}
