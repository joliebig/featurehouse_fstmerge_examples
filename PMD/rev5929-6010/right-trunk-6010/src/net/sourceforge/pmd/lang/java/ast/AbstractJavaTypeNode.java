package net.sourceforge.pmd.lang.java.ast;


public abstract class AbstractJavaTypeNode extends AbstractJavaNode implements TypeNode {

	public AbstractJavaTypeNode(int i) {
		super(i);
	}

	public AbstractJavaTypeNode(JavaParser p, int i) {
		super(p, i);
	}

	private Class type;

	public Class getType() {
		return type;
	}

	public void setType(Class type) {
		this.type = type;
	}
}
