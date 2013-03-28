package net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent;

import javax.swing.JTextArea;


public class RestorableJTextArea extends JTextArea 
	implements IRestorableTextComponent
{

	
	 private String _originalValue = null;
	 

	
	public void setText(String originalValue) {
		if (originalValue == null)
			_originalValue = "<null>";
		else _originalValue = originalValue;
		super.setText(_originalValue);
		setCaretPosition(0);
	}

	
	public void updateText(String newText) {
		super.setText(newText);
		setCaretPosition(0);
	}
	
	
	 public void restoreText() {
	 	super.setText(_originalValue);
		setCaretPosition(0);
	 }
}
