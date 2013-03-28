
package net.sourceforge.squirrel_sql.fw.dialects;


public class OptionalSqlClause
{
	
	private String _staticPart = null;
	private String _variablePart = null;
	
	public OptionalSqlClause(String staticPart, String variablePart) {
		_staticPart = staticPart;
		_variablePart = variablePart;
	}
	
	public String toString() {
		String result = "";
		if (_variablePart != null &&  !"".equals(_variablePart)) {
			
			result = new StringBuilder(_staticPart).append(" ").append(_variablePart).toString();
		} 
		return result;
	}
}
