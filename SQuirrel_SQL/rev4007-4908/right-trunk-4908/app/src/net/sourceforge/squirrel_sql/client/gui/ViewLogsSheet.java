package net.sourceforge.squirrel_sql.client.gui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;

import javax.swing.*;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.DialogWidget;
import net.sourceforge.squirrel_sql.client.preferences.SquirrelPreferences;
import net.sourceforge.squirrel_sql.client.util.ApplicationFiles;
import net.sourceforge.squirrel_sql.fw.gui.CursorChanger;
import net.sourceforge.squirrel_sql.fw.gui.DirectoryListComboBox;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.gui.TextPopupMenu;
import net.sourceforge.squirrel_sql.fw.gui.ToolBar;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.Utilities;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

public class ViewLogsSheet extends DialogWidget
{
	
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(ViewLogsSheet.class);

	
	private static final ILogger s_log =
		LoggerController.createLogger(ViewLogsSheet.class);

	
	private static ViewLogsSheet s_instance;

	
	private final IApplication _app;
    
    
    private final SquirrelPreferences _prefs;

	
	private final LogsComboBox _logDirCmb = new LogsComboBox();

	
	private final JTextArea _logContentsTxt = new JTextArea(20, 50);

	
	private final JButton _refreshBtn = new JButton(s_stringMgr.getString("ViewLogsSheet.refresh"));

    private final JCheckBox _errorChkbox = new JCheckBox("Errors");
    private final JCheckBox _debugChkbox = new JCheckBox("Debug");
    private final JCheckBox _infoChkbox = new JCheckBox("Info");
    
	
	private final File _logDir;

	
	private boolean _closing = false;

	
	private boolean _refreshing = false;
    
	
	private ViewLogsSheet(IApplication app)
	{
		super(s_stringMgr.getString("ViewLogsSheet.title"), true, true, true, true, app);
		if (app == null)
		{
			throw new IllegalArgumentException("IApplication == null");
		}

		_app = app;
        _prefs = _app.getSquirrelPreferences();
		_logDir = new ApplicationFiles().getExecutionLogFile().getParentFile();
		createUserInterface();
	}

	
	public static synchronized void showSheet(IApplication app)
	{
		if (s_instance == null)
		{
			s_instance = new ViewLogsSheet(app);
			app.getMainFrame().addWidget(s_instance);
			centerWithinDesktop(s_instance);
		}

		final boolean wasVisible = s_instance.isVisible();
		if (!wasVisible)
		{
			s_instance.setVisible(true);
		}
		s_instance.moveToFront();
		if (!wasVisible && !s_instance._refreshing)
		{
			s_instance.startRefreshingLog();
		}
	}

	
	public static synchronized void disposeInstance() {
		s_instance = null;
	}
	
