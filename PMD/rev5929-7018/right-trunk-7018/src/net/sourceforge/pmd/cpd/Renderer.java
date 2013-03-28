
package net.sourceforge.pmd.cpd;

import java.util.Iterator;


public interface Renderer {
    String render(Iterator<Match> matches);
}
