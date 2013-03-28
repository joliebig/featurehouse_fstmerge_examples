package net.sourceforge.pmd.sourcetypehandlers;


public interface VisitorStarter {

	VisitorStarter dummy = new VisitorStarter() { public void start(Object rootNode) {} };
	
    
    void start(Object rootNode);

}
