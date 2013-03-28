package net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent;

import javax.swing.DefaultCellEditor;
import javax.swing.JTextField;


public class CellEditorUsingRenderer extends DefaultCellEditor {

	
	
	private IDataTypeComponent _dataTypeObject;
	
    
    public CellEditorUsingRenderer(final JTextField textField,
    	IDataTypeComponent dataTypeObject) {
    	
    	super(textField);
    	
    	
    	
    	_dataTypeObject = dataTypeObject;
    	
        editorComponent = textField;
		this.clickCountToStart = 2;
        delegate = new EditorDelegate() {
            public void setValue(Object value) {

				
				
				
				if (CellEditorUsingRenderer.this._dataTypeObject != null)
					textField.setText(_dataTypeObject.renderObject(value));
				else textField.setText((value != null) ? value.toString() : "<null>");
            }

	    	public Object getCellEditorValue() {
			return textField.getText();
			}
        };
		textField.addActionListener(delegate);
    }

}
