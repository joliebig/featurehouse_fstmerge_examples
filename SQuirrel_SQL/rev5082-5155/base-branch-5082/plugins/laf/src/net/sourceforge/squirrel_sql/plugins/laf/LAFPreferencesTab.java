package net.sourceforge.squirrel_sql.plugins.laf;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

import net.sourceforge.squirrel_sql.fw.gui.MultipleLineLabel;
import net.sourceforge.squirrel_sql.fw.gui.OutputLabel;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.StringManager;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.preferences.IGlobalPreferencesPanel;

public class LAFPreferencesTab implements IGlobalPreferencesPanel
{

	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(LAFPreferencesTab.class);

	
	private static ILogger s_log = LoggerController.createLogger(LAFPreferencesTab.class);

	
	private LAFPlugin _plugin;

	
	

	
	private LAFRegister _lafRegister;

	
	private LAFPreferencesPanel _myPanel;

	
	public LAFPreferencesTab(LAFPlugin plugin, LAFRegister lafRegister)
	{
		super();
		if (plugin == null)
		{
			throw new IllegalArgumentException("Null LAFPlugin passed");
		}
		if (lafRegister == null)
		{
			throw new IllegalArgumentException("Null LAFRegister passed");
		}
		_plugin = plugin;
		
		_lafRegister = lafRegister;
	}

	
	public void initialize(IApplication app)
	{
		if (app == null)
		{
			throw new IllegalArgumentException("Null IApplication passed");
		}
		
		((LAFPreferencesPanel)getPanelComponent()).loadData();
	}

   public void uninitialize(IApplication app)
   {
      
   }

   
	public synchronized Component getPanelComponent()
	{
		if (_myPanel == null)
		{
			_myPanel = new LAFPreferencesPanel(_plugin, _lafRegister);
		}
		return _myPanel;
	}

	
	public void applyChanges()
	{
		_myPanel.applyChanges();
	}

	
	public String getTitle()
	{
		return LAFPreferencesPanel.LAFPreferencesPanelI18n.TAB_TITLE;
	}

	
	public String getHint()
	{
		return LAFPreferencesPanel.LAFPreferencesPanelI18n.TAB_HINT;
	}

	
	private static final class LAFPreferencesPanel extends JPanel
	{
		
		interface LAFPreferencesPanelI18n
		{
			
			String LOOK_AND_FEEL = s_stringMgr.getString("laf.lookAndFeel");
			
			String LAF_WARNING = s_stringMgr.getString("laf.lafWarning");
			
			String TAB_TITLE = s_stringMgr.getString("laf.lf");
			
			String TAB_HINT = s_stringMgr.getString("laf.settings");
			
			String LAF_LOC = s_stringMgr.getString("laf.jars");
         
         
         String LAF_CRITICAL_WARNING = s_stringMgr.getString("laf.lafCriticalWarning");
      }

		private LookAndFeelComboBox _lafCmb = new LookAndFeelComboBox();
		private JCheckBox _allowSetBorder = new JCheckBox(s_stringMgr.getString("laf.allowsetborder"));


		private LAFPlugin _plugin;
		private LAFRegister _lafRegister;

		private LAFPreferences _prefs;

		
		private LookAndFeelComboListener _lafComboListener;

		
		private BaseLAFPreferencesPanelComponent _curLAFConfigComp;

		private JPanel _lafPnl;
	
		LAFPreferencesPanel(LAFPlugin plugin, LAFRegister lafRegister)
		{
			super(new GridBagLayout());
			_plugin = plugin;
			_lafRegister = lafRegister;
			_prefs = _plugin.getLAFPreferences();
			createUserInterface();
		}

		public void addNotify()
		{
			super.addNotify();
			_lafComboListener = new LookAndFeelComboListener();
			_lafCmb.addActionListener(_lafComboListener);
		}

		public void removeNotify()
		{
			if (_lafComboListener != null)
			{
				_lafCmb.removeActionListener(_lafComboListener);
				_lafComboListener = null;
			}
			super.removeNotify();
		}

		void loadData()
		{
			final String selLafClassName = _prefs.getLookAndFeelClassName();
			_allowSetBorder.setSelected(_prefs.getCanLAFSetBorder());
			_lafCmb.setSelectedLookAndFeelClassName(selLafClassName);

			updateLookAndFeelConfigControl();
		}

		void applyChanges()
		{
			_prefs.setCanLAFSetBorder(_allowSetBorder.isSelected());
			_prefs.setLookAndFeelClassName(_lafCmb.getSelectedLookAndFeel().getClassName());
			
			_lafRegister.applyPreferences();

			boolean forceChange = false;
			if (_curLAFConfigComp != null)
			{
				forceChange = _curLAFConfigComp.applyChanges();
			}

			try
			{
				_lafRegister.setLookAndFeel(forceChange);
			}
			catch (Exception ex)
			{
				s_log.error("Error setting Look and Feel", ex);
			}
		}

