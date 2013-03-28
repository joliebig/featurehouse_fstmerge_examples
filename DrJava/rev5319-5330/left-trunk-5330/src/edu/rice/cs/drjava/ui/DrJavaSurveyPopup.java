

package edu.rice.cs.drjava.ui;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.event.*;
import java.awt.*;
import java.io.*;
import java.net.*;
import java.util.Date;

import edu.rice.cs.drjava.DrJava;
import edu.rice.cs.drjava.Version;
import edu.rice.cs.drjava.config.OptionConstants;
import edu.rice.cs.drjava.platform.*;
import edu.rice.cs.util.swing.UneditableTableModel;
import edu.rice.cs.util.swing.BorderlessScrollPane;
import edu.rice.cs.plt.lambda.Runnable1;
import edu.rice.cs.plt.lambda.LambdaUtil;


public class DrJavaSurveyPopup extends JDialog {
  
  public static final String[] DRJAVA_SURVEY_KEYS = new String[] {"os.name","os.version","java.version","java.vendor"};
  
  
  private JButton _noButton;
  
  private JButton _yesButton;
  
  private MainFrame _mainFrame;
  
  private JOptionPane _questionPanel;
  
  private JTable _propertiesTable;
  
  private JCheckBox _neverAskAgain;
  
  
  public DrJavaSurveyPopup(MainFrame parent) {
    super(parent, "Send System Information to DrJava Developers");
    setResizable(false);
    setSize(550,350);
    _mainFrame = parent;

    _yesButton = new JButton(_yesAction);
    _noButton = new JButton(_noAction);
    _neverAskAgain = new JCheckBox("Never ask me again",
                                   !DrJava.getConfig().getSetting(OptionConstants.DIALOG_DRJAVA_SURVEY_ENABLED).booleanValue());
    _neverAskAgain.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        DrJava.getConfig().setSetting(OptionConstants.DIALOG_DRJAVA_SURVEY_ENABLED, !_neverAskAgain.isSelected());
      }
    });

    JPanel buttonPanel = new JPanel();
    buttonPanel.add(_neverAskAgain);
    buttonPanel.add(_yesButton);
    buttonPanel.add(_noButton);

    _questionPanel = new JOptionPane("May DrJava anonymously send the information\nbelow to the DrJava developers?",
                                     JOptionPane.QUESTION_MESSAGE,JOptionPane.DEFAULT_OPTION,null,
                                     new Object[0]);
    int size = DRJAVA_SURVEY_KEYS.length + 2;
    String[][] rowData = new String[size][2];
    int rowNum = 0;
    for(String k: DRJAVA_SURVEY_KEYS) {
      rowData[rowNum][0] = k;
      rowData[rowNum][1] = System.getProperty(k);
      ++rowNum;
    }
    rowData[rowNum  ][0] = "DrJava revision";
    rowData[rowNum++][1] = String.valueOf(Version.getRevisionNumber());
    rowData[rowNum  ][0] = "DrJava build time";
    rowData[rowNum++][1] = String.valueOf(Version.getBuildTimeString());
    java.util.Arrays.sort(rowData,new java.util.Comparator<String[]>() {
      public int compare(String[] o1, String[] o2) {
        return o1[0].compareTo(o2[0]);
      }
    });
    String[] nvStrings = new String[] {"Name","Value"};
    UneditableTableModel model = new UneditableTableModel(rowData, nvStrings);
    _propertiesTable = new JTable(model);
    JScrollPane scroller = new BorderlessScrollPane(_propertiesTable);

    JPanel centerPanel = new JPanel(new BorderLayout());
    centerPanel.add(_questionPanel, BorderLayout.NORTH);
    centerPanel.add(scroller, BorderLayout.CENTER);
    
    getContentPane().setLayout(new BorderLayout());
    getContentPane().add(centerPanel, BorderLayout.CENTER);
    getContentPane().add(buttonPanel, BorderLayout.SOUTH);
    _mainFrame.setPopupLoc(this);
  }
  
  
  private Action _noAction = new AbstractAction("No") {
    public void actionPerformed(ActionEvent e) { noAction(); }
  };

  
  private Action _yesAction = new AbstractAction("Yes") {
    public void actionPerformed(ActionEvent e) { yesAction(); }
  };

  protected void noAction() {
    
    
    
    DrJava.getConfig().setSetting(OptionConstants.LAST_DRJAVA_SURVEY, new Date().getTime());
    DrJava.getConfig().setSetting(OptionConstants.LAST_DRJAVA_SURVEY_RESULT, getSurveyURL());
    setVisible(false);
    dispose();
  }

  public static final edu.rice.cs.util.Log LOG = new edu.rice.cs.util.Log("survey.txt",false);

  
  public static String getSurveyURL() {
    final String DRJAVA_SURVEY_PAGE = "http://www.drjava.org/submit-usage.php?";
    StringBuilder sb = new StringBuilder();
    sb.append(DRJAVA_SURVEY_PAGE);
    sb.append("rev=");
    sb.append(Version.getRevisionNumber());
    for(String k: DRJAVA_SURVEY_KEYS) {
      sb.append('&');
      sb.append(k);
      sb.append('=');
      sb.append(System.getProperty(k));
    }
    LOG.log(sb.toString());
    return sb.toString().replaceAll(" ","%20");
  }
  
  
  public static boolean maySubmitSurvey() {
    
    int days = DrJava.getConfig().getSetting(OptionConstants.DRJAVA_SURVEY_DAYS);
    Date nextCheck = 
      new Date(DrJava.getConfig().getSetting(OptionConstants.LAST_DRJAVA_SURVEY) +
               days * 24L * 60 * 60 * 1000); 
    return (new Date().after(nextCheck)) ||
      (!DrJava.getConfig().getSetting(OptionConstants.LAST_DRJAVA_SURVEY_RESULT).equals(getSurveyURL()));
  }
  
  protected void yesAction() {
    try {
      
      
      String result = getSurveyURL() + "&buildtime=" + Version.getBuildTimeString();
      LOG.log(result);
      
      if (!maySubmitSurvey()) {
        
        return;
      }
      
      BufferedReader br = null;
      try {
        URL url = new URL(result);
        InputStream urls = url.openStream();
        InputStreamReader is = new InputStreamReader(urls);
        br = new BufferedReader(is);
        String line;
        StringBuilder sb = new StringBuilder();
        while((line = br.readLine()) != null) { sb.append(line); sb.append(System.getProperty("line.separator")); }
        LOG.log(sb.toString());
      }
      catch(IOException e) {
        
        LOG.log("Could not open URL using Java", e);
        try {
          PlatformFactory.ONLY.openURL(new URL(result));
          DrJava.getConfig().setSetting(OptionConstants.LAST_DRJAVA_SURVEY_RESULT, result);
        }
        catch(IOException e2) {
          
          LOG.log("Could not open URL using web browser", e2);
        }
      }
      finally { 
        try { if (br != null) br.close(); }
        catch(IOException e) {  }
      }
    }
    finally { noAction(); }
  }
  
  
  protected final Runnable1<WindowEvent> NO = new Runnable1<WindowEvent>() {
    public void run(WindowEvent e) { noAction(); }
  };

  
  public void setVisible(boolean vis) {
    assert EventQueue.isDispatchThread();
    validate();
    if (vis) {
      _mainFrame.hourglassOn();
      _mainFrame.installModalWindowAdapter(this, LambdaUtil.NO_OP, NO);
    }
    else {
      _mainFrame.removeModalWindowAdapter(this);
      _mainFrame.hourglassOff();
      _mainFrame.toFront();
    }
    super.setVisible(vis);
  }
}
