package net.sourceforge.squirrel_sql.client.session.mainpanel;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.NumberFormat;

import javax.swing.*;

import net.sourceforge.squirrel_sql.fw.datasetviewer.*;
import net.sourceforge.squirrel_sql.fw.gui.MultipleLineLabel;
import net.sourceforge.squirrel_sql.fw.id.IHasIdentifier;
import net.sourceforge.squirrel_sql.fw.id.IIdentifier;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.gui.builders.UIFactory;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.SQLExecutionInfo;
import net.sourceforge.squirrel_sql.client.session.EditableSqlCheck;
import net.sourceforge.squirrel_sql.client.session.properties.SessionProperties;

public class ResultTab extends JPanel implements IHasIdentifier, IResultTab
{
    private static final long serialVersionUID = 1L;

    
	private IIdentifier _id;

	
	transient private ISession _session;

	
	transient private SQLExecutionInfo _exInfo;

	
	transient private IDataSetViewer _resultSetOutput;

	
	transient private IDataSetViewer _metaDataOutput;

	
	private JScrollPane _resultSetSp = new JScrollPane();

	
	private JScrollPane _metaDataSp = new JScrollPane();

	
	private JTabbedPane _tp;

	
	private SQLResultExecuterPanel _sqlPanel;

	
	private JLabel _currentSqlLbl = new JLabel();

	
	private String _sql;

	
	private QueryInfoPanel _queryInfoPanel = new QueryInfoPanel();

	
	private PropertyChangeListener _propsListener;

    private boolean _allowEditing;
   
    transient private IDataSetUpdateableTableModel _creator;
   
    transient private ResultSetDataSet _rsds;
   
   
   private static final StringManager s_stringMgr =
       StringManagerFactory.getStringManager(ResultTab.class);
   private ResultTabListener _resultTabListener;

   
   public ResultTab(ISession session, SQLResultExecuterPanel sqlPanel,
                    IIdentifier id, SQLExecutionInfo exInfo,
                    IDataSetUpdateableTableModel creator, ResultTabListener resultTabListener)
      throws IllegalArgumentException
   {
      super();
      _resultTabListener = resultTabListener;
      if (session == null)
      {
         throw new IllegalArgumentException("Null ISession passed");
      }
      if (sqlPanel == null)
      {
         throw new IllegalArgumentException("Null SQLPanel passed");
      }
      if (id == null)
      {
         throw new IllegalArgumentException("Null IIdentifier passed");
      }

      _session = session;
      _sqlPanel = sqlPanel;
      _id = id;
      reInit(creator, exInfo);


      createGUI();
      propertiesHaveChanged(null);
   }

	
	public void reInit(IDataSetUpdateableTableModel creator, SQLExecutionInfo exInfo)
	{
		_creator = creator;
		_creator.addListener(new DataSetUpdateableTableModelListener()
		{
			public void forceEditMode(boolean mode)
			{
				onForceEditMode(mode);
			}
		});

		_allowEditing = new EditableSqlCheck(exInfo).allowsEditing();

		final SessionProperties props = _session.getProperties();

		if (_allowEditing)
		{
			_resultSetOutput = BaseDataSetViewerDestination.getInstance(props.getSQLResultsOutputClassName(), _creator);

		}
		else
		{
			
			
			
			
			
			_resultSetOutput = BaseDataSetViewerDestination.getInstance(
				props.getReadOnlySQLResultsOutputClassName(), null);
		}


		_resultSetSp.setViewportView(_resultSetOutput.getComponent());
      _resultSetSp.setRowHeader(null);

      if (_session.getProperties().getShowResultsMetaData())
      {
         _metaDataOutput = BaseDataSetViewerDestination.getInstance(props.getMetaDataOutputClassName(), null);
         _metaDataSp.setViewportView(_metaDataOutput.getComponent());
         _metaDataSp.setRowHeader(null);
      }
	}

	
	public void addNotify()
	{
		super.addNotify();
		if (_propsListener == null)
		{
			_propsListener = new PropertyChangeListener()
			{
				public void propertyChange(PropertyChangeEvent evt)
				{
					propertiesHaveChanged(evt);
				}
			};
			_session.getProperties().addPropertyChangeListener(_propsListener);
		}
	}

	
	public void removeNotify()
	{
		super.removeNotify();
		if (_propsListener != null)
		{
			_session.getProperties().removePropertyChangeListener(_propsListener);
			_propsListener = null;
		}
	}

	
	public void showResults(ResultSetDataSet rsds, ResultSetMetaDataDataSet mdds,
								SQLExecutionInfo exInfo)
		throws DataSetException
	{
		_exInfo = exInfo;
		_sql = StringUtilities.cleanString(exInfo.getSQL());

		
		_resultSetOutput.show(rsds, null);
      _rsds = rsds;

		final int rowCount = _resultSetOutput.getRowCount();

		final int maxRows =_exInfo.getMaxRows(); 
		if (maxRows > 0 && rowCount >= maxRows)
		{
			String buf = _sql.replaceAll("&", "&amp;");
			buf = buf.replaceAll("<", "&lt;");
			buf = buf.replaceAll("<", "&gt;");
			buf = buf.replaceAll("\"", "&quot;");
            
            String limitMsg = 
                s_stringMgr.getString("ResultTab.limitMessage", 
                                      Integer.valueOf(rowCount));
			_currentSqlLbl.setText("<html><pre>&nbsp;" + limitMsg +
                                   ";&nbsp;&nbsp;" + buf + "</pre></html>");
		}
		else
		{
			_currentSqlLbl.setText(_sql);
		}

		
		if (mdds != null && _metaDataOutput != null)
		{
			_metaDataOutput.show(mdds, null); 
		}

		exInfo.resultsProcessingComplete();

		
		_queryInfoPanel.load(rsds, rowCount, exInfo);
	}

	
	public void clear()
	{
		if (_metaDataOutput != null)
		{
			_metaDataOutput.clear();
		}
		if (_resultSetOutput != null)
		{
			_resultSetOutput.clear();
		}
		_exInfo = null;
		_currentSqlLbl.setText("");
		_sql = "";
	}

	
	public String getSqlString()
	{
		return _exInfo != null ? _exInfo.getSQL() : null;
	}

	
	public String getViewableSqlString()
	{
		return StringUtilities.cleanString(getSqlString());
	}

	
	public String getTitle()
	{
		String title = _sql;
		if (title.length() < 20)
		{
			return title;
		}
		return title.substring(0, 15);
	}

	
	public void closeTab()
	{
		add(_tp, BorderLayout.CENTER);
		_sqlPanel.closeTab(this);
	}

	
	public void returnToTabbedPane()
	{
		add(_tp, BorderLayout.CENTER);
		_sqlPanel.returnToTabbedPane(this);
	}

	
	public Component getOutputComponent()
	{
		return _tp;
	}

    
    public void reRunSQL() {
        _resultTabListener.rerunSQL(_exInfo.getSQL(), ResultTab.this);
    }
    
	
	private void propertiesHaveChanged(PropertyChangeEvent evt)
	{
      SessionProperties props = _session.getProperties();
      if (evt == null
         || evt.getPropertyName().equals(
            SessionProperties.IPropertyNames.SQL_RESULTS_TAB_PLACEMENT))
      {
         _tp.setTabPlacement(props.getSQLResultsTabPlacement());
      }
   }


