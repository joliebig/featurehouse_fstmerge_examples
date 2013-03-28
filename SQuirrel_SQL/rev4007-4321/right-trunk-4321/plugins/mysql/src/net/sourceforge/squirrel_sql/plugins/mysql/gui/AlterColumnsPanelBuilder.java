package net.sourceforge.squirrel_sql.plugins.mysql.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.jgoodies.forms.layout.FormLayout;

import net.sourceforge.squirrel_sql.fw.gui.IntegerField;
import net.sourceforge.squirrel_sql.fw.sql.DataTypeInfo;
import net.sourceforge.squirrel_sql.fw.sql.ISQLConnection;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;

import net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;



import net.sourceforge.squirrel_sql.client.gui.builders.DefaultFormBuilder;
import net.sourceforge.squirrel_sql.client.gui.controls.ColumnsComboBox;
import net.sourceforge.squirrel_sql.client.gui.controls.DataTypesComboBox;
import net.sourceforge.squirrel_sql.client.session.ISession;

class AlterColumnsPanelBuilder
{
	



	
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(AlterTablePanelBuilder.class);

	
	private Map<String, DataTypeInfo> _dataTypesByTypeName;

	
	private ControlMediator _mediator;

	
	private ColumnsComboBox _columnsCmb;

	
	private DataTypesComboBox _dataTypesCmb;

	
	private IntegerField _columnLengthField;

	
	private JTextField _defaultvalue;

	
	private JCheckBox _allowNullChk;

	
	private JCheckBox _autoIncChk;

	
	private JCheckBox _unsignedChk;

	
	private JCheckBox _binaryChk;

	
	private JCheckBox _zeroFillChk;

	AlterColumnsPanelBuilder()
	{
		super();
	}

	public JPanel buildPanel(ISession session, ITableInfo ti)
		throws SQLException
	{
		initComponents(session, ti);

		final FormLayout layout = new FormLayout(
				"12dlu, left:max(40dlu;pref), 3dlu, 75dlu:grow(0.50), 7dlu, 75dlu:grow(0.50), 3dlu",
				"");
		final DefaultFormBuilder builder = new DefaultFormBuilder(layout);
		builder.setDefaultDialogBorder();
		builder.setLeadingColumnOffset(1);

		builder.appendSeparator(getString("AlterColumnsPanelBuilder.selectcolumn"));
		builder.append(getString("AlterColumnsPanelBuilder.columnname"), _columnsCmb, 3);

		builder.appendSeparator(getString("AlterColumnsPanelBuilder.attributes"));
		builder.append(getString("AlterColumnsPanelBuilder.datatype"), _dataTypesCmb, 3);

		builder.nextLine();
		builder.append(getString("AlterColumnsPanelBuilder.length"), _columnLengthField, 3);

		builder.nextLine();
		builder.append(getString("AlterColumnsPanelBuilder.default"), _defaultvalue, 3);

		builder.nextLine();
		builder.setLeadingColumnOffset(3);
		builder.append(_unsignedChk);
		builder.append(_autoIncChk);

		builder.nextLine();
		builder.append(_binaryChk);
		builder.append(_zeroFillChk);

		builder.nextLine();
		builder.append(_allowNullChk);
		builder.setLeadingColumnOffset(1);

		return builder.getPanel();
	}

	private static String getString(String stringMgrKey)
	{
		return s_stringMgr.getString(stringMgrKey);
	}

	private void updateControlStatus()
	{
		final TableColumnInfo tci = _columnsCmb.getSelectedColumn();
		_dataTypesCmb.setSelectedItem(_dataTypesByTypeName.get(tci.getTypeName().toUpperCase()));



		_columnLengthField.setInt(tci.getColumnSize());
		_defaultvalue.setText(tci.getDefaultValue());































	}

	private void initComponents(ISession session, ITableInfo ti)
		throws SQLException
	{
		_dataTypesByTypeName = new HashMap<String, DataTypeInfo>();
		_mediator = new ControlMediator();

		final ISQLConnection conn = session.getSQLConnection();

		_columnsCmb = new ColumnsComboBox(conn, ti);

		_dataTypesCmb = new DataTypesComboBox(conn);
		for (int i = 0, limit = _dataTypesCmb.getItemCount(); i < limit; ++i)
		{
			DataTypeInfo dti = _dataTypesCmb.getDataTypeAt(i);
			_dataTypesByTypeName.put(dti.getSimpleName().toUpperCase(), dti);
		}

		_columnLengthField = new IntegerField();
		_defaultvalue = new JTextField();
		_allowNullChk = new JCheckBox(getString("AlterColumnsPanelBuilder.allownull"));
		_unsignedChk = new JCheckBox(getString("AlterColumnsPanelBuilder.unsigned"));
		_autoIncChk = new JCheckBox(getString("AlterColumnsPanelBuilder.autoinc"));
		_binaryChk = new JCheckBox(getString("AlterColumnsPanelBuilder.binary"));
		_zeroFillChk = new JCheckBox(getString("AlterColumnsPanelBuilder.zerofill"));

























		_columnsCmb.addActionListener(_mediator);


















		updateControlStatus();
	}

	
	private final class ControlMediator implements ActionListener
	{
		public void actionPerformed(ActionEvent evt)
		{
			updateControlStatus();
		}
	}

}
