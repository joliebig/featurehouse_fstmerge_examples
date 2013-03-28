package net.sourceforge.squirrel_sql.client.preferences;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.CellComponentFactory;
import net.sourceforge.squirrel_sql.fw.gui.OkJPanel;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

public class DataTypePreferencesPanel implements  IGlobalPreferencesPanel
{

	
	private final DataTypePropertiesPanel _myPanel;
    
    private JScrollPane _myscrolledPanel;
    
    
    private static final StringManager s_stringMgr =
        StringManagerFactory.getStringManager(DataTypePreferencesPanel.class);

   
   public DataTypePreferencesPanel()
   {
      super();

      _myPanel = new DataTypePropertiesPanel();
      _myscrolledPanel = new JScrollPane(_myPanel);
      _myscrolledPanel.getVerticalScrollBar().setUnitIncrement(10);
      _myscrolledPanel.setPreferredSize(new Dimension(600, 450));      
   }


	public void initialize(IApplication app)
	{
		
		
		
		
	}

   public void uninitialize(IApplication app)
   {
      
      
      
      
   }

   public void initialize(IApplication app, ISession session)
		throws IllegalArgumentException
	{
		
		
		
		
	}

	public Component getPanelComponent()
	{
		return _myscrolledPanel;
	}

	public String getTitle()
	{
        
		return s_stringMgr.getString("DataTypePreferencesPanel.propsPanel.title");
	}

	public String getHint()
	{
        
		return s_stringMgr.getString("DataTypePreferencesPanel.propsPanel.hint");
	}

	public void applyChanges()
	{
		_myPanel.applyChanges();
	}

	private static final class DataTypePropertiesPanel extends JPanel
	{
		
		
		OkJPanel[] dataTypePanels;

		DataTypePropertiesPanel()
		{
			super();
			createGUI();
		}
		

		void applyChanges()
		{		
			for (int i=0; i< dataTypePanels.length; i++)
				dataTypePanels[i].ok();
		}


		private void createGUI()
		{

			setLayout(new GridBagLayout());
			final GridBagConstraints gbc = new GridBagConstraints();
			gbc.anchor = GridBagConstraints.WEST;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.insets = new Insets(4, 4, 4, 4);

			gbc.gridx = 0;
			gbc.gridy = 0;




         add(createDataTypesPanel(), gbc);

		}

		private JPanel createDataTypesPanel()
		{

			JPanel pnl = new JPanel(new GridBagLayout());
			final GridBagConstraints gbc = new GridBagConstraints();
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.insets = new Insets(4, 4, 4, 4);
			gbc.anchor = GridBagConstraints.WEST;

			gbc.gridx = 0;
			gbc.gridy = 0;

			
			
			dataTypePanels = CellComponentFactory.getControlPanels();
			for (int i=0; i<dataTypePanels.length; i++) {
				gbc.gridx=0;
				gbc.gridwidth = GridBagConstraints.REMAINDER;
				++gbc.gridy;
				pnl.add(dataTypePanels[i], gbc);
			}

			return pnl;
		}

		private static final class RightLabel extends JLabel
		{
			RightLabel(String title)
			{
				super(title, SwingConstants.RIGHT);
			}
		}

	}

}
