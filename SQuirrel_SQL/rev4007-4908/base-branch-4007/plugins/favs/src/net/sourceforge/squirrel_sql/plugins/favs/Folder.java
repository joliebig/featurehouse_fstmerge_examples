package net.sourceforge.squirrel_sql.plugins.favs;

import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.squirrel_sql.client.util.IdentifierFactory;
import net.sourceforge.squirrel_sql.fw.id.IHasIdentifier;
import net.sourceforge.squirrel_sql.fw.id.IIdentifier;
import net.sourceforge.squirrel_sql.fw.persist.IValidatable;
import net.sourceforge.squirrel_sql.fw.persist.ValidationException;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;


public final class Folder implements Cloneable, Serializable, IHasIdentifier,
										IValidatable  {

    private static final long serialVersionUID = 1L;

    private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(Folder.class);

	private static final String EMPTY_STRING = "";

	
	private interface i18n {
		
		static String ERR_BLANK_NAME = s_stringMgr.getString("favs.nameMustNotBeBlank");
	}

	public interface IPropertyNames {
		String ID = "Identifier";
		String NAME = "Name";
		String SUB_FOLDERS = "SubFolders";
	}

	
	private IIdentifier _id;

	
	private String _name;

	
	private List<Folder> _subFolders = new ArrayList<Folder>();

	
	private transient PropertyChangeSupport _propChgNotifier = null;

	
	public Folder() {
		this(IdentifierFactory.getInstance().createIdentifier(), EMPTY_STRING);
	}

	
	public Folder(IIdentifier id, String name) {
		super();
		_id = id != null ? id : IdentifierFactory.getInstance().createIdentifier();
		_name = getString(name);
	}

	
	public boolean equals(Object rhs) {
		boolean rc = false;
		if (rhs != null && rhs.getClass().equals(getClass())) {
			rc = ((Folder)rhs).getIdentifier().equals(getIdentifier());
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
		return _name.trim().length() > 0;
	}

	public IIdentifier getIdentifier() {
		return _id;
	}

	public void setIdentifier(IIdentifier id) {
		_id = id;
	}

	public String getName() {
		return _name;
	}

	public void setName(String name)
			throws ValidationException {
		String data = getString(name);
		if (data.length() == 0) {
			throw new ValidationException(i18n.ERR_BLANK_NAME);
		}
		if (_name != data) {
			final String oldValue = _name;
			_name = data;
			getPropertyChangeNotifier().firePropertyChange(IPropertyNames.NAME, oldValue, _name);
		}
	}

	
	
	

	public void addSubFolder(Folder subFolder) throws IllegalArgumentException {
		if (subFolder == null) {
			throw new IllegalArgumentException("Null Folder passed");
		}
		_subFolders.add(subFolder);
	}

	public boolean removeSubFolder(Folder subFolder) throws IllegalArgumentException {
		if (subFolder == null) {
			throw new IllegalArgumentException("Null Folder passed");
		}
		return _subFolders.remove(subFolder);
	}

	public Iterator<Folder> subFolders() {
		return _subFolders.iterator();
	}

	public Folder[] getSubFolders() {
		return _subFolders.toArray(new Folder[_subFolders.size()]);
	}

	public Folder getSubFolder(int idx) throws ArrayIndexOutOfBoundsException {
		return _subFolders.get(idx);
	}

	public void setSubFolders(Folder[] value) {
		_subFolders.clear();
		if (value != null) {
			for (int i = 0; i < value.length; ++i) {
				_subFolders.add(value[i]);
			}
		}
	}

	public void setSubFolder(int idx, Folder value) throws ArrayIndexOutOfBoundsException {
		_subFolders.set(idx, value);
	}

	private PropertyChangeSupport getPropertyChangeNotifier() {
		if (_propChgNotifier == null) {
			_propChgNotifier = new PropertyChangeSupport(this);
		}
		return _propChgNotifier;
	}

	private String getString(String data) {
		return data != null ? data.trim() : "";
	}
}
