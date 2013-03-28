package net.sourceforge.squirrel_sql.plugins.exportconfig.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.gui.builders.DefaultFormBuilder;
import net.sourceforge.squirrel_sql.client.preferences.SquirrelPreferences;
import net.sourceforge.squirrel_sql.fw.util.FileExtensionFilter;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.fw.xml.XMLBeanWriter;
import net.sourceforge.squirrel_sql.plugins.exportconfig.ExportConfigPreferences;

import com.jgoodies.forms.builder.ButtonBarBuilder;
import com.jgoodies.forms.layout.FormLayout;

public class ExportPanelBuilder
{
	
	private final static ILogger s_log =
		LoggerController.createLogger(ExportPanelBuilder.class);

	
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(ExportPanelBuilder.class);

	
	private static File s_lastDir;

	
	private final ControlMediator _mediator = new ControlMediator();

	
	private JCheckBox _exportPrefsChk;

	
	private JTextField _exportPrefsText;

	
	private JButton _exportPrefsBtn;

	
	private JCheckBox _exportDriversChk;

	
	private JTextField _exportDriversText;

	
	private JButton _exportDriversBtn;

	
	private JCheckBox _exportAliasesChk;

	
	private JTextField _exportAliasesText;

	
	private JButton _exportAliasesBtn;

	
	private JCheckBox _includeUserNamesChk;

	
	private JCheckBox _includePasswordsChk;

     
    private JButton _exportBtn = null;
    
    
    private JButton _cancelBtn = null;
    
     
    private IApplication _app = null;
    
    
    private JPanel _panel = null;
    
	public ExportPanelBuilder(IApplication app)
	{
		super();
        _app = app;
	}

	public JPanel buildPanel(ExportConfigPreferences prefs)
	{
		initComponents(prefs);

		final FormLayout layout = new FormLayout(
				"12dlu, left:max(40dlu;pref), 3dlu, 150dlu:grow(1.0), 3dlu, "
			  + "right:max(40dlu;pref), 3dlu",
				"");
		final DefaultFormBuilder builder = new DefaultFormBuilder(layout);
		builder.setDefaultDialogBorder();
		builder.setLeadingColumnOffset(1);

		builder.appendSeparator(s_stringMgr.getString("ExportPanel.prefs"));
		builder.append(_exportPrefsChk);
		builder.append(_exportPrefsText);
		builder.append(_exportPrefsBtn);

		builder.nextLine();
		builder.appendSeparator(s_stringMgr.getString("ExportPanel.drivers"));
		builder.append(_exportDriversChk);
		builder.append(_exportDriversText);
		builder.append(_exportDriversBtn);

		builder.nextLine();
		builder.appendSeparator(s_stringMgr.getString("ExportPanel.aliases"));
		builder.append(_exportAliasesChk);
		builder.append(_exportAliasesText);
		builder.append(_exportAliasesBtn);

		builder.setLeadingColumnOffset(3);
		builder.nextLine();
		builder.append(_includeUserNamesChk);

		builder.nextLine();
		builder.append(_includePasswordsChk);
		builder.setLeadingColumnOffset(1);

		builder.nextLine();
		builder.appendSeparator();
		builder.append(createButtonBar(), 5);

		_panel = builder.getPanel();
        return _panel;
	}

	
	public void writeToPerferences(ExportConfigPreferences prefs)
	{
		if (prefs == null)
		{
			throw new IllegalArgumentException("ExportConfigPreferences == null");
		}

		prefs.setExportPreferences(_exportPrefsChk.isSelected());
		prefs.setExportDrivers(_exportDriversChk.isSelected());
		prefs.setExportAliases(_exportAliasesChk.isSelected());

		prefs.setPreferencesFileName(_exportPrefsText.getText());
		prefs.setDriversFileName(_exportDriversText.getText());
		prefs.setAliasesFileName(_exportAliasesText.getText());

		prefs.setIncludeUserNames(_includeUserNamesChk.isSelected());
		prefs.setIncludePasswords(_includePasswordsChk.isSelected());
	}

	private void updateControlStatus()
	{
		boolean isSelected = _exportPrefsChk.isSelected();
		_exportPrefsText.setEditable(isSelected);
		_exportPrefsBtn.setEnabled(isSelected);

		isSelected = _exportDriversChk.isSelected();
		_exportDriversText.setEditable(isSelected);
		_exportDriversBtn.setEnabled(isSelected);

		isSelected = _exportAliasesChk.isSelected();
		_exportAliasesText.setEditable(isSelected);
		_exportAliasesBtn.setEnabled(isSelected);
		_includeUserNamesChk.setEnabled(isSelected);
		_includePasswordsChk.setEnabled(isSelected);
	}

