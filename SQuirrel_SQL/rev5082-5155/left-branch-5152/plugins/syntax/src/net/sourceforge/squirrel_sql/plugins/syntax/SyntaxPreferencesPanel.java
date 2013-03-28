package net.sourceforge.squirrel_sql.plugins.syntax;

import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.gui.MultipleLineLabel;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.preferences.INewSessionPropertiesPanel;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.properties.ISessionPropertiesPanel;

import net.sourceforge.squirrel_sql.plugins.syntax.prefspanel.StyleMaintenancePanel;
import net.sourceforge.squirrel_sql.plugins.syntax.prefspanel.StylesList;

public class SyntaxPreferencesPanel
	implements INewSessionPropertiesPanel, ISessionPropertiesPanel
{
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(SyntaxPreferencesPanel.class);


	
	private static final ILogger s_log =
		LoggerController.createLogger(SyntaxPreferencesPanel.class);

	
	private final SyntaxPreferences _prefs;

	
	private final MyPanel _myPanel;

	
	public SyntaxPreferencesPanel(SyntaxPreferences prefs, SyntaxPluginResources rsrc)
	{
		super();
		if (prefs == null)
		{
			throw new IllegalArgumentException("Null SyntaxPreferences passed");
		}
		_prefs = prefs;

		
		_myPanel = new MyPanel(prefs, rsrc);
	}

	
	public void initialize(IApplication app)
	{
		_myPanel.loadData(_prefs);
	}

	
	public void initialize(IApplication app, ISession session)
	{
		if (app == null)
		{
			throw new IllegalArgumentException("Null IApplication passed");
		}
		if (session == null)
		{
			throw new IllegalArgumentException("Null ISession passed");
		}

		_myPanel.loadData(_prefs);
	}

	
	public Component getPanelComponent()
	{
		return _myPanel;
	}

	
	public void applyChanges()
	{
		_myPanel.applyChanges(_prefs);
	}

	
	public String getTitle()
	{
		return MyPanel.i18n.TAB_TITLE;
	}

	
	public String getHint()
	{
		return MyPanel.i18n.TAB_HINT;
	}

	
	private final static class MyPanel extends JPanel
	{
      
		interface i18n
		{
			
			String TAB_TITLE = s_stringMgr.getString("syntax.prefSyntax");
			
			String TAB_HINT = s_stringMgr.getString("syntax.prefSyntaxHint");
			
         String NETBEANS = s_stringMgr.getString("syntax.prefUseNetbeans");
			
			String OSTER = s_stringMgr.getString("syntax.prefUseOster");
			
         String PLAIN = s_stringMgr.getString("syntax.prefUsePlain");

         
         String TEXT_LIMIT_LINE_VISIBLE = s_stringMgr.getString("syntax.textLimitLineVisible");
         
         String TEXT_LIMIT_LINE_WIDTH = s_stringMgr.getString("syntax.textLimitLineWidth");

      }

      private final JRadioButton _netbeansActiveOpt  = new JRadioButton(i18n.NETBEANS);
		private final JRadioButton _osterActiveOpt = new JRadioButton(i18n.OSTER);
      private final JRadioButton _plainActiveOpt  = new JRadioButton(i18n.PLAIN);

      private final JCheckBox _chkTextLimitLineVisible = new JCheckBox(i18n.TEXT_LIMIT_LINE_VISIBLE);
      private final JTextField _txtTextLimitLineWidth = new JTextField();


      private StylesListSelectionListener _listLis;


		private final StylesList _stylesList = new StylesList();

		private StyleMaintenancePanel _styleMaintPnl;

		MyPanel(SyntaxPreferences prefs, SyntaxPluginResources rsrc)
		{
			super();
			createUserInterface(prefs, rsrc);
		}

		
		public void addNotify()
		{
			super.addNotify();

			if (_listLis == null)
			{
				_listLis = new StylesListSelectionListener();
				_stylesList.addListSelectionListener(_listLis);
			}
		}

		
		public void removeNotify()
		{
			super.removeNotify();
			if (_listLis != null)
			{
				_stylesList.removeListSelectionListener(_listLis);
				_listLis = null;
			}
		}

		void loadData(SyntaxPreferences prefs)
		{
			_osterActiveOpt.setSelected(prefs.getUseOsterTextControl());
         _netbeansActiveOpt.setSelected(prefs.getUseNetbeansTextControl());
         _plainActiveOpt.setSelected(prefs.getUsePlainTextControl());

         _chkTextLimitLineVisible.setSelected(prefs.isTextLimitLineVisible());

         _txtTextLimitLineWidth.setText("" + prefs.getTextLimitLineWidth());

         _stylesList.loadData(prefs);
			_styleMaintPnl.setStyle(_stylesList.getSelectedSyntaxStyle());

			updateControlStatus();
		}


      void applyChanges(SyntaxPreferences prefs)
		{
         boolean oldUseNetbeansTextControl = prefs.getUseNetbeansTextControl();
         boolean oldUseOsterTextControl = prefs.getUseOsterTextControl();
         boolean oldUsePlainTextControl = prefs.getUsePlainTextControl();

         try
         {
            prefs.setUseNetbeansTextControl(_netbeansActiveOpt.isSelected());
            prefs.setUseOsterTextControl(_osterActiveOpt.isSelected());
            prefs.setUsePlainTextControl(_plainActiveOpt.isSelected());
         }
         catch (SyntaxPrefChangeNotSupportedException e)
         {
            prefs.setUseNetbeansTextControl(oldUseNetbeansTextControl);
            prefs.setUseOsterTextControl(oldUseOsterTextControl);
            prefs.setUsePlainTextControl(oldUsePlainTextControl);
         }

         prefs.setTextLimitLineVisible(_chkTextLimitLineVisible.isSelected());

         int limit = 80;

         try
         {
            int buf = Integer.parseInt(_txtTextLimitLineWidth.getText());

            if(0 < buf && buf < 1000)
            {
               limit = buf;
            }
            else
            {
               s_log.error("Invalid text limit widht: " + _txtTextLimitLineWidth.getText());
            }
         }
         catch (NumberFormatException e)
         {
            s_log.error("Invalid text limit widht: " + _txtTextLimitLineWidth.getText(), e);
         }

         prefs.setTextLimitLineWidth(limit);

         prefs.setColumnStyle(_stylesList.getSyntaxStyleAt(StylesList.IStylesListIndices.COLUMNS));
			prefs.setCommentStyle(_stylesList.getSyntaxStyleAt(StylesList.IStylesListIndices.COMMENTS));
			prefs.setErrorStyle(_stylesList.getSyntaxStyleAt(StylesList.IStylesListIndices.ERRORS));
			prefs.setFunctionStyle(_stylesList.getSyntaxStyleAt(StylesList.IStylesListIndices.FUNCTIONS));
			prefs.setIdentifierStyle(_stylesList.getSyntaxStyleAt(StylesList.IStylesListIndices.IDENTIFIERS));
			prefs.setLiteralStyle(_stylesList.getSyntaxStyleAt(StylesList.IStylesListIndices.LITERALS));
			prefs.setOperatorStyle(_stylesList.getSyntaxStyleAt(StylesList.IStylesListIndices.OPERATORS));
			prefs.setReservedWordStyle(_stylesList.getSyntaxStyleAt(StylesList.IStylesListIndices.RESERVED_WORDS));
			prefs.setSeparatorStyle(_stylesList.getSyntaxStyleAt(StylesList.IStylesListIndices.SEPARATORS));
			prefs.setTableStyle(_stylesList.getSyntaxStyleAt(StylesList.IStylesListIndices.TABLES));
			prefs.setWhiteSpaceStyle(_stylesList.getSyntaxStyleAt(StylesList.IStylesListIndices.WHITE_SPACE));
			prefs.setDataTypeStyle(_stylesList.getSyntaxStyleAt(StylesList.IStylesListIndices.DATA_TYPES));
		}

		private void updateControlStatus()
		{
			final boolean useOsterControl = _osterActiveOpt.isSelected();
         final boolean useNetbeansControl = _netbeansActiveOpt.isSelected();
         final boolean usePlainControl = _plainActiveOpt.isSelected();

			_stylesList.setEnabled(useOsterControl || useNetbeansControl);
			_styleMaintPnl.setEnabled(useOsterControl || useNetbeansControl);

         _chkTextLimitLineVisible.setEnabled(useNetbeansControl);
         _txtTextLimitLineWidth.setEnabled(useNetbeansControl);

         if(useNetbeansControl)
         {
            _txtTextLimitLineWidth.setEnabled(_chkTextLimitLineVisible.isSelected());
         }
      }

		private void createUserInterface(SyntaxPreferences prefs,
											SyntaxPluginResources rsrc)
		{
			setLayout(new GridBagLayout());
			GridBagConstraints gbc;

         ButtonGroup bg = new ButtonGroup();
         bg.add(_netbeansActiveOpt);
         bg.add(_osterActiveOpt);
         bg.add(_plainActiveOpt);

			_osterActiveOpt.addChangeListener(new ChangeListener()
			{
				public void stateChanged(ChangeEvent evt)
				{
					updateControlStatus();
				}
			});

         _netbeansActiveOpt.addChangeListener(new ChangeListener()
         {
            public void stateChanged(ChangeEvent evt)
            {
               updateControlStatus();
            }
         });

         _plainActiveOpt.addChangeListener(new ChangeListener()
         {
            public void stateChanged(ChangeEvent evt)
            {
               updateControlStatus();
            }
         });

         _chkTextLimitLineVisible.addActionListener(new ActionListener()
         {
            public void actionPerformed(ActionEvent e)
            {
               updateControlStatus();
            }
         });



			
			
			
			
			
			
			
			
			
			
			
			
			String text = s_stringMgr.getString("syntax.osterExplain");
         gbc = new GridBagConstraints(0,0,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(5,5,5,5), 0,0);
         add(new MultipleLineLabel(text), gbc);

         gbc = new GridBagConstraints(0,1,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,0,5,5), 0,0);
         add(createOptionsPanel(), gbc);

         gbc = new GridBagConstraints(1,0,1,2,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,5,5), 0,0);
			add(createStylePanel(rsrc), gbc);

         gbc = new GridBagConstraints(0,2,2,1,1,1,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(0,0,0,0), 0,0);
			add(new JPanel(), gbc);

		}

      private JPanel createOptionsPanel()
      {
         JPanel pnlRet = new JPanel(new GridBagLayout());

         GridBagConstraints gbc;

         gbc = new GridBagConstraints(0,0,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,5,5), 0,0);
         pnlRet.add(_netbeansActiveOpt, gbc);

         gbc = new GridBagConstraints(0,1,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0,5,5,5), 0,0);
         pnlRet.add(_osterActiveOpt, gbc);

         gbc = new GridBagConstraints(0,2,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0,5,5,5), 0,0);
         pnlRet.add(_plainActiveOpt, gbc);

         JPanel pnlLineLimit = new JPanel(new GridBagLayout());
         gbc = new GridBagConstraints(0,3,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,5,5), 0,0);
         pnlRet.add(pnlLineLimit, gbc);


         gbc = new GridBagConstraints(0,0,1,1,0,0,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0,0,5,5), 0,0);
         pnlLineLimit.add(_chkTextLimitLineVisible, gbc);

         gbc = new GridBagConstraints(1,0,1,1,0,0,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0,5,5,5), 0,0);
         pnlLineLimit.add(new JLabel(i18n.TEXT_LIMIT_LINE_WIDTH), gbc);

         _txtTextLimitLineWidth.setColumns(3);
         gbc = new GridBagConstraints(2,0,1,1,0,0,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0,0,5,5), 0,0);
         pnlLineLimit.add(_txtTextLimitLineWidth, gbc);

         return pnlRet;
      }


      private JPanel createStylePanel(SyntaxPluginResources rsrc)
      {
         JPanel pnl = new JPanel(new BorderLayout());
         
         pnl.setBorder(BorderFactory.createTitledBorder(s_stringMgr.getString("syntax.styles")));

         _styleMaintPnl = new StyleMaintenancePanel(_stylesList, rsrc);

         pnl.add(_styleMaintPnl, BorderLayout.NORTH);
         pnl.add(_stylesList, BorderLayout.CENTER);

         return pnl;
      }


		
		private class StylesListSelectionListener implements ListSelectionListener
		{
			public void valueChanged(ListSelectionEvent evt)
			{
				_styleMaintPnl.setStyle(((StylesList)evt.getSource()).getSelectedSyntaxStyle());
			}
		}
	}
}
