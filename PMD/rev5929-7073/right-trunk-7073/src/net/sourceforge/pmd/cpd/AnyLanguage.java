
package net.sourceforge.pmd.cpd;

public class AnyLanguage extends AbstractLanguage {
	public AnyLanguage(String... extension) {
		super(new AnyTokenizer(), extension);
	}
}
