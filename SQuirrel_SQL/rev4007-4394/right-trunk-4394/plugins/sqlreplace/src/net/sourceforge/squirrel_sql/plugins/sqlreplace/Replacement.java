
package net.sourceforge.squirrel_sql.plugins.sqlreplace;


public class Replacement {
	   
	   protected String _variable;

	   private String _value;

	   public Replacement()
	   {
	      this(null, null);
	   }

	   public Replacement(String variable, String value)
	   {
		   _variable = variable;
	      _value = value;
	   }

	public String getVariable() {
		return _variable;
	}

	public void setVariable(String _variable) {
		this._variable = _variable;
	}

	public String getRegexVariable() {
		return _variable.replace("$", "\\$");
	}
	
	public String getValue() {
		return _value;
	}

	public void setValue(String _value) {
		this._value = _value;
	}

	public String toString() {
		return getVariable() + " = " + getValue();
	}

	   
}
