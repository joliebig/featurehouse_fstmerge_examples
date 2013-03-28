package net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent;


import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class BaseKeyTextHandler extends KeyAdapter {
    
    
    boolean firstBlankText = true;
    
    
    
    protected void handleNotNullableField(String text, 
                                          char c,
                                          KeyEvent e,
                                          IRestorableTextComponent _textComponent)
    {
        if (text.length() == 0) {
            
            
            
            
            
            
            if (firstBlankText) {
                firstBlankText = false;
                return;                            
            } else {
                firstBlankText = true;
            }                        
            
            if ( c==KeyEvent.VK_BACK_SPACE 
                    || c == KeyEvent.VK_DELETE) 
            {
                
                _textComponent.restoreText();
                e.consume();
            }
        }
    }
}
