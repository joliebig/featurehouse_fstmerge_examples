
package net.sourceforge.pmd.renderers;

import java.io.IOException;
import java.util.Properties;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.lang.dfa.report.ReportHTMLPrintVisitor;
import net.sourceforge.pmd.lang.dfa.report.ReportTree;


public class YAHTMLRenderer extends AbstractAccumulatingRenderer {

    public static final String NAME = "yahtml";

    public static final String OUTPUT_DIR = "outputDir";

    private String outputDir;

    public YAHTMLRenderer(Properties properties) {
	
	super(NAME, "Yet Another HTML format.", properties);
	defineProperty(OUTPUT_DIR, "Output directory.");

	this.outputDir = properties.getProperty(OUTPUT_DIR);
    }

    
    @Override
    public void end() throws IOException {
	ReportTree tree = report.getViolationTree();
	tree.getRootNode().accept(new ReportHTMLPrintVisitor(outputDir == null ? ".." : outputDir));
	writer.write("<h3 align=\"center\">The HTML files are located "
		+ (outputDir == null ? "above the project directory" : "in '" + outputDir + '\'') + ".</h3>" + PMD.EOL);
    }
}
