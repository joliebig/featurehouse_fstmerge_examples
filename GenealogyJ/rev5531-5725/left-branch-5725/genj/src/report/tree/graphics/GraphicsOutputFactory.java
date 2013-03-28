

package tree.graphics;

import genj.report.Report;
import genj.util.swing.Action2;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;



public class GraphicsOutputFactory {

    
    public int output_type = 0;

    public String[] output_types = null;

    private Map<String, GraphicsOutput> outputs = new LinkedHashMap<String, GraphicsOutput>();
    public List<GraphicsOutput> outputList = new ArrayList<GraphicsOutput>();

    
    public GraphicsOutputFactory()
    {
        add("svg", new SvgWriter());
        add("pdf", new PdfWriter());
        add("png", new PngWriter());
        add("screen", new ScreenOutput());
    }

    
    public GraphicsOutput createOutput(Report report)
    {
        GraphicsOutput output = outputList.get(output_type);

        if (output == null)
            return null;

        if (output instanceof GraphicsFileOutput)
        {
            GraphicsFileOutput fileOutput = (GraphicsFileOutput)output;
            String extension = fileOutput.getFileExtension();

            
            File file = report.getFileFromUser(report.translate("output.file"),
                    Action2.TXT_OK, true, extension);
            if (file == null)
                return null;

            
            String suffix = "." + extension;
            if (!file.getPath().endsWith(suffix))
                file = new File(file.getPath() + suffix);
            fileOutput.setFile(file);
        }

        return output;
    }

    public void add(String name, GraphicsOutput output)
    {
        outputs.put(name, output);
        outputList.add(output);
        output_types = outputs.keySet().toArray(new String[0]);
    }
}
