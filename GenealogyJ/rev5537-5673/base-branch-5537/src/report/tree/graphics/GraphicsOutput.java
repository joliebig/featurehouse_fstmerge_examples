

package tree.graphics;

import genj.report.Report;

import java.io.IOException;



public interface GraphicsOutput {

    
	public void output(GraphicsRenderer renderer) throws IOException;

    
    public void display(Report report);
}
