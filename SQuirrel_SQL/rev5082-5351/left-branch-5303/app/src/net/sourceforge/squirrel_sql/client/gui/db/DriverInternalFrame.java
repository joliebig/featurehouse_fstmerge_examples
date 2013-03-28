package net.sourceforge.squirrel_sql.client.gui.db;



import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.DialogWidget;
import static net.sourceforge.squirrel_sql.client.preferences.PreferenceType.DRIVER_DEFINITIONS;
import net.sourceforge.squirrel_sql.fw.gui.DefaultFileListBoxModel;
import net.sourceforge.squirrel_sql.fw.gui.FileListBox;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.gui.IFileListBoxModel;
import net.sourceforge.squirrel_sql.fw.persist.ValidationException;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDriver;
import net.sourceforge.squirrel_sql.fw.sql.SQLDriverClassLoader;
import net.sourceforge.squirrel_sql.fw.util.FileExtensionFilter;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import javax.swing.*;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;


@SuppressWarnings("serial")
public class DriverInternalFrame extends DialogWidget
{
	
	public interface MaintenanceType
	{
		int NEW = 1;

		int MODIFY = 2;

		int COPY = 3;
	}

	
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(DriverInternalFrame.class);

	
	private static final ILogger s_log = LoggerController.createLogger(DriverInternalFrame.class);

	
	private static final int COLUMN_COUNT = 25;

	
	private static final int LIST_WIDTH = 400;

	
	private final IApplication _app;

	
	private final ISQLDriver _sqlDriver;

	
	private final int _maintType;

	
	private final JLabel _titleLbl = new JLabel();

	
	private final JTextField _driverName = new JTextField();

	
	private final JComboBox _driverClassCmb = new JComboBox();

	
	private final JTextField _url = new JTextField();

	private final JTextField _weburl = new JTextField();

	
	private final FileListBox _javaClassPathList = new FileListBox();

	
	private final FileListBox _extraClassPathList = new FileListBox(new DefaultFileListBoxModel());

	
	private ListDriversButton _javaClasspathListDriversBtn;

	
	private ListDriversButton _extraClasspathListDriversBtn;

	
	private JButton _extraClasspathDeleteBtn;

	
	private JButton _extraClasspathUpBtn;

	
	private JButton _extraClasspathDownBtn;

