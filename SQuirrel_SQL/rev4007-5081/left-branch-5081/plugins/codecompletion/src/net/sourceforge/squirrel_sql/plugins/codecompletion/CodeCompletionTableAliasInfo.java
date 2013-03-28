
package net.sourceforge.squirrel_sql.plugins.codecompletion;

import net.sourceforge.squirrel_sql.client.session.parser.kernel.TableAliasInfo;

public class CodeCompletionTableAliasInfo extends CodeCompletionTableInfo
{
	private TableAliasInfo _aliasInfo;

   private String _toString;

   public CodeCompletionTableAliasInfo(TableAliasInfo aliasInfo)
	{
		super(aliasInfo.tableName, "TABLE", null, null);
		_aliasInfo = aliasInfo;
      _toString = _aliasInfo.aliasName + " (Alias for " + _aliasInfo.tableName + ")";
	}

	public String getCompareString()
	{
		return _aliasInfo.aliasName;
	}

	public String toString()
	{
      return _toString;
	}

    public int getStatBegin()
    {
        return _aliasInfo.statBegin;
    }
}
