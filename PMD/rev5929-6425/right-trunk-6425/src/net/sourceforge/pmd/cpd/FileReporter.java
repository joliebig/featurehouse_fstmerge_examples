
package net.sourceforge.pmd.cpd;

import java.io.*;


public class FileReporter {
    private File reportFile;
    private String encoding;

    public FileReporter(String encoding) {
        this(null, encoding);
    }

    public FileReporter(File reportFile) {
        this(reportFile, System.getProperty("file.encoding"));
    }

    public FileReporter(File reportFile, String encoding) {
        this.reportFile = reportFile;
        this.encoding = encoding;
    }

    public void report(String content) throws ReportException {
        try {
            Writer writer = null;
            try {
            	OutputStream outputStream;
            	if (reportFile == null) {
            		outputStream = System.out;
            	} else {
            		outputStream = new FileOutputStream(reportFile);
            	}
                writer = new BufferedWriter(new OutputStreamWriter(outputStream, encoding));
                writer.write(content);
            } finally {
                if (writer != null) {
                    writer.close();
                }
            }
        } catch (IOException ioe) {
            throw new ReportException(ioe);
        }
    }
}
