
package net.sourceforge.squirrel_sql.plugins.firebirdmanager;

import java.io.OutputStream;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;


public class TextAreaOutputStream extends OutputStream {
	private JTextArea textArea;
	private JScrollPane scrollPane;

	
	public TextAreaOutputStream(JTextArea textArea, JScrollPane scrollPane) {
		setTextArea(textArea);
		setScrollPane(scrollPane);
	}
	
	@Override
	public void write(int b) {
		this.textArea.append((char)b + "");
    	refreshDisplay();
	}
	
	@Override
    public void write(byte[] b) {
        this.textArea.append(new String(b));
    	refreshDisplay();
    }
    
	@Override
    public void write(byte[] b, int off, int len) {
    	this.textArea.append(new String(b, off, len));
    	refreshDisplay();
    }	
	
	
	private void refreshDisplay() {
        this.textArea.setCaretPosition(this.textArea.getDocument().getLength());
        this.textArea.scrollRectToVisible(this.textArea.getVisibleRect());
    	this.scrollPane.getVerticalScrollBar().setValue(this.scrollPane.getVerticalScrollBar().getMaximum());
    	this.textArea.paintImmediately(0,0,this.textArea.getWidth(), this.textArea.getHeight());
	}

	
	
	public JTextArea getTextArea() {
		return textArea;
	}

	
	public void setTextArea(JTextArea textArea) {
		this.textArea = textArea;
	}

	
	public JScrollPane getScrollPane() {
		return scrollPane;
	}

	
	public void setScrollPane(JScrollPane scrollPane) {
		this.scrollPane = scrollPane;
	}
}
