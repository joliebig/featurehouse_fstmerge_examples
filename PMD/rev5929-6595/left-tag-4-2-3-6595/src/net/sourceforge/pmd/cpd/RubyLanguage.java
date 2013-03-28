
package net.sourceforge.pmd.cpd;

public class RubyLanguage extends AbstractLanguage {
	public RubyLanguage() {
		super(new RubyTokenizer(), ".rb", ".cgi", ".class");
	}
}
