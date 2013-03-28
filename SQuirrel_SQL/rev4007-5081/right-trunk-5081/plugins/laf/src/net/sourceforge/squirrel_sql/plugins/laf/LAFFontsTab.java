package net.sourceforge.squirrel_sql.plugins.laf;

import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.sourceforge.squirrel_sql.fw.gui.FontChooser;
import net.sourceforge.squirrel_sql.fw.gui.FontInfo;
import net.sourceforge.squirrel_sql.fw.gui.MultipleLineLabel;
import net.sourceforge.squirrel_sql.fw.gui.OutputLabel;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.StringManager;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.preferences.IGlobalPreferencesPanel;

class LAFFontsTab implements IGlobalPreferencesPanel
{
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(LAFFontsTab.class);


	
	private static final ILogger s_log =
		LoggerController.createLogger(LAFFontsTab.class);

	
	private LAFPlugin _plugin;

	
	

	
	private LAFRegister _lafRegister;

	
	private FontSelectionPanel _myPanel;

	
	

	
	public LAFFontsTab(LAFPlugin plugin, LAFRegister lafRegister)
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
		
		((FontSelectionPanel)getPanelComponent()).loadData();
	}

   public void uninitialize(IApplication app)
   {
   }

   
	public synchronized Component getPanelComponent()
	{
		if (_myPanel == null)
		{
			_myPanel = new FontSelectionPanel(_plugin, _lafRegister);
		}
		return _myPanel;
	}

	
	public void applyChanges()
	{
		_myPanel.applyChanges();
	}

	
	public String getTitle()
	{
		return FontSelectionPanel.i18n.TAB_TITLE;
	}

	
	public String getHint()
	{
		return FontSelectionPanel.i18n.TAB_HINT;
	}

	
	private static final class FontSelectionPanel extends JPanel
	{
		
		interface i18n
		{
			
			String LAF_WARNING =	s_stringMgr.getString("laf.warning");
			
			String TAB_TITLE = s_stringMgr.getString("laf.tabTitle");
			
			String TAB_HINT = s_stringMgr.getString("laf.tabHint");
		}

		
		private FontButton _menuFontBtn;

		
		private FontButton _staticFontBtn;

		
		private FontButton _statusBarFontBtn;

		
		private FontButton _otherFontBtn;

		private JLabel _menuFontLbl = new OutputLabel(" ");
		private JLabel _staticFontLbl = new OutputLabel(" ");
		private JLabel _statusBarFontLbl = new OutputLabel(" ");
		private JLabel _otherFontLbl = new OutputLabel(" ");

		
		private JCheckBox _menuFontEnabledChk = new JCheckBox(s_stringMgr.getString("laf.menuFontEanbled"));
		
		private JCheckBox _staticFontEnabledChk = new JCheckBox(s_stringMgr.getString("laf.staticFontEanbled"));
		
		private JCheckBox _statusBarFontEnabledChk = new JCheckBox(s_stringMgr.getString("laf.statusFontEanbled"));
		
		private JCheckBox _otherFontEnabledChk = new JCheckBox(s_stringMgr.getString("laf.otherFontEanbled"));

		private LAFPlugin _plugin;
		private LAFRegister _lafRegister;

		private LAFPreferences _prefs;

		FontSelectionPanel(LAFPlugin plugin, LAFRegister lafRegister)
		{
			super();
			_plugin = plugin;
			_lafRegister = lafRegister;
			_prefs = _plugin.getLAFPreferences();
			createUserInterface();
		}

		void loadData()
		{
			_menuFontEnabledChk.setSelected(_prefs.isMenuFontEnabled());
			_staticFontEnabledChk.setSelected(_prefs.isStaticFontEnabled());
			_statusBarFontEnabledChk.setSelected(
				_prefs.isStatusBarFontEnabled());
			_otherFontEnabledChk.setSelected(_prefs.isOtherFontEnabled());

			FontInfo fi = _prefs.getMenuFontInfo();
			_menuFontLbl.setText(fi != null ? fi.toString() : "");
			fi = _prefs.getStaticFontInfo();
			_staticFontLbl.setText(fi != null ? fi.toString() : "");
			fi = _prefs.getStatusBarFontInfo();
			_statusBarFontLbl.setText(fi != null ? fi.toString() : "");
			fi = _prefs.getOtherFontInfo();
			_otherFontLbl.setText(fi != null ? fi.toString() : "");

			_menuFontBtn.setEnabled(_prefs.isMenuFontEnabled());
			_staticFontBtn.setEnabled(_prefs.isStaticFontEnabled());
			_statusBarFontBtn.setEnabled(_prefs.isStatusBarFontEnabled());
			_otherFontBtn.setEnabled(_prefs.isOtherFontEnabled());
		}

		void applyChanges()
		{
			_prefs.setMenuFontInfo(_menuFontBtn.getFontInfo());
			_prefs.setStaticFontInfo(_staticFontBtn.getFontInfo());
			_prefs.setStatusBarFontInfo(_statusBarFontBtn.getFontInfo());
			_prefs.setOtherFontInfo(_otherFontBtn.getFontInfo());

			_prefs.setMenuFontEnabled(_menuFontEnabledChk.isSelected());
			_prefs.setStaticFontEnabled(_staticFontEnabledChk.isSelected());
			_prefs.setStatusBarFontEnabled(
				_statusBarFontEnabledChk.isSelected());
			_prefs.setOtherFontEnabled(_otherFontEnabledChk.isSelected());

			try
			{
				_lafRegister.updateApplicationFonts();
				

			}
			catch (Exception ex)
			{
				s_log.error("Error updating fonts", ex);
			}
		}

		private void createUserInterface()
		{
			setLayout(new GridBagLayout());
			final GridBagConstraints gbc = new GridBagConstraints();
			gbc.anchor = GridBagConstraints.WEST;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.insets = new Insets(4, 4, 4, 4);
			gbc.gridx = 0;
			gbc.gridy = 0;
			add(createFontsPanel(), gbc);

			++gbc.gridy;
			gbc.gridwidth = GridBagConstraints.REMAINDER;
			add(new MultipleLineLabel(i18n.LAF_WARNING), gbc);
		}

		private JPanel createFontsPanel()
		{
			_menuFontBtn =
				
				new FontButton(s_stringMgr.getString("laf.menus"), _menuFontLbl, _prefs.getMenuFontInfo());
			_staticFontBtn =
				
				new FontButton(s_stringMgr.getString("laf.staticText"), _staticFontLbl, _prefs.getStaticFontInfo());
			_statusBarFontBtn =
				
				new FontButton(s_stringMgr.getString("laf.statusBars"), _statusBarFontLbl, _prefs.getStatusBarFontInfo());
			_otherFontBtn =
				
				new FontButton(s_stringMgr.getString("laf.other"), _otherFontLbl, _prefs.getOtherFontInfo());

			FontButtonListener lis = new FontButtonListener();
			_menuFontBtn.addActionListener(lis);
			_staticFontBtn.addActionListener(lis);
			_statusBarFontBtn.addActionListener(lis);
			_otherFontBtn.addActionListener(lis);

			_menuFontEnabledChk.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent evt)
				{
					_menuFontBtn.setEnabled(_menuFontEnabledChk.isSelected());
				}
			});
			_staticFontEnabledChk.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent evt)
				{
					_staticFontBtn.setEnabled(
						_staticFontEnabledChk.isSelected());
				}
			});
			_statusBarFontEnabledChk.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent evt)
				{
					_statusBarFontBtn.setEnabled(
						_statusBarFontEnabledChk.isSelected());
				}
			});
			_otherFontEnabledChk.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent evt)
				{
					_otherFontBtn.setEnabled(_otherFontEnabledChk.isSelected());
				}
			});

			JPanel pnl = new JPanel();
			
			pnl.setBorder(BorderFactory.createTitledBorder(s_stringMgr.getString("laf.tabFonts")));
			pnl.setLayout(new GridBagLayout());
			final GridBagConstraints gbc = new GridBagConstraints();
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.insets = new Insets(4, 4, 4, 4);

			gbc.gridx = 0;
			gbc.gridy = 0;
			pnl.add(_menuFontEnabledChk, gbc);

			++gbc.gridy;
			pnl.add(_staticFontEnabledChk, gbc);

			++gbc.gridy;
			pnl.add(_statusBarFontEnabledChk, gbc);

			++gbc.gridy;
			pnl.add(_otherFontEnabledChk, gbc);

			++gbc.gridx;
			gbc.gridy = 0;
			pnl.add(_menuFontBtn, gbc);

			++gbc.gridy;
			pnl.add(_staticFontBtn, gbc);

			++gbc.gridy;
			pnl.add(_statusBarFontBtn, gbc);

			++gbc.gridy;
			pnl.add(_otherFontBtn, gbc);

			++gbc.gridx;
			gbc.gridy = 0;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.weightx = 1.0;
			pnl.add(_menuFontLbl, gbc);

			++gbc.gridy;
			pnl.add(_staticFontLbl, gbc);

			++gbc.gridy;
			pnl.add(_statusBarFontLbl, gbc);

			++gbc.gridy;
			pnl.add(_otherFontLbl, gbc);

			return pnl;
		}

		private static final class FontButton extends JButton
		{
			private FontInfo _fi;
			private JLabel _lbl;
			private Font _font;
			private boolean _dirty;

			FontButton(String text, JLabel lbl, FontInfo fi)
			{
				super(text);
				_lbl = lbl;
				_fi = fi;
			}

			FontInfo getFontInfo()
			{
				return _fi;
			}

			Font getSelectedFont()
			{
				return _font;
			}

			void setSelectedFont(Font font)
			{
				_font = font;
				if (_fi == null)
				{
					_fi = new FontInfo(font);
				}
				else
				{
					_fi.setFont(font);
				}
				_dirty = true;
			}

			boolean isDirty()
			{
				return _dirty;
			}
		}

		private static final class FontButtonListener implements ActionListener
		{
			public void actionPerformed(ActionEvent evt)
			{
				if (evt.getSource() instanceof FontButton)
				{
					FontButton btn = (FontButton) evt.getSource();
					FontInfo fi = btn.getFontInfo();
					Font font = null;
					if (fi != null)
					{
						font = fi.createFont();
					}
					font = new FontChooser().showDialog(font);
					if (font != null)
					{
						btn.setSelectedFont(font);
						btn._lbl.setText(new FontInfo(font).toString());
					}
				}
			}
		}
	}
}