	private File lastExtraClassPathFileSelected = null;

	
	DriverInternalFrame(IApplication app, ISQLDriver sqlDriver, int maintType)
	{
		super("", true, app);
		if (app == null) { throw new IllegalArgumentException("Null IApplication passed"); }
		if (sqlDriver == null) { throw new IllegalArgumentException("Null ISQLDriver passed"); }
		if (maintType < MaintenanceType.NEW || maintType > MaintenanceType.COPY) { throw new IllegalArgumentException(
		
			s_stringMgr.getString("DriverInternalFrame.error.illegalvalue", maintType)); }

		_app = app;
		_sqlDriver = sqlDriver;
		_maintType = maintType;

		createGUI();
		loadData();
		pack();
	}

	
	public void setTitle(String title)
	{
		super.setTitle(title);
		_titleLbl.setText(title);
	}

	
	ISQLDriver getSQLDriver()
	{
		return _sqlDriver;
	}

	
	private void loadData()
	{
		_driverName.setText(_sqlDriver.getName());
		_driverClassCmb.setSelectedItem(_sqlDriver.getDriverClassName());
		_url.setText(_sqlDriver.getUrl());
		_weburl.setText(_sqlDriver.getWebSiteUrl());

		_extraClassPathList.removeAll();
		String[] fileNames = _sqlDriver.getJarFileNames();
		IFileListBoxModel model = _extraClassPathList.getTypedModel();
		for (int i = 0; i < fileNames.length; ++i)
		{
			model.addFile(new File(fileNames[i]));
		}

		if (model.getSize() > 0)
		{
			_extraClassPathList.setSelectedIndex(0);
		}

	}

	
	private void performClose()
	{
		dispose();
	}

	
	private void performOk()
	{
		try
		{
			applyFromDialog();
			if (_maintType == MaintenanceType.NEW || _maintType == MaintenanceType.COPY)
			{
				_app.getDataCache().addDriver(_sqlDriver, _app.getMessageHandler());
			}
			else
			{
				_app.getDataCache().refreshDriver(_sqlDriver, _app.getMessageHandler());
			}

			_app.savePreferences(DRIVER_DEFINITIONS);
			dispose();
		}
		catch (Throwable th)
		{
			displayErrorMessage(th);
		}
	}

	
	private void applyFromDialog() throws ValidationException
	{
		_sqlDriver.setName(_driverName.getText().trim());
		_sqlDriver.setJarFileNames(_extraClassPathList.getTypedModel().getFileNames());

		String driverClassName = (String) _driverClassCmb.getSelectedItem();
		_sqlDriver.setDriverClassName(driverClassName != null ? driverClassName.trim() : null);

		_sqlDriver.setUrl(_url.getText().trim());
		_sqlDriver.setWebSiteUrl(_weburl.getText().trim());
	}

	
	private void displayErrorMessage(final Throwable th)
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				_app.showErrorDialog(th);
			}
		});
	}

	private void createGUI()
	{
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

		
		makeToolWindow(true);

		String winTitle;
		if (_maintType == MaintenanceType.MODIFY)
		{
			winTitle = s_stringMgr.getString("DriverInternalFrame.changedriver", _sqlDriver.getName());
		}
		else
		{
			winTitle = s_stringMgr.getString("DriverInternalFrame.adddriver");
		}
		setTitle(winTitle);

		_driverName.setColumns(COLUMN_COUNT);
		_url.setColumns(COLUMN_COUNT);

		
		
		Container contentPane = getContentPane();
		Color color = UIManager.getDefaults().getColor("Panel.background");
		if (color != null)
		{
			contentPane.setBackground(color);
		}

		GridBagConstraints gbc = new GridBagConstraints();
		contentPane.setLayout(new GridBagLayout());

		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbc.weightx = 1;

		
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.insets = new Insets(5, 10, 5, 10);
		gbc.fill = GridBagConstraints.HORIZONTAL;
		contentPane.add(_titleLbl, gbc);

		
		++gbc.gridy;
		gbc.insets = new Insets(0, 10, 5, 10);
		contentPane.add(new JSeparator(), gbc);

		++gbc.gridy;
		contentPane.add(createDriverPanel(), gbc);

		JTabbedPane tabPnl = new JTabbedPane();
		tabPnl.addTab(s_stringMgr.getString("DriverInternalFrame.javaclasspath"), createJavaClassPathPanel());
		tabPnl.addTab(s_stringMgr.getString("DriverInternalFrame.extraclasspath"), createExtraClassPathPanel());

		++gbc.gridy;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weighty = 1;
		contentPane.add(tabPnl, gbc);

		++gbc.gridy;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weighty = 0;
		contentPane.add(createDriverClassPanel(), gbc);

		
		++gbc.gridy;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(5, 10, 5, 10);
		contentPane.add(new JSeparator(), gbc);

		++gbc.gridy;
		contentPane.add(createButtonsPanel(), gbc);

		AbstractAction closeAction = new AbstractAction()
		{
			public void actionPerformed(ActionEvent actionEvent)
			{
				performClose();
			}
		};
		KeyStroke escapeStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
		getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(escapeStroke,
			"CloseAction");
		getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(escapeStroke, "CloseAction");
		getRootPane().getInputMap(JComponent.WHEN_FOCUSED).put(escapeStroke, "CloseAction");
		getRootPane().getActionMap().put("CloseAction", closeAction);

	}

	private JPanel createButtonsPanel()
	{
		JPanel pnl = new JPanel();

		JButton okBtn = new JButton(s_stringMgr.getString("DriverInternalFrame.ok"));
		okBtn.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				performOk();
			}
		});
		JButton closeBtn = new JButton(s_stringMgr.getString("DriverInternalFrame.close"));
		closeBtn.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				performClose();
			}
		});

		pnl.add(okBtn);
		pnl.add(closeBtn);

		GUIUtils.setJButtonSizesTheSame(new JButton[] { okBtn, closeBtn });
		getRootPane().setDefaultButton(okBtn);

		return pnl;
	}

	private JPanel createDriverPanel()
	{
		_driverName.setColumns(25);

		JPanel pnl = new JPanel(new GridBagLayout());
		pnl.setBorder(BorderFactory.createTitledBorder(s_stringMgr.getString("DriverInternalFrame.driver")));

		final GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(4, 4, 4, 4);

		gbc.gridx = 0;
		gbc.gridy = 0;
		pnl.add(new JLabel(s_stringMgr.getString("DriverInternalFrame.name"), SwingConstants.RIGHT), gbc);

		++gbc.gridy;
		pnl.add(new JLabel(s_stringMgr.getString("DriverInternalFrame.egurl"), SwingConstants.RIGHT), gbc);

		++gbc.gridy;
		pnl.add(new JLabel(s_stringMgr.getString("DriverInternalFrame.weburl"), SwingConstants.RIGHT), gbc);

		gbc.weightx = 1.0;
		gbc.gridy = 0;
		++gbc.gridx;
		pnl.add(_driverName, gbc);

		++gbc.gridy;
		pnl.add(_url, gbc);

		++gbc.gridy;
		pnl.add(_weburl, gbc);

		return pnl;
	}

	private Component createDriverClassPanel()
	{
		_driverClassCmb.setEditable(true);

		JPanel pnl = new JPanel(new GridBagLayout());

		final GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(4, 4, 4, 4);

		gbc.gridx = 0;
		gbc.gridy = 0;
		pnl.add(new JLabel(s_stringMgr.getString("DriverInternalFrame.classname"), SwingConstants.RIGHT), gbc);

		gbc.weightx = 1.0;
		++gbc.gridx;
		pnl.add(_driverClassCmb, gbc);

		return pnl;
	}

	
	private JPanel createJavaClassPathPanel()
	{
		_javaClasspathListDriversBtn = new ListDriversButton(_javaClassPathList);
		_javaClasspathListDriversBtn.setEnabled(_javaClassPathList.getModel().getSize() > 0);
		

		IFileListBoxModel model = _javaClassPathList.getTypedModel();
		if (model.getSize() > 0)
		{
			_javaClassPathList.setSelectedIndex(0);
		}

		JPanel pnl = new JPanel(new GridBagLayout());
		final GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(4, 4, 4, 4);
		gbc.anchor = GridBagConstraints.WEST;
		gbc.weighty = 1.0;

		
		
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridheight = GridBagConstraints.REMAINDER;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weightx = 1.0;
		JScrollPane sp =
			new JScrollPane(_javaClassPathList, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		final Dimension dm = sp.getPreferredSize();
		dm.width = LIST_WIDTH; 
		sp.setPreferredSize(dm);
		pnl.add(sp, gbc);

		++gbc.gridx;
		gbc.gridheight = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.NORTH;
		gbc.weightx = 0.0;
		pnl.add(_javaClasspathListDriversBtn, gbc);

		return pnl;
	}

	
	private JPanel createExtraClassPathPanel()
	{
		_extraClasspathListDriversBtn = new ListDriversButton(_extraClassPathList);
		_extraClassPathList.addListSelectionListener(new ExtraClassPathListBoxListener());
		_extraClassPathList.getModel().addListDataListener(new ExtraClassPathListDataListener());

		_extraClasspathUpBtn = new JButton(s_stringMgr.getString("DriverInternalFrame.up"));
		_extraClasspathUpBtn.setEnabled(false);
		_extraClasspathUpBtn.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				synchronized (_extraClassPathList)
				{
					int idx = _extraClassPathList.getSelectedIndex();
					if (idx > 0)
					{
						IFileListBoxModel model = _extraClassPathList.getTypedModel();
						File file = model.removeFile(idx);
						--idx;
						model.insertFileAt(file, idx);
						_extraClassPathList.setSelectedIndex(idx);
					}
				}
			}
		});

		_extraClasspathDownBtn = new JButton(s_stringMgr.getString("DriverInternalFrame.down"));
		_extraClasspathDownBtn.setEnabled(false);
		_extraClasspathDownBtn.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				synchronized (_extraClassPathList)
				{
					int idx = _extraClassPathList.getSelectedIndex();
					IFileListBoxModel model = _extraClassPathList.getTypedModel();
					if (idx > -1 && idx < (model.getSize() - 1))
					{
						File file = model.removeFile(idx);
						++idx;
						model.insertFileAt(file, idx);
						_extraClassPathList.setSelectedIndex(idx);
					}
				}
			}
		});

		JButton newBtn = new AddListEntryButton();

		_extraClasspathDeleteBtn = new JButton(s_stringMgr.getString("DriverInternalFrame.delete"));
		_extraClasspathDeleteBtn.setEnabled(false);
		_extraClasspathDeleteBtn.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				int idx = _extraClassPathList.getSelectedIndex();
				if (idx != -1)
				{
					IFileListBoxModel model = _extraClassPathList.getTypedModel();
					model.removeFile(idx);
					final int size = model.getSize();
					if (idx < size)
					{
						_extraClassPathList.setSelectedIndex(idx);
					}
					else if (size > 0)
					{
						_extraClassPathList.setSelectedIndex(size - 1);
					}
				}
			}
		});

		JPanel pnl = new JPanel(new GridBagLayout());
		final GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.WEST;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		gbc.insets = new Insets(4, 4, 4, 4);

		
		
		gbc.gridheight = GridBagConstraints.REMAINDER;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.fill = GridBagConstraints.BOTH;
		JScrollPane sp =
			new JScrollPane(_extraClassPathList, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		final Dimension dm = sp.getPreferredSize();
		dm.width = LIST_WIDTH; 
		sp.setPreferredSize(dm);
		pnl.add(sp, gbc);

		gbc.gridheight = 1;
		gbc.weightx = 0.0;
		gbc.weighty = 0.0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		++gbc.gridx;
		pnl.add(_extraClasspathListDriversBtn, gbc);

		++gbc.gridy;
		gbc.insets = new Insets(5, 5, 5, 5);
		pnl.add(new JSeparator(), gbc);
		gbc.insets = new Insets(4, 4, 4, 4);

		++gbc.gridy;
		pnl.add(_extraClasspathUpBtn, gbc);

		++gbc.gridy;
		pnl.add(_extraClasspathDownBtn, gbc);

		++gbc.gridy;
		gbc.insets = new Insets(5, 5, 5, 5);
		pnl.add(new JSeparator(), gbc);
		gbc.insets = new Insets(4, 4, 4, 4);

		++gbc.gridy;
		pnl.add(newBtn, gbc);

		++gbc.gridy;
		pnl.add(_extraClasspathDeleteBtn, gbc);

		return pnl;
	}

	
	private final class AddListEntryButton extends JButton implements ActionListener
	{
		private JFileChooser _chooser;

		AddListEntryButton()
		{
			super(s_stringMgr.getString("DriverInternalFrame.add"));
			addActionListener(this);
		}

		public void actionPerformed(ActionEvent evt)
		{
			if (_chooser == null)
			{
				_chooser = new JFileChooser();
				if (lastExtraClassPathFileSelected != null)
				{
					if (lastExtraClassPathFileSelected.isDirectory())
					{
						_chooser.setCurrentDirectory(lastExtraClassPathFileSelected);
					}
					else
					{
						_chooser.setCurrentDirectory(new File(lastExtraClassPathFileSelected.getParent()));
					}
				}
				_chooser.setFileHidingEnabled(false);
				_chooser.setMultiSelectionEnabled(true);
				_chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
				_chooser.addChoosableFileFilter(new FileExtensionFilter(
					s_stringMgr.getString("DriverInternalFrame.jarfiles"), new String[] { ".jar", ".zip" }));
			}
			int returnVal = _chooser.showOpenDialog(getParent());
			if (returnVal == JFileChooser.APPROVE_OPTION)
			{
				File[] selFiles = _chooser.getSelectedFiles();
				if (selFiles != null)
				{
					IFileListBoxModel myModel = _extraClassPathList.getTypedModel();
					for (int i = 0; i < selFiles.length; ++i)
					{
						myModel.addFile(selFiles[i]);
					}
					_extraClassPathList.setSelectedIndex(myModel.getSize() - 1);
				}
			}
		}
	}

	
	private final class ListDriversButton extends JButton implements ActionListener
	{
		private FileListBox _listBox;

		ListDriversButton(FileListBox listBox)
		{
			super(s_stringMgr.getString("DriverInternalFrame.listdrivers"));
			setEnabled(false);
			_listBox = listBox;
			addActionListener(this);
		}

		public void actionPerformed(ActionEvent e)
		{
			_driverClassCmb.removeAllItems();
			final String[] fileNames = _listBox.getTypedModel().getFileNames();

			if (fileNames.length > 0)
			{
				_app.getThreadPool().addTask(new Runnable()
				{
					@Override
					public void run()
					{
						try
						{
							final URL[] urls = new URL[fileNames.length];
							for (int i = 0; i < fileNames.length; ++i)
							{
								urls[i] = new File(fileNames[i]).toURI().toURL();
							}

							SQLDriverClassLoader cl = new SQLDriverClassLoader(urls);
							@SuppressWarnings("unchecked")
							
							Class[] classes = cl.getDriverClasses(s_log);
							for (int i = 0; i < classes.length; ++i)
							{
								addDriverClassToCombo(classes[i].getName());
							}
						}
						catch (MalformedURLException ex)
						{
							displayErrorMessage(ex);
						}

					}
				});
			}

			if (_driverClassCmb.getItemCount() > 0)
			{
				_driverClassCmb.setSelectedIndex(0);
			}
		}
	}

	private void addDriverClassToCombo(final String driverClassName)
	{
		GUIUtils.processOnSwingEventThread(new Runnable()
		{
			@Override
			public void run()
			{
				_driverClassCmb.addItem(driverClassName);
			}

		});
	}

	private class ExtraClassPathListDataListener implements ListDataListener
	{
		public void contentsChanged(ListDataEvent evt)
		{
			final boolean enable = _extraClassPathList.getModel().getSize() > 0;
			_extraClasspathListDriversBtn.setEnabled(enable);
		}

		public void intervalAdded(ListDataEvent evt)
		{
			final boolean enable = _extraClassPathList.getModel().getSize() > 0;
			_extraClasspathListDriversBtn.setEnabled(enable);
		}

		public void intervalRemoved(ListDataEvent evt)
		{
			final boolean enable = _extraClassPathList.getModel().getSize() > 0;
			_extraClasspathListDriversBtn.setEnabled(enable);
		}
	}

	private class ExtraClassPathListBoxListener implements ListSelectionListener
	{
		public void valueChanged(ListSelectionEvent evt)
		{
			final int selIdx = _extraClassPathList.getSelectedIndex();
			lastExtraClassPathFileSelected = _extraClassPathList.getSelectedFile();
			final ListModel model = _extraClassPathList.getModel();

			_extraClasspathDeleteBtn.setEnabled(selIdx != -1);

			_extraClasspathUpBtn.setEnabled(selIdx > 0 && model.getSize() > 1);
			_extraClasspathDownBtn.setEnabled(selIdx > -1 && selIdx < (model.getSize() - 1));
			
			
			
			
			
			
			
			
			
			
			
		}
	}
}
