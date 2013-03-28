package net.sourceforge.squirrel_sql.client.session.properties;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.preferences.INewSessionPropertiesPanel;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.gui.IntegerField;
import net.sourceforge.squirrel_sql.fw.gui.MultipleLineLabel;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;


public class SessionObjectTreePropertiesPanel
	implements INewSessionPropertiesPanel, ISessionPropertiesPanel
{

	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(SessionObjectTreePropertiesPanel.class);

    private static boolean _objectTreeRefreshNeeded = false;
    
	
	private final IApplication _app;

	
	private final ObjectTreepropsPanel _myPanel;
	private final JScrollPane _scrolledMyPanel;

	
	private SessionProperties _props;

	
	public SessionObjectTreePropertiesPanel(IApplication app)
		throws IllegalArgumentException
	{
		super();
		if (app == null)
		{
			throw new IllegalArgumentException("Null IApplication passed");
		}
		_app = app;
		_myPanel = new ObjectTreepropsPanel(app);
      _scrolledMyPanel = new JScrollPane(_myPanel);
   }

	
	public void initialize(IApplication app)
	{
		_props = _app.getSquirrelPreferences().getSessionProperties();
		_myPanel.loadData(_props);
	}

	public void initialize(IApplication app, ISession session)
		throws IllegalArgumentException
	{
		if (session == null)
		{
			throw new IllegalArgumentException("Null ISession passed");
		}
		_props = session.getProperties();
		_myPanel.loadData(_props);
	}

	public Component getPanelComponent()
	{
		return _scrolledMyPanel;
	}

	public String getTitle()
	{
		return ObjectTreepropsPanel.i18n.OBJECT_TREE;
	}

	public String getHint()
	{
		return ObjectTreepropsPanel.i18n.OBJECT_TREE;
	}

	public void applyChanges()
	{
		_myPanel.applyChanges(_props);
	}
    
	
    public void setObjectTreeRefreshNeeded(boolean objectTreeRefreshNeeded) {
        _objectTreeRefreshNeeded = objectTreeRefreshNeeded;
    }

    
    public boolean isObjectTreeRefreshNeeded() {
        return _objectTreeRefreshNeeded;
    }

    private static final class ObjectTreepropsPanel extends JPanel
	{
		
		interface i18n
		{
			
			String CATALOG_PREFIX = s_stringMgr.getString("sessionPropertiesPanel.catalogPrefix");
			
			String LIMIT_ROWS_CONTENTS = s_stringMgr.getString("sessionPropertiesPanel.limitRowsContents");
			
			String SCHEMA_PREFIX = s_stringMgr.getString("sessionPropertiesPanel.schemaPrefix");
			
			String SHOW_ROW_COUNT = s_stringMgr.getString("sessionPropertiesPanel.showRowCount");
			
			String OBJECT_TREE = s_stringMgr.getString("sessionPropertiesPanel.objectTree");
		}

		private IntegerField _contentsNbrRowsToShowField = new IntegerField(5);
		private JCheckBox _contentsLimitRowsChk = new JCheckBox(i18n.LIMIT_ROWS_CONTENTS);
		private JCheckBox _showRowCountChk = new JCheckBox(i18n.SHOW_ROW_COUNT);
      private JTextField _catalogFilterInclude = new JTextField();
      private JTextField _catalogFilterExclude = new JTextField();
		private JTextField _schemaFilterInclude = new JTextField();
		private JTextField _schemaFilterExclude = new JTextField();
		private JTextField _objectFilterInclude = new JTextField();
		private JTextField _objectFilterExclude = new JTextField();
		
		private JCheckBox _loadSchemasCatalogsChk = new JCheckBox(s_stringMgr.getString("sessionPropertiesPanel.loadSchemasCatalogs"));

		
		private final ControlMediator _controlMediator = new ControlMediator();

		ObjectTreepropsPanel(IApplication app)
		{
			super();
			createGUI();
		}

		void loadData(SessionProperties props)
		{
			_contentsNbrRowsToShowField.setInt(props.getContentsNbrRowsToShow());
			_contentsLimitRowsChk.setSelected(props.getContentsLimitRows());
			_showRowCountChk.setSelected(props.getShowRowCount());
			_loadSchemasCatalogsChk.setSelected(props.getLoadSchemasCatalogs());
         _catalogFilterInclude.setText(props.getCatalogFilterInclude());
         _schemaFilterInclude.setText(props.getSchemaFilterInclude());
         _objectFilterInclude.setText(props.getObjectFilterInclude());
         _catalogFilterExclude.setText(props.getCatalogFilterExclude());
         _schemaFilterExclude.setText(props.getSchemaFilterExclude());
         _objectFilterExclude.setText(props.getObjectFilterExclude());

			updateControlStatus();
		}

		void applyChanges(SessionProperties props)
		{
			props.setContentsNbrRowsToShow(_contentsNbrRowsToShowField.getInt());
			props.setContentsLimitRows(_contentsLimitRowsChk.isSelected());

         final boolean oldShowRowCount = props.getShowRowCount();
         final boolean newShowRowCount = _showRowCountChk.isSelected();
         props.setShowRowCount(newShowRowCount);
         
         

         final boolean oldLoadSchemasCatalogs = props.getLoadSchemasCatalogs();
         final boolean newLoadSchemasCatalogs = _loadSchemasCatalogsChk.isSelected();
         props.setLoadSchemasCatalogs(newLoadSchemasCatalogs);

         final String oldSchemaFilterInclude = props.getSchemaFilterInclude();
         final String oldCatalogFilterInclude = props.getCatalogFilterInclude();
         final String oldObjectFilterInclude = props.getObjectFilterInclude();
         final String oldSchemaFilterExclude = props.getSchemaFilterInclude();
         final String oldCatalogFilterExclude = props.getCatalogFilterInclude();
         final String oldObjectFilterExclude = props.getObjectFilterInclude();
         final String newSchemaFilterInclude = _schemaFilterInclude.getText();
         final String newCatalogFilterInclude = _catalogFilterInclude.getText();
         final String newObjectFilterInclude = _objectFilterInclude.getText();
         final String newSchemaFilterExclude = _schemaFilterExclude.getText();
         final String newCatalogFilterExclude = _catalogFilterExclude.getText();
         final String newObjectFilterExclude = _objectFilterExclude.getText();
         props.setCatalogFilterInclude(newCatalogFilterInclude);
         props.setSchemaFilterInclude(newSchemaFilterInclude);
         props.setObjectFilterInclude(newObjectFilterInclude);
         props.setCatalogFilterExclude(newCatalogFilterExclude);
         props.setSchemaFilterExclude(newSchemaFilterExclude);
         props.setObjectFilterExclude(newObjectFilterExclude);


         _objectTreeRefreshNeeded = false;
         if (
               oldLoadSchemasCatalogs != newLoadSchemasCatalogs ||
               oldShowRowCount != newShowRowCount ||

              !StringUtilities.areStringsEqual(oldCatalogFilterInclude, newCatalogFilterInclude) ||
              !StringUtilities.areStringsEqual(oldSchemaFilterInclude, newSchemaFilterInclude) ||
              !StringUtilities.areStringsEqual(oldObjectFilterInclude, newObjectFilterInclude) ||

              !StringUtilities.areStringsEqual(oldCatalogFilterExclude, newCatalogFilterExclude) ||
              !StringUtilities.areStringsEqual(oldSchemaFilterExclude, newSchemaFilterExclude) ||
              !StringUtilities.areStringsEqual(oldObjectFilterExclude, newObjectFilterExclude)
            )
         {
            _objectTreeRefreshNeeded = true;
         }
      }

		private void updateControlStatus()
		{
			_contentsNbrRowsToShowField.setEnabled(_contentsLimitRowsChk.isSelected());
		}

		private void createGUI()
		{
			setLayout(new GridBagLayout());

         GridBagConstraints gbc;

         gbc = new GridBagConstraints(0,0,1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(4,4,4,4), 0,0);
			add(createObjectTreePanel(), gbc);

         gbc = new GridBagConstraints(0,1,1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(4,4,4,4), 0,0);
			add(createFilterPanel(), gbc);


         gbc = new GridBagConstraints(0,2,1,1,1,1, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(4,4,4,4), 0,0);
			add(new JPanel(), gbc);
		}

		private JPanel createObjectTreePanel()
		{
			final JPanel pnl = new JPanel(new GridBagLayout());
			pnl.setBorder(BorderFactory.createTitledBorder(i18n.OBJECT_TREE));
			final GridBagConstraints gbc = new GridBagConstraints();
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.insets = new Insets(4, 4, 4, 4);
			gbc.anchor = GridBagConstraints.CENTER;

			_contentsLimitRowsChk.addChangeListener(_controlMediator);

			_contentsNbrRowsToShowField.setColumns(5);

			gbc.gridwidth = GridBagConstraints.REMAINDER;
			gbc.gridx = 0;
			gbc.gridy = 0;
			pnl.add(_loadSchemasCatalogsChk, gbc);

			++gbc.gridy; 
			gbc.gridx = 0;
			pnl.add(_showRowCountChk, gbc);

			++gbc.gridy; 
			gbc.gridx = 0;
			gbc.gridwidth = 2;
			pnl.add(_contentsLimitRowsChk, gbc);
			gbc.gridwidth = 1;
			gbc.gridx+=2;
			pnl.add(_contentsNbrRowsToShowField, gbc);
			++gbc.gridx;
			gbc.gridwidth = GridBagConstraints.REMAINDER;
			
			pnl.add(new JLabel(s_stringMgr.getString("generalPropertiesPanel.rows")), gbc);

			return pnl;
		}
		private JPanel createFilterPanel()
		{
			final JPanel pnl = new JPanel(new GridBagLayout());
			
			pnl.setBorder(BorderFactory.createTitledBorder(s_stringMgr.getString("sessionObjectTreePropetiesPanel.filters")));

			final GridBagConstraints gbc = new GridBagConstraints();
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.anchor = GridBagConstraints.WEST;
			gbc.insets = new Insets(4, 4, 4, 4);
			gbc.weightx = 1.0;

			gbc.gridx = 0;
			gbc.gridy = 0;
         gbc.insets = new Insets(4, 4, 4, 4);
         pnl.add(new MultipleLineLabel(s_stringMgr.getString("SessionObjectTreePropertiesPanel.filterNote")), gbc);

         ++gbc.gridy;
         pnl.add(createIncludeExcludePanel(), gbc);

			return pnl;
		}

      private JPanel createIncludeExcludePanel()
      {
         JPanel pnl = new JPanel(new GridBagLayout());
         GridBagConstraints gbc;

         gbc = new GridBagConstraints(0,0,1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(4,4,0,4),0,0);
         pnl.add(new JLabel(s_stringMgr.getString("SessionObjectTreePropertiesPanel.catalogInclude")), gbc);

         gbc = new GridBagConstraints(1,0,1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(4,4,0,4),0,0);
         pnl.add(new JLabel(s_stringMgr.getString("SessionObjectTreePropertiesPanel.catalogExclude")), gbc);

         gbc = new GridBagConstraints(0,1,1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(4,4,4,4),0,0);
         pnl.add(_catalogFilterInclude, gbc);

         gbc = new GridBagConstraints(1,1,1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(4,4,4,4),0,0);
         pnl.add(_catalogFilterExclude, gbc);


         gbc = new GridBagConstraints(0,2,1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(4,4,0,4),0,0);
         pnl.add(new JLabel(s_stringMgr.getString("SessionObjectTreePropertiesPanel.schemaInclude")), gbc);

         gbc = new GridBagConstraints(1,2,1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(4,4,0,4),0,0);
         pnl.add(new JLabel(s_stringMgr.getString("SessionObjectTreePropertiesPanel.schemaExclude")), gbc);

         gbc = new GridBagConstraints(0,3,1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(4,4,4,4),0,0);
         pnl.add(_schemaFilterInclude, gbc);

         gbc = new GridBagConstraints(1,3,1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(4,4,4,4),0,0);
         pnl.add(_schemaFilterExclude, gbc);


         gbc = new GridBagConstraints(0,4,1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(4,4,0,4),0,0);
         pnl.add(new JLabel(s_stringMgr.getString("SessionObjectTreePropertiesPanel.objectInclude")), gbc);

         gbc = new GridBagConstraints(1,4,1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(4,4,0,4),0,0);
         pnl.add(new JLabel(s_stringMgr.getString("SessionObjectTreePropertiesPanel.objectExclude")), gbc);

         gbc = new GridBagConstraints(0,5,1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(4,4,4,4),0,0);
         pnl.add(_objectFilterInclude, gbc);

         gbc = new GridBagConstraints(1,5,1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(4,4,4,4),0,0);
         pnl.add(_objectFilterExclude, gbc);


         gbc = new GridBagConstraints(0,6,1,1,1,1, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(4,4,4,4),0,0);
         pnl.add(new JPanel(), gbc);
         gbc = new GridBagConstraints(1,6,1,1,1,1, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(4,4,4,4),0,0);
         pnl.add(new JPanel(), gbc);

         return pnl;

      }

      
		private final class ControlMediator implements ChangeListener,
															ActionListener
		{
			public void stateChanged(ChangeEvent evt)
			{
				updateControlStatus();
			}

			public void actionPerformed(ActionEvent evt)
			{
				updateControlStatus();
			}
		}
	}
}
