
package net.sourceforge.squirrel_sql.plugins.firebirdmanager;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.plugins.firebirdmanager.gui.FirebirdManagerGrantDbObject;
import net.sourceforge.squirrel_sql.plugins.firebirdmanager.gui.FirebirdManagerPrivilege;

import org.firebirdsql.gds.GDSException;
import org.firebirdsql.gds.IscSvcHandle;
import org.firebirdsql.management.FBUser;
import org.firebirdsql.management.FBUserManager;

public class FirebirdManagerDataAccess {
	
    private final static ILogger log = LoggerController.createLogger(FirebirdManagerDataAccess.class);

    
    private FirebirdManagerDataAccess() {}

    
    
    @SuppressWarnings("unchecked")
    public static List<FBUser> getFirebirdUserList(ISession session) {
    	FBUserManager fbUserManager = new FBUserManager();
    	fbUserManager.setHost(FirebirdManagerHelper.getHost(session.getAlias().getUrl()));
    	fbUserManager.setPort(FirebirdManagerHelper.getPort(session.getAlias().getUrl()));
    	fbUserManager.setUser(session.getAlias().getUserName());
    	fbUserManager.setPassword(session.getAlias().getPassword());
		ArrayList<FBUser> listFBUser = new ArrayList<FBUser>();

    	try {
			IscSvcHandle iscSvcHandle = fbUserManager.attachServiceManager(fbUserManager.getGds());
			Map<String,FBUser> mapUsers = null;
			try {
				mapUsers = (Map<String,FBUser>)fbUserManager.getUsers();
			} catch (Exception e) {
				log.error(e.getLocalizedMessage());
			}

			Collection<FBUser> listUsers = (Collection<FBUser>)mapUsers.values();
			for (Iterator iter = listUsers.iterator(); iter.hasNext();) {
				listFBUser.add((FBUser) iter.next());
			}

			fbUserManager.detachServiceManager(fbUserManager.getGds(), iscSvcHandle);
			iscSvcHandle = null;
		} catch (GDSException e) {
			log.error(e.getLocalizedMessage());
		}
    	
    	return listFBUser;
    }
    
    
    
    public static List<String> getUsernameList(ISession session) {
    	List<FBUser> listFBUser = getFirebirdUserList(session);
    	List<String> listUsername = new ArrayList<String>();
    	
    	for (int i = 0; i < listFBUser.size(); i++) {
       		listUsername.add(listFBUser.get(i).getUserName());
    	}
    	
    	return listUsername;
    }
    

    
	public static List<String> getTriggerList(ISession session) {
		List<String> listTrigger = new ArrayList<String>();
	
		String sSQL = "Select RDB$TRIGGER_NAME, RDB$TRIGGER_TYPE," 
						+ " RDB$TRIGGER_SOURCE, RDB$TRIGGER_INACTIVE" 
						+ " From RDB$TRIGGERS" 
						+ " Where RDB$SYSTEM_FLAG = 0"
						+ " Order By RDB$TRIGGER_TYPE, RDB$TRIGGER_NAME";

		try {
			Statement stmt = session.getSQLConnection().createStatement();
			ResultSet result =
				stmt.executeQuery(sSQL);

            while (result.next()) {
               	listTrigger.add(result.getString("RDB$TRIGGER_NAME").trim());
            }
            result.close();
		} catch (Exception e) {
			log.error(e.getLocalizedMessage());
		}
		
		return listTrigger;
	}

	
	public static List<String> getRoleList(ISession session) {
		List<String> listRole = new ArrayList<String>();

		try {
			Statement stmt = session.getSQLConnection().createStatement();
			ResultSet rs = stmt.executeQuery("Select RDB$ROLE_NAME"
					+ " From RDB$ROLES Order By RDB$ROLE_NAME");
			while (rs.next()) {
				listRole.add(rs.getString("RDB$ROLE_NAME").trim());
			}
			rs.close();
		} catch (SQLException e) {
			log.error(e.getLocalizedMessage());
		}

		return listRole;
	}

	
	
