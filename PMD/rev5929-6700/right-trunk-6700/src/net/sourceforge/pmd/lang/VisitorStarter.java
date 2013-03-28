package net.sourceforge.pmd.lang;

import net.sourceforge.pmd.lang.ast.Node;


public interface VisitorStarter {

    VisitorStarter DUMMY = new VisitorStarter() {
	public void start(Node rootNode) {
	}
    };

    
    void start(Node rootNode);
}
