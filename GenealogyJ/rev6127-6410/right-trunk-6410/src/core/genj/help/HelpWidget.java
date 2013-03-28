
package genj.help;

import genj.io.CachingStreamHandler;
import genj.util.Resources;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.HyperlinkEvent.EventType;
import javax.swing.text.Element;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.ImageView;


class HelpWidget extends JPanel {

  private final static Logger LOG = Logger.getLogger("genj.help");
  private final static Resources RESOURCES = Resources.get(HelpWidget.class);
  private final static CachingStreamHandler CACHE = new CachingStreamHandler("help");
  
  final static String BASE = "http://genj.sourceforge.net/wiki/en/manual/";
  final static String WELCOME = "welcome";
  final static String MANUAL = "overview";
  final static String EXPORT = "?do=export_xhtmlbody";
  
  private JEditorPane content;
  private String page = null;

  
  public HelpWidget() {
    
    
    HTMLDocument doc = new HTMLDocument();
    doc.setAsynchronousLoadPriority(1);
    
    content = new JEditorPane();
    content.setBackground(Color.WHITE);
    content.setEditable(false);
    content.setEditorKit(new Kit());
    content.setDocument(doc);
    content.addHyperlinkListener(new Hyperlinker());
    
    
    setLayout(new BorderLayout());
    add(BorderLayout.CENTER, new JScrollPane(content));

    
  }
  
  String getPage() {
    return page;
  }
  
  void setPage(String page) {
    try {
      String old = this.page;
      this.page = page;
      content.setPage(new URL(null, BASE+page+EXPORT, CACHE));
      firePropertyChange("url", old, page);
    } catch (Throwable t) {
      LOG.log(Level.WARNING, "can't set help content", t);
    }
  }
  
  
  public Dimension getPreferredSize() {
    return new Dimension(480,480);
  }
  
  
  private static class Kit extends HTMLEditorKit {
    
    private static Factory factory = new Factory();
    
    @Override
    public ViewFactory getViewFactory() {
      return factory;
    }
  
    private static class Factory extends HTMLFactory {
      @Override
      public View create(Element elem) {
        Object o = elem.getAttributes().getAttribute(StyleConstants.NameAttribute);
        if (o instanceof HTML.Tag) {
          
          if (o==HTML.Tag.IMG) {
            MutableAttributeSet atts = (MutableAttributeSet)elem.getAttributes();
            atts.addAttribute(HTML.Attribute.BORDER, "0");
            atts.addAttribute(HTML.Attribute.ALIGN, "middle");
            ImageView img = new ImageView(elem);
            return img;
          }
        }
        
        return super.create(elem);
      }
    }
  }

  
  private class Hyperlinker implements HyperlinkListener {
    public void hyperlinkUpdate(HyperlinkEvent e) {
      
      
      if (e.getEventType()!=EventType.ACTIVATED)
        return;
      
      String s = e.getDescription();
      
      
      if (s.startsWith("genj:")) {
        LOG.info("Click on "+s);
        return;
      }
      
      
      URL url = e.getURL();
      s = url.toString();
      if (s.startsWith(BASE)) {
        
        if (s.indexOf('?')>0)
          return;
        setPage(s.substring(BASE.length()));
        return;
      }      
      
      
      if (url!=null) try {
        Desktop.getDesktop().browse(url.toURI());
      } catch (Throwable t) {
        LOG.info("can't open external url "+s);
      }
     
      
    }
  }
    
} 
