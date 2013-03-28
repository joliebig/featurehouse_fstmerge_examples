package net.sourceforge.squirrel_sql.fw.datasetviewer;

 import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.BinaryDisplayConverter;
import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.CellComponentFactory;
import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.RestorableJTextArea;
import net.sourceforge.squirrel_sql.fw.gui.TextPopupMenu;
import net.sourceforge.squirrel_sql.fw.gui.action.BaseAction;
 import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.Utilities;


public class PopupEditableIOPanel extends JPanel implements ActionListener {

    private static final long serialVersionUID = 1L;

    private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(PopupEditableIOPanel.class);

	
	private final JTextArea _ta;

	
	private final JScrollPane scrollPane;

	
	transient private final ColumnDisplayDefinition _colDef;

	transient private MouseAdapter _lis;

	private final TextPopupMenu _popupMenu;

	
	private JTextField fileNameField;

	
	private JComboBox externalCommandCombo;

	
	private Object originalValue;

	
	
	private JComboBox radixList = null;
	private String previousRadixListItem = null;
	
	private JCheckBox showAscii = null;
	private boolean previousShowAscii;

	class BinaryOptionActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {

			
			int base = 16;	
			if (previousRadixListItem.equals("Decimal")) base = 10;
			else if (previousRadixListItem.equals("Octal")) base = 8;
			else if (previousRadixListItem.equals("Binary")) base = 2;

			Byte[] bytes = BinaryDisplayConverter.convertToBytes(_ta.getText(),
				base, previousShowAscii);

			
			base = 16;	
			if (radixList.getSelectedItem().equals("Decimal")) base = 10;
			else if (radixList.getSelectedItem().equals("Octal")) base = 8;
			else if (radixList.getSelectedItem().equals("Binary")) base = 2;

			((RestorableJTextArea)_ta).updateText(
				BinaryDisplayConverter.convertToString(bytes,
				base, showAscii.isSelected()));

			previousRadixListItem = (String)radixList.getSelectedItem();
			previousShowAscii = showAscii.isSelected();

