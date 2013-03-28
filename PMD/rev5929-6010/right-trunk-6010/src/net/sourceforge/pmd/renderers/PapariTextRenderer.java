
package net.sourceforge.pmd.renderers;

import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Report;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Iterator;
import java.util.Map;


public class PapariTextRenderer extends AbstractRenderer {
    
    private String pwd;

    private String yellowBold = "";
    private String whiteBold = "";
    private String redBold = "";
    private String cyan = "";
    private String green = "";

    private String colorReset = "";

    
    private void initializeColorsIfSupported() {
        if (System.getProperty("pmd.color") != null &&
                !(System.getProperty("pmd.color").equals("0") || System.getProperty("pmd.color").equals("false"))) {
            this.yellowBold = "\u[1;33m";
            this.whiteBold = "\u[1;37m";
            this.redBold = "\u[1;31m";
            this.green = "\u[0;32m";
            this.cyan = "\u[0;36m";

            this.colorReset = "\u[0m";
        }
    }

    public void render(Writer writer, Report report) throws IOException {
        StringBuffer buf = new StringBuffer(PMD.EOL);
        initializeColorsIfSupported();
        String lastFile = null;
        int numberOfErrors = 0;
        int numberOfWarnings = 0;

        for (Iterator<RuleViolation> i = report.iterator(); i.hasNext();) {
            buf.setLength(0);
            numberOfWarnings++;
            RuleViolation rv = i.next();
            if (!rv.getFilename().equals(lastFile)) {
                lastFile = rv.getFilename();
                buf.append(this.yellowBold + "*" + this.colorReset + " file: " + this.whiteBold + this.getRelativePath(lastFile) + this.colorReset + PMD.EOL);
            }
            buf.append(this.green + "    src:  " + this.cyan + lastFile.substring(lastFile.lastIndexOf(File.separator) + 1) + this.colorReset + ":" + this.cyan + rv.getBeginLine() + (rv.getEndLine() == -1 ? "" : ":" + rv.getEndLine()) + this.colorReset + PMD.EOL);
            buf.append(this.green + "    rule: " + this.colorReset + rv.getRule().getName() + PMD.EOL);
            buf.append(this.green + "    msg:  " + this.colorReset + rv.getDescription() + PMD.EOL);
            buf.append(this.green + "    code: " + this.colorReset + this.getLine(lastFile, rv.getBeginLine()) + PMD.EOL + PMD.EOL);
            writer.write(buf.toString());
        }
        writer.write(PMD.EOL + PMD.EOL);
        writer.write("Summary:" + PMD.EOL + PMD.EOL);
        Map<String, Integer> summary = report.getCountSummary();
        for (Map.Entry<String, Integer> entry : summary.entrySet()) {
            buf.setLength(0);
            String key = entry.getKey();
            buf.append(key).append(" : ").append(entry.getValue()).append(PMD.EOL);
            writer.write(buf.toString());
        }

        for (Iterator<Report.ProcessingError> i = report.errors(); i.hasNext();) {
            buf.setLength(0);
            numberOfErrors++;
            Report.ProcessingError error = i.next();
            if (error.getFile().equals(lastFile)) {
                lastFile = error.getFile();
                buf.append(this.redBold + "*" + this.colorReset + " file: " + this.whiteBold + this.getRelativePath(lastFile) + this.colorReset + PMD.EOL);
            }
            buf.append(this.green + "    err:  " + this.cyan + error.getMsg() + this.colorReset + PMD.EOL + PMD.EOL);
            writer.write(buf.toString());
        }

        
        if (numberOfErrors > 0) {
            writer.write(this.redBold + "*" + this.colorReset + " errors:   " + this.whiteBold + numberOfWarnings + this.colorReset + PMD.EOL);
        }
        writer.write(this.yellowBold + "*" + this.colorReset + " warnings: " + this.whiteBold + numberOfWarnings + this.colorReset + PMD.EOL);
    }

    
    private String getLine(String sourceFile, int line) {
        String code = null;
        BufferedReader br = null;
        try {
            br = new BufferedReader(getReader(sourceFile));
            for (int i = 0; line > i; i++) {
                code = br.readLine().trim();
            }
        } catch (IOException ioErr) {
            ioErr.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException ioErr) {
                    ioErr.printStackTrace();
                }
            }
        }
        return code;
    }

    protected Reader getReader(String sourceFile) throws FileNotFoundException {
        return new FileReader(new File(sourceFile));
    }

    
    private String getRelativePath(String fileName) {
        String relativePath;

        
        if (pwd == null) {
            try {
                this.pwd = new File(".").getCanonicalPath();
            } catch (IOException ioErr) {
                
                this.pwd = "";
            }
        }

        
        if (fileName.indexOf(this.pwd) == 0) {
            relativePath = "." + fileName.substring(this.pwd.length());

            
            if (relativePath.startsWith("." + File.separator + "." + File.separator)) {
                relativePath = relativePath.substring(2);
            }
        } else {
            
            
            
            relativePath = fileName;
        }

        return relativePath;
    }
}
