
package genj.util.swing;

import genj.util.EnvironmentChecker;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class FontChooser extends JPanel {
  
  
  private static String[] families = null;
  
  
  private JComboBox fonts;
  
  
  private JTextField size;
  
  
  private final static boolean isRenderWithFont = 
    null == EnvironmentChecker.getProperty(FontChooser.class, "genj.debug.fontproblems", null, "supress font usage in font-selection-list");
    

  
    public FontChooser() {
    
    
    fonts = new JComboBox(getAllFonts());
    fonts.setEditable(false);
    fonts.setRenderer(new Renderer());
    size = new JTextField(3);
    
    
    setAlignmentX(0F);
    
    setLayout(new BorderLayout());
    add(fonts, BorderLayout.CENTER);
    add(size , BorderLayout.EAST  );
    
    
  }
  
  
  public Dimension getMaximumSize() {
    Dimension result = super.getPreferredSize();
    result.width = Integer.MAX_VALUE;
    return result;
  }

    public void setSelectedFont(Font font) {
    String family = font.getFamily();
    Font[] fs = getAllFonts();
    for (int i = 0; i < fs.length; i++) {
      if (fs[i].getFamily().equals(family)) {
        fonts.setSelectedIndex(i);
        break;
      }
      
    }
    size.setText(""+font.getSize());
  }
  
    public Font getSelectedFont() {
    Font font = (Font)fonts.getSelectedItem();
    if (font==null)
      font = getFont();
    return font.deriveFont((float)getSelectedFontSize());
  }
  
  
  private int getSelectedFontSize() {
    int result = 2;
    try {
      result = Integer.parseInt(size.getText());
    } catch (Throwable t) {
    }
    return Math.max(2,result);
  }
  
  
  private static Font[] getAllFonts() {

    
    if (families==null)
      families = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
    
    
    Font[] values = new Font[families.length];
    for (int i = 0; i < values.length; i++) {
      values[i] = new Font(families[i],0,12); 
    }
    
    
    return values;
  }
  
  
  public static void main(String[] args) {
    
    System.out.println("Running font test");
    
    Font[] fonts = getAllFonts();

    System.out.println("Found "+fonts.length+" fonts");
    
    String txt = "GenealogyJ";
    FontRenderContext ctx = new FontRenderContext(null, false, false);
    
    for (int f = 0; f < fonts.length; f++) {
      Font font = fonts[f];
      
      System.out.println("Testing font "+font+"...");
      
      LineMetrics lm = font.getLineMetrics(txt, ctx);
      lm.getAscent();
      lm.getBaselineIndex();
      lm.getDescent();
      lm.getHeight();
      lm.getLeading();
      lm.getStrikethroughOffset();
      lm.getUnderlineOffset();
      lm.getUnderlineThickness();
      
      System.out.println("OK");
      
    }
    
    
  }
  
  private static class Renderer extends DefaultListCellRenderer {
    
    
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
      if (value instanceof Font) {
        Font font = (Font)value;
        super.getListCellRendererComponent(list, font.getFamily(), index, isSelected, cellHasFocus);
        if (isRenderWithFont)
          setFont(font);
      } else {
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
      }
      return this;
    }
    
  } 
  
} 
