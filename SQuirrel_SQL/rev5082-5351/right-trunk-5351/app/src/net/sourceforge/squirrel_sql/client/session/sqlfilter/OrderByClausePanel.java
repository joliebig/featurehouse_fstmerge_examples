package net.sourceforge.squirrel_sql.client.session.sqlfilter;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.SortedSet;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

public class OrderByClausePanel implements ISQLFilterPanel
{
    
    private static final StringManager s_stringMgr =
        StringManagerFactory.getStringManager(OrderByClausePanel.class);
    
	
	private SQLFilterClauses _sqlFilterClauses;

	
	private OrderByClauseSubPanel _myPanel;

	
	public OrderByClausePanel(SortedSet<String> columnList, String tableName)
	{
		_myPanel = new OrderByClauseSubPanel(columnList, tableName);
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
		return OrderByClauseSubPanel.OrderByClausePanelI18n.ORDER_BY_CLAUSE;
	}

	
	public String getHint()
	{
		return OrderByClauseSubPanel.OrderByClausePanelI18n.HINT;
	}

	
	public void applyChanges()
	{
		_myPanel.applyChanges(_sqlFilterClauses);
	}

	
	private static final class OrderByClauseSubPanel extends JPanel
	{
        private static final long serialVersionUID = 1L;

        
		interface OrderByClausePanelI18n
		{

            
            
		    String ASC = "ASC";   
		    String DESC = "DESC"; 

		    
		    String ADD = s_stringMgr.getString("OrderByClausePanel.addLabel");
		    
		    String COLUMNS = 
                s_stringMgr.getString("OrderByClausePanel.columnsLabel");
		    
		    String ORDER_DIRECTION = 
                s_stringMgr.getString("OrderByClausePanel.orderDirectionLabel");
		    
		    String ORDER_BY_CLAUSE = 
                s_stringMgr.getString("OrderByClausePanel.orderByClauseLabel");
		    
		    String HINT = s_stringMgr.getString("OrderByClausePanel.hint");
		}

		
		private JLabel _columnLabel =
			new JLabel(OrderByClausePanelI18n.COLUMNS);
		
		private JComboBox _columnCombo;
		
		private JLabel _orderLabel =
			new JLabel(OrderByClausePanelI18n.ORDER_DIRECTION);
		
		private OrderCombo _orderCombo = new OrderCombo();
		
		private JButton _addButton = new JButton(OrderByClausePanelI18n.ADD);
		
		private JTextArea _orderClauseArea = new JTextArea(10, 40);
		
		private String _tableName;

		
		OrderByClauseSubPanel(SortedSet<String> columnList, String tableName)
		{
			_columnCombo = new JComboBox(columnList.toArray());
			_tableName = tableName;
			createUserInterface();
		}

		
		void loadData(SQLFilterClauses sqlFilterClauses)
		{
			_orderClauseArea.setText(
				sqlFilterClauses.get(getClauseIdentifier(), _tableName));
		}

		
		void applyChanges(SQLFilterClauses sqlFilterClauses)
		{
			sqlFilterClauses.put(
				getClauseIdentifier(),
				_tableName,
				_orderClauseArea.getText());
		}

		
		private void createUserInterface()
		{
         setLayout(new GridBagLayout());
         GridBagConstraints gbc;

         gbc = new GridBagConstraints(0,0,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(4,4,4,4),0,0);
         add(createControlsPanel(), gbc);

         _orderClauseArea.setLineWrap(true);
         JScrollPane sp = new JScrollPane(_orderClauseArea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,	JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

         gbc = new GridBagConstraints(0,1,1,1,1,1,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(4,4,4,4),0,0);
         add(sp, gbc);
		}

		

      private JPanel createControlsPanel()
      {
         JPanel ret = new JPanel(new GridBagLayout());
         GridBagConstraints gbc;

         gbc = new GridBagConstraints(0,0,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(4,4,4,4),0,0);
         ret.add(_columnLabel, gbc);

         gbc = new GridBagConstraints(0,1,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(4,4,4,4),0,0);
         ret.add(_columnCombo, gbc);


         gbc = new GridBagConstraints(1,0,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(4,4,4,4),0,0);
         ret.add(_orderLabel, gbc);

         gbc = new GridBagConstraints(1,1,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(4,4,4,4),0,0);
         ret.add(_orderCombo, gbc);


         gbc = new GridBagConstraints(2,1,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(4,4,4,4),0,0);
         ret.add(_addButton, gbc);


         _addButton.addActionListener(new ActionListener()
         {
            public void actionPerformed(ActionEvent evt)
            {
               addTextToClause();
            }
         });

         return ret;
      }

      
		private void addTextToClause()
		{
			String column = (String)_columnCombo.getSelectedItem();
			String order = (String)_orderCombo.getSelectedItem();
			if (_orderClauseArea.getText().length() > 0)
			{
				_orderClauseArea.append(",\n");
			}
			_orderClauseArea.append(column + " " + order);
		}

		
		public void clearFilter()
		{
			_orderClauseArea.setText("");
		}
	}

	private static final class OrderCombo extends JComboBox
	{
        private static final long serialVersionUID = 1L;

        OrderCombo()
		{
			super();
			addItem(OrderByClauseSubPanel.OrderByClausePanelI18n.ASC);
			addItem(OrderByClauseSubPanel.OrderByClausePanelI18n.DESC);
		}
	}

	
	public void clearFilter()
	{
		_myPanel.clearFilter();
	}

	
	public static String getClauseIdentifier()
	{
		return OrderByClauseSubPanel.OrderByClausePanelI18n.ORDER_BY_CLAUSE;
	}
}
