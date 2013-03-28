
package net.sourceforge.pmd.renderers;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;

import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.util.datasource.DataSource;



public interface Renderer {

    
    String getName();

    
    void setName(String name);

    
    String getDescription();

    
    void setDescription(String description);

    
    Map<String, String> getPropertyDefinitions();

    
    boolean isShowSuppressedViolations();

    
    void setShowSuppressedViolations(boolean showSuppressedViolations);

    
    Writer getWriter();

    
    void setWriter(Writer writer);

    
    void start() throws IOException;

    
    void startFileAnalysis(DataSource dataSource);

    
    void renderFileReport(Report report) throws IOException;

    
    void end() throws IOException;
}
