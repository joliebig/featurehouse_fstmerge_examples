
package genj.help;

import genj.util.EnvironmentChecker;
import genj.util.Resources;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.File;
import java.net.URL;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.help.HelpSet;
import javax.help.JHelp;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;


class HelpWidget extends JPanel {

  private final static Logger LOG = Logger.getLogger("genj.help");
  private final static Resources RESOURCES = Resources.get(HelpWidget.class);

  
  public HelpWidget() {
    
    
    super(new BorderLayout());
    
    
    JComponent pCenter = getContent();
    if (pCenter==null) {
      pCenter = new JLabel(RESOURCES.getString("help.missing", Locale.getDefault().getLanguage().toLowerCase()), SwingConstants.CENTER);
      pCenter.setBorder(new EmptyBorder(16,16,16,16));
    }
    
    
    add(pCenter, BorderLayout.CENTER);    
    
    
  }
  
  
  public Dimension getPreferredSize() {
    return new Dimension(480,480);
  }
  
  
  private JComponent getContent() {
    
    
    String file = calcHelpBase() + "/helpset.xml";
    LOG.info("Trying to use help in " + file );
    
    
    if (!new File(file).exists()) {
      LOG.log(Level.WARNING, "No help found in "+file);
      return null;
    }

    
    try {
      
      
      HelpSet set = (HelpSet)HelpSet.class.getConstructor(new Class[]{ClassLoader.class, URL.class})
        .newInstance(new Object[]{null,new URL("file","", file)});
      return (JComponent)JHelp.class.getConstructor(new Class[]{set.getClass()}).newInstance(new Object[]{set});
    } catch (Throwable t) {
      LOG.log(Level.WARNING, "Problem reading help", t);
    }
    
    
    return null;
  }

  
  private String calcHelpBase() {
    
    
    String dir = EnvironmentChecker.getProperty(
      new String[]{ "genj.help.dir", "user.dir/help"},
      ".",
      "read help"
    );
    
    
    String local = dir+"/"+Locale.getDefault().getLanguage();
    if (new File(local).exists()) {
      return local;
    }
    
    
    return dir+"/en";
    
  }

} 
