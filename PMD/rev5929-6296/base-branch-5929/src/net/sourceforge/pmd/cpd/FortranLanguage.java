
package net.sourceforge.pmd.cpd;

public class FortranLanguage extends AbstractLanguage {
	public FortranLanguage() {
		super(new FortranTokenizer(), ".for");
	}
}
