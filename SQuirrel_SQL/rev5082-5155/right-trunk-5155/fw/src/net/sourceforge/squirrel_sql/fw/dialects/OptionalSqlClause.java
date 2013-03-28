
package net.sourceforge.squirrel_sql.fw.dialects;

import org.antlr.stringtemplate.StringTemplate;

import net.sourceforge.squirrel_sql.fw.util.StringUtilities;


public class OptionalSqlClause
{
	
	private String _staticPart = null;
	private String _variablePart = null;
	private StringTemplate _st = null;
	private String _key = null;
	private String _value = null;
	
	public OptionalSqlClause(String staticPart, String variablePart) {
		_staticPart = staticPart;
		_variablePart = variablePart;
	}
		
	public OptionalSqlClause(StringTemplate st, String key, String value) {
		this._st = st;
		this._key = key;
		this._value = value;
	}
	
	public String toString() {
		String result = "";
		if (_st != null) {
			if (_value != null) {
				_st.setAttribute(_key, _value);
				result = _st.toString();
			}
		} else {
			if (!StringUtilities.isEmpty(_variablePart)) {
				result = new StringBuilder(_staticPart).append(" ").append(_variablePart).toString();
			}
		}
		return result;
		
	}
}
