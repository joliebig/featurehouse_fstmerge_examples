
package genj.search;

import genj.gedcom.Property;

import java.awt.Color;

import javax.swing.ImageIcon;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;


 class Hit {
  
  private final static SimpleAttributeSet 
    RED  = new SimpleAttributeSet(),
    BOLD = new SimpleAttributeSet();
  
  static {
    StyleConstants.setForeground(RED, Color.RED);
    StyleConstants.setBold(BOLD, true);
  }

  
  private Property property;
  
  
  private ImageIcon img; 
  
  
  private StyledDocument doc;
  
  
  private int entity;

  
   Hit(Property setProp, String value, Matcher.Match[] matches, int setEntity, boolean isID) {
    
    property = setProp;
    
    img = property.getImage(false);
    
    entity = setEntity;
    
    doc = new DefaultStyledDocument();
    try {
      int offset = 0;
      String tag = setProp.getPropertyName();
      
      doc.insertString(offset++, " ", null);
      
      if (!isID) {
	      doc.insertString(offset, tag, BOLD);
	      offset += tag.length();
	      doc.insertString(offset++, " ", null);
      }
      
      doc.insertString(offset, value, null);
      for (int i=0;i<matches.length;i++) {
        Matcher.Match m = matches[i];
        doc.setCharacterAttributes(offset+m.pos, m.len, RED, false);
      }
      offset += value.length();
      
      if (isID) {
	      doc.insertString(offset++, " ", null);
        doc.insertString(offset, tag, BOLD);
      }
      
      SimpleAttributeSet img = new SimpleAttributeSet();
      StyleConstants.setIcon(img, setProp.getImage(false));
      doc.insertString(0, " ", img);
    } catch (Throwable t) {
    }
    
  }
  
  
   StyledDocument getDocument() {
    return doc;
  }
  
  
   Property getProperty() {
    return property;
  }
  
  
   ImageIcon getImage() {
    return img;
  }
  
  
   int getEntity() {
    return entity;
  }
  
} 
