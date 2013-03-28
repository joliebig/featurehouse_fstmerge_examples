package net.sourceforge.squirrel_sql.client.gui.db;

import net.sourceforge.squirrel_sql.fw.sql.ISQLAlias;

import java.awt.event.MouseEvent;


public interface IAliasesList extends IBaseList
{
	
	public SQLAlias getSelectedAlias();

   void sortAliases();

   void requestFocus();

   void deleteSelected();

   void modifySelected();
}
