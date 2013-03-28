
package net.sourceforge.pmd.cpd;

public class JavascriptLanguage extends AbstractLanguage {
	public JavascriptLanguage() {
		super(new JavascriptTokenizer(), ".js");
	}
}