	public static List<FirebirdManagerGrantDbObject> getTableList(ISession session) {
		List<FirebirdManagerGrantDbObject> listTable = new ArrayList<FirebirdManagerGrantDbObject>();

		String sql = "Select RDB$RELATION_NAME, RDB$OWNER_NAME, RDB$DESCRIPTION " 
				+ " From RDB$RELATIONS" 
				+ " Where RDB$SYSTEM_FLAG = 0"
				+ " Order By RDB$RELATION_NAME";
		try {
			Statement stmt = session.getSQLConnection().getConnection().createStatement();
			ResultSet rsTables = stmt.executeQuery(sql);
			while (rsTables.next()) {
				
				String description = rsTables.getString("RDB$DESCRIPTION");
				if (description != null) {
					description = description.trim();
				}
				listTable.add(new FirebirdManagerGrantDbObject(
						rsTables.getString("RDB$RELATION_NAME").trim(),
						rsTables.getString("RDB$OWNER_NAME").trim(),
						description));
			}
			rsTables.close();
			stmt.close();
		} catch (SQLException e) {
			log.error(e.getLocalizedMessage());
		}
		
		













		return listTable;
	}

	
	public static List<String> getViewList(ISession session) {
	    List<String> listView = new ArrayList<String>();
	    
		try {
			DatabaseMetaData dbmd = session.getSQLConnection().getConnection().getMetaData();
			String[] types = {"VIEW"};
			ResultSet rs = dbmd.getTables(null, null, "%", types);
			while (rs.next()) {
				listView.add(rs.getString("TABLE_NAME").trim());
			}
			rs.close();
		} catch (SQLException e) {
			log.error(e.getLocalizedMessage());
		}
	    
	    return listView;
	}

	
	public static List<String> getProcedureList(ISession session) {
	    List<String> listProcedure = new ArrayList<String>();
	    
		try {
			DatabaseMetaData dbmd = session.getSQLConnection().getConnection().getMetaData();
			ResultSet rsProcedures = dbmd.getProcedures(null, null, "%");
			while (rsProcedures.next()) {
		        listProcedure.add(rsProcedures.getString("PROCEDURE_NAME").trim());
			}
			rsProcedures.close();
		} catch (SQLException e) {
			log.error(e.getLocalizedMessage());
		}
	    
	    return listProcedure;
	}
	
	
	
    public static FirebirdManagerPrivilege readPrivileges(ISession session, String userName, 
    		String relationName, boolean groupHeader) {
    	FirebirdManagerPrivilege fbPrivilege = new FirebirdManagerPrivilege();
        
        if (!groupHeader) {
            try {
                Statement stmt = session.getSQLConnection().createStatement();
                ResultSet rs = stmt.executeQuery("Select RDB$PRIVILEGE From RDB$USER_PRIVILEGES" + 
                        " Where RDB$USER = '" + userName + "'" + 
                        " and RDB$RELATION_NAME = '" + relationName + "'" +
                        " Order By RDB$PRIVILEGE");
                while (rs.next()) {
                    String sValue = rs.getString(1).trim();
                    if (sValue.equals("S"))
                        fbPrivilege.setSelect(true);
                    else if (sValue.equals("I"))
                        fbPrivilege.setInsert(true);
                    else if (sValue.equals("U"))
                        fbPrivilege.setUpdate(true);
                    else if (sValue.equals("D"))
                        fbPrivilege.setDelete(true);
                    else if (sValue.equals("R"))
                        fbPrivilege.setReference(true);
                    else if (sValue.equals("X"))
                        fbPrivilege.setExecute(true);
                    else if (sValue.equals("M"))
                        fbPrivilege.setMember(true);
                } 
                rs.close();
                stmt.close();
            } catch (SQLException e) {
                log.error(e.getLocalizedMessage());
            }
        }
        
        return fbPrivilege;
    } 
	
	
}

