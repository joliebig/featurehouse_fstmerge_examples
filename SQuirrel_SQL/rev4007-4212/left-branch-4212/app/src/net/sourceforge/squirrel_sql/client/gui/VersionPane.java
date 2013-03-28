package net.sourceforge.squirrel_sql.client.gui;


import javax.swing.JTextPane;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import net.sourceforge.squirrel_sql.client.Version;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;


public class VersionPane extends JTextPane {

    private boolean _showWebsite = false;

    
    private final static ILogger s_log =
        LoggerController.createLogger(VersionPane.class);
    
    
    public VersionPane(boolean showWebsite) {
        _showWebsite = showWebsite;
        init();
    }
    
    
    private void init() {
        String content = getContent();
        StyledDocument doc = getStyledDocument();
        SimpleAttributeSet s = new SimpleAttributeSet();
        StyleConstants.setAlignment(s, StyleConstants.ALIGN_CENTER);
        StyleConstants.setBold(s, true);
        
        try {          
            doc.setParagraphAttributes(0,content.length(), s, false);
            doc.insertString(0, content, s);
        } catch (Exception e) {
            s_log.error("init: Unexpected exception "+e.getMessage());
        }
        setOpaque(false);        
    }
    
    
    private String getContent() {
        StringBuffer text = new StringBuffer();
        text.append(Version.getVersion());
        text.append("\n");
        text.append(Version.getCopyrightStatement());
        if (_showWebsite) {
            text.append("\n");
            text.append(Version.getWebSite());            
        }
        return text.toString();
    }
}
