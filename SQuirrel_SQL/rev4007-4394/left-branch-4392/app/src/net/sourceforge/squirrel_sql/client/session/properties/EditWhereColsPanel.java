package net.sourceforge.squirrel_sql.client.session.properties;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListModel;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.PrimaryKeyInfo;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;



@SuppressWarnings("serial")
public class EditWhereColsPanel extends JPanel
{
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(EditWhereColsPanel.class);

    
    private static final ILogger s_log =
        LoggerController.createLogger(EditWhereColsPanel.class);    

	
	private String _tableName;
	
	
	private String _unambiguousTableName;
	
	
	private SortedSet<String> _columnList;
	
	
	private JList useColsList;
	
	
	private JList notUseColsList;
	
	
	private Object[] initalUseColsArray;
	
	
	private Object[] initalNotUseColsArray;
    
	private ISession _session = null;
    
    private PrimaryKeyInfo[] primaryKeyInfos = null; 
    
    EditWhereCols _editWhereCols = new EditWhereCols();
    
	
	interface EditWhereColsPanelI18N {
		
		String TITLE = s_stringMgr.getString("editWhereColsPanel.limitColsInCell");
		
		String HINT = s_stringMgr.getString("editWhereColsPanel.limitColsInCellHint");
        
        String USE_PK = s_stringMgr.getString("editWhereColsPanel.usePKLabel");

	}
	
	
	public EditWhereColsPanel(ISession session,
                              ITableInfo ti,                   
                              SortedSet<String> columnList, 
                              String unambiguousTableName)
		throws IllegalArgumentException
	{
		super();
		_session = session;
        _editWhereCols.setApplication(session.getApplication());
        getPrimaryKey(ti);
		
		_columnList = columnList;
		_tableName = ti.getQualifiedName();
		_unambiguousTableName = unambiguousTableName;
		
		
		HashMap<String, String> colsTable = EditWhereCols.get(unambiguousTableName);
		
		if (colsTable == null) {
			
			initalUseColsArray = _columnList.toArray();
			initalNotUseColsArray = new Object[0];
		}
		else {
			
			
			SortedSet<Object> initialUseColsSet = new TreeSet<Object>( );
			SortedSet<Object> initialNotUseColsList = new TreeSet<Object>();
			
			Iterator<String> it = _columnList.iterator();
			while (it.hasNext()) {
				Object colName = it.next();
				if (colsTable.get(colName) != null)
					initialUseColsSet.add(colName);
				else initialNotUseColsList.add(colName);
			}
			initalUseColsArray = initialUseColsSet.toArray();
			initalNotUseColsArray = initialNotUseColsList.toArray();
		}

		
		createGUI();
	}

	private void getPrimaryKey(ITableInfo ti) {
        try {
            primaryKeyInfos = _session.getMetaData().getPrimaryKey(ti);
        } catch (SQLException e) {
            s_log.error(
               "Unexpected exception while attempting to get primary key info" +
               " for table "+ti.getQualifiedName()+": "+e.getMessage(), e);
        }
    }
    
	
	public String getTitle()
	{
		return EditWhereColsPanelI18N.TITLE;
	}

	
	public String getHint()
	{
		return EditWhereColsPanelI18N.HINT;
	}

	
	public void reset() {	
		useColsList.setListData(initalUseColsArray);
		notUseColsList.setListData(initalNotUseColsArray);
	}
	
	
	public boolean ok() {
        
		
		if (notUseColsList.getModel().getSize() == 0) {
			_editWhereCols.put(_unambiguousTableName, null);
		}
		else {
			
			ListModel useColsModel = useColsList.getModel();
			
			
			if (useColsModel.getSize() == 0) {
				JOptionPane.showMessageDialog(this,
					
					s_stringMgr.getString("editWhereColsPanel.cannotRemoveAllCols"));
				return false;
			}
			
			
			HashMap<String, String> useColsMap = 
                new HashMap<String, String>(useColsModel.getSize());
			
			for (int i=0; i< useColsModel.getSize(); i++) {
				useColsMap.put((String)useColsModel.getElementAt(i), 
                               (String)useColsModel.getElementAt(i));
			}
			
			_editWhereCols.put(_unambiguousTableName, useColsMap);
		}
		return true;
	}
	
	
	private void moveToNotUsed() {
		
		
		ListModel notUseColsModel = notUseColsList.getModel();
		SortedSet<String> notUseColsSet = new TreeSet<String>();
		for (int i=0; i<notUseColsModel.getSize(); i++)
			notUseColsSet.add((String)notUseColsModel.getElementAt(i));
		
		
		ListModel useColsModel = useColsList.getModel();
		
		
		SortedSet<Object> useColsSet = new TreeSet<Object>();

		
		
		for (int i=0; i<useColsModel.getSize(); i++) {
			String colName = (String)useColsModel.getElementAt(i);
			if (useColsList.isSelectedIndex(i))
				notUseColsSet.add(colName);
			else useColsSet.add(colName);
		}
		
		useColsList.setListData(useColsSet.toArray());
		notUseColsList.setListData(notUseColsSet.toArray());
	}
	
	
	private void moveToUsed() {
		
		ListModel useColsModel = useColsList.getModel();
		SortedSet<String> useColsSet = new TreeSet<String>();
		for (int i=0; i<useColsModel.getSize(); i++)
			useColsSet.add((String)useColsModel.getElementAt(i));
		
		
		ListModel notUseColsModel = notUseColsList.getModel();
		
		
		SortedSet<Object> notUseColsSet = new TreeSet<Object>();

		
		
		for (int i=0; i<notUseColsModel.getSize(); i++) {
			String colName = (String)notUseColsModel.getElementAt(i);
			if (notUseColsList.isSelectedIndex(i))
				useColsSet.add(colName);
			else notUseColsSet.add(colName);
		}
		
		useColsList.setListData(useColsSet.toArray());
		notUseColsList.setListData(notUseColsSet.toArray());
	}
	
