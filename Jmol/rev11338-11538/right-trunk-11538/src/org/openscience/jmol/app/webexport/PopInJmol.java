
package org.openscience.jmol.app.webexport;

import javax.swing.*;

import org.jmol.api.JmolViewer;
import org.jmol.i18n.GT;
import org.jmol.util.TextFormat;

class PopInJmol extends WebPanel {

  PopInJmol(JmolViewer viewer, JFileChooser fc, WebPanel[] webPanels,
      int panelIndex) {
    super(viewer, fc, webPanels, panelIndex);
    panelName = "pop_in";
    listLabel = GT._("These names will be used as filenames for the applets");
    
  }

  JPanel appletParamPanel() {
    
    
    SpinnerNumberModel appletSizeModelW = new SpinnerNumberModel(300, 
        50, 
        1000, 
        25); 
    SpinnerNumberModel appletSizeModelH = new SpinnerNumberModel(300, 
        50, 
        1000, 
        25); 
    appletSizeSpinnerW = new JSpinner(appletSizeModelW);
    appletSizeSpinnerH = new JSpinner(appletSizeModelH);

    
    JPanel appletSizeWHPanel = new JPanel();
    appletSizeWHPanel.add(new JLabel(GT._("Applet width:")));
    appletSizeWHPanel.add(appletSizeSpinnerW);
    appletSizeWHPanel.add(new JLabel(GT._("height:")));
    appletSizeWHPanel.add(appletSizeSpinnerH);
    return (appletSizeWHPanel);
  }

  String fixHtml(String html) {
    return html;
  }

  String getAppletDefs(int i, String html, StringBuffer appletDefs,
                       JmolInstance instance) {
    String divClass = (i % 2 == 0 ? "floatRightDiv" : "floatLeftDiv");
    String name = instance.name;
    String javaname = instance.javaname;
    int JmolSizeW = instance.width;
    int JmolSizeH = instance.height;
    if (useAppletJS) {
      appletInfoDivs += "\n<div id=\"" + javaname + "_caption\">\n" 
          + GT.escapeHTML(GT._("insert a caption for {0} here.", name))
          + "\n</div>";
      appletInfoDivs += "\n<div id=\"" + javaname + "_note\">\n"
          + GT.escapeHTML(GT._("insert a note for {0} here.", name))
          + "\n</div>";
      appletDefs.append("\naddJmolDiv(" + i + ",'" + divClass + "','" + javaname
          + "'," + JmolSizeW + "," + JmolSizeH + ")");
    } else {
      String s = htmlAppletTemplate;
      s = TextFormat.simpleReplace(s, "@CLASS@", "" + divClass);
      s = TextFormat.simpleReplace(s, "@I@", "" + i);
      s = TextFormat.simpleReplace(s, "@WIDTH@", "" + JmolSizeW);
      s = TextFormat.simpleReplace(s, "@HEIGHT@", "" + JmolSizeH);
      s = TextFormat.simpleReplace(s, "@NAME@", GT.escapeHTML(name));
      s = TextFormat.simpleReplace(s, "@APPLETNAME@", GT.escapeHTML(javaname));
      appletDefs.append(s);
    }
    return html;
  }
}
