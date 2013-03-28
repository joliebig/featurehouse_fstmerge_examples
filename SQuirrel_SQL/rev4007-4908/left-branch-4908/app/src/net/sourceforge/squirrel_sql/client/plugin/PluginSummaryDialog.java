package net.sourceforge.squirrel_sql.client.plugin;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.util.ApplicationFiles;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetException;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

public class PluginSummaryDialog extends JDialog
{
    private static final long serialVersionUID = 1L;

    
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(PluginSummaryDialog.class);

	transient private final IApplication _app;

	private PluginSummaryTable _pluginPnl;

    static interface i18n {
        
        String UNLOAD_LABEL = 
            s_stringMgr.getString("PluginSummaryDialog.unload");
    }
    
	public PluginSummaryDialog(IApplication app, Frame owner)
		throws DataSetException
	{
		super(owner, s_stringMgr.getString("PluginSummaryDialog.title"));
		_app = app;
		createGUI();
	}

	private void saveSettings()
	{
		_app.getPluginManager().setPluginStatuses(_pluginPnl.getPluginStatus());
	}

	private void createGUI() throws DataSetException
	{
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);








		final Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());

		
		final JLabel pluginLoc = new JLabel(s_stringMgr.getString("PluginSummaryDialog.pluginloc",
					new ApplicationFiles().getPluginsDirectory().getAbsolutePath()));
		pluginLoc.setBorder(BorderFactory.createEmptyBorder(1, 4, 1, 4));

		contentPane.add(pluginLoc, BorderLayout.NORTH);

		
		final PluginManager pmgr = _app.getPluginManager();
		final PluginInfo[] pluginInfo = pmgr.getPluginInformation();
		final PluginStatus[] pluginStatus = pmgr.getPluginStatuses();
		_pluginPnl = new PluginSummaryTable(pluginInfo, pluginStatus);
		contentPane.add(new JScrollPane(_pluginPnl), BorderLayout.CENTER);

		final JPanel btnsPnl = new JPanel();
		final JButton okBtn = new JButton(s_stringMgr.getString("PluginSummaryDialog.ok"));
		okBtn.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				saveSettings();
				dispose();
			}
		});
		btnsPnl.add(okBtn);
        final JButton unloadButton = new JButton(i18n.UNLOAD_LABEL);
        unloadButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                int[] rows = _pluginPnl.getSelectedRows();
                if (rows.length == 0) {
                    
                    return;
                }
                for (int row : rows) {
                    
                    String internalName = 
                        (String)_pluginPnl.getModel().getValueAt(row, 1);
                    _app.getPluginManager().unloadPlugin(internalName);
                    
                    _pluginPnl.setValueAt("false", row, 3);
                }
                _pluginPnl.repaint();
            }
        });
        btnsPnl.add(unloadButton);
		final JButton closeBtn = new JButton(s_stringMgr.getString("PluginSummaryDialog.close"));
		closeBtn.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				dispose();
			}
		});
		btnsPnl.add(closeBtn);
		contentPane.add(btnsPnl, BorderLayout.SOUTH);


      AbstractAction closeAction = new AbstractAction()
      {
        private static final long serialVersionUID = 1L;

        public void actionPerformed(ActionEvent actionEvent)
         {
            setVisible(false);
            dispose();
         }
      };
      KeyStroke escapeStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
      getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(escapeStroke, "CloseAction");
      getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(escapeStroke, "CloseAction");
      getRootPane().getInputMap(JComponent.WHEN_FOCUSED).put(escapeStroke, "CloseAction");
      getRootPane().getActionMap().put("CloseAction", closeAction);



      pack();
      setSize(655, 500);
		GUIUtils.centerWithinParent(this);
		setResizable(true);
        
	}
}
