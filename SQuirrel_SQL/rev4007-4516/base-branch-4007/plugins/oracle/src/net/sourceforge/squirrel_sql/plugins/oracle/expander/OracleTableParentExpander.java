package net.sourceforge.squirrel_sql.plugins.oracle.expander;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.ObjectTreeNode;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.expanders.TableTypeExpander;
import net.sourceforge.squirrel_sql.plugins.oracle.prefs.OraclePreferenceBean;

public class OracleTableParentExpander extends TableTypeExpander 
{
    
    private OraclePreferenceBean _prefs = null;
    
	
	public OracleTableParentExpander(OraclePreferenceBean prefs)
	{
		super();
        _prefs = prefs;
	}

	
    public List<ObjectTreeNode> createChildren(ISession session, 
                                               ObjectTreeNode parentNode) 
        throws SQLException
	{
		final List<ObjectTreeNode> childNodes = super.createChildren(session, parentNode);
        List<ObjectTreeNode> result = new ArrayList<ObjectTreeNode> ();
		for (ObjectTreeNode childNode : childNodes) {
            if (_prefs.isExcludeRecycleBinTables()) {
                if (!childNode.getUserObject().toString().startsWith("BIN$")) {
                    result.add(childNode);
                }                
            } else {
                result.add(childNode);
            }
        }
        return result;
	}
}
