package net.sourceforge.squirrel_sql.client.session.sqlfilter;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;
import java.util.SortedSet;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

public class WhereClausePanel implements ISQLFilterPanel
{
    
    private static final StringManager s_stringMgr =
        StringManagerFactory.getStringManager(WhereClausePanel.class);    
    
	
	private SQLFilterClauses _sqlFilterClauses = new SQLFilterClauses();

	
	private WhereClauseSubPanel _myPanel;

	
	public WhereClausePanel(SortedSet<String> columnList, 
	                        Map<String, Boolean> textColumns, 
							String tableName)
		throws IllegalArgumentException
	{
		super();
		_myPanel = new WhereClauseSubPanel(columnList, textColumns, tableName);
	}

	
	public void initialize(SQLFilterClauses sqlFilterClauses)
		throws IllegalArgumentException
	{
		if (sqlFilterClauses == null)
		{
			throw new IllegalArgumentException("Null sqlFilterClauses passed");
		}

		_sqlFilterClauses = sqlFilterClauses;
		_myPanel.loadData(_sqlFilterClauses);
	}

	
	public Component getPanelComponent()
	{
		return _myPanel;
	}

	
	public String getTitle()
	{
		return WhereClauseSubPanel.WhereClauseSubPanelI18n.WHERE_CLAUSE;
	}

	
	public String getHint()
	{
		return WhereClauseSubPanel.WhereClauseSubPanelI18n.HINT;
	}

	
	public void applyChanges()
	{
		_myPanel.applyChanges(_sqlFilterClauses);
	}

	
	private static final class WhereClauseSubPanel extends JPanel
	{
        private static final long serialVersionUID = 1L;

        
		interface WhereClauseSubPanelI18n
		{
		    
		    String COLUMNS = 
		        s_stringMgr.getString("WhereClausePanel.columnLabel");
		    
		    String OPERATORS = 
		        s_stringMgr.getString("WhereClausePanel.operatorsLabel");
		    
		    String VALUE = s_stringMgr.getString("WhereClausePanel.valueLabel");
		    
		    String WHERE_CLAUSE = 
		        s_stringMgr.getString("WhereClausePanel.whereClauseLabel");
		    
		    String HINT = s_stringMgr.getString("WhereClausePanel.hint");
		    
		    String ADD = s_stringMgr.getString("WhereClausePanel.addLabel");
            
            
		    String AND = "AND";                 
		    String OR = "OR";                   
		    String LIKE = "LIKE";               
		    String IN = "IN";                   
		    String IS_NULL = "IS NULL";         
		    String IS_NOT_NULL = "IS NOT NULL"; 
		}

		
		private JComboBox _columnCombo;

		
		private JLabel _columnLabel = new JLabel(WhereClauseSubPanelI18n.COLUMNS);

		
		private OperatorTypeCombo _operatorCombo = new OperatorTypeCombo();

		
		private JLabel _operatorLabel = new JLabel(WhereClauseSubPanelI18n.OPERATORS);

		
		private JTextField _valueField = new JTextField(10);

		
		private JLabel _valueLabel = new JLabel(WhereClauseSubPanelI18n.VALUE);

		
		private AndOrCombo _andOrCombo = new AndOrCombo();

		
		private JLabel _andOrLabel = new JLabel(" ");

		
		private JTextArea _whereClauseArea = new JTextArea(10, 40);

		
		private JButton _addTextButton = new JButton(WhereClauseSubPanelI18n.ADD);

		
		private String _tableName;

		
		private Map<String, Boolean> _textColumns;

		
		WhereClauseSubPanel(SortedSet<String> columnList, 
		                    Map<String, Boolean> textColumns,
						    String tableName)
		{
			_tableName = tableName;
			_columnCombo = new JComboBox(columnList.toArray());
			_textColumns = textColumns;
			createGUI();
		}

		
		void loadData(SQLFilterClauses sqlFilterClauses)
		{
			_whereClauseArea.setText(
				sqlFilterClauses.get(getClauseIdentifier(), _tableName));
		}

		
		void applyChanges(SQLFilterClauses sqlFilterClauses)
		{
			sqlFilterClauses.put(
				getClauseIdentifier(),
				_tableName,
				_whereClauseArea.getText());
		}

		
		private void createGUI()
		{
         setLayout(new GridBagLayout());


         GridBagConstraints gbc;

         gbc = new GridBagConstraints(0,0,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(4,4,4,4),0,0);
         add(createControlsPanel(), gbc);

         _whereClauseArea.setBorder(BorderFactory.createEtchedBorder());
         _whereClauseArea.setLineWrap(true);
         JScrollPane sp = new JScrollPane(_whereClauseArea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                                    JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);


         gbc = new GridBagConstraints(0,1,1,1,1,1,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(4,4,4,4),0,0);
         add(sp, gbc);
		}


