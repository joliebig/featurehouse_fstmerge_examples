
package org.openscience.jmol.app.jmolpanel;

import org.jmol.api.*;
import org.jmol.i18n.GT;
import org.jmol.util.Logger;
import org.openscience.jmol.app.jmolpanel.JmolPanel;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.JComponent;
import javax.swing.InputVerifier;
import java.io.File;


public class PovrayDialog extends JDialog {

  private transient JmolViewer viewer;
  
  protected JButton    povrayPathButton;
  
  protected JButton    goButton;
  protected JTextField saveField;
  protected JTextField savePathLabel;
  private int          outputWidth = -1;
  private int          outputHeight = -1;
  protected JTextField povrayPathLabel;
  
  protected JCheckBox runPovCheck;
  
  protected JCheckBox allFramesCheck;
  protected JCheckBox antiAliasCheck;
  protected JCheckBox displayWhileRenderingCheck;
  
  
  private JLabel              imageSizeWidth;
  private JFormattedTextField imageSizeTextWidth;
  private JLabel              imageSizeHeight;
  private JFormattedTextField imageSizeTextHeight;
  private JCheckBox	          imageSizeRatioBox;
  private JComboBox           imageSizeRatioCombo;
  
  private JCheckBox outputFormatCheck;
  private JComboBox outputFormatCombo;

  private JCheckBox outputAlphaCheck;
  
  private JCheckBox mosaicPreviewCheck;
  private JLabel    mosaicPreviewStart;
  private JComboBox mosaicPreviewComboStart;
  private JLabel    mosaicPreviewEnd;
  private JComboBox mosaicPreviewComboEnd;

