package net.sourceforge.squirrel_sql.client.session.action;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;

import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.util.ICommand;

import net.sourceforge.squirrel_sql.client.session.IObjectTreeAPI;

public class CopyObjectNameCommand implements ICommand
{
	
	public interface ICopyTypes
	{
		int SIMPLE_NAME = 0;
		int QUALIFIED_NAME = 1;
	}

	private IObjectTreeAPI _api;

	
	private int _copyType;

	
	public CopyObjectNameCommand(IObjectTreeAPI api, int copyType)
	{
		super();
		if (api == null)
		{
			throw new IllegalArgumentException("IObjectTreeAPI == null");
		}
		if (copyType < ICopyTypes.SIMPLE_NAME || copyType > ICopyTypes.QUALIFIED_NAME)
		{
			throw new IllegalArgumentException("Invalid copyType of : " + copyType + " passed");
		}

		_api = api;
		_copyType = copyType;
	}

	
	public void execute()
	{
		final StringBuffer buf = new StringBuffer(100);
		final IDatabaseObjectInfo[] dbObjs = _api.getSelectedDatabaseObjects();

		
		for (int i = 0; i < dbObjs.length; i++)
		{
			final IDatabaseObjectInfo doi = dbObjs[i];
			final String name = _copyType == ICopyTypes.SIMPLE_NAME
									? doi.getSimpleName()
									: doi.getQualifiedName();
			buf.append(name).append(", ");
		}
		if (buf.length() > 0)
		{
			buf.setLength(buf.length() - 2);	
			Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();
			StringSelection data = new StringSelection(buf.toString());
			clip.setContents(data, data);
		}
	}
}