   private void onForceEditMode(boolean editable)
   {
      try
      {
         if(editable)
         {
            if (_allowEditing)
            {
               _resultSetOutput = BaseDataSetViewerDestination.getInstance(SessionProperties.IDataSetDestinations.EDITABLE_TABLE, _creator);
               _resultSetSp.setViewportView(_resultSetOutput.getComponent());
               _resultSetSp.setRowHeader(null);
               _rsds.resetCursor();
               _resultSetOutput.show(_rsds, null);
            }
            else
            {
                
               String msg = s_stringMgr.getString("ResultTab.cannotedit");
               JOptionPane.showMessageDialog(_session.getApplication().getMainFrame(), msg);
            }
         }
         else
         {
            SessionProperties props = _session.getProperties();

            String readOnlyOutput = props.getReadOnlySQLResultsOutputClassName();

            _resultSetOutput = BaseDataSetViewerDestination.getInstance(readOnlyOutput, _creator);
            _resultSetSp.setViewportView(_resultSetOutput.getComponent());
            _resultSetSp.setRowHeader(null);
            _rsds.resetCursor();
            _resultSetOutput.show(_rsds, null);
         }
      }
      catch (DataSetException e)
      {
         throw new RuntimeException(e);
      }
   }


	private void createGUI()
	{
		
		setLayout(new BorderLayout());

      int sqlResultsTabPlacement = _session.getProperties().getSQLResultsTabPlacement();
      _tp = UIFactory.getInstance().createTabbedPane(sqlResultsTabPlacement);

		JPanel panel1 = new JPanel();
		JPanel panel2 = new JPanel();
		panel2.setLayout(new GridLayout(1, 3, 0, 0));
      panel2.add(new TabButton(new RerunAction(_session.getApplication())));
		panel2.add(new TabButton(new CreateResultTabFrameAction(_session.getApplication())));
		panel2.add(new TabButton(new CloseAction()));
		panel1.setLayout(new BorderLayout());
		panel1.add(panel2, BorderLayout.EAST);
		panel1.add(_currentSqlLbl, BorderLayout.CENTER);
		add(panel1, BorderLayout.NORTH);
		add(_tp, BorderLayout.CENTER);

      _resultSetSp.setBorder(BorderFactory.createEmptyBorder());
      
      
      String resultsTabTitle = 
          s_stringMgr.getString("ResultTab.resultsTabTitle");
      _tp.addTab(resultsTabTitle, _resultSetSp);

      if (_session.getProperties().getShowResultsMetaData())
      {
         _metaDataSp.setBorder(BorderFactory.createEmptyBorder());
         
         
         String metadataTabTitle = 
             s_stringMgr.getString("ResultTab.metadataTabTitle");
         _tp.addTab(metadataTabTitle, _metaDataSp);
      }

		final JScrollPane sp = new JScrollPane(_queryInfoPanel);
		sp.setBorder(BorderFactory.createEmptyBorder());
        
        
        String infoTabTitle = 
            s_stringMgr.getString("ResultTab.infoTabTitle");
		_tp.addTab(infoTabTitle, sp);
	}

