package net.sourceforge.squirrel_sql.fw.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.Utilities;

public class ChooserPreviewer extends JComponent
{
	
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(ChooserPreviewer.class);

	
	private int DFT_BYTES_TO_READ = 2048;

	
	private final JPanel _emptyPnl = new JPanel();

	
	private final JTextArea _textComponent = new JTextArea();

	
	private final JLabel _imageComponent = new JLabel();

	
	private Component _currentComponent;

	
	private JScrollPane _currentComponentSp;

	
	private JFileChooser _chooser;

	
	private ChooserListener _propChangeListener;

	
	public ChooserPreviewer()
	{
		super();
		createUserInterface();
	}

	
	public void addNotify()
	{
		super.addNotify();
		cleanup();
		Component parent = getParent();
		while (parent != null)
		{
			if (parent instanceof JFileChooser)
			{
				_chooser = (JFileChooser)parent;
				break;
			}
			parent = parent.getParent();
		}

		if (_chooser != null)
		{
			_propChangeListener = new ChooserListener();
			_chooser.addPropertyChangeListener(_propChangeListener);
		}
	}

	
	public void removeNotify()
	{
		super.removeNotify();
		cleanup();
	}

	
	protected void cleanup()
	{
		if (_chooser != null && _propChangeListener != null)
		{
			_chooser.removePropertyChangeListener(_propChangeListener);
		}
		_propChangeListener = null;
		_chooser = null;
	}

	
	protected void fileChanged()
	{
		Component componentToUse = _emptyPnl;

		File file = _chooser.getSelectedFile();
		if (file != null && file.isFile() && file.canRead())
		{
			String suffix = Utilities.getFileNameSuffix(file.getPath()).toLowerCase();
			if (suffix.equals("gif") || suffix.equals("jpg")
				|| suffix.equals("jpeg") || suffix.equals("png"))
			{
				componentToUse = readImageFile(file);
			}
			else
			{
				componentToUse = readTextFile(file);
			}
		}

		if (componentToUse != _currentComponent)
		{
			_currentComponentSp.setViewportView(componentToUse);
			_currentComponent = componentToUse;
		}
	}

	
	protected Component readImageFile(File file)
	{
		final ImageIcon icon = new ImageIcon(file.getPath());
		_imageComponent.setIcon(icon);
		return _imageComponent;
	}

	
	protected Component readTextFile(File file)
	{
		StringBuffer buf = new StringBuffer(DFT_BYTES_TO_READ);
		if (file != null && file.isFile() && file.canRead())
		{
			try
			{
				FileReader rdr = new FileReader(file);
				try
				{
					char[] data = new char[DFT_BYTES_TO_READ];
					int count = rdr.read(data, 0, data.length);
					if (count != -1)
					{
						buf.append(data, 0, count);
					}
				}
				finally
				{
					rdr.close();
				}
			}
			catch (IOException ex)
			{
				buf = new StringBuffer(s_stringMgr.getString("ChooserPreviewer.error", ex.toString()));
			}
		}
		_textComponent.setText(buf.toString());
		_textComponent.setCaretPosition(0);
		return _textComponent;
	}

	
	private void createUserInterface()
	{
		_textComponent.setEditable(false);
		setLayout(new BorderLayout());
		_currentComponentSp = new JScrollPane(_textComponent);
		add(_currentComponentSp, BorderLayout.CENTER);
		setPreferredSize(new Dimension(250, 0));
	}

	
	private class ChooserListener implements PropertyChangeListener
	{
		public void propertyChange(PropertyChangeEvent evt)
		{
			if (evt.getPropertyName().equals(JFileChooser.SELECTED_FILE_CHANGED_PROPERTY))
			{
				ChooserPreviewer.this.fileChanged();
			}
		}
	}
}
