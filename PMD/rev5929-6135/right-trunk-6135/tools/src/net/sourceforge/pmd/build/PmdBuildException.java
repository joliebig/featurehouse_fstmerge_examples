
package net.sourceforge.pmd.build;



public class PmdBuildException extends Exception {

	
	private static final long serialVersionUID = 1L;

	public PmdBuildException(String message){
		super(message);
	}

	public PmdBuildException(Throwable e) {
		super(e);
	}
}