      private JPanel createControlsPanel()
      {

         JPanel ret = new JPanel(new GridBagLayout());
         GridBagConstraints gbc;

         gbc = new GridBagConstraints(0,0,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(4,4,4,4),0,0);
         ret.add(_andOrLabel, gbc);

         gbc = new GridBagConstraints(0,1,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(4,4,4,4),0,0);
         ret.add(_andOrCombo, gbc);



         gbc = new GridBagConstraints(1,0,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(4,4,4,4),0,0);
         ret.add(_columnLabel, gbc);

         gbc = new GridBagConstraints(1,1,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(4,4,4,4),0,0);
         ret.add(_columnCombo, gbc);



         gbc = new GridBagConstraints(2,0,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(4,4,4,4),0,0);
         ret.add(_operatorLabel, gbc);

         gbc = new GridBagConstraints(2,1,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(4,4,4,4),0,0);
         ret.add(_operatorCombo, gbc);



         gbc = new GridBagConstraints(3,0,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(4,4,4,4),0,0);
         ret.add(_valueLabel, gbc);

         gbc = new GridBagConstraints(3,1,1,1,1,0,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(4,4,4,4),0,0);
         ret.add(_valueField, gbc);



         gbc = new GridBagConstraints(4,1,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(4,4,4,4),0,0);
         ret.add(_addTextButton, gbc);


         _addTextButton.addActionListener(new ActionListener()
         {
            public void actionPerformed(ActionEvent evt)
            {
               addTextToClause();
            }
         });

         return ret;
      }

      private static final class OperatorTypeCombo extends JComboBox
		{
            private static final long serialVersionUID = 1L;

            OperatorTypeCombo()
			{
				addItem("=");
				addItem("<>");
				addItem(">");
				addItem("<");
				addItem(">=");
				addItem("<=");
				addItem(WhereClauseSubPanelI18n.IN);
				addItem(WhereClauseSubPanelI18n.LIKE);
				addItem(WhereClauseSubPanelI18n.IS_NULL);
				addItem(WhereClauseSubPanelI18n.IS_NOT_NULL);
			}
		}

		private static final class AndOrCombo extends JComboBox
		{
            private static final long serialVersionUID = 1L;

            AndOrCombo()
			{
				addItem(WhereClauseSubPanelI18n.AND);
				addItem(WhereClauseSubPanelI18n.OR);
			}
		}

		
		private void addTextToClause()
		{
			String value = _valueField.getText();
			String operator = (String)_operatorCombo.getSelectedItem();
			if (((value != null) && (value.length() > 0))
					|| ((operator.equals(WhereClauseSubPanelI18n.IS_NULL))
					|| 	(operator.equals(WhereClauseSubPanelI18n.IS_NOT_NULL))))
			{
				String andOr = (String)_andOrCombo.getSelectedItem();
				String column = (String)_columnCombo.getSelectedItem();

				
				
				if (_whereClauseArea.getText().length() > 0)
				{
					_whereClauseArea.append("\n" + andOr + " ");
				}

				
				
				if (operator.equals(WhereClauseSubPanelI18n.IN)
					&& (!value.trim().startsWith("(")))
				{
					value = "(" + value + ")";
				}

				

				else if ((value != null) && (value.length() > 0)) 
				{
					if (_textColumns.containsKey(column)
							&& (!value.trim().startsWith("'")))
					{
						value = "'" + value + "'";
					}
				}
				_whereClauseArea.append(column + " " + operator);

				if ((value != null) && (value.length() > 0)) 
				{
					_whereClauseArea.append(" " + value);
				}
			}
			_valueField.setText("");
		}

		
		public void clearFilter()
		{
			_whereClauseArea.setText("");
		}
	}

	
	public void clearFilter()
	{
		_myPanel.clearFilter();
	}

	
	public static String getClauseIdentifier()
	{
		return WhereClauseSubPanel.WhereClauseSubPanelI18n.WHERE_CLAUSE;
	}
}
