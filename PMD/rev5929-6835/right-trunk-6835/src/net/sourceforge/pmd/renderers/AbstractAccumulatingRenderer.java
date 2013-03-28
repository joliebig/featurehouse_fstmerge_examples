
package net.sourceforge.pmd.renderers;

import java.io.IOException;
import java.util.Properties;

import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.util.datasource.DataSource;


public abstract class AbstractAccumulatingRenderer extends AbstractRenderer {

    
    protected Report report;

    public AbstractAccumulatingRenderer(String name, String description, Properties properties) {
	super(name, description, properties);
    }

    
    public void start() throws IOException {
	report = new Report();
    }

    
    public void startFileAnalysis(DataSource dataSource) {
    }

    
    public void renderFileReport(Report report) throws IOException {
	this.report.merge(report);
    }

    
    public abstract void end() throws IOException;
}
