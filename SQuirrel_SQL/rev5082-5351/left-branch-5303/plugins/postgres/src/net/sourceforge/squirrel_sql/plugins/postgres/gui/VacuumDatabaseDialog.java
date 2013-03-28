package net.sourceforge.squirrel_sql.plugins.postgres.gui;



import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JTextField;

import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

public class VacuumDatabaseDialog extends AbstractPostgresDialog
{

	private static final long serialVersionUID = 1L;

	
	protected String _catalogName;

	
	protected JCheckBox _fullCheckBox;

	protected JCheckBox _analyzeCheckBox;

	
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(VacuumDatabaseDialog.class);

	static interface i18n
	{
		String TITLE = s_stringMgr.getString("VacuumDatabaseDialog.title");

		String CATALOG_LABEL = s_stringMgr.getString("VacuumDatabaseDialog.catalogLabel");

		String FULL_LABEL = s_stringMgr.getString("VacuumDatabaseDialog.fullLabel");

		String FULL_TOOLTIP = s_stringMgr.getString("VacuumDatabaseDialog.fullTooltip");

		String ANALYZE_LABEL = s_stringMgr.getString("VacuumDatabaseDialog.analyzeLabel");

		String ANALYZE_TOOLTIP = s_stringMgr.getString("VacuumDatabaseDialog.analyzeTooltip");
	}

	public VacuumDatabaseDialog(String catalogName)
	{
		_catalogName = catalogName;
		setTitle(VacuumDatabaseDialog.i18n.TITLE);
		init();
	}

	protected void init()
	{
		defaultInit();

		
		JLabel catalogLabel = getBorderedLabel(VacuumDatabaseDialog.i18n.CATALOG_LABEL + " ", _emptyBorder);
		_panel.add(catalogLabel, getLabelConstraints(_gbc));

		JTextField catalogTextField = getSizedTextField(_mediumField);
		catalogTextField.setEditable(false);
		if (_catalogName != null) catalogTextField.setText(_catalogName);
		_panel.add(catalogTextField, getFieldConstraints(_gbc));

		
		
		JLabel fullLabel = new JLabel(VacuumDatabaseDialog.i18n.FULL_LABEL);
		fullLabel.setBorder(_emptyBorder);
		_panel.add(fullLabel, getLabelConstraints(_gbc));

		_fullCheckBox = new JCheckBox();
		_fullCheckBox.setToolTipText(VacuumDatabaseDialog.i18n.FULL_TOOLTIP);
		_fullCheckBox.setPreferredSize(_mediumField);
		_panel.add(_fullCheckBox, getFieldConstraints(_gbc));

		
		JLabel analyzeLabel = new JLabel(VacuumDatabaseDialog.i18n.ANALYZE_LABEL);
		analyzeLabel.setBorder(_emptyBorder);
		_panel.add(analyzeLabel, getLabelConstraints(_gbc));

		_analyzeCheckBox = new JCheckBox();
		_analyzeCheckBox.setToolTipText(VacuumDatabaseDialog.i18n.ANALYZE_TOOLTIP);
		_analyzeCheckBox.setPreferredSize(_mediumField);
		_panel.add(_analyzeCheckBox, getFieldConstraints(_gbc));
	}

	public boolean getFullOption()
	{
		return _fullCheckBox.isSelected();
	}

	public boolean getAnalyzeOption()
	{
		return _analyzeCheckBox.isSelected();
	}
}
