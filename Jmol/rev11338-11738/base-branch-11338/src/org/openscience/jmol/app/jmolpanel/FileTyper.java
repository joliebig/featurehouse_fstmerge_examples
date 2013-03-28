
package org.openscience.jmol.app.jmolpanel;

import java.io.File;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.JLabel;
import javax.swing.JFileChooser;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JComboBox;
import javax.swing.border.EmptyBorder;


import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.BorderLayout;

public class FileTyper extends JPanel
    implements PropertyChangeListener, ItemListener {

  private JCheckBox useFileExtensionCheckBox;
  private JLabel fileTypeLabel;
  private JComboBox fileTypeComboBox;
  private boolean useFileExtension = true;

  private String[] choices = {
    JmolResourceHandler.getStringX("FileTyper.XYZ"),
    JmolResourceHandler.getStringX("FileTyper.PDB"),
    JmolResourceHandler.getStringX("FileTyper.CML"),
  };

  
  private int defaultTypeIndex = 0;
  private String fileType = choices[defaultTypeIndex];

  
  public FileTyper() {

    setLayout(new BorderLayout());

    JPanel fileTypePanel = new JPanel();
    fileTypePanel.setBorder(new EmptyBorder(5, 5, 5, 5));
    fileTypePanel.setLayout(new GridBagLayout());

    JLabel fillerLabel = new JLabel();
    GridBagConstraints gridBagConstraints = new GridBagConstraints();
    gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER;
    gridBagConstraints.fill = GridBagConstraints.BOTH;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.weighty = 1.0;
    fileTypePanel.add(fillerLabel, gridBagConstraints);


    useFileExtensionCheckBox =
        new JCheckBox(JmolResourceHandler
          .getStringX("FileTyper.useFileExtensionCheckBox"), useFileExtension);
    useFileExtensionCheckBox.addItemListener(this);
    String mnemonic =
      JmolResourceHandler
        .getStringX("FileTyper.useFileExtensionMnemonic");
    if ((mnemonic != null) && (mnemonic.length() > 0)) {
      useFileExtensionCheckBox.setMnemonic(mnemonic.charAt(0));
    }
    gridBagConstraints = new GridBagConstraints();
    gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER;
    fileTypePanel.add(useFileExtensionCheckBox, gridBagConstraints);

    gridBagConstraints = new GridBagConstraints();
    fileTypeLabel =
        new JLabel(JmolResourceHandler.getStringX("FileTyper.fileTypeLabel"));
    fileTypeLabel.setForeground(Color.black);
    fileTypePanel.add(fileTypeLabel, gridBagConstraints);
    fileTypeComboBox = new JComboBox(choices);
    gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER;
    fileTypePanel.add(fileTypeComboBox, gridBagConstraints);
    fileTypeComboBox.setSelectedIndex(defaultTypeIndex);
    fileTypeComboBox.addItemListener(this);

    add(fileTypePanel, BorderLayout.CENTER);

    setUseFileExtension(useFileExtension);
  }

  
  public String getType() {
    return fileType;
  }

  private void setUseFileExtension(boolean value) {
    useFileExtension = value;
    fileTypeLabel.setEnabled(!useFileExtension);
    fileTypeComboBox.setEnabled(!useFileExtension);
  }

  public void itemStateChanged(ItemEvent event) {

    if (event.getSource() == useFileExtensionCheckBox) {
      if (event.getStateChange() == ItemEvent.DESELECTED) {
        setUseFileExtension(false);
      } else {
        setUseFileExtension(true);
      }
    } else if (event.getSource() == fileTypeComboBox) {
      fileType = (String) fileTypeComboBox.getSelectedItem();
    }
  }

  public void propertyChange(PropertyChangeEvent event) {

    String property = event.getPropertyName();
    if (useFileExtension) {
      if (property.equals(JFileChooser.SELECTED_FILE_CHANGED_PROPERTY)) {
        File file = (File) event.getNewValue();
        String fileName = file.toString().toLowerCase();
        if (fileName.endsWith("xyz")) {
          fileTypeComboBox.setSelectedIndex(0);
        } else if (fileName.endsWith("pdb")) {
          fileTypeComboBox.setSelectedIndex(1);
        } else if (fileName.endsWith("cml")) {
          fileTypeComboBox.setSelectedIndex(2);
        } else {
          fileTypeComboBox.setSelectedIndex(0);
        }
      }
    }
  }

}

