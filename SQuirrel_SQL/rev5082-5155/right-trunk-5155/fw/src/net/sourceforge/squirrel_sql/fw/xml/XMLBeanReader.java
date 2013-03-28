package net.sourceforge.squirrel_sql.fw.xml;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.n3.nanoxml.IXMLElement;
import net.n3.nanoxml.IXMLParser;
import net.n3.nanoxml.StdXMLReader;
import net.n3.nanoxml.XMLParserFactory;
import net.sourceforge.squirrel_sql.fw.util.EnumerationIterator;
import net.sourceforge.squirrel_sql.fw.util.beanwrapper.StringWrapper;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

public class XMLBeanReader implements Iterable<Object>
{
	
	private static final ILogger s_log =
		LoggerController.createLogger(XMLBeanReader.class);

	private String[][] _fixStrings = new String[][]
	{
		{
			"com.bigfoot.colbell.squirrel",
			"net.sourceforge.squirrel_sql.client"
		},
		{
			"com.bigfoot.colbell.fw",
			"net.sourceforge.squirrel_sql.fw"
		},
		{
			"net.sourceforge.squirrel_sql.client.mainframe.MainFrameWindowState",
			"net.sourceforge.squirrel_sql.client.gui.mainframe.MainFrameWindowState"
		}
	};

	private ClassLoader _cl;
	private final List<Object> _beanColl = new ArrayList<Object>();

	public XMLBeanReader()
	{
		super();
	}

	public void load(File xmlFile) throws FileNotFoundException, XMLException
	{
		load(xmlFile, null);
	}

	public void load(File xmlFile, ClassLoader cl)
		throws FileNotFoundException, XMLException
	{
		if (!xmlFile.exists())
		{
			throw new FileNotFoundException(xmlFile.getName());
		}
		load(xmlFile.getAbsolutePath(), cl);
	}

	public void load(String xmlFileName)
		throws FileNotFoundException, XMLException
	{
		load(xmlFileName, null);
	}

	public synchronized void load(String xmlFileName, ClassLoader cl)
		throws FileNotFoundException, IllegalArgumentException, XMLException
	{
		if (xmlFileName == null)
		{
			throw new IllegalArgumentException("Null xmlFileName passed");
		}

		_cl = cl;
		_beanColl.clear();

		FileReader frdr = new FileReader(xmlFileName);
		try
		{
			load(frdr, cl);
		}
		finally
		{
			try
			{
				frdr.close();
			}
			catch (IOException ex)
			{
				s_log.error("Error closing FileReader", ex);
			}
		}
	}

	public void load(Reader rdr) throws XMLException
	{
		load(rdr, null);
	}

	public void load(Reader rdr, ClassLoader cl) throws XMLException
	{
		try
		{
			final IXMLParser parser = XMLParserFactory.createDefaultXMLParser();
			parser.setReader(new StdXMLReader(rdr));
			IXMLElement element = (IXMLElement) parser.parse();
			Iterator it = new EnumerationIterator(element.enumerateChildren());
			while (it.hasNext())
			{
				final IXMLElement elem = (IXMLElement) it.next();
				if (isBeanElement(elem))
				{
					_beanColl.add(loadBean(elem));
				}
			}
		}
		catch (Exception ex)
		{
			throw new XMLException(ex);
		}
	}

	public Iterator<Object> iterator()
	{
		return _beanColl.iterator();
	}

	private Object loadBean(IXMLElement beanElement) throws XMLException
	{
	    String beanClassName = null;
		try
		{
			beanClassName = getClassNameFromElement(beanElement);
			beanClassName = fixClassName(beanClassName);
			Class beanClass = null;
			if (_cl == null)
			{
				beanClass = Class.forName(beanClassName);
			}
			else
			{
				beanClass = Class.forName(beanClassName, true, _cl);
			}
			Object bean = beanClass.newInstance();
			BeanInfo info =
				Introspector.getBeanInfo(
					bean.getClass(),
					Introspector.USE_ALL_BEANINFO);
			PropertyDescriptor[] propDesc = info.getPropertyDescriptors();
			Map<String, PropertyDescriptor> props = 
                new HashMap<String, PropertyDescriptor>();
			for (int i = 0; i < propDesc.length; ++i)
			{
				props.put(propDesc[i].getName(), propDesc[i]);
			}
			final List<IXMLElement> children = beanElement.getChildren();
			for (Iterator<IXMLElement> it = children.iterator(); it.hasNext();)
			{
				final IXMLElement propElem = it.next();
				final PropertyDescriptor curProp = props.get(propElem.getName());
				if (curProp != null)
				{
					loadProperty(bean, curProp, propElem);
				}
			}

			return bean;
		}
		catch (Exception ex)
		{
		    s_log.error(
		        "Unexpected exception while attempting to load xml bean "+
		        ex.getMessage(), ex);
			throw new XMLException(ex);
		}
	}

