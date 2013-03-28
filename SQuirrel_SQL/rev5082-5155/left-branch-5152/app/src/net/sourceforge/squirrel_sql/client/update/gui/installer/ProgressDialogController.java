
package net.sourceforge.squirrel_sql.client.update.gui.installer;


public interface ProgressDialogController
{

	
	void showProgressDialog(String title, String msg, int total);

	
	void setDetailMessage(String msg);

	
	void incrementProgress();

	void resetProgressDialog(String title, String msg, int total);
	
	
	void hideProgressDialog();
}
