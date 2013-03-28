
package net.sourceforge.pmd.renderers;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.util.datasource.DataSource;


public abstract class AbstractIncrementingRenderer extends AbstractRenderer {

    
    protected List<Report.ProcessingError> errors = new LinkedList<Report.ProcessingError>();

    
    protected List<Report.SuppressedViolation> suppressed = new LinkedList<Report.SuppressedViolation>();

    public AbstractIncrementingRenderer(String name, String description, Properties properties) {
	super(name, description, properties);
    }

    
    public void start() throws IOException {
    }

    
    public void startFileAnalysis(DataSource dataSource) {
    }

    
    public void renderFileReport(Report report) throws IOException {
	Iterator<RuleViolation> violations = report.iterator();
	if (violations.hasNext()) {
	    renderFileViolations(violations);
	    getWriter().flush();
	}

	for (Iterator<Report.ProcessingError> i = report.errors(); i.hasNext();) {
	    errors.add(i.next());
	}

	if (showSuppressedViolations) {
	    suppressed.addAll(report.getSuppressedRuleViolations());
	}
    }

    
    public abstract void renderFileViolations(Iterator<RuleViolation> violations) throws IOException;

    
    public void end() throws IOException {
    }
}
