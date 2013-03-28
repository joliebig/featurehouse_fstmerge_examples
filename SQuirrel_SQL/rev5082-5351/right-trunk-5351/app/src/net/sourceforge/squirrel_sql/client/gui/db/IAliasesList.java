package net.sourceforge.squirrel_sql.client.gui.db;



public interface IAliasesList extends IBaseList
{
	
	public SQLAlias getSelectedAlias();

   void sortAliases();

   void requestFocus();

   void deleteSelected();

   void modifySelected();

   boolean isEmpty();

}
