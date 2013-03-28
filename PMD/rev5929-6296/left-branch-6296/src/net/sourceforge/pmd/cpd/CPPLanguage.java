
package net.sourceforge.pmd.cpd;

public class CPPLanguage extends AbstractLanguage {
	public CPPLanguage() {
		super(new CPPTokenizer(), ".h", ".c", ".cpp", ".cxx", ".cc", ".C");
	}
}
