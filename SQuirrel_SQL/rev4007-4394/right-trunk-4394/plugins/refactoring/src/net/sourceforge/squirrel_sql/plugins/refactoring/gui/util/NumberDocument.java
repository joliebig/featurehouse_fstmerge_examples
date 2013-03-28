package net.sourceforge.squirrel_sql.plugins.refactoring.gui.util;


import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;
import java.awt.*;


public class NumberDocument extends PlainDocument implements Document {

    private static final long serialVersionUID = 8245201344439966381L;
    private int maxLength = -1;
    private String valid = "-0123456789";
    private boolean fractional = false;


    
    public NumberDocument() {
    }


    
    public NumberDocument(int length) {
        this.maxLength = length;
    }


    
    public NumberDocument(int length, String specialValid) {
        this.maxLength = length;
        valid += specialValid;
    }


    
    public NumberDocument(int length, boolean fractional) {
        this.maxLength = length;
        this.fractional = fractional;
        if (this.fractional) valid += ".";
    }


    
    public void insertString(int i, String string, AttributeSet attributeSet) throws BadLocationException {
        if (maxLength > -1 && getLength() == maxLength) return;
        for (char c : string.toCharArray()) {
            if (valid.indexOf(c) == -1 || (fractional && c == '.' && getText(0, getLength()).indexOf(c) != -1)) {
                Toolkit.getDefaultToolkit().beep();
                return;
            }
        }
        super.insertString(i, string, attributeSet);
    }
}


