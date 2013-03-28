package net.sourceforge.squirrel_sql.client.gui.db;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.fw.gui.ModifiedDefaultListCellRenderer;
import net.sourceforge.squirrel_sql.fw.gui.Dialogs;
import net.sourceforge.squirrel_sql.fw.sql.ISQLAlias;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.*;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import java.awt.*;
import java.awt.event.MouseEvent;



public class JListAliasesListImpl extends BaseList implements IAliasesList
{
   private static final String PREF_KEY_SELECTED_ALIAS_INDEX = "Squirrel.selAliasIndex";
   
   
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(AliasesList.class);

   private IApplication _app;
   
	private final AliasesListModel _model;

	public JListAliasesListImpl(IApplication app, AliasesListModel aliasesListModel)
	{
      super(aliasesListModel, app);
      _app = app;
      _model = aliasesListModel;
		getList().setLayout(new BorderLayout());

		getList().setCellRenderer(new ModifiedDefaultListCellRenderer());


		_model.addListDataListener(new ListDataListener()
		{
			public void contentsChanged(ListDataEvent evt)
			{
				
			}
			public void intervalAdded(ListDataEvent evt)
			{
				final int idx = evt.getIndex0();
				SwingUtilities.invokeLater(new Runnable()
				{
					public void run()
					{
						getList().clearSelection();
						getList().setSelectedIndex(idx);
					}
				});
			}
			public void intervalRemoved(ListDataEvent evt)
			{
				final int idx = evt.getIndex0();
				SwingUtilities.invokeLater(new Runnable()
				{
					public void run()
					{
						getList().clearSelection();
						int modelSize = getList().getModel().getSize();
						if (idx < modelSize)
						{
							getList().setSelectedIndex(idx);
						}
						else if (modelSize > 0)
						{
							getList().setSelectedIndex(modelSize - 1);
						}
					}
				});
			}
		});




   }



   
	public SQLAlias getSelectedAlias()
	{
		return (SQLAlias)getList().getSelectedValue();
	}

   public void sortAliases()
   {
      final ISQLAlias selectedAlias = getSelectedAlias();

      _model.sortAliases();


		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				if(null != selectedAlias)
				{
					getList().setSelectedValue(selectedAlias, true);
				}
			}
		});

   }

   public void requestFocus()
   {
      getList().requestFocus();
   }

   public void deleteSelected()
   {
      SQLAlias toDel = (SQLAlias) getList().getSelectedValue();

      if (null != toDel)
      {
         if (Dialogs.showYesNo(_app.getMainFrame(), s_stringMgr.getString("JListAliasesListImpl.confirmDelete", toDel.getName())))
         {
            _model.remove(getList().getSelectedIndex());
            _app.getDataCache().removeAlias(toDel);

         }
      }
   }

   public void modifySelected()
   {
      if(null != getList().getSelectedValue())
      {
         _app.getWindowManager().showModifyAliasInternalFrame((ISQLAlias) getList().getSelectedValue());
      }
   }

   
	public String getToolTipText(MouseEvent evt)
	{
		String tip;
		final int idx = getList().locationToIndex(evt.getPoint());
		if (idx != -1)
		{
			tip = ((ISQLAlias)getList().getModel().getElementAt(idx)).getName();
		}
		else
		{
			tip = getToolTipText();
		}
		return tip;
	}

	
	public String getToolTipText()
	{
		return s_stringMgr.getString("AliasesList.tooltip");
	}

   public String getSelIndexPrefKey()
   {
      return PREF_KEY_SELECTED_ALIAS_INDEX;
   }
}