
package net.sourceforge.pmd.cpd;

import java.util.Properties;

public class JavaLanguage extends AbstractLanguage {
	public JavaLanguage() {
		this(new Properties());
	}

	public JavaLanguage(Properties properties) {
		super(new JavaTokenizer(), ".java");
		JavaTokenizer tokenizer = (JavaTokenizer)getTokenizer();
		tokenizer.setProperties(properties);
	}
}
