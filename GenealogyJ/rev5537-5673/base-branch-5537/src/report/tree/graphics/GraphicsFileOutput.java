

package tree.graphics;

import genj.report.Report;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;


public abstract class GraphicsFileOutput implements GraphicsOutput {

    
    private File file;

    
    public void setFile(File file) {
        this.file = file;
    }

    
    public void output(GraphicsRenderer renderer) throws IOException {
        OutputStream out = new FileOutputStream(file);
        write(out, renderer);
        out.close();
    }

    
    public void display(Report report) {
        report.showFileToUser(file);
    }

    
	public abstract void write(OutputStream out, GraphicsRenderer renderer) throws IOException;

    
    public abstract String getFileExtension();

}
