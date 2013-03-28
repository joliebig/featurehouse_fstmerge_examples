
package net.sourceforge.pmd.renderers;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;
import java.util.Properties;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.RuleViolation;


public class TextPadRenderer extends AbstractIncrementingRenderer {

    public static final String NAME = "textpad";

    public TextPadRenderer(Properties properties) {
	super(NAME, "TextPad integration.", properties);
    }

    
    @Override
    public void renderFileViolations(Iterator<RuleViolation> violations) throws IOException {
	Writer writer = getWriter();
	StringBuffer buf = new StringBuffer();
	while (violations.hasNext()) {
	    RuleViolation rv = violations.next();
	    buf.setLength(0);
	    
	    buf.append(rv.getFilename() + "(");
	    
	    buf.append(Integer.toString(rv.getBeginLine())).append(",  ");
	    
	    buf.append(rv.getRule().getName()).append("):  ");
	    
	    buf.append(rv.getDescription()).append(PMD.EOL);
	    writer.write(buf.toString());
	}
    }
}