			return;
		}
	}
	transient private BinaryOptionActionListener optionActionListener =
		new BinaryOptionActionListener();

	
	
	private final String TEMP_FILE_FLAG = "<temp file>";

	
	
	private final String FILE_REPLACE_FLAG = "%f";

	
	public PopupEditableIOPanel(ColumnDisplayDefinition colDef,
										 Object value, boolean isEditable) {

		originalValue = value;	

		_popupMenu = new TextPopupMenu();

		_colDef = colDef;
		_ta = CellComponentFactory.getJTextArea(colDef, value);

		if (isEditable) {
			_ta.setEditable(true);
			_ta.setBackground(Color.yellow);	
		}
		else {
			_ta.setEditable(false);
		}


		_ta.setLineWrap(true);
		_ta.setWrapStyleWord(true);

		setLayout(new BorderLayout());

		
		JPanel displayPanel = new JPanel();
		displayPanel.setLayout(new BorderLayout());
		scrollPane = new JScrollPane(_ta);
		

		displayPanel.add(scrollPane, BorderLayout.CENTER);
		if (CellComponentFactory.useBinaryEditingPanel(colDef)) {
			

			String[] radixListData = { "Hex", "Decimal", "Octal", "Binary" };
			radixList = new JComboBox(radixListData);
			radixList.addActionListener(optionActionListener);
			previousRadixListItem = "Hex";

			showAscii = new JCheckBox();
			previousShowAscii = false;
			showAscii.addActionListener(optionActionListener);

			JPanel displayControlsPanel = new JPanel();
			

			
			displayControlsPanel.add(new JLabel(s_stringMgr.getString("popupeditableIoPanel.numberBase")));
			displayControlsPanel.add(radixList);
			displayControlsPanel.add(new JLabel("    "));	
			displayControlsPanel.add(showAscii);
			
			displayControlsPanel.add(new JLabel(s_stringMgr.getString("popupeditableIoPanel.showAscii")));
			displayPanel.add(displayControlsPanel, BorderLayout.SOUTH);
		}
		add(displayPanel, BorderLayout.CENTER);

		
		
		if (CellComponentFactory.canDoFileIO(colDef)) {
			
			add(exportImportPanel(isEditable), BorderLayout.SOUTH);
		}

		_popupMenu.add(new LineWrapAction());
		_popupMenu.add(new WordWrapAction());
		_popupMenu.add(new XMLReformatAction());
		_popupMenu.setTextComponent(_ta);
	}

	
	private JPanel exportImportPanel(boolean isEditable) {
		JPanel eiPanel = new JPanel();
		eiPanel.setLayout(new GridBagLayout());

		final GridBagConstraints gbc = new GridBagConstraints();

		gbc.insets = new Insets(4,4,4,4);
		gbc.gridx = 0;
		gbc.gridy = 0;

		
		
		eiPanel.add(new JLabel(s_stringMgr.getString("popupeditableIoPanel.useFile")), gbc);


		fileNameField = new JTextField(TEMP_FILE_FLAG, 19);
		gbc.gridx++;
		eiPanel.add(fileNameField, gbc);


		
		
		JButton browseButton = new JButton(s_stringMgr.getString("popupeditableIoPanel.browse"));
		browseButton.setActionCommand("browse");
		browseButton.addActionListener(this);


		gbc.gridx++;
		eiPanel.add(browseButton, gbc);


		
		JButton exportButton = new JButton(s_stringMgr.getString("popupeditableIoPanel.export44"));
		exportButton.setActionCommand("export");
		exportButton.addActionListener(this);

		gbc.gridx++;
		eiPanel.add(exportButton, gbc);

		
		
		if ( isEditable == false)
			return eiPanel;

		
		
		JButton importButton = new JButton(s_stringMgr.getString("popupeditableIoPanel.import44"));
		importButton.setActionCommand("import");
		importButton.addActionListener(this);

		gbc.gridx++;
		eiPanel.add(importButton, gbc);

		
		gbc.gridy++;
		gbc.gridx = 0;
		
		eiPanel.add(new JLabel(s_stringMgr.getString("popupeditableIoPanel.withCommand")), gbc);

		
		gbc.gridx++;
		externalCommandCombo = new JComboBox(
			CellImportExportInfoSaver.getInstance().getCmdList());
		externalCommandCombo.setSelectedIndex(-1);	
		externalCommandCombo.setEditable(true);

		
		externalCommandCombo.setPreferredSize(fileNameField.getPreferredSize());

		eiPanel.add(externalCommandCombo, gbc);

		
		
		JButton externalCommandButton = new JButton(s_stringMgr.getString("popupeditableIoPanel.execute34"));
		externalCommandButton.setActionCommand("execute");
		externalCommandButton.addActionListener(this);

		gbc.gridx++;
		eiPanel.add(externalCommandButton, gbc);

		
		
		JButton applyButton = new JButton(s_stringMgr.getString("popupeditableIoPanel.applyFile"));
		applyButton.setActionCommand("apply");
		applyButton.addActionListener(this);

		gbc.gridx++;
		gbc.gridwidth = 2;
		eiPanel.add(applyButton, gbc);
		gbc.gridwidth = 1;	

		
		gbc.gridy++;
		gbc.gridx = 0;
		gbc.gridwidth = GridBagConstraints.REMAINDER;


		
		eiPanel.add(new JLabel(s_stringMgr.getString("popupeditableIoPanel.replaceFile", FILE_REPLACE_FLAG)), gbc);

		
		
		CellImportExportInfo info =
			CellImportExportInfoSaver.getInstance().get(_colDef.getFullTableColumnName());
		if (info != null) {
			
			fileNameField.setText(info.getFileName());
			externalCommandCombo.getEditor().setItem(info.getCommand());
		}

		return eiPanel;
	}


	
	public Object getObject(StringBuffer messageBuffer) {
		String text = null;
		try {
			text = getTextAreaCannonicalForm();
		}
		catch (Exception e) {
			messageBuffer.append(
				"Failed to convert binary text; error was:\n"+e.getMessage());
			return null;
		}
		return CellComponentFactory.validateAndConvertInPopup(_colDef,
					originalValue, text, messageBuffer);
	}

	
	public void requestFocus() {
		_ta.requestFocus();
	}

	
	public void actionPerformed(ActionEvent e) {

		
		File file;

		if (e.getActionCommand().equals("browse")) {
			JFileChooser chooser = new JFileChooser();
			String filename = fileNameField.getText();
            if (filename != null && !"".equals(filename)) {
                File f = new File(filename);
                String path = f.getAbsolutePath();
                if (path != null && !"".equals(path)) {
                    chooser.setCurrentDirectory(new File(path));
                }
            }
			int returnVal = chooser.showOpenDialog(this);
			if(returnVal == JFileChooser.APPROVE_OPTION) {
				
				
				try {
					fileNameField.setText(chooser.getSelectedFile().getCanonicalPath());
				}
				catch (Exception ex) {
					
					
					JOptionPane.showMessageDialog(this,
						
						s_stringMgr.getString("popupeditableIoPanel.errorGettingPath"),
						
						s_stringMgr.getString("popupeditableIoPanel.fileChooserError"),JOptionPane.ERROR_MESSAGE);
				}
			}
		}

		else if (e.getActionCommand().equals("apply")) {
			
			
			if ( (fileNameField.getText() != null &&
					fileNameField.getText().equals(TEMP_FILE_FLAG)) &&
				(externalCommandCombo.getEditor().getItem() == null ||
					((String)externalCommandCombo.getEditor().getItem()).length() == 0)) {
				
				
				
				CellImportExportInfoSaver.remove(_colDef.getFullTableColumnName());
			}
			else {
				
				CellImportExportInfoSaver.getInstance().save(
					_colDef.getFullTableColumnName(),fileNameField.getText(),
					((String)externalCommandCombo.getEditor().getItem()));
			}

		}

		 else if (e.getActionCommand().equals("import")) {

			 

			 if (fileNameField.getText() == null ||
				 fileNameField.getText().equals(TEMP_FILE_FLAG)) {
				 
				 JOptionPane.showMessageDialog(this,
					 
						s_stringMgr.getString("popupeditableIoPanel.selectImportDataFile"),
					 
						s_stringMgr.getString("popupeditableIoPanel.noFile"),JOptionPane.ERROR_MESSAGE);
				return;	
			 }

			 
			if (fileNameField.getText() == null)
				fileNameField.setText("");	
			file = new File(fileNameField.getText());
			if ( ! file.exists() || ! file.isFile() || ! file.canRead()) {
				

				
				String msg = s_stringMgr.getString("popupeditableIoPanel.fileDoesNotExist", fileNameField.getText());

			   JOptionPane.showMessageDialog(this, msg,

						
						s_stringMgr.getString("popupeditableIoPanel.fileError"),JOptionPane.ERROR_MESSAGE);
				return;	
			}

			
			
			
			
			
			
			
			
			
			importData(file);

			
			
			CellImportExportInfoSaver.getInstance().save(
				_colDef.getFullTableColumnName(), fileNameField.getText(),
				((String)externalCommandCombo.getEditor().getItem()));

		 }

		else {

			

			String canonicalFilePathName = fileNameField.getText();

			
			
			
			
			if (fileNameField.getText() == null ||
				fileNameField.getText().equals("")) {

				JOptionPane.showMessageDialog(this,

					
					s_stringMgr.getString("popupeditableIoPanel.noExportFile"),
					
					s_stringMgr.getString("popupeditableIoPanel.exportError"),JOptionPane.ERROR_MESSAGE);
				return;
			}

			
			if (fileNameField.getText().equals(TEMP_FILE_FLAG)) {
				
				try {
					file = File.createTempFile("squirrel", ".tmp");

					
					canonicalFilePathName = file.getCanonicalPath();
				}
				catch (Exception ex) {
					JOptionPane.showMessageDialog(this,
						
						s_stringMgr.getString("popupeditableIoPanel.cannotCreateTempFile", ex.getMessage()),
						
						s_stringMgr.getString("popupeditableIoPanel.exportError2"),
						JOptionPane.ERROR_MESSAGE);
					return;
				}
			}
			else {
				
				file = new File(fileNameField.getText());

				try {
					canonicalFilePathName = file.getCanonicalPath();
				}
				catch (Exception ex) {
					
					
					
					JOptionPane.showMessageDialog(this,
						
						s_stringMgr.getString("popupeditableIoPanel.cannotAccessFile", fileNameField.getText()),

						
						s_stringMgr.getString("popupeditableIoPanel.exportError3"),JOptionPane.ERROR_MESSAGE);
						return;
				}

				
				if (file.exists()) {
					if ( ! file.isFile()) {
						JOptionPane.showMessageDialog(this,
							
							s_stringMgr.getString("popupeditableIoPanel.notANormalFile"),
							
							s_stringMgr.getString("popupeditableIoPanel.exportError4"),JOptionPane.ERROR_MESSAGE);
						return;
					}
					if ( ! file.canWrite()) {
						JOptionPane.showMessageDialog(this,
							
							s_stringMgr.getString("popupeditableIoPanel.notWriteable"),
							
							s_stringMgr.getString("popupeditableIoPanel.exportError5"),JOptionPane.ERROR_MESSAGE);
						return;
					}
					
					
					int option = JOptionPane.showConfirmDialog(this,
						
						s_stringMgr.getString("popupeditableIoPanel.fileOverwrite", canonicalFilePathName),
						
							s_stringMgr.getString("popupeditableIoPanel.overwriteWarning"), JOptionPane.YES_NO_OPTION);
					if (option != JOptionPane.YES_OPTION) {
						

						
						
						
						return;
					}
					
					
					
					
				}
				else {
					
					try {
						if ( ! file.createNewFile()) {
							JOptionPane.showMessageDialog(this,
								
								s_stringMgr.getString("popupeditableIoPanel.createFileError", canonicalFilePathName),
								
								s_stringMgr.getString("popupeditableIoPanel.exportError6"),JOptionPane.ERROR_MESSAGE);
							return;
						}
					}
					catch (Exception ex) {

						Object[] args = new Object[]{canonicalFilePathName, ex.getMessage()};
						JOptionPane.showMessageDialog(this,
							
							s_stringMgr.getString("popupeditableIoPanel.cannotOpenFile", args),
							
							s_stringMgr.getString("popupeditableIoPanel.exportError7"),JOptionPane.ERROR_MESSAGE);
						return;
					}
				}
			}

			
			
			

			
			
			
			
			
			FileOutputStream outStream;
			try {
				outStream = new FileOutputStream(file);
			}
			catch (Exception ex) {
				JOptionPane.showMessageDialog(this,
					
					s_stringMgr.getString("popupeditableIoPanel.cannotFindFile", canonicalFilePathName),
					
					s_stringMgr.getString("popupeditableIoPanel.exportError8"),JOptionPane.ERROR_MESSAGE);
				return;
			}

            String extCmdComboItemStr = null;
            if (externalCommandCombo != null
                    && externalCommandCombo.getEditor() != null) 
            {
                extCmdComboItemStr = 
                    (String)externalCommandCombo.getEditor().getItem();
            }
            
			
			
			if ( ! TEMP_FILE_FLAG.equals(fileNameField.getText()) 
                    || (extCmdComboItemStr != null 
                            && extCmdComboItemStr.length() > 0)) {

				
				
				
				String commandString = extCmdComboItemStr;

				CellImportExportInfoSaver.getInstance().save(
					_colDef.getFullTableColumnName(), fileNameField.getText(),
					commandString);
			}

			if (e.getActionCommand().equals("export")) {

				

				if (exportData(outStream, canonicalFilePathName) == true) {

					
					
					
					
					
					
					
					JOptionPane.showMessageDialog(this,
						
						s_stringMgr.getString("popupeditableIoPanel.exportedToFile", canonicalFilePathName),
						
						s_stringMgr.getString("popupeditableIoPanel.exportSuccess"),JOptionPane.INFORMATION_MESSAGE);
				}
			}

			else if (e.getActionCommand().equals("execute")) {

				

				if (((String)externalCommandCombo.getEditor().getItem()) == null ||
					((String)externalCommandCombo.getEditor().getItem()).length() == 0) {
					
					JOptionPane.showMessageDialog(this,
						
						s_stringMgr.getString("popupeditableIoPanel.cannotExec"),
						
						s_stringMgr.getString("popupeditableIoPanel.executeError"),JOptionPane.ERROR_MESSAGE);
					return;
				}

				
				String command = ((String)externalCommandCombo.getEditor().getItem());

				int index;
				while ((index = command.indexOf(FILE_REPLACE_FLAG)) >= 0) {
					command = command.substring(0, index) +
						canonicalFilePathName +
						command.substring(index + FILE_REPLACE_FLAG.length());
				}

				
				if (exportData(outStream, canonicalFilePathName) == false) {
					
					
					
					return;
				}

				int commandResult;
				BufferedReader err = null;
				try {
					
					Process cmdProcess = Runtime.getRuntime().exec(command);

					
					commandResult = cmdProcess.waitFor();

					
					
					
					
					
					
					
					err = new BufferedReader(
						new InputStreamReader(cmdProcess.getErrorStream()));

					String errMsg = err.readLine();
					if (errMsg != null)
						throw new IOException(
							"text on error stream from command starting with:\n"+errMsg);
				}
				catch (Exception ex) {

					Object[] args = new Object[]{command, ex.getMessage()};
					JOptionPane.showMessageDialog(this,
						
						s_stringMgr.getString("popupeditableIoPanel.errWhileExecutin", args),
						
						s_stringMgr.getString("popupeditableIoPanel.executeError2"),JOptionPane.ERROR_MESSAGE);
					return;
				} finally {
				    Utilities.closeReader(err);
				}

				
				if (commandResult != 0) {
					
					
					int option = JOptionPane.showConfirmDialog(this,
							
							s_stringMgr.getString("popupeditableIoPanel.commandReturnNot0", Integer.valueOf(commandResult)),

							
							s_stringMgr.getString("popupeditableIoPanel.importWarning"), JOptionPane.YES_NO_OPTION);
					if (option != JOptionPane.YES_OPTION) {
						return;
					}
				}

				
				importData(file);

				
				
				
				
				file.delete();
			}
		} 
	}

	
	private void importData(File file) {
		
		
		FileInputStream inStream;
		String canonicalFilePathName = fileNameField.getText();
		try {
			inStream = new FileInputStream(file);

			
			
			
			
			
			
			
			
			
			canonicalFilePathName = file.getCanonicalPath();
		}
		catch (Exception ex) {

			Object[] args = new Object[]{canonicalFilePathName, ex.getMessage()};

			JOptionPane.showMessageDialog(this,
				
				s_stringMgr.getString("popupeditableIoPanel.fileOpenError", args),
				
				s_stringMgr.getString("popupeditableIoPanel.fileOpenErrorHeader"),JOptionPane.ERROR_MESSAGE);
			return;
		}

		
		
		
		try {

			
			
			
			String replacementText =
				CellComponentFactory.importObject(_colDef, inStream);

			
			
			
			

			
			
			if (radixList != null &&
				! (radixList.getSelectedItem().equals("Hex") &&
					showAscii.isSelected() == false) ) {
				
				int base = 16;	
				if (radixList.getSelectedItem().equals("Decimal")) base = 10;
				else if (radixList.getSelectedItem().equals("Octal")) base = 8;
				else if (radixList.getSelectedItem().equals("Binary")) base = 2;

				Byte[] bytes = BinaryDisplayConverter.convertToBytes(replacementText,
					16, false);

				
				replacementText = BinaryDisplayConverter.convertToString(bytes,
					base, showAscii.isSelected());
			}

			((RestorableJTextArea)_ta).updateText(replacementText);
		}
		catch (Exception ex) {

			Object[] args = new Object[]{canonicalFilePathName, ex.getMessage()};
			JOptionPane.showMessageDialog(this,
				
				s_stringMgr.getString("popupeditableIoPanel.errorReadingFile", args),
				
				s_stringMgr.getString("popupeditableIoPanel.importError2"),JOptionPane.ERROR_MESSAGE);
			return;
		}

		
		try {
			inStream.close();
		}
		catch (Exception ex) {
			
			
		}

		
	}


	
	private boolean exportData(FileOutputStream outStream,
	                           String canonicalFilePathName){

		
		
		
		try {

			
			
			
			
			
			CellComponentFactory.exportObject(_colDef, outStream,
				getTextAreaCannonicalForm());

		}
		catch (Exception ex) {

			Object[] args = new Object[]{canonicalFilePathName, ex.getMessage()};

			JOptionPane.showMessageDialog(this,
				
				s_stringMgr.getString("popupeditableIoPanel.errorWritingFile", args),
				
				s_stringMgr.getString("popupeditableIoPanel.exportError100"),JOptionPane.ERROR_MESSAGE);
			return false;
		}

		return true;
	}

	
	public void addNotify()
	{
		super.addNotify();
		if (_lis == null)
		{
			_lis = new MouseAdapter()
			{
				public void mousePressed(MouseEvent evt)
				{
					if (evt.isPopupTrigger())
					{
						_popupMenu.show(evt);
					}
				}
				public void mouseReleased(MouseEvent evt)
				{
						if (evt.isPopupTrigger())
					{
						_popupMenu.show(evt);
					}
				}
			};
			_ta.addMouseListener(_lis);
		}
	}

	public void removeNotify()
	{
		super.removeNotify();
		if (_lis != null)
		{
			_ta.removeMouseListener(_lis);
			_lis = null;
		}
	}

	private class LineWrapAction extends BaseAction
	{
		LineWrapAction()
		{
			
			super(s_stringMgr.getString("popupEditableIoPanel.wrapLines"));
		}

		public void actionPerformed(ActionEvent evt)
		{
			if (_ta != null)
			{
				_ta.setLineWrap(!_ta.getLineWrap());
			}
		}
	}

	private class WordWrapAction extends BaseAction
	{
        private static final long serialVersionUID = 1L;

        WordWrapAction()
		{
			
			super(s_stringMgr.getString("popupEditableIoPanel.wrapWord"));
		}

		public void actionPerformed(ActionEvent evt)
		{
			if (_ta != null)
			{
				_ta.setWrapStyleWord(!_ta.getWrapStyleWord());
			}
		}
	}

	private class XMLReformatAction extends BaseAction
	{
        private static final long serialVersionUID = 1L;

        XMLReformatAction()
		{
			
			super(s_stringMgr.getString("popupEditableIoPanel.reformatXml"));
		}

		public void actionPerformed(ActionEvent evt)
		{
			if (_ta != null)
			{
				_ta.setText(XmlRefomatter.reformatXml(_ta.getText()));
			}
		}
	}

	
	private String getTextAreaCannonicalForm() {
		
		if (_ta.getText() == null ||
			_ta.getText().equals("<null>") ||
			_ta.getText().length() == 0)
			return _ta.getText();

		
		
		if (radixList == null ||
			(radixList.getSelectedItem().equals("Hex") && ! showAscii.isSelected()) ) {
			
			return _ta.getText();
		}

		
		int base = 16;	
		if (radixList.getSelectedItem().equals("Decimal")) base = 10;
		else if (radixList.getSelectedItem().equals("Octal")) base = 8;
		else if (radixList.getSelectedItem().equals("Binary")) base = 2;

		
		Byte[] bytes = BinaryDisplayConverter.convertToBytes(_ta.getText(),
			base, showAscii.isSelected());

		
		return BinaryDisplayConverter.convertToString(bytes, 16, false);
	}
}
