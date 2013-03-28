
package org.openscience.jmol.app.webexport;

import javax.swing.*;

import org.jmol.api.JmolViewer;
import org.jmol.i18n.GT;
import org.jmol.util.TextFormat;

class ScriptButtons extends WebPanel {

  ScriptButtons(JmolViewer viewer, JFileChooser fc, WebPanel[] webPanels,
      int panelIndex) {
    super(viewer, fc, webPanels, panelIndex);
    panelName = "script_button";
    listLabel = GT._("These names will be used for button labels");
    
  }

  JPanel appletParamPanel() {
    SpinnerNumberModel appletSizeModel = new SpinnerNumberModel(60, 
        20, 
        100, 
        5); 
    appletSizeSpinnerP = new JSpinner(appletSizeModel);
    
    JPanel appletSizePPanel = new JPanel();
    appletSizePPanel.add(new JLabel(GT._("% of window for applet width:")));
    appletSizePPanel.add(appletSizeSpinnerP);
    return (appletSizePPanel);
  }

  String fixHtml(String html) {
    int size = ((SpinnerNumberModel) (appletSizeSpinnerP.getModel()))
        .getNumber().intValue();
    int leftpercent = 100 - size;
    html = TextFormat.simpleReplace(html, "@WIDTHPERCENT@", "" + size);
    html = TextFormat.simpleReplace(html, "@LEFTPERCENT@", "" + leftpercent);
    return html;
  }

  String getAppletDefs(int i, String html, StringBuffer appletDefs,
                       JmolInstance instance) {
    String name = instance.name;
    String buttonname = instance.javaname;
    if (i == 0)
      html = TextFormat.simpleReplace(html, "@APPLETNAME0@", GT.escapeHTML(buttonname));
    if (useAppletJS) {
      String info = "info for " + name;
      appletDefs.append("\naddAppletButton(" + i + ",'" + buttonname + "',\""
          + name + "\",\"" + info + "\");");
    } else {
      String s = htmlAppletTemplate;
      s = TextFormat.simpleReplace(s, "@APPLETNAME0@", GT.escapeHTML(buttonname));
      s = TextFormat.simpleReplace(s, "@NAME@", GT.escapeHTML(name));
      s = TextFormat.simpleReplace(s, "@LABEL@", GT.escapeHTML(name));
      appletDefs.append(s);
    }
    return html;
  }
}