	private void initComponents(ExportConfigPreferences prefs)
	{
		final File here = new File(".");

		final String export = s_stringMgr.getString("ExportPanel.export");
        
		_exportPrefsChk = new JCheckBox(export);
		_exportDriversChk = new JCheckBox(export);
		_exportAliasesChk = new JCheckBox(export);
		
		_exportPrefsText = new JTextField();
		_exportDriversText = new JTextField();
		_exportAliasesText = new JTextField();

		final String btnTitle = s_stringMgr.getString("ExportPanel.browse"); 
		_exportPrefsBtn = new JButton(btnTitle);
		_exportDriversBtn = new JButton(btnTitle);
		_exportAliasesBtn = new JButton(btnTitle);
		
        final String cancel = s_stringMgr.getString("ExportPanel.cancel");
        _exportBtn = new JButton(export);
        _cancelBtn = new JButton(cancel);





		_includeUserNamesChk = new JCheckBox(s_stringMgr.getString("ExportPanel.includeusers"));
		_includePasswordsChk = new JCheckBox(s_stringMgr.getString("ExportPanel.includepasswords"));

		_exportPrefsChk.addActionListener(_mediator);
		_exportDriversChk.addActionListener(_mediator);
		_exportAliasesChk.addActionListener(_mediator);

		_exportPrefsBtn.addActionListener(new BrowseButtonListener(_exportPrefsText));
		_exportDriversBtn.addActionListener(new BrowseButtonListener( _exportDriversText));
		_exportAliasesBtn.addActionListener(new BrowseButtonListener(_exportAliasesText));
		_exportBtn.addActionListener(new ExportButtonListener());
        _cancelBtn.addActionListener(new CancelButtonListener());
        
		_exportPrefsChk.setSelected(prefs.getExportPreferences());
		_exportDriversChk.setSelected(prefs.getExportDrivers());
		_exportAliasesChk.setSelected(prefs.getExportAliases());

		_includeUserNamesChk.setSelected(prefs.getIncludeUserNames());
		_includePasswordsChk.setSelected(prefs.getIncludePasswords());

		_exportPrefsText.setText(prefs.getPreferencesFileName());
		_exportDriversText.setText(prefs.getDriversFileName());
		_exportAliasesText.setText(prefs.getAliasesFileName());
        
		updateControlStatus();
	}

	private JPanel createButtonBar()
	{
		ButtonBarBuilder builder = new ButtonBarBuilder();
		builder.addGlue();
		builder.addGridded(_exportBtn);                      
		builder.addRelatedGap();                   
		builder.addGridded(_cancelBtn);

		return builder.getPanel();  
	}







	private String getFileName(File file)
	{
		try
		{
			return file.getCanonicalPath();
		}
		catch (IOException ex)
		{
			
			s_log.error(s_stringMgr.getString("exportconfig.errorReslovingFileName"), ex);
		}
		return file.getAbsolutePath();
	}

    
    private boolean confirmOverwrite(File f) throws CancelledException {
        
        String title = 
            s_stringMgr.getString("ExportPanel.confirmoverwritetitle");
        String message = 
            s_stringMgr.getString("ExportPanel.confirmoverwritemsg", 
                                  f.getAbsolutePath());
        
        int option = 
        JOptionPane.showConfirmDialog(SwingUtilities.getRoot(_panel), 
                                      message,
                                      title,
                                      JOptionPane.YES_NO_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            return true;
        } 
        if (option == JOptionPane.CANCEL_OPTION) {
            throw new CancelledException();
        }
        return false;
    }
    
	
	private final class ControlMediator implements ActionListener
	{
		public void actionPerformed(ActionEvent evt)
		{
			updateControlStatus();
		}
	}

	private final class BrowseButtonListener implements ActionListener
	{
		private final JTextField _tf;

		BrowseButtonListener(JTextField tf)
		{
			super();
			_tf = tf;
		}

		public void actionPerformed(ActionEvent evt)
		{
			final JFileChooser chooser = new JFileChooser();
			chooser.addChoosableFileFilter(new FileExtensionFilter("XML files",
													new String[] { ".xml" }));
			chooser.setSelectedFile(new File(_tf.getText()));


			if (chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION)
			{
				_tf.setText(getFileName(chooser.getSelectedFile()));
			}
		}
	}
    
    private final class ExportButtonListener implements ActionListener {
        
        
        public void actionPerformed(ActionEvent evt) {
            boolean succeeded = false;
            boolean cancelled = false;
            Exception ex = null;
            try {
                if (_exportPrefsChk.getModel().isSelected()) {
                    File f = new File(_exportPrefsText.getText());
                    if (!f.exists() || confirmOverwrite(f)) {
                        SquirrelPreferences prefs = _app.getSquirrelPreferences();
                        new XMLBeanWriter(prefs).save(f);
                    }
                }
                if (_exportDriversChk.getModel().isSelected()) {
                    File f = new File(_exportDriversText.getText()); 
                    if (!f.exists() || confirmOverwrite(f)) {
                        _app.getDataCache().saveDrivers(f);
                    }
                }
                if (_exportAliasesChk.getModel().isSelected()) {
                    File f = new File(_exportAliasesText.getText()); 
                    if (!f.exists() || confirmOverwrite(f)) {
                        _app.getDataCache().saveAliases(f);
                    }
                }
                succeeded = true;
            } catch (CancelledException e) { 
                cancelled = true;
                ex = e;
            } catch (Exception e) {
                ex = e;              
            }
            String outcomeMessage = "";
            String title = "";
            int optionType = 0;
            if (cancelled) {
                outcomeMessage = 
                    s_stringMgr.getString("ExportPanel.cancelledmessage");
                title = s_stringMgr.getString("ExportPanel.cancelledtitle");
                optionType = JOptionPane.INFORMATION_MESSAGE;
            }
            if (succeeded) {
                outcomeMessage = 
                    s_stringMgr.getString("ExportPanel.successmessage");
                title = s_stringMgr.getString("ExportPanel.successtitle");
                optionType = JOptionPane.INFORMATION_MESSAGE;
            }
            if (!succeeded && !cancelled) {
                outcomeMessage = 
                    s_stringMgr.getString("ExportPanel.failedmessage",
                                          ex.getMessage());
                title = s_stringMgr.getString("ExportPanel.failedtitle");
                optionType = JOptionPane.ERROR_MESSAGE;
            }
            SwingUtilities.getRoot(_panel).setVisible(false);
            JOptionPane.showMessageDialog(
                    SwingUtilities.getRoot(_panel), 
                    outcomeMessage, 
                    title, 
                    optionType);
        }
    }
    
    
    
    private final class CancelButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent evt) {
            SwingUtilities.getRoot(_panel).setVisible(false);
        }
    }
    
    
    private class CancelledException extends Exception {
    }
}
