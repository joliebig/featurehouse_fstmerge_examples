package net.sourceforge.pmd.lang.java.ast;


public class DummyJavaNode extends AbstractJavaNode {

    public DummyJavaNode(int id) {
	super(id);
    }

    public DummyJavaNode(JavaParser parser, int id) {
	super(parser, id);
    }
}
