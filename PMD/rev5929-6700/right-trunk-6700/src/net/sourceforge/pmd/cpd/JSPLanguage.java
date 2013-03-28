
package net.sourceforge.pmd.cpd;

public class JSPLanguage extends AbstractLanguage {
	public JSPLanguage() {
		super(new JSPTokenizer(), ".jsp", ".jspx");
	}
}