	private void loadProperty(
		Object bean,
		PropertyDescriptor propDescr,
		IXMLElement propElem)
		throws XMLException
	{
		final Method setter = propDescr.getWriteMethod();
		if (setter != null)
		{
			final Class parmType = setter.getParameterTypes()[0];
			final Class arrayType = parmType.getComponentType();
			final String value = propElem.getContent();
			if (isIndexedElement(propElem))
			{
				Object[] data = loadIndexedProperty(propElem);
				try
				{
					
					
					
					
					
					
					
					
					
					
					if (arrayType.getName().equals("java.lang.String")) {
						
						Object[] stringData = new Object[data.length];
						for (int i=0; i<data.length; i++)
							stringData[i] = ((StringWrapper)data[i]).getString();
						data = stringData;
					}

					Object obj = Array.newInstance(arrayType, data.length);
					System.arraycopy(data, 0, obj, 0, data.length);
					setter.invoke(bean, new Object[] { obj });
				}
				catch (Exception ex)
				{
					throw new XMLException(ex);
				}
			}
			else if (isBeanElement(propElem))
			{
				Object data = loadBean(propElem);
				try
				{
					setter.invoke(bean, new Object[] { data });
				}
				catch (Exception ex)
				{
					throw new XMLException(ex);
				}
			}
			else if (parmType == boolean.class)
			{
				Object data = Boolean.valueOf(value);
				try
				{
					setter.invoke(bean, new Object[] { data });
				}
				catch (Exception ex)
				{
					throw new XMLException(ex);
				}
			}
			else if (parmType == int.class)
			{
				Object data = new Integer(value);
				try
				{
					setter.invoke(bean, new Object[] { data });
				}
				catch (Exception ex)
				{
					throw new XMLException(ex);
				}
			}
			else if (parmType == short.class)
			{
				Object data = new Short(value);
				try
				{
					setter.invoke(bean, new Object[] { data });
				}
				catch (Exception ex)
				{
					throw new XMLException(ex);
				}
			}
			else if (parmType == long.class)
			{
				Object data = new Long(value);
				try
				{
					setter.invoke(bean, new Object[] { data });
				}
				catch (Exception ex)
				{
					throw new XMLException(ex);
				}
			}
			else if (parmType == float.class)
			{
				Object data = new Float(value);
				try
				{
					setter.invoke(bean, new Object[] { data });
				}
				catch (Exception ex)
				{
					throw new XMLException(ex);
				}
			}
			else if (parmType == double.class)
			{
				Object data = new Double(value);
				try
				{
					setter.invoke(bean, new Object[] { data });
				}
				catch (Exception ex)
				{
					throw new XMLException(ex);
				}
			}
			else if (parmType == char.class)
			{
				Object data;
				if (value != null && value.length() > 0)
				{
					data = new Character(value.charAt(0));
				}
				else
				{
					data = new Character(' ');
				}
				try
				{
					setter.invoke(bean, new Object[] { data });
				}
				catch (Exception ex)
				{
					throw new XMLException(ex);
				}
			}
			else
			{
				Object data = value;
				try
				{
					setter.invoke(bean, new Object[] { data });
				}
				catch (Exception ex)
				{
					throw new XMLException(ex);
				}
			}
		}
	}

	private Object[] loadIndexedProperty(IXMLElement beanElement)
		throws XMLException
	{
		final List<Object> beans = new ArrayList<Object>();
		final List<IXMLElement> children = beanElement.getChildren();
		for (Iterator<IXMLElement> it = children.iterator(); it.hasNext();)
		{
			beans.add(loadBean(it.next()));
		}
		return beans.toArray(new Object[beans.size()]);
	}

	private boolean isBeanElement(IXMLElement elem)
	{
		return elem.getAttribute(XMLConstants.CLASS_ATTRIBUTE_NAME, null)
			!= null;
	}

	private boolean isIndexedElement(IXMLElement elem)
	{
		String att = elem.getAttribute(XMLConstants.INDEXED, "false");
		return att != null && att.equals("true");
	}

	private String getClassNameFromElement(IXMLElement elem)
	{
		return elem.getAttribute(XMLConstants.CLASS_ATTRIBUTE_NAME, null);
	}

	private String fixClassName(String className)
	{
		for (int i = 0; i < _fixStrings.length; ++i)
		{
			String from = _fixStrings[i][0];
			if (className.startsWith(from))
			{
				className =
					_fixStrings[i][1] + className.substring(from.length());
				break;
			}
		}
		return className;
	}
}
