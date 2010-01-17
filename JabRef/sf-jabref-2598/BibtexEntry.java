

package net.sf.jabref;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.beans.VetoableChangeSupport;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import net.sf.jabref.export.FieldFormatter;


public class BibtexEntry
{
    public final static String ID_FIELD = "id";
    private String _id;
    private BibtexEntryType _type;
    private Map<String, String> _fields = new HashMap<String, String>();
    VetoableChangeSupport _changeSupport = new VetoableChangeSupport(this);

    
    private boolean searchHit, groupHit;

    public BibtexEntry(){
    	this(Util.createNeutralId());
    }
    
    public BibtexEntry(String id)
    {
        this(id, BibtexEntryType.OTHER);
    }

    public BibtexEntry(String id, BibtexEntryType type)
    {
        if (id == null)
        {
            throw new NullPointerException("Every BibtexEntry must have an ID");
        }

        _id = id;
        setType(type);
    }

    
    public String[] getOptionalFields()
    {
        return _type.getOptionalFields();
    }

    
    public String[] getRequiredFields()
    {
        return _type.getRequiredFields();
    }

    
    public String[] getGeneralFields() {
        return _type.getGeneralFields();
    }

    
    public Set<String> getAllFields() {
        return new TreeSet<String>(_fields.keySet());
    }

    
    public String describeRequiredFields()
    {
        return _type.describeRequiredFields();
    }

    
    public boolean hasAllRequiredFields(BibtexDatabase database)
    {
        return _type.hasAllRequiredFields(this, database);
    }

    
    public BibtexEntryType getType()
    {
        return _type;
    }

    
    public void setType(BibtexEntryType type)
    {
        if (type == null)
        {
            throw new NullPointerException(
                "Every BibtexEntry must have a type.  Instead of null, use type OTHER");
        }

        BibtexEntryType oldType = _type;

        try {
            
            
            
            _type = type;
            firePropertyChangedEvent(GUIGlobals.TYPE_HEADER,
                    oldType != null ? oldType.getName() : null,
                    type.getName());
        } catch (PropertyVetoException pve) {
            pve.printStackTrace();
        }


    }

    
    public boolean updateType() {
        BibtexEntryType newType = BibtexEntryType.getType(_type.getName());
        if (newType != null) {
            _type = newType;
            return true;
        }
        _type = BibtexEntryType.TYPELESS;
        return false;
    }

    
    public void setId(String id) throws KeyCollisionException {

        if (id == null) {
            throw new
                NullPointerException("Every BibtexEntry must have an ID");
        }

        try
        {
            firePropertyChangedEvent(ID_FIELD, _id, id);
        }
        catch (PropertyVetoException pv)
        {
            throw new KeyCollisionException("Couldn't change ID: " + pv);
        }

        _id = id;
    }

    
    public String getId()
    {
        return _id;
    }

    
    public String getField(String name) {
        return _fields.get(name);
    }

    public String getCiteKey() {
        return (_fields.containsKey(BibtexFields.KEY_FIELD) ?
                _fields.get(BibtexFields.KEY_FIELD) : null);
    }

    
    public void setField(Map<String, String> fields){
        _fields.putAll(fields);
    }

    
    public void setField(String name, String value) {

        if (ID_FIELD.equals(name)) {
            throw new IllegalArgumentException("The field name '" + name +
                                               "' is reserved");
        }

        String oldValue = _fields.get(name);
        try {
            
            
            
            _fields.put(name, value);
            firePropertyChangedEvent(name, oldValue, value);
        } catch (PropertyVetoException pve) {
            
            
            _fields.put(name, oldValue);
            throw new IllegalArgumentException("Change rejected: " + pve);
        }

    }

    
    public void clearField(String name) {

      if (ID_FIELD.equals(name)) {
           throw new IllegalArgumentException("The field name '" + name +
                                              "' is reserved");
       }
       Object oldValue = _fields.get(name);
       _fields.remove(name);
       try {
           firePropertyChangedEvent(name, oldValue, null);
       } catch (PropertyVetoException pve) {
           throw new IllegalArgumentException("Change rejected: " + pve);
       }


    }

    
    protected boolean allFieldsPresent(String[] fields, BibtexDatabase database) {
        for (int i = 0; i < fields.length; i++) {
            if (BibtexDatabase.getResolvedField(fields[i], this, database) == null) {
                return false;
            }
        }

        return true;
    }

