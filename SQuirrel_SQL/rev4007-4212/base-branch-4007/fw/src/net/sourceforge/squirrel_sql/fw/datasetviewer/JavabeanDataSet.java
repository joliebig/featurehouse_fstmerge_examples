package net.sourceforge.squirrel_sql.fw.datasetviewer;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.squirrel_sql.fw.util.IMessageHandler;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

public class JavabeanDataSet implements IDataSet
{
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(JavabeanDataSet.class);

	@SuppressWarnings("unused")
	private ILogger s_log =
		LoggerController.createLogger(JavabeanDataSet.class);

	
	private static final String _nameColumnName = 
        s_stringMgr.getString("javaBeanDataSet.name");
	
	private static final String _valueColumnName = 
        s_stringMgr.getString("javaBeanDataSet.value");

	
	private int _iCurrent = -1;
	private Object[] _currentRow;

	private List<Object[]> _data;

	private DataSetDefinition _dataSetDefinition;

	public JavabeanDataSet()
	{
		super();
		commonCtor();
	}

	public JavabeanDataSet(Object bean) throws DataSetException
	{
		super();
		setJavabean(bean);
	}

	public void setJavabean(Object bean) throws DataSetException
	{
		commonCtor();
		if (bean != null)
		{
			try
			{
				BeanInfo info = Introspector.getBeanInfo(bean.getClass(),
												Introspector.USE_ALL_BEANINFO);
				processBeanInfo(bean, info);
			}
			catch (Exception ex)
			{
				throw new DataSetException(ex);
			}
		}
	}

	private void processBeanInfo(Object bean, BeanInfo info)
		throws InvocationTargetException, IllegalAccessException
	{
		BeanInfo[] extra = info.getAdditionalBeanInfo();
		if (extra != null)
		{
			for (int i = 0; i < extra.length; ++i)
			{
				processBeanInfo(bean, extra[i]);
			}
		}

		PropertyDescriptor[] propDesc = info.getPropertyDescriptors();
		for (int i = 0; i < propDesc.length; ++i)
		{
			final String propName = propDesc[i].getName();
			final Method getter = propDesc[i].getReadMethod();
			if (propName != null && getter != null)
			{
                String displayName = propDesc[i].getDisplayName();
                if (displayName == null)
                {
                    displayName = propName;
                }
				final Object[] line = generateLine(displayName, bean, getter);
				if (line != null)
				{
					_data.add(line);
				}
			}
		}
	}

	
	protected Object[] generateLine(String propTitle, Object bean, Method getter)
		throws InvocationTargetException, IllegalAccessException
	{
		final Object[] line = new Object[2];
		line[0] = propTitle;
		line[1] = executeGetter(bean, getter);
		return line;
	}

	protected Object executeGetter(Object bean, Method getter)
		throws InvocationTargetException, IllegalAccessException
	{
		return getter.invoke(bean, (Object[])null);
	}

	public final int getColumnCount()
	{
		return 2;
	}

	public DataSetDefinition getDataSetDefinition()
	{
		return _dataSetDefinition;
	}

	public synchronized boolean next(IMessageHandler msgHandler)
		throws DataSetException
	{
		
		if (++_iCurrent < _data.size())
		{
			_currentRow = _data.get(_iCurrent);
			return true;
		}
		return false;
	}

	public synchronized Object get(int columnIndex)
	{
		return _currentRow[columnIndex];
	}

	private ColumnDisplayDefinition[] createColumnDefinitions()
	{
		ColumnDisplayDefinition[] columnDefs = new ColumnDisplayDefinition[2];
		columnDefs[0] = new ColumnDisplayDefinition(50, _nameColumnName);
		columnDefs[1] = new ColumnDisplayDefinition(50, _valueColumnName);
		return columnDefs;
	}

	private void commonCtor()
	{
		_iCurrent = -1;
		_data = new ArrayList<Object[]>();

		ColumnDisplayDefinition[] colDefs = createColumnDefinitions();
		_dataSetDefinition = new DataSetDefinition(colDefs);
	}
}
