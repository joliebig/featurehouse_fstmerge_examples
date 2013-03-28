package net.sourceforge.squirrel_sql.plugins.laf;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.JComboBox;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

public class LookAndFeelComboBox extends JComboBox
{
    private static final long serialVersionUID = 1L;

    
	private Map<String, LookAndFeelInfo> _lafsByName = 
        new TreeMap<String, LookAndFeelInfo>();

	
	private Map<String, LookAndFeelInfo> _lafsByClassName = 
        new TreeMap<String, LookAndFeelInfo>();

	
	public LookAndFeelComboBox()
	{
		this(null);
	}

	
	public LookAndFeelComboBox(String selectedLafName)
	{
		super();
		generateLookAndFeelInfo();
		if (selectedLafName == null)
		{
			selectedLafName = UIManager.getLookAndFeel().getName();
		}
		setSelectedLookAndFeelName(selectedLafName);
	}

	public LookAndFeelInfo getSelectedLookAndFeel()
	{
		return _lafsByName.get(getSelectedItem());
	}

	public void setSelectedLookAndFeelName(String selectedLafName)
	{
		if (selectedLafName != null)
		{
			getModel().setSelectedItem(selectedLafName);
		}
	}

	public void setSelectedLookAndFeelClassName(String selectedLafClassName)
	{
		if (selectedLafClassName != null)
		{
			LookAndFeelInfo info =_lafsByClassName.get(selectedLafClassName);
			if (info != null)
			{
				setSelectedLookAndFeelName(info.getName());
			}
		}
	}

	
	private void generateLookAndFeelInfo()
	{
		
		
		LookAndFeelInfo[] info = UIManager.getInstalledLookAndFeels();
		_lafsByName = new TreeMap<String, LookAndFeelInfo>();
		for (int i = 0; i < info.length; ++i)
		{
			_lafsByName.put(info[i].getName(), info[i]);
			_lafsByClassName.put(info[i].getClassName(), info[i]);
		}

		
		
		for (Iterator<LookAndFeelInfo> it = _lafsByName.values().iterator(); it.hasNext();)
		{
			addItem(it.next().getName());
		}
	}
}
