package net.sourceforge.squirrel_sql.fw.id;

import java.io.Serializable;


public interface IIdentifier extends Serializable
{
	public boolean equals(Object rhs);

	public String toString();

	public int hashCode();
}
