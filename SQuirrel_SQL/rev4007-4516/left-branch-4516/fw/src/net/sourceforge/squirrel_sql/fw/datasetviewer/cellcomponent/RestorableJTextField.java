package net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent;

import javax.swing.JTextField;


public class RestorableJTextField extends JTextField 
	implements IRestorableTextComponent
{

	
	 private String _originalValue = null;
	 

	
	public void setText(String originalValue) {
		if (originalValue == null)
			_originalValue = "<null>";
		else _originalValue = originalValue;
		super.setText(_originalValue);
	}

	
	public void updateText(String newText) {
		super.setText(newText);
	}
	
	
	 public void restoreText() {
	 	super.setText(_originalValue);
	 }
}
