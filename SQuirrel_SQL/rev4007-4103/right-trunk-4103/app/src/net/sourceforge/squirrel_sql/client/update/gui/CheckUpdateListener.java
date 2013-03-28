
package net.sourceforge.squirrel_sql.client.update.gui;


public interface CheckUpdateListener {

   
   void checkUpToDate();
   
   
   void showErrorMessage(String title, String msg, Exception e);
   
   
   void showPreferences();
}
