package net.sourceforge.pmd.dfa.report;

import net.sourceforge.pmd.IRuleViolation;
import net.sourceforge.pmd.PMD;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


public class ReportHTMLPrintVisitor extends ReportVisitor {

    private StringBuffer packageBuf = new StringBuffer();
    private StringBuffer classBuf = new StringBuffer();
    private int length;
    private String baseDir;

    private static final String fs = System.getProperty("file.separator");

    public ReportHTMLPrintVisitor(String baseDir) {
        this.baseDir = baseDir;
    }

    
    private void write(String filename, StringBuffer buf) throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter(new File(baseDir + fs + filename)));
        bw.write(buf.toString(), 0, buf.length());
        bw.close();
    }
    
    
    private String displayRuleViolation(IRuleViolation vio) {
 
    	StringBuffer sb = new StringBuffer(200);
        sb.append("<table border=\"0\">");
        sb.append("<tr><td><b>Rule:</b></td><td>").append(vio.getRule().getName()).append("</td></tr>");
        sb.append("<tr><td><b>Description:</b></td><td>").append(vio.getDescription()).append("</td></tr>");

        if (vio.getVariableName().length() > 0) {
        	sb.append("<tr><td><b>Variable:</b></td><td>").append(vio.getVariableName()).append("</td></tr>");
        }

        if (vio.getEndLine() > 0) {
        	sb.append("<tr><td><b>Line:</b></td><td>").append(vio.getEndLine()).append(" and ").append(vio.getBeginLine()).append("</td></tr>");
        } else {
        	sb.append("<tr><td><b>Line:</b></td><td>").append(vio.getBeginLine()).append("</td></tr>");
        }

        sb.append("</table>");
        return sb.toString();
    }

    
    public void visit(AbstractReportNode node) {

        
        if (node.getParent() == null) {
            this.packageBuf.insert(0,
                    "<html>" +
                    " <head>" +
                    "   <title>PMD</title>" +
                    " </head>" +
                    " <body>" + PMD.EOL + 
                    "<h2>Package View</h2>" +
                    "<table border=\"1\" align=\"center\" cellspacing=\"0\" cellpadding=\"3\">" +
                    " <tr>" + PMD.EOL + 
                    "<th>Package</th>" +
                    "<th>Class</th>" +
                    "<th>#</th>" +
                    " </tr>" + PMD.EOL);

            this.length = this.packageBuf.length();
        }


        super.visit(node);


        if (node instanceof ViolationNode) {
            ViolationNode vnode = (ViolationNode) node;
            vnode.getParent().addNumberOfViolation(1);
            IRuleViolation vio = vnode.getRuleViolation();
            classBuf.append("<tr>" +
                    " <td>" + vio.getMethodName() + "</td>" +
                    " <td>" + this.displayRuleViolation(vio) + "</td>" +
                    "</tr>");
        }
        if (node instanceof ClassNode) {
            ClassNode cnode = (ClassNode) node;
            String str = cnode.getClassName();

            classBuf.insert(0,
                    "<html><head><title>PMD - " + str + "</title></head><body>" + PMD.EOL + 
                    "<h2>Class View</h2>" +
                    "<h3 align=\"center\">Class: " + str + "</h3>" +
                    "<table border=\"\" align=\"center\" cellspacing=\"0\" cellpadding=\"3\">" +
                    " <tr>" + PMD.EOL + 
                    "<th>Method</th>" +
                    "<th>Violation</th>" +
                    " </tr>" + PMD.EOL);

            classBuf.append("</table>" +
                    " </body>" +
                    "</html>");


            try {
                this.write(str + ".html", classBuf);
            } catch (Exception e) {
                throw new RuntimeException("Error while writing HTML report: " + e.getMessage());
            }
            classBuf = new StringBuffer();


            this.packageBuf.insert(this.length,
                    "<tr>" +
                    " <td>-</td>" +
                    " <td><a href=\"" + str + ".html\">" + str + "</a></td>" +
                    " <td>" + node.getNumberOfViolations() + "</td>" +
                    "</tr>" + PMD.EOL);
            node.getParent().addNumberOfViolation(node.getNumberOfViolations());
        }
        if (node instanceof PackageNode) {
            PackageNode pnode = (PackageNode) node;
            String str;

            
            if (node.getParent() == null) {
                str = "Aggregate";
            } else {           
                str = pnode.getPackageName();
                node.getParent().addNumberOfViolation(node.getNumberOfViolations());
            }

            this.packageBuf.insert(this.length,
                    "<tr><td><b>" + str + "</b></td>" +
                    " <td>-</td>" +
                    " <td>" + node.getNumberOfViolations() + "</td>" +
                    "</tr>" + PMD.EOL);
        }
        
        if (node.getParent() == null) {
            this.packageBuf.append("</table> </body></html>");
            try {
                this.write("index.html", this.packageBuf);
            } catch (Exception e) {
                throw new RuntimeException("Error while writing HTML report: " + e.getMessage());
            }
        }
    }
}