    private void usePK() {
        if (primaryKeyInfos == null || primaryKeyInfos.length <= 0) {
            
            String msg = 
                s_stringMgr.getString("editWhereColsPanel.noPK", _tableName);
            JOptionPane.showMessageDialog(this,msg);
            
            return;
        }
        HashSet<String> pkCols = new HashSet<String>();
        for (int i = 0; i < primaryKeyInfos.length; i++) {
            PrimaryKeyInfo pkInfo = primaryKeyInfos[i];
            pkCols.add(pkInfo.getColumnName());
        }
        
        ArrayList<String> newNotUseList = new ArrayList<String>();
        ListModel useColsModel = useColsList.getModel();
        ListModel notUseColsModel = notUseColsList.getModel();
        
        for (int i=0; i<useColsModel.getSize(); i++) {
            Object colName = useColsModel.getElementAt(i);
            if (!pkCols.contains(colName)) {
                newNotUseList.add(colName.toString());
            }
        }        
        
        for (int i=0; i<notUseColsModel.getSize(); i++) {
            Object colName = notUseColsModel.getElementAt(i);
            if (!pkCols.contains(colName)) {
                newNotUseList.add(colName.toString());
            }
        }
        
        useColsList.setListData(pkCols.toArray());
        notUseColsList.setListData(newNotUseList.toArray());
        
        
    }
	
	
	private void createGUI()
	{

		JPanel useColsPanel = new JPanel(new BorderLayout());
		
		useColsPanel.add(new JLabel(s_stringMgr.getString("editWhereColsPanel.useColumns")), BorderLayout.NORTH);
		useColsList = new JList(initalUseColsArray);
		JScrollPane scrollPane = new JScrollPane(useColsList);
		scrollPane.setPreferredSize(new Dimension(200, 200));
		useColsPanel.add(scrollPane, BorderLayout.SOUTH);
		add(useColsPanel);

		JPanel moveButtonsPanel = new JPanel();
		JPanel buttonPanel = new JPanel(new GridLayout(3,1));

		JButton moveToNotUsedButton = new JButton("=>");
		moveToNotUsedButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				moveToNotUsed();
			}
 		});
		buttonPanel.add(moveToNotUsedButton);
		JButton moveToUsedButton = new JButton("<=");
		moveToUsedButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				moveToUsed();
			}
			});
		buttonPanel.add(moveToUsedButton);

        JButton usePKButton = new JButton(EditWhereColsPanelI18N.USE_PK);
        usePKButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                usePK();
            }
        });
        buttonPanel.add(usePKButton);
        
		moveButtonsPanel.add(buttonPanel, BorderLayout.CENTER);
		add(moveButtonsPanel);
	  
		JPanel notUseColsPanel = new JPanel(new BorderLayout());
		
		notUseColsPanel.add(new JLabel(s_stringMgr.getString("editWhereColsPanel.notUseColumns")), BorderLayout.NORTH);
		notUseColsList = new JList(initalNotUseColsArray);
 		JScrollPane notUseScrollPane = new JScrollPane(notUseColsList);
		notUseScrollPane.setPreferredSize(new Dimension(200, 200));
		notUseColsPanel.add(notUseScrollPane, BorderLayout.SOUTH);
		add(notUseColsPanel);
	}
}