	private final class TabButton extends JButton
	{
		TabButton(Action action)
		{
			super(action);
			setMargin(new Insets(0, 0, 0, 0));
			setBorderPainted(false);
			setText("");
		}
	}

	private class CloseAction extends SquirrelAction
	{
		CloseAction()
		{
			super(
				_session.getApplication(),
				_session.getApplication().getResources());
		}

		public void actionPerformed(ActionEvent evt)
		{
			closeTab();
		}
	}

	private class CreateResultTabFrameAction extends SquirrelAction
	{
		CreateResultTabFrameAction(IApplication app)
		{
			super(app, app.getResources());
		}

		public void actionPerformed(ActionEvent evt)
		{
			_sqlPanel.createWindow(ResultTab.this);
		}
	}

   public class RerunAction  extends SquirrelAction
   {
      RerunAction(IApplication app)
      {
         super(app, app.getResources());
      }

      public void actionPerformed(ActionEvent evt)
      {
         reRunSQL();   
      }
   }


   
	public IIdentifier getIdentifier()
	{
		return _id;
	}

	private static class QueryInfoPanel extends JPanel
	{
        private static final long serialVersionUID = 2124193091025851544L;
        
        private MultipleLineLabel _queryLbl = new MultipleLineLabel();
		private JLabel _rowCountLbl = new JLabel();
		private JLabel _executedLbl = new JLabel();
		private JLabel _elapsedLbl = new JLabel();

		QueryInfoPanel()
		{
			super();
			createGUI();
		}

		void load(ResultSetDataSet rsds, int rowCount,
					SQLExecutionInfo exInfo)
		{
			_queryLbl.setText(StringUtilities.cleanString(exInfo.getSQL()));
			_rowCountLbl.setText(String.valueOf(rowCount));
			_executedLbl.setText(exInfo.getSQLExecutionStartTime().toString());
			_elapsedLbl.setText(formatElapsedTime(exInfo));
		}

		private String formatElapsedTime(SQLExecutionInfo exInfo)
		{
			final NumberFormat nbrFmt = NumberFormat.getNumberInstance();
			double executionLength = exInfo.getSQLExecutionElapsedMillis() / 1000.0;
			double outputLength = exInfo.getResultsProcessingElapsedMillis() / 1000.0;
            
            String totalTime = nbrFmt.format(executionLength + outputLength);
            String queryTime = nbrFmt.format(executionLength);
            String outputTime = nbrFmt.format(outputLength);
            
            
            String elapsedTime = 
                s_stringMgr.getString("ResultTab.elapsedTime",
                                      new String[] { totalTime, 
                                                     queryTime,
                                                     outputTime});
			return elapsedTime;
		}

		private void createGUI()
		{
			setLayout(new GridBagLayout());
			GridBagConstraints gbc = new GridBagConstraints();

			gbc.anchor = GridBagConstraints.NORTHWEST;
			gbc.gridwidth = 1;
			gbc.weightx = 0;

			gbc.gridx = 0;
			gbc.gridy = 0;
			gbc.insets = new Insets(5, 10, 5, 10);
			gbc.fill = GridBagConstraints.HORIZONTAL;
            
            String label = s_stringMgr.getString("ResultTab.executedLabel");
			add(new JLabel(label, SwingConstants.RIGHT), gbc);

			++gbc.gridy;
            
            label = s_stringMgr.getString("ResultTab.rowCountLabel");
			add(new JLabel(label, SwingConstants.RIGHT), gbc);

			++gbc.gridy;
            
            label = s_stringMgr.getString("ResultTab.statementLabel");            
			add(new JLabel(label, SwingConstants.RIGHT), gbc);

			++gbc.gridy;
            
            label = s_stringMgr.getString("ResultTab.elapsedTimeLabel");            
            add(new JLabel(label, SwingConstants.RIGHT), gbc);

			gbc.gridwidth = GridBagConstraints.REMAINDER;
			gbc.weightx = 1;

			gbc.gridx = 1;
			gbc.gridy = 0;
			add(_executedLbl, gbc);

			++gbc.gridy;
			add(_rowCountLbl, gbc);

			++gbc.gridy;
			add(_queryLbl, gbc);

			++gbc.gridy;
			add(_elapsedLbl, gbc);
		}
	}
}
