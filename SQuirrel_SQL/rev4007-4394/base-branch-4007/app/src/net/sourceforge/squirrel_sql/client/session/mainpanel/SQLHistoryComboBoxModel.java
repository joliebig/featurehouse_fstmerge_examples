package net.sourceforge.squirrel_sql.client.session.mainpanel;

import javax.swing.DefaultComboBoxModel;
import javax.swing.MutableComboBoxModel;
import javax.swing.event.ListDataListener;

import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import java.util.ArrayList;


public class SQLHistoryComboBoxModel extends DefaultComboBoxModel
{
	
	private static final ILogger s_log =
		LoggerController.createLogger(SQLHistoryComboBoxModel.class);

	
	private static MutableComboBoxModel s_sharedDataModel;

	
	private MutableComboBoxModel _dataModel;

	
	private Object _selectedObject;

	public SQLHistoryComboBoxModel(boolean useSharedModel)
	{
		super();
		if (useSharedModel && s_sharedDataModel == null)
		{
			throw new IllegalStateException("Shared instance has not been initialized");
		}
		_dataModel = useSharedModel ? s_sharedDataModel : new DefaultComboBoxModel();
	}

	public synchronized static void initializeSharedInstance(Object[] data)
	{
		if (s_sharedDataModel != null)
		{
			s_log.error("Shared data model has already been initialized");
		}
		else
		{
			s_sharedDataModel = new DefaultComboBoxModel(data);
		}
	}

	
	public boolean isUsingSharedDataModel()
	{
		return _dataModel == s_sharedDataModel;
	}

	
	public synchronized void setUseSharedModel(boolean use)
	{
		if (isUsingSharedDataModel() != use)
		{
			_dataModel = use ? s_sharedDataModel : duplicateSharedDataModel();
		}
	}

	
	public void addElement(Object object)
	{
		_dataModel.addElement(object);
	}

	
	public void insertElementAt(Object object, int index)
	{
		_dataModel.insertElementAt(object, index);
	}

	
	public void removeElement(Object object)
	{
		_dataModel.removeElement(object);
	}

	
	public void removeElementAt(int index)
	{
		_dataModel.removeElementAt(index);
	}

	
	public Object getSelectedItem()
	{
		return _selectedObject;
	}

	
	public void setSelectedItem(Object object)
	{
		_selectedObject = object;
		fireContentsChanged(this, -1, -1);
	}

	
	public void addListDataListener(ListDataListener arg0)
	{
		_dataModel.addListDataListener(arg0);
	}

	
	public Object getElementAt(int arg0)
	{
		return _dataModel.getElementAt(arg0);
	}

	
	public int getSize()
	{
		return _dataModel.getSize();
	}

	
	public void removeListDataListener(ListDataListener arg0)
	{
		_dataModel.removeListDataListener(arg0);
	}

	protected synchronized MutableComboBoxModel duplicateSharedDataModel()
	{
		MutableComboBoxModel newModel = new DefaultComboBoxModel();
		for (int i = 0, limit = s_sharedDataModel.getSize(); i < limit; ++i)
		{
			SQLHistoryItem obj = (SQLHistoryItem)s_sharedDataModel.getElementAt(i);
			newModel.addElement(obj.clone());
		} 
		return newModel;
	}

   public ArrayList<SQLHistoryItem> getItems()
   {
      ArrayList<SQLHistoryItem> ret = new ArrayList<SQLHistoryItem>();

      for (int i = 0; i < _dataModel.getSize(); i++)
      {
         ret.add((SQLHistoryItem) _dataModel.getElementAt(i));
      }

      return ret;


   }
}