  private String outputExtension = ".png";
  private String outputFileType = "N";


  
  public PovrayDialog(JFrame f, JmolViewer viewer) {

    super(f, GT._("Render in POV-Ray"), true);
    this.viewer = viewer;

    
    String text = null;
    
    
    int screenWidth = viewer.getScreenWidth();
    int screenHeight = viewer.getScreenHeight();
    setImageDimensions(screenWidth, screenHeight);

    
    ActionListener updateActionListener = new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        updateScreen();
      }
    };
    InputVerifier updateInputVerifier = new InputVerifier() {
      public boolean verify(JComponent component) {
        updateScreen();
        return true;
      }
    };
    ItemListener updateItemListener = new ItemListener() {
      public void itemStateChanged(ItemEvent e) {
        updateScreen();
      }
    };
    
    
    Box windowBox = Box.createVerticalBox();
    getContentPane().add(windowBox);
    
    
    Box mainBox = Box.createVerticalBox();
    
    
    Box justSavingBox = Box.createVerticalBox();
    text = GT._("Conversion from Jmol to POV-Ray");
    justSavingBox.setBorder(new TitledBorder(text));
    
    Box saveBox = Box.createHorizontalBox();
    text = GT._("File Name:");
    saveBox.setBorder(new TitledBorder(text));
    text = GT._("'caffeine.pov' -> 'caffeine.pov', 'caffeine.pov.ini', 'caffeine.pov.spt'");
    saveBox.setToolTipText(text);
    saveField = new JTextField("Jmol.pov", 20);
    saveField.addActionListener(updateActionListener);
    saveField.setInputVerifier(updateInputVerifier);
    saveBox.add(saveField);
    justSavingBox.add(saveBox);

    
    Box savePathBox = Box.createHorizontalBox();
    text = GT._("Working Directory");
    savePathBox.setBorder(new TitledBorder(text));
    text = GT._("Where the .pov files will be saved");
    savePathBox.setToolTipText(text);
    savePathLabel = new JTextField("");
    savePathLabel.setEditable(false);
    savePathLabel.setBorder(null);
    savePathBox.add(savePathLabel);
    text = GT._("Select");
    JButton savePathButton = new JButton(text);
    savePathButton.addActionListener(new ActionListener() {

      public void actionPerformed(ActionEvent e) {
        showSavePathDialog();
      }
    });
    savePathBox.add(savePathButton);
    justSavingBox.add(savePathBox);
    mainBox.add(justSavingBox);

    
    Box povOptionsBox = Box.createVerticalBox();
    text = GT._("POV-Ray Runtime Options");
    povOptionsBox.setBorder(new TitledBorder(text));
    
    
    Box runPovBox = Box.createHorizontalBox();
    text = GT._("Run POV-Ray directly");
    runPovCheck = new JCheckBox(text, true);
    text = GT._("Launch POV-Ray from within Jmol");
    runPovCheck.setToolTipText(text);
    runPovCheck.addItemListener(updateItemListener);
    runPovBox.add(runPovCheck);
    runPovBox.add(Box.createGlue());
    povOptionsBox.add(runPovBox);

    
 
    
    Box displayBox = Box.createHorizontalBox();
    text = GT._("Display While Rendering");
    displayWhileRenderingCheck = new JCheckBox(text, true);
    text = GT._("Should POV-Ray attempt to display while rendering?");
    displayWhileRenderingCheck.setToolTipText(text);
    displayWhileRenderingCheck.addItemListener(updateItemListener);
    displayBox.add(displayWhileRenderingCheck);
    displayBox.add(Box.createGlue());
    povOptionsBox.add(displayBox);

    
    Box imageBox = Box.createHorizontalBox();
    
    
    
    
    
    
    
    
    
    
    
    imageBox.add(Box.createHorizontalStrut(10));
    Box imageSizeDetailBox = Box.createVerticalBox();
    Box imageSizeXYBox = Box.createHorizontalBox();
    text = GT._("width:")+" ";
    imageSizeWidth = new JLabel(text);
    text = GT._("Image width");
    imageSizeWidth.setToolTipText(text);
    imageSizeXYBox.add(imageSizeWidth);
    imageSizeTextWidth = new JFormattedTextField();
    imageSizeTextWidth.setValue(new Integer(outputWidth));
    imageSizeTextWidth.addPropertyChangeListener("value",
      new PropertyChangeListener() {
        public void propertyChange(PropertyChangeEvent e) {
          imageSizeChanged();
          updateCommandLine();
        }
      }
    );
    imageSizeXYBox.add(imageSizeTextWidth);
    imageSizeXYBox.add(Box.createHorizontalStrut(10));
    text = GT._("height:")+" ";
    imageSizeHeight = new JLabel(text);
    text = GT._("Image height");
    imageSizeHeight.setToolTipText(text);
    imageSizeXYBox.add(imageSizeHeight);
    imageSizeTextHeight = new JFormattedTextField();
    imageSizeTextHeight.setValue(new Integer(outputHeight));
    imageSizeTextHeight.addPropertyChangeListener("value",
      new PropertyChangeListener() {
        public void propertyChange(PropertyChangeEvent e) {
          imageSizeChanged();
          updateCommandLine();
        }
      }
    );
    imageSizeXYBox.add(imageSizeTextHeight);
    imageSizeXYBox.add(Box.createGlue());
    imageSizeDetailBox.add(imageSizeXYBox);
    Box imageSizeBox = Box.createHorizontalBox();
    text = GT._("Fixed ratio : ");
    imageSizeRatioBox = new JCheckBox(text, true);
    text = GT._("Use a fixed ratio for width:height");
    imageSizeRatioBox.setToolTipText(text);
    imageSizeRatioBox.addItemListener(new ItemListener() {
      public void itemStateChanged(ItemEvent e) {
        imageSizeChanged();
        updateCommandLine();
      }
    });
    imageSizeBox.add(imageSizeRatioBox);
    imageSizeBox.add(Box.createHorizontalStrut(10));
    imageSizeRatioCombo = new JComboBox();
    text = GT._("User defined");
    imageSizeRatioCombo.addItem(text);
    text = GT._("Keep ratio of Jmol window");
    imageSizeRatioCombo.addItem(text);
    text = "4:3";
    imageSizeRatioCombo.addItem(text);
    text = "16:9";
    imageSizeRatioCombo.addItem(text);
    imageSizeRatioCombo.setSelectedIndex(1);
    imageSizeRatioCombo.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        imageSizeChanged();
        updateCommandLine();
      }
    });
    imageSizeBox.add(imageSizeRatioCombo);
    imageSizeBox.add(Box.createGlue());
    imageSizeDetailBox.add(imageSizeBox);
    imageSizeDetailBox.add(Box.createGlue());
    imageBox.add(imageSizeDetailBox);
    imageBox.add(Box.createGlue());
    povOptionsBox.add(imageBox);
    imageSizeChanged();
    
    
    Box outputBox = Box.createHorizontalBox();
    
    outputBox.add(Box.createHorizontalStrut(10));
    outputFormatCombo = new JComboBox();
    
    text = GT._("N - PNG");
    outputFormatCombo.addItem(text);
    
    text = GT._("P - PPM");
    outputFormatCombo.addItem(text);
    
    text = GT._("C - Compressed Targa-24");
    outputFormatCombo.addItem(text);
    
    text = GT._("T - Uncompressed Targa-24");
    outputFormatCombo.addItem(text);
    outputFormatCombo.setSelectedIndex(0);
    outputFormatCombo.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        outputFormatChanged();
        updateCommandLine();
      }
    });
    outputBox.add(outputFormatCombo);
    outputBox.add(Box.createGlue());
    povOptionsBox.add(outputBox);
    outputFormatChanged();

    
    Box alphaBox = Box.createHorizontalBox();
    text = GT._("Alpha transparency");
    outputAlphaCheck = new JCheckBox(text, false);
    text = GT._("Output Alpha transparency data");
    outputAlphaCheck.setToolTipText(text);
    outputAlphaCheck.addItemListener(new ItemListener() {
      public void itemStateChanged(ItemEvent e) {
        updateCommandLine();
      }
    });
    alphaBox.add(outputAlphaCheck);
    alphaBox.add(Box.createGlue());
    povOptionsBox.add(alphaBox);
    
    
    Box mosaicBox = Box.createHorizontalBox();
    text = GT._("Mosaic preview");
    mosaicPreviewCheck = new JCheckBox(text, false);
    text = GT._("Render the image in several passes");
    mosaicPreviewCheck.setToolTipText(text);
    mosaicPreviewCheck.addItemListener(new ItemListener() {
      public void itemStateChanged(ItemEvent e) {
      	mosaicPreviewChanged();
      	updateCommandLine();
      }
    });
    mosaicBox.add(mosaicPreviewCheck);
    mosaicBox.add(Box.createHorizontalStrut(10));
    text = GT._("Start size : ");
    mosaicPreviewStart = new JLabel(text);
    text = GT._("Initial size of the tiles");
    mosaicPreviewStart.setToolTipText(text);
    mosaicBox.add(mosaicPreviewStart);
    mosaicPreviewComboStart = new JComboBox();
    for (int power = 0; power < 8; power++) {
    	mosaicPreviewComboStart.addItem(Integer.toString((int)Math.pow(2, power)));
    }
    mosaicPreviewComboStart.setSelectedIndex(3);
    mosaicPreviewComboStart.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        mosaicPreviewChanged();
        updateCommandLine();
      }
    });
    mosaicBox.add(mosaicPreviewComboStart);
    mosaicBox.add(Box.createHorizontalStrut(10));
    text = GT._("End size : ");
    mosaicPreviewEnd = new JLabel(text);
    text = GT._("Final size of the tiles");
    mosaicPreviewEnd.setToolTipText(text);
    mosaicBox.add(mosaicPreviewEnd);
    mosaicPreviewComboEnd = new JComboBox();
    for (int power = 0; power < 8; power++) {
    	mosaicPreviewComboEnd.addItem(Integer.toString((int)Math.pow(2, power)));
    }
    mosaicPreviewComboEnd.setSelectedIndex(0);
    mosaicPreviewComboEnd.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        mosaicPreviewChanged();
        updateCommandLine();
      }
    });
    mosaicBox.add(mosaicPreviewComboEnd);
    mosaicBox.add(Box.createGlue());
    povOptionsBox.add(mosaicBox);
    mosaicPreviewChanged();
  
    
    Box povrayPathBox = Box.createHorizontalBox();
    text = GT._("Location of the POV-Ray Executable");
    povrayPathBox.setBorder(new TitledBorder(text));
    text = GT._("Location of the POV-Ray Executable");
    povrayPathBox.setToolTipText(text);
    povrayPathLabel = new JTextField("");
    povrayPathLabel.setEditable(false);
    povrayPathLabel.setBorder(null);
    povrayPathBox.add(povrayPathLabel);
    text = GT._("Select");
    povrayPathButton = new JButton(text);
    povrayPathButton.addActionListener(new ActionListener() {

      public void actionPerformed(ActionEvent e) {
        showPovrayPathDialog();
      }
    });
    povrayPathBox.add(povrayPathButton);
    povOptionsBox.add(povrayPathBox);

    
    
    
    mainBox.add(povOptionsBox);

    
    Box buttonBox = Box.createHorizontalBox();
    buttonBox.add(Box.createGlue());
    text = GT._("Go!");
    goButton = new JButton(text);
    text = GT._("Save file and possibly launch POV-Ray");
    goButton.setToolTipText(text);
    goButton.addActionListener(new ActionListener() {

      public void actionPerformed(ActionEvent e) {
        goPressed();
      }
    });
    buttonBox.add(goButton);
    text = GT._("Cancel");
    JButton cancelButton = new JButton(text);
    text = GT._("Cancel this dialog without saving");
    cancelButton.setToolTipText(text);
    cancelButton.addActionListener(new ActionListener() {

      public void actionPerformed(ActionEvent e) {
        cancelPressed();
      }
    });
    buttonBox.add(cancelButton);
    
    windowBox.add(mainBox);
    windowBox.add(buttonBox);

    getPathHistory();
    updateScreen();
    pack();
    centerDialog();
    setVisible(true);
  }

  
  public void setImageDimensions(int imageWidth, int imageHeight) {
    outputWidth = imageWidth;
    outputHeight = imageHeight;
    updateCommandLine();
  }

  
  void goPressed() {

    
  	String basename = saveField.getText();
    String filename = basename;
    String savePath = savePathLabel.getText();
    File theFile = new File(savePath, filename);
    if (theFile != null) {
      basename = filename = theFile.getAbsolutePath();
        
        
        
        
          int height = Integer.parseInt(imageSizeTextHeight.getValue().toString());
          int width = Integer.parseInt(imageSizeTextWidth.getValue().toString());
        
        
        String data = viewer.generateOutput("Povray", filename + ":::" + getINI(), width, height);
        if (data == null)
          return;
        viewer.writeTextFile(filename + ".ini", data);        
    }
    
    
    boolean callPovray = runPovCheck.isSelected();
    if (callPovray) {
      String[] commandLineArgs = null;

      	commandLineArgs = new String[] {
      	  povrayPathLabel.getText(), filename + ".ini"
      	};



      try {
        Runtime.getRuntime().exec(commandLineArgs);
      } catch (java.io.IOException e) {
        Logger.error("Caught IOException in povray exec", e);
        Logger.error("CmdLine:");
        for (int i = 0; i < commandLineArgs.length; i++) {
          Logger.error("  <" + commandLineArgs[i] + ">");
        }
      }
    }
    setVisible(false);
    saveHistory();
    dispose();
  }       

  
  void cancelPressed() {
    setVisible(false);
    dispose();
  }

  
  void showSavePathDialog() {

    JFileChooser myChooser = new JFileChooser();
    myChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    int button = myChooser.showDialog(this, GT._("Select"));
    if (button == JFileChooser.APPROVE_OPTION) {
      java.io.File newFile = myChooser.getSelectedFile();
      String savePath;
      if (newFile.isDirectory()) {
        savePath = newFile.toString();
      } else {
        savePath = newFile.getParent();
      }
      savePathLabel.setText(savePath);
      updateCommandLine();
      pack();
    }
  }

  
  void showPovrayPathDialog() {

    JFileChooser myChooser = new JFileChooser();
    int button = myChooser.showDialog(this, GT._("Select"));
    if (button == JFileChooser.APPROVE_OPTION) {
      java.io.File newFile = myChooser.getSelectedFile();
      povrayPathLabel.setText(newFile.toString());
      updateCommandLine();
      pack();
    }
  }

  
  void imageSizeChanged() {
  	
  	  boolean selected = true;
  	  boolean enabled = true;
  	  boolean ratioSelected = false;
  	  
  	  if (imageSizeRatioBox != null) {
  	    ratioSelected = imageSizeRatioBox.isSelected();
  	    imageSizeRatioBox.setEnabled(selected && enabled);
  	  }
  	  if (imageSizeWidth != null) {
  	    imageSizeWidth.setEnabled(selected && enabled);
  	  }
  	  if (imageSizeTextWidth != null) {
  	    imageSizeTextWidth.setEnabled(selected && enabled);
  	  }
  	  if (imageSizeHeight != null) {
  	    imageSizeHeight.setEnabled(selected && !ratioSelected && enabled);
  	  }
  	  if (imageSizeTextHeight != null) {
  	    imageSizeTextHeight.setEnabled(selected && !ratioSelected && enabled);
  	  }
  	  if (imageSizeRatioCombo != null) {
  	  	imageSizeRatioCombo.setEnabled(selected && ratioSelected && enabled);
  	    if ((imageSizeTextWidth != null) && (imageSizeTextHeight != null)) {
          int width = Integer.parseInt(imageSizeTextWidth.getValue().toString());
  	      int height;
  	      switch (imageSizeRatioCombo.getSelectedIndex()) {
  	      case 0: 
  	        break;
  	      case 1: 
  	        height = (int)(((double) width) * outputHeight / outputWidth);
  	        imageSizeTextHeight.setValue(new Integer(height));
  	        break;
  	      case 2: 
  	        height = (int)(((double) width) * 3 / 4);
  	        imageSizeTextHeight.setValue(new Integer(height));
  	        break;
  	      case 3: 
  	        height = (int)(((double) width) * 9 / 16);
  	        imageSizeTextHeight.setValue(new Integer(height));
  	        break;
  	      }
  	    }
  	  }
  	
  }
  
  
  void outputFormatChanged() {
  	if (outputFormatCheck != null) {
  	  boolean selected = outputFormatCheck.isSelected();
  	  boolean enabled = true;
  	  outputFormatCheck.setEnabled(enabled);
  	  if (outputFormatCombo != null) {
  	    outputFormatCombo.setEnabled(selected && enabled);
        switch (outputFormatCombo.getSelectedIndex()) {
        case 0: 
          outputExtension = ".png";
          outputFileType = "N";
          break;
        case 1: 
          outputExtension = ".ppm";
          outputFileType = "P";
          break;
        case 2: 
          outputExtension = ".tga";
          outputFileType = "C";
          break;
        case 3: 
          outputExtension = ".tga";
          outputFileType = "T";
          break;
        }        
  	  }
  	}
  }
  
  

  void mosaicPreviewChanged() {
  	if (mosaicPreviewCheck != null) {
  	  boolean selected = mosaicPreviewCheck.isSelected();
  	  boolean enabled = runPovCheck.isSelected();
  	  mosaicPreviewCheck.setEnabled(enabled);
  	  if (mosaicPreviewStart != null) {
  	    mosaicPreviewStart.setEnabled(selected && enabled);
  	  }
  	  if (mosaicPreviewComboStart != null) {
  	    mosaicPreviewComboStart.setEnabled(selected && enabled);
  	  }
  	  if (mosaicPreviewEnd != null) {
  	    mosaicPreviewEnd.setEnabled(selected && enabled);
  	  }
  	  if (mosaicPreviewComboEnd != null) {
  	    mosaicPreviewComboEnd.setEnabled(selected && enabled);
  	  }
  	}
  }

  
  protected void updateScreen() {
  	
  	
  	boolean callPovray = false;
  	if (runPovCheck != null) {
  	  callPovray = runPovCheck.isSelected();
  	}
    String text = null;
    if (callPovray) {
      text = GT._("Go!");
    } else {
      text = GT._("Save");
    }
    if (goButton != null) {
      goButton.setText(text);
    }
    
    
    boolean useIni = true;
    
    
    if (antiAliasCheck != null) {
      antiAliasCheck.setEnabled(callPovray || useIni);
    }
    if (povrayPathButton != null) {
      povrayPathButton.setEnabled(callPovray || useIni);
    }
    
    
    
    
    
    
    imageSizeChanged();
    outputFormatChanged();

    
  	
    updateCommandLine();
  }
  
  protected void updateCommandLine() {
    
     
  }
  
  
  protected String getCommandLine() {

  	
  	String basename = null;
  	if (saveField != null) {
  		basename = saveField.getText();
  	}
  	String savePath = null;
  	if (savePathLabel != null) {
  	  savePath = savePathLabel.getText();
  	}
  	String povrayPath = null;
  	if (povrayPathLabel != null) {
  	  povrayPath = povrayPathLabel.getText();
  	}
    if ((savePath == null) ||
        (povrayPath == null) ||
	    (basename == null)) {
      
      
      
      return "";
    }

    
    if (!savePath.endsWith(java.io.File.separator)) {
      savePath += java.io.File.separator;
    }

    String commandLine =
      doubleQuoteIfContainsSpace(povrayPath) +
      " +I" + simpleQuoteIfContainsSpace(savePath + basename + ".pov");

    commandLine +=
      " +O" +
      simpleQuoteIfContainsSpace(savePath + basename + outputExtension) +
      " +F" + outputFileType;
    
    
    if ((outputAlphaCheck != null) && (outputAlphaCheck.isSelected())) {
      commandLine +=
        " +UA";
    }
    
    
    
      commandLine +=
        " +H" + imageSizeTextHeight.getValue() +
		" +W" + imageSizeTextWidth.getValue();
    
    
    
	  
		
    
    

    

      commandLine += " +A0.1";
    

    
    if ((displayWhileRenderingCheck != null) &&
        (displayWhileRenderingCheck.isSelected())) {
      commandLine += " +D +P";
    }

    
    if ((allFramesCheck != null) && (allFramesCheck.isSelected())) {
      commandLine += " +KFI1";
      commandLine += " +KFF" + viewer.getModelCount();
      commandLine += " +KI1";
      commandLine += " +KF" + viewer.getModelCount();
    }

    
    if ((mosaicPreviewCheck != null) && (mosaicPreviewCheck.isSelected())) {
      commandLine +=
        " +SP" + mosaicPreviewComboStart.getSelectedItem() +
		" +EP" + mosaicPreviewComboEnd.getSelectedItem();
    }
  
    commandLine += " -V"; 

    return commandLine;
  }

  
  
  

  private String getINI() {

    StringBuffer data = new StringBuffer();
    
  	String savePath = savePathLabel.getText();
    if (!savePath.endsWith(java.io.File.separator)) {
      savePath += java.io.File.separator;
    }
    String basename = saveField.getText();
  	
    
    data.append("Input_File_Name=" + savePath + basename + "\n");

    
    data.append("Output_to_File=true\n");
    data.append("Output_File_Type=" + outputFileType + "\n");
    data.append("Output_File_Name=" + savePath + basename + outputExtension + "\n");
    
    
    data.append("Height=" + imageSizeTextHeight.getValue() + "\n");
    data.append("Width=" + imageSizeTextWidth.getValue() + "\n");

    
    if ((allFramesCheck != null) && (allFramesCheck.isSelected())) {
      data.append("Initial_Frame=1\n");
      data.append("Final_Frame=" + viewer.getModelCount() + "\n");
      data.append("Initial_Clock=1\n");
      data.append("Final_Clock=" + viewer.getModelCount() + "\n");
    }
    
    
    if ((outputAlphaCheck != null) && (outputAlphaCheck.isSelected())) {
      data.append("Output_Alpha=true\n");
    }
    
    
      data.append("Antialias=true\n");
      data.append("Antialias_Threshold=0.1\n");

    
    if ((displayWhileRenderingCheck != null) &&
        (displayWhileRenderingCheck.isSelected())) {
      data.append("Display=true\n");
      data.append("Pause_When_Done=true\n");
    }

    
    if ((mosaicPreviewCheck != null) && (mosaicPreviewCheck.isSelected())) {
      data.append("Preview_Start_Size=" + mosaicPreviewComboStart.getSelectedItem() + "\n");
      data.append("Preview_End_Size=" + mosaicPreviewComboEnd.getSelectedItem() + "\n");
    }
    
    data.append("Warning_Level=5\n");
    data.append("Verbose=false\n");
    return data.toString();
  }

  
  
  
  protected void centerDialog() {

    Dimension screenSize = this.getToolkit().getScreenSize();
    Dimension size = this.getSize();
    screenSize.height = screenSize.height / 2;
    screenSize.width = screenSize.width / 2;
    size.height = size.height / 2;
    size.width = size.width / 2;
    int y = screenSize.height - size.height;
    int x = screenSize.width - size.width;
    this.setLocation(x, y);
  }

  
  class PovrayWindowListener extends WindowAdapter {

    
    public void windowClosing(WindowEvent e) {
      cancelPressed();
      setVisible(false);
      dispose();
    }
  }

  
  private void getPathHistory() {

    java.util.Properties props = JmolPanel.historyFile.getProperties();
    if (povrayPathLabel != null) {
      String povrayPath = props.getProperty("povrayPath",
        System.getProperty("user.home"));
      if (povrayPath != null) {
        povrayPathLabel.setText(povrayPath);
      }
    }
    if (savePathLabel != null) {
      String savePath = props.getProperty("povraySavePath",
        System.getProperty("user.home"));
      if (savePath != null) {
        savePathLabel.setText(savePath);
      }
    }
  }

  
  private void saveHistory() {
    java.util.Properties props = new java.util.Properties();
    props.setProperty("povrayPath", povrayPathLabel.getText());
    props.setProperty("povraySavePath", savePathLabel.getText());
    JmolPanel.historyFile.addProperties(props);
  }

  String doubleQuoteIfContainsSpace(String str) {
    for (int i = str.length(); --i >= 0; )
      if (str.charAt(i) == ' ')
        return "\"" + str + "\"";
    return str;
  }

  String simpleQuoteIfContainsSpace(String str) {
    for (int i = str.length(); --i >= 0; )
      if (str.charAt(i) == ' ')
        return "\'" + str + "\'";
    return str;
  }
}
