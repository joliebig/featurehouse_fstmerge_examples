


package net.sf.freecol.client.gui.option;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import net.sf.freecol.FreeCol;
import net.sf.freecol.client.gui.Canvas;
import net.sf.freecol.client.gui.i18n.Messages;
import net.sf.freecol.common.option.FileOption;
import net.sf.freecol.common.option.Option;


public final class FileOptionUI extends JPanel implements OptionUpdater, PropertyChangeListener {

    @SuppressWarnings("unused")
    private static final Logger logger = Logger.getLogger(FileOptionUI.class.getName());

    private final FileOption option;
    private final JTextField fileField;
    private File originalValue;

    
    
    public FileOptionUI(final FileOption option, boolean editable) {
        super(new FlowLayout(FlowLayout.LEFT));

        this.option = option;
        this.originalValue = option.getValue();

        String name = option.getName();
        String description = option.getShortDescription();
        JLabel label = new JLabel(name, JLabel.LEFT);
        label.setToolTipText((description != null) ? description : name);
        add(label);

        final String value = (option.getValue() != null) 
            ? option.getValue().getAbsolutePath()
            : "";
        fileField = new JTextField(value, 10);
        add(fileField);
        
        JButton browse = new JButton(Messages.message("file.browse"));
        if (editable) {
            browse.addActionListener(new ActionListener() {
               public void actionPerformed(ActionEvent e) {
                   final Canvas canvas = FreeCol.getFreeColClient().getCanvas();
                   File file = canvas.showLoadDialog(FreeCol.getSaveDirectory());

                   if (file == null) {
                       return;
                   }

                   if (!file.isFile()) {
                       canvas.errorMessage("fileNotFound");
                       return;
                   }

                   fileField.setText(file.getAbsolutePath());
               }
            });
        }
        add(browse);
        
        JButton remove = new JButton(Messages.message("option.remove"));
        if (editable) {
            remove.addActionListener(new ActionListener() {
               public void actionPerformed(ActionEvent e) {
                   fileField.setText("");
               }
            });
        }
        add(remove);
        
        browse.setEnabled(editable);
        remove.setEnabled(editable);
        fileField.setEnabled(false);
        label.setLabelFor(fileField);
        fileField.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent arg0) {
                editUpdate();
            }
            public void insertUpdate(DocumentEvent arg0) {
                editUpdate();
            }
            public void removeUpdate(DocumentEvent arg0) {
                editUpdate();
            }
            private void editUpdate() {
                if (option.isPreviewEnabled()) {
                    final File value = new File(fileField.getText());
                    if (!option.getValue().equals(value)) {
                        option.setValue(value);
                    }
                }
            }
        });
        
        option.addPropertyChangeListener(this);
        
        setOpaque(false);
    }


    
    public void rollback() {
        option.setValue(originalValue);
    }
    
    
    public void unregister() {
        option.removePropertyChangeListener(this);    
    }
    
    
    public void propertyChange(PropertyChangeEvent event) {
        if (event.getPropertyName().equals("value")) {
            final File value = (File) event.getNewValue();
            if (!value.equals(new File(fileField.getText()))) {
                fileField.setText(value.getAbsolutePath());
                originalValue = value;
            }
        }
    }
    
    
    public void updateOption() {
        if (fileField.getText().equals("")) {
            option.setValue(null);
        } else {
            option.setValue(new File(fileField.getText()));
        }
    }
    
    
    public void reset() {
        setValue(option.getValue());
    }
    
    
    public void setValue(File f) {
        if (f != null) { 
            fileField.setText(f.getAbsolutePath());
        } else {
            fileField.setText("");
        }
    }
}
