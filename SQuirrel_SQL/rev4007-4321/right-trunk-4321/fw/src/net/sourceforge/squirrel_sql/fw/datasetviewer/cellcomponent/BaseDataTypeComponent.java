
package net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import net.sourceforge.squirrel_sql.fw.datasetviewer.CellDataPopup;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ColumnDisplayDefinition;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;


public class BaseDataTypeComponent {

    
    private static ILogger s_log = 
        LoggerController.createLogger(BaseDataTypeComponent.class);    
    
    
    protected ColumnDisplayDefinition _colDef;

    
    protected JTable _table;
    
    
    protected DefaultColumnRenderer _renderer = 
        DefaultColumnRenderer.getInstance();
    
    
    protected RestorableJTextField _textField;

    
    protected RestorableJTextArea _textArea;
    
     
    public static final String NULL_VALUE_PATTERN = "<null>";
    
    
    public void setColumnDisplayDefinition(ColumnDisplayDefinition def) {
        this._colDef = def;
    }
    
    
    public void setTable(JTable table) {
        _table = table;
    }
    
    
    public JTextArea getJTextArea(Object value) {
        _textArea = new RestorableJTextArea();
        _textArea.setText((String) _renderer.renderObject(value));

        
        KeyListener keyListener = getKeyListener(_textArea);
        if (keyListener != null) {
            _textArea.addKeyListener(keyListener);
        }

        return _textArea;
    }
    
    
    public JTextField getJTextField() {
        _textField = new RestorableJTextField();

        KeyListener keyListener = getKeyListener(_textField);
        if (keyListener != null) {
            
            _textField.addKeyListener(keyListener);
        }
        
        
        
        
        
        
        _textField.addMouseListener(new MouseAdapter()
        {
            public void mousePressed(MouseEvent evt)
            {
                if (evt.getClickCount() == 2)
                {
                    MouseEvent tableEvt = SwingUtilities.convertMouseEvent(
                        _textField,
                        evt, _table);
                    CellDataPopup.showDialog(_table, _colDef, tableEvt, true);
                }
            }
        }); 
        
        
        return _textField;
    }    
    
    
    public String renderObject(final Object value) {
        String text = (String)_renderer.renderObject(value);
        return text;
    }    
    
    
    public Object validateAndConvert(final String value, 
                                     final Object originalValue, 
                                     final StringBuffer messageBuffer) {
        
        if (value.equals(NULL_VALUE_PATTERN)) {
            return null;
        }

        
        return value;   
    }    
    
    
    public Object validateAndConvertInPopup(String value, 
                                            Object originalValue, 
                                            StringBuffer messageBuffer) 
    {
        return validateAndConvert(value, originalValue, messageBuffer);
    }    
    
   
	protected KeyListener getKeyListener(IRestorableTextComponent component)
	{
		return null;
	}

    
    public void exportObject(FileOutputStream outStream, String text)
    throws IOException {
        OutputStreamWriter outWriter = null;
        try {
            outWriter = new OutputStreamWriter(outStream);
            outWriter.write(text);
            outWriter.flush();
            outWriter.close();
        } finally {
            if (outWriter != null) {
                try {
                    outWriter.close();
                } catch (IOException e) {
                    s_log.error("exportObject: Unexpected exception: "+e, e);
                }
            }
        }
    }

    
   public String importObject(FileInputStream inStream)
        throws IOException {

        InputStreamReader inReader = new InputStreamReader(inStream);

        int fileSize = inStream.available();

        char charBuf[] = new char[fileSize];

        int count = inReader.read(charBuf, 0, fileSize);

        if (count != fileSize)
            throw new IOException(
                "Could read only "+ count +
                " chars from a total file size of " + fileSize +
                ". Import failed.");

        
        
        
        
        String fileText;
        if (charBuf[count-1] == KeyEvent.VK_ENTER)
            fileText = new String(charBuf, 0, count-1);
        else fileText = new String(charBuf);

        
        
        StringBuffer messageBuffer = new StringBuffer();
        validateAndConvertInPopup(fileText, null, messageBuffer);
        if (messageBuffer.length() > 0) {
            
            throw new IOException(
                "Text does not represent data of type "+getClassName()+
                ".  Text was:\n"+fileText);
        }

        
        
        return fileText;
   }

   
   public String getClassName() {
       return "java.lang.String";
   }
      
}
