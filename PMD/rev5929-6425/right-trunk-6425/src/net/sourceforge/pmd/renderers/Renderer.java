
package net.sourceforge.pmd.renderers;

import java.io.IOException;
import java.io.Writer;

import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.util.datasource.DataSource;


public interface Renderer {

    
    void showSuppressedViolations(boolean show);

    
    String render(Report report);

    
    void render(Writer writer, Report report) throws IOException;

    
    void setWriter(Writer writer);

    
    Writer getWriter();

    
    void start() throws IOException;

    
    void startFileAnalysis(DataSource dataSource);

    
    void renderFileReport(Report report) throws IOException;

    
    void end() throws IOException;

}
