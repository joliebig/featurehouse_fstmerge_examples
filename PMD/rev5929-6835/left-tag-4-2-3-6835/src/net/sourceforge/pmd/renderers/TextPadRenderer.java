
package net.sourceforge.pmd.renderers;

import net.sourceforge.pmd.IRuleViolation;
import net.sourceforge.pmd.PMD;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;


public class TextPadRenderer extends OnTheFlyRenderer {

    public void start() throws IOException {}

    public void renderFileViolations(Iterator<IRuleViolation> violations) throws IOException {
        Writer writer = getWriter();
        StringBuffer buf = new StringBuffer();
        while (violations.hasNext()) {
            IRuleViolation rv = violations.next();
            buf.setLength(0);
            
            buf.append(PMD.EOL).append(rv.getFilename() + "(");
            
            buf.append(Integer.toString(rv.getBeginLine())).append(",  ");
            
            buf.append(rv.getRule().getName()).append("):  ");
            
            buf.append(rv.getDescription());
            writer.write(buf.toString());
        }
    }

    public void end() throws IOException {}
}
