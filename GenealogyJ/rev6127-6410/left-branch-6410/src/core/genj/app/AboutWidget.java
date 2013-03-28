
 
package genj.app;

import genj.Version;
import genj.util.EnvironmentChecker;
import genj.util.Resources;
import genj.util.swing.Action2;
import genj.util.swing.ImageIcon;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;


public class AboutWidget extends JTabbedPane {
  
  private final static int DEFAULT_ROWS = 16, DEFAULT_COLS = 40;
  
  
  private Resources resources = Resources.get(AboutWidget.class);
  
  
  public AboutWidget() {

    JLabel welcome = new JLabel(new ImageIcon(this, "/splash.png"));
    welcome.setBackground(Color.WHITE);
    welcome.setOpaque(true);
    
    addTab("GenealogyJ "+Version.getInstance().getVersionString(), null, welcome);
    addTab(resources.getString("about.authors"), null, new AuthorsPanel());
    addTab(resources.getString("about.copyright"), null, new CopyrightPanel());

  }
  
  
  protected void readTextFile(JTextArea ta, String file) {
    try {
      FileInputStream fin = new FileInputStream(file);
      Reader in = new InputStreamReader(fin);
      ta.read(in,null);
      fin.close();
    }
    catch (Throwable t) {
    }
  }

    
  private class AuthorsPanel extends JScrollPane {

      
    protected AuthorsPanel() {

      
      JTextArea text = new JTextArea(DEFAULT_ROWS,DEFAULT_COLS);
      text.setLineWrap(false);
      text.setWrapStyleWord(true);
      text.setEditable(false);

      String dir = EnvironmentChecker.getProperty("user.dir", ".", "get authors.txt");
      
      String path = dir + File.separatorChar + "doc" + File.separatorChar + "authors.txt";
      
      readTextFile(text, path);

      
      setViewportView(text);      
      
      setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
      setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

      
      
    }      
  } 

    
  private class CopyrightPanel extends JPanel {

      
    protected CopyrightPanel()  {
      
      super(new BorderLayout());
      
      add(getNorth(), BorderLayout.NORTH);
      add(getCenter(), BorderLayout.CENTER);
    
    }
    
    
    private JComponent getNorth() {
      
      JTextArea text = new JTextArea(resources.getString("app.disclaimer"),3,DEFAULT_COLS);
      text.setLineWrap(true);
      text.setWrapStyleWord(true);
      text.setEditable(false);
      
      JPanel panel = new JPanel(new BorderLayout());
      panel.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createTitledBorder(resources.getString("about.copyright")),
        new EmptyBorder(3, 3, 3, 3)
      ));
      panel.add(text, BorderLayout.CENTER);
      
      return panel;
    }

    
    private JComponent getCenter() {
          
      
      JTextArea text = new JTextArea(DEFAULT_ROWS,DEFAULT_COLS);
      String dir = EnvironmentChecker.getProperty("user.dir",".","read gpl.txt");
      
      String path = dir + File.separatorChar + "doc" + File.separatorChar + "gpl.txt";
      readTextFile(text, path);
      text.setLineWrap(false);
      text.setEditable(false);
      text.setBorder(new EmptyBorder(3, 3, 3, 3));
      
      
      JScrollPane scroll = new JScrollPane(text);
      scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
      scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
      scroll.setBorder(BorderFactory.createTitledBorder(resources.getString("about.license")));
      
      
      return scroll;
    }
  
  } 
  
  private class Log extends Action2 {
    Log() {
      setText("Log");
    }
    public void actionPerformed(ActionEvent event) {
      try {
        Desktop.getDesktop().open(App.LOGFILE);
      } catch (Throwable t) {
        Logger.getLogger("genj.io").log(Level.INFO, "can't open logfile", t);
      }
    }
  }
  
  
} 