   public void dispose()
	{
		
		_closing = true;

		ViewLogsSheet.disposeInstance();
		
		super.dispose();
	}

	
	private void performClose()
	{
		dispose();
	}

	
	private synchronized void startRefreshingLog()
	{
		if (!_refreshing)
		{
			_app.getThreadPool().addTask(new Refresher());
		}
	}

    
    private void enableComponents(final boolean enabled) 
    {
        GUIUtils.processOnSwingEventThread(new Runnable() {
            public void run() {
                _refreshBtn.setEnabled(enabled);
                _logDirCmb.setEnabled(enabled);
            }
        });
    }
    
	
	private void refreshLog()
	{
	    enableComponents(false);
        CursorChanger cursorChg = new CursorChanger(getAwtContainer());
		cursorChg.show();
		try
		{
			try
			{
				SwingUtilities.invokeAndWait(new Runnable()
				{
					public void run()
					{
						_logContentsTxt.setText("");
					}
				});
			}
			catch (Exception ex)
			{
                
				s_log.error(s_stringMgr.getString("ViewLogsSheet.error.clearlogcontents"), ex);
			}
			final File logFile = (File)_logDirCmb.getSelectedItem();
			if (logFile != null)
			{
				try
				{
					if (logFile.exists() && logFile.canRead())
					{
						final BufferedReader rdr = new BufferedReader(new FileReader(logFile));
						try
						{
							String line = null;
							StringBuffer chunk = new StringBuffer(16384);
							while ((line = rdr.readLine()) != null)
							{
								if (_closing)
								{
									return;
								}

								if (chunk.length() > 16000)
								{
									final String finalLine = chunk.toString();
									SwingUtilities.invokeAndWait(new Runnable()
									{
										public void run()
										{
											if (!_closing)
											{
												_logContentsTxt.append(finalLine);
											}
										}
									});
									chunk = new StringBuffer(16384);
								}
								else
								{
                                    if (shouldAppendLineToChunk(line)) {
                                        chunk.append(line).append('\n');
                                    }                                    
								}
							}

							if (_closing)
							{
								return;
							}

							final String finalLine = chunk.toString();
							SwingUtilities.invokeAndWait(new Runnable()
							{
								public void run()
								{
									if (!_closing)
									{
										_logContentsTxt.append(finalLine);
									}
								}
							});
						}
						finally
						{
							rdr.close();
						}
					}
				}
				catch (Exception ex)
				{
                    
					final String msg = s_stringMgr.getString("ViewLogsSheet.error.processinglogfile");
					s_log.error(msg, ex);
				}
			}
			else
			{
                
				s_log.debug(s_stringMgr.getString("ViewLogsSheet.info.nulllogfile"));
			}

			if (_closing)
			{
				return;
			}

			
			try
			{
				int pos = Math.max(0, _logContentsTxt.getText().length() - 1);
				int line = _logContentsTxt.getLineOfOffset(pos);
				final int finalpos = _logContentsTxt.getLineStartOffset(line);
                SwingUtilities.invokeAndWait(new Runnable() {
                    public void run()
                    {
                        _logContentsTxt.setCaretPosition(finalpos);
                    }
                });
			}
			catch (Exception ex)
			{
                
				s_log.error(s_stringMgr.getString("ViewLogsSheet.error.setcaret"), ex);
			}
		}
		finally
		{
            enableComponents(true);
			_refreshing = false;
			cursorChg.restore();
		}
	}

    
    private boolean shouldAppendLineToChunk(String line) {
        boolean result = false;
        if (line == null || line.length() == 0) {
            return false;
        }
        if (_errorChkbox.isSelected() 
                && _debugChkbox.isSelected() 
                && _infoChkbox.isSelected()) 
        {
            return true;
        }
        int threadNameEndIdx = line.indexOf("]");
        if (threadNameEndIdx > -1) {
            char levelChar = line.charAt(threadNameEndIdx+2);
            if (_errorChkbox.isSelected() && levelChar == 'E') {
                result = true;
            }
            if (_debugChkbox.isSelected() && levelChar == 'D') {
                result = true;
            }
            if (_infoChkbox.isSelected() && levelChar == 'I') {
                result = true;
            }
            if (levelChar != 'E' && levelChar != 'D' && levelChar != 'I') {
                result = true;
            }
        } else {
            result = true;
        }
        return result;
    }
    
	
	private void createUserInterface()
	{
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		makeToolWindow(true);
		Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());
		contentPane.add(createToolBar(), BorderLayout.NORTH);
		contentPane.add(createMainPanel(), BorderLayout.CENTER);
		contentPane.add(createButtonsPanel(), BorderLayout.SOUTH);
		pack();