		private void createUserInterface()
		{
			final GridBagConstraints gbc = new GridBagConstraints();
			gbc.anchor = GridBagConstraints.WEST;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.insets = new Insets(4, 4, 4, 4);

			gbc.gridy = 0;
			gbc.gridx = 0;
			add(createSettingsPanel(), gbc);

			++gbc.gridy;
			add(createLookAndFeelPanel(), gbc);

			++gbc.gridy;
			gbc.gridx = 0;
			gbc.gridwidth = GridBagConstraints.REMAINDER;
			add(new MultipleLineLabel(LAFPreferencesPanelI18n.LAF_WARNING), gbc);

         ++gbc.gridy;
         gbc.gridx = 0;
         gbc.gridwidth = GridBagConstraints.REMAINDER;
         MultipleLineLabel enforedWarningLabel = new MultipleLineLabel(LAFPreferencesPanelI18n.LAF_CRITICAL_WARNING);
         enforedWarningLabel.setForeground(Color.red);
         add(enforedWarningLabel, gbc);
		}

		private JPanel createLookAndFeelPanel()
		{
			_lafPnl = new JPanel(new GridBagLayout());
			
			_lafPnl.setBorder(BorderFactory.createTitledBorder(s_stringMgr.getString("laf.broderLaf")));

			final GridBagConstraints gbc = new GridBagConstraints();
			gbc.weightx = 1;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.insets = new Insets(4, 4, 4, 4);
			gbc.anchor = GridBagConstraints.WEST;

			gbc.gridx = 0;
			gbc.gridy = 0;
			_lafPnl.add(new JLabel(LAFPreferencesPanelI18n.LOOK_AND_FEEL, SwingConstants.RIGHT), gbc);

			++gbc.gridx;
			_lafPnl.add(_lafCmb, gbc);

			gbc.gridx = 0;
			++gbc.gridy;
			_lafPnl.add(new JLabel(LAFPreferencesPanelI18n.LAF_LOC, SwingConstants.RIGHT), gbc);

			++gbc.gridx;
			_lafPnl.add(new OutputLabel(_plugin.getLookAndFeelFolder().getAbsolutePath()), gbc);

			return _lafPnl;
		}

		private JPanel createSettingsPanel()
		{
			JPanel pnl  = new JPanel(new GridBagLayout());
			pnl.setBorder(BorderFactory.createTitledBorder(s_stringMgr.getString("laf.general")));

			final GridBagConstraints gbc = new GridBagConstraints();
			gbc.weightx = 1;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.insets = new Insets(4, 4, 4, 4);
			gbc.anchor = GridBagConstraints.WEST;

			gbc.gridx = 0;
			gbc.gridy = 0;
			pnl.add(_allowSetBorder, gbc);


			return pnl;
		}

		private void updateLookAndFeelConfigControl()
		{
			if (_curLAFConfigComp != null)
			{
				_lafPnl.remove(_curLAFConfigComp);
				_curLAFConfigComp = null;
			}

			UIManager.LookAndFeelInfo lafInfo = _lafCmb.getSelectedLookAndFeel();
			if (lafInfo != null)
			{
				final String selLafClassName = lafInfo.getClassName();
				if (selLafClassName != null)
				{
					ILookAndFeelController ctrl = _lafRegister.getLookAndFeelController(selLafClassName);
					if (ctrl != null)
					{
						_curLAFConfigComp = ctrl.getPreferencesComponent();
						if (_curLAFConfigComp != null)
						{
							_curLAFConfigComp.loadPreferencesPanel();
							final GridBagConstraints gbc = new GridBagConstraints();
							gbc.fill = GridBagConstraints.HORIZONTAL;
							gbc.insets = new Insets(4, 4, 4, 4);
							gbc.gridx = 0;
							gbc.gridy = GridBagConstraints.RELATIVE;
							gbc.gridwidth = GridBagConstraints.REMAINDER;
							_lafPnl.add(_curLAFConfigComp, gbc);
						}
					}
					else
					{
						s_log.debug("No ILookAndFeelController found for: " +
											selLafClassName);
					}
				}
			}
			else
			{
				s_log.debug("Selected Look and Feel class is null");
			}
			validate();
		}

		private class LookAndFeelComboListener implements ActionListener
		{
			public void actionPerformed(ActionEvent evt)
			{
				LAFPreferencesPanel.this.updateLookAndFeelConfigControl();
			}
		}

	}
}
