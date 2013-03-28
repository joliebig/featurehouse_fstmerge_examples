
package net.sourceforge.pmd.cpd;

public class PHPLanguage extends AbstractLanguage {
	public PHPLanguage() {
		super(new PHPTokenizer(), ".php", ".class");
	}
}
