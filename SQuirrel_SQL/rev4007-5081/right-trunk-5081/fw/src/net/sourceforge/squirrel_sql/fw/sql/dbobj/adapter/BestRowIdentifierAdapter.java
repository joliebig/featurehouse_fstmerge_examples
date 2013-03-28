package net.sourceforge.squirrel_sql.fw.sql.dbobj.adapter;

import java.sql.DatabaseMetaData;

import net.sourceforge.squirrel_sql.fw.sql.dbobj.BestRowIdentifier;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

public class BestRowIdentifierAdapter
{
	
    private static final StringManager s_stringMgr =
        StringManagerFactory.getStringManager(BestRowIdentifierAdapter.class);

	
	public interface IPropertyNames
	{
		String COLUMN_NAME = "columnName";
		String PRECISION = "precision";
		String PSEUDO = "pseudoColumn";
		String SCALE = "scale";
		String SCOPE = "scope";
		String SQL_DATA_TYPE = "sqlDataType";
		String TYPE_NAME = "typeName";
	}

	private final BestRowIdentifier _viewObj;

	public BestRowIdentifierAdapter(BestRowIdentifier obj)
	{
		if (obj == null)
		{
			throw new IllegalArgumentException("BestRowIdentifier == null");
		}
		_viewObj = obj;
	}

	public String getColumnName()
	{
		return _viewObj.getColumnName();
	}

	public short getSQLDataType()
	{
		return _viewObj.getSQLDataType();
	}

	public String getTypeName()
	{
		return _viewObj.getTypeName();
	}

	public int getPrecision()
	{
		return _viewObj.getPrecision();
	}

	public short getScale()
	{
		return _viewObj.getScale();
	}

	public String getScope()
	{
		final int scope = _viewObj.getScope();
		switch (scope)
		{
			case DatabaseMetaData.bestRowTemporary:
				return s_stringMgr.getString("BestRowIdentifierAdapter.temporary");
			case DatabaseMetaData.bestRowTransaction:
				return s_stringMgr.getString("BestRowIdentifierAdapter.transaction");
			case DatabaseMetaData.bestRowSession:
				return s_stringMgr.getString("BestRowIdentifierAdapter.session");
			default:
				return s_stringMgr.getString("BestRowIdentifierAdapter.unknown");
		}
	}

	public String getPseudoColumn()
	{
		final short value = _viewObj.getPseudoColumn();
		switch (value)
		{
			case DatabaseMetaData.bestRowPseudo:
				return s_stringMgr.getString("BestRowIdentifierAdapter.pseudo");
			case DatabaseMetaData.bestRowNotPseudo:
				return s_stringMgr.getString("BestRowIdentifierAdapter.notPseudo");
			case DatabaseMetaData.bestRowUnknown:
				return s_stringMgr.getString("BestRowIdentifierAdapter.unknownPseudo");
			default:
				return s_stringMgr.getString("BestRowIdentifierAdapter.unknown");
		}
	}

}
