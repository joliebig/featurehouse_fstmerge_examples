

package net.sf.freecol.client.gui.panel;

import java.awt.AWTEvent;
import java.awt.ActiveEvent;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GridLayout;
import java.awt.MenuComponent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileFilter;

import net.miginfocom.swing.MigLayout;
import net.sf.freecol.FreeCol;
import net.sf.freecol.client.gui.Canvas;
import net.sf.freecol.client.gui.i18n.Messages;
import net.sf.freecol.server.generator.MapGeneratorOptions;



public class FreeColDialog<T> extends FreeColPanel {

    private static final Logger logger = Logger.getLogger(FreeColDialog.class.getName());

    protected static final String CANCEL = "CANCEL";

    
    private T response = null;

    
    private boolean responseGiven = false;

    protected JButton cancelButton = new JButton(Messages.message("cancel"));

    
    public FreeColDialog(Canvas parent) {
        super(parent);

        cancelButton.setActionCommand(CANCEL);
        cancelButton.addActionListener(this);
        enterPressesWhenFocused(cancelButton);
        setCancelComponent(cancelButton);
    }

    
    public synchronized void setResponse(T response) {
        this.response = response;
        responseGiven = true;
        logger.info("Response has been set to " + response);
        notifyAll();
    }


    
    public synchronized T getResponse() {
        
        

        try {
            if (SwingUtilities.isEventDispatchThread()) {
                EventQueue theQueue = getToolkit().getSystemEventQueue();

                while (!responseGiven) {
                    
                    AWTEvent event = theQueue.getNextEvent();
                    Object src = event.getSource();

                    
                    if (event instanceof ActiveEvent) {
                        ((ActiveEvent) event).dispatch();
                    } else if (src instanceof Component) {
                        ((Component) src).dispatchEvent(event);
                    } else if (src instanceof MenuComponent) {
                        ((MenuComponent) src).dispatchEvent(event);
                    } else {
                        logger.warning("unable to dispatch event: " + event);
                    }
                }
            } else {
                while (!responseGiven) {
                    wait();
                }
            }
        } catch(InterruptedException e){}

        T tempResponse = response;
        response = null;
        responseGiven = false;

        return tempResponse;
    }

    
    public void resetResponse() {
        response = null;
        responseGiven = false;
    }

    
    public static <T> FreeColDialog<ChoiceItem<T>> createChoiceDialog(String text, String cancelText, 
                                                                      List<ChoiceItem<T>> choices) {

        if (choices.isEmpty()) {
            throw new IllegalArgumentException("Can not create choice dialog with 0 choices!");
        }

        
        final List<JButton> choiceBtnLst = new ArrayList<JButton>();

        final FreeColDialog<ChoiceItem<T>> choiceDialog =
            new FreeColDialog<ChoiceItem<T>>(FreeCol.getFreeColClient().getCanvas()) {
            public void requestFocus() {
            	for(JButton b : choiceBtnLst){
            		if(b.isEnabled()){
            			b.requestFocus();
            			return;
            		}
            	}
            }
        };

        choiceDialog.setLayout(new MigLayout("fillx, wrap 1", "[align center]", ""));
        JTextArea textArea = getDefaultTextArea(text);

        choiceDialog.add(textArea);

        int columns = 1;
             if ((choices.size() % 4) == 0 && choices.size() > 12) columns = 4;
        else if ((choices.size() % 3) == 0 && choices.size() > 6)  columns = 3;
        else if ((choices.size() % 2) == 0 && choices.size() > 4)  columns = 2;
        
        else if (choices.size() > 21) columns = 4;
        else if (choices.size() > 10) columns = 2;
        
        JPanel choicesPanel = new JPanel(new GridLayout(0, columns, 10, 10));
        choicesPanel.setBorder(new CompoundBorder(choicesPanel.getBorder(), 
                                                  new EmptyBorder(10, 20, 10, 20)));

        

        for (final ChoiceItem<T> object : choices) {
            final JButton objectButton = new JButton(object.toString());
            if(object.isEnabled()){
            	objectButton.addActionListener(new ActionListener() {
            		public void actionPerformed(ActionEvent event) {
            			choiceDialog.setResponse(object);
            		}
            	});
            	enterPressesWhenFocused(objectButton);
            }
            objectButton.setEnabled(object.isEnabled());
            choiceBtnLst.add(objectButton);
            choicesPanel.add(objectButton);
        }
        if (choices.size() > 20) {
            JScrollPane scrollPane = new JScrollPane(choicesPanel,
                                                     JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                                                     JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            choiceDialog.add(scrollPane, "newline 20");
        } else {
            choicesPanel.setOpaque(false);
            choiceDialog.add(choicesPanel, "newline 20");
        }

        if (cancelText != null) {
            choiceDialog.cancelButton.setText(cancelText);
            choiceDialog.add(choiceDialog.cancelButton, "newline 20, tag cancel");
        }

        choiceDialog.setSize(choiceDialog.getPreferredSize());

        return choiceDialog;
    }


    
    public static FreeColDialog<Boolean> createConfirmDialog(String text, String okText, String cancelText) {
        return createConfirmDialog(new String[] {text}, null, okText, cancelText);
    }

    public static FreeColDialog<Boolean> createConfirmDialog(String[] texts, ImageIcon[] icons,
                                                             String okText, String cancelText) {
        
        final FreeColDialog<Boolean> confirmDialog =
            new FreeColDialog<Boolean>(FreeCol.getFreeColClient().getCanvas());

        confirmDialog.setLayout(new MigLayout("wrap 2", "[][fill]", ""));

        confirmDialog.okButton.setText(okText);
        confirmDialog.okButton.addActionListener(new ActionListener() {
                public void actionPerformed( ActionEvent event ) {
                    confirmDialog.setResponse(Boolean.TRUE);
                }
            });

        confirmDialog.cancelButton.setText(cancelText);
        confirmDialog.cancelButton.removeActionListener(confirmDialog);
        confirmDialog.cancelButton.addActionListener(new ActionListener() {
                public void actionPerformed( ActionEvent event ) {
                    confirmDialog.setResponse(Boolean.FALSE);
                }
            });

        for (int i = 0; i < texts.length; i++) {
            if (icons != null && icons[i] != null) {
                confirmDialog.add(new JLabel(icons[i]));
                confirmDialog.add(getDefaultTextArea(texts[i]));
            } else {
                confirmDialog.add(getDefaultTextArea(texts[i]), "skip");
            }
        }

        confirmDialog.add(confirmDialog.okButton, "newline 20, span, split 2, tag ok");
        confirmDialog.add(confirmDialog.cancelButton, "tag cancel");

        return confirmDialog;
    }

    
    public static FreeColDialog<String> createInputDialog(String text, String defaultValue,
                                                          String okText, String cancelText) {

        final JTextField input = new JTextField(defaultValue);
        
        final FreeColDialog<String> inputDialog =
            new FreeColDialog<String>(FreeCol.getFreeColClient().getCanvas())  {
            public void requestFocus() {
                input.requestFocus();
            }
        };

        inputDialog.setLayout(new MigLayout("wrap 1, gapy 20", "", ""));

        inputDialog.okButton.setText(okText);
        inputDialog.okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                inputDialog.setResponse(input.getText());
            }
        });

        input.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                inputDialog.setResponse(input.getText());
            }
        });
        
        input.selectAll();

        inputDialog.add(getDefaultTextArea(text));
        inputDialog.add(input, "width 180:, growx");

        if (cancelText == null) {
            inputDialog.add(inputDialog.okButton, "tag ok");
            inputDialog.setCancelComponent(inputDialog.okButton);
        } else {
            inputDialog.cancelButton.setText(cancelText);
            inputDialog.add(inputDialog.okButton, "split 2, tag ok");
            inputDialog.add(inputDialog.cancelButton, "tag cancel");
        }

        inputDialog.setSize(inputDialog.getPreferredSize());

        return inputDialog;
    }


    public static FreeColDialog<Dimension> createMapSizeDialog() {

        final int defaultSize = FreeCol.getSpecification().getRangeOption("model.option.mapSize")
            .getValue();
        final int defaultHeight = MapGeneratorOptions.getHeight(defaultSize);
        final int defaultWidth = MapGeneratorOptions.getWidth(defaultSize);
        final int COLUMNS = 5;
        
        final String widthText = Messages.message("width");
        final String heightText = Messages.message("height");
        
        final JTextField inputWidth = new JTextField(Integer.toString(defaultWidth), COLUMNS);
        final JTextField inputHeight = new JTextField(Integer.toString(defaultHeight), COLUMNS);

        final FreeColDialog<Dimension> mapSizeDialog =
            new FreeColDialog<Dimension>(FreeCol.getFreeColClient().getCanvas());

        mapSizeDialog.setLayout(new MigLayout("wrap 2", "", ""));

        mapSizeDialog.okButton.setText(Messages.message("ok"));
        mapSizeDialog.okButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent event) {
                    try {
                        int width = Integer.parseInt(inputWidth.getText());
                        int height = Integer.parseInt(inputHeight.getText());
                        if (width <= 0 || height <= 0) {
                            throw new NumberFormatException();
                        }
                        mapSizeDialog.setResponse(new Dimension(width, height));
                    } catch (NumberFormatException nfe) {
                        FreeCol.getFreeColClient().getCanvas().errorMessage("integerAboveZero");
                    }
                }
            });

        
        JLabel widthLabel = new JLabel(widthText);
        widthLabel.setLabelFor(inputWidth);
        JLabel heightLabel = new JLabel(heightText);
        heightLabel.setLabelFor(inputHeight);

        mapSizeDialog.add(new JLabel(Messages.message("editor.mapSize")), "span, align center");
        mapSizeDialog.add(widthLabel, "newline 20");
        mapSizeDialog.add(inputWidth);
        mapSizeDialog.add(heightLabel);
        mapSizeDialog.add(inputHeight);

        mapSizeDialog.add(mapSizeDialog.okButton, "newline 20, span, split2, tag ok");
        mapSizeDialog.add(mapSizeDialog.cancelButton, "tag cancel");

        mapSizeDialog.setSize(mapSizeDialog.getPreferredSize());

        return mapSizeDialog;
    }

    
    public static FreeColDialog<File> createLoadDialog(File directory, FileFilter[] fileFilters) {
        final FreeColDialog<File> loadDialog =
            new FreeColDialog<File>(FreeCol.getFreeColClient().getCanvas());
        final JFileChooser fileChooser = new JFileChooser(directory);

        loadDialog.okButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent event) {
                    File selectedFile = fileChooser.getSelectedFile();
                    if (selectedFile != null) {
                        loadDialog.setResponse(selectedFile);
                    }
                }
            });

        if (fileFilters.length > 0) {
            for (FileFilter fileFilter : fileFilters) {
                fileChooser.addChoosableFileFilter(fileFilter);
            }
            fileChooser.setFileFilter(fileFilters[0]);
            fileChooser.setAcceptAllFileFilterUsed(false);
        }
        fileChooser.setDialogType(JFileChooser.OPEN_DIALOG);
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setFileHidingEnabled(false);
        fileChooser.setControlButtonsAreShown(false);
        loadDialog.setLayout(new MigLayout("fill", "", ""));
        loadDialog.add(fileChooser, "grow");
        loadDialog.add(loadDialog.okButton, "newline 20, split 2, tag ok");
        loadDialog.add(loadDialog.cancelButton, "tag cancel");
        loadDialog.setSize(480, 320);

        return loadDialog;
    }


    
    public static FreeColDialog<File> createSaveDialog(File directory, final String standardName,
                                                       FileFilter[] fileFilters, String defaultName) {
        final FreeColDialog<File> saveDialog =
            new FreeColDialog<File>(FreeCol.getFreeColClient().getCanvas());
        final JFileChooser fileChooser = new JFileChooser(directory);
        final File defaultFile = new File(defaultName);

        fileChooser.setDialogType(JFileChooser.SAVE_DIALOG);
        if (fileFilters.length > 0) {
            for (int i=0; i<fileFilters.length; i++) {
                fileChooser.addChoosableFileFilter(fileFilters[i]);
            }
            fileChooser.setFileFilter(fileFilters[0]);
            fileChooser.setAcceptAllFileFilterUsed(false);
        }
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                String actionCommand = event.getActionCommand();
                if (actionCommand.equals(JFileChooser.APPROVE_SELECTION)) {
                    File file = fileChooser.getSelectedFile();
                    if (standardName != null && !file.getName().endsWith(standardName)) {
                        file = new File(file.getAbsolutePath() + standardName);
                    }
                    saveDialog.setResponse(file);
                }
                else if (actionCommand.equals(JFileChooser.CANCEL_SELECTION)) {
                    saveDialog.setResponse(null);
                }
            }
        });
        fileChooser.setFileHidingEnabled(false);
        fileChooser.setSelectedFile(defaultFile);
        saveDialog.setLayout(new BorderLayout());
        saveDialog.add(fileChooser);
        saveDialog.setSize(480, 320);

        return saveDialog;
    }


    
    public static FileFilter getFSGFileFilter() {

        return new FreeColFileFilter( ".fsg", "filter.savedGames" );
    }
    

    
    public static FileFilter getFGOFileFilter() {

        return new FreeColFileFilter( ".fgo", "filter.gameOptions" );
    }
    
    
    
    public static FileFilter getGameOptionsFileFilter() {

        return new FreeColFileFilter( ".fgo", ".fsg", "filter.gameOptionsAndSavedGames" );
    }


    static final class FreeColFileFilter extends FileFilter {

        private final String  extension1;
        private final String  extension2;
        private final String  description;

        FreeColFileFilter( String  extension1,
                           String  extension2,
                           String  descriptionMessage ) {

            this.extension1 = extension1;
            this.extension2 = extension2;
            description = Messages.message(descriptionMessage);
        }

        FreeColFileFilter( String  extension, String  descriptionMessage ) {

            this.extension1 = extension;
            this.extension2 = "....";
            description = Messages.message(descriptionMessage);
        }

        public boolean accept(File f) {

            return f.isDirectory() || f.getName().endsWith(extension1)
                || f.getName().endsWith(extension2);
        }

        public String getDescription() {

            return description;
        }
    }

    
    public void initialize() {}

    
    public void actionPerformed(ActionEvent event) {
        String command = event.getActionCommand();
        if (CANCEL.equals(command)) {
            setResponse(null);
        } else {
            super.actionPerformed(event);
        }
    }

}
