package net.sourceforge.squirrel_sql.plugins.mssql.action;



import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.JFileChooser;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.SQLDriverProperty;
import net.sourceforge.squirrel_sql.fw.sql.WrappedSQLException;
import net.sourceforge.squirrel_sql.fw.util.*;

import net.sourceforge.squirrel_sql.plugins.mssql.MssqlPlugin;
import net.sourceforge.squirrel_sql.plugins.mssql.gui.GenerateSqlDialog;
import net.sourceforge.squirrel_sql.fw.util.ExtensionFilter;
import net.sourceforge.squirrel_sql.plugins.mssql.util.MssqlIntrospector;

public class GenerateSqlCommand implements ICommand {

	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(GenerateSqlCommand.class);

	private ISession _session;
	private final MssqlPlugin _plugin;

	private final IDatabaseObjectInfo[] _dbObjs;

	public GenerateSqlCommand(ISession session, MssqlPlugin plugin, IDatabaseObjectInfo[] dbObjs) {
		super();
		if (session == null)
			throw new IllegalArgumentException("ISession == null");
		if (plugin == null)
			throw new IllegalArgumentException("MssqlPlugin == null");
		if (dbObjs == null)
			throw new IllegalArgumentException("IDatabaseObjectInfo[] is null");

		_session = session;
		_plugin = plugin;
		_dbObjs = dbObjs;
	}

	public void execute() throws BaseException {
		try {
            
            
            SQLDriverProperty[] props = _session.getSQLConnection().getConnectionProperties().getDriverProperties();
            for (int i = 0; i < props.length; i++) {
                if (props[i].getName().equals("DBNAME")) {
                    
                    if (!props[i].getValue().equals(_session.getSQLConnection().getCatalog())) {

							   String[] params = {props[i].getValue(), _session.getSQLConnection().getCatalog()};
							   
								_session.getApplication().showErrorDialog(s_stringMgr.getString("mmsql.catalogErr", params));
                        return;
                    }
                }
            }
            
			GenerateSqlDialog dlog = new GenerateSqlDialog(_session, _plugin, _dbObjs);
            dlog.preselectObjects(_dbObjs);
			dlog.pack();
			GUIUtils.centerWithinParent(dlog);
			if (!dlog.showGeneralSqlDialog())
                return;
            
            JFileChooser fc = new JFileChooser();
            if (dlog.getOneFile()) {
                ExtensionFilter ef = new ExtensionFilter();
					 
					 ef.addExtension(s_stringMgr.getString("mmsql.sqlScripts"),"sql");
					 
					 ef.addExtension(s_stringMgr.getString("mmsql.textFiles"),"txt");
                fc.setFileFilter(ef);
            }
            else
                fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                            
            if (fc.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
                
                FileWriter fw = null;
                
                if (dlog.getOneFile()) {
                    fw = new FileWriter(fc.getSelectedFile(),false);
                    
                    if (dlog.getScriptDatabase())
                        fw.write(MssqlIntrospector.generateCreateDatabaseScript(_session.getSQLConnection().getCatalog(),_session.getSQLConnection()));
                    
                    if (dlog.getScriptUsersAndRoles())
                        fw.write(MssqlIntrospector.generateUsersAndRolesScript(_session.getSQLConnection().getCatalog(),_session.getSQLConnection()));
                }
                
                ArrayList<IDatabaseObjectInfo> objs = dlog.getSelectedItems();
                for (int i = 0; i < objs.size(); i++) {
                    IDatabaseObjectInfo oi = objs.get(i);
                    
                    if (!dlog.getOneFile())
                        fw = new FileWriter(fc.getSelectedFile() + java.io.File.separator + MssqlIntrospector.getFixedVersionedObjectName(oi.getSimpleName()) + ".txt",false);
                    
                    if (dlog.getGenerateDrop())
                        fw.write(MssqlIntrospector.generateDropScript(oi));
                    
                    if (dlog.getGenerateCreate()) {
                        String script = MssqlIntrospector.generateCreateScript(oi, _session.getSQLConnection(),dlog.getScriptConstraints());
                        fw.write(script);
                    }
                    
                    if (dlog.getScriptIndexes()) {
                        String script = MssqlIntrospector.generateCreateIndexesScript(oi, _session.getSQLConnection());
                        fw.write(script);
                    }
                    
                    if (dlog.getScriptTriggers()) {
                        String script = MssqlIntrospector.generateCreateTriggersScript(oi, _session.getSQLConnection());
                        fw.write(script);
                    }
                    
                    if (dlog.getScriptPermissions()) {
                        String script = MssqlIntrospector.generatePermissionsScript(oi, _session.getSQLConnection());
                        fw.write(script);
                    }
                    
                    if (!dlog.getOneFile())
                        fw.close();
                }
                if (dlog.getOneFile())
                    fw.close();
            }
		}
		catch (SQLException ex) {
            ex.printStackTrace();
			throw new WrappedSQLException(ex);
		}
        catch (IOException ex) {
            ex.printStackTrace();
			_session.getApplication().showErrorDialog(ex.getMessage());
        }
	}
    
}
