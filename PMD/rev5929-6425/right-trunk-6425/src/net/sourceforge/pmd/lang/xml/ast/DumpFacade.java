package net.sourceforge.pmd.lang.xml.ast;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.pmd.lang.ast.xpath.Attribute;

public class DumpFacade {

    private PrintWriter writer;
    private boolean recurse;

    public void initializeWith(Writer writer, String prefix, boolean recurse, XmlNode node) {
	this.writer = (writer instanceof PrintWriter) ? (PrintWriter) writer : new PrintWriter(writer);
	this.recurse = recurse;
	this.dump(node, prefix);
	try {
	    writer.flush();
	} catch (IOException e) {
	    throw new RuntimeException("Problem flushing PrintWriter.", e);
	}
    }

    public Object visit(XmlNode node, Object data) {
	dump(node, (String) data);
	if (recurse) {
	    for (int i = 0; i < node.jjtGetNumChildren(); i++) {
		visit((XmlNode) node.jjtGetChild(i), data + " ");
	    }
	    return data;
	} else {
	    return data;
	}
    }

    private void dump(XmlNode node, String prefix) {
	
	
	

	
	writer.print(prefix);

	
	writer.print(node.toString());

	
	
	
	
	
	

	
	String image = node.getImage();

	

	
	if (image != null) {
	    image = image.replace("\n", "\\n");
	    image = image.replace("\r", "\\r");
	    image = image.replace("\t", "\\t");
	}

	
	List<String> extras = new ArrayList<String>();
	Iterator<Attribute> iterator = node.getAttributeIterator();
	while (iterator.hasNext()) {
	    Attribute attribute = iterator.next();
	    extras.add(attribute.getName() + "=" + attribute.getValue());
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
