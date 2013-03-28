
package genj.report;

import javax.swing.text.Element;
import javax.swing.text.StyleConstants;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;
import javax.swing.text.html.HTML;


public class HTMLEditorKit extends javax.swing.text.html.HTMLEditorKit {

    
    private ViewFactory factory = new HTMLFactory();

    
    private Class from;

    
    public HTMLEditorKit(Class from) {
        this.from = from;
    }

    
    public void setFrom(Class from) {
        this.from = from;
    }

    
    public ViewFactory getViewFactory() {
        return factory;
    }

    
    private class HTMLFactory extends
            javax.swing.text.html.HTMLEditorKit.HTMLFactory {

        
        public View create(Element elem) {
            Object o = elem.getAttributes().getAttribute(
                    StyleConstants.NameAttribute);
            if (o instanceof HTML.Tag) {
                HTML.Tag kind = (HTML.Tag) o;
                if (kind == HTML.Tag.IMG)
                    return new ClassLoaderImageView(elem, from);
            }
            return super.create(elem);
        }
    }
}
