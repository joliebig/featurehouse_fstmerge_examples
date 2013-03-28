
package net.sourceforge.squirrel_sql.plugins.oracle.sqlloader.ui;

import static javax.swing.GroupLayout.DEFAULT_SIZE;
import static javax.swing.GroupLayout.PREFERRED_SIZE;
import static javax.swing.GroupLayout.Alignment.BASELINE;
import static javax.swing.GroupLayout.Alignment.LEADING;
import static javax.swing.GroupLayout.Alignment.TRAILING;
import static javax.swing.LayoutStyle.ComponentPlacement.RELATED;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.EventListener;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JRootPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.DialogWidget;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.plugins.oracle.sqlloader.control.GenerateControlFileActionListener;


public class ControlFileGenerationFrame extends DialogWidget implements
		EventListener {

	final class CloseDialogActionListener implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			dispose();
		}
	}

	private static final int SMALL_TEXTFIELDS_WIDTH = 23;

	private static final int CONTROL_FILE_TEXTFIELD_WIDTH = 122;

	private static final String DEFAULT_FIELD_SEPARATOR = ",";

	private static final String DEFAULT_STRING_DELIMITATOR = "\"";

	private ISession session;

	
	public ControlFileGenerationFrame(String title, ISession session) {
		super(title, true, true, true, true, session.getApplication());
		this.session = session;
		initComponents();
	}

	
	private void initComponents() {
		
		CloseDialogActionListener closeDialogActionListener = new CloseDialogActionListener();

		
        JPanel loadModePanel = new JPanel();
        ButtonGroup loadModeButtonGroup = new ButtonGroup();
        final JRadioButton appendRadioButton = new JRadioButton("Append");
        JRadioButton replaceRadioButton = new JRadioButton("Replace");
        loadModePanel.setBorder(BorderFactory.createTitledBorder("Load mode"));
        loadModeButtonGroup.add(appendRadioButton);
        loadModeButtonGroup.add(replaceRadioButton);
        replaceRadioButton.setSelected(true);
        GroupLayout loadModePanelLayout = new GroupLayout(loadModePanel);
        loadModePanel.setLayout(loadModePanelLayout);
        loadModePanelLayout.setHorizontalGroup(
            loadModePanelLayout.createParallelGroup(LEADING)
            .addGroup(loadModePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(loadModePanelLayout.createParallelGroup(LEADING)
                    .addComponent(appendRadioButton)
                    .addComponent(replaceRadioButton))
                .addContainerGap())
        );
        loadModePanelLayout.setVerticalGroup(
            loadModePanelLayout.createParallelGroup(LEADING)
            .addGroup(loadModePanelLayout.createSequentialGroup()
                .addComponent(replaceRadioButton)
                .addPreferredGap(RELATED)
                .addComponent(appendRadioButton))
        );
        
        
        final JTextField fieldSeparatorTextfield = new JTextField();
        JLabel fieldSeparatorLabel = new JLabel("Field separator: ");
        fieldSeparatorTextfield.setText(DEFAULT_FIELD_SEPARATOR);

        
        final JTextField stringDelimitatorTextfield = new JTextField();
        JLabel stringDelimitatorLabel = new JLabel("String delimitator: ");
        stringDelimitatorTextfield.setText(DEFAULT_STRING_DELIMITATOR);

        
        final JFileChooser controlFileChooser = new JFileChooser();
        final JPanel controlFilePanel = new JPanel();
        final JTextField controlFileTextfield = new JTextField();
        JButton controlFileButton = new JButton("Choose...");
        controlFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		controlFileButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (controlFileChooser.showOpenDialog(controlFilePanel)==JFileChooser.APPROVE_OPTION) {
					controlFileTextfield.setText(controlFileChooser.getSelectedFile().getAbsolutePath());
				}
			}
		});
        controlFilePanel.setBorder(BorderFactory.createTitledBorder("Directory for control files"));
        GroupLayout controlFilePanelLayout = new GroupLayout(controlFilePanel);
        controlFilePanel.setLayout(controlFilePanelLayout);
        controlFilePanelLayout.setHorizontalGroup(
            controlFilePanelLayout.createParallelGroup(LEADING)
            .addGroup(controlFilePanelLayout.createSequentialGroup()
                .addComponent(controlFileTextfield, PREFERRED_SIZE, CONTROL_FILE_TEXTFIELD_WIDTH, PREFERRED_SIZE)
                .addPreferredGap(RELATED)
                .addComponent(controlFileButton))
        );
        controlFilePanelLayout.setVerticalGroup(
            controlFilePanelLayout.createParallelGroup(LEADING)
            .addGroup(controlFilePanelLayout.createParallelGroup(BASELINE)
                .addComponent(controlFileTextfield, PREFERRED_SIZE, DEFAULT_SIZE, PREFERRED_SIZE)
                .addComponent(controlFileButton))
        );

        
        JPanel buttonPanel = new JPanel();
        JButton generateButton = new JButton("Generate");
        JButton closeButton = new JButton("Close");
        generateButton.addActionListener(new GenerateControlFileActionListener(stringDelimitatorTextfield, fieldSeparatorTextfield, appendRadioButton, controlFileTextfield, session));
		closeButton.addActionListener(closeDialogActionListener);

		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		GroupLayout buttonPanelLayout = new GroupLayout(buttonPanel);
        buttonPanel.setLayout(buttonPanelLayout);
        buttonPanelLayout.setHorizontalGroup(
            buttonPanelLayout.createParallelGroup(LEADING)
            .addGroup(buttonPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(generateButton)
                .addPreferredGap(RELATED)
                .addComponent(closeButton)
                .addContainerGap())
        );
        buttonPanelLayout.setVerticalGroup(
            buttonPanelLayout.createParallelGroup(LEADING)
            .addGroup(TRAILING, buttonPanelLayout.createSequentialGroup()
                .addContainerGap(DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(buttonPanelLayout.createParallelGroup(BASELINE)
                    .addComponent(closeButton)
                    .addComponent(generateButton)))
        );

        
        final Container contentPane = getContentPane();
		GroupLayout layout = new GroupLayout(contentPane);
        contentPane.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(LEADING)
                    .addComponent(loadModePanel, PREFERRED_SIZE, DEFAULT_SIZE, PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(LEADING)
                            .addComponent(fieldSeparatorLabel)
                            .addComponent(stringDelimitatorLabel))
                        .addPreferredGap(RELATED)
                        .addGroup(layout.createParallelGroup(LEADING)
                            .addComponent(stringDelimitatorTextfield, PREFERRED_SIZE, SMALL_TEXTFIELDS_WIDTH, PREFERRED_SIZE)
                            .addComponent(fieldSeparatorTextfield, PREFERRED_SIZE, SMALL_TEXTFIELDS_WIDTH, PREFERRED_SIZE)))
                    .addComponent(controlFilePanel, PREFERRED_SIZE, DEFAULT_SIZE, PREFERRED_SIZE)
                    .addComponent(buttonPanel, DEFAULT_SIZE, DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(loadModePanel, PREFERRED_SIZE, DEFAULT_SIZE, PREFERRED_SIZE)
                .addPreferredGap(RELATED)
                .addGroup(layout.createParallelGroup(BASELINE)
                    .addComponent(fieldSeparatorLabel)
                    .addComponent(fieldSeparatorTextfield, PREFERRED_SIZE, DEFAULT_SIZE, PREFERRED_SIZE))
                .addPreferredGap(RELATED)
                .addGroup(layout.createParallelGroup(BASELINE)
                    .addComponent(stringDelimitatorLabel)
                    .addComponent(stringDelimitatorTextfield, PREFERRED_SIZE, DEFAULT_SIZE, PREFERRED_SIZE))
                .addPreferredGap(RELATED)
                .addComponent(controlFilePanel, PREFERRED_SIZE, DEFAULT_SIZE, PREFERRED_SIZE)
                .addPreferredGap(RELATED)
                .addComponent(buttonPanel, PREFERRED_SIZE, DEFAULT_SIZE, PREFERRED_SIZE)
                .addContainerGap(DEFAULT_SIZE, Short.MAX_VALUE))
        );
        final JRootPane rootPane = getRootPane();
		
        rootPane.registerKeyboardAction(closeDialogActionListener, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);
        
        rootPane.setDefaultButton(generateButton);
        
        pack();
	}
}