      AbstractAction closeAction = new AbstractAction()
      {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent actionEvent)
         {
            performClose();
         }
      };
      KeyStroke escapeStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
      getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(escapeStroke, "CloseAction");
      getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(escapeStroke, "CloseAction");
      getRootPane().getInputMap(JComponent.WHEN_FOCUSED).put(escapeStroke, "CloseAction");
      getRootPane().getActionMap().put("CloseAction", closeAction);

   }

	private ToolBar createToolBar()
	{
		final ToolBar tb = new ToolBar();
		tb.setUseRolloverButtons(true);
		tb.setFloatable(false);

		final Object[] args = {getTitle(), _logDir.getAbsolutePath()};
		final String lblTitle = s_stringMgr.getString("ViewLogsSheet.storedin", args);
		final JLabel lbl = new JLabel(lblTitle);
		lbl.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
		tb.add(lbl);

		return tb;
	}

	
	private JPanel createMainPanel()
	{
		
		final TextPopupMenu pop = new TextPopupMenu();
		pop.setTextComponent(_logContentsTxt);
		_logContentsTxt.addMouseListener(new MouseAdapter()
		{
			public void mousePressed(MouseEvent evt)
			{
				if (evt.isPopupTrigger())
				{
					pop.show(evt.getComponent(), evt.getX(), evt.getY());
				}
			}
			public void mouseReleased(MouseEvent evt)
			{
				if (evt.isPopupTrigger())
				{
					pop.show(evt.getComponent(), evt.getX(), evt.getY());
				}
			}
		});

		File appLogFile = new ApplicationFiles().getExecutionLogFile();
		_logDirCmb.load(appLogFile.getParentFile());

		if (_logDirCmb.getModel().getSize() > 0)
		{
			_logDirCmb.setSelectedItem(appLogFile.getName());
		}

		
		
		
		
		_logDirCmb.addActionListener(new ChangeLogListener());

		final JPanel pnl = new JPanel(new BorderLayout());
		pnl.add(_logDirCmb, BorderLayout.NORTH);
		_logContentsTxt.setBorder(BorderFactory.createEmptyBorder(0, 1, 0, 0));
		pnl.add(new JScrollPane(_logContentsTxt), BorderLayout.CENTER);

		return pnl;
	}

	
	private JPanel createButtonsPanel()
	{
		JPanel pnl = new JPanel();

		pnl.add(_refreshBtn);
		_refreshBtn.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				startRefreshingLog();
			}
		});

		JButton closeBtn = new JButton(s_stringMgr.getString("ViewLogsSheet.close"));
		closeBtn.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				performClose();
			}
		});
		pnl.add(closeBtn);

        _errorChkbox.setSelected(_prefs.getShowErrorLogMessages());
        _errorChkbox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                _prefs.setShowErrorLogMessages(_errorChkbox.isSelected());
            }
        }); 
        _infoChkbox.setSelected(_prefs.getShowInfoLogMessages());
        _infoChkbox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                _prefs.setShowInfoLogMessages(_infoChkbox.isSelected());
            }
        }); 
        _debugChkbox.setSelected(_prefs.getShowDebugLogMessage());
        _debugChkbox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                _prefs.setShowDebugLogMessages(_debugChkbox.isSelected());
            }
        }); 
        pnl.add(_errorChkbox);
        pnl.add(_infoChkbox);
        pnl.add(_debugChkbox);
                
		GUIUtils.setJButtonSizesTheSame(new JButton[] {closeBtn, _refreshBtn});
		getRootPane().setDefaultButton(closeBtn);

		return pnl;
	}

	private final class ChangeLogListener implements ActionListener
	{
		public void actionPerformed(ActionEvent evt)
		{
			ViewLogsSheet.this.startRefreshingLog();
		}
	}

	private final class Refresher implements Runnable
	{
		public void run()
		{
			ViewLogsSheet.this.refreshLog();
		}
	}

	private static final class LogsComboBox extends DirectoryListComboBox
	{
		private static final long serialVersionUID = 1L;
		
		private File _dir;

		public void load(File dir, FilenameFilter filter)
		{
			_dir = dir;
			super.load(dir, filter);
		}

		public void addItem(Object anObject)
		{
			super.addItem(new LogFile(_dir, anObject.toString()));
		}
	}

	private static final class LogFile extends File
	{
		private static final long serialVersionUID = 1L;
		
		private final String _stringRep;

		LogFile(File dir, String name)
		{
			super(dir, name);
			StringBuffer buf = new StringBuffer();
			buf.append(getName()).append(" (")
				.append(Utilities.formatSize(length())).append(")");
			_stringRep = buf.toString();
		}

		public String toString()
		{
			return _stringRep;
		}
	}
}
