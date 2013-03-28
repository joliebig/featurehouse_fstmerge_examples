

package net.sourceforge.squirrel_sql.plugins.sqlbookmark;

import java.util.Iterator;

import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.*;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.*;

import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.client.preferences.IGlobalPreferencesPanel;
import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI;


public class SQLBookmarkPreferencesController implements IGlobalPreferencesPanel
{

   private static final StringManager s_stringMgr =
      StringManagerFactory.getStringManager(SQLBookmarkPreferencesController.class);


   
   protected SQLBookmarkPreferencesPanel _pnlPrefs;

   
   protected IApplication _app;

   
   protected SQLBookmarkPlugin _plugin;
   private DefaultMutableTreeNode _nodeSquirrelMarks;
   private DefaultMutableTreeNode _nodeUserMarks;



   
   public SQLBookmarkPreferencesController(SQLBookmarkPlugin plugin)
   {
      this._plugin = plugin;
   }

   
   public void initialize(IApplication app)
   {
      this._app = app;

      
      _pnlPrefs.btnEdit.setText(s_stringMgr.getString("sqlbookmark.btnTextEdit"));

      DefaultMutableTreeNode root = new DefaultMutableTreeNode("");

      
      _nodeSquirrelMarks = new DefaultMutableTreeNode(s_stringMgr.getString("sqlbookmark.nodeSquirrelMarks"));
      
      _nodeUserMarks = new DefaultMutableTreeNode(s_stringMgr.getString("sqlbookmark.nodeUserMarks"));

      root.add(_nodeUserMarks);
      root.add(_nodeSquirrelMarks);
      DefaultTreeModel dtm = new DefaultTreeModel(root);
      _pnlPrefs.treBookmarks.setModel(dtm);
      _pnlPrefs.treBookmarks.setRootVisible(false);

      _pnlPrefs.treBookmarks.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

      Bookmark[] defaultBookmarks = DefaultBookmarksFactory.getDefaultBookmarks();
      for (int i = 0; i < defaultBookmarks.length; i++)
      {
         _nodeSquirrelMarks.add(new DefaultMutableTreeNode(defaultBookmarks[i]));
      }

      for (Iterator<Bookmark> i = _plugin.getBookmarkManager().iterator(); i.hasNext();)
      {
         Bookmark mark = i.next();
         _nodeUserMarks.add(new DefaultMutableTreeNode(mark));
      }

      _pnlPrefs.treBookmarks.expandPath(new TreePath(dtm.getPathToRoot(_nodeUserMarks)));

      String propDefaultMarksInPopup =
         _plugin.getBookmarkProperties().getProperty(SQLBookmarkPlugin.BOOKMARK_PROP_DEFAULT_MARKS_IN_POPUP, "" + false);

      _pnlPrefs.chkSquirrelMarksInPopup.setSelected(Boolean.valueOf(propDefaultMarksInPopup).booleanValue());

      _pnlPrefs.treBookmarks.getSelectionModel().addTreeSelectionListener(new TreeSelectionListener()
      {
         public void valueChanged(TreeSelectionEvent e)
         {
            onTreeSelectionChanged(e);
         }
      });



      _pnlPrefs.btnRun.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            onRun();
         }
      });


      _pnlPrefs.btnAdd.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            onAdd();
         }
      });
      _pnlPrefs.btnEdit.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            onEdit();
         }
      });
      _pnlPrefs.btnDel.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            onDelete();
         }
      });

      _pnlPrefs.btnUp.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            onUp();
         }
      });

      _pnlPrefs.btnDown.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            onDown();
         }
      });


   }

   public void uninitialize(IApplication app)
   {
      _plugin.removeALLSQLPanelsAPIListeningForBookmarks();
   }

   
   public String getTitle()
   {
      return _plugin.getResourceString("prefs.title");
   }

   
   public String getHint()
   {
      return _plugin.getResourceString("prefs.hint");
   }

   
   public void applyChanges()
   {
      
      BookmarkManager bookmarks = _plugin.getBookmarkManager();

      bookmarks.removeAll();

      for (int i = 0; i < _nodeUserMarks.getChildCount(); ++i)
      {
         Bookmark bookmark = (Bookmark) ((DefaultMutableTreeNode) _nodeUserMarks.getChildAt(i)).getUserObject();
         bookmarks.add(bookmark);
      }

      
      _plugin.rebuildMenu();
      bookmarks.save();


      _plugin.getBookmarkProperties().put(SQLBookmarkPlugin.BOOKMARK_PROP_DEFAULT_MARKS_IN_POPUP, "" + _pnlPrefs.chkSquirrelMarksInPopup.isSelected());
      _plugin.saveBookmarkProperties();
   }

   
   public Component getPanelComponent()
   {
      
      _pnlPrefs = new SQLBookmarkPreferencesPanel(_plugin);

      return _pnlPrefs;
   }

   private void onTreeSelectionChanged(TreeSelectionEvent e)
   {
      if(null == e.getPath())
      {
         return;
      }

      DefaultMutableTreeNode dmtn = (DefaultMutableTreeNode) e.getPath().getLastPathComponent();

      if(false == dmtn.getUserObject() instanceof Bookmark)
      {
         return;
      }

      if(dmtn.getParent() == _nodeSquirrelMarks)
      {
         _pnlPrefs.btnUp.setEnabled(false);
         _pnlPrefs.btnDown.setEnabled(false);
         _pnlPrefs.btnDel.setEnabled(false);
         
         _pnlPrefs.btnEdit.setText(s_stringMgr.getString("sqlbookmark.btnTextView"));
      }
      else
      {
         _pnlPrefs.btnUp.setEnabled(true);
         _pnlPrefs.btnDown.setEnabled(true);
         _pnlPrefs.btnDel.setEnabled(true);
         
         _pnlPrefs.btnEdit.setText(s_stringMgr.getString("sqlbookmark.btnTextEdit"));
      }
   }



   private void onRun()
   {


      DefaultMutableTreeNode selNode = null;
      if(null != _pnlPrefs.treBookmarks.getSelectionPath())
      {
         selNode = (DefaultMutableTreeNode) _pnlPrefs.treBookmarks.getSelectionPath().getLastPathComponent();
      }

      if (null == selNode || false == selNode.getUserObject() instanceof Bookmark)
      {
         
         _app.getMessageHandler().showErrorMessage(s_stringMgr.getString("sqlbookmark.noRunSelection"));
         return;
      }



      ISQLPanelAPI[] apis = _plugin.getSQLPanelAPIsListeningForBookmarks();

      if(0 == apis.length)
      {
         
         JOptionPane.showMessageDialog(_app.getMainFrame(), s_stringMgr.getString("sqlbookmark.noSQLPanel"));
         return;
      }

      Bookmark bm = (Bookmark) selNode.getUserObject();

      for (int i = 0; i < apis.length; i++)
      {
         ISQLPanelAPI api = apis[i];
         new RunBookmarkCommand(_app.getMainFrame(), api.getSession(), bm, _plugin ,api.getSQLEntryPanel()).execute();
      }

   }



   public void onAdd()
   {
      BookmarEditController ctrlr = new BookmarEditController(_app.getMainFrame(), null, true);

      if (ctrlr.isCancelled())
      {
         return;
      }

      DefaultMutableTreeNode newChild = new DefaultMutableTreeNode(ctrlr.getBookmark());
      _nodeUserMarks.add(newChild);

      ((DefaultTreeModel)_pnlPrefs.treBookmarks.getModel()).nodeStructureChanged(_nodeUserMarks);

      selectNode(newChild);
   }

   private void selectNode(DefaultMutableTreeNode toSel)
   {
      TreeNode[] pathToRoot = ((DefaultTreeModel) _pnlPrefs.treBookmarks.getModel()).getPathToRoot(toSel);
      _pnlPrefs.treBookmarks.setSelectionPath(new TreePath(pathToRoot));
   }

   
   public void onEdit()
   {
      DefaultMutableTreeNode selNode = null;
      if(null != _pnlPrefs.treBookmarks.getSelectionPath())
      {
         selNode = (DefaultMutableTreeNode) _pnlPrefs.treBookmarks.getSelectionPath().getLastPathComponent();
      }

      if (null == selNode || false == selNode.getUserObject() instanceof Bookmark)
      {
         
         _app.getMessageHandler().showErrorMessage(s_stringMgr.getString("sqlbookmark.noEditSelection"));
         return;
      }

      boolean editable = selNode.getParent() == _nodeUserMarks;

      BookmarEditController ctrlr = new BookmarEditController(_app.getMainFrame(), (Bookmark) selNode.getUserObject(), editable);

      if(ctrlr.isCancelled())
      {
         return;
      }

      selNode.setUserObject(ctrlr.getBookmark());
   }


   public void onDelete()
   {

      DefaultMutableTreeNode selNode = null;
      if(null != _pnlPrefs.treBookmarks.getSelectionPath())
      {
         selNode = (DefaultMutableTreeNode) _pnlPrefs.treBookmarks.getSelectionPath().getLastPathComponent();
      }

      if (null == selNode || false == selNode.getUserObject() instanceof Bookmark)
      {
         
         _app.getMessageHandler().showErrorMessage(s_stringMgr.getString("sqlbookmark.noDeleteSelection"));
         return;
      }


      
      int ret = JOptionPane.showConfirmDialog(_app.getMainFrame(), s_stringMgr.getString("sqlbookmark.deleteConfirm"));
      if(JOptionPane.YES_OPTION != ret)
      {
         return;
      }


      DefaultMutableTreeNode nextSel = selNode.getNextSibling();
      if(null == nextSel)
      {
         nextSel = selNode.getPreviousSibling();
      }

      _nodeUserMarks.remove(selNode);

      ((DefaultTreeModel)_pnlPrefs.treBookmarks.getModel()).nodeStructureChanged(_nodeUserMarks);

      selectNode(nextSel);

   }

   private void onUp()
   {
      DefaultMutableTreeNode selNode = null;
      if(null != _pnlPrefs.treBookmarks.getSelectionPath())
      {
         selNode = (DefaultMutableTreeNode) _pnlPrefs.treBookmarks.getSelectionPath().getLastPathComponent();
      }

      if (  null == selNode
         || false == selNode.getUserObject() instanceof Bookmark
         || 0 == _nodeUserMarks.getIndex(selNode))
      {
         return;
      }

      int selIx = _nodeUserMarks.getIndex(selNode);

      _nodeUserMarks.insert(selNode, selIx - 1);

      ((DefaultTreeModel)_pnlPrefs.treBookmarks.getModel()).nodeStructureChanged(_nodeUserMarks);

      selectNode(selNode);

   }


   private void onDown()
   {
      DefaultMutableTreeNode selNode = null;
      if(null != _pnlPrefs.treBookmarks.getSelectionPath())
      {
         selNode = (DefaultMutableTreeNode) _pnlPrefs.treBookmarks.getSelectionPath().getLastPathComponent();
      }

      if (  null == selNode
         || false == selNode.getUserObject() instanceof Bookmark
         || _nodeUserMarks.getChildCount() - 1 == _nodeUserMarks.getIndex(selNode))
      {
         return;
      }

      int selIx = _nodeUserMarks.getIndex(selNode);
      _nodeUserMarks.insert(selNode, selIx + 1);

      ((DefaultTreeModel)_pnlPrefs.treBookmarks.getModel()).nodeStructureChanged(_nodeUserMarks);

      selectNode(selNode);

   }

}
