

package edu.rice.cs.drjava.ui;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.event.*;
import java.awt.*;
import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.jar.*;

import edu.rice.cs.drjava.*;
import edu.rice.cs.drjava.config.OptionConstants;
import edu.rice.cs.drjava.platform.*;
import edu.rice.cs.util.swing.Utilities;
import edu.rice.cs.plt.lambda.Runnable1;
import edu.rice.cs.plt.lambda.LambdaUtil;
import edu.rice.cs.plt.lambda.Box;
import edu.rice.cs.plt.lambda.SimpleBox;
import edu.rice.cs.plt.concurrent.JVMBuilder;
import edu.rice.cs.util.FileOps;


public class NewVersionPopup extends JDialog {
  
  private JComboBox _modeBox;
  
  private JButton _closeButton;
  
  private JButton _updateButton;
  
  private JButton _downloadButton;
  
  private MainFrame _mainFrame;
  
  private JOptionPane _versionPanel;
  
  private JPanel _bottomPanel;
  
  private static Date BUILD_TIME = Version.getBuildTime();
  
  private String[] _msg = null;
  
  private String _newestVersionString = "";
  
  
  
  public NewVersionPopup(MainFrame parent) {
    super(parent, "Check for New Version of DrJava");
    setResizable(false);
    
    _mainFrame = parent;
    _mainFrame.setPopupLoc(this);
    this.setSize(500,150);
    
    _modeBox = new JComboBox(OptionConstants.NEW_VERSION_NOTIFICATION_CHOICES.toArray());
    for(int i = 0; i < OptionConstants.NEW_VERSION_NOTIFICATION_CHOICES.size(); ++i) {
      if (DrJava.getConfig().getSetting(OptionConstants.NEW_VERSION_NOTIFICATION)
            .equals(OptionConstants.NEW_VERSION_NOTIFICATION_CHOICES.get(i))) {
        _modeBox.setSelectedIndex(i);
        break;
      }
    }
    _modeBox.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        DrJava.getConfig().setSetting(OptionConstants.NEW_VERSION_NOTIFICATION,
                                      OptionConstants.NEW_VERSION_NOTIFICATION_CHOICES.get(_modeBox.getSelectedIndex()));
        _msg = null;
        updateText();
      }
    });
    
    _updateButton = new JButton(_updateAction);
    _downloadButton = new JButton(_downloadAction);
    _closeButton = new JButton(_closeAction);
    _updateButton.setEnabled(false);
    _downloadButton.setEnabled(false);
    
    _bottomPanel = new JPanel(new BorderLayout());
    JPanel buttonPanel = new JPanel();
    buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
    buttonPanel.add(_updateButton);
    buttonPanel.add(_downloadButton);
    buttonPanel.add(_closeButton);
    _bottomPanel.add(buttonPanel, BorderLayout.CENTER);
    JPanel comboPanel = new JPanel();
    comboPanel.add(new JLabel("Check for: "));
    comboPanel.add(_modeBox);
    _bottomPanel.add(comboPanel, BorderLayout.WEST);
    
    updateText();
  }
  
  private void updateText() {
    if (_msg != null) {
      _versionPanel = new JOptionPane(_msg,JOptionPane.INFORMATION_MESSAGE,
                                      JOptionPane.DEFAULT_OPTION,null,
                                      new Object[0]);   
      
      JPanel cp = new JPanel(new BorderLayout(5,5));
      cp.setBorder(new EmptyBorder(5,5,5,5));
      setContentPane(cp);
      cp.add(_versionPanel, BorderLayout.CENTER);
      cp.add(_bottomPanel, BorderLayout.SOUTH);    
      getRootPane().setDefaultButton(_closeButton);
      setTitle("Check for New Version of DrJava");
      pack();
      _mainFrame.setPopupLoc(this);
      return;
    }
    setTitle("Checking for new versions, please wait...");
    String[] msg = new String[] {"Checking drjava.org for new versions.", "Please wait..."};
    _versionPanel = new JOptionPane(msg,JOptionPane.INFORMATION_MESSAGE,
                                    JOptionPane.DEFAULT_OPTION,null,
                                    new Object[0]);   
    JPanel cp = new JPanel(new BorderLayout(5,5));
    cp.setBorder(new EmptyBorder(5,5,5,5));
    setContentPane(cp);
    cp.add(_versionPanel, BorderLayout.CENTER);
    cp.add(_bottomPanel, BorderLayout.SOUTH);    
    getRootPane().setDefaultButton(_closeButton);
    pack();
    _mainFrame.setPopupLoc(this);
    
    
    
    EventQueue.invokeLater(new Runnable() {
      public void run() {
        _msg = getMessage(null);
        _versionPanel = new JOptionPane(_msg,JOptionPane.INFORMATION_MESSAGE,
                                        JOptionPane.DEFAULT_OPTION,null,
                                        new Object[0]);   
        
        JPanel cp = new JPanel(new BorderLayout(5,5));
        cp.setBorder(new EmptyBorder(5,5,5,5));
        setContentPane(cp);
        cp.add(_versionPanel, BorderLayout.CENTER);
        cp.add(_bottomPanel, BorderLayout.SOUTH);    
        getRootPane().setDefaultButton(_closeButton);
        setTitle("Check for New Version of DrJava");
        pack();
        _mainFrame.setPopupLoc(NewVersionPopup.this);
      }
    });
  }
  
  
  private Action _closeAction = new AbstractAction("Close") {
    public void actionPerformed(ActionEvent e) { closeAction(); }
  };
  
  
  private Action _updateAction = new AbstractAction("Automatic Update") {
    public void actionPerformed(ActionEvent e) { updateAction(); }
  };
  
  
  private Action _downloadAction = new AbstractAction("Manual Download") {
    public void actionPerformed(ActionEvent e) { downloadAction(); }
  };
  
  protected void closeAction() {
    NewVersionPopup.this.setVisible(false);
    NewVersionPopup.this.dispose();
  }
  
  protected void downloadAction() {
    closeAction();
    _openFileDownloadPage(getManualDownloadURL());
  }
  
  public static final edu.rice.cs.util.Log LOG = new edu.rice.cs.util.Log("version.txt",false);
  
  protected void abortUpdate() {
    abortUpdate("", false);
  }
  
  protected void abortUpdate(String message) {
    abortUpdate(message, false);
  }

  protected void abortUpdate(boolean close) {
    abortUpdate("", close);
  }
  
  protected void abortUpdate(String message, boolean close) {
    LOG.log(message);
    if (close) closeAction();
    StringBuilder sb = new StringBuilder();
    sb.append("Could not update DrJava automatically");
    if (message.length() > 0) {
      sb.append(":\n");
      sb.append(message);
    }
    else {
      sb.append('.');
    }
    sb.append("\nPlease download DrJava yourself.");
    JOptionPane.showMessageDialog(this, sb.toString(),
                                  "Error Updating DrJava", JOptionPane.ERROR_MESSAGE);
    downloadAction();
  }
  
  protected void updateAction() {
    JPanel cp = new JPanel(new BorderLayout(5,5));
    cp.setBorder(new EmptyBorder(5,5,5,5));
    setContentPane(cp);
    cp.add(new JOptionPane("Waiting for www.sourceforge.net ...",JOptionPane.INFORMATION_MESSAGE,
                           JOptionPane.DEFAULT_OPTION,null,
                           new Object[0]), BorderLayout.CENTER);
    JProgressBar pb = new JProgressBar(0,100);
    pb.setIndeterminate(true);
    cp.add(pb, BorderLayout.SOUTH);
    validate();
    
    new Thread(new Runnable() {
      public void run() {
        
        ArrayList<File> toCleanUp = new ArrayList<File>();
        
        try {
          LOG.log("updateAction");
          
          final File targetFile = FileOps.getDrJavaApplicationFile();
          LOG.log("\ttargetFile = "+targetFile);
          if ((targetFile == null) || (targetFile.getParentFile() == null)) {
            abortUpdate("Could not determine where DrJava is located on this computer.", true);
            return;
          }
          
          
          String fileName = _newestVersionString;
          final int lastDotPos = fileName.length();
          if (targetFile.toString().endsWith(".jar")) { fileName += ".jar"; }
          else if (targetFile.toString().endsWith(".exe")) { fileName += ".exe"; }
          else if (targetFile.toString().endsWith(".app")) { fileName += "-osx.tar.gz"; }
          else { abortUpdate("Could not determine the file type to download.", true); return; }
          LOG.log("\tfileName = "+fileName);
          
          
          File destFile = FileOps.generateNewFileName(targetFile.getParentFile(),
                                                      fileName.substring(0,lastDotPos),
                                                      fileName.substring(lastDotPos));
          toCleanUp.add(destFile);
          LOG.log("Downloading to "+destFile);
          
          File macTempDir = null;
          File macTarFile = null;
          if (fileName.endsWith("-osx.tar.gz")) {
            
            
            macTarFile = new File("/usr/bin/tar");
            LOG.log("Searching for "+macTarFile);
            if (!macTarFile.exists()) {
              String path = System.getenv("PATH");
              for(String p: path.split(System.getProperty("path.separator"))) {
                macTarFile = new File(p, "tar");
                LOG.log("Searching for "+macTarFile);
                if (macTarFile.exists()) break;
              }
              if (!macTarFile.exists()) { abortUpdate("Could not find tar on this computer.", true); return; }
            }
            
            
            macTempDir = FileOps.generateNewFileName(destFile.getParentFile(), _newestVersionString);
          }
          
          
          final File tempClassFile = File.createTempFile("drjavarestart-",".jar");
          toCleanUp.add(tempClassFile);
          BufferedOutputStream tempClassOut = new BufferedOutputStream(new FileOutputStream(tempClassFile));
          BufferedInputStream tempClassIn = new BufferedInputStream(new FileInputStream(FileOps.getDrJavaFile()));
          edu.rice.cs.plt.io.IOUtil.copyInputStream(tempClassIn, tempClassOut);
          tempClassIn.close();
          tempClassOut.close();
          LOG.log("Copied drjava.jar to "+tempClassFile);
          
          
          URL fileURL = new URL(getAutomaticDownloadURL()+fileName);
          LOG.log("fileURL = "+fileURL);
          
          URLConnection uc = fileURL.openConnection();
          final int length = uc.getContentLength();
          InputStream in = uc.getInputStream();
          ProgressMonitorInputStream pin = new ProgressMonitorInputStream(_mainFrame, "Downloading "+fileName+" ...", in);
          ProgressMonitor pm = pin.getProgressMonitor();
          pm.setMaximum(length);
          pm.setMillisToDecideToPopup(0);
          pm.setMillisToPopup(0);
          EventQueue.invokeLater(new Runnable() { public void run() { closeAction(); } });
          
          BufferedInputStream bin = new BufferedInputStream(pin);
          BufferedOutputStream bout = new BufferedOutputStream(new FileOutputStream(destFile));
          edu.rice.cs.plt.io.IOUtil.copyInputStream(bin,bout);
          bin.close();
          bout.close();
          if ((!destFile.exists()) || (destFile.length() != length)) {
            abortUpdate("Could not download update."); return;
          }
          
          LOG.log("Downloaded to "+destFile);
          
          if (fileName.endsWith("-osx.tar.gz")) {
            
            macTempDir.mkdirs();
            toCleanUp.add(macTempDir);
            Process p = new ProcessBuilder()
              .command(macTarFile.getAbsolutePath(), "xfz", destFile.getAbsolutePath())
              .directory(macTempDir)
              .redirectErrorStream(true)
              .start();
            boolean waiting = false;
            do {
              try {
                p.waitFor();
                waiting = false;
              }
              catch(InterruptedException ie) { abortUpdate("Installation was interrupted."); return; }
            } while(waiting);
            if (p.exitValue() != 0) { abortUpdate("Unpacking with tar failed."); return; }
            
            destFile.delete();
            destFile = new File(macTempDir, "DrJava.app");
            if (!destFile.exists() ||
                !new File(destFile,"Contents/Resources/Java/drjava.jar").exists()) {
              abortUpdate("Downloaded file contained unexpected files."); return;
            }
          }
          else {
            
            JarFile jf = new JarFile(destFile);
          }
          
          
          
          toCleanUp.clear();
          
          
          final File finalDestFile = destFile;
          Thread restart = new Thread() {
            public void run() {
              try {
                LOG.log("Restarting...");
                Process p = JVMBuilder.DEFAULT.classPath(tempClassFile).start(DrJavaRestart.class.getName(),
                                                                              finalDestFile.getAbsolutePath(),
                                                                              targetFile.getAbsolutePath(),
                                                                              tempClassFile.getAbsolutePath());
              }
              catch(Exception e) {
                LOG.log("Exception in shutdown hook: "+e);
                tempClassFile.delete();
                JOptionPane.showMessageDialog(null, 
                                              "A new version of DrJava was downloaded. However,\n"+
                                              "it could not be started automatically.\n\n"+
                                              "The new copy is now installed at:\n"+
                                              finalDestFile.getAbsolutePath()+"\n\n"+
                                              "The old copy is still installed at:\n"+
                                              targetFile.getAbsolutePath()+"\n\n"+
                                              "Please start DrJava manually.",
                                              "Error Updating DrJava", JOptionPane.ERROR_MESSAGE);
                
              }
            }
          };
          Runtime.getRuntime().addShutdownHook(restart);
          Utilities.invokeAndWait(new Runnable() { public void run() { _mainFrame.quit(); } });
          
          
          Runtime.getRuntime().removeShutdownHook(restart);
          tempClassFile.delete();
          JOptionPane.showMessageDialog(_mainFrame, 
                                        "A new version of DrJava was downloaded. However, you chose\n"+
                                        "not to restart DrJava, so the old version was not automatically\n"+
                                        "replaced.\n\n"+
                                        "The new copy is now installed at:\n"+
                                        finalDestFile.getAbsolutePath()+"\n\n"+
                                        "The old copy is still installed at:\n"+
                                        targetFile.getAbsolutePath());
        }
        catch(InterruptedIOException iie) {  return; }
        catch(final IOException e) {
          EventQueue.invokeLater(new Runnable() {
            public void run() {
              abortUpdate("Error installing update:\n"+e.getMessage()); return; 
            }
          });
        }
        finally {
          
          for(File f: toCleanUp) { edu.rice.cs.plt.io.IOUtil.deleteRecursively(f); }
        }
      }
    }).start();
  }
  
  
  protected String getAutomaticDownloadURL() {
    if (_newestVersionString.indexOf("weekly") > 0) {
      return "http://www.cs.rice.edu/~javaplt/drjavarice/weekly/files/";
    }
    else {
      return "http://prdownloads.sourceforge.net/drjava/";
    }
  }
  
  
  protected String getManualDownloadURL() {
    if (_newestVersionString.indexOf("weekly") > 0) {
      return "http://www.cs.rice.edu/~javaplt/drjavarice/weekly/";
    }
    final String DRJAVA_FILES_PAGE = "http://sourceforge.net/project/showfiles.php?group_id=44253";
    final String LINK_PREFIX = "<a href=\"/project/showfiles.php?group_id=44253";
    final String LINK_SUFFIX = "\">";
    BufferedReader br = null;
    try {
      URL url = new URL(DRJAVA_FILES_PAGE);
      InputStream urls = url.openStream();
      InputStreamReader is = new InputStreamReader(urls);
      br = new BufferedReader(is);
      String line;
      int pos;
      
      while((line = br.readLine()) != null) {
        if ((pos = line.indexOf(_newestVersionString)) >= 0) {
          int prePos = line.indexOf(LINK_PREFIX);
          if ((prePos >= 0) && (prePos < pos)) {
            int suffixPos = line.indexOf(LINK_SUFFIX, prePos);
            if ((suffixPos >= 0) && (suffixPos + LINK_SUFFIX.length() == pos)) {
              String versionLink = 
                edu.rice.cs.plt.text.TextUtil.xmlUnescape(line.substring(prePos + LINK_PREFIX.length(), suffixPos));
              return DRJAVA_FILES_PAGE + versionLink;
            }
          }
        }
      };
    }
    catch(IOException e) { return DRJAVA_FILES_PAGE; }
    finally { 
      try { if (br != null) br.close(); }
      catch(IOException e) {  }
    }
    return DRJAVA_FILES_PAGE;
  }
  
  
  private void _openFileDownloadPage(String url) {
    try { PlatformFactory.ONLY.openURL(new URL(url)); }
    catch(Exception ex) {  }
  }
  
  
  public boolean checkNewVersion() {
    Box<Boolean> availableRef = new SimpleBox<Boolean>(false);
    getMessage(availableRef);
    return availableRef.value();
  }
  
  @SuppressWarnings("fallthrough")
  protected String[] getMessage(Box<Boolean> availableRef) {
    Box<String> stableString = new SimpleBox<String>("");
    Box<String> betaString = new SimpleBox<String>("");
    Box<String> devString = new SimpleBox<String>("");
    Box<String> weeklyString = new SimpleBox<String>("");
    Box<Date> stableTime = new SimpleBox<Date>(new Date(0));
    Box<Date> betaTime = new SimpleBox<Date>(new Date(0));
    Box<Date> devTime = new SimpleBox<Date>(new Date(0));
    Box<Date> weeklyTime = new SimpleBox<Date>(new Date(0));
    boolean newVersion = false;
    _newestVersionString = "";
    if (availableRef != null) { availableRef.set(false); }
    switch(_modeBox.getSelectedIndex()) {
      case 3: if (FileOps.getDrJavaApplicationFile().toString().endsWith(".jar")) { 
        newVersion |= checkNewWeeklyVersion(weeklyString,weeklyTime); 
      }
      case 2:
        newVersion |= checkNewDevVersion(devString,devTime); 
      case 1:
        newVersion |= checkNewBetaVersion(betaString,betaTime); 
      case 0:
        newVersion |= checkNewStableVersion(stableString,stableTime);
        _updateButton.setEnabled(newVersion);
        _downloadButton.setEnabled(newVersion);
        DrJava.getConfig().setSetting(OptionConstants.LAST_NEW_VERSION_NOTIFICATION, new Date().getTime());
        if (availableRef != null) { availableRef.set(newVersion); }
        if (newVersion) {
          TreeMap<Date,String[]> versionSorter = new TreeMap<Date,String[]>();
          versionSorter.put(stableTime.value(),new String[] {"stable release",      stableString.value() });
          versionSorter.put(betaTime.value(),  new String[] {"beta release",        betaString.value() });
          versionSorter.put(devTime.value(),   new String[] {"development release", devString.value() });
          versionSorter.put(weeklyTime.value(),new String[] {"weekly build",        weeklyString.value() });
          String newestType = versionSorter.get(versionSorter.lastKey())[0];
          _newestVersionString = versionSorter.get(versionSorter.lastKey())[1];

          return new String[] {
            "A new "+newestType+" has been found.",
              "The new version is: "+_newestVersionString,
              "Do you want to download this new version?"};
        }
        else {
          if (availableRef != null) { availableRef.set(false); }
          return new String[] {
            "No new version of DrJava has been found.", "You are using the newest version that matches your criterion."};
        }
      default:
        _updateButton.setEnabled(false);
        _downloadButton.setEnabled(false);
        return new String[] { "Checking for new versions has been disabled.", "You can change this setting below." };
    }
  }
  
  
  public static boolean checkNewStableVersion(Box<String> versionStringRef,
                                              Box<Date> buildTimeRef) {
    try {
      Date newestTime = getBuildTime(new URL("http://www.drjava.org/LATEST_VERSION.TXT"), versionStringRef);
      if (newestTime == null) { return false; }
      if (buildTimeRef != null) { buildTimeRef.set(newestTime); }
      return BUILD_TIME.before(newestTime);
    }
    catch(MalformedURLException e) { return false; }
  }
  
  public static boolean checkNewBetaVersion(Box<String> versionStringRef,
                                            Box<Date> buildTimeRef) {
    try {
      Date newestTime = getBuildTime(new URL("http://www.drjava.org/LATEST_BETA_VERSION.TXT"), versionStringRef);
      if (newestTime == null) { return false; }
      if (buildTimeRef != null) { buildTimeRef.set(newestTime); }
      return BUILD_TIME.before(newestTime);
    }
    catch(MalformedURLException e) { return false; }
  }
  
  public static boolean checkNewDevVersion(Box<String> versionStringRef,
                                           Box<Date> buildTimeRef) {
    try {
      Date newestTime = getBuildTime(new URL("http://www.drjava.org/LATEST_DEV_VERSION.TXT"), versionStringRef);
      if (newestTime == null) { return false; }
      if (buildTimeRef != null) { buildTimeRef.set(newestTime); }
      return BUILD_TIME.before(newestTime);
    }
    catch(MalformedURLException e) { return false; }
  }
  
  
  public static boolean checkNewWeeklyVersion(Box<String> versionStringRef,
                                              Box<Date> buildTimeRef) {
    try {
      Date newestTime = getBuildTime(new URL("http://www.cs.rice.edu/~javaplt/drjavarice/weekly/LATEST_WEEKLY_VERSION.TXT"), versionStringRef);
      if (newestTime == null) { return false; }
      if (buildTimeRef != null) { buildTimeRef.set(newestTime); }
      return BUILD_TIME.before(newestTime);
    }
    catch(MalformedURLException e) { return false; }
  }
  
  
  public static Date getBuildTime(URL url) {
    return getBuildTime(url, null);
  }
  
  
  public static Date getBuildTime(URL url, Box<String> versionStringRef) {
    try {
      InputStream urls = url.openStream();
      InputStreamReader is = null;
      BufferedReader br = null;
      is = new InputStreamReader(urls);
      br = new BufferedReader(is);
      String line = br.readLine();
      if (versionStringRef != null) { versionStringRef.set(line); }
      br.close();
      
      
      final String DRJAVA_PREFIX = "drjava-";
      if (!line.startsWith(DRJAVA_PREFIX)) { return null; }
      line = line.substring(DRJAVA_PREFIX.length());
      
      final String STABLE_PREFIX = "stable-";
      if (line.startsWith(STABLE_PREFIX)) { line = line.substring(STABLE_PREFIX.length()); }
      
      final String BETA_PREFIX = "beta-";
      if (line.startsWith(BETA_PREFIX)) { line = line.substring(BETA_PREFIX.length()); }
      
      final String WEEKLY_PREFIX = "weekly-";
      if (line.startsWith(WEEKLY_PREFIX)) { line = line.substring(WEEKLY_PREFIX.length()); }
      
      int releasePos = line.indexOf("-r");
      if (releasePos>=0) { line = line.substring(0, releasePos); }
      
      return new SimpleDateFormat("yyyyMMdd z").parse(line + " GMT");
    }
    catch (Exception e) { 
      return null;
    }
  }
  
  
  protected final Runnable1<WindowEvent> CANCEL = new Runnable1<WindowEvent>() {
    public void run(WindowEvent e) { closeAction(); }
  };
  
  
  public void setVisible(boolean vis) {
    assert EventQueue.isDispatchThread();
    validate();
    if (vis) {
      _mainFrame.hourglassOn();
      _mainFrame.installModalWindowAdapter(this, LambdaUtil.NO_OP, CANCEL);
    }
    else {
      _mainFrame.removeModalWindowAdapter(this);
      _mainFrame.hourglassOff();
      _mainFrame.toFront();
    }
    super.setVisible(vis);
  }
}