    private void firePropertyChangedEvent(String fieldName, Object oldValue,
        Object newValue) throws PropertyVetoException
    {
        _changeSupport.fireVetoableChange(new PropertyChangeEvent(this,
                fieldName, oldValue, newValue));
    }

    
    public void addPropertyChangeListener(VetoableChangeListener listener)
    {
        _changeSupport.addVetoableChangeListener(listener);
    }

    
    public void removePropertyChangeListener(VetoableChangeListener listener)
    {
        _changeSupport.removeVetoableChangeListener(listener);
    }

    
    public void write(Writer out, FieldFormatter ff, boolean write) throws IOException {
        
        out.write("@"+_type.getName().toUpperCase()+"{");

        String str = Util.shaveString(getField(BibtexFields.KEY_FIELD));
        out.write(((str == null) ? "" : str)+","+Globals.NEWLINE);
        HashMap<String, String> written = new HashMap<String, String>();
        written.put(BibtexFields.KEY_FIELD, null);
        boolean hasWritten = false;
        
        String[] s = getRequiredFields();
        if (s != null) for (int i=0; i<s.length; i++) {
            hasWritten = hasWritten | writeField(s[i], out, ff, hasWritten);
            written.put(s[i], null);
        }
        
        s = getOptionalFields();
        if (s != null) for (int i=0; i<s.length; i++) {
            if (!written.containsKey(s[i])) { 
                
                hasWritten = hasWritten | writeField(s[i], out, ff, hasWritten);
                written.put(s[i], null);
            }
        }
        
        TreeSet<String> remainingFields = new TreeSet<String>();
        for (String key : _fields.keySet()){
            boolean writeIt = (write ? BibtexFields.isWriteableField(key) :
                               BibtexFields.isDisplayableField(key));
            if (!written.containsKey(key) && writeIt)
                       remainingFields.add(key);
        }
        for (String field: remainingFields)
            hasWritten = hasWritten | writeField(field, out, ff, hasWritten);

        
        out.write((hasWritten ? Globals.NEWLINE : "")+"}"+Globals.NEWLINE);
    }

    
    private boolean writeField(String name, Writer out,
                            FieldFormatter ff, boolean isFirst) throws IOException {
        String o = getField(name);
        if (o != null) {
            if (isFirst)
                out.write(","+Globals.NEWLINE);
            out.write("  "+name+" = ");

            try {
                out.write(ff.format(o.toString(), name));
            } catch (Throwable ex) {
                throw new IOException
                    (Globals.lang("Error in field")+" '"+name+"': "+ex.getMessage());
            }
            return true;
        } else
            return false;
    }

    
    public Object clone() {
        BibtexEntry clone = new BibtexEntry(_id, _type);
        clone._fields = new HashMap<String, String>(_fields); 
        return clone;
    }

    public String toString() {
        return getType().getName()+":"+getField(BibtexFields.KEY_FIELD);
    }

    public boolean isSearchHit() {
        return searchHit;
    }

    public void setSearchHit(boolean searchHit) {
        this.searchHit = searchHit;
    }

    public boolean isGroupHit() {
        return groupHit;
    }

    public void setGroupHit(boolean groupHit) {
        this.groupHit = groupHit;
    }

    
    public String getAuthorTitleYear(int maxCharacters) {
        String[] s = new String[] {
                getField("author"),
                getField("title"),
                getField("year")};
        for (int i = 0; i < s.length; ++i)
            if (s[i] == null)
                s[i] = "N/A";
        String text = s[0] + ": \"" + s[1] + "\" (" + s[2] + ")";
        if (maxCharacters <= 0 || text.length() <= maxCharacters)
            return text;
        return text.substring(0, maxCharacters + 1) + "...";
    }
    
}
