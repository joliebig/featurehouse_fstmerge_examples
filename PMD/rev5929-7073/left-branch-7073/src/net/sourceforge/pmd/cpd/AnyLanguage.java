
package net.sourceforge.pmd.cpd;

public class AnyLanguage extends AbstractLanguage {
	public AnyLanguage(String... extensions) {
		super(new AnyTokenizer(), extensions);
	}
}
