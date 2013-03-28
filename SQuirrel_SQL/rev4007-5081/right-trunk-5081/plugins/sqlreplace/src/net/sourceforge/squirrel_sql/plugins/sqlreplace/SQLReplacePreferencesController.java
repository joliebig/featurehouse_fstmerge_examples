
package net.sourceforge.squirrel_sql.plugins.sqlreplace;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;

import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.preferences.IGlobalPreferencesPanel;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;


public class SQLReplacePreferencesController implements IGlobalPreferencesPanel {


	   
	   protected SQLReplacementPreferencesPanel _pnlPrefs;

	   
	   protected IApplication _app;

	   
	   protected SQLReplacePlugin _plugin;
	   
	
	public SQLReplacePreferencesController(SQLReplacePlugin _plugin) {
		super();
		this._plugin = _plugin;
	}

	
	public void initialize(IApplication app) {
	      this._app = app;

	      _pnlPrefs.btnSave.addActionListener(new ActionListener()
	      {
	         public void actionPerformed(ActionEvent e)
	         {
	            onSave();
	         }
	      });
	      _pnlPrefs.replacementEditor.setText(_plugin.getReplacementManager().getContent()); 
	}

	
	public void uninitialize(IApplication app) {
	     
	}

	
	public void applyChanges() {
		if(_pnlPrefs.hasChanged())
		{
			onSave();
		}
	}

	
	public String getHint() {
	      return _plugin.getResourceString("prefs.hint");
	}

	
  	public Component getPanelComponent() {

  		
		_pnlPrefs = new SQLReplacementPreferencesPanel(_plugin);
		return _pnlPrefs;
	}

	
	public String getTitle() {
	      return _plugin.getResourceString("prefs.title");
	}

	private void onSave()
	{
		String content = _pnlPrefs.replacementEditor.getText();
		ReplacementManager repman = _plugin.getReplacementManager();
		repman.setContentFromEditor(content);
	}
	 
}
