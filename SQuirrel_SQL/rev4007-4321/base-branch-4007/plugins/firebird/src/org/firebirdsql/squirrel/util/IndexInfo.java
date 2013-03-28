package org.firebirdsql.squirrel.util;


public class IndexInfo
{
	
	public interface IPropertyNames
	{
		String ACTIVE = "active";
		String DESCRIPTION = "description";
		String EXPRESSION_SOURCE = "expressionSource";
		String FOREIGN_KEY_CONSTRAINT = "foreignKeyConstraintName";
		String ID = "id";
		String NAME = "name";
		String RELATION_NAME = "relationName";
		String SEGMENT_COUNT = "segmentCount";
		String SYSTEM_DEFINED = "systemDefined";
		String UNIQUE = "unique";
	}

    private String _name;
    private String _description;
    private int _id;
    private String _relationName;
    private boolean _unique;
    private int _segmentCount;
    private boolean _active;
    private boolean _isSystemDefined;
    private String _foreignKeyConstraint;
    private String _expressionSource;

    public IndexInfo(String name, String description, int id,
                        String relationName, int unique, int segmentCount,
                        int inactive, int isSystemDefined,
                        String foreignKeyConstraint, String expressionSource)
    {
        super();

        _name = name;
        _description = description;
        _id = id;
        _relationName = relationName;
        _unique = unique == 1;
        _segmentCount = segmentCount;
        _active = inactive == 0;
        _isSystemDefined = isSystemDefined > 0;
        _foreignKeyConstraint = foreignKeyConstraint;
        _expressionSource = expressionSource;
    }

    public String getName()
    {
        return _name;
    }

    public void setName(String value)
    {
        _name = value;
    }

    public String getDescription()
    {
        return _description;
    }

    public void setDescription(String value)
    {
        _description = value;
    }

    public String getRelationName()
    {
        return _relationName;
    }

    public void setRelationName(String value)
    {
        _description = value;
    }

    public int getId()
    {
        return _id;
    }

    public void setId(int value)
    {
        _id = value;
    }

    public boolean isUnique()
    {
        return _unique;
    }

    public void setUnique(boolean value)
    {
        _unique = value;
    }

    public int getSegmentCount()
    {
        return _segmentCount;
    }

    public void setSegmentCount(int value)
    {
        _segmentCount = value;
    }

    public boolean isActive()
    {
        return _active;
    }

    public void setActive(boolean value)
    {
        _active = value;
    }

    public boolean isSystemDefined()
    {
        return _isSystemDefined;
    }

    public void setSystemDefined(boolean value)
    {
        _isSystemDefined = value;
    }

    public String getForeignKeyConstraintName()
    {
        return _foreignKeyConstraint;
    }

    public void setForeignKeyConstraintName(String value)
    {
        _foreignKeyConstraint = value;
    }

    public String getExpressionSource()
    {
        return _expressionSource;
    }

    public void setExpressionSource(String value)
    {
        _expressionSource = value;
    }
}
