package net.sourceforge.squirrel_sql.fw.persist;


public interface IPersistable extends IDirty, IValidatable
{
	void load();
	void save();
}
