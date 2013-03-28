package net.sourceforge.squirrel_sql.plugins.favs;

import java.io.Serializable;

import net.sourceforge.squirrel_sql.client.util.IdentifierFactory;
import net.sourceforge.squirrel_sql.fw.id.IHasIdentifier;
import net.sourceforge.squirrel_sql.fw.id.IIdentifier;
import net.sourceforge.squirrel_sql.fw.persist.IValidatable;


final class Query implements Cloneable, Serializable, IHasIdentifier, IValidatable {

    private static final long serialVersionUID = 1L;

    private static final String EMPTY_STRING = "";


	public interface IPropertyNames {
		String DESCRIPTION = "Description";
		String ID = "Identifier";
		String NAME = "Name";
		String SQL = "Sql";
	}

		private IIdentifier _id;

	
	private String _name;

	
    @SuppressWarnings("unused")
	private String _description;

	
	private String _sql;

	
	public Query() {
		this(IdentifierFactory.getInstance().createIdentifier(), EMPTY_STRING,
				EMPTY_STRING, EMPTY_STRING);
	}

	
	public Query(IIdentifier id, String name, String description, String sql) {
		super();
		_id = id != null ? id : IdentifierFactory.getInstance().createIdentifier();
		_name = getString(name);
		_description = getString(description);
		_sql = getString(sql);
	}

	
	public boolean equals(Object rhs) {
		boolean rc = false;
		if (rhs != null && rhs.getClass().equals(getClass())) {
			rc = ((Query)rhs).getIdentifier().equals(getIdentifier());
		}
		return rc;
	}

	
	public Object clone() {
		try {
			return super.clone();
		} catch(CloneNotSupportedException ex) {
			throw new InternalError(ex.getMessage());   
		}
	}

	public synchronized int hashCode() {
		return getIdentifier().hashCode();
	}

	public String toString() {
		return getName();
	}

	
	public synchronized boolean isValid() {
		return _name.trim().length() > 0 && _sql.trim().length() > 0;
	}

	public IIdentifier getIdentifier() {
		return _id;
	}

	public String getName() {
		return _name;
	}

	private String getString(String data) {
		return data != null ? data.trim() : "";
	}
}
