package net.sourceforge.pmd.lang;


public interface XPathFunctionRegister {

    XPathFunctionRegister DUMMY = new XPathFunctionRegister() {
	public void register() {
	}
    };

    
    void register();
}
