package net.sourceforge.squirrel_sql.client.gui.db;

import net.sourceforge.squirrel_sql.client.IApplication;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseListener;



public class AliasesList implements IToogleableAliasesList
{
   private JPanel _pnlContainer = new JPanel(new GridLayout(1,1));
   private JListAliasesListImpl _jListImpl;
   private JTreeAliasesListImpl _jTreeImpl;
   private boolean _viewAsTree;

   public AliasesList(IApplication app)
	{
      AliasesListModel listModel = new AliasesListModel(app);
      _jListImpl= new JListAliasesListImpl(app, listModel);
      _jTreeImpl = new JTreeAliasesListImpl(app, listModel);
   }

   private IAliasesList getCurrentImpl()
   {
      if(_viewAsTree)
      {
         return _jTreeImpl;
      }
      else
      {
         return _jListImpl;
      }
   }

   public void setViewAsTree(boolean b)
   {
      _viewAsTree = b;

      if(_viewAsTree)
      {
         _pnlContainer.remove(_jListImpl.getComponent());
         _pnlContainer.add(_jTreeImpl.getComponent());
      }
      else
      {
         _pnlContainer.remove(_jTreeImpl.getComponent());
         _pnlContainer.add(_jListImpl.getComponent());
      }

      _pnlContainer.validate();
      _pnlContainer.repaint();
   }

   public IAliasTreeInterface getAliasTreeInterface()
   {
      return _jTreeImpl;
   }

   public void deleteSelected()
   {
      getCurrentImpl().deleteSelected();
   }

   public void modifySelected()
   {
      getCurrentImpl().modifySelected();
   }


   
	public SQLAlias getSelectedAlias()
	{
      return getCurrentImpl().getSelectedAlias();
   }

   public void sortAliases()
   {
      getCurrentImpl().sortAliases();
   }

   public void requestFocus()
   {
      getCurrentImpl().requestFocus();
   }


   public JComponent getComponent()
   {
      return _pnlContainer;
   }


   public void selectListEntryAtPoint(Point point)
   {
      getCurrentImpl().selectListEntryAtPoint(point);
   }


   public void addMouseListener(MouseListener mouseListener)
   {
      _jListImpl.addMouseListener(mouseListener);
      _jTreeImpl.addMouseListener(mouseListener);
   }

   public void removeMouseListener(MouseListener mouseListener)
   {
      _jListImpl.removeMouseListener(mouseListener);
      _jTreeImpl.removeMouseListener(mouseListener);
   }



}
