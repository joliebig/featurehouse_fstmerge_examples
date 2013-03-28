package net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent;





public class DefaultColumnRenderer
{
	
	private static final DefaultColumnRenderer s_instance = new DefaultColumnRenderer();

	
	protected DefaultColumnRenderer()
	{
		super();
	}

	
	public static DefaultColumnRenderer getInstance()
	{
		return s_instance;
	}

	
	public Object renderObject(Object obj)
	{
		if (obj != null)
		{
			return obj.toString();
		}
		return "<null>";
	}
}
